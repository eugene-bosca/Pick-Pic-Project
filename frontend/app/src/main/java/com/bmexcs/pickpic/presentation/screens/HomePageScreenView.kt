package com.bmexcs.pickpic.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.bmexcs.pickpic.R
import androidx.navigation.NavHostController
import com.bmexcs.pickpic.data.models.EventInfo
import com.bmexcs.pickpic.navigation.Route
import com.bmexcs.pickpic.presentation.viewmodels.EventsViewModel
import com.bmexcs.pickpic.presentation.viewmodels.HomePageViewModel

@Composable
fun HomePageScreenView(
    navController: NavHostController,
    viewModel: HomePageViewModel = hiltViewModel(),
    eventsViewModel: EventsViewModel = hiltViewModel(),
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
    ) {
        Image(
            painter = painterResource(R.drawable.pickpick_logo),
            contentDescription = "PickPic Logo",
            modifier = Modifier.size(1000.dp, 187.5.dp)
        )
        Spacer(modifier = Modifier.height(32.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            JoinEventButton(navController)
            CreateEventButton(navController)
        }
        Spacer(modifier = Modifier.height(32.dp))

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
                items(events) { eventItem: EventInfo ->
                    EventListing(eventsViewModel, eventItem, navController)
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun JoinEventButton(navController: NavHostController) {
    Button(
        onClick = {
            navController.navigate(Route.Event.route)
        }
    ) {
        Icon(
            painter = painterResource(R.drawable.group_add_24px),
            contentDescription = "Join Events Icon",
            modifier = Modifier
                .size(36.dp)
                .padding(end = 8.dp)
        )
        Text("Join Event")
    }
}

@Composable
fun CreateEventButton(navController: NavHostController) {
    Button(
        onClick = {
            navController.navigate(Route.CreateEvent.route)
        }
    ) {
        Icon(
            painter = painterResource(R.drawable.add_circle_24px),
            contentDescription = "Create Event Icon",
            modifier = Modifier
                .size(36.dp)
                .padding(end = 8.dp)
        )
        Text("Create Event")
    }
}

@Composable
fun EventListing(
    eventsViewModel: EventsViewModel,
    eventItem: EventInfo,
    navController: NavHostController
) {
    ElevatedButton(
        onClick = {
            eventsViewModel.setEvent(EventInfo(event_id = eventItem.event_id, event_name = eventItem.event_name))
            navController.navigate(Route.Event.route)
        },
        shape = RoundedCornerShape(16.dp),
    ) {
        ListItem(
            headlineContent = {
                Text(eventItem.event_name)
            },
            supportingContent = {
                Text(eventItem.owner)
            },
            trailingContent = {
                IconButton(onClick = { /* doSomething() */ }) {
                    Icon(Icons.Filled.MoreVert, contentDescription = null)
                }
            },
            colors = ListItemDefaults.colors(
                containerColor = Color.Transparent
            )
        )
    }
}
