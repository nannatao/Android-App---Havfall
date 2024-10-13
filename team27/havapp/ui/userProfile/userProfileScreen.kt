package no.uio.ifi.in2000.team27.havapp.ui.userProfile

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import no.uio.ifi.in2000.team27.havapp.R
import no.uio.ifi.in2000.team27.havapp.data.location.LocationState
import no.uio.ifi.in2000.team27.havapp.model.cleaning.CleaningActivity
import no.uio.ifi.in2000.team27.havapp.model.utilities.Constants.Companion.BLUE_BACKGROUND_COLOR
import no.uio.ifi.in2000.team27.havapp.model.utilities.Constants.Companion.LIGHT_BLUE_CARD_BACKGROUND
import no.uio.ifi.in2000.team27.havapp.model.utilities.Constants.Companion.WHITEISH_COLOR
import no.uio.ifi.in2000.team27.havapp.ui.home.HomeViewModel
import no.uio.ifi.in2000.team27.havapp.ui.sfFontFamily
import java.util.concurrent.TimeUnit

/*
* Denne skjermen viser profilen som inneholder brukernavn og avatar samt utførte ryddeaksjoner og ryddestatistikk
*
* */
/***********************
 *  UserProfileScreen  *
 ***********************/
@RequiresApi(Build.VERSION_CODES.O)
@Composable
@SuppressLint(
    "UnusedMaterial3ScaffoldPaddingParameter", "UnusedBoxWithConstraintsScope",
    "StateFlowValueCalledInComposition"
)
fun UserProfileScreen(
    userProfileViewModel: UserProfileViewModel,
    navController: NavController
) {
    val userProfileUiState by userProfileViewModel.userProfileUiState.collectAsState()
    val cleaningActivityList = userProfileUiState.cleaningActivities.reversed()

    val snackbarHostState = SnackbarHostState()

    val homeViewModel = HomeViewModel()
    val locationState by homeViewModel.locationState.collectAsState()
    val context = LocalContext.current

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // background image
            AsyncImage(
                modifier = Modifier.matchParentSize()
                    .padding(bottom = 49.dp),
                model = R.drawable.profilskjermbakgrunn,
                contentDescription = "Background image",
                contentScale = ContentScale.FillBounds,
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                UserProfileContent(
                    userProfileUiState = userProfileUiState,
                    userProfileViewModel = userProfileViewModel,
                    navController = navController,
                    cleaningActivityList = cleaningActivityList
                )
            }
        }
    }
}


/*************************
 *  UserProfileContent   *
 *************************/
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun UserProfileContent(
    userProfileUiState: UserProfileUiState,
    userProfileViewModel: UserProfileViewModel,
    navController: NavController,
    cleaningActivityList: List<CleaningActivity>
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        LogoutButton(modifier = Modifier
            .align(Alignment.TopEnd)
            .padding(top = 16.dp, end = 16.dp), userProfileViewModel = userProfileViewModel, navController = navController)
    }

    UserCard(userProfileUiState = userProfileUiState)
    MyCleaningActivities(cleaningActivityList)
    TrashCleanedSummary(userProfileUiState)
}


/**************
 *  UserCard  *
 **************/
@Composable
fun UsernameText(username: String) {
    Text(
        text = username,
        fontFamily = sfFontFamily,
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.SemiBold,
        textAlign = TextAlign.Start,
        color = Color.White,
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentWidth(Alignment.CenterHorizontally)
            .padding(horizontal = 16.dp)
    )
}

@Composable
fun AvatarImage(avatar: Int) {
    AsyncImage(
        model = avatar,
        contentDescription = "User avatar",
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(115.dp)
            .clip(CircleShape),
        alignment = Alignment.Center
    )
}

@Composable
fun UserCard(userProfileUiState: UserProfileUiState) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, bottom = 1.dp),
        colors = CardDefaults.cardColors(Color.Transparent),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
                .padding (top = 15.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AvatarImage(avatar = userProfileUiState.avatarId)
            UsernameText(username = userProfileUiState.username)
        }
    }
}

/*************************
 *  MyCleaningActivities  *
 *************************/

@Composable
fun time2Text(timeMi: Long): String {
    return formatTime(timeMi)
}

@Composable
fun formatTime(timeMi: Long): String {
    val hours = TimeUnit.MILLISECONDS.toHours(timeMi)
    val min = TimeUnit.MILLISECONDS.toMinutes(timeMi) % 60
    val sec = TimeUnit.MILLISECONDS.toSeconds(timeMi) % 60

    if (hours == Long.MIN_VALUE) {
        return String.format("%01dm, %02ds", min, sec)
    }
    if (min.toDouble() == 0.toDouble()) {
        return String.format("%01dm, %02ds", min, sec)
    }
    return String.format("%01dt, %02dm", hours, min)
}

@Composable
fun MyCleaningActivities(cleaningActivityList: List<CleaningActivity>) {
    Text(
        text = "Mine ryddeaksjoner",
        fontFamily = sfFontFamily,
        style = MaterialTheme.typography.headlineSmall,
        textAlign = TextAlign.Start,
        color = Color.White,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    )
    if (cleaningActivityList.isNotEmpty()) {
        MyCleaningActivitiesCard(cleaningActivityList)
    } else {
        EmptyCleaningActivitiesCard()
    }
}

