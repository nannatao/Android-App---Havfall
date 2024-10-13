# Arkitektur

## Oversikt over arkitekturen
Applikasjonen Havfall tar i bruk MVVM arkitekturen og vi har forsøkt etter beste evne å opprettholde Android Best Practices. 

## Objektorienterte prinsipper
#### Klasser
Applikasjonen består av klasser og dataklasser. Alle ViewModels, Repositories og Datasources er klasser som holder på private og offentlige funksjoner og attributter. Prinsippet om innkapsling gjelder her for å kontrollere strengt hva en klasse tilbyr av metoder utad. En del hjelpefunksjoner er private fordi det ikke er meningen at andre klasser skal ha tilgang på disse. Setter’s av state er også for det meste tillukket den klassen de holder til. 

### Abstraksjon

Databasen vår er å regne som veldig objekt-orientert i sitt design. 

Vi har definert dataklasser (objekter) med Entity-dekorasjoner (i henhold til Room sin dokumentasjon) som holder på attributter til objektet. Disse har alle hver sin primærnøkkel de kan akksesseres med. 

Videre har hver entitet et Data Access Object (DAO) som er et interface som består av metoder dekorert med SQL-spørringer.  Interface’ene brukes av Room for å generere Java-metoder og selve databasen i SQLite. Dette abstraherer bort veldig mye av jobben med databaseskjemaer. 

I tillegg har vi gjort design-valget om å lage ett database repository som tilbyr offentlige metoder for å hente, sette og slette data fra databasen. Dette gjør at vi slipper å forholde oss til de enkelte spørringene, og kan gjøre flere endringer/spørringer i én funksjon. Som en bonus kan alle i teamet ta i bruk databasen uten å vite konkret hvordan implementasjonen fungerer på de lavere nivåene.  

### Høy kohesjon og lav kobling

Når det gjelder høy kohesjon og lav kobling er det to ting som er viktig: ansvar og avhengigheter. 

Vi har i stor grad vært flinke til å begrense ansvarsområdet til alle klasser og funksjoner. En funksjon utfører som regel kun  én oppgave (bortsett fra funksjoner i viewmodels). Én Viewmodel har ansvar over én skjerm (vi kunne designet applikasjonen annerledes ved å ha delte viewmodels for noen deler av forskjellige skjermer). Ett repository skal ha ansvar over de(n) datakilden(e) som gir logisk mening. 

Koblingene er litt mer avanserte. Noen repositories bruker (er avhengig av) andre repositories. Her noen eksempler:
	* CleaningDaysrepository bruker weatherrepository og tidalrepository
	- CalendarRepository bruker cleaningdaysrepository
	- MapRepository bruker alertRepository

Det har også oppstått noen sirkulære avhengigheter (circular dependencies) i koden. HomeViewModel CleaningsDaysRepository og WeatherRepository, men CleaningDaysRepo. er allerede koblet til WeatheRepo. som gjør at vi har unødvendig mange koblinger og avhengigheter. 

Noe som også er verdt å utpeke er at CleaningDaysRepository bruker to andre repositories, som kunne vært forenklet ved at datakildene brukte én større repository. 

Mange av problemene her stammer fra at vi ikke har tatt i bruk et Dependency Injection rammeverk som Hilt og Dagger.  Etter anbefaling fra veilederne våre droppet vi dette, men det burde vi helst ha gjort.

## Design Pattern: MVVM + Repository

Denne arkitekturen gjør det veldig enkelt å legge til funksjonalitet, nye API’er, hente mange forskjellige typer data, samtidig som det gjør det enkelt for oss å lage/kjøre tester og finne feil selv i en mellomstor kodebase.

Litt om inndelingen vår av kodebasen:

**View:**
* Presenterer state, muliggjør brukerinteraksjon
* Observerer state i ViewModel 
* En view for hver skjerm. Hvert view kan bestå av flere seperate komponenter (composables).

**ViewModel**
* Presenterer state til View. 
* Tilbyr alle funksjoner en skjerm trenger for å interagere med back-end. De inneholder også en del logikk for når, hva og hvordan ting skal vises i brukergrensesnittet.
* State: Holder på variabler som vises på skjermen, eller variabler som trengs for å holde styr på UI-komponenter (f eks om en dropdown er oppe eller nede). 
* Oppdatere state
	* starte henting av data, og reagere på brukerinteraksjon…

**Model (/data mappen)**
* hente og behandle data og presentere denne dataen til ViewModel
* Typisk deler man Model opp i flere klasser med ulikt ansvar for høyere kohesjon:

**Repositories**
En repository-klasse for hver ulike datatype. En repository kan ha ansvar for flere datasources. Et eksempel på et repository er WeatherRepository, som har ansvar over alle datasources som har med værdata å gjøre. Dette er et abstraksjonsnivå over datakildene. 

**Datasources**: 
En datasource-klasse for hver datakilde (API-endepunkt). Disse klassene inneholder de faktiske HTTP GET-kallene, og tilbyr flere forskjellige funksjoner. De er også tett knyttet til Data Transfer Objects (DTO) vi har autogenerert med JSON-to-Kotlin-Dataclasses plugin. Disse holder på responsen fra API’ene som datasourcen deserealiserer. 


## Tech stack

Appen vår kjører på Android API nivå 24 til 34, men target’er 34.
(minSdk = 24, targetSdk = 34, compileSdk = 34). 

Vi valgte API  34 primært for 3 grunner:
	1. Flere valg for brukeren når de blir bedt om å dele posisjon (man kan velge unøyaktig posisjon)
	2. Appen kjører på den nyeste versjonen av Android (Android 14)
	3. Appen er kompatibel med de mest populære versjonene av android (dekker 96.3% av mobiler siden vi støtter ned til API 24) samtidig som vi vil være kompatible med de neste versjonene av Android.

Vår tech-stack kort oppsummert:
- Android med Jetpack Compose
- Ktor for HTTP og nettverk
- Coil og Glide for bildebehandling
- Google Play Location Services og Google Maps Utils for lokasjoner, permissions og lignende
- MapBox for kart-løsningen vår
- Room som lag over lokal database (SQLite).
- JUnit og Espresso for testing


## Teknisk gjeld

Som rasjonelle utviklere er vi ikke 100% fornøyde med kodekvaliteten. Skulle vi videreutviklet dette (eller startet om igjen), hadde dette vært det første vi ville endret:
- userLocation: brorparten av logikken her ligger i MainActivity, men det skulle helst blitt håndtert i en egen repository. 
- Hver gang brukeren går inn på hjemskjermen, så lastes optimale ryddedager på nytt. Dette gjør at appen gjør unødvendig mye arbeid og API-kall. Vi skulle egentlig cache dette i Room og kun kalle API’ene en gang i timen. Men når vi skulle prøve å lagre dette ble det for avansert siden de optimale ryddedagene behandles i en veldig dårlig data-type :
	- Pair<Boolean, Pair<List<MutableMap<String?, Any?>?>, List<String>>?>
- Slike datatyper skaper problemer for skalering og modularitet. 


