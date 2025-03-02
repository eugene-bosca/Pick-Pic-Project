package com.bmexcs.pickpic.data.repositories

import android.util.Log
import com.bmexcs.pickpic.data.models.Profile
import com.bmexcs.pickpic.data.sources.ProfileDataSource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepository @Inject constructor(
    private val authRepository: AuthRepository,
    private val profileDataSource: ProfileDataSource
) {
    // TODO: remove
    private var dummyProfile = Profile(
        displayName = "Jordan",
        email = "jordan@jordan.jordan",
        phone = "123-jordanjordanjordan"
    )

    suspend fun getProfile(): Profile? {
        Log.d("ProfileRepository", "Returning dummy profile: $dummyProfile")
        return dummyProfile

        // TODO: use this
//        val user = authRepository.getCurrentUser() ?: return null
//        return profileDataSource.getProfile(user.uid)
    }

    suspend fun saveProfile(profile: Profile) {
        Log.d("ProfileRepository", "Saving dummy profile: $profile")
        dummyProfile = profile

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
