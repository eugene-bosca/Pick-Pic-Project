package com.bmexcs.pickpic.data.profile

import com.bmexcs.pickpic.data.auth.AuthRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepository @Inject constructor(
    private val authRepository: AuthRepository
) {
    fun getUserName(): List<String> {
        // TODO: access the user id, etc.
        val userId = "xd"
        val profile: Profile? = fetchUserProfile(userId)
        return profile?.name.orEmpty()
    }

    // TODO: move to data source (and actually invoke the db)
    private fun fetchUserProfile(userId: String): Profile? {
        return listOf(
            Profile("no xd", name = listOf("Jordan", "my", "beloved")),
            Profile("xd", name = listOf("Alan's", "backend"))
        ).find { it.id == userId }
    }
}
