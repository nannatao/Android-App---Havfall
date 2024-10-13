package no.uio.ifi.in2000.team27.havapp.ui.navbar

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import no.uio.ifi.in2000.team27.havapp.R
import no.uio.ifi.in2000.team27.havapp.model.utilities.Constants.Companion.BLUE_BACKGROUND_COLOR
import no.uio.ifi.in2000.team27.havapp.model.utilities.Constants.Companion.BLUE_BUTTON_COLOR
import no.uio.ifi.in2000.team27.havapp.model.utilities.Constants.Companion.WHITEISH_COLOR
import no.uio.ifi.in2000.team27.havapp.ui.cleaningActivity.CleaningActivityViewModel
import no.uio.ifi.in2000.team27.havapp.ui.sfFontFamily

@Composable
private fun BottomBar(modifier: Modifier = Modifier, onNavigationItemSelected: (Int) -> Unit) {
    /**
     * Denne funksjonen lager navigasjonsbaren som vises nederst på skjermen.
     * Den inneholder ikke start-rydde knappen og skal heller ikke kalles på direkte.
     * Bruk heller [NavBarWithCleaningButton] på alle skjermer som skal ha navigasjonsbar.
     */
    val bottomBarBackgroundColor = Color(WHITEISH_COLOR)
    val iconColor = Color(BLUE_BACKGROUND_COLOR)

    val spacing = 15.dp // Mellomrom mellom knappene

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
            .background(bottomBarBackgroundColor)
            .padding(vertical = 15.dp, horizontal = 15.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Knappene i navigasjonsbaren
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { onNavigationItemSelected(0) }) {
                Icon(
                    painter = painterResource(id = R.drawable.homeicon),
                    contentDescription = "Hjem",
                    tint = iconColor,
                    modifier = Modifier
                        .size(30.dp)
                )
            }

            Spacer(modifier = Modifier.width(spacing))

            IconButton(onClick = { onNavigationItemSelected(1) }) {
                Icon(
                    painter = painterResource(id = R.drawable.impacticon),
                    contentDescription = "Din Påvirkning",
                    tint = iconColor,
                    modifier = Modifier.size(30.dp)
                )
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { onNavigationItemSelected(2) }) {
                Icon(
                    painter = painterResource(id = R.drawable.mapicon),
                    contentDescription = "Kart",
                    tint = iconColor,
                    modifier = Modifier.size(30.dp)
                )
            }
            Spacer(modifier = Modifier.width(spacing))
            IconButton(onClick = { onNavigationItemSelected(3) }) {
                Icon(
                    painter = painterResource(id = R.drawable.profileicon),
                    contentDescription = "Profil",
                    tint = iconColor,
                    modifier = Modifier.size(30.dp)
                )
            }
        }
    }
}


@SuppressLint("RestrictedApi")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavBarWithCleaningButton(
    navController: NavController,
    selectIconBorderColor: Color,
    cleaningActivityViewModel: CleaningActivityViewModel
) {
    /**
     * Denne funksjonen lager navigasjonsbaren som vises nederst på skjermen.
     * Den inneholder start-rydde knappen og skal brukes på alle skjermer som skal ha navigasjonsbar.
     */
    val iconColor = Color(BLUE_BUTTON_COLOR)

    val timerState = cleaningActivityViewModel.timerState.collectAsState()


    Box {
        BottomBar(modifier = Modifier.align(Alignment.BottomCenter),
            onNavigationItemSelected = { index ->
                when (index) {
                    0 -> if (navController.currentDestination != navController.findDestination(route = "homeScreen")) {
                        run { navController.navigate("homeScreen") }
                    }

                    1 -> if (navController.currentDestination != navController.findDestination(route = "impactScreen")) {
                        run { navController.navigate("impactScreen") }
                    }

                    2 -> if (navController.currentDestination != navController.findDestination(route = "polygonMapScreen")) {
                        run { navController.navigate("polygonMapScreen") }
                    }

                    3 -> if (navController.currentDestination != navController.findDestination(route = "userProfileScreen")) {
                        run { navController.navigate("userProfileScreen") }
                    }
                }
            })
        IconButton(
            onClick = {
                timerState.value.hasStartedActivity = true
                navController.navigate("cleaningActivityScreen")
            },
            modifier = Modifier
                .padding(bottom = 30.dp)
                .border(
                    width = 8.dp,
                    color = selectIconBorderColor,
                    shape = CircleShape
                )
                .clip(CircleShape)
                .background(iconColor)
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.startcleaningicon),
                contentDescription = "Start",
                tint = Color.White,
                modifier = Modifier.size(50.dp)
            )
        }
        Text(
            text = "Start",
            fontFamily = sfFontFamily,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Medium,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 5.dp)
        )
    }
}