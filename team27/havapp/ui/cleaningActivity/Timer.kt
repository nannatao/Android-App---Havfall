package no.uio.ifi.in2000.team27.havapp.ui.cleaningActivity

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import no.uio.ifi.in2000.team27.havapp.R
import no.uio.ifi.in2000.team27.havapp.model.cleaning.TrashType
import no.uio.ifi.in2000.team27.havapp.model.cleaning.stringToTrashType
import no.uio.ifi.in2000.team27.havapp.model.utilities.Constants.Companion.BLUE_BACKGROUND_COLOR
import no.uio.ifi.in2000.team27.havapp.model.utilities.Constants.Companion.BLUE_BUTTON_COLOR
import no.uio.ifi.in2000.team27.havapp.model.utilities.Constants.Companion.LIGHT_BLUE_CARD_BACKGROUND
import no.uio.ifi.in2000.team27.havapp.model.utilities.Constants.Companion.RED_ALERT_COLOR
import no.uio.ifi.in2000.team27.havapp.model.utilities.Constants.Companion.WHITEISH_COLOR
import no.uio.ifi.in2000.team27.havapp.ui.sfFontFamily
import java.util.concurrent.TimeUnit

/*
På CleaningActivityScreen har vi en egen navigasjonsbar som kun viser en stoppknapp.
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CleaningActivityNavBar(
    navController: NavController,
    cleaningActivityViewModel: CleaningActivityViewModel = viewModel()
) {
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        IconButton(
            onClick = {
                cleaningActivityViewModel.stopTimer()
            },
            modifier = Modifier
                .padding(bottom = 30.dp)
                .border(
                    width = 8.dp,
                    color = Color(WHITEISH_COLOR),
                    shape = CircleShape
                )
                .clip(CircleShape)
                .background(Color(RED_ALERT_COLOR))
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.stopicon),
                contentDescription = "Stop",
                tint = Color(WHITEISH_COLOR),
                modifier = Modifier.size(50.dp)
            )
        }

        Timer(
            navController = navController,
            cleaningActivityViewModel = cleaningActivityViewModel,
        )

    }
}

/*
Navbaren er knyttet til timer (for å stoppe og starte timeren).
Timer starer så fort brukeren kommer inn på CleaningActivityScreen og teller timer, minutter og sekunder.
 */
