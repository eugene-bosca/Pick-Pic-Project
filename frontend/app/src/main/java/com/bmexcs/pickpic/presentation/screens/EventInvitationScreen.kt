package com.bmexcs.pickpic.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
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
import com.bmexcs.pickpic.data.models.EventUser
import com.bmexcs.pickpic.presentation.viewmodels.EventInvitationViewModel

@Composable
fun EventInvitationScreenView(
    navController: NavHostController,
    viewModel: EventInvitationViewModel = hiltViewModel()
) {
    val events by viewModel.events.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchEvents()
    }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        if (events.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text("No Events Found")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                items(events) { eventItem: EventUser ->
                    ListItem(
                        headlineContent = {
                            Text(eventItem.event.event_name)
                        },
                        supportingContent = {
                            Text("Event Owner: ${eventItem.user.display_name}")
                        },
                        trailingContent = {
                            Row {
                                IconButton(onClick = { viewModel.acceptEvent(eventItem.event.event_id) }) {
                                    Icon(Icons.Filled.Check,
                                        contentDescription = null,
                                        tint = Color.Green
                                    )
                                }
                                IconButton(onClick = { viewModel.declineEvent(eventItem.event.event_id) }) {
                                    Icon(Icons.Filled.Close,
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
