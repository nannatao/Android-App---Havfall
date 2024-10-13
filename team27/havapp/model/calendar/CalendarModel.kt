package no.uio.ifi.in2000.team27.havapp.model.calendar

import java.time.LocalDate


// Modell for kalenderen, inneholder kall
// @param valgtDato : Dato
// @param synligeDatoer : List<DatoKort>
data class CalendarModel(
    val valgtDato: Dato,
    val synligeDatoer: List<DateInfo>
) {
    data class Dato(
        val dato: LocalDate,
        val valgt: Boolean,
        val dagens: Boolean
    )

}

