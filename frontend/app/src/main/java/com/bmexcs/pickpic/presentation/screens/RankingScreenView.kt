package com.bmexcs.pickpic.presentation.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bmexcs.pickpic.presentation.viewmodels.RankingViewModel
import androidx.navigation.NavHostController
import com.bmexcs.pickpic.navigation.Route
import kotlinx.coroutines.delay

@Composable
fun RankingScreenView(
    navController: NavHostController,
    viewModel: RankingViewModel = hiltViewModel()
) {
    var totalOffsetX by remember { mutableFloatStateOf(0f) }
    var currentOffsetX by remember { mutableFloatStateOf(0f) }
    var swipeStartedKey by remember { mutableIntStateOf(0) }

    val currentBitmap by viewModel.currentImage.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFD0DADC)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, _, _ ->
                        currentOffsetX += pan.x
                        totalOffsetX += pan.x

                        if (pan != Offset.Zero) {
                            swipeStartedKey++
                        }

                        if (pan == Offset.Zero && currentOffsetX != 0f) {
                            currentOffsetX = 0f
                        }
                    }
                }
        ) {
            LaunchedEffect(key1 = swipeStartedKey) {
                if (swipeStartedKey > 0) {
                    // Delay to prevent several swipe actions occurring and stopping at once.
                    delay(100)

                    val absTotalOffsetX = kotlin.math.abs(totalOffsetX)

                    if (absTotalOffsetX > 50) {
                        if (totalOffsetX > 0) {
                            viewModel.onSwipe(RankingViewModel.SwipeDirection.RIGHT)
                            Log.d("SwipeView", "Swipe Detected: RIGHT")
                        } else {
                            viewModel.onSwipe(RankingViewModel.SwipeDirection.LEFT)
                            Log.d("SwipeView", "Swipe Detected: LEFT")
                        }
                    } else {
                        Log.d("SwipeView", "Not a swipe. totalOffsetX too small: $absTotalOffsetX")
                    }

                    totalOffsetX = 0f
                }
            }

            currentBitmap?.let { image ->
                Image(
                    bitmap = image.bitmap.asImageBitmap(),
                    contentDescription = "Current Image",
                    modifier = Modifier.fillMaxSize()
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ReturnButton(onClick = { navController.navigate(Route.Event.route) })
                    SkipButton(onClick = { viewModel.onSkip() })
                }
            }
        }
    }
}

@Composable
fun ReturnButton(onClick: () -> Unit) {
    Button(onClick) {
        Text("Return to Events Page")
    }
}

@Composable
fun SkipButton(onClick: () -> Unit) {
    Button(onClick) {
        Text("Skip Photo")
    }
}
