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

//private const val TAG = "ApiService"

object ApiService {
    val gson = Gson()
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
