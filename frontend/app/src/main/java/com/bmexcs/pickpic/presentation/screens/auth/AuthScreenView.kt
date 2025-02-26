package com.bmexcs.pickpic.presentation.screens.auth

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
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun AuthScreenView(
    authViewModel: AuthViewModel = hiltViewModel(),
    onClickHomePage: () -> Unit,
) {
    val context = LocalContext.current

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        AuthBox {
            Text(fontSize = 20.sp, text = "Welcome! Please sign in.")
            Spacer(modifier = Modifier.height(16.dp))

            GoogleSignInButton(onSignIn = {
                // TODO: improved success and failure handling
                authViewModel.signInWithGoogle(
                    onSuccess = { onClickHomePage() },
                    onFailure = { Toast.makeText(context, "Sign-in failed", Toast.LENGTH_SHORT).show() }
                )
            })
        }
    }
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

@Preview(showBackground = true)
@Composable
fun PreviewCenteredColumnWithBox() {
    AuthScreenView { }
}
