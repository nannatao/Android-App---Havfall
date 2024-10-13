package no.uio.ifi.in2000.team27.havapp.model.utilities

fun translateMonth(month: String): String {
    // Konverterer månednavn fra engelsk til norsk.
    // Brukes i CalendarCard
    return when (month) {
        "JANUARY" -> "januar"
        "FEBRUARY" -> "februar"
        "MARCH" -> "mars"
        "APRIL" -> "april"
        "MAY" -> "mai"
        "JUNE" -> "juni"
        "JULY" -> "juli"
        "AUGUST" -> "august"
        "SEPTEMBER" -> "september"
        "OCTOBER" -> "oktober"
        "NOVEMBER" -> "november"
        "DECEMBER" -> "desember"
        else -> "januar"
    }
}