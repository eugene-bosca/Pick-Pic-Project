package com.bmexcs.pickpic.presentation.ranking

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.asImageBitmap
import kotlinx.coroutines.delay

@Composable
fun SwipeView(
    viewModel: SwipeViewModel = viewModel() // Get the ViewModel instance
) {
    val dir = LocalContext.current

    LaunchedEffect(key1 = Unit) {
        viewModel.initializeBitmaps(dir) // Initialize Bitmaps
    }

    var totalOffsetX by remember { mutableStateOf(0f) }
    var currentOffsetX by remember { mutableStateOf(0f) }
    var swipeStartedKey by remember { mutableStateOf(0) } // Key for LaunchedEffect

    val currentBitmap by viewModel.currentBitmap.observeAsState(initial = null)

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
                        viewModel.onSwipe(SwipeViewModel.SwipeDirection.RIGHT)
                        Log.d("SwipeView", "Swipe Detected: RIGHT")
                    } else {
                        viewModel.onSwipe(SwipeViewModel.SwipeDirection.LEFT)
                        Log.d("SwipeView", "Swipe Detected: LEFT")
                    }
                } else {
                    Log.d("SwipeView", "Not a swipe. totalOffsetX too small: $absTotalOffsetX")
                }
                totalOffsetX = 0f // Reset total offset
            }
        }
        currentBitmap?.let { bitmap ->
            Image(
                bitmap = bitmap.asImageBitmap(), // Display the Bitmap
                contentDescription = "Current Image", // Important for accessibility
                modifier = Modifier.fillMaxSize() // Or your preferred size
            )
        }
    }
}
