package com.bmexcs.pickpic.data.repositories

import com.bmexcs.pickpic.data.models.User
import com.bmexcs.pickpic.data.sources.UserDataSource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val userDataSource: UserDataSource,
    private val authRepository: AuthRepository
) {
    fun getUser(): User {
        return userDataSource.getUser()
    }

    suspend fun updateUser(user: User) {
        userDataSource.updateUser(user)
    }

    suspend fun signOut() {
        authRepository.signOut()
    }

    suspend fun getUserWithEmail(email: String): String {
        return userDataSource.getUserIdFromEmail(email)
    }
}
