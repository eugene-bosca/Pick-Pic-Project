package com.bmexcs.pickpic.data.repositories

import android.util.Log
import com.bmexcs.pickpic.data.models.User
import com.bmexcs.pickpic.data.sources.UserDataSource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val authRepository: AuthRepository,
) {
    // TODO: use UserDataSource
    private var dummyUser = User(
        displayName = "Jordan",
        email = "jordan@jordan.jordan",
        phone = "123-jordanjordanjordan"
    )

    suspend fun getUser(): User? {
        Log.d("ProfileRepository", "Returning dummy profile: $dummyUser")
        return dummyUser
    }

    suspend fun updateUser(user: User) {
        Log.d("ProfileRepository", "Saving dummy profile: $user")
        dummyUser = user
    }

    suspend fun signOut() {
        authRepository.signOut()
    }
}
