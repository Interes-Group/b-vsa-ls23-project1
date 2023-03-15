package sk.stuba.fei.uim.vsa.pr1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;

/**
 * Trieda API pre vypracovanie zadania. Výsledné vypracovanie zadania musí dediť (extends) od tejto triedy.
 * V preťaženom konštruktore vlastnej implementácie nezabudnite zavolať aj super konštruktor ({@code super();}).
 * V triede už je k dispozícii vytvorený EntityManagerFactory (property emf), tú môžete využiť
 * v rámci svojich metód len ju v tých metódach nezatvárajte. Nezabudnite však vždy zatvoriť vytvoreného
 * EntityManager a prípadné transakcie!
 *
 * @param <S> Trieda reprezentujúca študenta (student)
 * @param <T> Trieda reprezentujúca pedagóga (teacher)
 * @param <A> Trieda reprezentujúca záverečnú prácu (assignment)
 */
public abstract class AbstractThesisService<S, T, A> implements AutoCloseable {

    protected static final Logger log = LoggerFactory.getLogger(AbstractThesisService.class);

    protected final EntityManagerFactory emf;

    public AbstractThesisService() {
        this.emf = Persistence.createEntityManagerFactory("vsa-project-1");
    }

    @Override
    public void close() {
        emf.close();
    }

    // ------------- STUDENT API -------------

    /**
     * Vytvorenie entity študenta.
     *
     * @param aisId Identifikátor vo formáte zo systému AIS.
     * @param name  Meno nového študenta. Obsahuje krstné meno aj priezvisko.
     * @param email Školský emailový kontakt.
     * @return Inštanciu entity študenta po vložení do databázy. V prípade chyby vráti null.
     */
    public abstract S createStudent(Long aisId, String name, String email);

    /**
     * Získanie inštancie entity študenta podľa identifikátora entity.
     *
     * @param id Identifikátor entity.
     * @return Inštanciu entity uloženú v databáze. V prípade chyby vráti null.
     * @throws IllegalArgumentException Ak parameter id je null.
     */
    public abstract S getStudent(Long id);

    /**
     * Aktualizuje inštanciu entity študent.
     *
     * @param student Inštancia entity pre aktualizáciu/perzistenciu.
     * @return Aktualizovanú inštanciu entity. V prípade chyby vráti null.
     * @throws IllegalArgumentException Ak je inštancia null alebo je identifikátor entity null.
     */
    public abstract S updateStudent(S student);

    /**
     * Získanie zoznamu všetkých perzistovaných inštancií entity študent.
     *
     * @return Zoznam inštancií entity študent. V prípade chyby vráti prázdny zoznam.
     */
    public abstract List<S> getStudents();

    /**
     * Vymazanie inštancie entity študent.
     *
     * @param id Identifikátor inštancie entity.
     * @return Pôvodnú inštanciu, ktorá bola vymazaná z databázy. V prípade chyby vráti null.
     * @throws IllegalArgumentException Ak je identifikátor null.
     */
    public abstract S deleteStudent(Long id);

    // ------------- TEACHER API -------------

    /**
     * Vytvorenie novej inštancie entity pedagóga.
     *
     * @param aisId      Identifikátor vo formáte ako v systéme AIS.
     * @param name       Meno pedagóga, obsahujúce krstné meno a priezvisko.
     * @param email      Školský kontaktný email.
     * @param department Oddelenie pracoviska pedagóga na fakulte.
     * @return Novo vytvorenú inštanciu entity pedagóg. V prípade chyby vráti null.
     */
    public abstract T createTeacher(Long aisId, String name, String email, String department);

    /**
     * Získanie inštancie entity pedagóga z databázy.
     *
     * @param id Identifikátor entity.
     * @return Inštanciu entity pedagóg. V prípade chyby vráti null.
     * @throws IllegalArgumentException Ak je identifikátor null.
     */
    public abstract T getTeacher(Long id);

    /**
     * Aktualizácia inštancie entity pedagóg.
     *
     * @param teacher Inštancia pre aktualizáciu/perzistenciu.
     * @return Aktualizovanú inštanciu entity pedagóga po uložení do databázy. V prípade chyby vráti null.
     * @throws IllegalArgumentException Ak je inštancia null alebo identifikátor inštancie null.
     */
    public abstract T updateTeacher(T teacher);

    /**
     * Získanie zoznamu všetkých uložených inštancií entity pedagóga v databáze.
     *
     * @return Zoznam inštancií entity pedagóga. V prípade chyby vráti prázdny zoznam.
     */
    public abstract List<T> getTeachers();

