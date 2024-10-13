package no.uio.ifi.in2000.team27.havapp.ui.cleaningActivity

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team27.havapp.MainActivity.Companion.database
import no.uio.ifi.in2000.team27.havapp.data.database.DatabaseRepositoryImpl
import no.uio.ifi.in2000.team27.havapp.data.database.HavfallDatabase
import no.uio.ifi.in2000.team27.havapp.model.cleaning.CleaningActivity
import no.uio.ifi.in2000.team27.havapp.model.cleaning.TrashType
import no.uio.ifi.in2000.team27.havapp.model.utilities.translateMonth
import no.uio.ifi.in2000.team27.havapp.ui.home.HomeViewModel
import java.time.LocalDate
import java.util.Locale

/*
Viewmodel for CleaningActivityScreen
 */

/*
UiState for cleaningActivity
 */
data class CleaningActivityUiState(
    val cleaningActivity: CleaningActivity = CleaningActivity(
        location = "",
        date = "",
        duration = "",
        trash = mapOf(
            "Plast" to 0,
            "Fiskeutstyr" to 0,
            "Sigaretter" to 0,
            "Annet" to 0
        ),
        userId = database.appStateDao().getUserId()
    ),
    val currentActivityId: Long = 0
)

/*
UiState for timer
 */
data class TimerState(
    var time: Long = 0L,
    val isRunning: Boolean = false,
    val startTime: Long = 0L,
    val showStopDialog: Boolean = false,
    val showResultDialog: Boolean = false,
    var hasStartedActivity: Boolean = false
)

