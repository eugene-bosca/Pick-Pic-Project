package com.bmexcs.pickpic.data.repositories

import android.content.Context
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.PasswordCredential
import androidx.credentials.exceptions.ClearCredentialException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.bmexcs.pickpic.BuildConfig
import com.bmexcs.pickpic.data.services.SignInResult
import com.bmexcs.pickpic.data.sources.AuthDataSource
import com.bmexcs.pickpic.data.sources.UserDataSource
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import dagger.hilt.android.qualifiers.ApplicationContext
import java.security.MessageDigest
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "AuthRepository"

@Singleton
class AuthRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val authDataSource: AuthDataSource,
    private val userDataSource: UserDataSource
) {
    fun getCurrentUser() = authDataSource.getCurrentUser()

    suspend fun signInWithGoogle(): SignInResult {
        val credentialManager = CredentialManager.create(context)
        val request = buildGoogleSignInRequest()

        return try {
            val response = credentialManager.getCredential(
                request = request,
                context = context,
            )
            handleSignIn(response)
            SignInResult.Success
        } catch (e: GoogleIdTokenParsingException) {
            Log.e(TAG, "Google sign-in failed, could not parse Google ID token", e)
            SignInResult.TokenParseError
        } catch (e: NoCredentialException) {
            Log.e(TAG, "Google sign-in failed, user has no credentials", e)
            SignInResult.NoCredentials
        } catch (e: GetCredentialException) {
            Log.e(TAG, "Google sign-in failed, could not retrieve credential", e)
            SignInResult.ConnectionError
        } catch (e: Exception) {
            Log.e(TAG, "Google sign-in failed, unknown issue", e)
            SignInResult.UnknownError
        }
    }

    suspend fun signOut() {
        authDataSource.signOut()

        val credentialManager = CredentialManager.create(context)
        val request = ClearCredentialStateRequest()

        try {
            credentialManager.clearCredentialState(request)
        } catch (e: ClearCredentialException) {
            Log.e(TAG, "Google sign-out failed, could not clear credentials", e)
        }
    }

    suspend fun initUserState() {
        val firebaseId = authDataSource.getCurrentUser().uid
        val name = authDataSource.getCurrentUser().displayName ?: throw Exception("Empty display name")
        val email = authDataSource.getCurrentUser().email ?: throw Exception("Empty email field")
        val token = authDataSource.getIdToken() ?: throw Exception("Invalid Firebase ID token")

        userDataSource.initUserWithFirebase(firebaseId, name, email, token)
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

                authDataSource.firebaseAuthWithPassword(username, password)
            }
            // Custom credential sign-in.
            is CustomCredential -> {
                // Google OAuth2.0 sign-in.
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    val googleIdTokenCredential =
                        GoogleIdTokenCredential.createFrom(credential.data)
                    val googleIdToken = googleIdTokenCredential.idToken

                    Log.i(TAG, "GoogleIdTokenCredential: googleIdToken=$googleIdToken")

                    authDataSource.firebaseAuthWithGoogle(googleIdToken)
                }
            }
        }

        // Retrieve and cache the user ID associated with the Firebase ID.
        initUserState()
    }
}