    /**
     * Vymazanie inštancie entity pedagóga z databázy.
     *
     * @param id Identifikátor inštancie entity pedagóga.
     * @return Práve vymazanú inštanciu entity pedagóga. V prípade chyby vráti null.
     * @throws IllegalArgumentException Ak je identifikátor null.
     */
    public abstract T deleteTeacher(Long id);

    // ------------- THESIS (ASSIGNMENT) API -------------

    /**
     * Vytvorenie novej inštancie entity záverečnej práce.
     *
     * @param supervisor  Identifikátor inštancie entity pedagóga v roly vedúceho práce.
     * @param title       Názov záverečnej práce.
     * @param type        Typ záverečnej práce z enumerácie. (viď README.md)
     * @param description Popis zadanie záverečnej práce.
     * @return Novo vytvorenú inštanciu entity záverečnej práce. V prípade chyby vráti null.
     * @throws IllegalArgumentException Ak identifikátor inštancie entity pedagóga (supervisor) je null.
     */
    public abstract A makeThesisAssignment(Long supervisor, String title, String type, String description);

    /**
     * Priradenie entity študenta k inštancie entity záverečnej práce a zmena stavu záverečnej práce.
     *
     * @param thesisId  Identifikátor inštancie entity záverečnej práce.
     * @param studentId Identifikátor inštancie entity študenta.
     * @return Aktualizovanú inštanciu entity záverečnej práce. V prípade chyby vráti null.
     * @throws IllegalArgumentException Ak identifikátor záverečnej práce alebo študenta je null.
     * @throws IllegalStateException    Ak záverečná práca nie je v stave kedy ju je možné priradiť študentovi.
     *                                  T.j. je už posunutá do iného stavu, alebo je po deadline.
     */
    public abstract A assignThesis(Long thesisId, Long studentId);

    /**
     * Odovzdanie záverečnej práce. Práca je posunutá do nového stavu.
     *
     * @param thesisId Identifikátor inštancie entity záverečnej práce.
     * @return Aktualizovanú inštanciu entity záverečnej práce. V prípade chyby vráti null.
     * @throws IllegalArgumentException Identifikátor záverečnej práce je null.
     * @throws IllegalStateException    Ak záverečná práca nie je v stave kedy ju je možné odovzdať
     *                                  (t.j. je už posunutá do koncového stavu, alebo je po deadline),
     *                                  alebo nie je priradený žiadnemu študentovi.
     */
    public abstract A submitThesis(Long thesisId);

    /**
     * Vymazanie záverečnej práce z databázy.
     *
     * @param id Identifikátor inštancie entity záverečnej práce.
     * @return Práve vymazanú inštanciu entity záverečnej práce. V prípade chyby vráti null.
     * @throws IllegalArgumentException Ak je identifikátor záverečnej práce null.
     */
    public abstract A deleteThesis(Long id);

    /**
     * Získanie zoznamu všetkých záverečných prác v databáze.
     *
     * @return Zoznam inštancií entity záverečnej práce. V prípade chyby vráti prázdny zoznam.
     */
    public abstract List<A> getTheses();

    /**
     * Získanie zoznamu záverečných prác s rovnakým vedúcim práce.
     *
     * @param teacherId Identifikátor entity pedagóga v roly vedúceho práce.
     * @return Zoznam inštancií entity záverečnej práce. V prípade chyby vráti prázdny zoznam.
     */
    public abstract List<A> getThesesByTeacher(Long teacherId);

    /**
     * Získanie zoznamu záverečných prác s rovnakým študentom ako vypracovateľom práce.
     *
     * @param studentId Identifikátor entity študenta v roly vypracovateľa práce.
     * @return Zoznam inštancií entity záverečnej práce. V prípade chyby vráti prázdny zoznam.
     */
    public abstract A getThesisByStudent(Long studentId);

    /**
     * Získanie inštancie entity záverečnej práce podľa identifikátora.
     *
     * @param id Identifikátor inštancie záverečnej práce.
     * @return Inštanciu záverečnej práce. V prípade chyby vráti null.
     * @throws IllegalArgumentException Ak je identifikátor null.
     */
    public abstract A getThesis(Long id);

    /**
     * Aktualizácia inštancie entity záverečnej práce.
     *
     * @param thesis Inštancia entity záverečnej práce pre aktualizáciu/perzistenciu.
     * @return Aktualizovanú inštanciu entity z databázy. V prípade chyby vráti null.
     * @throws IllegalArgumentException Ak je inštancia null alebo identifikátor inštancie null.
     */
    public abstract A updateThesis(A thesis);
}
