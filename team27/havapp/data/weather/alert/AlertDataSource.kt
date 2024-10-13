package no.uio.ifi.in2000.team27.havapp.data.weather.alert

import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.*
import io.ktor.serialization.gson.gson
import io.ktor.util.appendIfNameAbsent
import no.uio.ifi.in2000.team27.havapp.data.weather.alert.MetAlertsDataFrame.Feature
import no.uio.ifi.in2000.team27.havapp.data.weather.alert.MetAlertsDataFrame.Geometry
import no.uio.ifi.in2000.team27.havapp.data.weather.alert.MetAlertsDataFrame.Resource
import no.uio.ifi.in2000.team27.havapp.model.alerts.fylkeIdFraNavn


/**
 * Denne klassen er en datakilde for værvarsling med MetAlerts API.
 * I tillegg til Datasource-klassen inneholder den to dataklasser:
 * - AlertResponse er svaret vi får fra API-et, brukes for å serialisere JSON-responsen.
 * - Alert er en egen dataklasse for å definere en varsel, som bruker de relevante feltene
 */

data class AlertResponse(
    val type: String,               // FeatureCollection
    val features: List<Feature>,    // Liste av Feature
    val lang: String,               // Språk
    val lastChange: String          // Siste endring
)

data class Alert(
    val id: String,                 // ID til varsel
    val name: String,               // Tittel/navn på varsel
    val description: String,        // Beskrivelse av varsel
    val severity: String,           // Alvorlighetsgrad
    val area: String,               // Område
    val event: String,              // Hva slags hendelse
    val geometry: Geometry,         // Til GeoJSON
    val instruction: String,        // Instruksjoner
    val riskMatrixColor: String,    // Fargekode for risiko
    val resources: List<Resource>,   // Ressurser
    val county: List<String>        // Fylke
)


class AlertDataSource {

    private val client = HttpClient {
        defaultRequest {
            url("https://gw-uio.intark.uh-it.no/in2000/")
            headers.appendIfNameAbsent(
                name = "X-Gravitee-API-Key",
                value = "1dce2f6d-29c0-4dcd-bb44-b3c014a2a690"
            )
        }
        install(ContentNegotiation) {
            gson()
        }
    }

    suspend fun fetchAllAlerts(): MutableSet<Alert> {
        /**
         * Henter alle værvarsling fra MetAlerts API.
         * @return MutableSet<Alert> - Et sett av alle varsler
         */
        val alerts = mutableSetOf<Alert>()

        val result = client.get("weatherapi/metalerts/2.0/current.json").body<AlertResponse>()
        result.features.forEach {
            if (it.properties.county.isNotEmpty()) {
                // Hvis fylke er spesifisert, legg til varsel (varsler uten fylke er på havet)
                alerts.add(_featureToAlert(it)) // Konverterer Feature til Alert og legger til i settet
            }
        }
        return alerts
    }

    suspend fun getAlertsForCounty(fylkeNavn: String): MutableSet<Alert> {
        /**
         * Henter værvarsling for et fylke hvis det finnes.
         * @param fylkeNavn navnet på fylket.
         * @return Hvis flere varsler finnes: en liste med [Alert] objekter
         *         Hvis ett varsel finnes: et [Alert] objekt
         *         Hvis ingen varsler finnes: null
         **/
        val alerts = mutableSetOf<Alert>()

        val allAlerts = fetchAllAlerts()
        allAlerts.forEach { alert ->
            if (alert.county.contains(fylkeIdFraNavn(fylkeNavn))) {
                alerts.add(alert)
            }
        }
        return alerts
    }

    suspend fun getAlertColorFromId(id: String): Int {
        /**
         * Henter fargekoden for et varsel basert på ID til varsel.
         * @param id ID til varselet som fargen skal hentes for.
         * @return en fargekode som en [Int].
         **/
        val alert = fetchAllAlerts().firstOrNull { it.id == id }
        return when (alert?.riskMatrixColor) {
            "Yellow" -> 0xFFFFEB3B.toInt()
            "Orange" -> 0xFFFF9800.toInt()
            "Red" -> 0xFFF44336.toInt()
            else -> 0xFF000000.toInt()
        }
    }

    private fun _featureToAlert(feature: Feature): Alert {
        /**
         * Hjelpefunksjon. Brukes kun i denne filen.
         * Tar inn en Feature og oppretter et nytt Alert-objekt.
         * @param feature Feature som skal konverteres.
         * @return Alert - konvertert fra Feature.
         */
        return Alert(
            id = feature.properties.id,
            name = feature.properties.eventAwarenessName,
            description = feature.properties.description,
            severity = feature.properties.severity,
            area = feature.properties.area,
            event = feature.properties.event,
            instruction = feature.properties.instruction,
            riskMatrixColor = feature.properties.riskMatrixColor,
            resources = feature.properties.resources,
            geometry = feature.geometry,
            county = feature.properties.county
        )
    }

    fun featureToAlertTest(feature : Feature) : Alert {
        /*
        * Hjelpefunksjon for aa teste den private funksjonen _featureToAlert i test-mappen
        * @param feature Feature som skal konverteres.
        * @return Alert - konvertert fra Feature.
        * */
        return _featureToAlert(feature)
    }

    suspend fun _getAllColorTypes(): Set<String> {
        /**
         * Hjelpefunksjon for å hente alle fargekodene som brukes i varsler.
         */
        val alerts = fetchAllAlerts()
        return alerts.map { it.riskMatrixColor }.toSet()
    }

}


suspend fun main() {
    /**
     * Enkel test for AlertDataSource.
     * Kjør denne for å se hva funksjonene gjør.
     */

    val varselDataSource = AlertDataSource()
    val response = varselDataSource.fetchAllAlerts()

    val farger = varselDataSource._getAllColorTypes()

    println("================ Farger ================")
    farger.forEach {
        println(it)
    }

    println("================ Varsler ===============")

    response.forEach {
        println("=====================================")
        println("id: ${it.id}")
        println("Navn: ${it.name}")
        println("Område: ${it.area}")
        println("Forklaring: ${it.description}")
        println("Alvorsgrad: ${it.severity}")
        println("Geometri: ${it.geometry}")
        println("Geometri type: ${it.geometry.type}")
        println("Farge: ${it.riskMatrixColor}")
    }
    println("=====================================")
}

