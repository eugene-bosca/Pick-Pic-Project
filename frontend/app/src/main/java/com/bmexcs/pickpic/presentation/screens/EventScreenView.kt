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
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
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
import com.bmexcs.pickpic.data.models.ImageMetadata
import com.bmexcs.pickpic.presentation.viewmodels.FilterType
import kotlinx.coroutines.launch

private data class ButtonInfo (
    val label: String,
    val icon: Int,
    val onClick: () -> Unit
)

private data class FullImage (
    val request: ImageRequest,
    val data: ByteArray,
    val metadata: ImageMetadata
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventScreenView(
    navController: NavHostController,
    viewModel: EventsViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Current event info
    val event by viewModel.event.collectAsState()
    val eventId = event.id
    val eventName = event.name

    // Images
    val images by viewModel.images.collectAsState()
    val imageList = images.toList()

    val isLoading by viewModel.isLoading.collectAsState()

    // Pagination state
    var pageSize by remember { mutableIntStateOf(8) }

    val filterType by viewModel.filterType.collectAsState()

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

    // Fullscreen image
    var fullImage by remember { mutableStateOf<FullImage?>(null) }

    // Observe the snackbar message
    val snackbarMessage by viewModel.snackbarMessage.collectAsState()
    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let { message ->
            scope.launch {
                snackbarHostState.showSnackbar(message)
                viewModel.clearSnackbarMessage()
            }
        }
    }

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
            onClick = { navController.navigate("invite/$eventId/${event.owner.id}") }

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

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }, // Add SnackbarHost here
        topBar = {
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
                    // Refresh button
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(
                            imageVector = Icons.Filled.Refresh,
                            contentDescription = "Refresh"
                        )
                    }
                    // Download album button
                    IconButton(onClick = { viewModel.downloadAlbum(context, imageList) }) {
                        Icon(
                            painter = painterResource(R.drawable.download),
                            contentDescription = "Download Album"
                        )
                    }
                }
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
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
                                contentPadding = PaddingValues(8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                            ) {
                                items(images.entries.toList()) { (imageMetadata, stream) ->
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
                                                fullImage = FullImage(
                                                    request = imageRequest,
                                                    data = it,
                                                    metadata = imageMetadata
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

                FilterOptionsDropdown(
                    isExpanded = expandFilter.value,
                    onDismiss = {
                        expandFilter.value = !expandFilter.value
                    },
                    onFilterByDate = {
                        if (viewModel.filterType.value == FilterType.FilterDateDesc) {
                            viewModel.filterType.value = FilterType.FilterDateAsc
                        } else {
                            viewModel.filterType.value = FilterType.FilterDateDesc
                        }
                        viewModel.refresh()
                        // TODO: filter viewModel.getImagesByEventId(viewModel.event.value.event_id)
                        expandFilter.value = !expandFilter.value
                    },
                    onFilterByScore = {
                        if (viewModel.filterType.value == FilterType.FilterRankDesc) {
                            viewModel.filterType.value = FilterType.FilterRankAsc
                        } else {
                            viewModel.filterType.value = FilterType.FilterRankDesc
                        }
                        viewModel.refresh()
                        // TODO: filter viewModel.getImagesByEventId(viewModel.event.value.event_id)
                        expandFilter.value = !expandFilter.value
                    },
                    filter = filterType
                )

                fullImage?.let { image ->
                    ImageFull(
                        image = image.request,
                        title = image.metadata.uploader.name,
                        score = image.metadata.score,
                        isDeleteButtonVisible = viewModel.isCurrentUserOwner(event.owner.id) ||
                                viewModel.isUserPhotoUploader(image.metadata),
                        onDismiss = {
                            fullImage = null
                        },
                        onDelete = {
                            viewModel.deleteImage(
                                viewModel.event.value.id,
                                image.metadata.id
                            )
                            fullImage = null
                        },
                        onDownload = {
                            viewModel.saved.value =
                                viewModel.saveImageFromByteArrayToGallery(
                                    context, image.data, image.metadata.id
                                )
                        }
                    )
                }
            }
        }
    )
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

@Composable
fun FilterOptionsDropdown(
    isExpanded: Boolean,
    onDismiss: () -> Unit,
    onFilterByDate: () -> Unit,
    onFilterByScore: () -> Unit,
    filter: FilterType
) {
    val filterButtonBox = remember { mutableStateOf(Offset.Zero) }

    Box(
        modifier = Modifier.onGloballyPositioned { coordinates ->
            filterButtonBox.value = coordinates.localToWindow(Offset.Zero)
        }
    ) {
        DropdownMenu(
            expanded = isExpanded,
            onDismissRequest = onDismiss,
            offset = DpOffset(100.dp, 0.dp)
        ) {
            // Date Filter Option
            DropdownMenuItem(
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Show the correct arrow for the Date filter
                        when (filter) {
                            FilterType.FilterDateAsc -> Icon(
                                imageVector = Icons.Default.ArrowUpward,
                                contentDescription = "Ascending"
                            )
                            FilterType.FilterDateDesc -> Icon(
                                imageVector = Icons.Default.ArrowDownward,
                                contentDescription = "Descending"
                            )
                            else -> {} // No icon for other filter types
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Date")
                    }
                },
                onClick = onFilterByDate
            )
            DropdownMenuItem(
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Show the correct arrow for the Score filter
                        when (filter) {
                            FilterType.FilterRankAsc -> Icon(
                                imageVector = Icons.Default.ArrowUpward,
                                contentDescription = "Ascending"
                            )
                            FilterType.FilterRankDesc -> Icon(
                                imageVector = Icons.Default.ArrowDownward,
                                contentDescription = "Descending"
                            )
                            else -> {} // No icon for other filter types
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Score")
                    }
                },
                onClick = onFilterByScore
            )
        }
    }
}

@Composable
fun FilterIconUpDown(filterType: FilterType) {
    if (filterType == FilterType.FilterDateDesc || filterType == FilterType.FilterRankDesc) {
       Icon(
           imageVector = Icons.Default.ArrowDownward,
           contentDescription = "Descending"
       )
    } else if (filterType == FilterType.FilterDateAsc || filterType == FilterType.FilterRankAsc) {
        Icon(
            imageVector = Icons.Default.ArrowUpward,
            contentDescription = "Ascending"
        )
    }
}
