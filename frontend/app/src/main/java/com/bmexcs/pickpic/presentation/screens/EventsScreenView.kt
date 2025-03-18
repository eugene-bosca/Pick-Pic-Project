package com.bmexcs.pickpic.presentation.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
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
import com.bmexcs.pickpic.presentation.viewmodels.EventsViewModel
import androidx.compose.material3.Icon
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.bmexcs.pickpic.data.models.ImageInfo

private data class ButtonInfo (
    val label: String,
    val icon: Int,
    val onClick: () -> Unit
)

private data class FullscreenImage (
    val request: ImageRequest,
    val data: ByteArray,
    val info: ImageInfo
) {
    fun uuid(): String = info.image.image_id

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FullscreenImage

        if (request != other.request) return false
        if (!data.contentEquals(other.data)) return false
        if (info != other.info) return false

        return true
    }

    override fun hashCode(): Int {
        var result = request.hashCode()
        result = 31 * result + data.contentHashCode()
        result = 31 * result + info.hashCode()
        return result
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventScreenView(
    navController: NavHostController,
    viewModel: EventsViewModel = hiltViewModel(),
) {
    val context = LocalContext.current

    // Current event info
    val eventInfo by viewModel.event.collectAsState()
    val eventId = eventInfo.event_id
    val eventName = eventInfo.event_name // Use event_name instead of name

    // Debugging: Print eventInfo to verify its structure
    LaunchedEffect(eventInfo) {
        println("Event Info: $eventInfo")
    }

    // Images
    val images = viewModel.images.collectAsState().value.toList()
    val isLoading by viewModel.isLoading.collectAsState()

    // Pagination state
    var pageSize by remember { mutableIntStateOf(8) }

    val totalPages = if (images.size % pageSize == 0) {
        images.size / pageSize
    } else {
        images.size / pageSize + 1
    }

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
                        pageSize += 8
                    }
                }
            }
    }

    // Filter button
    val expandFilter = remember { mutableStateOf(false) }
    val filterButtonBox = remember { mutableStateOf(Offset.Zero) }

    // Fullscreen image
    var fullScreenImage by remember { mutableStateOf<FullscreenImage?>(null) }

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
        TopAppBar(
            title = { Text(text = eventName) },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            },
            actions = {
                IconButton(onClick = { viewModel.refresh() }) {
                    Icon(
                        imageVector = Icons.Filled.Refresh,
                        contentDescription = "Refresh"
                    )
                }
            }
        )

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
                        contentPadding = PaddingValues(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        items(images) { (imageInfo, stream) ->
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
                                            info = imageInfo
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
                        // TODO: filter viewModel.getImagesByEventId(viewModel.event.value.event_id)
                        expandFilter.value = !expandFilter.value
                    }
                )
                DropdownMenuItem(
                    text = { Text("Score") },
                    onClick = {
                        // TODO: filter viewModel.getImagesByEventId(viewModel.event.value.event_id)
                        expandFilter.value = !expandFilter.value
                    }
                )
            }
        }

        fullScreenImage?.let { image ->
            ImageFull(
                image = image.request,
                title = image.info.image.user.display_name,
                score = image.info.image.score,
                isDeleteButtonVisible = viewModel.isCurrentUserOwner(eventInfo.owner.user_id) ||
                        viewModel.isUserPhotoUploader(image.info),
                onDismiss = {
                    fullScreenImage = null
                },
                onDelete = {
                    viewModel.deleteImage(
                        viewModel.event.value.event_id,
                        image.uuid()
                    )
                    fullScreenImage = null
                },
                onDownload = {
                    viewModel.saved.value =
                        viewModel.saveImageFromByteArrayToGallery(
                            context, image.data, image.uuid()
                        )
                }
            )
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
                .border(width = 1.dp, color = Color.Black)
        )
    }
}

@Composable
fun ImageFull(
    image: ImageRequest,
    title: String,
    score: Long,
    isDeleteButtonVisible: Boolean,
    onDismiss: () -> Unit,
    onDelete: () -> Unit,
    onDownload: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false, // Allow full-screen width
            dismissOnBackPress = true, // Dismiss on back press
            dismissOnClickOutside = true // Dismiss on outside click
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clickable {
                    onDismiss()
                }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    title,
                    fontSize = 24.sp,
                    color = Color.White,
                )

                Text(
                    "Score: $score",
                    fontSize = 24.sp,
                    color = Color.White,
                )
            }

            AsyncImage(
                model = image,
                contentDescription = "Full Screen Image",
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize().weight(1f)
            )

            Box(
                modifier = Modifier.fillMaxWidth().weight(0.2f)
            ) {
                NavigationBar {
                    if (isDeleteButtonVisible) {
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    painter = painterResource(R.drawable.trash_can),
                                    contentDescription = "Delete Photo"
                                )
                            },
                            label = { Text("Delete Photo", fontSize = 16.sp) },
                            selected = false,
                            onClick = onDelete,
                        )
                    }
                    NavigationBarItem(
                        icon = {
                            Icon(
                                painter = painterResource(R.drawable.download_box),
                                contentDescription = "Download Photo"
                            )
                        },
                        label = { Text("Download Photo", fontSize = 16.sp) },
                        selected = false,
                        onClick = onDownload,
                    )
                }
            }
        }
    }
}
