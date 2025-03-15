package com.bmexcs.pickpic.presentation.screens

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material.icons.filled.QrCode


@Composable
fun EventScreenView(
    navController: NavHostController,
    viewModel: EventsViewModel = hiltViewModel(),
) {
    val images = viewModel.images.collectAsState().value.toList()
    val context = LocalContext.current

    val expandedState = remember { mutableStateOf(mutableMapOf<Int, Boolean>()) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            // Use the context and uri to convert the image to byte array
            val byteArray = viewModel.uriToByteArray(context, uri)
            if (byteArray != null) {
                viewModel.addImage(byteArray)
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
    ) {
        Row {
            ElevatedButton(
                modifier = Modifier.padding(horizontal = 20.dp),
                onClick = { launcher.launch("image/*") },
            ) {
                Icon(
                    painter = painterResource(R.drawable.add_circle_24px),
                    contentDescription = "Rank Photos Icon",
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text("Add Photos")
            }

            Spacer(modifier = Modifier.width(16.dp))

            ElevatedButton(
                onClick = { navController.navigate(Route.Ranking.route) },
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    painter = painterResource(R.drawable.podium),
                    contentDescription = "Rank Photos Icon",
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text("Rank Photos")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        val eventInfo by viewModel.event.collectAsState()
        val eventId = eventInfo.event_id // Retrieve the event_id

        ElevatedButton(
            onClick = {
                navController.navigate("qrInviteView/$eventId")
            },
            modifier = Modifier.fillMaxWidth(0.6f)
        ) {
            Icon(
                imageVector = Icons.Filled.QrCode,
                contentDescription = "QR or Link Invite",
                modifier = Modifier.padding(end = 8.dp)
            )
            Text("QR or Link Invite")
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (images.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                itemsIndexed(images) { index, (image_id, stream) ->
                    ElevatedCard(
                        modifier = Modifier
                            .size(width = 150.dp, height = 225.dp)
                            .border(width = 1.dp, color = Color.Black)
                    ) {

                        if (stream != null) {
                            val imageRequest = ImageRequest.Builder(context)
                                .data(stream)  // For loading from a ByteArray or other data source
                                .placeholder(null)
                                .memoryCachePolicy(CachePolicy.ENABLED) // Use in-memory cache
                                .diskCachePolicy(CachePolicy.ENABLED)   // Use disk cache
                                .crossfade(true) // Optional: for smooth fade-in transition
                                .build()

                            val isExpanded = expandedState.value[index] ?: false
                            Box(
                                modifier = Modifier
                                    .padding(start = 140.dp)
                            ) {
                                IconButton(
                                    onClick = {
                                        expandedState.value =
                                            expandedState.value.toMutableMap().apply {
                                                put(index, !isExpanded)
                                            }
                                    },
                                    modifier = Modifier
                                        .size(30.dp)
                                ) {
                                    Icon(
                                        painterResource(id = R.drawable.more_horizontal),
                                        contentDescription = "More options"
                                    )
                                }
                                DropdownMenu(
                                    expanded = isExpanded,
                                    onDismissRequest = {
                                        expandedState.value =
                                            expandedState.value.toMutableMap().apply {
                                                put(index, false)
                                            }
                                    }
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("Delete Photo") },
                                        onClick = {
                                            viewModel.deleteImage(viewModel.event.value.event_id, image_id)
                                            expandedState.value =
                                                expandedState.value.toMutableMap().apply {
                                                    put(index, false)
                                                }
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Download Photo") },
                                        onClick = {
                                            viewModel.saved.value = viewModel.saveImageFromByteArrayToGallery(context, stream, image_id)
                                            expandedState.value =
                                                expandedState.value.toMutableMap().apply {
                                                    put(index, false)
                                                }
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
                }
            }
        }
    }
}
