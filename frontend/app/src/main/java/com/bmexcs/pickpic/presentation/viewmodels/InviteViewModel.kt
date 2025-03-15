package com.bmexcs.pickpic.presentation.viewmodels

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject



@HiltViewModel
class InviteViewModel @Inject constructor() : ViewModel() {
    private val _emailList = MutableStateFlow<List<String>>(emptyList())
    val emailList: StateFlow<List<String>> = _emailList

    fun addEmail(email: String) {
        _emailList.value += email
    }

    fun removeEmail(email: String) {
        _emailList.value -= email
    }

    fun confirmInvites(emailList: List<String>) {

    }
}
