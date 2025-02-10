package com.bmexcs.pickpic.presentation.events

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bmexcs.pickpic.presentation.NavigationDrawer

@Composable
fun EventScreen(
    onClickHomePage: () -> Unit,
    onClickProfile: () -> Unit,
    onClickSupport: () -> Unit,
    onClickEvent: () -> Unit,
    onClickRanking: () -> Unit
) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column (
            modifier = Modifier.padding(innerPadding)
        ){
            Text("EVENT!")
            NavigationDrawer(
                onClickHomePage,
                onClickProfile,
                onClickSupport,
                onClickEvent,
                onClickRanking,
            )
        }
    }
}