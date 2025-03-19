package com.bmexcs.pickpic.data.sources

import android.util.Log
import com.bmexcs.pickpic.data.models.UserMetadata
import com.bmexcs.pickpic.data.services.UserApiService
import com.bmexcs.pickpic.data.services.NotFoundException
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "UserDataSource"

@Singleton
class UserDataSource @Inject constructor(
    private val authDataSource: AuthDataSource
) {

    private var cachedUser: UserMetadata? = null

    private val userApi = UserApiService()

    fun getUser(): UserMetadata {
        return cachedUser ?: throw Exception("Null user")
    }

    suspend fun initUserWithFirebase() {
        val token = authDataSource.getIdToken() ?: throw Exception("Invalid Firebase ID token")
        val firebaseId = authDataSource.getCurrentUser().uid

        Log.d(TAG, "Initialize user state with firebaseId=$firebaseId")

        cachedUser = try {
            val user = userApi.userFromFirebaseId(firebaseId, token)
            user
        } catch (e: NotFoundException) {
            Log.d(TAG, "UserMetadata not found, creating new user")
            createUser()
        }
    }

    suspend fun updateUser(user: UserMetadata) {
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")
        val firebaseId = authDataSource.getCurrentUser().uid

        Log.d(TAG, "Updating user state")

        cachedUser = userApi.update(firebaseId, user, token)
    }

    private suspend fun createUser(): UserMetadata {
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")

        Log.d(TAG, "createUser with firebaseID=${authDataSource.getCurrentUser().uid}")

        return try {
            val newUser = userApi.create(
                firebaseId = authDataSource.getCurrentUser().uid,
                name = authDataSource.getCurrentUser().displayName ?: throw Exception("Empty display name"),
                email = authDataSource.getCurrentUser().email ?: throw Exception("Empty email field"),
                token
            )
            newUser
        } catch (e: Exception) {
            Log.e(TAG, "Failed to create new user: $e")
            UserMetadata()
        }
    }

    suspend fun getUsersFromEmails(emails: List<String>): List<String> {
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")

        Log.d(TAG, "Getting users from $emails")
        return userApi.usersFromEmails(emails, token)
    }
}