@RequiresApi(Build.VERSION_CODES.O)
class CleaningActivityViewModel(database: HavfallDatabase, homeViewModel: HomeViewModel,
                                @field:SuppressLint("StaticFieldLeak") val context: Context
) : ViewModel() {

    private val databaseRepository = DatabaseRepositoryImpl(database.userDao(), database.cleaningActivityDao(), database.appStateDao())

    var userId = database.appStateDao().getUserId()
    private val locationState = homeViewModel._locationState


    // Stateflows
    private val _cleaningActivityUiState = MutableStateFlow(CleaningActivityUiState())
    val cleaningActivityUiState: StateFlow<CleaningActivityUiState> =
        _cleaningActivityUiState.asStateFlow()

    private val _timerState = MutableStateFlow(TimerState())
    val timerState: StateFlow<TimerState> = _timerState.asStateFlow()


    init {
        fetchInitialData()
    }

    /*
    Oppdaterer brukers lokasjon, og setter igang cleaningActivity (ryddeaktivitet) og timer
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun fetchInitialData() {
        viewModelScope.launch {
            locationState.update { locationState ->
                locationState.copy(
                    latitude = locationState.latitude,
                    longitude = locationState.longitude
                )
            }
            startCleaningActivity()
            startTimer()
        }
    }

    /*
    Starter ryddeaktivitet: lager et CleaningActivity objekt og oppdaterer verdiene til objektet
     */
    private fun startCleaningActivity() {
        //val con = contexxt
        viewModelScope.launch {
            val cleaningActivity = CleaningActivity(
                location = getCityCountryFromLatLng(context = context, lat = locationState.value.latitude, lon = locationState.value.longitude),
                date = "${LocalDate.now().dayOfMonth}\n${translateMonth(LocalDate.now().month.toString())}",
                duration = "",
                trash = mapOf(
                    "Plast" to 0,
                    "Fiskeutstyr" to 0,
                    "Sigaretter" to 0,
                    "Annet" to 0
                ),
                userId = userId
            )
            // Oppdaterer UI State
            _cleaningActivityUiState.value = CleaningActivityUiState(cleaningActivity = cleaningActivity)
        }
    }

    /*
    Stopper ryddeaktivitet og oppdaterer tiden som ble brukt.
     */
    private fun stopCleaningActivity() {
        viewModelScope.launch {
            _cleaningActivityUiState.update { currentState ->
                val updatedCleaningActivity = currentState.cleaningActivity.copy(
                    duration = timerState.value.time.toString(),
                )
                currentState.copy(cleaningActivity = updatedCleaningActivity)
            }
            databaseRepository.insertCleaningActivity(cleaningActivityUiState.value.cleaningActivity)
        }
    }

    /*
    Resetter UiState til CleaningActivity (alle verdiene til "" eller 0)
     */
    private fun resetCleaningActivity() {
        viewModelScope.launch{
            _cleaningActivityUiState.value = CleaningActivityUiState(
                cleaningActivity = CleaningActivity(
                    location = "",
                    date = "",
                    duration = "",
                    trash = mapOf(
                        "Plast" to 0,
                        "Fiskeutstyr" to 0,
                        "Sigaretter" to 0,
                        "Annet" to 0
                    ),
                    userId = userId
                ),
                currentActivityId = 0
            )
        }
    }

    // Legger til en TrashType til CleaningActivity
    fun onClickAddTrashType(trashType: TrashType) {
        viewModelScope.launch {
            val trashType: String = trashTypeToString(trashType)
            _cleaningActivityUiState.update { currentState ->
                val updatedStatistics = currentState.cleaningActivity.trash.toMutableMap()
                updatedStatistics[trashType] = updatedStatistics.getOrDefault(trashType, 0) + 1
                currentState.copy(
                    cleaningActivity = currentState.cleaningActivity.copy(
                        trash = updatedStatistics
                    )
                )
            }
        }
    }

    // Fjerner en TrashType fra CleaningActivity
    fun onClickRemoveTrashType(trashType: TrashType) {
        viewModelScope.launch {
            val trashType: String = trashTypeToString(trashType)
            _cleaningActivityUiState.update { currentState ->
                val updatedStatistics = currentState.cleaningActivity.trash.toMutableMap()
                val currentCount = updatedStatistics.getOrDefault(trashType, 0)
                if (currentCount > 0) {
                    updatedStatistics[trashType] = currentCount - 1
                }
                currentState.copy(
                    cleaningActivity = currentState.cleaningActivity.copy(
                        trash = updatedStatistics
                    )
                )
            }
        }
    }

    private var timerJob: Job? = null

    /*Setter i gang timer.
     */
    fun startTimer() {
        if (timerJob == null || !timerJob!!.isActive) {
            val startTime = System.currentTimeMillis() - _timerState.value.time
            timerJob = viewModelScope.launch {
                _timerState.update { it.copy(isRunning = true) }
                while (_timerState.value.isRunning) {
                    val updatedTime = System.currentTimeMillis() - startTime
                    _timerState.update { it.copy(time = updatedTime) }
                    delay(1000)
                }
            }
        }
    }

    /*Stopper timer */
    fun stopTimer() {
        _timerState.value = _timerState.value.copy(isRunning = false)
        showStopDialog()
    }

    //Resetter timer
    private fun resetTimer() {
        _timerState.value.time = 0L
    }

    //Viser dialog når ryddingen stopper (midlertidig)
    private fun showStopDialog() {
        _timerState.value = _timerState.value.copy(showStopDialog = true)
    }

    //Fjerner dialog.
    fun hideStopDialog() {
        _timerState.value = _timerState.value.copy(showStopDialog = false)
    }

    //Viser en dialog med resultat av rydding (TrashTypes og tid brukt).
    fun showResultDialog() {
        _timerState.value = _timerState.value.copy(showResultDialog = true)
    }

    /*
    Etter at resultDialog er blitt fremvist, så resettes timer og cleaningactivity
     */
    fun hideResultDialog() {
        _timerState.value = _timerState.value.copy(showResultDialog = false)
        _timerState.value = _timerState.value.copy(hasStartedActivity = false)
        stopCleaningActivity()
        resetTimer()
        resetCleaningActivity()
    }

    /*Henter lokasjon*/
    private fun getCityCountryFromLatLng(
        context: Context,
        lat: Double,
        lon: Double,
    ): String {
        val geocoder = Geocoder(context, Locale.getDefault())
        geocoder.getFromLocation(lat, lon, 1)?.let { addresses ->
            val address = addresses.firstOrNull()
            val thoroughfare = address?.thoroughfare
            val subthoroughfare = address?.subThoroughfare
            return "$thoroughfare $subthoroughfare"
        } ?: run {
           return "Ukjent sted"
        }
    }

}