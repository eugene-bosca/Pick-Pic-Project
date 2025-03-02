package com.bmexcs.pickpic.data.repositories

import android.util.Log
import com.bmexcs.pickpic.data.models.Profile
import com.bmexcs.pickpic.data.serializable.SerializableUUID
import com.bmexcs.pickpic.data.sources.ProfileDataSource
import kotlinx.serialization.Serializable
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepository @Inject constructor(
    private val authRepository: AuthRepository,
    private val profileDataSource: ProfileDataSource
) {
    suspend fun getProfile(): Profile? {
        val user = authRepository.getCurrentUser() ?: return null
        return profileDataSource.getProfile(user.uid)
    }

    suspend fun saveProfile(profile: Profile) {
        Log.d("ProfileRepository", "Saving profile to repository: $profile")
        val user = authRepository.getCurrentUser() ?: return
        Log.d("ProfileRepository", "Current profile found: ${user.uid}")
        profileDataSource.saveProfile(user.uid, profile)
    }
}
