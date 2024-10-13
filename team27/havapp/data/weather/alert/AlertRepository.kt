package no.uio.ifi.in2000.team27.havapp.data.weather.alert

import android.graphics.Color
import no.uio.ifi.in2000.team27.havapp.data.weather.alert.Alert

class AlertRepository {

    private val datasource = AlertDataSource()


    /**
     * Henter alle varsler fra datakilden.
     * @return en [MutableSet] av [Alert] objekter.
     **/
    suspend fun getAllAlerts(): MutableSet<Alert> {
        return datasource.fetchAllAlerts()
    }


    suspend fun getAlertForCounty(fylkeNavn: String): Alert? {
        /**
         * Henter et varsel for et fylke hvis det finnes.
         * @param fylkeNavn navnet på fylket.
         * @return et [Alert] objekt hvis det er en varsel knyttet til det, null ellers.
         **/
        val alerts = datasource.getAlertsForCounty(fylkeNavn)

        return if (alerts.isNotEmpty()) {
            alerts.first()
        } else {
            null
        }
    }

    fun getAlertColor(alert: Alert): Int {
        /**
         * Henter fargekoden for et varsel.
         * @param Alert varselet som fargen skal hentes for.
         * @return en fargekode som en [Int].
         **/
        val alpha = 255 // 255 = 100%, 128 = 50% opacity
        return when (alert.riskMatrixColor) {
            "Yellow" -> Color.argb(alpha, 255, 235, 59)
            "Orange" -> Color.argb(alpha, 255, 152, 0)
            "Red" -> Color.argb(alpha, 244, 67, 54)
            else -> Color.argb(alpha, 0, 0, 0)
        }
    }

    suspend fun getAlertColorFromId(id: String): Int {
        return datasource.getAlertColorFromId(id)
    }

}


// Test for getAllAlerts funksjon
suspend fun main() {
    val alertRepository = AlertRepository()

    val alerts = alertRepository.getAllAlerts()
    for (alert in alerts) {
        println(alert)
    }
}