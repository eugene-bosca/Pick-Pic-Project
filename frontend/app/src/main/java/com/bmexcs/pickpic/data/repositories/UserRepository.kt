package com.bmexcs.pickpic.data.repositories

import com.bmexcs.pickpic.data.models.UserMetadata
import com.bmexcs.pickpic.data.sources.AuthDataSource
import com.bmexcs.pickpic.data.sources.UserDataSource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val userDataSource: UserDataSource,
    private val authDataSource: AuthDataSource
) {
    fun getUser(): UserMetadata {
        return userDataSource.getUser()
    }

    suspend fun updateUser(user: UserMetadata) {
        val firebaseId = authDataSource.getCurrentUser().uid
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")

        userDataSource.updateUser(user, firebaseId, token)
    }
}
