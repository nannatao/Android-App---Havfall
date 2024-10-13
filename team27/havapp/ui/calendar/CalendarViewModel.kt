package no.uio.ifi.in2000.team27.havapp.ui.calendar

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team27.havapp.data.calendar.CalendarRepository
import no.uio.ifi.in2000.team27.havapp.model.calendar.DateInfo


// UI state for kalender
data class CalenderUIState(
    var perfektDagKort: List<DateInfo> = emptyList(),
    var hasLoaded: Boolean = false
)

class CalendarViewModel : ViewModel() {

    private val _kalenderState = MutableStateFlow(CalenderUIState())
    val kalenderState: StateFlow<CalenderUIState> = _kalenderState.asStateFlow()
    private val repo = CalendarRepository()

    // initialiserer synlige datoer, altsaa de 10 neste datoene
    // oppdaterer ryddestatusen til hver av de 10 datoene
    @RequiresApi(Build.VERSION_CODES.O)
    fun initialiser(lat: Double, lon: Double) {
        setteSynlige()
        oppdaterRyddeStatus(lat, lon)
        _kalenderState.value = kalenderState.value.copy(hasLoaded = true)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setteSynlige() {
        // setter kalenderstate lik 10 neste datoer
        _kalenderState.value =
            CalenderUIState(repo.data(lastSelectedDate = repo.today()).synligeDatoer)
    }

    /*Oppdaterer statusen til hver dato i kalenderState, om det er optimal ryddedag eller ikke
    * @param lat Double
    * @param lon Double
    * */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun oppdaterRyddeStatus(lat: Double, lon: Double) {
        viewModelScope.launch {
            val updatedKort = kalenderState.value.perfektDagKort.map { dateInfo ->
                val (optimalDay, result) = repo.checkOptimalCleaningDay(lat, lon, dateInfo.dato.dato)
                val optimalweather = result?.first
                val tideInfo = result?.second

                // Oppdaterer datoInfo kort
                if (optimalDay) {
                    dateInfo.copy(
                        perfektRyddedag = optimalDay,
                        lavvann = tideInfo,
                        weather = optimalweather
                    )
                } else {
                    dateInfo.copy(
                        perfektRyddedag = optimalDay
                    )
                }
            }

            // Setter den oppdaterte listen med DatoInfo kort inni _kalenderState
            _kalenderState.value = kalenderState.value.copy(perfektDagKort = updatedKort)
        }
    }

}