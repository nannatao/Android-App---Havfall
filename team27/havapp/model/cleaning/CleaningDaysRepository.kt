package no.uio.ifi.in2000.team27.havapp.model.cleaning

import android.os.Build
import androidx.annotation.RequiresApi
import no.uio.ifi.in2000.team27.havapp.data.weather.WeatherRepository
import no.uio.ifi.in2000.team27.havapp.data.weather.tidalwater.TidalRepository
import java.time.LocalDate
import java.time.LocalTime

class CleaningDaysRepository {

    private val weatherRepo = WeatherRepository()
    private val tidalRepo = TidalRepository()

    //funksjonen henter tider for lavvann for en gitt dag mellom kl.8 og kl.22
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getCleaningTimeframe(
        lat: Double,
        lon: Double,
        fromtime: String,
        totime: String
    ): List<String> {

        val lavvannTider = tidalRepo.getLowtideTime(lat, lon, fromtime, totime)

        return tidalRepo.getCleaningTime(lavvannTider)
    }

    //funkjonen henter været for en gitt dag
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getWeatherToday(
        lat: Double,
        lon: Double,
        day: LocalDate
    ): List<MutableMap<String?, Any?>?> {
        return weatherRepo.getWeatherInfoForToday(lat, lon, day)
    }



    /**
     * Algoritme for å finne optimal ryddedag
     *
     * Metrikker:
     * - temperatur (Double)
     * - værbeskrivelse (String)
     * - vind (Double)
     * - vindkast (Double)
     * - nedbør (Double)
     * - tidevann (List<String>)
     *
     * API brukt: locationForecast 2.0
     * API-link: https://api.met.no/weatherapi/locationforecast/2.0/complete?lat=60.10&lon=10
     *
     * Om algoritmen:
     * - sjekker været time for time på en valgt dag
     * - tidsramme: kl.08:00 til kl.22.00
     * - parametre: værinfo og tider for lavvann for en gitt dag
     * - dataformat som blir returnert: liste med map-er
     *
     **/
    @RequiresApi(Build.VERSION_CODES.O)
    fun findOptimalCleaningDay(
        weatherInfo: List<MutableMap<String?, Any?>?>,
        tideForCleaning: List<String>
    ): Boolean {
        val temperatureRange = 0.0..25.0
        val windSpeedRange = 0.0..7.9
        val gustSpeedRange = 0.0..10.0
        val precipitationRange = 0.0..0.4

        val goodWeather = setOf(
            "clearsky_day", "clearsky_night",
            "fair_day", "fair_night",
            "partlycloudy_day", "partlycloudy_night",
            "cloudy"
        )

        for (map in weatherInfo) {
            if (map != null) {
                val klokkeslett = map["klokkeslett"] as? LocalTime
                val temperature = map["temperatur"] as? Double
                val weatherDescription = map["værbeskrivelse"] as? String
                val windSpeed = map["vind"] as? Double
                val gustSpeed = map["vindkast"] as? Double
                val precipitation = map["nedbør"] as? Double

                return (temperature!! in temperatureRange &&
                    weatherDescription in goodWeather &&
                    windSpeed!! in windSpeedRange &&
                    gustSpeed!! in gustSpeedRange &&
                    precipitation!! in precipitationRange &&
                    tideForCleaning.contains(klokkeslett?.toString())
                )
            } else {
                println("Weather information is null")
            }
        }
        return false

    }

}