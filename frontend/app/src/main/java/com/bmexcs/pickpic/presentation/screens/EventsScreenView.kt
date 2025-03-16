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
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material.icons.filled.QrCode

@Composable
fun EventScreenView(
    navController: NavHostController,
    viewModel: EventsViewModel = hiltViewModel(),
) {
    val images = viewModel.images.collectAsState().value.toList()
    val isLoading by viewModel.isLoading.collectAsState()
    val context = LocalContext.current

    val expandFilter = remember { mutableStateOf( false ) }

    // Pagination state
    val pageSize = 10
    var currentPage by remember { mutableStateOf(0) }

    val totalPages = if (images.size % pageSize == 0) {
        images.size / pageSize
    } else {
        images.size / pageSize + 1
    }

    val imagesToDisplay = images
        .drop(currentPage * pageSize) // Skip images for previous pages
        .take(pageSize) // Take only `pageSize` images for the current page

    var fullScreenImage by remember { mutableStateOf<ImageRequest?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.uriToByteArray(context, uri)?.let { byteArray ->
                viewModel.addImage(byteArray)
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
    ) {
        Row {
            AddPhotosButton(onClick = { launcher.launch("image/*") })
            RankPhotosButton(onClick = { navController.navigate(Route.Ranking.route) })
        }

        Spacer(modifier = Modifier.height(16.dp))

        val eventInfo by viewModel.event.collectAsState()
        val eventId = eventInfo.event_id // Retrieve the event_id

        Row {
            InviteFriendsButton(onClick = {
                navController.navigate("invite/$eventId")
            })

            QRLinkInviteButton(onClick = {
                navController.navigate("qrInviteView/$eventId")
            })
        }

        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .padding(start = 300.dp)
        ) {
            ElevatedButton(
                onClick = {
                    expandFilter.value = !expandFilter.value
                },
                modifier = Modifier
                    .width(100.dp)

            ) {
                Text("Filter")
                Icon(
                    painterResource(id = R.drawable.filter),
                    contentDescription = "More options",
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
            DropdownMenu(
                expanded = expandFilter.value,
                onDismissRequest = {
                    expandFilter.value = !expandFilter.value
                }
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

        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            images.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Empty event. Click Add Photos to get started!")
                }
            }
            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f), // This ensures the grid takes up available space
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
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
                                    imageData = it,
                                    imageRequest = imageRequest,
                                    imageId = imageId,
                                    onClick = { fullScreenImage = imageRequest },
                                    viewModel = viewModel
                                )
                            }
                    }
                }

            }
        }

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
fun AddPhotosButton(onClick: () -> Unit) {
    Button(
        modifier = Modifier.padding(horizontal = 20.dp),
        onClick = onClick,
    ) {
        Icon(
            painter = painterResource(R.drawable.add_circle_24px),
            contentDescription = "Add Photos",
            modifier = Modifier.padding(end = 8.dp)
        )
        Text("Add Photos")
    }
}

@Composable
fun RankPhotosButton(onClick: () -> Unit) {
    Button(onClick) {
        Icon(
            painter = painterResource(R.drawable.podium),
            contentDescription = "Rank Photos Icon",
            modifier = Modifier.padding(end = 8.dp)
        )
        Text("Rank Photos")
    }
}

@Composable
fun InviteFriendsButton(onClick: () -> Unit) {
    Button(
        modifier = Modifier.padding(horizontal = 20.dp),
        onClick = onClick,
    ) {
        Icon(
            painter = painterResource(R.drawable.podium),
            contentDescription = "Invite Friends",
            modifier = Modifier.padding(end = 8.dp)
        )
        Text("Invite Friends")
    }
}

@Composable
fun QRLinkInviteButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
    ) {
        Icon(
            imageVector = Icons.Filled.QrCode,
            contentDescription = "QR or Link Invite",
            modifier = Modifier.padding(end = 8.dp)
        )
        Text("QR or Link")
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
