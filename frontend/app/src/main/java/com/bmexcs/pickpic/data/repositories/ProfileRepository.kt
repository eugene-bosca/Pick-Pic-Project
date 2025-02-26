package com.bmexcs.pickpic.data.repositories

import com.bmexcs.pickpic.data.models.Profile
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepository @Inject constructor(
    private val authRepository: AuthRepository
) {
    fun getUserName(): String {
        // TODO: access the user id, etc.
        return "Alan McSillyface"
    }
}
