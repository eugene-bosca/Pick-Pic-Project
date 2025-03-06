package com.bmexcs.pickpic.data.utils.apiServices

import android.util.Log
import com.bmexcs.pickpic.data.models.EventContent
import com.bmexcs.pickpic.data.models.EventPicture
import com.bmexcs.pickpic.data.models.ListUserEventsResponse
import com.bmexcs.pickpic.data.utils.ApiService
import com.bmexcs.pickpic.data.utils.ApiService.parseResponseBody
import com.bmexcs.pickpic.data.utils.HttpContentType
import com.bmexcs.pickpic.data.utils.HttpException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Request
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import okhttp3.Response

suspend fun getListUsersEvents(userId: String, token: String):
        ListUserEventsResponse = withContext(Dispatchers.IO) {
    val endpoint = "list_users_events/$userId/"

    Log.d("$TAG/Event:getListUsersEvents", "GET: $endpoint")

    val request = Request.Builder()
        .url(buildUrl(endpoint))
        .addHeader("Authorization", "Bearer $token")
        .get()
        .build()

    client.newCall(request).execute().use { response ->
        val responseOK = handleResponseStatus(response)

        val body = response.body?.string()
        val resultType = object : TypeToken<ListUserEventsResponse>() {}.type

        val result: ListUserEventsResponse = gson.fromJson(body, resultType)

        return@withContext result
    }
}
suspend fun getEventContents(eventId: String, token: String): Array<EventPicture> {

    val endpoint = "event_contents/$eventId/"

    Log.d("$TAG/Event:getEventContents", "GET: $endpoint")

    val request = Request.Builder()
        .url(buildUrl(endpoint))
        .addHeader("Authorization", "Bearer $token")
        .get()
        .build()

    client.newCall(request).execute().use { response ->
        val responseOK = handleResponseStatus(response)

        val parsed = parseResponseBody(response.body?.string() ?: "", Array<EventPicture>::class.java)

        return parsed
    }
}

//suspend fun addImageByEvent(eventContent: EventContent) {
//    val token = authDataSource.getIdToken() ?: throw Exception("No user token")
//    ApiService.post("event-contents/", eventContent, String::class.java, token)
//}
//suspend fun deleteImageByEventId(imageId: String) {
//    val token = authDataSource.getIdToken() ?: throw Exception("No user token")
//    ApiService.delete("event-contents", EventContent::class.java, token)
//}

suspend fun deleteImage(imageID: String, token: String): String {
    val endpoint = "event_contents/$imageID/"

    Log.d("$TAG/Event:getEventContents", "GET: $endpoint")

    val request = Request.Builder()
        .url(buildUrl(endpoint))
        .addHeader("Authorization", "Bearer $token")
        .get()
        .build()

    client.newCall(request).execute().use { response ->

        val responseOK = handleResponseStatus(response)
        return response.body.toString()
    }
}

suspend fun postImageByEvent(imageID: String, token: String): String {
    val endpoint = "event_contents/$imageID/"

    Log.d("$TAG/Event:getEventContents", "GET: $endpoint")

    val request = Request.Builder()
        .url(buildUrl(endpoint))
        .addHeader("Authorization", "Bearer $token")
        .get()
        .build()

    client.newCall(request).execute().use { response ->

        val responseOK = handleResponseStatus(response)
        return response.body.toString()
    }
}
