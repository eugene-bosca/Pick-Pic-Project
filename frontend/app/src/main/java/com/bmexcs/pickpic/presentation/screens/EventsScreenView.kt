package com.bmexcs.pickpic.presentation.screens

import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
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
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material.icons.filled.QrCode


@Composable
fun EventScreenView(
    navController: NavHostController,
    viewModel: EventsViewModel = hiltViewModel(),
) {
    val images by viewModel.images.collectAsState()
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            // Use the context and uri to convert the image to byte array
            val byteArray = viewModel.uriToByteArray(context, uri)
            if(byteArray != null) {
                viewModel.addImage(byteArray)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ElevatedButton(
                onClick = { launcher.launch("image/*") },
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    painter = painterResource(R.drawable.add_circle_24px),
                    contentDescription = "Add Photos Icon",
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

        ElevatedButton(
            onClick = { navController.navigate(Route.QrInviteView.route) },
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
