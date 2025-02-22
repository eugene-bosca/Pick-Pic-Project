package com.bmexcs.pickpic.presentation.screens.profile

import androidx.lifecycle.ViewModel
import com.bmexcs.pickpic.data.profile.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository
) : ViewModel() {
    // User profile ViewModel.
}
