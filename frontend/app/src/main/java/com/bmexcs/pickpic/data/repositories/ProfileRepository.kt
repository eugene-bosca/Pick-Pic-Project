package com.bmexcs.pickpic.data.repositories

import android.util.Log
import com.bmexcs.pickpic.data.models.User
import com.bmexcs.pickpic.data.sources.ProfileDataSource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepository @Inject constructor(
    private val authRepository: AuthRepository,
    private val profileDataSource: ProfileDataSource
) {
    // TODO: remove
    private var dummyUser = User(
        displayName = "Jordan",
        email = "jordan@jordan.jordan",
        phone = "123-jordanjordanjordan"
    )

    suspend fun getProfile(): User? {
        Log.d("ProfileRepository", "Returning dummy profile: $dummyUser")
        return dummyUser

        // TODO: use this
//        val user = authRepository.getCurrentUser() ?: return null
//        return profileDataSource.getProfile(user.uid)
    }

    suspend fun saveProfile(user: User) {
        Log.d("ProfileRepository", "Saving dummy profile: $user")
        dummyUser = user

        // TODO: use this
//        Log.d("ProfileRepository", "Saving profile to repository: $profile")
//        val user = authRepository.getCurrentUser() ?: return
//
//        Log.d("ProfileRepository", "Current profile found: ${user.uid}")
//        profileDataSource.saveProfile(user.uid, profile)
    }

    suspend fun signOut() {
        authRepository.signOut()
    }
}
