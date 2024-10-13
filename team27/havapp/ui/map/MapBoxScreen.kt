package no.uio.ifi.in2000.team27.havapp.ui.map

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mapbox.geojson.Point
import no.uio.ifi.in2000.team27.havapp.model.utilities.Constants.Companion.BLUE_BACKGROUND_COLOR

/*
Kart med filter-knapper som kan skru ulike funksjoner av og på.
 */
@Composable
fun MapScreen(
    navController: NavController,
    mapViewModel: MapViewModel
) {
    val mapUiState by mapViewModel.mapUiState.collectAsState()
    val map = MapBox()

    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        map.MapBoxMap(
            point = Point.fromLngLat(10.7522, 59.9139),
            modifier = Modifier
                .fillMaxSize(),
            navController = navController,
            mapViewModel = mapViewModel,
        )
        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 16.dp, top = 16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.End
        ) {
            // Farevarsel-filter knapp
            Button(
                onClick = {
                    if (mapViewModel.mapUiState.value.alertFilter) {
                        mapViewModel.setAlertFilter(false)
                    } else {
                        mapViewModel.setAlertFilter(true)
                    }
                },
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (mapUiState.alertFilter) {
                        Color(0xFF2C6DFF)
                    } else {
                        Color(BLUE_BACKGROUND_COLOR)
                    }
                ),
            ) {
                Text("    Farevarsler    ")
            }

            // Miljøstasjon-filter knapp
            Button(
                onClick = {
                    if (mapViewModel.mapUiState.value.recyclingStationsVisible) {
                        mapViewModel.setRecyclingStationsVisible(false)
                    } else {
                        mapViewModel.setRecyclingStationsVisible(true)
                    }
                },
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (mapUiState.recyclingStationsVisible) {
                        Color(0xFF2C6DFF)
                    } else {
                        Color(BLUE_BACKGROUND_COLOR)
                    }
                ),
            ) {
                Text("  Miljøstasjoner  ")
            }
        }
    }
}