package no.uio.ifi.in2000.team27.havapp.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team27.havapp.data.database.DatabaseRepository
import no.uio.ifi.in2000.team27.havapp.data.database.DatabaseRepositoryImpl
import no.uio.ifi.in2000.team27.havapp.data.database.HavfallDatabase

// State for onboarding, lagrer brukernavn, avatar og brukerId
data class OnboardingUiState(
    var username: String = "",
    var avatarId: Int = 0,
    var userId: Long = 0
)


/**
* OnboardingViewModel er en ViewModel som kobler til databasen og lagrer bruker informasjon.
*/
class OnboardingViewModel(database: HavfallDatabase) : ViewModel() {

    private val databaseRepository: DatabaseRepository = DatabaseRepositoryImpl(
        database.userDao(),
        database.cleaningActivityDao(),
        database.appStateDao()
    )

    private val _onboardingUiState = MutableStateFlow(OnboardingUiState())
    val onboardingUiState: StateFlow<OnboardingUiState> = _onboardingUiState.asStateFlow()

    fun insertUser(username: String, avatarId: Int) {
        viewModelScope.launch {
            // Sett inn bruker i databasen
            val userId = databaseRepository.insertUser(username, avatarId)

            // Oppdater UI state
            _onboardingUiState.update { currentState ->
                currentState.copy(
                    userId = userId,
                    username = username,
                    avatarId = avatarId
                )
            }
        }
    }

    fun updateUsernameInViewModel(username: String) {
        _onboardingUiState.update { currentState ->
            currentState.copy(
                username = username
            )
        }
    }
}