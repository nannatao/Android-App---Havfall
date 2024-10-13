package no.uio.ifi.in2000.team27.havapp.ui.home

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
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import no.uio.ifi.in2000.team27.havapp.data.location.LocationState
import no.uio.ifi.in2000.team27.havapp.data.weather.WeatherRepository
import no.uio.ifi.in2000.team27.havapp.data.weather.alert.Alert
import no.uio.ifi.in2000.team27.havapp.data.weather.alert.AlertRepository
import no.uio.ifi.in2000.team27.havapp.model.impact.FunFactCard
import no.uio.ifi.in2000.team27.havapp.model.impact.funfacts
import java.util.Locale

/**
 * HomeViewModel er en ViewModel som brukes av HomeScreen for å hente data fra WeatherRepository og AlertRepository.
 * ViewModelen holder på dataen som skal vises i HomeScreen, og oppdaterer denne dataen ved hjelp av WeatherRepository og AlertRepository.
 * ViewModelen har også en funksjon for å oppdatere lokasjonen til brukeren. Denne funksjonen kalles fra MainActivity.
 */


/***************
 * Dataklasser *
 **************/

data class HomeUiState(
    val temperature: Double? = null,                         // temperatur
    val weatherSymbolLink: Int? = 2131165278,               // lenke til værsymbol (int er id til skyer)
    val currentCounty: String = "Oslo",                    // fylket brukeren befinner seg i
    val alertUiState: AlertUiState = AlertUiState()       // varsel State
)

data class AlertUiState(
    val alert: Alert? = null,                            // varsel
    val alertColor: Int = 0xE1E2EC,                     // fargekoden til varselet
    val alertTitle: String = "Ingen farevarsel",       // tittel på varsel
    val alertDescription: String = "Ingen farevarsel" // beskrivelse av varsel
)


/*****************
 * HomeViewModel *
 ****************/
@RequiresApi(Build.VERSION_CODES.O)
class HomeViewModel : ViewModel() {

    // Repositoryer
    private val weatherRepository = WeatherRepository()
    private val alertRepository = AlertRepository()

    // Stateflows
    private val _homeUiState = MutableStateFlow(HomeUiState())
    val homeUiState: StateFlow<HomeUiState> = _homeUiState.asStateFlow()

    private val _alertUiState = MutableStateFlow(AlertUiState())
    val alertUiState: StateFlow<AlertUiState> = _alertUiState.asStateFlow()

    val _locationState = MutableStateFlow(LocationState())
    val locationState: StateFlow<LocationState> = _locationState.asStateFlow()

    //for snackbar
    //var hasInternetAccess by mutableStateOf(true)
    private var hasAlert by mutableStateOf(false)


    /******************
     * Initialisering *
     ******************/
    init {
        fetchInitialData()

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun fetchInitialData() {
        viewModelScope.launch {
            fetchAlertForCurrentCounty()
        }
    }

    /****************
     *   Location   *
     ****************/

    fun updateLocation(lat: Double, lon: Double, newLocation: String, context: Context) {
        /**
         * Oppdaterer lokasjonen til brukeren.
         * @param lat Lengdegrad
         * @param lon Breddegrad
         * @param newLocation Tekstlig presentasjon av koordinatene
         */
        viewModelScope.launch {
            withContext(IO) {
                _locationState.update { currentState ->
                    currentState.copy(
                        latitude = lat,
                        longitude = lon,
                        locationText = newLocation
                    )
                }
                getCountyFromLatLng(context, lat, lon) { county ->
                    _homeUiState.update { currentState ->
                        currentState.copy(
                            currentCounty = county
                        )
                    }
                }
                try {
                    val alert = withContext(IO) {
                        alertRepository.getAlertForCounty(homeUiState.value.currentCounty)
                    }
                    val alertColor = alertRepository.getAlertColor(alert!!)
                    _alertUiState.update { currentState ->
                        currentState.copy(
                            alert = alert,
                            alertColor = alertColor,
                            alertTitle = alert.name,
                            alertDescription = alert.description
                        )
                    }
                    hasAlert = true
                } catch (e: Exception) {
                    //hasInternetAccess = false
                    Log.d("Internet", "No Internet!")
                    e.printStackTrace()
                }
            }
        }
        onLocationChanged(lat, lon, context)
    }

    private fun onLocationChanged(lat: Double, lon: Double, context: Context) {
        /**
         * Denne funksjonen kalles når lokasjonen til brukeren endres.
         * Den oppdaterer temperaturen, værsymbolet og varselet for det nye stedet.
         * @param lat Lengdegrad
         * @param lon Breddegrad
         */
        getCountyFromLatLng(context, lat, lon) { county ->
            _homeUiState.update { currentState ->
                currentState.copy(
                    currentCounty = county
                )
            }
        }
        fetchAlertForCurrentCounty()
    }

    private fun getCountyFromLatLng(
        context: Context,
        lat: Double,
        lon: Double,
        callback: (String) -> Unit
    ) {
        val geocoder = Geocoder(context, Locale.getDefault())
        geocoder.getFromLocation(lat, lon, 1)?.let { addresses ->
            val address = addresses.firstOrNull()
            val county = address?.adminArea?.replace(Regex("(?i)\\s?kommune"), "") ?: ""
            callback(county)
        } ?: run {
            callback("Ukjent fylke")
        }
    }


    /****************
     *  Temperatur  *
     ****************/
    suspend fun fetchWeatherSymbol(context: Context, lat: Double, lon: Double) {
        viewModelScope.launch(IO) {
            try {
                val symbol = withContext(IO) {
                    weatherRepository.getCustomWeatherIconForLocation(context, lat, lon)
                }
                _homeUiState.update { currentState ->
                    currentState.copy(
                        weatherSymbolLink = symbol
                    )
                }
            } catch (e: Exception) {
                //hasInternetAccess = false
                Log.d("Internet", "No Internet!")
                //e.printStackTrace()
            }
        }
    }

    /***********
     *  Alert  *
     ***********/

    private fun fetchAlertForCurrentCounty() {
        /**
         * Henter varselet for fylket brukeren befinner seg i.
         */
        viewModelScope.launch(IO) {
            try {
                val alert = withContext(IO) {
                    alertRepository.getAlertForCounty(homeUiState.value.currentCounty)
                }
                val alertColor = alertRepository.getAlertColor(alert!!)
                _alertUiState.update { currentState ->
                    currentState.copy(
                        alert = alert,
                        alertColor = alertColor,
                        alertTitle = alert.name,
                        alertDescription = alert.description
                    )
                }
                hasAlert = true
            } catch (e: Exception) {
                //hasInternetAccess = false
                Log.d("Internet", "No Internet!")
                //e.printStackTrace()
            }
        }
    }

    /********************
     *  Random Funfact  *
     ********************/
    fun getRandomFunfact() : FunFactCard {
        val randomIndex = (0..<funfacts.size).random()
        return funfacts[randomIndex]
    }
}
