package com.bmexcs.pickpic.presentation.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.bmexcs.pickpic.R
import com.bmexcs.pickpic.presentation.viewmodels.ProfileViewModel
import androidx.navigation.NavHostController

@Composable
fun ProfileScreenView(
    navController: NavHostController,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val profile by viewModel.profile.collectAsState(initial = null)
    var displayName by remember { mutableStateOf(profile?.displayName ?: "") }
    var email by remember { mutableStateOf(profile?.email ?: "") }
    var phone by remember { mutableStateOf(profile?.phone ?: "") }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Profile Picture
            val profilePicture: Painter = painterResource(id = R.drawable.ic_launcher_foreground) //replace with actual image.
            Image(
                painter = profilePicture,
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(150.dp)
                    .align(Alignment.CenterHorizontally)
                    .clickable {
                        // TODO: Implement image selection logic
                    }
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Display Name
            OutlinedTextField(
                value = displayName,
                onValueChange = { displayName = it },
                label = { Text("Display Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Email
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Phone
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Phone") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Save Button
            Button(
                onClick = {
                    viewModel.saveProfile(displayName, email, phone)
                    Log.d("ProfileScreen", "Saving profile: Name=$displayName, Email=$email, Phone=$phone")
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Save")
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Log Out Button
            Button(
                onClick = {
                    viewModel.logout()
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Log Out")
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
