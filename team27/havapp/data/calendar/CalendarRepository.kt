package no.uio.ifi.in2000.team27.havapp.data.calendar

import android.os.Build
import androidx.annotation.RequiresApi
import no.uio.ifi.in2000.team27.havapp.model.calendar.CalendarModel
import no.uio.ifi.in2000.team27.havapp.model.cleaning.CleaningDaysRepository
import java.time.LocalDate

/**
 * CalendarRepository er et repository for kalenderfunksjonaliteten i HomeScreen.
 * Den har en kobling til CalendarDataSource og CleaningDaysRepository, og
 * funksjonen checkOptimalCleaningDay sjekker optimal ryddedag for en bestemt dato (hentet fra CalendarDataSource).
 */

class CalendarRepository {
    private val repo = CleaningDaysRepository()
    private val dataSource = CalendarDataSource()

    /*
   * Funksjonen sjekker om det er en perfekt ryddedag på en bestemt dato, og for en bestemt lokasjon.
   * @param lat latitude
   * @param lon longitude
   * @param dato Datoen man sjekker
   * */
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun checkOptimalCleaningDay(lat: Double, lon: Double, dato: LocalDate): Pair<Boolean, Pair<List<MutableMap<String?, Any?>?>, List<String>>?> {
        val fromtime = "${dato}T00:00"
        val totime = "${dato.plusDays(1)}T00:00"

        val weatherToday = repo.getWeatherToday(lat, lon, dato)
        val lowtideToday = repo.getCleaningTimeframe(59.9139, 10.7522, fromtime, totime) //changed lat lon to OSLO

        val optimalDay = repo.findOptimalCleaningDay(weatherToday, lowtideToday)
        val result = if (optimalDay) Pair(weatherToday, lowtideToday) else null

        // returner et par med boolean og et par med weathertoday og lowtidetoday
        return Pair(optimalDay, result)

    }

    /*
    * henter kalenderdata fra datasource, og returnerer en CalendarModel
    * */
    @RequiresApi(Build.VERSION_CODES.O)
    fun data(lastSelectedDate: LocalDate): CalendarModel {
        return dataSource.getData(lastSelectedDate)
    }

    // henter dagens dato
    @RequiresApi(Build.VERSION_CODES.O)
    fun today(): LocalDate {
        return dataSource.today
    }
}


// Test for sjekkPerfektRyddedag output
@RequiresApi(Build.VERSION_CODES.O)
suspend fun main() {
    val rep = CalendarRepository()
    val today = LocalDate.now()

    val result = rep.checkOptimalCleaningDay(59.9139, 10.7522, today.plusDays(2))

    if (result.first) {
        println("Weather forecast for the perfect cleaning day:")
        result.second?.first?.forEach { forecast ->
            println("Time: ${forecast?.get("klokkeslett")}, Weather: ${forecast?.get("værbeskrivelse")}, Temperature: ${forecast?.get("temperatur")}°C, Wind: ${forecast?.get("vind")} m/s, Wind Gust: ${forecast?.get("vindkast")} m/s, Precipitation: ${forecast?.get("nedbør")} mm")
        }
    } else {
        println("Failed to retrieve weather forecast.")
    }
}