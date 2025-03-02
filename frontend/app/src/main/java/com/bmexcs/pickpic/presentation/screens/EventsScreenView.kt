package com.bmexcs.pickpic.presentation.screens

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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.bmexcs.pickpic.R
import com.bmexcs.pickpic.navigation.Route
import com.bmexcs.pickpic.presentation.shared.ImageFull
import com.bmexcs.pickpic.presentation.viewmodels.EventsViewModel

@Composable
fun EventScreenView(
    navController: NavHostController,
    viewModel: EventsViewModel = hiltViewModel(),
) {

    val images by viewModel.images.collectAsState()
    var fullScreenImageUrl by remember { mutableStateOf<String?>(null) }

    Column (
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
    ){
        ElevatedButton(onClick = {navController.navigate(Route.Ranking.route)}) {
            Icon(
                painter = painterResource(R.drawable.podium),
                contentDescription = "Rank Photos Icon",
                modifier = Modifier.padding(end = 8.dp)
            )
            Text("Rank Photos")
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
                    items(images) { bitmap ->
                        ElevatedCard (
                            modifier = Modifier
                                .size(width = 150.dp, height = 225.dp)
                                .border(width = 1.dp, color = Color.Black)
                        ) {
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
    ImageFull(
        imageUrl = fullScreenImageUrl,
        onDismiss = { fullScreenImageUrl = null } // Reset the state to dismiss the dialog
    )
}