@SuppressLint("StateFlowValueCalledInComposition")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Timer(
    cleaningActivityViewModel: CleaningActivityViewModel = viewModel(),
    navController: NavController,
) {
    val timerState by cleaningActivityViewModel.timerState.collectAsState()

    val stopDialogState = timerState.showStopDialog
    val resultDialogState = timerState.showResultDialog

    val trashTypeImages = mapOf(
        TrashType.PLAST to R.drawable.flaske_ikon,
        TrashType.FISKEUTSTYR to R.drawable.krok_ikon,
        TrashType.SIGARETTER to R.drawable.sigarett_ikon,
        TrashType.ANNET to R.drawable.boss_ikon
    )


    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.Top
        ) {

            // Vis den formatterte tiden (00:00:00)
            Text(
                text = formatTime(timerState.time),
                fontFamily = sfFontFamily,
                fontWeight = FontWeight.Medium,
                color = Color(WHITEISH_COLOR),
                modifier = Modifier
                    .padding(bottom = 5.dp, top = 5.dp)
            )
        }
        /*
        Dialog som kommer opp når bruker trykker på stoppknappen - bruker får valget mellom å fortsette
        eller å avslutte.
         */
        if (stopDialogState) {
            AlertDialog(
                onDismissRequest = {
                    cleaningActivityViewModel.hideStopDialog()
                },
                containerColor = Color(LIGHT_BLUE_CARD_BACKGROUND),
                text = {
                    Text(
                        text = "Er du sikker på at du vil avslutte rydding?",
                        textAlign = TextAlign.Center,
                        style = TextStyle(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 22.sp,
                            letterSpacing = 0.5.sp
                        )
                    )
                },
                dismissButton = {
                    // FORTSETT-knapp
                    Button(
                        onClick = {
                            cleaningActivityViewModel.hideStopDialog()
                            cleaningActivityViewModel.startTimer()
                        },
                        modifier = Modifier
                            .height(48.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(WHITEISH_COLOR)),
                    ) {
                        Text("FORTSETT", color = Color(BLUE_BACKGROUND_COLOR))
                    }
                },
                confirmButton = {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        //AVSLUTT-knapp
                        Button(
                            onClick = {
                                cleaningActivityViewModel.hideStopDialog()
                                cleaningActivityViewModel.showResultDialog()
                            },
                            modifier = Modifier
                                .padding(start = 36.dp)
                                .height(48.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(RED_ALERT_COLOR)),
                        ) {
                            Text("AVSLUTT", color = Color(WHITEISH_COLOR))
                        }
                    }
                }
            )
        }
        /*
        Denne dialogen kommmer opp dersom brukeren trykket på "AVSLUTT".
        Viser hvor mye brukeren har ryddet av hver TrashType (søppeltype) og hvor lang tid h*n har brukt.
         */
        if (resultDialogState) {
            val cleaningActivity = cleaningActivityViewModel.cleaningActivityUiState.value.cleaningActivity
            AlertDialog(
                onDismissRequest = {
                    cleaningActivityViewModel.hideResultDialog()
                },
                containerColor = Color(LIGHT_BLUE_CARD_BACKGROUND),

                title = {
                    Text(
                        text = "Ryddeoppsummering",
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        color = Color(BLUE_BACKGROUND_COLOR),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 25.sp
                    )
                },
                text = {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        cleaningActivity.trash.forEach { (trashType, count) ->
                            Spacer(modifier = Modifier.height(25.dp))
                            Box(
                                contentAlignment = Alignment.CenterStart,
                                modifier = Modifier
                                    .padding(vertical = 8.dp)
                                    .padding(start = 60.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth(),
                                ) {
                                    Image(
                                        painter = painterResource(id = trashTypeImages.getValue(stringToTrashType(trashType))),
                                        contentDescription = "TrashType Image",
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Column(
                                        modifier = Modifier.padding(start = 16.dp),
                                    ) {

                                        Text(
                                            text = count.toString(),
                                            style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 30.sp),
                                            textAlign = TextAlign.Start,
                                            color = Color(BLUE_BACKGROUND_COLOR)
                                        )
                                        Text(
                                            text = trashTypeToString(stringToTrashType(trashType)),
                                            style = TextStyle(fontWeight = FontWeight.Normal, fontSize = 20.sp),
                                            textAlign = TextAlign.Start,
                                            color = Color(BLUE_BACKGROUND_COLOR)
                                        )

                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(30.dp))
                        Text(
                            text = "Ryddetid: " + time2Text(timeMi = timerState.time),
                            style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 20.sp),
                            modifier = Modifier.padding(vertical = 8.dp),
                            color = Color(BLUE_BACKGROUND_COLOR)
                        )
                        Spacer(modifier = Modifier.height(25.dp))
                    }
                },
                confirmButton = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Button(
                            onClick = {
                                cleaningActivityViewModel.hideResultDialog()
                                navController.navigate("homeScreen")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(BLUE_BUTTON_COLOR))
                        ) {
                            Text("Lukk")
                        }
                    }
                }
            )
        }
    }
}

/*
Konverter fra Trashtype til String-format.
 */
fun trashTypeToString(trashType: TrashType): String {
    return when (trashType) {
        TrashType.FISKEUTSTYR -> "Fiskeutstyr"
        TrashType.PLAST -> "Plast"
        TrashType.SIGARETTER -> "Sigaretter"
        TrashType.ANNET -> "Annet"
    }
}

/*
Konverterer long til String-format (for tid).
 */
@Composable
fun time2Text(timeMi: Long): String {
    return formatTime(timeMi)
}


/*
Formatterer tiden.
 */
@Composable
fun formatTime(timeMi: Long): String {
    val hours = TimeUnit.MILLISECONDS.toHours(timeMi)
    val min = TimeUnit.MILLISECONDS.toMinutes(timeMi) % 60
    val sec = TimeUnit.MILLISECONDS.toSeconds(timeMi) % 60

    return String.format("%02d:%02d:%02d", hours, min, sec)
}