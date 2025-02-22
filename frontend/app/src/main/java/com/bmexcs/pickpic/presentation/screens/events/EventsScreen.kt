package com.bmexcs.pickpic.presentation.screens.events

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.bmexcs.pickpic.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventScreen(
    onClickHomePage: () -> Unit,
    onClickProfile: () -> Unit,
    onClickSupport: () -> Unit,
    onClickEvent: () -> Unit,
    onClickRanking: () -> Unit,
    viewModel: EventsViewModel = hiltViewModel()
) {

    val dogImages by viewModel.dogImages.collectAsState()

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.Top,
        ){
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = { /* doSomething() */ }) {
                        Icon(Icons.Filled.Menu, contentDescription = null)
                    }
                },
                actions = {
                    IconButton(onClick = { /* doSomething() */ }) {
                        Icon(Icons.Filled.MoreVert, contentDescription = null)
                    }
                }
            )
            ElevatedButton(onClick = onClickRanking) {
                Icon(
                    painter = painterResource(R.drawable.podium),
                    contentDescription = "Rank Photos Icon",
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text("Rank Photos")
            }
            Spacer(modifier = Modifier.height(33.dp))

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
                    columns = GridCells.Fixed(2), // Ensure two images per row
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp), // Space between images
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(dogImages) { dogUrl ->
                        ElevatedCard (
                            modifier = Modifier
                                .size(width = 150.dp, height = 225.dp)
                                .border(width = 1.dp, color = Color.Black)
                        ) {
                            AsyncImage(
                                model = dogUrl,
                                contentDescription = "Dog image",
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
