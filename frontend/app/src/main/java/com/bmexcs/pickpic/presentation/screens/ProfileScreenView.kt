package com.bmexcs.pickpic.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bmexcs.pickpic.R
import com.bmexcs.pickpic.presentation.viewmodels.ProfileViewModel
import androidx.navigation.NavHostController
import com.bmexcs.pickpic.navigation.Route

@Composable
fun ProfileScreenView(
    navController: NavHostController,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.loadProfile()
    }

    Column(modifier = Modifier.padding(16.dp)) {
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
        EditableDisplayNameField(viewModel)
        Spacer(modifier = Modifier.height(16.dp))

        // Email
        EditableEmailField(viewModel)
        Spacer(modifier = Modifier.height(16.dp))

        // Phone Number
        EditablePhoneNumberField(viewModel)
        Spacer(modifier = Modifier.height(16.dp))

        // Log Out Button
        LogOutButton(viewModel, navController)
    }
}

@Composable
fun EditableDisplayNameField(viewModel: ProfileViewModel) {
    val profileState by viewModel.user.collectAsState()

    Text(
        text = "Display Name",
        fontSize = 18.sp,
        modifier = Modifier.padding(start = 8.dp)
    )

    Spacer(modifier = Modifier.height(4.dp))

    OutlinedTextField(
        value = profileState?.displayName ?: "",
        onValueChange = { viewModel.updateDisplayName(it) },
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Email
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
    )
}

@Composable
fun EditableEmailField(viewModel: ProfileViewModel) {
    val profileState by viewModel.user.collectAsState()

    Text(
        text = "Email",
        fontSize = 18.sp,
        modifier = Modifier.padding(start = 8.dp)
    )

    Spacer(modifier = Modifier.height(4.dp))

    OutlinedTextField(
        value = profileState?.email ?: "",
        onValueChange = { viewModel.updateEmail(it) },
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Email
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
    )
}

@Composable
fun EditablePhoneNumberField(viewModel: ProfileViewModel) {
    val profileState by viewModel.user.collectAsState()

    Text(
        text = "Phone Number",
        fontSize = 18.sp,
        modifier = Modifier.padding(start = 8.dp)
    )

    Spacer(modifier = Modifier.height(4.dp))

    OutlinedTextField(
        value = profileState?.phone ?: "",
        onValueChange = { viewModel.updatePhoneNumber(it) },
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Email
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
    )
}

@Composable
fun LogOutButton(viewModel: ProfileViewModel, navController: NavHostController) {
    Button(
        onClick = {
            viewModel.logOut()
            // TODO: check that logout was successful
            navController.navigate(Route.Auth.route)
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Log Out", fontSize = 16.sp)
    }
}
