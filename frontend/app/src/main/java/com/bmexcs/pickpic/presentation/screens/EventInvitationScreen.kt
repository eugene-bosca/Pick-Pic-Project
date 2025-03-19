package com.bmexcs.pickpic.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.bmexcs.pickpic.data.models.EventMetadata
import com.bmexcs.pickpic.presentation.viewmodels.EventInvitationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventInvitationScreenView(
    navController: NavHostController,
    viewModel: EventInvitationViewModel = hiltViewModel()
) {
    val events by viewModel.events.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadEvents()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Event Invitations") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when {
                isLoading && events.isNotEmpty() -> {
                    CircularProgressIndicator()
                }

                events.isEmpty() -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No Events Found")
                    }
                }

                errorMessage != null -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            errorMessage ?: "An unknown error occurred",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentPadding = PaddingValues(horizontal = 16.dp)
                    ) {
                        items(events) { eventItem: EventMetadata ->
                            ListItem(
                                headlineContent = {
                                    Text(eventItem.name)
                                },
                                supportingContent = {
                                    Text("Event Owner: ${eventItem.owner.name}")
                                },
                                trailingContent = {
                                    Row {
                                        IconButton(onClick = { viewModel.acceptEvent(eventItem.id) }) {
                                            Icon(
                                                Icons.Filled.Check,
                                                contentDescription = null,
                                                tint = Color.Green
                                            )
                                        }
                                        IconButton(onClick = { viewModel.declineEvent(eventItem.id) }) {
                                            Icon(
                                                Icons.Filled.Close,
                                                contentDescription = null,
                                                tint = Color.Red
                                            )
                                        }
                                    }
                                }
                            )
                            HorizontalDivider()
                        }
                    }
                }
            }
        }
    }
}
