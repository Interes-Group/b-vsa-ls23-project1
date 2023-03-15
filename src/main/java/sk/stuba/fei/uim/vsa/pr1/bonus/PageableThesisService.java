package sk.stuba.fei.uim.vsa.pr1.bonus;


import java.util.Date;
import java.util.Optional;

/**
 * Rozhranie pre doplnenie implementácie o vyhľadávacie metódy pracujúce so stránkovaním výsledkov databázy.
 * Jednotlivé {@link Optional} argumenty metódy sú možné kritéria vyhľadávania danej entity. V implementácii metód
 * je potrebné ošetriť všetky možné kombinácie prítomnosti ({@code Optional.isPresent()}), resp. neprítomnosti ({@code Optional.empty()})
 * hodnoty v {@link Optional}. Stránkovanie začína od 0.
 *
 * @param <S> Trieda reprezentujúca študenta (student)
 * @param <T> Trieda reprezentujúca pedagóga (teacher)
 * @param <A> Trieda reprezentujúca záverečnú prácu (assignment)
 */
public interface PageableThesisService<S, T, A> {

    /**
     * Vyhľadanie študentov v aplikácii na základe poskytnutých kritérií.
     *
     * @param name     Kritérium vyhľadania študenta podľa mena.
     * @param year     Kritérium vyhľadania študenta podľa ročníku štúdia.
     * @param pageable Objekt určujúci, ktorá strana má byť vrátená spolu s ďalšími informáciami.
     * @return Vráti stránku s obsahom zoznamom entít študentov, ktorí vyhovujú zadaným kritériám.
     * Ak nie je nájdená žiadna entita je vrátená prázdna strana.
     */
    Page<S> findStudents(Optional<String> name, Optional<String> year, Pageable pageable);

    /**
     * Vyhľadanie pedagógov v aplikácii na základe poskytnutých kritérií.
     *
     * @param name      Kritérium vyhľadania pedagóga podľa mena.
     * @param institute Kritérium vyhľadania pedagóga podľa inštitútu, na ktorom vyučuje.
     * @param pageable  Objekt určujúci, ktorá strana má byť vrátená spolu s ďalšími informáciami.
     * @return Vráti stránku s obsahom zoznamom entít pedagógov, ktorí vyhovujú zadaným kritériám.
     * Ak nie je nájdená žiadna entita je vrátená prázdna strana.
     */
    Page<T> findTeachers(Optional<String> name, Optional<String> institute, Pageable pageable);

    /**
     * Vyhľadanie záverečných práv v aplikácii na základe poskytnutých kritérií.
     *
     * @param department  Kritérium vyhľadania záverečnej práce podľa oddelenia, kde je práca vypísaná.
     * @param publishedOn Kritérium vyhľadania záverečnej práce podľa dátumu zverejnenia práce.
     * @param type        Kritérium vyhľadania záverečnej práce podľa typu vypísanej práce.
     * @param status      Kritérium vyhľadania záverečnej práce podľa statusu vypracovanie práce.
     * @param pageable    Objekt určujúci, ktorá strana má byť vrátená spolu s ďalšími informáciami.
     * @return Vráti stránku s obsahom zoznamom entít záverečných prác, ktoré vyhovujú zadaným kritériám.
     * Ak nie je nájdená žiadna entita je vrátená prázdna strana.
     */
    Page<A> findTheses(Optional<String> department, Optional<Date> publishedOn, Optional<String> type, Optional<String> status, Pageable pageable);

}
