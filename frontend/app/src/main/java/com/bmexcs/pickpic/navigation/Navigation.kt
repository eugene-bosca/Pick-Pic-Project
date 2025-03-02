package com.bmexcs.pickpic.navigation
import AppHeader
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.bmexcs.pickpic.presentation.screens.EventScreenView
import com.bmexcs.pickpic.presentation.screens.HomePageScreenView
import com.bmexcs.pickpic.presentation.screens.ProfileScreenView
import com.bmexcs.pickpic.presentation.screens.RankingScreenView
import com.bmexcs.pickpic.presentation.screens.SupportScreenView
import com.bmexcs.pickpic.presentation.screens.AuthScreenView
import com.bmexcs.pickpic.presentation.screens.EventInvitationScreenView

@Composable
fun Navigation(navController: NavHostController) {
    Column {
        NavHost(navController = navController, startDestination = AuthPage) {
            composable<HomePage> {
                // Apply AppHeader to pages which need it.
                AppHeader("Home Page", navController) {
                    HomePageScreenView(navController)
                }
            }
            composable<Profile> {
                AppHeader("Profile", navController) {
                    ProfileScreenView(navController)
                }
            }
            composable<Support> {
                AppHeader("Support", navController) {
                    SupportScreenView(navController)
                }
            }
            composable<Event> {
                AppHeader("Events", navController) {
                    EventScreenView(navController)
                }
            }
            composable<Ranking> {
                AppHeader("Ranking", navController) {
                    RankingScreenView(navController)
                }
            }
            composable<AuthPage> { AuthScreenView(navController) }
            // Add more destinations similarly.
            composable<EventInvitation> { EventInvitationScreenView(navController) }
        }
    }
}
