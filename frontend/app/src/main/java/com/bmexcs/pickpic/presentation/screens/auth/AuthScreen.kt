package com.bmexcs.pickpic.presentation.screens.auth

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun AuthScreen(
    authViewModel: AuthViewModel = viewModel(),
    onClickHomePage: () -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        AuthBox {
            Text(
                fontSize = 20.sp,
                text = "Welcome! Please sign in."
            )
            Spacer(modifier = Modifier.height(16.dp))
            AuthButton(text = "Sign in with Google", onClick = { onClickHomePage() })
            AuthButton(text = "Sign Out", onClick = { onClickHomePage() })
        }
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

@Preview(showBackground = true)
@Composable
fun PreviewCenteredColumnWithBox() {
    AuthScreen { }
}
