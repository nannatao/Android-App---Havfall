package no.uio.ifi.in2000.team27.havapp.data.weather.temperature.NowCastDataFrame

data class Data(
    val instant: Instant,
    val next_1_hours: Next1Hours,
    val next_6_hours: Next6Hours,
    val next_12_hours: Next12Hours
)