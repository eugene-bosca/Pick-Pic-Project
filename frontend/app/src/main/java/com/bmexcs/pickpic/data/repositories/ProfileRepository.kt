package com.bmexcs.pickpic.data.repositories

import com.bmexcs.pickpic.data.models.Profile
import com.bmexcs.pickpic.data.sources.ProfileDataSource
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
}
