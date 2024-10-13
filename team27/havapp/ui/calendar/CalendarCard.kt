package no.uio.ifi.in2000.team27.havapp.ui.calendar

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import no.uio.ifi.in2000.team27.havapp.R
import no.uio.ifi.in2000.team27.havapp.model.calendar.DateInfo
import no.uio.ifi.in2000.team27.havapp.model.utilities.Constants.Companion.BLUE_BACKGROUND_COLOR
import no.uio.ifi.in2000.team27.havapp.model.utilities.Constants.Companion.BLUE_BUTTON_COLOR
import no.uio.ifi.in2000.team27.havapp.model.utilities.Constants.Companion.LIGHT_BLUE_CARD_BACKGROUND
import no.uio.ifi.in2000.team27.havapp.model.utilities.Constants.Companion.WHITEISH_COLOR
import no.uio.ifi.in2000.team27.havapp.ui.sfFontFamily
import java.text.DateFormatSymbols
import java.time.LocalTime


@RequiresApi(Build.VERSION_CODES.O)
@Composable
// Overskrift til kalender
fun Header(antDager: Int) {
    Row {
        Text(
            text = "De $antDager neste optimale ryddedagene",
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically)
                .padding(horizontal = 30.dp),
            color = Color(WHITEISH_COLOR),
            style = MaterialTheme.typography.titleMedium
        )
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DateCard(date: DateInfo) {
/** Et kort som holder på datoinfo og værsymbol
 * Kortet er clickable, og ved et klikk kommer det mer detaljert værinformasjon
 * @param date DatoKort
 */
    val openDialog = remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .padding(vertical = 8.dp, horizontal = 4.dp)
            .height(150.dp)
            .width(75.dp)
            .clickable { openDialog.value = true },
        colors = CardDefaults.cardColors(
            containerColor = Color(WHITEISH_COLOR).copy(alpha = 0.7f)
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                //.height(170.dp)
                .padding(4.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            /*
            Værsymbolkode:
            Viser værsymbol for kl.12.00 hver dag
             */
            var weatherSymbolId = R.drawable.partlycloudy_day
            var lastKnownWeatherDescription: String? = null

            date.weather?.forEach { weatherMap ->
                val isNoonTimeSlot = weatherMap?.get("klokkeslett")?.toString() == "12:00"

                if (isNoonTimeSlot) {
                    val weatherDescription =
                        weatherMap?.get("værbeskrivelse")?.toString() ?: lastKnownWeatherDescription

                    if (weatherDescription != null) {
                        weatherSymbolId = when (weatherDescription) {
                            "clearsky_day" -> R.drawable.clearsky_day
                            "clearsky_night" -> R.drawable.clearsky_night
                            "fair_day" -> R.drawable.fair_day
                            "fair_night" -> R.drawable.fair_night
                            "partlycloudy_day" -> R.drawable.partlycloudy_day
                            "partlycloudy_night" -> R.drawable.partlycloudy_night
                            "cloudy" -> R.drawable.cloudy
                            else -> R.drawable.partlycloudy_day
                        }
                        lastKnownWeatherDescription = weatherDescription
                    }
                }
            }

            //værsymbol
            Image(
                modifier = Modifier.size(100.dp)
                        then (Modifier.padding(start = 13.5.dp)),

                painter = painterResource(id = weatherSymbolId),
                contentDescription = "Weather Symbol",
            )

            // maaned
            val monthNames = DateFormatSymbols.getInstance(java.util.Locale("no", "NO")).shortMonths
            val month = monthNames[date.dato.dato.monthValue - 1].uppercase()

            Column(
                horizontalAlignment = Alignment.CenterHorizontally

            ) {
                // dato
                Text(
                    text = date.dato.dato.dayOfMonth.toString(),
                    textAlign = TextAlign.Center,
                    style = TextStyle(
                        color = Color(BLUE_BACKGROUND_COLOR),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))

                // måned
                Text(
                    text = month,
                    style = TextStyle(
                        color = Color(BLUE_BACKGROUND_COLOR),
                        fontSize = 16.sp
                    )
                )
            }

            if (openDialog.value) {
                AlertDialog(
                    onDismissRequest = {
                        openDialog.value = false
                    },
                    containerColor = Color(LIGHT_BLUE_CARD_BACKGROUND),
                    title = {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp),
                            verticalArrangement = Arrangement.Top,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(
                                modifier = Modifier.size(100.dp)
                                        then (Modifier.padding(start = 13.5.dp)),

                                painter = painterResource(id = weatherSymbolId),
                                contentDescription = "Weather Symbol",
                            )
                            // Informasjon om optimal ryddedag
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    //.height(170.dp)
                                    .padding(4.dp),
                                verticalArrangement = Arrangement.Top,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Optimal ryddedag:",
                                    fontWeight = FontWeight.Bold,
                                    color = Color(BLUE_BACKGROUND_COLOR)
                                )
                                Text(
                                    text = "${date.dato.dato.dayOfMonth}. $month",
                                    fontWeight = FontWeight.Bold,
                                    color = Color(BLUE_BACKGROUND_COLOR)
                                )

                            }
                        }
                    },
                    text = {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentSize(Alignment.Center)
                        ) {
                            Text(
                                modifier = Modifier
                                    .padding(
                                        start = 25.dp,
                                        end = 16.dp,
                                        top = 20.dp,
                                        bottom = 8.dp
                                    ),
                                text = "Værforholdene: ",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(BLUE_BACKGROUND_COLOR)
                            )
                            Spacer(Modifier.width(10.dp))

                            date.weather?.forEach { weatherMap ->
                                weatherMap?.forEach { entry ->
                                    if (entry.key == "klokkeslett") {
                                        val time = LocalTime.parse(entry.value.toString())

                                        if (time == LocalTime.parse("12:00")) {
                                            Column(modifier = Modifier.padding(start = 16.dp)) {
                                                weatherMap.filter { it.key != "klokkeslett" && it.key != "værbeskrivelse" }
                                                    .forEach { (key, value) ->
                                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                                            Spacer(Modifier.width(8.dp)) // Add spacing between key-value pairs
                                                            var painter =
                                                                painterResource(id = R.drawable.lette_regnbyger_natt_1)
                                                            var measurementUnit = ""


                                                            when (key) {
                                                                "temperatur" -> {
                                                                    painter =
                                                                        painterResource(id = R.drawable.clearsky_day)
                                                                    measurementUnit = "°"
                                                                }

                                                                "vind" -> {
                                                                    painter =
                                                                        painterResource(id = R.drawable.vind)
                                                                    measurementUnit = " m/s"
                                                                }

                                                                "vindkast" -> {
                                                                    painter =
                                                                        painterResource(id = R.drawable.vind)
                                                                    measurementUnit = " m/s"
                                                                }

                                                                "nedbør" -> {
                                                                    painter =
                                                                        painterResource(id = R.drawable.regn1)
                                                                    measurementUnit = " mm/t"
                                                                }
                                                            }
                                                            Image(
                                                                painter = painter,
                                                                contentDescription = "Weather Icon",
                                                                modifier = Modifier.size(24.dp)
                                                            )
                                                            Spacer(Modifier.width(16.dp))
                                                            Text(
                                                                "$key: $value$measurementUnit",
                                                                fontSize = 18.sp,
                                                                color = Color(BLUE_BACKGROUND_COLOR)
                                                            )
                                                        }
                                                    }
                                            }
                                        }
                                    }
                                }
                            }

                            //tidevann-informasjon
                            Spacer(Modifier.width(50.dp))
                            Text(
                                modifier = Modifier.padding(
                                    start = 22.dp,
                                    end = 16.dp,
                                    top = 28.dp,
                                    bottom = 11.dp
                                ),
                                text = "Tidspunkt med lavvann: ",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.Center,
                                color = Color(BLUE_BACKGROUND_COLOR)
                            )

                            LazyColumn(
                                contentPadding = PaddingValues(
                                    top = 10.dp,
                                    start = 10.dp,
                                    end = 0.dp,
                                    bottom = 16.dp
                                )
                            ) {
                                items(date.lavvann?.chunked(3) ?: emptyList()) { rowItems ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(bottom = 15.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        rowItems.forEach { tideInfo ->
                                            TideInfoItem(tideInfo)
                                            Spacer(Modifier.width(0.dp))
                                            Spacer(Modifier.height(15.dp))
                                        }

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
    }
}


// Inneholder informasjon om tidevann
@Composable
fun TideInfoItem(tideInfo: String) {
    Box(
        modifier = Modifier
            .width(75.dp)
            .height(50.dp)
            .padding(start = 15.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color(WHITEISH_COLOR).copy(alpha = 0.6f)),
        contentAlignment = Alignment.Center
    ) {


        Text(
            tideInfo,
            fontSize = 18.sp,
            modifier = Modifier, textAlign = TextAlign.Center,
            color = Color(BLUE_BACKGROUND_COLOR)
        )
    }

}

// Tomt datokort (dersom det ikke er fire optimale ryddedager, fylles den tomme plassen)
@Composable
fun EmptyCard() {
    Card(
        modifier = Modifier
            .padding(vertical = 8.dp, horizontal = 4.dp)
            .height(150.dp)
            .width(75.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(WHITEISH_COLOR).copy(alpha = 0.7f)
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Tomt kort
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Content(
    lat: Double,
    lon: Double,
    view: CalendarViewModel,
    needsLoading: Boolean
) {
    /** Viser opptil 4 datoer som er perfekte ryddedager
    * @param lat Double
    * @param lon Double
    * @param view CalendarViewModel
     **/
    val kalUI by view.kalenderState.collectAsState()

    if (needsLoading) {
            LaunchedEffect(key1 = lat, key2 = lon) {
            view.initialiser(lat, lon)
        }
    }
    val antOptimaleDager = kalUI.perfektDagKort.filter { it.perfektRyddedag }.take(4)

    Header(antOptimaleDager.size)

    Card( // et ytre kort som inneholder alle datokortene
        colors = CardDefaults.cardColors(
            containerColor = Color(BLUE_BACKGROUND_COLOR)
        ),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.padding(8.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {


            val perfectDays = kalUI.perfektDagKort.filter { it.perfektRyddedag }.take(4)
            val numberOfEmptyCards = 4 - perfectDays.size

            // Viser alle optimale ryddedager
            perfectDays.forEach { date ->
                DateCard(date)
            }

            // Fyller opp resterende plass med tomme kort
            repeat(numberOfEmptyCards) {
                EmptyCard()
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
/** en rad med kort med optimale ryddedager
* @param lat: Double
* @param lon: Double
 * @param calendarViewModel: CalendarViewModel
**/
fun PerfectDaysCard(lat: Double, lon: Double, calendarViewModel: CalendarViewModel) {

    val calendarState by calendarViewModel.kalenderState.collectAsState()
    var isLoading by remember { mutableStateOf(true) }
    LaunchedEffect(key1 = calendarState.perfektDagKort) {
        if (calendarState.perfektDagKort.isNotEmpty() && calendarState.hasLoaded) {
            delay(7000)
            isLoading = false
        }
    }

    if (isLoading) {
        Row {
            Text(
                text = "Finner optimale ryddedager...",
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically)
                    .padding(horizontal = 30.dp),
                color = Color(WHITEISH_COLOR),
                fontWeight = FontWeight.Normal,
                fontFamily = sfFontFamily,
                fontSize = 16.sp
            )
        }
    }

    Column(verticalArrangement = Arrangement.Center) {
        Spacer(modifier = Modifier.padding(5.dp))
        ShimmeringRowItem(
            isLoading = isLoading,
            contentAfterLoading = {
                Content(lat, lon, calendarViewModel, needsLoading = false)
            },
        )
    }

    // Dette er en hack for å laste inn innholdet i Content composablen.
    // Den er usynlig. Dette er dårlig praksis, men det fungerer
    Column(
        modifier = Modifier.alpha(0f).size(0.dp)
    ) {
        Content(lat, lon, calendarViewModel, needsLoading = true)
    }
}



/************
 * Loading  *
 ************/
/*
 * Loading animasjon for datokortene mens de lastes inn
 */
@Composable
fun ShimmeringRowItem(
    isLoading: Boolean,
    contentAfterLoading: @Composable () -> Unit,
) {
    if (isLoading) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            repeat(4) {
                Card(
                    modifier = Modifier
                        .padding(vertical = 8.dp, horizontal = 4.dp)
                        .height(150.dp)
                        .width(75.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(WHITEISH_COLOR).copy(alpha = 0.7f)
                    ),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize().shimmerEffect()) {
                        // Tomt kort
                    }
                }
            }
        }
    } else {
        contentAfterLoading()
    }
}


@SuppressLint("ModifierFactoryUnreferencedReceiver")
fun Modifier.shimmerEffect(): Modifier = composed {
    var size by remember {
        mutableStateOf(IntSize.Zero)
    }
    val transition = rememberInfiniteTransition(label = "shimmerEffect")
    val startOffsetX by transition.animateFloat(
        label = "",
        initialValue = -2 * size.width.toFloat(),
        targetValue = 2 * size.width.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1000,
            ),
            repeatMode = RepeatMode.Reverse
        )
    )

    background(
        brush = Brush.linearGradient(
            colors = listOf(
                Color(WHITEISH_COLOR).copy(alpha = 0.6f),
                Color(WHITEISH_COLOR).copy(alpha = 0.8f),
                Color(LIGHT_BLUE_CARD_BACKGROUND),
                Color(WHITEISH_COLOR).copy(alpha = 0.8f),
                Color(WHITEISH_COLOR).copy(alpha = 0.6f)
            ),
            // Diagonalt fra venstre til høyre
            start = Offset(startOffsetX, 0f),
            end = Offset(startOffsetX + size.width, size.height.toFloat())
        )
    )

        .onGloballyPositioned {
            size = it.size
        }
}



