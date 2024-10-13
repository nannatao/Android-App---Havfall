package no.uio.ifi.in2000.team27.havapp.data.calendar

import android.os.Build
import androidx.annotation.RequiresApi
import no.uio.ifi.in2000.team27.havapp.model.calendar.CalendarModel
import no.uio.ifi.in2000.team27.havapp.model.calendar.DateInfo
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.stream.Collectors
import java.util.stream.Stream

/**
 * CalendarDataSource er en datakilde kalenderfunksjonaliteten i HomeScreen.
 * Den henter data for aa lage en kalender, gjennom GetData-funksjonen.
 */

class CalendarDataSource {
    // Ulike funksjoner som brukes for aa lage kalender for naavaerende uke


    // @param today, gir dagens dato
    val today: LocalDate
        @RequiresApi(Build.VERSION_CODES.O)
        get() {
            return LocalDate.now()
        }

    /*Hovedfunksjonen for dataen i kalenderen
    * @param startDate, alltid satt lik today
    * @param lastSelectedDate
    * @return CalendarModel med synligeDatoer og dagens dato
    * */
    @RequiresApi(Build.VERSION_CODES.O)
    fun getData(dagens: LocalDate): CalendarModel {
        // helt til slutten av API-et
        val endOfAPI = today.plusDays(10)
        // finner datoene som skal vere synlige i ukekalender, datoene i denne uka
        val visibleDates = getDatesBetween(today, endOfAPI)
        // returnerer en CalenderModel med synlige datoer og dagens dato
        return toUiModel(visibleDates, dagens)
    }

    /*
    * Finner datoene mellom en startdato og en sluttdato
    * @param startDate
    * @param endDate
    * @returns List av datoer
    * Brukes i getData
    * */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun getDatesBetween(startDate: LocalDate, endDate: LocalDate): List<LocalDate> {
        // kalkulerer forskjellen mellom start og slutt
        val numOfDays = ChronoUnit.DAYS.between(startDate, endDate)

        // returnerer datoer fra start helt til sluttdate
        return Stream.iterate(startDate) { date ->
            date.plusDays(1)
        }
            .limit(numOfDays)
            .collect(Collectors.toList())
    }

    /* Gir en calendarmodel med dagens dato og datoene i uka
    * @param dateList, liste over datoer
    * @param lastSelectedDate, datoen i dag
    * @returns CalendarModel
    Brukes i getData
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun toUiModel(
        dateList: List<LocalDate>,
        lastSelectedDate: LocalDate
    ): CalendarModel {
        return CalendarModel(
            valgtDato = CalendarModel.Dato(
                valgt = true,
                dagens = lastSelectedDate.isEqual(today),
                dato = lastSelectedDate
            ),
            synligeDatoer = dateList.map {
                // Gjor om LocalDate objekter i dateList til DatoKort objekter
                toItemUiModel(it, it.isEqual(lastSelectedDate))
            }
        )
    }

    /*
  * Lager DatoKort objekter med perfektRyddedag som false
  * @param date, en av datoene i uka
  * @param isSelectedDate, om dato er valgt eller ikke
  * @returns DatoKort objekter
  * Brukes i toUiModel
   */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun toItemUiModel(date: LocalDate, isSelectedDate: Boolean) = DateInfo(
        dato = CalendarModel.Dato(
            valgt = isSelectedDate,
            dagens = date.isEqual(today),
            dato = date,
        ),
        perfektRyddedag = false,
        lavvann = emptyList(),
        weather = null
    )
}