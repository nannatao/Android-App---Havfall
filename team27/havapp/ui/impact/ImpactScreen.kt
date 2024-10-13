package no.uio.ifi.in2000.team27.havapp.ui.impact

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import no.uio.ifi.in2000.team27.havapp.model.utilities.Constants.Companion.BLUE_BACKGROUND_COLOR
import no.uio.ifi.in2000.team27.havapp.model.utilities.Constants.Companion.BLUE_BUTTON_COLOR
import no.uio.ifi.in2000.team27.havapp.model.utilities.Constants.Companion.LIGHT_BLUE_CARD_BACKGROUND
import no.uio.ifi.in2000.team27.havapp.model.utilities.Constants.Companion.WHITEISH_COLOR
import no.uio.ifi.in2000.team27.havapp.ui.sfFontFamily

/*******************************
 *        ImpactScreen         *
 *******************************/
// Denne skjermen viser brukerens påvirkning og funfacts om havet
@Composable
@SuppressLint(
    "UnusedMaterial3ScaffoldPaddingParameter", "UnusedBoxWithConstraintsScope",
    "StateFlowValueCalledInComposition"
)
fun ImpactScreen(
    impactViewModel: ImpactViewModel = viewModel(),
    navController: NavController //brukes ikke, men skal tas inn for navigasjonen
) {

    val impactUiState by impactViewModel.impactUiState.collectAsState()

    // Holder state'en til snackbar
    val snackbarHostState = SnackbarHostState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color(BLUE_BACKGROUND_COLOR)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .background(Color(BLUE_BACKGROUND_COLOR))
                .fillMaxWidth()
                .verticalScroll(
                    rememberScrollState()
                )
        ) {
            ImpactScreenContent(impactUiState = impactUiState)
        }
    }
}

/*******************************
 *        Screen Content       *
 *******************************/
@Composable
fun ImpactScreenContent(impactUiState: ImpactUiState) {
    ImpactOverview(impactUiState)
    Text(
        text = "For meg",
        Modifier
            .padding(16.dp)
            .padding(start = 20.dp),
        fontFamily = sfFontFamily,
        style = MaterialTheme.typography.headlineSmall,
        textAlign = TextAlign.Start,
        color = Color.White
    )
    Spacer(modifier = Modifier.height(10.dp))
    SwipeableCards()
}

/*******************************
 * Øverste del av ImpactScreen *
 *******************************/
@Composable
fun ImpactOverview(impactUiState: ImpactUiState) {
    /* Holder på søppelsekk og progress bar */
    Column(
        modifier = Modifier
            .padding(16.dp)
            .padding(top = 50.dp, start = 20.dp)
            .fillMaxWidth()
            .background(Color(BLUE_BACKGROUND_COLOR)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start
    ) {
        Row {
            Text(
                text = "Min påvirkning",
                fontFamily = sfFontFamily,
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Start,
                color = Color(WHITEISH_COLOR)
            )
        }
        Row {
            Text(
                text = "Gjennomfør en ryddeaksjon for å se søppelsekken fylles! Klarer du 10?",
                fontFamily = sfFontFamily,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Start,
                color = Color(WHITEISH_COLOR)
            )
        }
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .fillMaxHeight()
                .background(Color(BLUE_BACKGROUND_COLOR))
        ) {
            AsyncImage(
                model = impactUiState.trashBagImageResource,
                contentDescription = "Loading...",
                modifier = Modifier
                    .size(200.dp)
                    .wrapContentWidth(Alignment.CenterHorizontally)
            )
            ProgressBar(progress = impactUiState.progress)
        }
    }
}


// En bar som viser brukerens progresjon (antall ryddinger gjennomført)
@Composable
fun ProgressBar(progress: Float) {
    LinearProgressIndicator(
        modifier = Modifier
            .absoluteOffset(x = 0.dp, y = 160.dp)
            .height(20.dp) // bredde
            .graphicsLayer {
                rotationZ = 270f // setter den sidelengs
                transformOrigin = TransformOrigin(0f, 0f)
            },
        progress = progress,
        color = Color(BLUE_BUTTON_COLOR),       // Fyll
        trackColor = Color(LIGHT_BLUE_CARD_BACKGROUND), // Bakgrunn
        strokeCap = StrokeCap.Round
    )
}