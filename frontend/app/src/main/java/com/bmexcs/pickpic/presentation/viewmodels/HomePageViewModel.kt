package com.bmexcs.pickpic.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bmexcs.pickpic.data.models.EventListItem
import com.bmexcs.pickpic.data.repositories.AuthRepository
import com.bmexcs.pickpic.data.repositories.HomePageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


@HiltViewModel
class HomePageViewModel @Inject constructor(
    private val listEvents: HomePageRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    // Now we use JsonElement instead of Profile.
    private val _events = MutableStateFlow<List<EventListItem>>(emptyList())
    val events: StateFlow<List<EventListItem>> = _events

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private var user: String? = null

    init {
        user = authRepository.getCurrentUser()?.uid
    }

    fun fetchEvents() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                user?.let { userId ->
                    Log.d("HomePageViewModel", "Fetching events for user: $userId")
                    // Execute the network call on the IO dispatcher
                    val eventItems = withContext(Dispatchers.IO) {
                        listEvents.getEvents("68e24b1a-36c8-4de5-a751-ba414e77db0b")
                    }
                    _events.value = eventItems
                    _errorMessage.value = null
                } ?: run {
                    _errorMessage.value = "User is not authenticated"
                    Log.d("HomePageViewModel", "User is not authenticated")
                }
            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage ?: "An unknown error occurred"
                Log.e("HomePageViewModel", "Error fetching events", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}
