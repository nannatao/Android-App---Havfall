package no.uio.ifi.in2000.team27.havapp.ui.map

import android.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team27.havapp.data.map.MapRepository

// state for kartet hvor man kan skru av og på ulike filter
data class MapUIState(
    val map: List<Triple<String, List<LatLng>, Int>> = emptyList(), // [(id, koordinater, farge)]
    var alertFilter: Boolean = true, //farevarsler
    var recyclingStationsVisible: Boolean = true, //gjenvinningsstasjoner
)

/**
* MapViewModel er en ViewModel som brukes av MapBoxScreen for å hente polygoner for farevarsel og endre på synligheten til filter.
*/
class MapViewModel : ViewModel() {
    private val repository = MapRepository()
    private var _mapUiState: MutableStateFlow<MapUIState> = MutableStateFlow(MapUIState())
    val mapUiState: StateFlow<MapUIState> = _mapUiState.asStateFlow()

    init {
        fetchPolygons()
    }

    private fun fetchPolygons() {
        viewModelScope.launch {
            try {
                val map = repository.getPolygonMap().map { (id, coordinates, color) ->
                    val transparentColor =
                        Color.argb(64, Color.red(color), Color.green(color), Color.blue(color))
                    Triple(id, coordinates, transparentColor)
                }
                _mapUiState.update { currentState ->
                    currentState.copy(
                        map = map
                    )
                }

            } catch (e: Exception) {
                println("Klarte ikke å hente data")
            }
        }
    }

    fun setAlertFilter(alertFilter: Boolean) {
        _mapUiState.value = _mapUiState.value.copy(alertFilter = alertFilter)
    }

    fun setRecyclingStationsVisible(recyclingStationsVisible: Boolean) {
        _mapUiState.value = _mapUiState.value.copy(recyclingStationsVisible = recyclingStationsVisible)
    }
}