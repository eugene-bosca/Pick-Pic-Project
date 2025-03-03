package com.bmexcs.pickpic.data.utils

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.OkHttpClient

private const val TAG = "ApiService"

object ApiService {
    private const val BASE_URL = "https://pick-pic-service-627889116714.northamerica-northeast2.run.app"
    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    private val client = OkHttpClient()
    private val gson = Gson()

    fun <T> fetch(
        endpoint: String,
        responseType: Class<T>,
        token: String
    ): T {
        Log.d(TAG, "Fetching from endpoint: $endpoint")

        val url = buildUrl(endpoint)

        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $token")
            .get()
            .build()

        client.newCall(request).execute().use { response ->
            Log.d(TAG, "Response code: ${response.code}")

            validateResponse(response)

            val body = response.body?.string() ?: throw HttpException(
                response.code,
                "Empty response body"
            )

            return parseResponseBody(body, responseType)
        }
    }

    // TODO: test
    fun <T, R> post(
        endpoint: String,
        requestBody: R,
        responseType: Class<T>,
        token: String
    ): T {
        Log.d(TAG, "Posting to endpoint: $endpoint")

        val url = buildUrl(endpoint)

        val jsonBody = toJson(requestBody)
        val requestBodyObj = jsonBody.toRequestBody(jsonMediaType)

        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $token")
            .addHeader("Content-Type", "application/json")
            .post(requestBodyObj)
            .build()

        client.newCall(request).execute().use { response ->
            Log.d(TAG, "Response code: ${response.code}")

            validateResponse(response)

            val body = response.body?.string() ?: throw HttpException(
                response.code,
                "Empty response body"
            )

            return parseResponseBody(body, responseType)
        }
    }

    // TODO: test
    fun <T, R> patch(
        endpoint: String,
        requestBody: R,
        responseType: Class<T>,
        token: String
    ): T {
        Log.d(TAG, "Patching to endpoint: $endpoint")

        val url = buildUrl(endpoint)

        val jsonBody = toJson(requestBody)
        val requestBodyObj = jsonBody.toRequestBody(jsonMediaType)

        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $token")
            .addHeader("Content-Type", "application/json")
            .patch(requestBodyObj)
            .build()

        client.newCall(request).execute().use { response ->
            Log.d(TAG, "Response code: ${response.code}")

            validateResponse(response)

            val body = response.body?.string() ?: throw HttpException(
                response.code,
                "Empty response body"
            )

            return parseResponseBody(body, responseType)
        }
    }

    private fun buildUrl(path: String): String = "$BASE_URL/$path"

    private fun validateResponse(response: Response) {
        if (!response.isSuccessful) {
            throw HttpException(response.code, "Error fetching data")
        }
    }

    private fun <T> parseResponseBody(body: String, modelClass: Class<T>): T {
        return try {
            gson.fromJson(body, modelClass)
        } catch (e: JsonSyntaxException) {
            throw JsonParseException("Error parsing JSON", e)
        }
    }

    private fun <T> toJson(obj: T): String = gson.toJson(obj)
}

class HttpException(code: Int, message: String) : Exception("$message (HTTP $code)")
class JsonParseException(message: String, cause: Throwable) : Exception(message, cause)
