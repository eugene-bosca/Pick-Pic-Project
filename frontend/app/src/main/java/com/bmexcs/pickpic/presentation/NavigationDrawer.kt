package com.bmexcs.pickpic.presentation

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun NavigationDrawer(
    onClickHomePage: () -> Unit,
    onClickProfile: () -> Unit,
    onClickSupport: () -> Unit,
    onClickEvent: () -> Unit,
    onClickRanking: () -> Unit
){
    Button(
        onClick = { onClickHomePage() },
    ) {
        Text("Go to Home")
    }
    Button(
        onClick = { onClickProfile() },
    ) {
        Text("Go to Profile")
    }
    Button(
        onClick = { onClickSupport() },
    ) {
        Text("Go to Support")
    }
    Button(
        onClick = { onClickEvent() },
    ) {
        Text("Go to Event")
    }
    Button(
        onClick = { onClickRanking() },
    ) {
        Text("Go to Ranking")
    }
}