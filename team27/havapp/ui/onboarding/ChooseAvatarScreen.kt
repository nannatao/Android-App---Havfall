package no.uio.ifi.in2000.team27.havapp.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyHorizontalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import no.uio.ifi.in2000.team27.havapp.model.utilities.AvatarIdToIconMap
import no.uio.ifi.in2000.team27.havapp.model.utilities.Constants.Companion.DARK_BLUE_BACKGROUND_COLOR
import no.uio.ifi.in2000.team27.havapp.model.utilities.Constants.Companion.USER_PROFILE_BLUE_BACKGROUND_COLOR
import no.uio.ifi.in2000.team27.havapp.model.utilities.Constants.Companion.WHITEISH_COLOR
import no.uio.ifi.in2000.team27.havapp.ui.sfFontFamily

/************************
 *  ChooseAvatarScreen  *
 ************************/
@Composable
fun ChooseAvatarScreen(
    navController: NavController,
    onboardingViewModel: OnboardingViewModel
) {
    ChooseAvatarScreenContent(
        navController = navController,
        onboardingViewModel = onboardingViewModel
    )
}

/********************
 *  Screen Content  *
 ********************/
@Composable
fun ChooseAvatarScreenContent(
    navController: NavController,
    onboardingViewModel: OnboardingViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(USER_PROFILE_BLUE_BACKGROUND_COLOR)),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.size(16.dp))
        ChooseAvatar(navController = navController, onboardingViewModel = onboardingViewModel)
    }
}

/******************
 *  ChooseAvatar  *
 ******************/
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChooseAvatar(
    navController: NavController,
    onboardingViewModel: OnboardingViewModel
) {

    Column(
        modifier = Modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        var avatar by remember { mutableStateOf(1) }

        // Valgt avatar
        AsyncImage(
            model = AvatarIdToIconMap.mapping[avatar]!!,
            contentDescription = "avatar",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(170.dp)
                .clip(CircleShape)
        )

        // Content
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "Velg din rydder",
                fontSize = 18.sp,
                fontFamily = sfFontFamily,
                color = Color(WHITEISH_COLOR),
                modifier = Modifier.padding(top = 16.dp),
            )

            // Sett-i-gang knapp
            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(WHITEISH_COLOR)
                ),
                modifier = Modifier.padding(top = 16.dp),
                onClick = {
                    // hent brukernavn fra forrige skjerm
                    val username = onboardingViewModel.onboardingUiState.value.username
                    // lagre bruker i databasen
                    onboardingViewModel.insertUser(username, avatar)

                    // naviger til hjemskjermen
                    navController.navigate("homescreen")
                }
            ) {
                Text(text = "Sett i gang!", fontFamily = sfFontFamily, modifier = Modifier.padding(2.dp), color = Color(
                    DARK_BLUE_BACKGROUND_COLOR
                ))
            }

            // Liste med avatarer
            val itemsList = AvatarIdToIconMap.mapping.values.toList()
            LazyHorizontalStaggeredGrid(
                verticalArrangement = Arrangement.spacedBy((-100).dp),
                rows = StaggeredGridCells.Fixed(3),
                horizontalItemSpacing = 15.dp,
                modifier = Modifier.padding(start = 20.dp, end = 20.dp)
            ) {
                items(itemsList) { item ->
                    // et kort for hver avatar
                    Card(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .aspectRatio(1f),
                        colors = CardDefaults.cardColors(Color.Transparent),
                        onClick = {
                            avatar =
                                AvatarIdToIconMap.mapping.filterValues { it == item }.keys.first()
                        }
                    ) {
                        AsyncImage(
                            model = item,
                            contentDescription = "avatar",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .aspectRatio(1f)
                        )
                    }
                }
            }
        }
    }
}
