package com.bmexcs.pickpic.presentation.screens

import ImageFull
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.bmexcs.pickpic.presentation.shared.ImageTile
import com.bmexcs.pickpic.presentation.viewmodels.EventsViewModel

@Composable
fun EventScreenView(
    navController: NavHostController,
    viewModel: EventsViewModel = hiltViewModel()
) {

    val dogImages by viewModel.dogImages.collectAsState()
    var fullScreenImageUrl by remember { mutableStateOf<String?>(null) }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.Top,
        ){
            if (dogImages.isEmpty()) {
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
                    items(dogImages) { dogUrl ->
                        ImageTile(dogUrl,onClick = { fullScreenImageUrl = dogUrl })
                    }
                }
            }
        }
        ImageFull(
            imageUrl = fullScreenImageUrl,
            onDismiss = { fullScreenImageUrl = null } // Reset the state to dismiss the dialog
        )
    }
}