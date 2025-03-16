package com.bmexcs.pickpic.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bmexcs.pickpic.data.models.EventInfo
import com.bmexcs.pickpic.data.models.ImageInfo
import com.bmexcs.pickpic.data.models.User
import com.bmexcs.pickpic.data.repositories.EventRepository
import com.bmexcs.pickpic.data.repositories.UserRepository
import com.bmexcs.pickpic.data.utils.Api
import com.bmexcs.pickpic.data.utils.HttpException
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Request
import javax.inject.Inject

@HiltViewModel
class InvitedViewModel @Inject constructor(
    private val eventRepository: EventRepository,
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _eventInfo = MutableStateFlow<EventInfo?>(null)
    val eventInfo: StateFlow<EventInfo?> = _eventInfo

    private val _ownerInfo = MutableStateFlow<User?>(null)
    val ownerInfo: StateFlow<User?> = _ownerInfo

    private val _images = MutableStateFlow<List<ImageInfo>>(emptyList())
    val images: StateFlow<List<ImageInfo>> = _images

    fun getEvent(eventId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val eventInfo = eventRepository.getEventInfo(eventId)
                _eventInfo.value = eventInfo
            } catch (e: Exception) {
                _eventInfo.value = null
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getEventOwnerInfo(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val ownerInfo = eventRepository.getEventOwnerInfo(userId)
                _ownerInfo.value = ownerInfo
            } catch (e: Exception) {
                _ownerInfo.value = null
            } finally {
                _isLoading.value = false
            }
        }
        Log.d("ownerinfo: ",ownerInfo.value.toString())
    }

    fun getEventContentInfo(eventId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val images = eventRepository.getImages(eventId)
                _images.value = images
            } catch (e: Exception) {
                _images.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }
}