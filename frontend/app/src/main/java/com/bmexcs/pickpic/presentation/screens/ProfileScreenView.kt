package com.bmexcs.pickpic.presentation.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.bmexcs.pickpic.presentation.viewmodels.ProfileViewModel
import androidx.navigation.NavHostController

@Composable
fun ProfileScreenView(
    navController: NavHostController,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    Text(
        text = "Welcome to the Profile Screen!",
    )
}