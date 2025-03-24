package com.bmexcs.pickpic.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bmexcs.pickpic.presentation.viewmodels.InvitedViewModel

@Composable
fun InvitedQRView(
    viewModel: InvitedViewModel = hiltViewModel(),
    eventId: String
) {
    val context = LocalContext.current

    val isLoading = viewModel.isLoading.collectAsState().value
    val event = viewModel.event.collectAsState().value
    val images = viewModel.images.collectAsState().value
    val owner = viewModel.owner.collectAsState().value

    // Fetch event metadata when the composable is first launched.
    LaunchedEffect(Unit) {
        viewModel.setEventId(eventId)
        viewModel.checkUserLoggedIn(context)
        viewModel.getEvent(eventId)
        viewModel.getAllImagesMetadata(eventId)
    }

    // Fetch event owner metadata only after event is available.
    LaunchedEffect(event) {
        if (event != null) {
            viewModel.getEventOwnerMetadata(event.owner.id)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isLoading) {
            CircularProgressIndicator()
        } else {
            if (event != null) {
                Text(
                    text = "You've been invited to an album!",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(bottom = 8.dp),
                    textAlign = TextAlign.Center
                )
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                    ) {
                        DisplayEntry("Event Name", event.name)
                        DisplayEntry("Owner Name", owner?.name ?: "Unknown")
                        DisplayEntry("Email", owner?.email ?: "Unknown")
                        DisplayEntry("Images", images.count().toString())
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = { viewModel.handleAcceptDirectInvitation(context) },
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 8.dp)
                        ) {
                            Text("Accept")
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = {
                                viewModel.handleDeclineInvite(context)
                            },
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 8.dp)
                        ) {
                            Text("Decline")
                        }
                    }
                }
            } else {
                Text(
                    text = "Failed to load event details.",
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun DisplayEntry(title: String, value: String) {
    Text(
        text = title,
        fontSize = 14.sp,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
    )

    Text(
        text = value,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}
