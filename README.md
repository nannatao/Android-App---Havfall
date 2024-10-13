# Android-App "Havfall"
Appen "Havfall" som ble laget i faget IN2000 Software Engineering med prosjektarbeid ved UiO. 


## IN2000 Prosjektoppgave - Team 27

## Teammedlemmer

Mattis Stene-Johansen\
Eesha Fayyaz \
Lea Ho\
Aida Monfared\
Nanna Tao Karlstrom\
Arpita Srivastava

## Integrasjon av dokumentasjon i kildekode / brukerdokumentasjon:

Kode-dokumentasjon: 
Utviklerne av Havfall har inkludert forklarende kommentarer innenfor selve kildekodens filer. Dette sikrer at forklaringene er så nære som mulig til den faktiske koden som utfører funksjonene de beskriver. Kommentarene er skrevet på en måte som er lett forståelige for brukeren, med mål om at de som ser gjennom koden kan forstå hva hver kodebit gjør uten å måtte referere til eksterne dokumenter. Koden inneholder blant annet dokumentasjon på hvilke parametre som en funksjon tar inn (@param) og hva som returneres (@return). 

Brukerdokumentasjon: 
Alt av brukerdokumentasjon finnes i rapporten vår, under punkt 2.Brukerdokumentasjon. 

VIKTIG INFORMASJON TIL BRUKER/SENSOR: 
For å kunne resette applikasjonen, slik at databasen nullstilles og onboarding-skjermer vises, så har vi plassert en "logout" knapp øverst i høyre hjørne
på UserProfileScreen. Det gir i utgangspunktet ikke mening å kunne logge ut dersom man ikke logger inn, men denne funksjonaliteten er kun implementert 
slik at appen kan testes av nye brukere. 


## Hvordan kjøre Havfall-applikasjonen 

1. Åpne Android Studio prosjektet:
- Start Android Studio.
- Velg 'Open an existing Android Studio project' og naviger til mappen hvor Havfall-prosjektet er lagret.
- Vent til Android Studio har fullført indeksering og gradle byggeprosesser.

2. Kontroller prosjektinnstillinger:
- Sjekk build.gradle filer for å forsikre deg om at alle avhengigheter er riktig konfigurert.
- Sørg for at SDK-versjonen matcher det som trengs for applikasjonen. SDK-versjonen skal være 34, dersom Android Studio 14 brukes. 

3. Velg kjøringskonfigurasjon:
- I verktøylinjen, klikk på " Devise Manager" velg en passende kjøringskonfigurasjon for app-modulen. For vår applikasjon kan "Resizable" og "Medium Phone"   emulatorer trygt brukes og API-level skal være 34. 
- Hvis en default emualtor ikke eksisterer, opprett en ny ved å trykke på "Devise Manager" og legg deretter til en ny Android App-konfigurasjon.

4. Start den virtuelle enheten:
- Åpne "Device Manager" og start den virtuelle enheten du konfigurerte tidligere.

5. Bygg og kjør applikasjonen:
- Trykk på 'Run'-knappen (en grønn trekant) eller bruk hurtigtasten Shift + F10 på Windows/Linux eller Ctrl + R på Mac for å bygge og kjøre applikasjonen.
- Android Studio vil bygge applikasjonen og deretter installere den på den valgte enheten.


## Forklaringer til "Warnings" i kildekoden 

Hvor: CalendarDatasource og flere andre filer der vi har @param kommentarer 
Warning beskjed: This block comment looks like it was intended to be a KDoc comment 
Forklaring: Disse warningsene kommer som følge av @param, men peker ikke på noe reelt problem 

Hvor: CleaningActivityDao 
Warning beskjed: Variable 'oldCleaningActivity' is never used 
Forklaring: Vi beholder denne variabelen fordi kan hende den brukes i @update (javakoden til Room databasen) 

Hvor:AlertDatasource og Home Viewmodel 
Warning beskjed: Function name '_featureToAlert'/ '_locationState' should start with a lowercase letter
Forklaring: Funksjonen starter med en liten forbokstav, men det registreres ikke av programmet 

Hvor: TemperatureDataSource
Warning beskjed: Condition 'timesery.data.next_1_hours != null' is always 'true'
Forklaring: dette stemmer ikke 

