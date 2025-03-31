package com.bmexcs.pickpic.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreenView(
    navController: NavHostController,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.loadProfile()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile") },
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
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            EditableDisplayNameField(viewModel)
            Spacer(modifier = Modifier.height(16.dp))

            EditableEmailField(viewModel)
            Spacer(modifier = Modifier.height(16.dp))

            EditablePhoneNumberField(viewModel)
            Spacer(modifier = Modifier.height(16.dp))

            SubmitButton(viewModel)

            Spacer(modifier = Modifier.weight(1f))

            LogOutButton(viewModel, navController)
        }
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
        value = profileState?.name ?: "",
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
    val isEmailValid by remember { derivedStateOf { viewModel.isEmailValid } }

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
        singleLine = true,
        isError = !isEmailValid,
        supportingText = {
            if (!isEmailValid) {
                Text(text = "Invalid email format", color = MaterialTheme.colorScheme.error)
            }
        }
    )
}

@Composable
fun EditablePhoneNumberField(viewModel: ProfileViewModel) {
    val profileState by viewModel.user.collectAsState()
    val isPhoneValid by remember { derivedStateOf { viewModel.isPhoneValid } }

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
        singleLine = true,
        isError = !isPhoneValid,
        supportingText = {
            if (!isPhoneValid) {
                Text(text = "Invalid phone format", color = MaterialTheme.colorScheme.error)
            }
        }
    )
}

@Composable
fun SubmitButton(viewModel: ProfileViewModel) {
    val isEmailValid by remember { derivedStateOf { viewModel.isEmailValid } }
    val isPhoneValid by remember { derivedStateOf { viewModel.isPhoneValid } }

    Button(
        onClick = {
            viewModel.submit()
        },
        modifier = Modifier.fillMaxWidth(),
        enabled = isEmailValid && isPhoneValid
    ) {
        Text("Submit", fontSize = 16.sp)
    }
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
