package com.bmexcs.pickpic.presentation.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bmexcs.pickpic.presentation.shared.NavigationDrawer
import androidx.navigation.NavHostController

@Composable
fun SupportScreenView(
    navController: NavHostController,
) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column (
            modifier = Modifier.padding(innerPadding)
        ){
            // TODO: if we ever do actually include contact info, it
            //  should probably be located in a constant object somewhere
            //  in the utils package.
            Text("Support info goes here")
        }
    }
}
