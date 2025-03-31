package com.bmexcs.pickpic.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.bmexcs.pickpic.R
import androidx.navigation.NavHostController
import com.bmexcs.pickpic.data.models.EventMetadata
import com.bmexcs.pickpic.data.models.UserMetadata
import com.bmexcs.pickpic.navigation.Route
import com.bmexcs.pickpic.presentation.viewmodels.HomePageViewModel

@Composable
fun HomePageScreenView(
    navController: NavHostController,
    viewModel: HomePageViewModel = hiltViewModel(),
) {
    val events by viewModel.events.collectAsState()
    val userMetadata by viewModel.userMetadata.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchEvents()
        viewModel.fetchUserMetadata()
    }

    val ownedEventsCount = events.count { it.owner.id == userMetadata?.id }
    val subscribedEventsCount = events.count { it.owner.id != userMetadata?.id }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        UserProfileCard(userMetadata, ownedEventsCount, subscribedEventsCount, onClick = {navController.navigate(Route.Profile.route)})

        Spacer(modifier = Modifier.height(16.dp))

        InvitesButton(navController)

        Spacer(modifier = Modifier.height(16.dp))

        when {
            isLoading && events.isEmpty() -> {
                CircularProgressIndicator()
            }

            events.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No Events Found")
                }
            }

            errorMessage != null -> {
                Box(
                    modifier = Modifier.fillMaxWidth().weight(1f),
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
                        EventListing(
                            isOwner = viewModel.isCurrentUserOwner(eventItem.owner.id),
                            eventItem = eventItem,
                            onEnter = {
                                viewModel.setEvent(eventItem)
                                navController.navigate(Route.Event.route)
                            },
                            onDelete = {
                                viewModel.deleteEvent(eventItem.id)
                            },
                            onLeave = {
                                viewModel.leaveInvitedEvent(eventItem.id)
                            }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }

        // Create Event button.
        FloatingActionButton(
            onClick = { navController.navigate(Route.CreateEvent.route) },
            modifier = Modifier
                .align(Alignment.End)
                .padding(16.dp),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ) {
            Icon(
                painter = painterResource(R.drawable.add_circle_24px),
                modifier = Modifier.size(36.dp),
                contentDescription = "Create Event"
            )
        }
    }
}

@Composable
fun InvitesButton(navController: NavHostController) {
    Button(
        onClick = {
            navController.navigate(Route.EventInvitation.route)
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Mail,
            contentDescription = "Join Events Icon",
            modifier = Modifier
                .size(36.dp)
                .padding(end = 8.dp)
        )
        Text(" Pending Invites")
    }
}

@Composable
fun EventListing(
    isOwner: Boolean,
    eventItem: EventMetadata,
    onEnter: () -> Unit,
    onDelete: () -> Unit,

    // callback function for when user is not owner but would like to leave
    onLeave: () -> Unit
) {
    val expandFilter = remember { mutableStateOf(false) }

    ElevatedButton(
        onClick = onEnter,
        shape = RoundedCornerShape(16.dp),
    ) {
        ListItem(
            headlineContent = {
                Text(eventItem.name)
            },
            supportingContent = {
                Text(text = "Host: ${eventItem.owner.name}")
            },
            trailingContent = {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isOwner) {
                        IconButton(onClick = { expandFilter.value = !expandFilter.value }) {
                            Icon(Icons.Filled.MoreVert, contentDescription = null)

                            DropdownMenu(
                                expanded = expandFilter.value,
                                onDismissRequest = {
                                    expandFilter.value = !expandFilter.value
                                }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Delete") },
                                    onClick = {
                                        onDelete()
                                        expandFilter.value = false
                                    }
                                )
                            }
                        }

                        Box(modifier = Modifier.size(24.dp)) {
                            Icon(
                                painter = painterResource(R.drawable.ic_crown),
                                contentDescription = "Owner",
                                tint = Color(0xFFD4AF37),
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    } else {
                        IconButton(onClick = { expandFilter.value = !expandFilter.value }) {
                            Icon(Icons.Filled.MoreVert, contentDescription = null)

                            DropdownMenu(
                                expanded = expandFilter.value,
                                onDismissRequest = {
                                    expandFilter.value = !expandFilter.value
                                }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Leave") },
                                    onClick = {
                                        onLeave()
                                        expandFilter.value = false
                                    }
                                )
                            }
                        }
                        Spacer(modifier = Modifier.size(24.dp))
                    }
                }

            },
            colors = ListItemDefaults.colors(
                containerColor = Color.Transparent
            )
        )
    }
}

@Composable
fun UserProfileCard(userMetadata: UserMetadata?, ownedEventsCount: Int, subscribedEventsCount: Int, onClick: () -> Unit = {}) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(160.dp)
            .clickable(
                onClick = onClick
            ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF7F2FA)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp), // Increased padding for better spacing
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Enlarged Circular Profile Image
            Image(
                painter = painterResource(id = R.drawable.penguin_logo), // Replace with actual user profile image if available
                contentDescription = "User Profile Picture",
                contentScale = ContentScale.Crop, // Ensures the image fills the circle
                modifier = Modifier
                    .size(96.dp) // Bigger logo
                    .clip(CircleShape) // Makes it fully circular
                    .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape) // Optional border for styling
            )

            Spacer(modifier = Modifier.width(24.dp))

            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = userMetadata?.name ?: "Guest",
                    style = MaterialTheme.typography.headlineSmall, // Larger font for name
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$ownedEventsCount",
                            style = MaterialTheme.typography.headlineLarge,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Text(
                            text = "Owned",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$subscribedEventsCount",
                            style = MaterialTheme.typography.headlineLarge,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Text(
                            text = "Subscribed",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
