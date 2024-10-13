package no.uio.ifi.in2000.team27.havapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.core.app.ActivityCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import no.uio.ifi.in2000.team27.havapp.data.database.HavfallDatabase
import no.uio.ifi.in2000.team27.havapp.model.utilities.Constants.Companion.BLUE_BACKGROUND_COLOR
import no.uio.ifi.in2000.team27.havapp.model.utilities.Constants.Companion.USER_PROFILE_BLUE_BACKGROUND_COLOR
import no.uio.ifi.in2000.team27.havapp.ui.calendar.CalendarViewModel
import no.uio.ifi.in2000.team27.havapp.ui.cleaningActivity.CleaningActivityScreen
import no.uio.ifi.in2000.team27.havapp.ui.cleaningActivity.CleaningActivityViewModel
import no.uio.ifi.in2000.team27.havapp.ui.home.HomeScreen
import no.uio.ifi.in2000.team27.havapp.ui.home.HomeViewModel
import no.uio.ifi.in2000.team27.havapp.ui.impact.ImpactScreen
import no.uio.ifi.in2000.team27.havapp.ui.impact.ImpactViewModel
import no.uio.ifi.in2000.team27.havapp.ui.map.MapScreen
import no.uio.ifi.in2000.team27.havapp.ui.map.MapViewModel
import no.uio.ifi.in2000.team27.havapp.ui.navbar.NavBarWithCleaningButton
import no.uio.ifi.in2000.team27.havapp.ui.onboarding.ChooseAvatarScreen
import no.uio.ifi.in2000.team27.havapp.ui.onboarding.OnboardingViewModel
import no.uio.ifi.in2000.team27.havapp.ui.onboarding.WelcomeScreen
import no.uio.ifi.in2000.team27.havapp.ui.theme.HavAppTheme
import no.uio.ifi.in2000.team27.havapp.ui.userProfile.UserProfileScreen
import no.uio.ifi.in2000.team27.havapp.ui.userProfile.UserProfileViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
class MainActivity : ComponentActivity() {

    // Initialisering av globale variabler
    private lateinit var context: Context
    private lateinit var onboardingViewModel: OnboardingViewModel
    private lateinit var cleaningActivityViewModel: CleaningActivityViewModel
    private lateinit var homeViewModel: HomeViewModel
    private val mapViewModel: MapViewModel by viewModels()
    private lateinit var calendarViewModel: CalendarViewModel
    companion object {
        lateinit var database: HavfallDatabase
    }

    // Location services
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient



    // Flagg for å indikere om brukeren har gjennomført onboarding
    private lateinit var startDestination: String

