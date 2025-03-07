package com.bmexcs.pickpic.presentation.screens

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.bmexcs.pickpic.R
import com.bmexcs.pickpic.navigation.Route
import com.bmexcs.pickpic.presentation.viewmodels.EventsViewModel

@Composable
fun EventScreenView(
    navController: NavHostController,
    viewModel: EventsViewModel = hiltViewModel(),
) {

    viewModel.initializeEventsScreenView()
    val images by viewModel.images.collectAsState()
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            // Use the context and uri to convert the image to byte array
            val byteArray = viewModel.uriToByteArray(context, uri)
            if(byteArray != null) {
                viewModel.addImageByEvent(byteArray)
            }
        }
    }

    Column (
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
    ){
        Row(
        ) {
            ElevatedButton(
                modifier = Modifier.padding(horizontal = 20.dp),
                onClick = {launcher.launch("image/*")},
            ) {
                Icon(
                    painter = painterResource(R.drawable.add_circle_24px),
                    contentDescription = "Rank Photos Icon",
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text("Add Photos")
            }
            ElevatedButton(onClick = {navController.navigate(Route.Ranking.route)}) {
                Icon(
                    painter = painterResource(R.drawable.podium),
                    contentDescription = "Rank Photos Icon",
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text("Rank Photos")
            }
        }

        Spacer(modifier = Modifier.height(33.dp))
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
                    horizontalArrangement = Arrangement.spacedBy(16.dp), // Space between images
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(images) { stream ->
                        Log.d("Stream", stream.contentToString())
                        ElevatedCard (
                            modifier = Modifier
                                .size(width = 150.dp, height = 225.dp)
                                .border(width = 1.dp, color = Color.Black)
                        ) {

                            if(stream != null) {
                                val bitmap = BitmapFactory.decodeByteArray(stream, 0, stream.size)

                                if(bitmap != null) {
                                    Image(
                                        bitmap = bitmap.asImageBitmap(),
                                        contentDescription = "Event image",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(all = 15.dp)
                                            .padding(bottom = 20.dp)
                                            .border(width = 1.dp, color = Color.Black)
                                    )
                                }
                            } else {
                                Box(
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

