package no.uio.ifi.in2000.team27.havapp.data.location

/*
State for brukerens lokasjon.
 */

data class LocationState(
    val latitude: Double = 59.9139,                       // default Oslo
    val longitude: Double = 10.7522,                      // default Oslo
    var locationText: String = "$latitude, $longitude"    // textual representation of coordinates
)
