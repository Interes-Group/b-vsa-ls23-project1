package sk.stuba.fei.uim.vsa.pr1.utils;

import java.util.Map;

public class TestConstants {

    public static final String DB = getEnvOrDefault("VSA_DB", "vsa_pr1");
    public static final String USERNAME = getEnvOrDefault("VSA_DB_USERNAME", "vsa");
    public static final String PASSWORD = getEnvOrDefault("VSA_DB_PASSWORD", "vsa");
    public static final String[] ID_FIELDS = getEnvOrDefault("VSA_ID_FIELDS", "id,aisId,ais,aisid,ID").split(",");

    public static final Map<String, String> ENV = System.getenv();

    public static String getEnvOrDefault(String key, String defaultValue) {
        return System.getenv(key) == null ? defaultValue : System.getenv(key);
    }

}
