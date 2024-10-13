package no.uio.ifi.in2000.team27.havapp.data.weather.alert.MetAlertsDataFrame

data class Feature(
    val geometry: Geometry,
    val properties: Properties,
    val type: String,
    val `when`: When
)