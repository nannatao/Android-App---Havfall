package no.uio.ifi.in2000.team27.havapp.data.map

import com.google.android.gms.maps.model.LatLng
import no.uio.ifi.in2000.team27.havapp.data.weather.alert.AlertRepository

/**
 * Dette er repository-klassen for kartet.
 * Denne klassen er ansvarlig for å hente data om koordinater fra AlertRepository og konvertere
 * disse til polygons som kan vises av kartet.
 */


class MapRepository {

    private val alertRepository = AlertRepository()
    private var polygonMap: MutableList<Triple<String, List<LatLng>, Int>> = mutableListOf()

    private suspend fun getCoordinates() {
        /**
         * Henter koordinatene fra AlertRepository og konverterer disse til polygons som kan vises på kartet.
         * Den henter først alle varslene fra AlertRepository, og for hvert varsel henter den koordinatene,
         * konverterer disse til LatLng-objekter og legger til i polygonMap sammen med ID og fargekoden til varselet.
         */

        var tall = 0
        val allAlerts = alertRepository.getAllAlerts()

        allAlerts.forEach { alert ->
            val id = alert.id
            alert.geometry.coordinates.forEach { koordinater ->
                val list = convertToLatLng(koordinater)
                polygonMap.add(tall, Triple(id, list, alertRepository.getAlertColorFromId(id)))
                tall++
            }
        }
    }


    // Benyttes i koden
    fun convertToLatLng(pointsArray: List<List<Any>>): List<LatLng> {
        /**
         * Konverterer en liste med koordinater til en liste med LatLng-objekter.
         * @param pointsArray en liste av koordinater
         * @return en liste av LatLng-objekter
         */
        return pointsArray.map { point ->
            val lat = (point[1] as? Number)?.toDouble() ?: 0.0
            val long = (point[0] as? Number)?.toDouble() ?: 0.0
            LatLng(lat, long)
        }
    }


    // Denne koden fungerer med testen, men viser ikke polygoner i kartet
    // Derfor brukes convertToLatLng i koden og denne brukes bare for testing
    fun convertToLatLngWorksWithTest(pointsArray: List<List<Any>>): List<LatLng> {
        /**
         * Konverterer en liste med koordinater til en liste med LatLng-objekter.
         * @param pointsArray en liste av koordinater
         * @return en liste av LatLng-objekter
         */

        return pointsArray.flatten().mapNotNull { point ->
            if ((point is List<*>) && (point.size == 2)) {
                val lat = (point[1] as? Number)?.toDouble()
                val lng = (point[0] as? Number)?.toDouble()
                if (lat != null && lng != null) {
                    LatLng(lat, lng)
                } else {
                    null
                }
            } else {
                null
            }
        }
    }

    suspend fun getPolygonMap(): MutableList<Triple<String, List<LatLng>, Int>> {
        /**
         * Henter polygonMap.
         * @return polygonMap
         */
        getCoordinates()
        return polygonMap
    }
}