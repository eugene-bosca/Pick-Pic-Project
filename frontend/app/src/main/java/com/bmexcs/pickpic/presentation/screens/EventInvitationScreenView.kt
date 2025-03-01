package com.bmexcs.pickpic.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bmexcs.pickpic.presentation.viewmodels.EventInvitationViewModel

@Composable
fun EventInvitationScreenView(
    eventName: String,
    eventID: String,
    onNavigateAway: () -> Unit,
    viewModel: EventInvitationViewModel = hiltViewModel()
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "You've been invited to $eventName's event!",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(onClick = {
                    viewModel.acceptEvent(eventID)
                    onNavigateAway()
                }) {
                    Text("Accept")
                }
                Button(onClick = onNavigateAway) { // Simplified onDecline
                    Text("Decline")
                }
            }
        }
    }
}