@Composable
fun MyCleaningActivitiesCard(cleaningActivityList: List<CleaningActivity>) {

    var itemsToShow by remember { mutableStateOf(2)}

    if (cleaningActivityList.size == 1) {
        itemsToShow = 1
    }

    val cleaningActivitySubList = cleaningActivityList.subList(0, itemsToShow) // kun de y siste ryddinger

    var pushDownContentBy by remember { mutableStateOf(30.dp)}
    var showMoreButtonClicked by remember { mutableStateOf(false)}

    Card(
        modifier = Modifier
            .width(400.dp)
            .padding(top = 16.dp, start = 26.dp, end = 16.dp),
        colors = CardDefaults.cardColors(Color(LIGHT_BLUE_CARD_BACKGROUND)),
        shape = RoundedCornerShape(size = 18.dp),

        ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(1),
                contentPadding = PaddingValues(horizontal = 5.dp, vertical = 2.dp),
                verticalItemSpacing = 2.dp
            ) {
                items(cleaningActivitySubList) { item ->
                    // en grid av CleaningActivities
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(70.dp),
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(
                                horizontalAlignment = Alignment.Start,
                            ) {

                                // Location
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(start = 7.dp).padding(top = 7.dp)) {
                                    Image(
                                        painter = painterResource(R.drawable.vector),
                                        contentDescription = "vector",
                                        modifier = Modifier.size(25.dp).padding(end = 7.dp)
                                    )
                                    Text(
                                        text = item.location,
                                        fontFamily = sfFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(BLUE_BACKGROUND_COLOR)
                                    )
                                }
                                // Tid
                                Text(
                                    text = "         " + time2Text(timeMi = item.duration.toLong()),
                                    fontFamily = sfFontFamily,
                                    color = Color(BLUE_BACKGROUND_COLOR)
                                )
                            }
                            // Date
                            Box(
                                modifier = Modifier
                                    .width(50.dp)
                                    .height(60.dp)
                                    .padding(4.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(WHITEISH_COLOR)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = item.date,
                                    fontFamily = sfFontFamily,
                                    color = Color(BLUE_BACKGROUND_COLOR),
                                    textAlign = TextAlign.Center,
                                    lineHeight = 20.sp
                                )
                            }
                        }
                    }
                }
            }
            if (cleaningActivityList.size > 2) {
                if (!showMoreButtonClicked) {
                    Button(
                        modifier = Modifier.padding(top = 0.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(
                                LIGHT_BLUE_CARD_BACKGROUND
                            ).copy(0.8f)
                        ),
                        onClick = {
                            showMoreButtonClicked = true
                            if (cleaningActivityList.size == 3) {
                                itemsToShow = 3
                                pushDownContentBy = 200.dp
                            } else {
                                if (cleaningActivityList.size == 4) {
                                    itemsToShow = 4
                                    pushDownContentBy = 150.dp
                                } else {
                                    itemsToShow = 5
                                }
                            }
                        }
                    ) {
                        Text("Vis fler", color = Color(BLUE_BACKGROUND_COLOR))
                    }
                } else {
                    Button(
                        modifier = Modifier.padding(top = 0.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(
                                LIGHT_BLUE_CARD_BACKGROUND
                            ).copy(0.8f)
                        ),
                        onClick = {
                            showMoreButtonClicked = false
                            itemsToShow = 2
                        }
                    ) {
                        Text("Skjul", color = Color(BLUE_BACKGROUND_COLOR))
                    }
                }
            }
        }
    }
    // Flytter resten av komponentene ut av synet dersom "vis mer" er trykket
    if (showMoreButtonClicked) {
        Spacer(modifier = Modifier.height(pushDownContentBy))
    }

}

@Composable
fun EmptyCleaningActivitiesCard() {
    Card(
        modifier = Modifier
            .width(400.dp)
            .padding(16.dp)
            .padding(start = 10.dp),
        colors = CardDefaults.cardColors(Color(LIGHT_BLUE_CARD_BACKGROUND)),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(90.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                modifier = Modifier.padding(16.dp),
                text = "Her vil dine ryddeaksjoner dukke opp. Kom deg ut og hjelp planeten vår!",
                fontFamily = sfFontFamily,
                fontWeight = FontWeight.Medium,
                color = Color(BLUE_BACKGROUND_COLOR)
            )
        }
    }
}


/***************************
 *  Trash Cleaned Summary  *
 ***************************/