    @SuppressLint("DiscouragedApi")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HavAppTheme {
                /*********************
                 * Globale variabler *
                 *********************/
                context = this
                database = HavfallDatabase.getDatabase(context)

                homeViewModel = HomeViewModel()
                // Dersom brukeren har gjennomført onboarding, vises hjemskjermen, ellers vises velkomstskjermen
                val hasCompletedOnboarding = database.appStateDao().getHasCompletedOnboarding()
                startDestination = if (hasCompletedOnboarding) "homeScreen" else "welcome"

                onboardingViewModel = OnboardingViewModel(database)
                cleaningActivityViewModel = CleaningActivityViewModel(database, homeViewModel, context)

                calendarViewModel = CalendarViewModel()

                val navController: NavHostController = rememberNavController()

                // States for å håndtere lokasjon og tillatelser
                var locationText by remember { mutableStateOf("No location obtained :(") }
                var showPermissionResultText by remember { mutableStateOf(false) }
                var permissionResultText by remember { mutableStateOf("Permission Granted...") }

                Surface(
                    color = MaterialTheme.colorScheme.background
                ) {

                    // Spør om tillatelse til å bruke lokasjon
                    RequestLocationPermission(
                        onPermissionGranted = {
                            // Callback når tillatelse er gitt
                            showPermissionResultText = true
                            // Forsøk å hent siste kjente lokasjon
                            getLastUserLocation(
                                onGetLastLocationSuccess = {
                                    locationText =
                                        "Location using LAST-LOCATION: LATITUDE: ${it.first}, LONGITUDE: ${it.second}"
                                    homeViewModel.updateLocation(it.first, it.second, locationText, context)
                                },
                                onGetLastLocationFailed = { exception ->
                                    showPermissionResultText = true
                                    locationText =
                                        exception.localizedMessage ?: "Error Getting Last Location"
                                },
                                onGetLastLocationIsNull = {
                                    // Hvis ingen tidligere lokasjon er kjent, forsøk å hent nåværende lokasjon
                                    getCurrentLocation(
                                        onGetCurrentLocationSuccess = {
                                            locationText =
                                                "Location using CURRENT-LOCATION: LATITUDE: ${it.first}, LONGITUDE: ${it.second}"
                                            homeViewModel.updateLocation(
                                                it.first,
                                                it.second,
                                                locationText,
                                                context
                                            )
                                        },
                                        onGetCurrentLocationFailed = {
                                            showPermissionResultText = true
                                            locationText =
                                                it.localizedMessage
                                                    ?: "Error Getting Current Location"
                                        }
                                    )
                                }
                            )
                        },
                        onPermissionDenied = {
                            // Callback når tillatelse er nektet
                            showPermissionResultText = true
                            permissionResultText =
                                "Permission Denied :(" // TODO: Håndtere når brukeren sier nei?
                        },
                        onPermissionsRevoked = {
                            // Callback når tillatelse er trukket tilbake
                            showPermissionResultText = true
                            permissionResultText =
                                "Permission Revoked :(" // TODO: Håndtere når brukeren trekker tilbake tillatelsen?
                        }
                    )
                    /**
                     * Navigasjonskomponenten som håndterer navigasjon mellom skjermer.
                     * Den sørger for å vise riktig skjerm basert på hvilken rute som er aktiv.
                     * Den har også ansvaret for å vise navigasjonsbaren nederst på skjermen.
                     **/
                    AppNavigation(navController = navController)
                    val context = this
                    Log.d(
                        "weatherSymbol",
                        context.resources.getIdentifier(
                            "clearsky_day",
                            "drawable",
                            context.packageName
                        ).toString()
                    )
                }
            }
        }
    }

    /*******************
     *  AppNavigation  *
     *******************/
    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    fun AppNavigation(navController: NavHostController) {

        // State for å vise eller gjemme navigasjonsbaren
        var showBottomBar by remember { mutableStateOf(true) }
        var darkBlueCleaningIconBorder by remember { mutableStateOf(false) }

        Scaffold(
            bottomBar = {
                if (showBottomBar) {
                    if (darkBlueCleaningIconBorder) {
                        NavBarWithCleaningButton(navController, selectIconBorderColor = Color(USER_PROFILE_BLUE_BACKGROUND_COLOR), CleaningActivityViewModel(database, homeViewModel, context))
                    } else {
                        NavBarWithCleaningButton(navController, selectIconBorderColor = Color(BLUE_BACKGROUND_COLOR), CleaningActivityViewModel(database, homeViewModel, context)) // Vår egen navigasjonsbar
                    }
                }
            }
        ) {
            // Navigasjonsgraf for å håndtere navigasjon mellom skjermer
            NavHost(navController = navController, startDestination = startDestination) {

                composable("homeScreen") {
                    showBottomBar = true
                    darkBlueCleaningIconBorder = false
                    HomeScreen(navController = navController, homeViewModel = homeViewModel, calendarViewModel = calendarViewModel)
                }
                composable("polygonMapScreen") {
                    showBottomBar = true
                    darkBlueCleaningIconBorder = false
                    MapScreen(navController = navController, mapViewModel = mapViewModel)
                }
                composable("userProfileScreen") {
                    showBottomBar = true
                    darkBlueCleaningIconBorder = true
                    UserProfileScreen(navController = navController, userProfileViewModel = UserProfileViewModel(database))
                }
                composable("impactScreen") {
                    showBottomBar = true
                    darkBlueCleaningIconBorder = false
                    ImpactScreen(impactViewModel = ImpactViewModel(database), navController = navController)
                }
                composable("cleaningActivityScreen") {
                    showBottomBar = false // Gjem navigasjonsbaren på ryddeskjermen
                    darkBlueCleaningIconBorder = false
                    CleaningActivityScreen(navController = navController, cleaningActivityViewModel = CleaningActivityViewModel(database, homeViewModel, context))
                }
                composable("welcome") {
                    showBottomBar = false
                    WelcomeScreen(navController, onboardingViewModel = onboardingViewModel)
                }
                composable("avatar") {
                    showBottomBar = false
                    ChooseAvatarScreen(
                        navController = navController,
                        onboardingViewModel = onboardingViewModel
                    )
                }
            }
        }
    }

    /***********************
     *  Location Services  *
     ***********************/

    @OptIn(ExperimentalPermissionsApi::class)
    @Composable
    fun RequestLocationPermission(
        onPermissionGranted: () -> Unit,
        onPermissionDenied: () -> Unit,
        onPermissionsRevoked: () -> Unit
    ) {
        /**
         * Composable function to request location permissions and handle different scenarios.
         *
         * @param onPermissionGranted Callback to be executed when all requested permissions are granted.
         * @param onPermissionDenied Callback to be executed when any requested permission is denied.
         * @param onPermissionsRevoked Callback to be executed when previously granted permissions are revoked.
         */

        // Initialize the state for managing multiple location permissions.
        val permissionState = rememberMultiplePermissionsState(
            listOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
            )
        )

        // Use LaunchedEffect to handle permissions logic when the composition is launched.
        LaunchedEffect(key1 = permissionState) {
            // Check if all previously granted permissions are revoked.
            val allPermissionsRevoked =
                permissionState.permissions.size == permissionState.revokedPermissions.size

            // Filter permissions that need to be requested.
            val permissionsToRequest = permissionState.permissions.filter {
                !it.status.isGranted
            }

            // If there are permissions to request, launch the permission request.
            if (permissionsToRequest.isNotEmpty()) permissionState.launchMultiplePermissionRequest()

            // Execute callbacks based on permission status.
            if (allPermissionsRevoked) {
                onPermissionsRevoked()
            } else {
                if (permissionState.allPermissionsGranted) {
                    onPermissionGranted()
                } else {
                    onPermissionDenied()
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLastUserLocation(
        onGetLastLocationSuccess: (Pair<Double, Double>) -> Unit,
        onGetLastLocationFailed: (Exception) -> Unit,
        onGetLastLocationIsNull: () -> Unit
    ) {
        /**
         * Retrieves the last known user location asynchronously.
         *
         * @param onGetLastLocationSuccess Callback function invoked when the location is successfully retrieved.
         *        It provides a Pair representing latitude and longitude.
         * @param onGetLastLocationFailed Callback function invoked when an error occurs while retrieving the location.
         *        It provides the Exception that occurred.
         */

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        // Check if location permissions are granted
        if (areLocationPermissionsGranted()) {
            // Retrieve the last known location
            fusedLocationProviderClient.lastLocation
                .addOnSuccessListener { location ->
                    location?.let {
                        // If location is not null, invoke the success callback with latitude and longitude
                        onGetLastLocationSuccess(Pair(it.latitude, it.longitude))
                    }?.run {
                        onGetLastLocationIsNull()
                    }
                }
                .addOnFailureListener { exception ->
                    // If an error occurs, invoke the failure callback with the exception
                    onGetLastLocationFailed(exception)
                }
        }
    }

    @SuppressLint("MissingPermission")

    fun getCurrentLocation(
        onGetCurrentLocationSuccess: (Pair<Double, Double>) -> Unit,
        onGetCurrentLocationFailed: (Exception) -> Unit,
        priority: Boolean = true
    ) {
        /**
         * Retrieves the current user location asynchronously.
         *
         * @param onGetCurrentLocationSuccess Callback function invoked when the current location is successfully retrieved.
         *        It provides a Pair representing latitude and longitude.
         * @param onGetCurrentLocationFailed Callback function invoked when an error occurs while retrieving the current location.
         *        It provides the Exception that occurred.
         * @param priority Indicates the desired accuracy of the location retrieval. Default is high accuracy.
         *        If set to false, it uses balanced power accuracy.
         */
        // Determine the accuracy priority based on the 'priority' parameter
        val accuracy = if (priority) Priority.PRIORITY_HIGH_ACCURACY
        else Priority.PRIORITY_BALANCED_POWER_ACCURACY

        // Check if location permissions are granted
        if (areLocationPermissionsGranted()) {
            // Retrieve the current location asynchronously
            fusedLocationProviderClient.getCurrentLocation(
                accuracy, CancellationTokenSource().token,
            ).addOnSuccessListener { location ->
                location?.let {
                    // If location is not null, invoke the success callback with latitude and longitude
                    onGetCurrentLocationSuccess(Pair(it.latitude, it.longitude))
                }?.run {
                    //Location null do something
                }
            }.addOnFailureListener { exception ->
                // If an error occurs, invoke the failure callback with the exception
                onGetCurrentLocationFailed(exception)
            }
        }
    }

    private fun areLocationPermissionsGranted(): Boolean {
        /**
         * Checks if location permissions are granted.
         *
         * @return true if both ACCESS_FINE_LOCATION and ACCESS_COARSE_LOCATION permissions are granted; false otherwise.
         */
        return (ActivityCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    this, Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED)
    }
}