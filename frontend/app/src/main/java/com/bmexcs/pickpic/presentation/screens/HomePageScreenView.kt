package com.bmexcs.pickpic.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.bmexcs.pickpic.R
import coil.compose.AsyncImage
import com.bmexcs.pickpic.presentation.viewmodels.HomePageMockViewModel
import androidx.navigation.NavHostController
import com.bmexcs.pickpic.navigation.Event

@Composable
fun HomePageScreenView(
    navController: NavHostController,
    viewModel: HomePageMockViewModel = hiltViewModel()
) {
    val dogImages by viewModel.dogImages.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchDogImages()
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.pickpick_logo),
                contentDescription = "PickPic Logo",
                modifier = Modifier.size(1000.dp, 187.5.dp)
            )
            Spacer(modifier = Modifier.height(33.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ElevatedButton(onClick = { navController.navigate(Event) }) {
                    Icon(
                        painter = painterResource(R.drawable.group_add_24px),
                        contentDescription = "Join Events Icon",
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text("Join Event")
                }
                ElevatedButton(onClick = { navController.navigate(Event) }) {
                    Icon(
                        painter = painterResource(R.drawable.add_circle_24px),
                        contentDescription = "Create Event Icon",
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text("Create Event")
                }
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
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    items(dogImages) { dogUrl ->
                        ListItem(
                            headlineContent = {
                                Text("Fido and Princess' Wedding")
                            },
                            supportingContent = {
                                Text("75 photos uploaded")
                            },
                            trailingContent = {
                                IconButton(onClick = { /* doSomething() */ }) {
                                    Icon(Icons.Filled.MoreVert, contentDescription = null)

                                }
                            },
                            leadingContent = {
                                AsyncImage(
                                    model = dogUrl,
                                    contentDescription = "Dog image",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .border(
                                            2.dp,
                                            MaterialTheme.colorScheme.primary,
                                            CircleShape
                                        )
                                )
                            }
                        )
                        HorizontalDivider()
                    }
                }
            }
        }
    }

}
