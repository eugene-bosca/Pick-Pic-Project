package com.bmexcs.pickpic.presentation.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.bmexcs.pickpic.presentation.viewmodels.RankingViewModel
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay

@Composable
fun RankingScreenView(
    navController: NavHostController,
    viewModel: RankingViewModel = hiltViewModel()
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFD0DADC)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        SwipeView(viewModel)
    }
}

@Composable
fun SwipeView(
    viewModel: RankingViewModel = hiltViewModel()
) {
    var totalOffsetX by remember { mutableFloatStateOf(0f) }
    var currentOffsetX by remember { mutableFloatStateOf(0f) }
    var swipeStartedKey by remember { mutableIntStateOf(0) }

    val currentBitmap by viewModel.currentImage.collectAsState()

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

        // Timeout using LaunchedEffect
        LaunchedEffect(key1 = swipeStartedKey) {
            if (swipeStartedKey > 0) { // Only start timeout if a swipe has started
                delay(100) // Timeout: 100 milliseconds (adjust as needed)

                val absTotalOffsetX = kotlin.math.abs(totalOffsetX)

                if (absTotalOffsetX > 50) { // Check total offset
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
                totalOffsetX = 0f // Reset total offset
            }
        }

        currentBitmap?.let { image ->
            Image(
                bitmap = image.bitmap.asImageBitmap(), // Display the Bitmap
                contentDescription = "Current Image", // Important for accessibility
                modifier = Modifier.fillMaxSize() // Or your preferred size
            )
        }
    }
}
