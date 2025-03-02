package com.bmexcs.pickpic.presentation.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun SupportScreenView(
    navController: NavHostController,
) {
    Box(
        modifier = Modifier.padding(10.dp)
    ) {
        Text(
        "Need Help? We're Here for You!\n" +
            "\n" +
            "Welcome to the Pick-Pic Support Page! Whether you're creating events, sharing photos, or ranking your favorite moments, we're here to help.  \n" +
            "\n" +
            "Pick-Pic was created as a project by University of Waterloo students, dedicated to making photo-sharing fun.  \n" +
            "\n" +
            "If you any questions or feedback, feel free to reach out to jkjamali@uwaterloo.ca. We’re here to ensure you have the best experience possible!  \n" +
            "\n" +
            "Happy photo-sharing! \uD83D\uDCF8✨",
            fontSize = 20.sp
        )
    }
}
