package no.uio.ifi.in2000.team27.havapp.ui.onboarding

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import no.uio.ifi.in2000.team27.havapp.R
import no.uio.ifi.in2000.team27.havapp.model.utilities.Constants.Companion.USER_PROFILE_BLUE_BACKGROUND_COLOR
import no.uio.ifi.in2000.team27.havapp.model.utilities.Constants.Companion.WHITEISH_COLOR
import no.uio.ifi.in2000.team27.havapp.ui.sfFontFamily

/*******************
 *  WelcomeScreen  *
 *******************/
@Composable
fun WelcomeScreen(
    navController: NavController,
    onboardingViewModel: OnboardingViewModel
) {

    var username by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(USER_PROFILE_BLUE_BACKGROUND_COLOR))
    ) {
        Spacer(modifier = Modifier.padding(40.dp))
        // Logo
            AsyncImage(
                model = R.drawable.havfall_logo_hvit,
                contentDescription = "Logo",
                modifier = Modifier
                    .size(140.dp)
                    .align(Alignment.TopStart)
                    .padding(top = 10.dp)
                    .padding(start = 60.dp)
            )

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.padding(40.dp))
            // Tastaturkontroller
            val keyboardController = LocalSoftwareKeyboardController.current
            Text(
                text = "Velkommen til\nHavfall",
                style = MaterialTheme.typography.headlineMedium,
                fontFamily = sfFontFamily,
                color = Color(WHITEISH_COLOR),
                modifier = Modifier.padding(horizontal = 5.dp)
                    .padding(end = 120.dp),
                lineHeight = 50.sp
            )
            Spacer(modifier = Modifier.padding(40.dp))
            Text(
                text = "Hva heter du?",
                fontSize = 18.sp,
                modifier = Modifier.padding(horizontal = 10.dp)
                    .padding(end = 165.dp),
                fontFamily = sfFontFamily,
                color = Color.White
            )
            // Tekstfelt for brukernavn
            TextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("") },
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    // Farger for tekstfeltet
                    focusedTextColor = Color(WHITEISH_COLOR),
                    unfocusedTextColor = Color(WHITEISH_COLOR),
                    unfocusedContainerColor = Color.Transparent,
                    unfocusedIndicatorColor = Color(WHITEISH_COLOR),
                    focusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color(WHITEISH_COLOR),
                ),
                textStyle = TextStyle(color = Color.White, fontSize = 20.sp),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
                    keyboardController?.hide()
                })
            )

            Spacer(modifier = Modifier.padding(40.dp))

            Button(
                colors = ButtonDefaults.buttonColors(containerColor = Color(WHITEISH_COLOR)),
                onClick = {
                    keyboardController?.hide()
                    // Update OnboardingUiState
                    onboardingViewModel.updateUsernameInViewModel(username)
                    Log.d("Onboarding", "WScreen: Updated uname in uiState - OnboardingUiState: ${onboardingViewModel.onboardingUiState.value}")
                    // navigasjon til avatarskjermen
                    navController.navigate("avatar")
                }
            ) {
                Text(text = "Neste ", fontFamily = sfFontFamily, modifier = Modifier.padding(2.dp), color = Color(USER_PROFILE_BLUE_BACKGROUND_COLOR))
                AsyncImage(model = R.drawable.arrowicon, contentDescription = "neste", modifier = Modifier.size(15.dp))
            }
        }
    }
}
