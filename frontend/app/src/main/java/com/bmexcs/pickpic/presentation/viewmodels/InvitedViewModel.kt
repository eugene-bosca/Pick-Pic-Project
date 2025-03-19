package com.bmexcs.pickpic.presentation.viewmodels

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bmexcs.pickpic.data.models.EventInfo
import com.bmexcs.pickpic.data.models.ImageInfo
import com.bmexcs.pickpic.data.models.User
import com.bmexcs.pickpic.data.repositories.EventRepository
import com.bmexcs.pickpic.data.repositories.UserRepository
import com.bmexcs.pickpic.presentation.MainActivity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "InvitedViewModel"

@HiltViewModel
class InvitedViewModel @Inject constructor(
    private val eventRepository: EventRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private var eventId = ""

    fun setEventId(id: String) {
        eventId = id
    }

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
    }

    fun getEventContentInfo(eventId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val images = eventRepository.getAllImageInfo(eventId)
                _images.value = images
            } catch (e: Exception) {
                _images.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun checkUserLoggedIn(context: Context) {
        try {
            userRepository.getUser()
        } catch (e: Exception) {
            Log.d(TAG, e.toString())
            redirectLogin(context)
            Log.d(TAG, "User is not logged in")
        }
    }

    fun handleAcceptInvite(context: Context) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                eventRepository.addUserToEvent(eventId, userRepository.getUser().user_id)
            } catch (e: Exception) {
                Log.d(TAG, e.toString())
            } finally {
                _isLoading.value = false
                toMainActivity(context)
            }
        }
    }
    fun handleDeclineInvite(context: Context) {
        toMainActivity(context)
    }

    private fun toMainActivity(context: Context) {
        val intent = Intent(context, MainActivity::class.java)
        context.startActivity(intent)
    }

    private fun redirectLogin(context: Context) {
        // Create an Intent to start MainActivity.
        val intent = Intent(context, MainActivity::class.java).apply {
            putExtra("QR_REQUIRE_LOGIN", true)
            putExtra("EVENT_ID", eventId)
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)

        // Finish the current activity (user cannot go back).
        (context as? Activity)?.finish()
    }
}
