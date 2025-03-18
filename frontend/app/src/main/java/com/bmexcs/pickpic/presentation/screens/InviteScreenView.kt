package com.bmexcs.pickpic.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.bmexcs.pickpic.data.models.InvitedUser
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.ui.draw.alpha
import com.bmexcs.pickpic.presentation.viewmodels.InviteViewModel

@Composable
fun InviteScreenView(
    navController: NavHostController,
    eventId: String,
    ownerId: String,
    viewModel: InviteViewModel = hiltViewModel()
) {
    // Load invited users when the screen is first displayed
    LaunchedEffect(eventId) {
        viewModel.loadInvitedUsers(eventId)
    }

    val invitedUsers by viewModel.invitedUsers.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    val isEventOwner = viewModel.isCurrentUserOwner(ownerId)

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        // Error message if any
        error?.let {
            Text(
                text = it,
                color = Color.Red,
                modifier = Modifier.padding(8.dp)
            )
        }

        // Loading indicator
        if (isLoading) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        // Email input field
        EditableEmailField(
            eventId = eventId,
            viewModel = viewModel
        )

        Spacer(modifier = Modifier.height(16.dp))

        // QR Code navigation button
        Button(
            onClick = {
                navController.navigate("qrInviteView/$eventId")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Generate QR Code")
        }

        Spacer(modifier = Modifier.height(25.dp))

        InvitedUsersList(
            invitedUsers = invitedUsers,
            eventId = eventId,
            ownerId = ownerId,
            isEventOwner = isEventOwner,
            onKickUser = { eventId, userId -> viewModel.kickUser(eventId, userId) }
        )
    }
}

fun isValidEmail(email: String): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

@Composable
fun EditableEmailField(
    eventId: String,
    viewModel: InviteViewModel = hiltViewModel()
) {
    var userEmail by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }
    val emailList by viewModel.emailList.collectAsState()
    val showConfirmButton = emailList.isNotEmpty()

    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Enter Friends Email",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 8.dp)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = userEmail,
                onValueChange = {
                    userEmail = it
                    isError = false // Reset error on change
                },
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.weight(1f),
                singleLine = true,
                placeholder = { Text("Email") },
                isError = isError,
                supportingText = {
                    if (isError) {
                        Text("Invalid email format", color = Color.Red)
                    }
                }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    if (userEmail.isNotBlank() && isValidEmail(userEmail)) {
                        viewModel.addEmail(userEmail.trim())
                        userEmail = "" // Clear the input field
                    } else {
                        isError = true // Show error message
                    }
                },
                shape = RoundedCornerShape(20.dp)
            ) {
                Text("+")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Column {
            emailList.forEach { email ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFD9C4EC), shape = RoundedCornerShape(20.dp))
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = email, fontSize = 16.sp, modifier = Modifier.padding(4.dp))
                    Text(
                        text = "X",
                        fontSize = 16.sp,
                        color = Color.Gray,
                        modifier = Modifier
                            .padding(4.dp)
                            .clickable {
                                viewModel.removeEmail(email)
                            }
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
            }
        }

        if (showConfirmButton) {
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    viewModel.confirmInvites(emailList, eventId)
                },
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Confirm Invite")
            }
        }
    }
}

@Composable
fun InvitedUsersList(
    invitedUsers: List<InvitedUser>,
    eventId: String,
    ownerId: String,
    isEventOwner: Boolean,
    onKickUser: (eventId: String, userId: String) -> Unit
) {
    if (invitedUsers.isNotEmpty()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Already Invited Users",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn {
                items(invitedUsers) { invitedUser ->
                    // Check if this invited user is the owner
                    val isOwner = invitedUser.user.user_id == ownerId

                    // For the owner or accepted users, use full opacity; otherwise, use reduced opacity.
                    val rowAlpha = if (isOwner || invitedUser.accepted) 1f else 0.5f

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .alpha(rowAlpha)
                            .background(Color(0xFFE0E0E0), shape = RoundedCornerShape(20.dp))
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = invitedUser.user.display_name,
                                fontSize = 16.sp,
                                modifier = Modifier.padding(4.dp)
                            )
                            Text(
                                text = invitedUser.user.email,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(4.dp)
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            when {
                                // If the invited user is the owner, show "Owner" and do not grey out.
                                isOwner -> {
                                    Text(
                                        text = "Owner",
                                        fontSize = 14.sp,
                                        modifier = Modifier.padding(4.dp)
                                    )
                                }
                                // If the invite hasn't been accepted, show "Pending Invite" in gray.
                                !invitedUser.accepted -> {
                                    Text(
                                        text = "Pending Invite",
                                        fontSize = 14.sp,
                                        color = Color.Gray,
                                        modifier = Modifier.padding(4.dp)
                                    )
                                }
                            }
                            // Show remove icon only if:
                            // - the current user is the event owner,
                            // - the invited user has accepted,
                            // - and the invited user is not the owner.
                            if (isEventOwner && invitedUser.accepted && !isOwner) {
                                IconButton(
                                    onClick = {
                                        onKickUser(eventId, invitedUser.user.user_id)
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Remove user"
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
    }
}


