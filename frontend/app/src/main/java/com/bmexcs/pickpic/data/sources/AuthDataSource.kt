package com.bmexcs.pickpic.data.sources

import android.util.Log
import androidx.credentials.exceptions.GetCredentialException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

private const val TAG = "AUTH_DATASOURCE"

class AuthDataSource @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) {

    fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth.currentUser
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
