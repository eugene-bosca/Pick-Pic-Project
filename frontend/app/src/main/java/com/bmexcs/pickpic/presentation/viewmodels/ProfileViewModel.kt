package com.bmexcs.pickpic.presentation.viewmodels

import androidx.lifecycle.ViewModel
import com.bmexcs.pickpic.data.repositories.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository
) : ViewModel() {
    // User profile ViewModel.
}
