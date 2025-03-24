package com.bmexcs.pickpic.presentation.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bmexcs.pickpic.presentation.viewmodels.RankingViewModel
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay
import kotlin.random.Random

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

    val currentBitmap by viewModel.currentImage.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val event by viewModel.event.collectAsState()
    val eventName = event.name

    // Animation progress (0f to 1f)
    val animationProgress by animateFloatAsState(
        targetValue = if (isAnimating) 1f else 0f,
        animationSpec = tween(durationMillis = 500),
        label = "SwipeAnimation"
    )

    // Animation for trash can (left swipe)
    val trashCanScale by animateFloatAsState(
        targetValue = if (swipeDirection == RankingViewModel.SwipeDirection.LEFT && isAnimating) 1.2f else 1f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 300f),
        label = "TrashCanScale"
    )

    // Animation for particles (right swipe)
    val particleCount = 15
    val particles = remember { List(particleCount) { Particle() } }

    // Reset animation state when bitmap changes
    LaunchedEffect(currentBitmap) {
        currentOffsetX = 0f
        isAnimating = false
        swipeDirection = null
        particles.forEach { it.reset() }
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
                        if (!isAnimating) {
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
                }
        ) {
            // Trash can icon (shown only during left swipe animation)
            if (swipeDirection == RankingViewModel.SwipeDirection.LEFT && isAnimating) {
                Icon(
                    painter = painterResource(android.R.drawable.ic_menu_delete),
                    contentDescription = "Trash",
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(32.dp)
                        .scale(trashCanScale),
                    tint = Color.Red
                )
            }

            // Particles (shown only during right swipe animation)
            if (swipeDirection == RankingViewModel.SwipeDirection.RIGHT && isAnimating) {
                particles.forEach { particle ->
                    if (particle.isActive) {
                        Box(
                            modifier = Modifier
                                .offset(particle.offset.x.dp, particle.offset.y.dp)
                                .size(particle.size.dp)
                                .background(
                                    color = Color.Green.copy(alpha = particle.alpha),
                                    shape = CircleShape
                                )
                        )
                    }
                }
            }

            LaunchedEffect(key1 = swipeStartedKey) {
                if (swipeStartedKey > 0 && !isAnimating) {
                    delay(100)
                    val absTotalOffsetX = kotlin.math.abs(totalOffsetX)

                    if (absTotalOffsetX > 100) {
                        isAnimating = true

                        if (totalOffsetX > 0) {
                            swipeDirection = RankingViewModel.SwipeDirection.RIGHT
                            particles.forEach { it.activate() }
                        } else {
                            swipeDirection = RankingViewModel.SwipeDirection.LEFT
                        }

                        delay(500) // Wait for animation to complete
                        viewModel.onSwipe(swipeDirection!!)
                        isAnimating = false
                        currentOffsetX = 0f
                        totalOffsetX = 0f
                    }
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
                                    translationX = if (isAnimating) {
                                        when (swipeDirection) {
                                            RankingViewModel.SwipeDirection.RIGHT ->
                                                currentOffsetX + (2000f * animationProgress)

                                            RankingViewModel.SwipeDirection.LEFT ->
                                                currentOffsetX - (2000f * animationProgress)

                                            else -> currentOffsetX
                                        }
                                    } else {
                                        currentOffsetX
                                    }
                                    rotationZ = currentOffsetX * 0.1f
                                    scaleX =
                                        if (swipeDirection == RankingViewModel.SwipeDirection.LEFT && isAnimating) {
                                            1f - animationProgress
                                        } else 1f
                                    scaleY =
                                        if (swipeDirection == RankingViewModel.SwipeDirection.LEFT && isAnimating) {
                                            1f - animationProgress
                                        } else 1f
                                    alpha =
                                        if (isAnimating && swipeDirection == RankingViewModel.SwipeDirection.LEFT) {
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

                        // Animate particles for right swipe
                        if (swipeDirection == RankingViewModel.SwipeDirection.RIGHT && isAnimating) {
                            LaunchedEffect(Unit) {
                                particles.forEach { particle ->
                                    with(particle) {
                                        offset = Offset(
                                            Random.nextFloat() * 300f - 150f,
                                            Random.nextFloat() * -300f - 50f
                                        )
                                        size = Random.nextFloat() * 10f + 5f
                                        alpha = Random.nextFloat() * 0.7f + 0.3f
                                        velocity = Offset(
                                            Random.nextFloat() * 4f - 2f,
                                            Random.nextFloat() * -8f - 4f
                                        )
                                        isActive = true
                                    }
                                }

                                // Animate particles over time
                                repeat(60) { frame ->
                                    particles.forEach { particle ->
                                        with(particle) {
                                            offset += velocity
                                            velocity = velocity.copy(y = velocity.y + 0.2f)
                                            alpha *= 0.95f
                                        }
                                    }
                                    delay(16)
                                }

                                particles.forEach { it.isActive = false }
                            }

                            // Draw active particles
                            particles.forEach { particle ->
                                if (particle.isActive) {
                                    Box(
                                        modifier = Modifier
                                            .offset(particle.offset.x.dp, particle.offset.y.dp)
                                            .size(particle.size.dp)
                                            .background(
                                                color = Color.Green.copy(alpha = particle.alpha),
                                                shape = CircleShape
                                            )
                                    )
                                }
                            }
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

// Particle data class for right swipe animation
private class Particle {
    var offset by mutableStateOf(Offset(0f, 0f))
    var size by mutableStateOf(0f)
    var alpha by mutableStateOf(0f)
    var isActive by mutableStateOf(false)
    var velocity by mutableStateOf(Offset(0f, 0f))

    fun activate() {
        offset = Offset(
            Random.nextFloat() * 300f - 150f,
            Random.nextFloat() * -300f - 50f
        )
        size = Random.nextFloat() * 10f + 5f
        alpha = Random.nextFloat() * 0.7f + 0.3f
        velocity = Offset(
            Random.nextFloat() * 4f - 2f,
            Random.nextFloat() * -8f - 4f
        )
        isActive = true
    }

    fun reset() {
        isActive = false
    }
}