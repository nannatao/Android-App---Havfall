package no.uio.ifi.in2000.team27.havapp.ui.home

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import no.uio.ifi.in2000.team27.havapp.R
import no.uio.ifi.in2000.team27.havapp.data.location.LocationState
import no.uio.ifi.in2000.team27.havapp.model.utilities.Constants.Companion.BLUE_BACKGROUND_COLOR
import no.uio.ifi.in2000.team27.havapp.model.utilities.Constants.Companion.BLUE_BUTTON_COLOR
import no.uio.ifi.in2000.team27.havapp.model.utilities.Constants.Companion.LIGHT_BLUE_CARD_BACKGROUND
import no.uio.ifi.in2000.team27.havapp.model.utilities.Constants.Companion.WHITEISH_COLOR
import no.uio.ifi.in2000.team27.havapp.ui.calendar.CalendarViewModel
import no.uio.ifi.in2000.team27.havapp.ui.calendar.PerfectDaysCard
import no.uio.ifi.in2000.team27.havapp.ui.sfFontFamily


/**************
 * HomeScreen *
 **************/
@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint(
    "UnusedMaterial3ScaffoldPaddingParameter", "UnusedBoxWithConstraintsScope",
    "StateFlowValueCalledInComposition"
)
@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel,
    calendarViewModel: CalendarViewModel,
    navController: NavController //brukes ikke, men skal tas inn for navigasjonen
) {
    val context = LocalContext.current
    val alertUiState by homeViewModel.alertUiState.collectAsState()
    val locationState by homeViewModel.locationState.collectAsState()

    val snackbarHostState = SnackbarHostState()

    LaunchedEffect(Unit) {
        homeViewModel.fetchWeatherSymbol(context, locationState.latitude, locationState.longitude)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->


        //snackbar for internet access
        /*if (!homeViewModel.hasInternetAccess) {
            // LaunchedEffect for å vise snackbar kun når hasInternetAccess endres til false
            LaunchedEffect(key1 = Unit) {
                snackbarHostState.showSnackbar(
                    message = "Feil ved henting av data. Sjekk din Internett-tilgang og prøv igjen.",
                    duration = SnackbarDuration.Short
                )
            }
        }*/


        HomeContent(
            calendarViewModel = calendarViewModel,
            alertUiState = alertUiState,
            locationState = locationState,
            innerPadding = innerPadding,
            homeViewModel = homeViewModel
        )
    }
}


/*
* Innholdet til homescreen
* */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeContent(
    calendarViewModel: CalendarViewModel,
    alertUiState: AlertUiState,
    locationState: LocationState,
    innerPadding: PaddingValues,
    homeViewModel: HomeViewModel
) {
    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start,
        modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()
            .background(Color(BLUE_BACKGROUND_COLOR))
    ) {

        WelcomeAndAlertSymbol(alertUiState = alertUiState)

        PerfectDaysCard(lat = locationState.latitude, lon = locationState.longitude, calendarViewModel = calendarViewModel)

        PopupInformationDialog()

        Spacer(
            modifier = Modifier
                .height(15.dp)
        )

        FunFactCard(homeViewModel)
    }
}

/*
Velkomst til brukeren og eventuelt farevarsel-ikon.
-viser blå boks hvis det ikke er farevarsel og viser farevarsel boks hvis farevarsel
 */
@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun WelcomeAndAlertSymbol(alertUiState: AlertUiState) {
    val openDialog = remember { mutableStateOf(false) }
    val painter = painterResource(id = R.drawable.faretegn_bakgrunn) //farevarsel bilde

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "La oss rydde kysten!",
            fontWeight = FontWeight.Normal,
            //style = TextStyle(fontSize = 28.sp),
            style = MaterialTheme.typography.headlineMedium,

            color = Color(WHITEISH_COLOR),
            modifier = Modifier
                .padding(top = 50.dp)
                .then(Modifier.padding(horizontal = 15.dp))
        )

        Spacer(
            modifier = Modifier
                .width(5.dp)
                .padding(top = 100.dp)
        )

        /*
        Mer detaljert informasjon om farevarsel
         */
        if (alertUiState.alert == null) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Spacer(
                    modifier = Modifier
                        .width(5.dp)
                        .padding(top = 28.dp)
                )

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color(BLUE_BACKGROUND_COLOR)
                    ),
                    modifier = Modifier
                        .padding(16.dp)
                        .size(50.dp)
                        .clickable { openDialog.value = true },
                    shape = RoundedCornerShape(8.dp),
                ) {

                }
            }

        } else {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Spacer(
                    modifier = Modifier
                        .width(5.dp)
                        .padding(top = 28.dp)
                )

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color(BLUE_BACKGROUND_COLOR)
                    ),
                    modifier = Modifier
                        .padding(16.dp)
                        .size(50.dp)
                        .clickable { openDialog.value = true },
                    shape = RoundedCornerShape(8.dp),
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Image( //FAREVARSEL BILDE
                            painter = painter,
                            contentDescription = "faretegn_bakgrunn",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                        )

                        if (openDialog.value) {
                            Log.d("HomeScreen", "AlertUiState: ${alertUiState.alert}")
                            AlertDialog(
                                onDismissRequest = {
                                    openDialog.value = false
                                },
                                containerColor = Color(LIGHT_BLUE_CARD_BACKGROUND),
                                title = { Text(text = alertUiState.alertTitle) },
                                text = { Text(text = alertUiState.alertDescription) },
                                confirmButton = {
                                    Box( modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center
                                    )
                                    { Button( onClick = { openDialog.value = false },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(BLUE_BUTTON_COLOR)) ) {
                                        Text("Lukk", color = Color(WHITEISH_COLOR)) } } }

                            )

                        }
                    }
                }
            }
        }
    }
}


