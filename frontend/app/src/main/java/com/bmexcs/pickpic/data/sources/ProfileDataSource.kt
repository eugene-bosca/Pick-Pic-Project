package com.bmexcs.pickpic.data.sources

import com.bmexcs.pickpic.data.models.Profile
import javax.inject.Inject
import okhttp3.OkHttpClient
import okhttp3.Request

class ProfileDataSource @Inject constructor(
    authDataSource: AuthDataSource
) {
    private val client = OkHttpClient()

    suspend fun getProfile(userId: String): Profile {
        // TODO make request
        return Profile()
    }
}
