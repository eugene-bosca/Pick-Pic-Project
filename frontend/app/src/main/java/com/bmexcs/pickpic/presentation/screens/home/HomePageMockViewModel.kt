package com.bmexcs.pickpic.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

class HomePageMockViewModel : ViewModel() {

    // Backing property for the dog images list
    private val _dogImages = MutableStateFlow<List<String>>(emptyList())
    val dogImages: StateFlow<List<String>> = _dogImages

    /**
     * Fetches 5 random dog images from the Dog CEO API using OkHttp.
     */
    fun fetchDogImages() {
        // Launch a coroutine on the IO dispatcher since this is a network request
        viewModelScope.launch(Dispatchers.IO) {
            val client = OkHttpClient()
            // Dog CEO API endpoint to fetch 5 random images
            val request = Request.Builder()
                .url("https://dog.ceo/api/breeds/image/random/10")
                .build()

            try {
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        // Optionally, handle the error appropriately
                        return@launch
                    }
                    val responseBody = response.body?.string()
                    if (responseBody != null) {
                        // Parse the JSON response
                        val jsonObject = JSONObject(responseBody)
                        // Ensure the API call was successful
                        if (jsonObject.getString("status") == "success") {
                            val images = mutableListOf<String>()
                            val jsonArray = jsonObject.getJSONArray("message")
                            for (i in 0 until jsonArray.length()) {
                                images.add(jsonArray.getString(i))
                            }
                            // Update the state flow with the fetched images
                            _dogImages.value = images
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // Optionally, update state to reflect the error
            }
        }
    }
}
