package no.uio.ifi.in2000.team27.havapp.data.weather

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import no.uio.ifi.in2000.team27.havapp.data.weather.temperature.TemperaturDataSource
import java.time.LocalDate


/**
 * Dette Repositoryet gir et abstrakt lag over datakilder for værdata.
 * Denne brukes for å hente data fra de obligatoriske MET værdatakildene.
 */

class WeatherRepository {

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getWeatherInfoForToday(
        lat: Double,
        lon: Double,
        day: LocalDate
    ): List<MutableMap<String?, Any?>?> {
        /**
         * Henter værsymbolkode, temperatur og nedbørsmengde for i morgen for en gitt lokasjon.
         * @param lat Lengdegrad
         * @param lon Breddegrad
         * @return et map med data for de første 6 timene i morgen for en gitt lokasjon
         */

        val datasource = TemperaturDataSource()
        val url = "weatherapi/locationforecast/2.0/complete?lat=$lat&lon=$lon"

        return datasource.fetchWeatherTodayFromAPI(day.dayOfMonth, url)
    }

    /* Henter værikon
    * @param context Context
    * @param lat Lengdegrad
    * @param lon Breddegrad
    * @return en id for værsymbol
    * */
    @SuppressLint("DiscouragedApi")
    suspend fun getCustomWeatherIconForLocation(context: Context, lat: Double, lon: Double): Int {
        val datasource = TemperaturDataSource()
        val url = "weatherapi/nowcast/2.0/complete?lat=$lat&lon=$lon"
        val fileName = datasource.fetchWeatherSymbolCodeFromAPI(url)

        return context.resources.getIdentifier(fileName, "drawable", context.packageName)
    }
}

