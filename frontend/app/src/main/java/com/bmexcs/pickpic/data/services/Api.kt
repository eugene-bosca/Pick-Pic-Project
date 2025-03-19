package com.bmexcs.pickpic.data.services

import android.util.Log
import okhttp3.Response
import java.net.URL

object Api {
    private const val BASE_URL = "https://pick-pic-service-627889116714.northamerica-northeast2.run.app"

    private const val TAG = "ApiUtils"

    fun handleResponseStatus(response: Response): Boolean {
        val code = response.code

        if (code in 200..204) {
            Log.i(TAG, "SUCCESS with code: $code")
            return true
        }

        when (code) {
            400 -> throw HttpException(code, "Bad request")
            401 -> throw HttpException(code, "Unauthorized")
            403 -> throw HttpException(code, "Forbidden")
            404 -> throw NotFoundException("Endpoint does not exist")
            in 501..599 -> throw HttpException(code, "Internal server error")
        }

        Log.w(TAG, "Issue with request: $response")
        return false
    }

    fun url(endpoint: String): URL = URL("$BASE_URL/$endpoint")
}

class HttpException(code: Int, message: String) : Exception("$message (HTTP $code)")
class NotFoundException(message: String) : Exception(message)
