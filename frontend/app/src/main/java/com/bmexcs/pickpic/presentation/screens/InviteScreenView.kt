package com.bmexcs.pickpic.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import com.bmexcs.pickpic.data.models.UserInfo
import com.bmexcs.pickpic.presentation.viewmodels.InviteViewModel

@Composable
fun InviteScreenView(
    navController: NavHostController,
    eventId: String,
    viewModel: InviteViewModel = hiltViewModel()
) {
    // Load invited users when the screen is first displayed
    LaunchedEffect(eventId) {
        viewModel.loadInvitedUsers(eventId)
    }

    val invitedUsers by viewModel.invitedUsers.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

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

        // Already invited users list
        InvitedUsersList(invitedUsers = invitedUsers)
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
fun InvitedUsersList(invitedUsers: List<UserInfo>) {
    if (invitedUsers.isNotEmpty()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Already Invited Users",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Column {
                invitedUsers.forEach { user ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFE0E0E0), shape = RoundedCornerShape(20.dp))
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = user.email, // TODO: make this display name?
                            fontSize = 16.sp,
                            modifier = Modifier.padding(4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
    }
}