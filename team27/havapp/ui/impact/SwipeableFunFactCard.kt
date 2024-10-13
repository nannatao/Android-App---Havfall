package no.uio.ifi.in2000.team27.havapp.ui.impact

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.calculateTargetValue
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.splineBasedDecay
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.verticalDrag
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team27.havapp.R
import no.uio.ifi.in2000.team27.havapp.model.utilities.Constants.Companion.BLUE_BACKGROUND_COLOR
import no.uio.ifi.in2000.team27.havapp.model.utilities.Constants.Companion.BLUE_BUTTON_COLOR
import no.uio.ifi.in2000.team27.havapp.model.utilities.Constants.Companion.LIGHTBLUE_ACCENT
import no.uio.ifi.in2000.team27.havapp.model.utilities.Constants.Companion.LIGHT_BLUE_CARD_BACKGROUND
import no.uio.ifi.in2000.team27.havapp.model.utilities.Constants.Companion.USER_PROFILE_BLUE_BACKGROUND_COLOR
import java.lang.Float.max
import java.lang.Float.min
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

/*****************************
 *  Swipeable Funfact Cards  *
 *****************************/
// Inspirert av Chris Sinco (https://www.jetpackcompose.app/snippets/SwipeableCards)

@Preview
@Composable
fun SwipeableCards() {

    var cardColors by remember {
        mutableStateOf(
            listOf(
                Color(LIGHT_BLUE_CARD_BACKGROUND),
                Color(USER_PROFILE_BLUE_BACKGROUND_COLOR),
                Color(BLUE_BUTTON_COLOR),
                Color(LIGHTBLUE_ACCENT),
            ).reversed()
        )
    }

    val images = listOf(
        R.drawable.ff_gull,
        R.drawable.ff_havert_er_varmt,
        R.drawable.ff_spermhvaler,
        R.drawable.ff_havet_er_stort,
        R.drawable.ff_tilgjengelig_vann
    )

    var funfactListIndex by remember { mutableIntStateOf(0) }
    
    Box(
        Modifier
            .background(Color(BLUE_BACKGROUND_COLOR))
            .padding(vertical = 32.dp)
            .fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        cardColors.forEachIndexed { idx, color ->
            key(color) {
                SwipeableCard(
                    order = idx,
                    totalCount = images.size,
                    backgroundColor = color,
                    onMoveToBack = {
                        cardColors = listOf(color) + (cardColors - color)
                        if (funfactListIndex == images.size - 1) {
                            // Hvis brukeren har bladd igjennom alle funfacts,
                            // resett indeksen
                            funfactListIndex = 0
                        } else {
                            // Hvis ikke, øk indeks med 1
                            funfactListIndex += 1
                        }
                    },
                    funfactImage = images[funfactListIndex] //funfactlist[funfactListIndex + idx].img
                )
            }
        }
    }
}

@Composable
fun SwipeableCard(
    order: Int,
    totalCount: Int,
    backgroundColor: Color = Color.White,
    onMoveToBack: () -> Unit,
    funfactImage: Int
) {
    // Animasjon
    val animatedScale by animateFloatAsState(
        targetValue = 1f - (totalCount - order) * 0.05f, label = "",
    )
    val animatedYOffset by animateDpAsState(
        targetValue = ((totalCount - order) * -12).dp, label = "",
    )

    Box(
        modifier = Modifier
            .offset { IntOffset(x = 0, y = animatedYOffset.roundToPx()) }
            .graphicsLayer {
                scaleX = animatedScale
                scaleY = animatedScale
            }
            .swipeToBack { onMoveToBack() }
    ) {
        // Selve kortet
        FunfactCard(imageResource = funfactImage, backgroundColor = backgroundColor)
    }
}

@Composable
fun FunfactCard(imageResource: Int, backgroundColor: Color) {
    Card(
        modifier = Modifier
            .height(220.dp)
            .fillMaxWidth(.8f),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        border = BorderStroke(8.dp, backgroundColor),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(
            modifier = Modifier
                .height(220.dp)
                .fillMaxWidth() // .8f)
        ) {
            // Bakgrunnsbilde til kortene
            AsyncImage(
                model = imageResource,             //R.drawable.havet_er_stort
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.matchParentSize()
            )
        }
    }
}

