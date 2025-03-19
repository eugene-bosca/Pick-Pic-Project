package com.bmexcs.pickpic.data.sources

import android.util.Log
import com.bmexcs.pickpic.data.dtos.User
import com.bmexcs.pickpic.data.dtos.UserCreation
import com.bmexcs.pickpic.data.services.UserApiService
import com.bmexcs.pickpic.data.services.NotFoundException
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "UserDataSource"

@Singleton
class UserDataSource @Inject constructor(
    private val authDataSource: AuthDataSource
) {

    private var cachedUser: User? = null

    private val userApi = UserApiService()

    fun getUser(): User {
        return cachedUser ?: throw Exception("Null user")
    }

    suspend fun initUserWithFirebase() {
        val firebaseId = authDataSource.getCurrentUser().uid
        val token = authDataSource.getIdToken() ?: throw Exception("Invalid Firebase ID token")

        Log.d(TAG, "Initialize user state with firebaseId=$firebaseId")

        cachedUser = try {
            val user = userApi.userFromFirebaseId(firebaseId, token)
            user
        } catch (e: NotFoundException) {
            Log.d(TAG, "User not found, creating new user")
            createUser()
        }
    }

    suspend fun updateUser(user: User) {
        Log.d(TAG, "Updating user state")

        val token = authDataSource.getIdToken() ?: throw Exception("No user token")
        cachedUser = userApi.update(user, token)
    }

    private suspend fun createUser(): User {
        val userCreation = UserCreation(
            firebase_id = authDataSource.getCurrentUser().uid,
            display_name = authDataSource.getCurrentUser().displayName ?: throw Exception("Empty display name"),
            email = authDataSource.getCurrentUser().email ?: throw Exception("Empty email field")
        )
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")

        Log.d(TAG, "createUser with firebaseID=${authDataSource.getCurrentUser().uid}")

        return try {
            val newUser = userApi.create(userCreation, token)
            newUser
        } catch (e: Exception) {
            Log.e(TAG, "Failed to create new user: $e")
            User()
        }
    }

    suspend fun getUsersFromEmails(emails: List<String>): List<String> {
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")

        Log.d(TAG, "Getting users from $emails")
        return userApi.usersFromEmails(emails, token)
    }
}