Hvor: CalendarViewmodel  
Warning beskjed: Value of 'optimalDay' is always true, Value of 'optimalDay' is always false
Forklaring: dette stemmer ikke 

Hvor: CleaningActivityViewmodel 
Warning beskjed: Name shadowed: trashType 
Forklaring: dette stemmer ikke 

Hvor: CleaningActivityViewmodel og Home Viewmodel 
Warning beskjed: 'getFromLocation(Double, Double, Int): (Mutable)List<Address!>?' is deprecated. Deprecated in Java 
Forklaring: Fant ingen løsning på dette og getFromLocation fungerte i vår kode

Hvor: HomeScreen og flere andre skjermer som tar Navcontroller som parameter 
Warning beskjed: Parameter 'navController' is never used 
Forklaring: navController trengs for navigasjon (selv om parameteren ikke brukes direkte) 

Hvor: HomeViewmodel 
Warning beskjed: Property 'homeUiState' could be private
Forklaring: dette stemmer ikke 

Hvor: Tester.kt
Warnins: flere warnings for funksjonen alertParamaterRiktigType()
Forklaring: dette stemmer ikke, da dette er en test 
 
 
## Bugs og ting som ikke fungerer 

1. Bug i fremvisningen av "optimal ryddedag"-algoritmen på HomeScreen (Ui -> calendar -> CalendarCard.kt -> fun DateCard())
- Vi fikk en bug i fremvisningen av de optimale ryddedagene på HomeScreen: Dersom dagen i dag var en optimal ryddedag, ville værsymbolet plutselig vise       default værsymbol etter kl.12 på dagen. Dette skjer nok fordi vi henter værsymboler for kl.12 hver dag, og at denne informasjon "forsvinner" fra API-et. I tillegg får vi ikke hentet informasjon om været for dagen i dag, når klokken er senere enn kl.12, selv om dagen vises som en "optimal ryddedag".

2. Internett access håndtering (Ui -> home -> HomeScreen.kt -> fun HomeScreen())
- Tidligere i prosjektet fungerte håndteringen av internett-tilgang, med snackbar som dukker opp dersom emulator ikke har internett-tilgang (denne koden     er nå kommentert ut for å kunne kjøre applikasjonen). 
- Dessverre begynte applikasjonen å krasje de to siste dagene før deadline av prosjektet, og vi måtte derfor kommentere ut denne funksjonaliteten for 
  å kunne kjøre appen. 

3. Hardkoding av gjenvinningstasjoner for marint avfall (Ui -> map -> MapBoxMap -> fun createRecyclingMap())
- Vi skulle gjerne ha gjort et API-kall for å hente inn alle gjenvinningstasjoner for marint avfall. 
- Dessverre ble ikke dette mulig, da sortere.no (de som har datakildene og API-et) ikke vill dele dette med oss.Derfor gikk til ryddenorge.no sine sider     for å se hente informasjon fra alle aktuelle gjenvinningstasjoner, og hardkodet inn alle gjenvinningstasjoner for marint avfall i Oslo og omegn.
- Kilde til rydddenorges kartfunksjon: https://ryddenorge.no/aksjoner

4. Kartfilter for farevarsel kommer oppå hverandre når filteret slås av og på (Ui -> map -> MapBoxMap.kt -> fun MapBoxMap())
- I kartet kan man skru av og på farevarsel-filteret, som bevisst er satt til å være litt gjennomsiktig for å kunne se kartet under. 
- Denne funksjonaliteten ble implementert i siste liten, og dermed fikk vi ikke fjernet denne buggen: når man skrur av og på filteret for farevarsel         virker det som filterne multipliserer og blir flere og flere. Til slutt blir polygonene veldig røde og man ser ikke kartet under. 

5. Fire "errors" i databasetesting (DataBaseTest) 
- Denne testen ble laget under implementeringen av Databasen (testdrevet utvikling) og fikk derfor noen "errors" etter at databasen var ferdig               implementert. 
- Vi fikk dessverre ikke tid til å endre på testen, og besluttet å bevare testen i sin originiale for den testdrevne utviklingen. 

6. Fikk ikke tid til å gi nytt navn til applikasjonen 
- Vi fikk dessverre ikke tid til å bytte navn på applikasjonen fra "Havapp" til "Havfall", men applikasjonen kjører ihvertfall som den skal :-) 
