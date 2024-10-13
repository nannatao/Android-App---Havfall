package no.uio.ifi.in2000.team27.havapp.data.weather.tidalwater

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

class TidalRepository {
    val data = TidalWaterDataSource()

    /*
    Denne funksjonen finner tidsvinduer der det er lavest vannstand (lavvann -> median).
     */

    // Get low tide times for a given time frame
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getLowtideTime(
        lat: Double,
        lon: Double,
        fromtime: String,
        totime: String
    ): Array<String> {
        data.tidalMap.clear() // Clears the existing map before fetching new data

        data.getTidalWater(lat, lon, fromtime, totime)
        var lavvannTider: Array<String> = emptyArray()

        val lavvann = data.lavvann
        val median = data.median

        val tidalMapCopy = data.tidalMap.toMap()

        tidalMapCopy.forEach { (hoyde, tid) ->
            if (hoyde in lavvann..median) {
                val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
                val offsetDateTime = OffsetDateTime.parse(tid, formatter)
                val hourlyTime = offsetDateTime.toLocalTime().toString()

                lavvannTider += hourlyTime

            }
        }

        return lavvannTider
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getCleaningTime(list: Array<String>): List<String> {
        // kl 8 til kl 22
        // Filter the list to get values between 08:00 and 22:00
        val filteredList = list.filter { timeString ->
            val time = LocalTime.parse(timeString)
            time in LocalTime.of(8, 0)..LocalTime.of(22, 0)
        }

        return filteredList

    }

}


//Test for tider for lavvann
@RequiresApi(Build.VERSION_CODES.O)
suspend fun main() {
    val repo = TidalRepository()
    val liste = repo.getLowtideTime(59.9139, 10.7522, "2024-04-29T00:00", "2024-04-30T00:00")

    println("Alle tider: ")
    liste.forEach {
        println(it)
    }

    val filtrertListe = repo.getCleaningTime(liste)

    println("")
    println("Filtrert liste:")

    filtrertListe.forEach {
        println(it)
    }
}