package com.bmexcs.pickpic.presentation.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bmexcs.pickpic.presentation.viewmodels.RankingViewModel
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RankingScreenView(
    navController: NavHostController,
    viewModel: RankingViewModel = hiltViewModel()
) {
    var totalOffsetX by remember { mutableFloatStateOf(0f) }
    var currentOffsetX by remember { mutableFloatStateOf(0f) }
    var swipeStartedKey by remember { mutableIntStateOf(0) }

    val currentBitmap by viewModel.currentImage.collectAsState()

    val isLoading by viewModel.isLoading.collectAsState()

    val eventInfo by viewModel.event.collectAsState()
    val eventName = eventInfo.event_name

    // when the bitmap changes, refresh the offset
    LaunchedEffect(currentBitmap) {
        currentOffsetX = 0f;
    }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        TopAppBar(
            title = { Text(text = eventName) },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        )

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

                    if (absTotalOffsetX > 100) {
                        if (totalOffsetX > 0) {
                            viewModel.onSwipe(RankingViewModel.SwipeDirection.RIGHT)
                            Log.d("SwipeView", "Swipe Detected: RIGHT")
                        } else {
                            viewModel.onSwipe(RankingViewModel.SwipeDirection.LEFT)
                            Log.d("SwipeView", "Swipe Detected: LEFT")
                        }
                    } else {
                        Log.d(
                            "SwipeView",
                            "Not a swipe. totalOffsetX too small: $absTotalOffsetX"
                        )
                    }

                    totalOffsetX = 0f
                }
            }

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                if (isLoading) {
                    CircularProgressIndicator()
                } else {
                    currentBitmap?.let { image ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .graphicsLayer {
                                    translationX = currentOffsetX
                                    rotationZ = currentOffsetX * 0.1f
                                }
                        ) {
                            Image(
                                bitmap = image.bitmap.asImageBitmap(),
                                contentDescription = "Current Image",
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    } ?: Text(
                        "All images ranked!",
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}
