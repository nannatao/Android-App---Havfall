package no.uio.ifi.in2000.team27.havapp.data.weather.tidalwater

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL
import javax.xml.parsers.DocumentBuilderFactory


/*
Ressurser:
Url:
https://api.sehavniva.no/tideapi.php?lat=59.9139&lon=10.7522&fromtime=2024-03-19T00%3A00&totime=2024-03-20T00%3A00&datatype=pre&refcode=cd&place=Oslo&file=&lang=en&interval=60&dst=1&tzone=1&tide_request=locationdata
Vi endrer disse faktorene i URL-en:
- lat lon basert på posisjonsdata
- tidspunkt (for dags dato)
 */


class TidalWaterDataSource {

    //verdier for høyvann og lavvann
    var hoyvann = 0.00
    var lavvann = Double.MAX_VALUE
    var median = 0.00
    val tidalMap: MutableMap<Double, String> = mutableMapOf()

    /*
    Denne funksjonen henter tidevannsdata for en bestemt lokasjon, på en gitt dag.
     */

    suspend fun getTidalWater(lat: Double, lon: Double, fromtime: String, totime: String) =
        withContext(
            Dispatchers.IO
        ) {

            try {
                // URL-en til XML-filen
                val url =
                    URL("https://api.sehavniva.no/tideapi.php?lat=$lat&lon=$lon&fromtime=$fromtime&totime=$totime&datatype=pre&refcode=cd&place=&file=&lang=en&interval=60&dst=1&tzone=1&tide_request=locationdata")

                // Henter XML-filen fra URL-en
                val connection = url.openConnection()
                val inputStream = connection.getInputStream()

                // Parser XML-filen
                val documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                val document = documentBuilder.parse(inputStream)


                // Henter <waterlevel> elementer and henter ut informasjonen vi trenger
                val waterlevelNodes = document.getElementsByTagName("waterlevel")

                for (index in 0 until waterlevelNodes.length) {
                    val waterlevelNode = waterlevelNodes.item(index)
                    val value = waterlevelNode.attributes.getNamedItem("value").nodeValue.toDouble()
                    val time = waterlevelNode.attributes.getNamedItem("time").nodeValue

                    //putter høydeverdi og tidspunkt inn i map-et
                    tidalMap[value] = time

                    if (value > hoyvann) {
                        hoyvann = value
                    }

                    if (value < lavvann) {
                        lavvann = value
                    }

                }
                median = (hoyvann + lavvann) / 2

            } catch (e: Exception) {
                println(e.message)
            }
        }
}