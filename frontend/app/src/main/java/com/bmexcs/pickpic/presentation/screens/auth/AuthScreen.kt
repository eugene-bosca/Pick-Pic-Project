package com.bmexcs.pickpic.presentation.screens.auth

import android.credentials.GetCredentialException
import androidx.credentials.GetCredentialResponse
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
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetPasswordOption
import androidx.credentials.PasswordCredential
import androidx.credentials.exceptions.NoCredentialException
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bmexcs.pickpic.BuildConfig
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.Firebase
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.security.MessageDigest
import java.util.UUID

private const val TAG = "AuthScreen"

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
            GoogleSignInButton(onSignIn = { onClickHomePage() })
        }
    }
}

@Composable
fun nativeSignInButton(onSignIn: () -> Unit) {
//    val buildRequest: () -> GetCredentialRequest = {
//        val getPasswordOption: GetPasswordOption = GetPasswordOption()
//    }
//
//    AuthButton("Sign in with E-mail", onSignIn, buildRequest)
}

@Composable
fun GoogleSignInButton(onSignIn: () -> Unit) {
    val buildRequest: () -> GetCredentialRequest = {
        // Create the nonce.
        val rawNonce = UUID.randomUUID().toString()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(rawNonce.toByteArray())
        val hashedNonce = digest.fold(""){str, it -> str + "%02x".format(it)}

        // Build the options to make a credential request for the user's Google ID token.
        val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(BuildConfig.WEB_CLIENT_ID)
            .setNonce(hashedNonce)
            .build()

        // Encapsulates a credential request which will be passed to getCredential.
        GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()
    }

    AuthButton("Sign in with Google", onSignIn, buildRequest)
}

@Composable
fun AuthButton(text: String, onSignIn: () -> Unit, buildRequest: () -> GetCredentialRequest) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val onClick: () -> Unit = {
        val credentialManager = CredentialManager.create(context)
        val request = buildRequest()

        coroutineScope.launch {
            val response: GetCredentialResponse? = try {
                credentialManager.getCredential(
                    request = request,
                    context = context,
                )
            } catch (e: GetCredentialException) {
                Log.e(TAG, "GetCredentialException: ${e.message}")
                null
            } catch (e: GoogleIdTokenParsingException) {
                Log.e(TAG, "GoogleIdTokenParsingException: ${e.message}")
                null
            }  catch (e: NoCredentialException) {
                // TODO: prompt user to add account
                Log.e(TAG, "NoCredentialException: ${e.message}")
                null
            }

            response?.let {
                handleSignIn(it)
                Toast.makeText(context, "Sign-in successful!", Toast.LENGTH_SHORT).show()
                onSignIn()
            } ?: Toast.makeText(context, "Sign-in failed", Toast.LENGTH_SHORT).show()
        }
    }

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

suspend fun handleSignIn(result: GetCredentialResponse) {
    when (val credential: Credential = result.credential) {
        is PasswordCredential -> {
            val username = credential.id
            val password = credential.password

            Log.i(TAG, "PasswordCredential: username = $username, password = $password")

            firebaseAuthWithPassword(username, password)
        }
        is CustomCredential -> {
            if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val googleIdToken = googleIdTokenCredential.idToken

                Log.i(TAG, "GoogleIdTokenCredential: googleIdToken=$googleIdToken")

                firebaseAuthWithGoogle(googleIdToken)
            }
        }
    }
}

suspend fun firebaseAuthWithPassword(username: String, password: String) {
    //
}

suspend fun firebaseAuthWithGoogle(googleIdToken: String) {
    val authCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
    Firebase.auth.signInWithCredential(authCredential).await()
}

@Preview(showBackground = true)
@Composable
fun PreviewCenteredColumnWithBox() {
    AuthScreen { }
}