/*****************************
 *  Animations, SwipeToBack  *
 *****************************/
fun Modifier.swipeToBack(
    onMoveToBack: () -> Unit
): Modifier = composed {
    val offsetY = remember { Animatable(0f) }
    val rotation = remember { Animatable(0f) }
    var leftSide by remember { mutableStateOf(true) }
    var clearedHurdle by remember { mutableStateOf(false) }

    pointerInput(Unit) {
        val decay = splineBasedDecay<Float>(this)

        coroutineScope {
            while (true) {
                offsetY.stop()
                val velocityTracker = VelocityTracker()

                awaitPointerEventScope {
                    verticalDrag(awaitFirstDown().id) { change ->
                        val verticalDragOffset = offsetY.value + change.positionChange().y
                        val horizontalPosition = change.previousPosition.x

                        leftSide = horizontalPosition <= size.width / 2
                        val offsetXRatioFromMiddle = if (leftSide) {
                            horizontalPosition / (size.width / 2)
                        } else {
                            (size.width - horizontalPosition) / (size.width / 2)
                        }
                        val rotationalOffset = max(1f, (1f - offsetXRatioFromMiddle) * 4f)

                        launch {
                            offsetY.snapTo(verticalDragOffset)
                            rotation.snapTo(if (leftSide) rotationalOffset else -rotationalOffset)
                        }

                        velocityTracker.addPosition(change.uptimeMillis, change.position)
                        if (change.positionChange() != Offset.Zero) change.consume()
                    }
                }

                val velocity = velocityTracker.calculateVelocity().y
                val targetOffsetY = decay.calculateTargetValue(offsetY.value, velocity)

                if (targetOffsetY.absoluteValue <= size.height) {
                    // Not enough velocity; Reset.
                    launch { offsetY.animateTo(targetValue = 0f, initialVelocity = velocity) }
                    launch { rotation.animateTo(targetValue = 0f, initialVelocity = velocity) }
                } else {
                    // Enough velocity to fling the card to the back
                    val boomerangDuration = 600
                    val maxDistanceToFling = (size.height * 4).toFloat()
                    val maxRotations = 3
                    val easeInOutEasing = CubicBezierEasing(0.42f, 0.0f, 0.58f, 1.0f)

                    val distanceToFling = min(
                        targetOffsetY.absoluteValue + size.height, maxDistanceToFling
                    )
                    val rotationToFling = min(
                        360f * (targetOffsetY.absoluteValue / size.height).roundToInt(),
                        360f * maxRotations
                    )
                    val rotationOvershoot = rotationToFling + 12f

                    val animationJobs = listOf(
                        launch {
                            rotation.animateTo(targetValue = if (leftSide) rotationToFling else -rotationToFling,
                                initialVelocity = velocity,
                                animationSpec = keyframes {
                                    durationMillis = boomerangDuration
                                    0f at 0 using easeInOutEasing
                                    (if (leftSide) rotationOvershoot else -rotationOvershoot) at boomerangDuration - 50 using LinearOutSlowInEasing
                                    (if (leftSide) rotationToFling else -rotationToFling) at boomerangDuration
                                })
                            rotation.snapTo(0f)
                        },
                        launch {
                            offsetY.animateTo(targetValue = 0f,
                                initialVelocity = velocity,
                                animationSpec = keyframes {
                                    durationMillis = boomerangDuration
                                    -distanceToFling at (boomerangDuration / 2) using easeInOutEasing
                                    40f at boomerangDuration - 70
                                }
                            ) {
                                if (value <= -size.height * 2 && !clearedHurdle) {
                                    onMoveToBack()
                                    clearedHurdle = true
                                }
                            }
                        }
                    )
                    animationJobs.joinAll()
                    clearedHurdle = false
                }
            }
        }
    }
        .offset { IntOffset(0, offsetY.value.roundToInt()) }
        .graphicsLayer {
            transformOrigin = TransformOrigin.Center
            rotationZ = rotation.value
        }
}