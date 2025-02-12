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

    init {
        if (_dogImages.value.isEmpty()){
            fetchDogImages()
        }
    }

    fun fetchDogImages() {
        // Launch a coroutine on the IO dispatcher since this is a network request.
        viewModelScope.launch(Dispatchers.IO) {
            val client = OkHttpClient()
            // Dog CEO API endpoint to fetch 10 random images.
            val request = Request.Builder()
                .url("https://dog.ceo/api/breeds/image/random/10")
                .build()

            try {
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        return@launch
                    }
                    val responseBody = response.body?.string()

                    if (responseBody != null) {
                        val jsonObject = JSONObject(responseBody)

                        if (jsonObject.getString("status") == "success") {
                            val images = mutableListOf<String>()
                            val jsonArray = jsonObject.getJSONArray("message")

                            for (i in 0 until jsonArray.length()) {
                                images.add(jsonArray.getString(i))
                            }
                            _dogImages.value = images
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
