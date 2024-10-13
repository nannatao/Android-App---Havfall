package no.uio.ifi.in2000.team27.havapp.ui.cleaningActivity

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import no.uio.ifi.in2000.team27.havapp.R
import no.uio.ifi.in2000.team27.havapp.model.utilities.Constants.Companion.BLUE_BACKGROUND_COLOR
import no.uio.ifi.in2000.team27.havapp.ui.sfFontFamily

/************************************
 *      CleaningActivityScreen      *
 ************************************/
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CleaningActivityScreen(
    cleaningActivityViewModel: CleaningActivityViewModel,
    navController: NavController,
) {
    /*
    Setter i gang ryddeaktivitet.
     */
    LaunchedEffect(key1 = true) {
        cleaningActivityViewModel.fetchInitialData()
    }

    Scaffold(
        modifier = Modifier.background(Color(BLUE_BACKGROUND_COLOR)),

        //bottom-bar med stoppknapp
        bottomBar = {
            CleaningActivityNavBar(
                navController = navController,
                cleaningActivityViewModel = cleaningActivityViewModel
            )
        },
        containerColor = Color(BLUE_BACKGROUND_COLOR)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(BLUE_BACKGROUND_COLOR)),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {

            Column( modifier = Modifier
                .fillMaxSize()
            ){
                //overskrifter
                TitleAndDescription()

                //cards med 4 ulike söppel
                CleaningActivityScreenContent(cleaningActivityViewModel = cleaningActivityViewModel)
            }

        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CleaningActivityScreenContent(cleaningActivityViewModel: CleaningActivityViewModel) {
    /*
     Viser alle kortene med soppel og plus/minus knapper.
    */

    LazyVerticalGrid(
        columns = GridCells.Fixed(1),
        contentPadding = PaddingValues(horizontal = 5.dp, vertical = 5.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            CleaningCard(
                painter = painterResource(id = R.drawable.flaske_ikon),
                title = "Plast",
                cleaningActivityViewModel = cleaningActivityViewModel,
            )
        }
        item {
            CleaningCard(
                painter = painterResource(id = R.drawable.krok_ikon),
                title = "Fiskeutstyr",
                cleaningActivityViewModel = cleaningActivityViewModel,
                modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            CleaningCard(
                painter = painterResource(id = R.drawable.sigarett_ikon),
                title = "Sigaretter",
                cleaningActivityViewModel = cleaningActivityViewModel,
                modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            CleaningCard(
                painter = painterResource(id = R.drawable.boss_ikon),
                title = "Annet",
                cleaningActivityViewModel = cleaningActivityViewModel,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun TitleAndDescription() {
    /* Øvre del av Ryddeskjerm */

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
    ) {
        Row {
            Text(
                text = "Ryddelogg",
                fontFamily = sfFontFamily,
                style = MaterialTheme.typography.displaySmall,
                textAlign = TextAlign.Start,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 23.dp)
            )
        }
        Text(
            "Her kan du logge havfallet du plukker.",
            fontFamily = sfFontFamily,
            color = Color.White,
            fontSize = 18.sp,
            modifier = Modifier.padding(horizontal = 25.dp))
    }


}