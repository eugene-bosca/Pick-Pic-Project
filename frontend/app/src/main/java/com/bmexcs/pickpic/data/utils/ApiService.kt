package com.bmexcs.pickpic.data.utils

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.OkHttpClient
import okhttp3.Response
import java.lang.reflect.Type

private const val TAG = "ApiService"

object ApiService {
    private const val BASE_URL = "https://pick-pic-service-627889116714.northamerica-northeast2.run.app"

    private val client = OkHttpClient()
    private val gson = Gson()

    fun handleResponseStatus(response: Response): Boolean {
        val code = response.code

        if (code == 200 || code == 201) {
            Log.w(TAG, "Response code: $code")
            return true
        }

        if (code == 400) {
            throw HttpException(code, "Bad request")
        }
        else if (code == 401) {
            throw HttpException(code, "Unauthorized")
        }
        else if (code == 403) {
            throw HttpException(code, "Forbidden")
        }
        else if (code == 404) {
            throw NotFoundException("Endpoint does not exist")
        }
        else if (code in 501..599) {
            throw HttpException(code, "Internal server error")
        }
        Log.w(TAG, "Issue with request: $response")
        return false
    }

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
            val responseOK = handleResponseStatus(response)

            var body = response.body?.string() ?: throw HttpException(
                response.code,
                "Empty response body"
            )

            return@withContext parseResponseBody(body, responseType)
        }
    }

    suspend fun <T> getImage(
        endpoint: String,
        responseType: Class<T>,
        token: String
    ) : ByteArray? = withContext(Dispatchers.IO) {
        Log.d(TAG, "Fetching from endpoint: $endpoint")
        val url = buildUrl(endpoint)
        Log.d(TAG, "URL: $url")
        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $token")
            .get()
            .build()

        client.newCall(request).execute().use { response ->
            val responseOK = handleResponseStatus(response)

            return@withContext response.body?.bytes()
        }
    }

    suspend fun <T> getList(
        endpoint: String,
        responseType: Class<T>,  // Type of the list elements (not List<T>)
        token: String
    ): List<T> = withContext(Dispatchers.IO) {
        Log.d(TAG, "Fetching from endpoint: $endpoint")
        val url = buildUrl(endpoint)

        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $token")
            .get()
            .build()

        client.newCall(request).execute().use { response ->
            Log.d(TAG, "Response code: ${response.code}")

            val responseOK = handleResponseStatus(response)

            val body = response.body?.string()
            if (body.isNullOrEmpty()) {
                throw HttpException(response.code, "Empty response body")
            }

            try {
                // Create a Type for List<T>
                val listType: Type = TypeToken.getParameterized(List::class.java, responseType).type

                // Ensure the return type is explicitly List<T>
                val parsedResponse: List<T> = Gson().fromJson(body, listType)
                    ?: throw IllegalStateException("Failed to parse response")

                return@withContext parsedResponse
            } catch (e: Exception) {
                throw IllegalStateException("Error parsing response: ${e.message}", e)
            }
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
            .addHeader("Content-Type", contentType.toString())
            .post(requestBodyObj)
            .build()

        client.newCall(request).execute().use { response ->
            handleResponseStatus(response)

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
            .addHeader("Content-Type", contentType.toString())
            .patch(requestBodyObj)
            .build()

        client.newCall(request).execute().use { response ->
            handleResponseStatus(response)

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
        Log.d(TAG, "Putting to endpoint: $endpoint")

        val url = buildUrl(endpoint)

        val jsonBody = toJson(requestBody)
        val requestBodyObj = jsonBody.toRequestBody(contentType.toMediaType())

        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $token")
            .addHeader("Content-Type", contentType.toString())
            .put(requestBodyObj)
            .build()

        client.newCall(request).execute().use { response ->
            handleResponseStatus(response)

            val body = response.body?.string() ?: throw HttpException(
                response.code,
                "Empty response body"
            )

            return@withContext parseResponseBody(body, responseType)
        }
    }

    // TODO: test
    suspend fun put(
        endpoint: String,
        requestBody: ByteArray,
        token: String,
        contentType: HttpContentType = HttpContentType.PNG
    ): String = withContext(Dispatchers.IO) {
        Log.d(TAG, "Overloaded Call to PUT endpoint: $endpoint")

        val url = buildUrl(endpoint)

        val requestBodyObj = requestBody.toRequestBody()

        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $token")
            .addHeader("Content-Type", contentType.toString())
            .put(requestBodyObj)
            .build()

        client.newCall(request).execute().use { response ->


            val body = response.body?.string() ?: throw HttpException(
                response.code,
                "Empty response body"
            )
            handleResponseStatus(response)

            return@withContext body
        }
    }

    suspend fun <T> delete(
        endpoint: String,
        responseType: Class<T>,
        token: String
    ): T = withContext(Dispatchers.IO) {
        Log.d(TAG, "Deleting to endpoint: $endpoint")

        val url = buildUrl(endpoint)

        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $token")
            .delete()
            .build()

        client.newCall(request).execute().use { response ->
            handleResponseStatus(response)

            val body = response.body?.string() ?: throw HttpException(
                response.code,
                "Empty response body"
            )

            return@withContext parseResponseBody(body, responseType)
        }
    }

    fun buildUrl(path: String): String = "$BASE_URL/$path"

    fun <T> parseResponseBody(body: String, modelClass: Class<T>): T {
        Log.d("parseResponseBody", body)
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
