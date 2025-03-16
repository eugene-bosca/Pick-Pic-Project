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

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.uriToByteArray(context, uri)?.let { byteArray ->
                viewModel.addImage(byteArray)
            }
        }
    }

    data class ButtonInfo (
        val label: String,
        val icon: Int,
        val onClick: () -> Unit
    )

    val buttons = listOf(
        ButtonInfo(
            "Invite",
            R.drawable.group_add_24px,
            onClick = { navController.navigate("invite/$eventId") }
        ),
        ButtonInfo(
            "QR Code",
            R.drawable.qrcode_plus,
            onClick = { navController.navigate("qrInviteView/$eventId") }
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
        Box( // Wrap the image content inside a Box with weight(1f)
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator()
                }

                images.isEmpty() -> {
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
    Button(onClick) {
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
        modifier = Modifier.fillMaxWidth(0.6f)
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
