package no.uio.ifi.in2000.team27.havapp.ui.impact

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team27.havapp.R
import no.uio.ifi.in2000.team27.havapp.data.database.DatabaseRepositoryImpl
import no.uio.ifi.in2000.team27.havapp.data.database.HavfallDatabase
import no.uio.ifi.in2000.team27.havapp.model.utilities.Utilities


/**
 * ImpactViewModel er en ViewModel som brukes av ImpactScreen for å hente data fra databaseRepository.
 * Oppdaterer søppelsekk og progresjonsbaren som viser antall ryddinger brukeren har utført
 */
data class ImpactUiState(
    var progress: Float = 0f,
    var impactCounter: Int = 0,  // Teller antall CleaningActivities
    var trashBagImageResource: Int = 0
)

class ImpactViewModel(database: HavfallDatabase) : ViewModel() {

    private val databaseRepository = DatabaseRepositoryImpl(database.userDao(), database.cleaningActivityDao(), database.appStateDao())
    var userId = database.appStateDao().getUserId()

    private val _impactUiState = MutableStateFlow(ImpactUiState())
    val impactUiState: StateFlow<ImpactUiState> = _impactUiState.asStateFlow()

    /*
    var hasInternetAccess by mutableStateOf(true) // indikerer om appen har tilgang til internett
        private set // setteren er privat, slik at vi kun kan endre verdien fra ViewModel
     */

    init {
        fetchCleaningCount(_impactUiState)
    }

    private fun fetchCleaningCount(impactUiState: MutableStateFlow<ImpactUiState>) {
        viewModelScope.launch {
            databaseRepository.getActivitiesCountForUser(userId, impactUiState)
        }
        setProgress()
        setTrashBagImage()
    }

    private fun setProgress() {
        viewModelScope.launch {
            var count = _impactUiState.value.impactCounter
            if (count == 0) {
                delay(500)
                count = _impactUiState.value.impactCounter
            }
            // Konverterer antallet til en Float som passer i Progressbar, f eks: 3 -> 0.3
            val progress: Float = count.toFloat() / 10f
            // setter progress i UiState
            _impactUiState.update {
                it.copy(progress = progress)
            }
        }
    }

    private fun setTrashBagImage() {
        viewModelScope.launch {
            var count = _impactUiState.value.impactCounter
            if (count == 0) {
                delay(500)
                count = _impactUiState.value.impactCounter
            }

            var trashBagImageResource = 0

            if (count == 0) {
                trashBagImageResource = R.drawable.soppelsekk // Tom søppelsekk
            }
            if (count > 0) {
                trashBagImageResource =
                    Utilities.getTrashImageFromCount(count) // søppelsekk basert på impactCounter
            }
            if (count >= 10) {
                trashBagImageResource = Utilities.getTrashImageFromCount(10) // Full søppelsekk
            }
            _impactUiState.update {
                it.copy(trashBagImageResource = trashBagImageResource)
            }
        }
    }

}