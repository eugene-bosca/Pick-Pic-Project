package com.bmexcs.pickpic.data.sources

import android.util.Log
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import androidx.credentials.exceptions.GetCredentialException
import com.bmexcs.pickpic.data.models.Profile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import javax.inject.Inject

private const val TAG = "AUTH_DATASOURCE"

class AuthDataSource @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) {
    private val client = OkHttpClient()

    fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }

    // check if a user profile exists based on firebaseID
    suspend fun getUserProfile(): Profile? = withContext(Dispatchers.IO) {
        val firebaseID = getCurrentUser()?.uid
        val url = "$BASE_URL/users/$firebaseID"

        val token = getFirebaseToken()

        // Build the request with the Bearer token.
        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $token")
            .get()
            .build()

        Log.d(TAG, "request: $request")

        client.newCall(request).execute().use { response ->
            Log.d(TAG, "Response received: $response")

            val body = response.body?.string() ?: throw Exception("Empty response body")

            if (response.code == 404) {
                Log.d(TAG, "User profile does not exist")
                return@use null
            }

            val profile = parseResponseBody<Profile>(body)

            return@use profile
        }
    }

    // Function to create a profile
    suspend fun createProfile(profile: Profile): Profile? = withContext(Dispatchers.IO) {

        val url = "$BASE_URL/users/" // Adjust the URL as needed

        val token = getFirebaseToken()

        val jsonString = """
        {
          "firebase_id": "${getCurrentUser()?.uid}",
          "display_name": "${profile.displayName}",
          "email": "${profile.email}",
          "phone": "${profile.phone}",
          "profile_picture": ""
        }
        """.trimIndent()

        Log.d(TAG, "creating profile: $jsonString")

        // Build the request with the Bearer token and JSON body
        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $token")
            .addHeader("Content-Type", "application/json")
            .post(jsonString.toRequestBody("application/json".toMediaType()))
            .build()

        client.newCall(request).execute().use { response ->
            validateResponse(response)

            val body = response.body?.string() ?: throw Exception("Empty response body")

            // Parse the response into a Profile object
            return@use parseResponseBody<Profile>(body)
        }
    }

    suspend fun firebaseAuthWithPassword(username: String, password: String) {
        try {
            firebaseAuth.signInWithEmailAndPassword(username, password).await()
        } catch (e: Exception) {
            Log.e(TAG, "Password sign-in failed", e)
        }
    }

    suspend fun firebaseAuthWithGoogle(googleIdToken: String) {
        // TODO: return auth state based on error.
        try {
            val authCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
            firebaseAuth.signInWithCredential(authCredential).await()
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
        firebaseAuth.signOut()
    }
}
