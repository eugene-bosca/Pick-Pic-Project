package com.bmexcs.pickpic.presentation.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bmexcs.pickpic.presentation.NavigationDrawer

@Composable
fun HomePageScreen(
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
            Text("HOME!")
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