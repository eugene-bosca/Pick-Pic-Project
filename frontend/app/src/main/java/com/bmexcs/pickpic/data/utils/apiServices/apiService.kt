package com.bmexcs.pickpic.data.utils.apiServices

import android.util.Log
import com.bmexcs.pickpic.data.utils.HttpException
import com.bmexcs.pickpic.data.utils.NotFoundException
import okhttp3.OkHttpClient
import okhttp3.Response
import com.google.gson.Gson

//====================================
// Utility file for other API services.
//====================================

const val TAG = "ApiService"
private const val BASE_URL = "https://pick-pic-service-627889116714.northamerica-northeast2.run.app"
val client = OkHttpClient()
val gson = Gson()

fun buildUrl(path: String): String = "${BASE_URL}/$path"

fun handleResponseStatus(response: Response): Boolean {
    val code = response.code
    Log.w(TAG, "Response code: $code")

    if (code == 200 || code == 201) {
        return true
    }
    when (code) {
        400 -> throw HttpException(code, "Bad request")
        401 -> throw HttpException(code, "Unauthorized")
        403 -> throw HttpException(code, "Forbidden")
        404 -> throw NotFoundException("Not found")
    }
    if (code in 501..599) {
        throw HttpException(code, "Internal server error")
    }
    Log.w(TAG, "Issue with request: $response")
    return false
}