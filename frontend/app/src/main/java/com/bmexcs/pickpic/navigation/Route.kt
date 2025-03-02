package com.bmexcs.pickpic.navigation

sealed class Route(val route: String) {
    data object Home : Route("home")
    data object Profile : Route("profile")
    data object Support : Route("support")
    data object Event : Route("event")
    data object Ranking : Route("ranking")
    data object AuthPage : Route("auth")
    data object EventInvitation : Route("event_invitation")
}
