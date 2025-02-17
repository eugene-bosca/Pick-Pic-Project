package com.bmexcs.pickpic.presentation.screens.auth

import android.credentials.GetCredentialException
import android.util.Log
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.NoCredentialException
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import kotlinx.coroutines.launch
import java.security.MessageDigest
import java.util.UUID

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
            GoogleSignInButton()
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

@Composable
fun GoogleSignInButton() {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val onClick: () -> Unit = {

        // CredentialManager manages user authentication flows.
        // In particular, we use it to launch framework UI flows for registering/using credentials.
        val credentialManager = CredentialManager.create(context)

        // Create a SHA-256 nonce for
        val rawNonce = UUID.randomUUID().toString()
        val bytes = rawNonce.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        val hashedNonce = digest.fold(""){str, it -> str + "%02x".format(it)}

        Log.i("TOKEN", "GoogleSignInButton: hashedNonce = $hashedNonce")

        // Build the options to make a credential request for the user's Google ID token.
        val webClientId = "627889116714-bbpkrid0d5lnvjsghdjvl9eoan3ud7fr.apps.googleusercontent.com"
        val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(webClientId)
            .setNonce(hashedNonce)
            .build()

        // Encapsulates a request to get a user credential.
        // We pass it the GetGoogleIdOption to get the Google ID token.
        val request: GetCredentialRequest = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        coroutineScope.launch {
            try {
                val result = credentialManager.getCredential(
                    request = request,
                    context = context,
                )

                // TODO: switch over all supported credential types.
                val credential = result.credential

                // Extract the Google ID token.
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val googleIdToken = googleIdTokenCredential.idToken

                Log.i("TOKEN", googleIdToken)

                Toast.makeText(context, "Sign-in successful!", Toast.LENGTH_SHORT).show()
            }
            catch (e: GetCredentialException) {
                Toast.makeText(context, "Sign-in failed", Toast.LENGTH_SHORT).show()
                Log.e("TOKEN", "GetCredentialException: ${e.message}")
            }
            catch (e: GoogleIdTokenParsingException) {
                Toast.makeText(context, "Sign-in failed", Toast.LENGTH_SHORT).show()
                Log.e("TOKEN", "GoogleIdTokenParsingException: ${e.message}")
            }
            catch (e: NoCredentialException) {
                // TODO: prompt user to add account
                Toast.makeText(context, "Sign-in failed", Toast.LENGTH_SHORT).show()
                Log.e("TOKEN", "NoCredentialException: ${e.message}")
            }
        }
    }

    AuthButton("Sign in with Google", onClick)
}

@Preview(showBackground = true)
@Composable
fun PreviewCenteredColumnWithBox() {
    AuthScreen { }
}
