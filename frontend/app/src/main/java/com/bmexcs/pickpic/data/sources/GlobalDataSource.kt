package com.bmexcs.pickpic.data.sources

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import okhttp3.Response

const val BASE_URL = "https://pick-pic-service-627889116714.northamerica-northeast2.run.app"

suspend fun getFirebaseToken(): String? {
    if (FirebaseAuth.getInstance().currentUser != null) {
        val firebaseUser = FirebaseAuth.getInstance().currentUser

        val tokenResult = firebaseUser?.getIdToken(false)?.await()

        return tokenResult?.token

    } else {
        Log.e("GlobalDataSource:getFirebaseToken", "User is not authenticated")
        throw Exception("User is not authenticated")
    }
}

fun validateResponse(response: Response) {
    if (!response.isSuccessful) {
        throw Exception("Error fetching events: ${response.code}")
    }
}

inline fun <reified T> parseResponseBody(body: String): T {
    val gson = Gson()

    return try {
        gson.fromJson(body, T::class.java)
    } catch (e: JsonSyntaxException) {
        throw Exception("Error parsing JSON: ${e.message}")
    }
}