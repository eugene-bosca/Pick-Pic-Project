package com.bmexcs.pickpic.data.repositories

import com.bmexcs.pickpic.data.models.UserMetadata
import com.bmexcs.pickpic.data.sources.UserDataSource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val userDataSource: UserDataSource,
    private val authRepository: AuthRepository
) {
    fun getUser(): UserMetadata {
        return userDataSource.getUser()
    }

    suspend fun updateUser(user: UserMetadata) {
        userDataSource.updateUser(user)
    }

    suspend fun signOut() {
        authRepository.signOut()
    }

    suspend fun getUsersFromEmails(emails: List<String>): List<String> {
        return userDataSource.getUsersFromEmails(emails)
    }
}
