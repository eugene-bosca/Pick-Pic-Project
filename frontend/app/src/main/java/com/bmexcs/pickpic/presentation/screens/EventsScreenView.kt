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

private data class ButtonInfo (
    val label: String,
    val icon: Int,
    val onClick: () -> Unit
)

private data class FullscreenImage (
    val request: ImageRequest,
    val data: ByteArray,
    val id: String
)

@Composable
fun EventScreenView(
    navController: NavHostController,
    viewModel: EventsViewModel = hiltViewModel(),
) {
    val context = LocalContext.current

    val images = viewModel.images.collectAsState().value.toList()
    val isLoading by viewModel.isLoading.collectAsState()

    var fullScreenImage by remember { mutableStateOf<FullscreenImage?>(null) }

    val eventInfo by viewModel.event.collectAsState()
    val eventId = eventInfo.event_id

    val expandFilter = remember { mutableStateOf( false ) }

    // Pagination state
    val pageSize = 10
    var currentPage by remember { mutableIntStateOf(0) }

    val totalPages = if (images.size % pageSize == 0) {
        images.size / pageSize
    } else {
        images.size / pageSize + 1
    }

    val imagesToDisplay = images
        .drop(currentPage * pageSize) // Skip images for previous pages
        .take(pageSize) // Take only `pageSize` images for the current page

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.uriToByteArray(context, uri)?.let { byteArray ->
                viewModel.addImage(byteArray)
            }
        }
    }

    val buttons = listOf(
        ButtonInfo(
            "Invite",
            R.drawable.group_add_24px,
            onClick = { navController.navigate("invite/$eventId") }
        ),
        ButtonInfo(
            "Filter",
            R.drawable.filter,
            onClick = { expandFilter.value = !expandFilter.value }
        ),
        ButtonInfo(
            "Upload",
            R.drawable.image,
            onClick = { launcher.launch("image/*") }
        ),
        ButtonInfo(
            "Rank",
            R.drawable.podium,
            onClick = { navController.navigate(Route.Ranking.route) }
        )
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
    ) {
        // Pagination controls directly below the grid
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ElevatedButton(
                onClick = {
                    if (currentPage > 0) {
                        currentPage -= 1
                    }
                },
                enabled = currentPage > 0
            ) {
                Text("Previous")
            }

            ElevatedButton(
                onClick = {
                    if (currentPage < totalPages - 1) {
                        currentPage += 1
                    }
                },
                enabled = currentPage < totalPages - 1
            ) {
                Text("Next")
            }
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator()
                }

                imagesToDisplay.isEmpty() -> {
                    Text("Empty event. Click Add Photos to get started!")
                }

                else -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        itemsIndexed(imagesToDisplay) { _, (imageId, stream) ->
                            stream?.let {
                                val imageRequest = ImageRequest.Builder(context)
                                    .data(it)
                                    .memoryCachePolicy(CachePolicy.ENABLED)
                                    .diskCachePolicy(CachePolicy.ENABLED)
                                    .crossfade(true)
                                    .build()

                                ImageTile(
                                    imageRequest = imageRequest,
                                    onClick = {
                                        fullScreenImage = FullscreenImage(
                                            request = imageRequest,
                                            data = it,
                                            id = imageId
                                        )
                                    },
                                )
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

        fullScreenImage?.let { image ->
            ImageFull(
                image = image.request,
                onDismiss = {
                    fullScreenImage = null
                }
            ) {
                NavigationBar {
                    NavigationBarItem(
                        icon = {
                            Icon(
                                painter = painterResource(R.drawable.trash_can),
                                contentDescription = "Delete Photo"
                            )
                        },
                        label = { Text("Delete Photo", fontSize = 16.sp) },
                        selected = false,
                        onClick = {
                            image.id.let {
                                viewModel.deleteImage(
                                    viewModel.event.value.event_id,
                                    it
                                )
                                fullScreenImage = null
                            }
                        },
                    )
                    NavigationBarItem(
                        icon = {
                            Icon(
                                painter = painterResource(R.drawable.download_box),
                                contentDescription = "Download Photo"
                            )
                        },
                        label = { Text("Download Photo", fontSize = 16.sp) },
                        selected = false,
                        onClick = {
                            viewModel.saved.value =
                                viewModel.saveImageFromByteArrayToGallery(
                                    context, image.data, image.id
                                )
                        },
                    )
                }
            }
        }
    }
}

@Composable
fun ImageTile(imageRequest: ImageRequest, onClick: () -> Unit) {
    ElevatedCard(
        modifier = Modifier
            .size(width = 150.dp, height = 225.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp)
    ) {
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
