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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.ui.draw.alpha
import com.bmexcs.pickpic.presentation.viewmodels.InviteViewModel
import androidx.compose.ui.text.input.KeyboardType
import com.bmexcs.pickpic.data.models.UserMetadata

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InviteScreenView(
    navController: NavHostController,
    eventId: String,
    ownerId: String,
    viewModel: InviteViewModel = hiltViewModel()
) {
    // Load invited users when the screen is first displayed.
    LaunchedEffect(eventId) {
        viewModel.loadInvitedUsers(eventId)
    }

    val invitedUsers by viewModel.invitedUsers.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    val isEventOwner = viewModel.isCurrentUserOwner(ownerId)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Invite Friends") },
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
                .padding(16.dp)
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Error message if any.
            error?.let {
                Text(
                    text = it,
                    color = Color.Red,
                    modifier = Modifier.padding(8.dp)
                )
            }

            if (isLoading) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            EditableEmailField(
                eventId = eventId,
                viewModel = viewModel
            )

            Spacer(modifier = Modifier.height(16.dp))

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
}

@Composable
fun EditableEmailField(
    eventId: String,
    viewModel: InviteViewModel = hiltViewModel()
) {
    val userEmail by remember { derivedStateOf { viewModel.emailInput } }
    val isEmailValid by remember { derivedStateOf { viewModel.isEmailValid } }
    var addButtonPressed by remember { mutableStateOf(false) }

    val emailList by viewModel.emailList.collectAsState()
    val showConfirmButton = emailList.isNotEmpty()

    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Invitee emails",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 8.dp)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = userEmail,
                onValueChange = {
                    viewModel.emailInput = it
                    addButtonPressed = false
                },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Email
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.weight(1f),
                singleLine = true,
                placeholder = { Text("Email") },
                isError = !isEmailValid,
                supportingText = {
                    if (!isEmailValid && addButtonPressed) {
                        Text("Invalid email format", color = MaterialTheme.colorScheme.error)
                    }
                }
            )

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = {
                    addButtonPressed = true
                    if (userEmail.isNotBlank() && isEmailValid) {
                        viewModel.addEmail(userEmail.trim())
                        viewModel.emailInput = ""
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
    invitedUsers: List<UserMetadata>,
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
                    val isOwner = invitedUser.id == ownerId

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
                                text = invitedUser.name,
                                fontSize = 16.sp,
                                modifier = Modifier.padding(4.dp)
                            )
                            Text(
                                text = invitedUser.email,
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
                                        onKickUser(eventId, invitedUser.id)
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