@Composable
fun TrashCleanedSummary(userProfileUiState: UserProfileUiState) {
    var trashSummary = userProfileUiState.cleaningSummary
    var cardColor = CardDefaults.cardColors(Color(LIGHT_BLUE_CARD_BACKGROUND))
    var cardTextModifier = Modifier.padding(start = 10.dp)

    Text(
        text = "Hittil i år",
        fontFamily = sfFontFamily,
        style = MaterialTheme.typography.headlineSmall,
        textAlign = TextAlign.Start,
        color = Color(WHITEISH_COLOR),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(top = 15.dp)
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 20.dp),
        colors = CardDefaults.cardColors(Color.Transparent),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row (){
                Card(
                    modifier = Modifier.size(150.dp, 69.dp),
                    colors = cardColor,
                    shape = RoundedCornerShape(size = 20.dp),
                ) {
                    Box(modifier = Modifier.padding(8.dp)) {
                        AsyncImage(
                            model = R.drawable.flaske_ikon,
                            contentDescription = "Plast",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .size(45.dp)
                                .align(Alignment.TopEnd)
                                .padding(top = 4.dp)
                        )

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(end = 55.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = trashSummary["Plast"].toString(),
                                modifier = cardTextModifier
                                    .padding(end = 45.dp),
                                fontWeight = FontWeight.Bold
                            )
                            Text(text = "Plast", modifier = Modifier.padding(end = 10.dp))
                        }
                    }
                }
                Spacer(modifier = Modifier.width(53.dp))
                Card(
                    modifier = Modifier
                        .size(150.dp, 69.dp),
                    colors = cardColor,
                    shape = RoundedCornerShape(size = 20.dp),
                ) {
                    Box(modifier = Modifier.padding(8.dp)){

                        AsyncImage(
                            model = R.drawable.fiskeutstyr,
                            contentDescription = "Fiskeutstyr",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .size(40.dp)
                                .align(Alignment.TopEnd)
                                .padding(end = 3.dp)
                        )
                        Column (modifier = Modifier
                            .fillMaxSize()
                            .padding(end = 30.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally){
                            Text(text = trashSummary["Fiskeutstyr"].toString(),
                                modifier = Modifier
                                    .padding(end = 60.dp),
                                fontWeight = FontWeight.Bold)
                            Text(text = "Fiskeutstyr")

                        }

                    }

                }
            }
            Spacer(modifier = Modifier.height(15.dp))

            Row (){
                Card(
                    modifier = Modifier
                        .size(150.dp, 69.dp),
                    colors = cardColor,
                    shape = RoundedCornerShape(size = 20.dp),
                ) {
                    Box(modifier = Modifier.padding(8.dp)) {
                        AsyncImage(
                            model = R.drawable.sigarett_ikon,
                            contentDescription = "Sigaretter",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .size(40.dp)
                                .align(Alignment.TopEnd)
                                .padding(end = 3.dp)
                        )

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(end = 55.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ){
                            Text(text = trashSummary["Sigaretter"].toString(), modifier = cardTextModifier
                                .padding(end = 48.dp),
                                fontWeight = FontWeight.Bold)
                            Text(text = "Tobakk")

                        }

                    }
                }

                Spacer(modifier = Modifier.width(53.dp))

                Card(
                    modifier = Modifier.size(150.dp, 69.dp),
                    colors = cardColor,
                    shape = RoundedCornerShape(size = 20.dp),
                ) {
                    Box(modifier = Modifier.padding(10.dp)) {

                        AsyncImage(
                            model = R.drawable.boss_ikon,
                            contentDescription = "Annet",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .size(40.dp)
                                .align(Alignment.TopEnd)
                                .padding(end = 2.dp)
                        )

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(end = 55.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ){
                            Text(
                                text = trashSummary["Annet"].toString(),
                                modifier = cardTextModifier
                                    .padding(end = 45.dp),
                                fontWeight = FontWeight.Bold
                            )
                            Text(text = "Annet", modifier = Modifier.padding(end = 2.dp))
                        }

                    }
                }
            }
        }
    }

}



/*******************
 *  Logout button  *
 *******************/

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun LogoutButton(modifier: Modifier, userProfileViewModel: UserProfileViewModel, navController: NavController) {
    Button(
        modifier = modifier,
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF030C31)),
        onClick = {
            navController.navigate("welcome")
            userProfileViewModel.onClickLogOut()
        }
    ) {
        AsyncImage(model = R.drawable.logouticon, contentDescription = "Logg ut", contentScale = ContentScale.Fit, modifier = Modifier.size(30.dp))
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun LogoutDialog(userProfileViewModel: UserProfileViewModel, navController: NavController) {
    AlertDialog(
        onDismissRequest = {

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
                ),
                color = Color(WHITEISH_COLOR)
            )
        },
        dismissButton = {
            // AVBRYT
            Button(
                onClick = {
                    userProfileViewModel.hideLogoutDialog()
                },
                modifier = Modifier
                    .height(48.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(WHITEISH_COLOR)),
            ) {
                Text("Avbryt", color = Color(BLUE_BACKGROUND_COLOR))
            }
        },
        confirmButton = {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = {
                        navController.navigate("welcome")
                        userProfileViewModel.onClickLogOut()
                    },
                    modifier = Modifier
                        .padding(start = 36.dp)
                        .height(48.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF35C5C)),
                ) {
                    Text("AVSLUTT", color = Color(WHITEISH_COLOR))
                }
            }
        }
    )
}