/******************
 * Info-boks med pop-up*
 ******************/
@Composable
fun PopupInformationDialog() {
    val openDialog = remember { mutableStateOf(false) }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(WHITEISH_COLOR)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        modifier = Modifier
            .padding(top = 20.dp)
                then (Modifier.padding(horizontal = 28.dp))
                then (Modifier.padding(bottom = 10.dp))
            .fillMaxWidth()
            .height(130.dp)
            .clickable { openDialog.value = true },
        shape = RoundedCornerShape(20.dp)

    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(R.drawable.hvorfor_rydde),
                contentDescription = "Your Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            Column(
                modifier = Modifier
                    .padding(16.dp)
                        then (Modifier.padding(horizontal = 25.dp))
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Hvorfor rydde?",
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = sfFontFamily,
                    fontSize = 30.sp,
                    color = Color(WHITEISH_COLOR),
                    textAlign = TextAlign.Center
                )
            }
        }
    }

    /*
    Her kommer informasjon om Havfall appen:
    - Intro til hvorfor man burde holde havet rent
    - Info værmetrikker for optimale ryddedager
    - Info om ulike søppel-kategorier
     */
    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = {
                openDialog.value = false
            },
            containerColor = Color(LIGHT_BLUE_CARD_BACKGROUND),
            text = {
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                        .height(450.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Column {
                        Row {
                            Column {
                                Spacer(modifier = Modifier.height(12.dp))
                                Image(
                                    painter = painterResource(id = R.drawable.havfall_moerklogoutennavn),
                                    contentDescription = "Vind",
                                    modifier = Modifier.size(width = 35.dp, height = 35.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = " Bak Havfall",
                                    textAlign = TextAlign.End,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(BLUE_BACKGROUND_COLOR),
                                    style = MaterialTheme.typography.headlineLarge

                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Rent hav er avgjørende for planeten vår. Ved å holde havet rent, beskytter vi livet i det, sikrer matkilder og bevarer klimaets stabilitet.",
                            fontWeight = FontWeight.Normal,
                            style = MaterialTheme.typography.titleMedium,
                            color = Color(BLUE_BACKGROUND_COLOR)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Hva er en optimal ryddedag?",
                            fontWeight = FontWeight.SemiBold,
                            style = MaterialTheme.typography.headlineSmall,
                            lineHeight = 25.sp,
                            color = Color(BLUE_BACKGROUND_COLOR)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(id = R.drawable.clearsky_day),
                                contentDescription = "Sol",
                                modifier = Modifier.size(width = 35.dp, height = 35.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Temperatur",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 18.sp,
                                    color = Color(BLUE_BACKGROUND_COLOR)
                                )
                                Text(
                                    text = "Vi har valgt et temperaturområde på 0.0-25.0 grader Celsius for å sikre en komfortabel temperatur for deg som rydder.",
                                    color = Color(BLUE_BACKGROUND_COLOR),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(id = R.drawable.vind),
                                contentDescription = "Vind",
                                modifier = Modifier.size(width = 35.dp, height = 35.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Vind og vindkast",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 18.sp,
                                    color = Color(BLUE_BACKGROUND_COLOR)
                                )
                                Text(
                                    text = "Vindhastighetsområde er satt på 0.0-7.9 m/s for å sikre at det er tilstrekkelig lite vind til å gjennomføre ryddearbeidet effektivt.",
                                    color = Color(BLUE_BACKGROUND_COLOR),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(id = R.drawable.regn1),
                                contentDescription = "Nedbør",
                                modifier = Modifier.size(width = 35.dp, height = 35.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Nedbør",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 18.sp,
                                    color = Color(BLUE_BACKGROUND_COLOR)
                                )
                                Text(
                                    text = "Nedbørsområde er satt til 0.0-0.4 mm/t for å sikre at nedbøren ikke hindrer ryddeaktivitetene.",
                                    color = Color(BLUE_BACKGROUND_COLOR),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(id = R.drawable.vann2),
                                contentDescription = "Lavvann og høyvann",
                                modifier = Modifier.size(width = 35.dp, height = 35.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Lavvann og høyvann",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 18.sp,
                                    color = Color(BLUE_BACKGROUND_COLOR)
                                )
                                Text(
                                    text = "Et havnivå som ligger mellom lavvann og median vil sikre at strandområdene som skal ryddes er tilgjengelige og ikke er for oversvømte eller uframkommelige.",
                                    color = Color(BLUE_BACKGROUND_COLOR),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(26.dp))
                        Text(
                            text = "Kategorier av Havfall",
                            fontWeight = FontWeight.SemiBold,
                            style = MaterialTheme.typography.titleLarge,
                            //fontSize = 22.sp,
                            color = Color(BLUE_BACKGROUND_COLOR)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Vi har nøye utvalgt 4 kategorier for ryddeappen vår basert på statistikk som viser de vanligste typene avfall langs kysten og i havet.",
                            color = Color(BLUE_BACKGROUND_COLOR),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(id = R.drawable.flaske_ikon),
                                contentDescription = "Plast",
                                modifier = Modifier.size(width = 35.dp, height = 35.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Plast",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 18.sp,
                                    color = Color(BLUE_BACKGROUND_COLOR)
                                )
                                Text(
                                    text = "Plast utgjør en stor trussel mot havmiljøet, og det er en av de mest utbredte formene for avfall som finnes i havet.",
                                    color = Color(BLUE_BACKGROUND_COLOR),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(id = R.drawable.krok_ikon),
                                contentDescription = "krok",
                                modifier = Modifier
                                    .size(width = 35.dp, height = 35.dp)
                                    .offset(y = (-3).dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Fiskeutstyr",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 18.sp,
                                    color = Color(BLUE_BACKGROUND_COLOR)
                                )
                                Text(
                                    text = "Fiskeutstyr har en betydelig innvirkning på marint liv og utgjør en betydelig kilde til forurensning.",
                                    color = Color(BLUE_BACKGROUND_COLOR),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(id = R.drawable.sigarett_ikon),
                                contentDescription = "Sigarett",
                                modifier = Modifier
                                    .size(width = 35.dp, height = 35.dp)
                                    .offset(y = (-50).dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Tobakk",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 18.sp,
                                    color = Color(BLUE_BACKGROUND_COLOR)
                                )
                                Text(
                                    text = "Sigaretter og snus er også svært vanlige typer avfall som finnes på stranden. Disse produktene inneholder giftige kjemikalier og kan forårsake alvorlige skader på både menneskers helse og naturmiljøet.",
                                    color = Color(BLUE_BACKGROUND_COLOR),
                                    style = MaterialTheme.typography.bodyMedium
                                )

                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(id = R.drawable.boss_ikon),
                                contentDescription = "Boss",
                                modifier = Modifier
                                    .size(width = 35.dp, height = 35.dp)
                                    .offset(y = (-19).dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Annet",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 18.sp,
                                    color = Color(BLUE_BACKGROUND_COLOR)
                                )
                                Text(
                                    text = "\"Annet\" kategorien gir deg muligheten til å rapportere andre typer avfall som du finner på stranden som ikke faller innenfor de øvrige kategoriene.",
                                    color = Color(BLUE_BACKGROUND_COLOR),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Box(
                    modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center
                )
                {
                    Button(
                        onClick = { openDialog.value = false },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(
                                BLUE_BUTTON_COLOR
                            )
                        )
                    ) {
                        Text("Lukk", color = Color(WHITEISH_COLOR))
                    }
                }
            }
        )
    }
}


/******************
 *  Funfact-boks  *
 ******************/
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FunFactCard(homeViewModel: HomeViewModel) {

    val randomFunfact = homeViewModel.getRandomFunfact()

    Column(
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Dagens funfact",
            style = MaterialTheme.typography.titleMedium,
            color = Color(WHITEISH_COLOR),
            textAlign = TextAlign.Start,
            modifier = Modifier.padding(0.dp)
                    then (Modifier.padding(horizontal = 13.5.dp))
        )

        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color(BLUE_BUTTON_COLOR).copy(alpha = 0.7f)
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 6.dp
            ),
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth()
                .height(150.dp),
            shape = RoundedCornerShape(20.dp)
        ) {
            Box(
                modifier = Modifier.padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Column {
                    Text(
                        text = randomFunfact.title,
                        fontWeight = FontWeight.Bold,
                        fontFamily = sfFontFamily,
                        fontSize = 16.sp,
                        color = Color(WHITEISH_COLOR)
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = randomFunfact.text,
                        fontWeight = FontWeight.Normal,
                        fontFamily = sfFontFamily,
                        fontSize = 14.sp,
                        color = Color(WHITEISH_COLOR),
                        overflow = TextOverflow.Visible
                    )
                }
            }
        }
    }
}