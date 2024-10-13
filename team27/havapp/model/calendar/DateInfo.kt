package no.uio.ifi.in2000.team27.havapp.model.calendar


// Klasse for datokort
// @param dato CalendarModel.Dato
// @param perfektRyddedag Boolean
// @param
data class DateInfo (
    val dato: CalendarModel.Dato,
    var perfektRyddedag: Boolean,
    val lavvann : List<String>?,
    val weather : List<MutableMap<String?, Any?>?>?
)