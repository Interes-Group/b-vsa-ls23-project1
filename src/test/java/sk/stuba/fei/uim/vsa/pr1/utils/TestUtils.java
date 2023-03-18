package sk.stuba.fei.uim.vsa.pr1.utils;

import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.stuba.fei.uim.vsa.pr1.AbstractThesisService;
import sk.stuba.fei.uim.vsa.pr1.bonus.Pageable;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.lang.reflect.*;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.reflections.scanners.Scanners.SubTypes;

public class TestUtils {

    private static final Logger log = LoggerFactory.getLogger(TestUtils.class);

    public static Object getFieldValue(Object obj, String field) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        List<String> fields = findField(obj, field);
        if (fields.isEmpty())
            throw new NoSuchFieldException("Cannot find field with name '" + field + "' on class '" + obj.getClass().getName() + "'");
        Method getter = obj.getClass().getMethod("get" + capitalize(fields.get(0)));
        if (getter.getParameterCount() != 0)
            throw new NoSuchMethodException("Retrieved method 'get" + capitalize(fields.get(0)) + "' does not have 0 arguments so it cannot be safely invoked");
        return getter.invoke(obj);
    }

    public static <T> T getFieldValue(Object obj, String field, Class<T> fieldType) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException, NoSuchFieldException {
        List<String> fields = findField(obj, field, fieldType);
        if (fields.isEmpty())
            throw new NoSuchFieldException("Cannot find field '" + fieldType.getSimpleName() + " " + field + "' on class '" + obj.getClass().getName() + "'");
        Method getter = obj.getClass().getMethod("get" + capitalize(fields.get(0)));
        if (getter.getParameterCount() != 0)
            throw new NoSuchMethodException("Retrieved method 'get" + capitalize(fields.get(0)) + "' does not have 0 arguments so it cannot be safely invoked");
        if (getter.getReturnType() != fieldType)
            throw new NoSuchMethodException("Retrieved method 'get" + capitalize(fields.get(0)) + "' does not have provided return type " + fieldType.getName());
        return fieldType.cast(getter.invoke(obj));
    }

    public static Object setFieldValue(Object obj, String field, Object newValue) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method setter = obj.getClass().getMethod("set" + capitalize(field), newValue.getClass());
        if (setter.getParameterCount() != 1 || setter.getParameterTypes()[0] != newValue.getClass())
            throw new NoSuchMethodException("Retrieved method 'set" + capitalize(field) + "' does not have 1 argument of type '" + newValue.getClass() + "' so it cannot be safely invoked");
        setter.invoke(obj, newValue);
        return obj;
    }

    public static boolean isFieldNull(Object obj, String field) {
        try {
            return Objects.isNull(getFieldValue(obj, field));
        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException | NoSuchFieldException e) {
            log.error(e.getMessage(), e);
            return true;
        }
    }

    public static boolean hasField(Object obj, String field) {
        return !findField(obj, field, null, true).isEmpty();
    }

    public static List<String> findField(Object obj, String fieldName) {
        return findField(obj, fieldName, null, true);
    }

    public static List<String> findField(Object obj, String fieldName, boolean exactMatch) {
        return findField(obj, fieldName, null, exactMatch);
    }

    public static <T> List<String> findField(Object obj, String fieldName, Class<T> fieldType) {
        return findField(obj, fieldName, fieldType, true);
    }

    public static <T> List<String> findField(Object obj, Class<T> fieldType) {
        return findField(obj, null, fieldType, false);
    }

    public static <T> List<String> findField(Object obj, String fieldName, Class<T> fieldType, boolean exactMatch) {
        return Arrays.stream(obj.getClass().getMethods())
                .filter(m -> checkIfMethodIsGetter(m, fieldName, fieldType, exactMatch))
                .map(method -> camelCase(method.getName().substring(3)))
                .collect(Collectors.toList());
    }

    public static <T> boolean checkIfMethodIsGetter(Method method, String fieldName, Class<T> fieldType, boolean exactMatch) {
        boolean check = method.getName().startsWith("get") && method.getParameterCount() == 0;
        if (fieldType != null) {
            check = check && Objects.equals(method.getReturnType(), fieldType);
        }
        if (fieldName != null) {
            if (exactMatch) {
                check = check && method.getName().equals("get" + capitalize(fieldName));
            } else {
                check = check && method.getName().toLowerCase().contains(fieldName.toLowerCase());
            }
        }
        return check;
    }

    public static String capitalize(String str) {
        if (str.length() == 0) return "";
        if (str.length() == 1) return str.toUpperCase();
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    public static String camelCase(String str) {
        if (str.length() == 0) return "";
        if (str.length() == 1) return str.toLowerCase();
        return str.substring(0, 1).toLowerCase() + str.substring(1);
    }

    public static Long getEntityId(Object obj, String idFieldName) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException, NoSuchFieldException {
        if (idFieldName == null) {
            idFieldName = "id";
        }
        List<String> fields = findField(obj, idFieldName, Long.class);
        if (fields != null && !fields.isEmpty()) {
            if (fields.size() > 1)
                log.warn("More than one potential id field find on object of class " + obj.getClass().getName());
            return getFieldValue(obj, fields.get(0), Long.class);
        }
        return null;
    }

    public static Long getEntityId(Object obj) throws NoSuchFieldException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        Field idField = Arrays.stream(obj.getClass().getDeclaredFields())
                .filter(f -> f.isAnnotationPresent(Id.class) && f.getType().equals(Long.class))
                .findAny()
                .orElse(null);
        if (idField == null) return null;
        return getFieldValue(obj, idField.getName(), Long.class);
    }

    public static void testToHaveAnIdField(Object obj, String idField) {
        try {
            Long value = getEntityId(obj, idField);
            assertTrue(Objects.nonNull(value) && value > 0);
        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException |
                 NoSuchFieldException e) {
            fail(e);
        }
    }

    public static void testToHaveAnIdField(Object obj) {
        try {
            Long value = getEntityId(obj);
            assertTrue(Objects.nonNull(value) && value > 0);
        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException |
                 NoSuchFieldException e) {
            fail(e);
        }
    }

    public static String findIdFieldOfEntityClass(List<String> potentialIdFields, Class entityClass) {
        return potentialIdFields.stream()
                .map(idField -> {
                    try {
                        Object s = entityClass.newInstance();
                        return findField(s, idField, Long.class);
                    } catch (InstantiationException | IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                })
                .flatMap(List::stream)
                .filter(Objects::nonNull)
                .distinct()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Cannot find id field of Long type for entity class " + entityClass.getCanonicalName()));
    }

    public static String findIdFieldOfEntityClass(Class entityClass) {
        return Arrays.stream(entityClass.getDeclaredFields())
                .filter(f -> f.isAnnotationPresent(Id.class) && f.getType().equals(Long.class))
                .filter(f -> Arrays.stream(entityClass.getMethods())
                        .anyMatch(m -> checkIfMethodIsGetter(m, f.getName(), f.getType(), true)))
                .map(Field::getName)
                .distinct()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Cannot find id field of Long type for entity class " + entityClass.getCanonicalName()));
    }

    public static boolean checkIfClassIsEntity(Class clazz) {
        return clazz.isAnnotationPresent(Entity.class);
    }

    //---------------------------------

    public static AbstractThesisService<?, ?, ?> getServiceClass() {
        Reflections reflections = new Reflections("sk.stuba.fei.uim.vsa.pr1");
        Set<Class<?>> cps = reflections.get(SubTypes.of(AbstractThesisService.class).asClass());
        assertEquals(1, cps.size());
        return cps.stream().map(clazz -> {
            AbstractThesisService<?, ?, ?> thesisService = null;
            try {
                thesisService = (AbstractThesisService<?, ?, ?>) clazz.getDeclaredConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException e) {
                log.error(e.getMessage(), e);
            }
            assertNotNull(thesisService);
            log.info("Thesis service class: " + thesisService.getClass().getName());
            return thesisService;
        }).findFirst().orElse(null);
    }

    public static Class<?> getEntityClassFromService(Class<AbstractThesisService<?, ?, ?>> serviceClass, int parameterOrder) {
        return (Class<?>) ((ParameterizedType) serviceClass.getGenericSuperclass()).getActualTypeArguments()[parameterOrder];
    }

    public static Pageable createPageable(int page, int size) {
        Reflections reflections = new Reflections("sk.stuba.fei.uim.vsa.pr1");
        Set<Class<?>> cps = reflections.get(SubTypes.of(Pageable.class).asClass());
        assertEquals(1, cps.size());
        return cps.stream()
                .map(clazz -> {
                    Pageable pageable = null;
                    try {
                        pageable = (Pageable) clazz.getDeclaredConstructor().newInstance();
                    } catch (InvocationTargetException | InstantiationException | IllegalAccessException |
                             NoSuchMethodException e) {
                        log.error(e.getMessage(), e);
                    }
                    assertNotNull(pageable);
                    return pageable;
                })
                .findFirst()
                .map(p -> p.of(page, size))
                .orElse(null);
    }

    public static Connection getDBConnection(String db, String username, String password) throws ClassNotFoundException, SQLException {
        Class.forName("org.postgresql.Driver");
        return DriverManager.getConnection("jdbc:postgresql://localhost:5432/" + db, username, password);
    }

    public static void runSQLStatement(Connection con, String sql, boolean silent) {
        try (Statement stmt = con.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException ex) {
            if (!silent)
                log.error(ex.getMessage(), ex);
        }
    }

    public static List<String> tables = new ArrayList<>();

    public static void clearDB(Connection dbConnection) {
        if (tables.isEmpty()) {
            try (Statement stmt = dbConnection.createStatement()) {
                ResultSet set = stmt.executeQuery("SELECT tablename FROM pg_tables WHERE schemaname = current_schema()");
                while (set.next()) {
                    String table = set.getString("tablename");
                    if (!Objects.equals(table, "sequence")) {
                        tables.add(table);
                    }
                }
            } catch (SQLException ex) {
                log.error(ex.getMessage(), ex);
            }
        }
        //runSQLStatement(dbConnection, "SET FOREIGN_KEY_CHECKS=0", true);
        tables.forEach(table -> runSQLStatement(dbConnection, "TRUNCATE TABLE " + table + " CASCADE", false));
        //runSQLStatement(dbConnection, "SET FOREIGN_KEY_CHECKS=1", true);
    }

    //----------------------------------------------

    public static <T> T getTestClassFieldValues(Class<?> testClass, String field, Class<T> fieldValueType) throws NoSuchFieldException, IllegalAccessException {
        Field f = testClass.getField(field);
        int mod = f.getModifiers();
        if (Modifier.isPublic(mod) && Modifier.isStatic(mod) && Modifier.isFinal(mod)) {
            return fieldValueType.cast(f.get(null));
        } else {
            return null;
        }
    }

    public static Object createTeacher(AbstractThesisService<Object, Object, Object> service, Class testClass) throws NoSuchFieldException, IllegalAccessException {
        return service.createTeacher(
                getTestClassFieldValues(testClass, "aisId", Long.class),
                getTestClassFieldValues(testClass, "name", String.class),
                getTestClassFieldValues(testClass, "email", String.class),
                getTestClassFieldValues(testClass, "department", String.class));
    }

    public static Object createStudent(AbstractThesisService<Object, Object, Object> service, Class testClass) throws NoSuchFieldException, IllegalAccessException {
        return service.createStudent(
                getTestClassFieldValues(testClass, "aisId", Long.class),
                getTestClassFieldValues(testClass, "name", String.class),
                getTestClassFieldValues(testClass, "email", String.class));
    }

    public static Object createThesis(AbstractThesisService<Object, Object, Object> service, Class testClass, Long teacherId) throws NoSuchFieldException, IllegalAccessException {
        return service.makeThesisAssignment(
                teacherId,
                getTestClassFieldValues(testClass, "title", String.class),
                getTestClassFieldValues(testClass, "type", String.class),
                getTestClassFieldValues(testClass, "description", String.class));
    }
}
