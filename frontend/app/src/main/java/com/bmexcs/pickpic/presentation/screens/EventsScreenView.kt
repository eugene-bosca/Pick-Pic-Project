package com.bmexcs.pickpic.presentation.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.bmexcs.pickpic.R
import com.bmexcs.pickpic.navigation.Route
import com.bmexcs.pickpic.presentation.shared.ImageFull
import com.bmexcs.pickpic.presentation.viewmodels.EventsViewModel
import androidx.compose.material3.Icon
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.sp

@Composable
fun EventScreenView(
    navController: NavHostController,
    viewModel: EventsViewModel = hiltViewModel(),
) {
    val context = LocalContext.current

    val images = viewModel.images.collectAsState().value.toList()
    val isLoading by viewModel.isLoading.collectAsState()

    var fullScreenImage by remember { mutableStateOf<ImageRequest?>(null) }

    val eventInfo by viewModel.event.collectAsState()
    val eventId = eventInfo.event_id

    val expandFilter = remember { mutableStateOf(false) }

    // Infinite scroll state
    val gridState = rememberLazyGridState()

    // Load more when scrolled to end
    LaunchedEffect(gridState) {
        snapshotFlow { gridState.layoutInfo.visibleItemsInfo }
            .collect { visibleItems ->
                if (visibleItems.isNotEmpty() && !isLoading) {
                    val lastVisibleItemIndex = visibleItems.last().index
                    val totalItemsCount = gridState.layoutInfo.totalItemsCount

                    // Load more if we're within 5 items from the end
                    if (lastVisibleItemIndex >= totalItemsCount - 5) {
                        viewModel.loadNextPage()
                    }
                }
            }
    }

    // Rest of your existing code for launcher, buttons, etc...

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            when {
                isLoading && images.isEmpty() -> {
                    CircularProgressIndicator()
                }

                images.isEmpty() -> {
                    Text("Empty event. Click Add Photos to get started!")
                }

                else -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        state = gridState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        itemsIndexed(images) { _, (imageId, stream) ->
                            stream?.let {
                                val imageRequest = ImageRequest.Builder(context)
                                    .data(it)
                                    .memoryCachePolicy(CachePolicy.ENABLED)
                                    .diskCachePolicy(CachePolicy.ENABLED)
                                    .crossfade(true)
                                    .build()

                                ImageTile(
                                    imageData = it,
                                    imageRequest = imageRequest,
                                    imageId = imageId,
                                    onClick = { fullScreenImage = imageRequest },
                                    viewModel = viewModel
                                )
                            }
                        }

                        // Show loading indicator at bottom
                        if (isLoading) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                        }
                    }
                }
            }
        }

        NavigationBar {
            buttons.forEach { info ->
                NavigationBarItem(
                    icon = {
                        Icon(
                            painter = painterResource(info.icon),
                            contentDescription = info.label
                        )
                    },
                    label = { Text(info.label, fontSize = 16.sp) },
                    selected = false,
                    onClick = info.onClick,
                )
            }
        }

        val filterButtonBox = remember { mutableStateOf(Offset.Zero) }

        Box(
            modifier = Modifier.onGloballyPositioned { coordinates ->
                filterButtonBox.value = coordinates.localToWindow(Offset.Zero)
            }
        ) {
            DropdownMenu(
                expanded = expandFilter.value,
                onDismissRequest = {
                    expandFilter.value = !expandFilter.value
                },
                offset = DpOffset(100.dp, 0.dp)
            ) {
                DropdownMenuItem(
                    text = { Text("Date") },
                    onClick = {
                        viewModel.getImagesByEventId(viewModel.event.value.event_id)
                        expandFilter.value = !expandFilter.value
                    }
                )
                DropdownMenuItem(
                    text = { Text("Score") },
                    onClick = {
                        viewModel.getImagesByEventId(viewModel.event.value.event_id)
                        expandFilter.value = !expandFilter.value
                    }
                )
            }
        }

        fullScreenImage?.let { request ->
            Dialog(
                onDismissRequest = { fullScreenImage = null }
            ) {
                ImageFull(
                    image = request,
                    onDismiss = { fullScreenImage = null }
                )
            }
        }
    }
}

@Composable
fun ImageTile(
    imageData: ByteArray,
    imageRequest: ImageRequest,
    imageId: String,
    onClick: () -> Unit,
    viewModel: EventsViewModel
) {
    ElevatedCard(
        modifier = Modifier
            .size(width = 150.dp, height = 225.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp)
    ) {
        val context = LocalContext.current

        var isExpanded by remember { mutableStateOf(false) }

        Box(modifier = Modifier.padding(start = 140.dp)) {
            IconButton(
                onClick = { isExpanded = !isExpanded },
                modifier = Modifier.size(30.dp)
            ) {
                Icon(
                    painterResource(id = R.drawable.more_horizontal),
                    contentDescription = "More options"
                )
            }
            DropdownMenu(
                expanded = isExpanded,
                onDismissRequest = { isExpanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Delete Photo") },
                    onClick = {
                        viewModel.deleteImage(viewModel.event.value.event_id, imageId)
                        isExpanded = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Download Photo") },
                    onClick = {
                        viewModel.saved.value = viewModel.saveImageFromByteArrayToGallery(context, imageData, imageId)
                        isExpanded = false
                    }
                )
            }
        }
        AsyncImage(
            model = imageRequest,
            contentDescription = "Event image",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .padding(all = 15.dp)
                .padding(bottom = 20.dp)
                .border(width = 1.dp, color = Color.Black)
        )
    }
}
