# B-VSA LS 22/23 - Semestrálny projekt 1

![Java 1.8](https://img.shields.io/badge/Java-1.8-red)
![EclipseLink 2.7.11](https://img.shields.io/badge/EclipseLink-2.7.11-blue)
![PostgreSQL Driver](https://img.shields.io/badge/PostgreSQL-42.5.4-green)
[![Public domain](https://img.shields.io/badge/License-Unlicense-lightgray)](https://unlicense.org)

Cieľom 1. semestrálneho projektu je implementovať jednoduchú aplikácie pre správu záverečných prác
v rámci štúdia na vysokej škole využívajúc technológiu JPA s pripojením na SQL databázu. Aplikácia musí poskytovať
API v podobe java metód na obsluhu funkcionality. Aplikácia nemusí poskytovať žiadne používateľské rozhranie. Môžte
sa inšpirovať [projektami z cvičení](https://github.com/Interes-Group/b-vsa-cvicenia). Dbajte na držanie pokynov
zadania nakoľko bude opravované automatizovane pomocou JUnit testov. Na konci zadania je návod, ako si spustiť tzv.
"sanity-check" test lokálne pre otestovanie splnenia konfigurácie vypracovania aby bol projekt testovateľný.

## Entity

Aplikácia musí pracovať s entitami:

- **Študent** - obsahuje atribúty:
    - AIS id (identifikátor študenta)
    - meno
    - email (musí byť unikátne)
    - ročník štúdia (číslo)
    - semester štúdia (číslo)
    - program štúdia
    - záverečná práca
- **Pedagóg** - obsahuje atribúty:
    - AIS id (identifikátor pedagóga)
    - meno
    - email (musí byť unikátne)
    - inštitút (pracovisko)
    - oddelenie (v rámci pracoviska)
    - vypísané záverečné práce
- **Záverečná práca** - obsahuje atribúty:
    - registračné číslo (musí byť generované a začínať na reťazec "FEI-")
    - názov
    - popis
    - pracovisko (ktoré vypísalo prácu)
    - vedúci práce (učiteľ)
    - vypracovateľ (študent)
    - dátum zverejnenia (dátum)
    - deadline odovzdania práce (dátum)
    - typ (enumerácia)
    - status (enumerácia)(atribút v Java triede sa musí volať rovnako!)

### Asociácie

Medzi implementovanými entitami musia byť dodržané vzťahy nasledovne:

- Študent môže mať zapísanú maximálne jednu záverečnú prácu.
- Pedagóg môže mať vypísaných viacero záverečných prác, ale aj žiadnu.
- Záverečná práca musí mať práve jedného pedagóga a môže mať najviac jedného študenta vypracovateľa.

## Funkcionalita

Aplikácia musí zabezpečiť CRUD operácie nad všetkými entitami spomenuté v tomto zadaní. Musí implementovať zadané API
v podobe Java metód (trieda [`AbstractThesisService`](src/main/java/sk/stuba/fei/uim/vsa/pr1/AbstractThesisService.java)).
**Triedy dosadené za generické typy v implementácii `AbstractThesisService` musia byť uvedené v poradí: Študent, Pedagóg, Záverečná práca**
Spomínané entity musia obsahovať jedinečné **identifikátory typu Long** môžu byť generované databázou, alebo použiť predpísané atribúty (napr. AIS id). 
Študent a pedagóg môžu existovať aj bez záverečnej práce. Záverečná práca musí mať priradeného aspoň vedúceho práce.
Záverečná práca môže existovať aj bez zadaného študenta.

Záverečná práca obsahuje dva atribúty, ktorých hodnoty sú enumerácie (typ a status). Enumeračné atribúty musia byť
perzistované ako reťazce (ako názov enumeračnej hodnoty), nie ako číslo. Enumerácie by mali mať minimálne nasledovné hodnoty:

- Enumerácia Typ (musia byť presne tieto hodnoty):
    - BACHELOR
    - MASTER
    - DISSERTATION
- Enumerácia Status (skutočné hodnoty môžu byť ľubovolné):
    - Voľná
    - Zabraná (môže byť aj 'Pracuje sa')
    - Odovzdaná

Uvedené hodnoty nemusia byť presne také ako sú uvedené, je vám umožnená autorská sloboda, ale musia mať rovnaký význam/charakter.
Typ záverečnej práce musí byť nastavený pri jej vytvorení. Novo vytvorená práca je v stave "_Voľná_". Ak je práci priradený študent
stav práce sa má zmeniť na "_Zabraná_". Ak je práca odovzdaná musí zmeniť stav na "_Odovzdaná_". 

Pri vytváraní záverečnej práce je potrebné vygenerovať registračné číslo práce, ktoré musí začínať na reťazec **"FEI-"**.
Pracovisko, pod ktorým je práca vypísaná je odvodené od pedagóga. Dátum zverejnenia práce môže byť nastavený na aktuálny dátum
pri vytvorení záverečnej práce. Deadline (termín) odovzdania musí byť 3 mesiace od zverejnenia práce. Študent si môže zapísať 
ľubovolnú záverečnú prácu, ktorej dátum odovzdania je väčší, ako aktuálny dátum a jej stav je nastavený na "_Volná_".
Študent môže odovzdať iba priradenú prácu do deadlinu (termínu) odovzdania, ktorá je v stave "_Zabraná_".

Vymazanie záverečnej práce nemá vplyv na ostatné entity. Vymazanie študenta odstráni študenta zo všetkých jeho záverečných
prác. Ak je vymazaný pedagóg sú vymazané aj všetky záverečné práce, ktorých je vedúci.

## Hodnotenie

**Zadanie je hodnotené 15 bodmi. Vypracovanie je nutné odovzdať do 02.04.2023 23:59.**

Zadanie si naklonujte z repozitára zadania. Svoje vypracovanie nahrajte do vášho repozitára pre toto zadanie pomocou
programu Git (git commit + git push). Vypracovanie môžete "pusho-vať" priebežne. Main metóda musí ostať prázdna po
poslednom commit-e do repozitára. Názov Java balíčka nemeňte. Nepresúvajte ani nemeňte súbory pripravené v zadaní ako
triedy `Project1` a `AbstractThesisService` a súbor [`logback.xml`](src/main/resources/logback.xml).

**Úpravy pom.xml súbory sú zakázané** mimo ďalej uvedených zmien:

- Pridanie závislosti na driver databázy podľa vlastného výberu. Povolené SQL databázy:
    - MySQL
    - OracleDB
    - Derby
    - PostgreSQL (bude použitá ako testovacia databáza pri oprave)
    - H2
- Doplnenie informácií o authorovi (developerovi, tag `<developers><developer>`)

**Názov `persistence-unit`** v súbore [persistence.xml](src/main/resources/META-INF/persistence.xml) **nemeňte**. Pre
vlastné otestovanie aplikácie použite metódu main v triede [Project1](src/main/java/sk/stuba/fei/uim/vsa/pr1/pr1/Project1.java).
Použitá databáza sa musí volať **vsa_pr1** a musí mať vytvoreného používateľa s **menom 'vsa' a heslom 'vsa'**. Pre
tieto účeli môžte využiť súbor [starter.postgres.sql](configs/starter.postgres.sql) (určený pre databázu PostgreSQL).

**Typy atribútov entít musia byť** v Jave ako **triedy**/objekty (Integer, Float, Double, String a pod.),
**nepoužívajte primitívne dátové typy** (ako int, double, long a pod.);

Implementujte vlastnú triedu, ktorá bude dediť od triedy `AbstractThesisService` a tak implementovať jej
abstraktné metódy a **preťažovať konštruktor**. Táto trieda má slúžiť, ako API (alebo ako fasáda) pre funkcionality aplikácie
a testy hodnotenia budú využívať jej implementáciu. Pozorne si prečítajte komentáre nad triedou a metódami.
Metódy môžu hádzať výnimky v prípade chybného stavu či vstupu avšak musia byť tzv. runtime, to znamená, že
nemôžte do metód doplniť definíciu aké výnimky metóda môže hodiť. V prípade prázdneho výsledku:

- Ak metóda vracia jeden objekt môžte vrátiť null.
- Ak metóda vracia kolekciu vráťte prázdnu kolekciu.

Hodnotiť sa bude iba master/main branch. Kvôli testom a zrýchleniu opravovania je nutné dodržať pokyny a štruktúru
projektu, ako je stanovené v zadaní! Iba kód poslednej verzie vypracovania (t.j. z posledného commit-u) do termínu
odovzdania sa berie do úvahy. Okrem testov sa bude kód a funkcionalita kontrolovať aj manuálne. Hodnotiť sa budú iba
skompilovateľné a spustiteľné riešenia!

### Sanity check test

Projekt obsahuje test tzv. "sanity-check", ktorý kontroluje, či váš projekt dodržuje pokyny zadania a tak či bude 
akceptovaný na opravu. Test je napísaný v triede [`SanityCheckTest`](src/test/java/sk/stuba/fei/uim/vsa/pr1/SanityCheckTest.java).
Pre spustenie testu stačí vykonať **maven** lifecycle goal **test**, resp. spustiť príkaz:

```shell
mvn test
```

Alebo otvoriť testovací súbor a spustiť jUnit test (zelená šípka vedľa názvu triedy). Ak test skončí úspešne (t.j. bez chyby),
váš projekt je pripravený na opravu.

### Logovanie a výpisy

V rámci projektu je nakonfigurovaná knižnica pre logovanie (formátované výpisy) [Logback](https://www.baeldung.com/logback). Túto knižnicu môžte použiť namiesto
Java `System.out.println` pre formátovaný výpis (napríklad ako je v `SanityCheckTest` triede).

Knižnicu je potrebné inicializovať v každej triede kde ju chcete použiť riadkom:

```Java
private static final org.slf4j.Logger log=org.slf4j.LoggerFactory.getLogger(<názov triedy ktorú treba logovať>.class);
```

Následne je možné využiť metódy ako `log.info`, `log.warn` či `log.error` v prípade výnimiek. Trieda `AbstractThesisService`
už má túto knižnicu inicializovanú, takže ju je možné priamo použiť vo vašej implementácii.

## Bonus (3b)

V rámci zadania máte možnosť implementovať rozšírenie funkcionality o tzv. stránkovanie výsledkov za 3 bonusové body.
Pre implementáciu bonusu implementujte rozhrania Page a Pageable ako dve nové vlastné triedy a rozšírte vašu triedu,
ktorá dedí od triedy AbstractThesisService, o implementovanie rozhrania PageableThesisService.

Týmto spôsobom rozšírite vašu implementáciu o tri ďalšie metódy. Jednotlivé metódy majú argumenty typu Optional.
To znamená, že musíte ošetriť všetky kombinácie, kedy sú jednotlivé argumenty ako prázdny Optional (t.j. Optional.empty())
a kedy obsahuje Optional hodnotu (t.j. Optional.isPresent() == true). Metódy musia vrátiť jednu stránku podľa zadaného
objektu implementujúci rozhranie Pageable.

Bonusové vypracovanie bude uznané jedine v prípade ak je implementované celé zadanie.
