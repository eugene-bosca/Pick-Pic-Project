package com.bmexcs.pickpic.data.sources

import android.util.Log
import com.bmexcs.pickpic.data.models.UserMetadata
import com.bmexcs.pickpic.data.services.UserApiService
import com.bmexcs.pickpic.data.services.NotFoundException
import javax.inject.Singleton

private const val TAG = "UserDataSource"

@Singleton
class UserDataSource {

    private var cachedUser: UserMetadata? = null

    private val userApi = UserApiService()

    fun getUser(): UserMetadata {
        return cachedUser ?: throw Exception("Null user")
    }

    suspend fun initUserWithFirebase(firebaseId: String, name: String, email: String, token: String) {
        Log.d(TAG, "Initialize user state with firebaseId=$firebaseId")

        cachedUser = try {
            val user = userApi.userFromFirebaseId(firebaseId, token)
            user
        } catch (e: NotFoundException) {
            Log.d(TAG, "UserMetadata not found, creating new user")
            val user = try {
                val newUser = userApi.create(firebaseId, name, email, token)
                newUser
            } catch (e: Exception) {
                Log.e(TAG, "Failed to create new user: $e")
                UserMetadata()
            }
            user
        }
    }

    suspend fun updateUser(user: UserMetadata, firebaseId: String, token: String) {
        Log.d(TAG, "Updating user state")

        cachedUser = userApi.update(firebaseId, user, token)
    }
}
