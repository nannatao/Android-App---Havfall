package no.uio.ifi.in2000.team27.havapp.data.weather.temperature

import android.os.Build
import androidx.annotation.RequiresApi
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get
import io.ktor.serialization.gson.gson
import io.ktor.util.appendIfNameAbsent
import no.uio.ifi.in2000.team27.havapp.data.weather.temperature.NowCastDataFrame.Geometry
import no.uio.ifi.in2000.team27.havapp.data.weather.temperature.NowCastDataFrame.Properties
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

/**
 * Denne DataSourcen henter data fra en ekstern API og returnerer temperaturen.
 * API Brukt: MET weatherapi/locationforecast/2.0
 * https://api.met.no/weatherapi/locationforecast/2.0/documentation
 **/

data class WeatherForecastResponse(
    val type: String,
    val geometry: Geometry,
    val properties: Properties
)


class TemperaturDataSource {

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

    suspend fun fetchWeatherSymbolCodeFromAPI(url: String): String? {
        /**
         * Henter værsymbolkode fra API
         * @param url: URL-en for å hente symbolet fra, e.g. "weatherapi/locationforecast/2.0/complete?lat=59.9138688&lon=10.7522454"
         **/
        try {
            val response: WeatherForecastResponse = client.get(url).body<WeatherForecastResponse>()
            return response.properties.timeseries.firstOrNull()?.data?.next_1_hours?.summary?.symbol_code
        } catch (e: Exception) {
            e.printStackTrace()
            "Error: ${e.message}"
        }
        return "clearsky_day"
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun fetchWeatherTodayFromAPI(dag: Int, url: String): List<MutableMap<String?, Any?>?> {
        /**
         * Henter været time for time for i morgen (3.april) fra API
         * Været for hver time blir lagt inn i et map -> værsymbolkode, temperatur, vindstyrke, vindkaststyrke og nedbørsmengde
         * dataform: liste med maps -> hver map har key = beskrivelse av dataene og value = dataene
         * @param url: URL-en for å hente symbolet fra, e.g. "weatherapi/locationforecast/2.0/complete?lat=59.9138688&lon=10.7522454"
         **/
        try {
            val response: WeatherForecastResponse = client.get(url).body<WeatherForecastResponse>()
            val formatter = DateTimeFormatter.ISO_ZONED_DATE_TIME

            //initierer liste med været time for time
            val weatherList: MutableList<MutableMap<String?, Any?>?> = mutableListOf()

            for (timesery in response.properties.timeseries) {
                val t = ZonedDateTime.parse(timesery.time, formatter).toLocalDateTime()
                val dato: LocalDate = t.toLocalDate()
                val tid = t.toLocalTime()

                if (dato.dayOfMonth == dag && tid >= LocalTime.parse("08:00") && tid <= LocalTime.parse(
                        "22:00"
                    )
                ) {
                    val symbolCode = if (timesery.data.next_1_hours != null) {
                        timesery.data.next_1_hours.summary.symbol_code
                    } else {
                        timesery.data.next_6_hours.summary.symbol_code
                    }
                    val temperature = timesery.data.instant.details.air_temperature
                    val windSpeed = timesery.data.instant.details.wind_speed
                    val gustSpeed = timesery.data.instant.details.wind_speed_of_gust
                    val precipitation = timesery.data.instant.details.precipitation_rate

                    val weatherTmrw: MutableMap<String?, Any?> = mutableMapOf()
                    weatherTmrw["klokkeslett"] = tid
                    weatherTmrw["værbeskrivelse"] = symbolCode
                    weatherTmrw["temperatur"] = temperature
                    weatherTmrw["vind"] = windSpeed
                    weatherTmrw["vindkast"] = gustSpeed
                    weatherTmrw["nedbør"] = precipitation

                    weatherList.add(weatherTmrw)
                }
            }
            return weatherList

        } catch (e: Exception) {
            e.printStackTrace()
            "Error: ${e.message}"
        }
        return emptyList()

    }
}
