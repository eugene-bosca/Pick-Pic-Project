package com.bmexcs.pickpic.data.repositories

import android.content.Context
import android.util.Log
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.PasswordCredential
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.bmexcs.pickpic.BuildConfig
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import java.security.MessageDigest
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "AUTH_REPO"

@Singleton
class AuthRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val firebaseAuth: FirebaseAuth,
) {

    // TODO: probably remove; just an example of flows
    private val _authState = MutableStateFlow(firebaseAuth.currentUser != null)
    val authState: StateFlow<Boolean> = _authState.asStateFlow()

    suspend fun signInWithGoogle(): Boolean {
        val credentialManager = CredentialManager.create(context)
        val request = buildGoogleSignInRequest()

        val response: GetCredentialResponse? = try {
            credentialManager.getCredential(
                request = request,
                context = context,
            )
        } catch (e: GetCredentialException) {
            Log.e(TAG, "Google sign-in failed, could not retrieve credential", e)
            null
        } catch (e: GoogleIdTokenParsingException) {
            Log.e(TAG, "Google sign-in failed, could not parse Google ID token", e)
            null
        }  catch (e: NoCredentialException) {
            // TODO: prompt user to add account
            // TODO: handle connection issues
            Log.e(TAG, "Google sign-in failed, user has no credentials", e)
            null
        }

        response?.let {
            handleSignIn(it)
        }
        // TODO: extremely scuffed. We should return an object from a sealed interface
        //  (i.e., an enum) to encapsulate the error type.
        return response != null
    }

    private fun buildGoogleSignInRequest(): GetCredentialRequest {
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
        return GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()
    }

    private suspend fun handleSignIn(result: GetCredentialResponse) {
        when (val credential: Credential = result.credential) {
            // Email and password sign-in.
            is PasswordCredential -> {
                val username = credential.id
                val password = credential.password

                Log.i(TAG, "PasswordCredential: username = $username, password = $password")

                firebaseAuthWithPassword(username, password)
            }
            // Custom credential sign-in.
            is CustomCredential -> {
                // Google OAuth2.0 sign-in.
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    val googleIdTokenCredential =
                        GoogleIdTokenCredential.createFrom(credential.data)
                    val googleIdToken = googleIdTokenCredential.idToken

                    Log.i(TAG, "GoogleIdTokenCredential: googleIdToken=$googleIdToken")

                    firebaseAuthWithGoogle(googleIdToken)
                }
            }
        }
    }

    private suspend fun firebaseAuthWithPassword(username: String, password: String) {
        // TODO: use credential manager
        try {
            firebaseAuth.signInWithEmailAndPassword(username, password).await()
            _authState.value = firebaseAuth.currentUser != null
        } catch (e: Exception) {
            Log.e(TAG, "Password sign-in failed", e)
        }
    }

    private suspend fun firebaseAuthWithGoogle(googleIdToken: String) {
        // TODO: return auth state based on error.
        try {
            val authCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
            firebaseAuth.signInWithCredential(authCredential).await()
            _authState.value = firebaseAuth.currentUser != null
        } catch (e: GetCredentialException) {
            // Could not retrieve from credential manager.
            Log.e(TAG, "Google sign-in failed, could not retrieve credential", e)
        } catch (e: FirebaseAuthInvalidUserException) {
            // User account has been disabled, or email corresponds to a user that DNE.
            Log.e(TAG, "Google sign-in failed, account is disabled or user DNE", e)
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            // Credential is malformed or has expired.
            // I.e., password is incorrect (if it's an email credential).
            Log.e(TAG, "Google sign-in failed, credential malformed or expired", e)
        } catch (e: FirebaseAuthUserCollisionException) {
            // Email already exists.
            Log.e(TAG, "Google sign-in failed, email already exists", e)
        }
    }

    fun signOut() {
        // TODO: use credential manager (?)
        firebaseAuth.signOut()
        _authState.value = false
    }
}