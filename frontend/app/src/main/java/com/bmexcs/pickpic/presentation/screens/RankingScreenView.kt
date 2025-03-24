package com.bmexcs.pickpic.presentation.screens

import android.annotation.SuppressLint
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bmexcs.pickpic.presentation.viewmodels.RankingViewModel
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay
import kotlin.random.Random
import kotlin.math.abs
import kotlin.math.min

@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RankingScreenView(
    navController: NavHostController,
    viewModel: RankingViewModel = hiltViewModel()
) {
    var totalOffsetX by remember { mutableFloatStateOf(0f) }
    var currentOffsetX by remember { mutableFloatStateOf(0f) }
    var swipeStartedKey by remember { mutableIntStateOf(0) }
    var isAnimating by remember { mutableStateOf(false) }
    var swipeDirection by remember { mutableStateOf<RankingViewModel.SwipeDirection?>(null) }

    // Animation states for swipe feedback
    var showThumbsUp by remember { mutableStateOf(false) }
    var showThumbsDown by remember { mutableStateOf(false) }

    val currentBitmap by viewModel.currentImage.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val event by viewModel.event.collectAsState()
    val eventName = event.name

    // Animation values
    val animationProgress by animateFloatAsState(
        targetValue = if (isAnimating) 1f else 0f,
        animationSpec = tween(durationMillis = 500),
        label = "SwipeAnimation"
    )

    // Animation for feedback icons
    val feedbackIconScale by animateFloatAsState(
        targetValue = if (showThumbsUp || showThumbsDown) 1.5f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "FeedbackIconAnimation"
    )

    val feedbackIconAlpha by animateFloatAsState(
        targetValue = if (showThumbsUp || showThumbsDown) 0.9f else 0f,
        animationSpec = tween(durationMillis = 300),
        label = "FeedbackIconAlphaAnimation"
    )

    val swipeProgress by derivedStateOf {
        if (isAnimating) 0f else min(1f, abs(totalOffsetX) / 300f)
    }

    LaunchedEffect(currentBitmap) {
        currentOffsetX = 0f
        isAnimating = false
        swipeDirection = null
        showThumbsUp = false
        showThumbsDown = false
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TopAppBar(
            title = { Text(text = eventName) },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                }
            }
        )

        Box(modifier = Modifier.fillMaxSize()) {
            // Text indicators above image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .padding(top = 32.dp)
            ) {
                if (totalOffsetX > 0) {
                    Text(
                        text = "UPVOTE",
                        color = Color.Green.copy(alpha = swipeProgress),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 32.dp)
                            .graphicsLayer { translationX = 100f * (1 - swipeProgress) }
                    )
                }
                if (totalOffsetX < 0) {
                    Text(
                        text = "DOWNVOTE",
                        color = Color.Red.copy(alpha = swipeProgress),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(start = 32.dp)
                            .graphicsLayer { translationX = -100f * (1 - swipeProgress) }
                    )
                }
            }

            // Image container with gestures
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 80.dp)
                    .pointerInput(Unit) {
                        detectTransformGestures { _, pan, _, _ ->
                            if (!isAnimating) {
                                currentOffsetX += pan.x
                                totalOffsetX += pan.x
                                if (pan != Offset.Zero) swipeStartedKey++
                                if (pan == Offset.Zero && currentOffsetX != 0f) currentOffsetX = 0f
                            }
                        }
                    },
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
                                    translationX = when {
                                        isAnimating && swipeDirection == RankingViewModel.SwipeDirection.RIGHT ->
                                            currentOffsetX + (2000f * animationProgress)
                                        isAnimating && swipeDirection == RankingViewModel.SwipeDirection.LEFT ->
                                            currentOffsetX - (2000f * animationProgress)
                                        else -> currentOffsetX
                                    }
                                    rotationZ = currentOffsetX * 0.1f
                                    scaleX = if (isAnimating && swipeDirection == RankingViewModel.SwipeDirection.LEFT) {
                                        1f - animationProgress
                                    } else 1f
                                    scaleY = if (isAnimating && swipeDirection == RankingViewModel.SwipeDirection.LEFT) {
                                        1f - animationProgress
                                    } else 1f
                                    alpha = if (isAnimating && swipeDirection == RankingViewModel.SwipeDirection.LEFT) {
                                        1f - animationProgress
                                    } else 1f
                                }
                        ) {
                            Image(
                                bitmap = image.bitmap.asImageBitmap(),
                                contentDescription = "Current Image",
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    } ?: Text("All images ranked!", fontSize = 18.sp)
                }
            }

            // Material3 Feedback indicators
            if (showThumbsUp) {
                // Green circular ripple effect with thumbs up icon
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    // Background ripple
                    Surface(
                        modifier = Modifier
                            .size(200.dp)
                            .scale(feedbackIconScale),
                        shape = CircleShape,
                        color = Color.Green.copy(alpha = feedbackIconAlpha * 0.3f)
                    ) {}

                    // Inner circle with icon
                    Surface(
                        modifier = Modifier
                            .size(100.dp)
                            .scale(feedbackIconScale),
                        shape = CircleShape,
                        color = Color.Green.copy(alpha = feedbackIconAlpha * 0.7f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.ThumbUp,
                                contentDescription = "Upvote",
                                tint = Color.White,
                                modifier = Modifier.size(60.dp)
                            )
                        }
                    }
                }
            }

            if (showThumbsDown) {
                // Red circular ripple effect with thumbs down icon
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    // Background ripple
                    Surface(
                        modifier = Modifier
                            .size(200.dp)
                            .scale(feedbackIconScale),
                        shape = CircleShape,
                        color = Color.Red.copy(alpha = feedbackIconAlpha * 0.3f)
                    ) {}

                    // Inner circle with icon
                    Surface(
                        modifier = Modifier
                            .size(100.dp)
                            .scale(feedbackIconScale),
                        shape = CircleShape,
                        color = Color.Red.copy(alpha = feedbackIconAlpha * 0.7f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.ThumbDown,
                                contentDescription = "Downvote",
                                tint = Color.White,
                                modifier = Modifier.size(60.dp)
                            )
                        }
                    }
                }
            }
        }

        // Swipe handling
        LaunchedEffect(swipeStartedKey) {
            if (swipeStartedKey > 0 && !isAnimating) {
                delay(100)
                val absTotalOffsetX = abs(totalOffsetX)

                if (absTotalOffsetX > 100) {
                    isAnimating = true

                    // Determine swipe direction and show appropriate feedback
                    swipeDirection = if (totalOffsetX > 0) {
                        showThumbsUp = true
                        RankingViewModel.SwipeDirection.RIGHT
                    } else {
                        showThumbsDown = true
                        RankingViewModel.SwipeDirection.LEFT
                    }

                    // Keep feedback visible for a moment
                    delay(800)

                    viewModel.onSwipe(swipeDirection!!)
                    isAnimating = false
                    showThumbsUp = false
                    showThumbsDown = false
                    currentOffsetX = 0f
                    totalOffsetX = 0f
                }
            }
        }
    }
}