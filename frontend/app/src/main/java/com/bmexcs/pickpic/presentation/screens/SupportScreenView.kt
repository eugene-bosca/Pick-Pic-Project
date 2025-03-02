package com.bmexcs.pickpic.presentation.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController

@Composable
fun SupportScreenView(
    navController: NavHostController,
) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column (
            modifier = Modifier.padding(innerPadding)
        ){
            Text("**Need Help? We're Here for You!**  \n" +
                    "\n" +
                    "Welcome to the Pick-Pic Support Page! Whether you're creating events, sharing photos, or ranking your favorite moments, we're here to help.  \n" +
                    "\n" +
                    "Pick-Pic was created as a project by **University of Waterloo students**, dedicated to making photo-sharing fun.  \n" +
                    "\n" +
                    "If you any questions or feedback, feel free to reach out to **jkjamali@uwaterloo.ca**. We’re here to ensure you have the best experience possible!  \n" +
                    "\n" +
                    "Happy photo-sharing! \uD83D\uDCF8✨")
        }
    }
}
