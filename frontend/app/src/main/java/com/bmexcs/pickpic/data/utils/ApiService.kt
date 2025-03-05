package com.bmexcs.pickpic.data.utils

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.OkHttpClient

private const val TAG = "ApiService"

object ApiService {
    private const val BASE_URL = "https://pick-pic-service-627889116714.northamerica-northeast2.run.app"

    private val client = OkHttpClient()
    private val gson = Gson()

    suspend fun <T> get(
        endpoint: String,
        responseType: Class<T>,
        token: String
    ): T = withContext(Dispatchers.IO) {
        Log.d(TAG, "Fetching from endpoint: $endpoint")
        val url = buildUrl(endpoint)

        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $token")
            .get()
            .build()

        client.newCall(request).execute().use { response ->
            if (response.code != 200) {
                Log.w(TAG, "Response code: ${response.code}")
            } else {
                Log.i(TAG, "Got response ${response.code}")
            }

            if (response.code == 404) {
                throw NotFoundException("Endpoint does not exist")
            }

            val body = response.body?.string() ?: throw HttpException(
                response.code,
                "Empty response body"
            )

            return@withContext parseResponseBody(body, responseType)
        }
    }

    // TODO: test
    suspend fun <T, R> post(
        endpoint: String,
        requestBody: R,
        responseType: Class<T>,
        token: String,
        contentType: HttpContentType = HttpContentType.JSON
    ): T = withContext(Dispatchers.IO) {
        Log.d(TAG, "Posting to endpoint: $endpoint")

        val url = buildUrl(endpoint)

        val jsonBody = toJson(requestBody)
        Log.d(TAG, "jsonBody = $jsonBody")

        val requestBodyObj = jsonBody.toRequestBody(contentType.toMediaType())

        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $token")
            .addHeader("Content-Type", "application/json")
            .post(requestBodyObj)
            .build()

        client.newCall(request).execute().use { response ->
            if (response.code != 200) {
                Log.w(TAG, "Response code: ${response.code}")
            } else {
                Log.i(TAG, "Got response ${response.code}")
            }

            val body = response.body?.string() ?: throw HttpException(
                response.code,
                "Empty response body"
            )

            return@withContext parseResponseBody(body, responseType)
        }
    }

    // TODO: test
    suspend fun <T, R> patch(
        endpoint: String,
        requestBody: R,
        responseType: Class<T>,
        token: String,
        contentType: HttpContentType = HttpContentType.JSON
    ): T = withContext(Dispatchers.IO) {
        Log.d(TAG, "Patching to endpoint: $endpoint")

        val url = buildUrl(endpoint)

        val jsonBody = toJson(requestBody)
        val requestBodyObj = jsonBody.toRequestBody(contentType.toMediaType())

        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $token")
            .addHeader("Content-Type", "application/json")
            .patch(requestBodyObj)
            .build()

        client.newCall(request).execute().use { response ->
            if (response.code != 200) {
                Log.w(TAG, "Response code: ${response.code}")
            } else {
                Log.i(TAG, "Got response ${response.code}")
            }

            val body = response.body?.string() ?: throw HttpException(
                response.code,
                "Empty response body"
            )

            return@withContext parseResponseBody(body, responseType)
        }
    }

    // TODO: test
    suspend fun <T, R> put(
        endpoint: String,
        requestBody: R,
        responseType: Class<T>,
        token: String,
        contentType: HttpContentType = HttpContentType.JSON
    ): T = withContext(Dispatchers.IO) {
        Log.d(TAG, "Patching to endpoint: $endpoint")

        val url = buildUrl(endpoint)

        val jsonBody = toJson(requestBody)
        val requestBodyObj = jsonBody.toRequestBody(contentType.toMediaType())

        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $token")
            .addHeader("Content-Type", "application/json")
            .put(requestBodyObj)
            .build()

        client.newCall(request).execute().use { response ->
            if (response.code != 200) {
                Log.w(TAG, "Response code: ${response.code}")
            } else {
                Log.i(TAG, "Got response ${response.code}")
            }

            val body = response.body?.string() ?: throw HttpException(
                response.code,
                "Empty response body"
            )

            return@withContext parseResponseBody(body, responseType)
        }
    }

    suspend fun <T> delete(
        endpoint: String,
        responseType: Class<T>,
        token: String
    ): T = withContext(Dispatchers.IO) {
        Log.d(TAG, "Patching to endpoint: $endpoint")

        val url = buildUrl(endpoint)

        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $token")
            .delete()
            .build()

        client.newCall(request).execute().use { response ->
            if (response.code != 200) {
                Log.w(TAG, "Response code: ${response.code}")
            } else {
                Log.i(TAG, "Got response ${response.code}")
            }

            val body = response.body?.string() ?: throw HttpException(
                response.code,
                "Empty response body"
            )

            return@withContext parseResponseBody(body, responseType)
        }
    }

    private fun buildUrl(path: String): String = "$BASE_URL/$path"

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
class NotFoundException(message: String) : Exception(message)
class JsonParseException(message: String, cause: Throwable) : Exception(message, cause)
