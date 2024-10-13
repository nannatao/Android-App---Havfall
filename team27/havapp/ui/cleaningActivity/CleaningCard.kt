package no.uio.ifi.in2000.team27.havapp.ui.cleaningActivity

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import no.uio.ifi.in2000.team27.havapp.model.utilities.Constants.Companion.WHITEISH_COLOR
import no.uio.ifi.in2000.team27.havapp.model.cleaning.stringToTrashType

/*
Kort som viser et ikon for en Trashtype (søppeltype), med pluss og minus-knapper
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CleaningCard(
    painter: Painter,
    title: String,
    cleaningActivityViewModel: CleaningActivityViewModel,
    modifier: Modifier = Modifier
) {
    val trashType = stringToTrashType(title)
    val cleaningActivityUiState by cleaningActivityViewModel.cleaningActivityUiState.collectAsState()
    val counter = cleaningActivityUiState.cleaningActivity.trash[trashTypeToString(trashType)] ?: 0

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(150.dp)
            .padding(8.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(WHITEISH_COLOR).copy(alpha = 0.7f)
        )
    ) {
        Box(Modifier.fillMaxSize()) {
            Box(Modifier.fillMaxSize()) {
                Row(modifier = Modifier.fillMaxHeight()) {

                    //Boks med minustegn
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        contentAlignment = Alignment.Center
                    ) {
                        Button(onClick = {
                            if (counter > 0) {
                                cleaningActivityViewModel.onClickRemoveTrashType(trashType)
                            }

                        }, colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                        ) {
                            Text("-",
                                fontWeight = FontWeight.Bold,
                                fontSize = 50.sp,
                                color = Color.Black)
                        }
                    }

                    // Box med søppelikon og beskrivelse
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Image(
                                painter = painter,
                                contentDescription = title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(50.dp, 50.dp)
                                    .fillMaxWidth()
                                    .clip(RectangleShape), // Set the shape to RectangleShape
                            )
                            Text(title, fontSize = 16.sp)
                        }
                    }

                    //Box med pluss-tegn
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        contentAlignment = Alignment.Center
                    ) {
                        Button(onClick = {
                            cleaningActivityViewModel.onClickAddTrashType(trashType)
                        },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                        ) {
                            Text("+",
                                fontWeight = FontWeight.Bold,
                                fontSize = 40.sp,
                                color = Color.Black)
                        }
                    }
                }
            }

            // Teller (plassert i høyre hjørnet av skjermen)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                contentAlignment = Alignment.TopEnd
            ) {
                Text(counter.toString(), fontSize = 26.sp,
                    fontWeight = FontWeight.SemiBold)
            }

        }
    }
}