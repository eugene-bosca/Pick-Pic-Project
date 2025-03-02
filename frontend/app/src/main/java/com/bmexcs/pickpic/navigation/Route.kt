sealed class Route(val route: String) {
    object Home : Route("home")
    object Profile : Route("profile")
    object Support : Route("support")
    object Event : Route("event")
    object Ranking : Route("ranking")
    object AuthPage : Route("auth")
    object EventInvitation : Route("event_invitation")
}