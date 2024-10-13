package no.uio.ifi.in2000.team27.havapp.ui.userProfile

import android.content.Context
import android.location.Geocoder
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team27.havapp.data.database.DatabaseRepositoryImpl
import no.uio.ifi.in2000.team27.havapp.data.database.HavfallDatabase
import no.uio.ifi.in2000.team27.havapp.model.utilities.AvatarIdToIconMap
import no.uio.ifi.in2000.team27.havapp.model.cleaning.CleaningActivity
import java.util.Locale

// State for userProfile
data class UserProfileUiState(
    // Initialiserer variabler som skal brukes i UserProfile-skjermen
    var username: String = "",
    var avatarId: Int = 0,
    var userId: Long = 0,
    var cleaningActivities: List<CleaningActivity> = emptyList(),
    var cleaningSummary: MutableMap<String, Int> = mutableMapOf<String, Int>(
        "Plast" to 0,
        "Fiskeutstyr" to 0,
        "Sigaretter" to 0,
        "Annet" to 0
    )
)

/**
 * userProfileViewModel er en ViewModel som kobler til databasen og henter brukerinformasjon som brukernavn, avatar, ryddeaktiviteter og statistikk
 */
@RequiresApi(Build.VERSION_CODES.O)
class UserProfileViewModel(database: HavfallDatabase) : ViewModel() {

    private val databaseRepository = DatabaseRepositoryImpl(database.userDao(), database.cleaningActivityDao(), database.appStateDao())

    private val _userProfileUiState = MutableStateFlow(UserProfileUiState())
    val userProfileUiState = _userProfileUiState.asStateFlow()

    var showLogoutDialog = MutableStateFlow(false)

    var hasInternetAccess by mutableStateOf(true) // indikerer om appen har tilgang til internett
        private set // setteren er privat, slik at vi kun kan endre verdien fra ViewModel

    var userId = database.appStateDao().getUserId()

    init {
        fetchUsername()
        fetchAvatarResource()
        fetchUserCleaningActivities()
        fetchTrashSummary(_userProfileUiState)
    }

    fun fetchUsername() {
        viewModelScope.launch {
            val username = databaseRepository.getUserById(userId).username
            _userProfileUiState.update { currentState ->
                currentState.copy(
                    username = username
                )
            }
        }
    }

    fun fetchAvatarResource() {
        viewModelScope.launch {
            val userAvatar = databaseRepository.getUserById(userId).avatarId
            _userProfileUiState.update { currentState ->
                currentState.copy(
                    avatarId = AvatarIdToIconMap.mapping[userAvatar]!!
                )
            }
        }
    }

    fun fetchUserCleaningActivities() {
        /**
         * Fetches all non-empty cleaning activites for the user and puts them in the UI State.
         */
        viewModelScope.launch {
            databaseRepository.getCleaningActivitiesForUser(userId)
                .catch { exception -> Log.e("Error",
                    exception.toString()
                ) }.collect { cleaningActivities ->
                    val filteredActivities = cleaningActivities.filter { cleaningActivity ->
                        cleaningActivity.trash.values.any { amount -> amount > 0}
                    }
                    _userProfileUiState.update { currentState ->
                        currentState.copy(
                            cleaningActivities = filteredActivities
                        )
                    }
                }
        }
    }

    fun fetchTrashSummary(userProfileUiState: MutableStateFlow<UserProfileUiState>) {
        viewModelScope.launch {
            val summary = databaseRepository.getTrashSummaryForUser(userId, userProfileUiState)
        }
    }


    fun getCityCountryFromLatLng(
        context: Context,
        lat: Double,
        lon: Double,
        callback: (String) -> Unit
    ) {
        val geocoder = Geocoder(context, Locale.getDefault())
        geocoder.getFromLocation(lat, lon, 1)?.let { addresses ->
            val address = addresses.firstOrNull()
            val city = address?.subAdminArea?.replace(Regex("(?i)\\s?kommune"), "") ?: ""
            val country = address?.countryName?.replace("Norway", "Norge") ?: ""
            val thoroughfare = address?.thoroughfare
            val subthoroughfare = address?.subThoroughfare
            val result = "${thoroughfare} ${subthoroughfare}"
            callback(result)
        } ?: run {
            callback("Ukjent sted")
        }
    }

    fun onClickLogOut() {
        viewModelScope.launch {
            databaseRepository.logOut()
        }
    }

    fun showLogoutDialog() {
        showLogoutDialog.value = true
    }

    fun hideLogoutDialog() {
        showLogoutDialog.value = false
    }


}