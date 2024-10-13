package no.uio.ifi.in2000.team27.havapp.ui.map

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.viewinterop.NoOpUpdate
import androidx.core.graphics.drawable.toBitmap
import androidx.navigation.NavController
import com.google.android.gms.maps.model.LatLng
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.animation.flyTo
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.PolygonAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PolygonAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.createPolygonAnnotationManager
import no.uio.ifi.in2000.team27.havapp.R


class MapBox {
    /*
    Denne funksjonen viser et standard kart fra MapBox.
    Kartet har noen funksjonaliteter som kan skrus av og på som et filter (se mapScreen og MapViewModel for sammeneheng).
     */
    @SuppressLint("UseCompatLoadingForDrawables")
    @Composable
    fun MapBoxMap(
        modifier: Modifier = Modifier,
        point: Point?,
        navController: NavController, //trengs for navigasjon
        mapViewModel: MapViewModel
    ) {
        val polygonMap by mapViewModel.mapUiState.collectAsState()
        val recyclingMap = createRecyclingMap()
        val context = LocalContext.current

        val mapUiState = mapViewModel.mapUiState.collectAsState()

        val recycling = remember {
            context.getDrawable(R.drawable.recycling_30)!!.toBitmap()
        }

        var pointAnnotationManager: PointAnnotationManager? by remember {
            mutableStateOf(null)
        }

        var polygonAnnotationManager: PolygonAnnotationManager? by remember {
            mutableStateOf(null)
        }

        var cleaningPinAnnotationManager: PointAnnotationManager? by remember {
            mutableStateOf(null)
        }

        var locationAnnotationManager: PointAnnotationManager? by remember {
            mutableStateOf(null)
        }

        AndroidView(
            factory = {
                MapView(it).also { mapView ->
                    mapView.mapboxMap.loadStyle(Style.OUTDOORS)
                    val annotationApi = mapView.annotations
                    pointAnnotationManager = annotationApi.createPointAnnotationManager()
                    polygonAnnotationManager = annotationApi.createPolygonAnnotationManager()
                    locationAnnotationManager = annotationApi.createPointAnnotationManager()
                    cleaningPinAnnotationManager = annotationApi.createPointAnnotationManager()
                }
            },
            update = { mapView ->
                if (point != null) {
                    //funksjon 1: viser omraader for farevarseler
                    if (mapUiState.value.alertFilter) {
                        polygonAnnotationManager?.apply {
                            for (trippelTuppel in polygonMap.map) {
                                val pointList =
                                    trippelTuppel.second.map { Point.fromLngLat(it.longitude, it.latitude) }

                                val polygonOptions = PolygonAnnotationOptions()
                                    .withPoints(listOf(pointList))
                                    .withFillColor("red")
                                    .withFillOpacity(0.2)

                                create(polygonOptions)
                            }
                            mapView.mapboxMap
                                .flyTo(CameraOptions.Builder().zoom(10.0).center(point).build())
                        }
                    } else {
                        //fjerner polygonene
                        polygonAnnotationManager?.deleteAll()
                    }

                    //funksjon 2: viser alle gjenvinningsstasjoner i Oslo
                    if (mapUiState.value.recyclingStationsVisible) {
                        pointAnnotationManager?.let {
                            recyclingMap.forEach { (_, latLng) ->
                                val point = Point.fromLngLat(latLng.longitude, latLng.latitude)
                                val pointAnnotationOptions = PointAnnotationOptions()
                                    .withPoint(point)
                                    .withIconImage(recycling)
                                pointAnnotationManager?.create(pointAnnotationOptions)
                            }
                            mapView.mapboxMap
                                .flyTo(CameraOptions.Builder().zoom(10.0).center(point).build())
                        }
                    } else {
                        //fjerner gjenvinningsstasjoner
                        pointAnnotationManager?.deleteAll()
                    }
                }
                NoOpUpdate
            },
            modifier = modifier
        )
    }

    private fun createRecyclingMap(): MutableMap<String, LatLng> {
        return mutableMapOf(
            //gjenvinningsstasjoner som aksepterer marint avfall
            "Smestad gjenvinningsstasjon" to LatLng(59.933780, 10.672470),
            "Haraldsrud gjenvinningsstasjon" to LatLng(59.930280, 10.828600),
            "Ragn-Sells Lørenskog" to LatLng(59.927830, 10.965530),
            "Grønmo gjenvinningsstasjon" to LatLng(59.838645, 10.845660),
            "Isi gjenvinningsstasjon" to LatLng(59.9386178, 10.4378309),
            "Oppegård gjenvinningsstasjon" to LatLng(59.795039, 10.823508),
            "Yggeset gjenvinningsstasjon" to LatLng(59.7924728, 10.4648197),
            "Teigen gjenvinningsstasjon" to LatLng(59.7563918, 10.6407847),
            "Follestad gjenvinningsstasjon" to LatLng(59.696606, 10.474554),
            "Bølstad gjenvinningsstasjon" to LatLng(59.690751, 10.772817),
        )
    }
}
