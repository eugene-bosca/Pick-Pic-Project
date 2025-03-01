package com.bmexcs.pickpic.presentation.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.bmexcs.pickpic.navigation.HomePage
import com.bmexcs.pickpic.presentation.viewmodels.AuthViewModel

@Composable
fun AuthScreenView(
    navController: NavHostController,
    authViewModel: AuthViewModel = hiltViewModel(),
) {
    val context = LocalContext.current

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        AuthBox {
            Text(fontSize = 20.sp, text = "Welcome! Please sign in.")
            Spacer(modifier = Modifier.height(16.dp))

            AuthEmailField()
            AuthPasswordField()

            Spacer(Modifier.height(20.dp))

            GoogleSignInButton(onSignIn = {
                // TODO: improved success and failure handling
                authViewModel.signInWithGoogle(
                    onSuccess = { navController.navigate(HomePage) },
                    onFailure = { Toast.makeText(context, "Sign-in failed", Toast.LENGTH_SHORT).show() }
                )
            })
        }
    }
}

@Composable
fun AuthEmailField() {
    var email by remember { mutableStateOf(TextFieldValue("")) }

    OutlinedTextField(
        value = email,
        onValueChange = { newText ->
            email = newText
        },
        label = { Text("Email") },
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Email
        ),
        shape = RoundedCornerShape(16.dp),
        colors = TextFieldDefaults.colors(unfocusedContainerColor = Color.White, focusedContainerColor = Color.White,
            unfocusedIndicatorColor = Color.Transparent),
        modifier = Modifier.width(250.dp),
        singleLine = true
    )
}

@Composable
fun AuthPasswordField() {
    var password by remember { mutableStateOf(TextFieldValue("")) }

    OutlinedTextField(
        value = password,
        onValueChange = { newText ->
            password = newText
        },
        label = { Text("Password") },
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Password
        ),
        shape = RoundedCornerShape(16.dp),
        colors = TextFieldDefaults.colors(unfocusedContainerColor = Color.White, focusedContainerColor = Color.White,
            unfocusedIndicatorColor = Color.Transparent),
        modifier = Modifier.width(250.dp),
        singleLine = true
    )
}

@Composable
fun GoogleSignInButton(onSignIn: () -> Unit) {
    AuthButton("Sign in with Google", onSignIn)
}

@Composable
fun AuthButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.width(250.dp)
    ) {
        Text(
            fontSize = 18.sp,
            text = text
        )
    }
}

@Composable
fun AuthBox(content: @Composable() (() -> Unit)) {
    Box(
        modifier = Modifier
            .clip(shape = RoundedCornerShape(20.dp))
            .background(color = Color(0xFFEEEEEE))
    ) {
        Column(
            modifier = Modifier.padding(50.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            content()
        }
    }
}