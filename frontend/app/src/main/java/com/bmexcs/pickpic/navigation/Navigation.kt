package com.bmexcs.pickpic.navigation

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
import com.bmexcs.pickpic.presentation.shared.AppHeader


@Composable
fun Navigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Route.Home.route) {
        // Home Screen
        composable(Route.Home.route) {
            AppHeader("Home Page", navController) {
                HomePageScreenView(navController)
            }
        }

        // Profile Screen
        composable(Route.Profile.route) {
            AppHeader("Profile", navController) { 
                ProfileScreenView(navController)
            }
        }

        // Support Screen
        composable(Route.Support.route) {
            AppHeader("Support", navController) { 
                SupportScreenView(navController)
            }
        }

        // Event Screen
        composable(Route.Event.route) {
            AppHeader("Events", navController) { 
                EventScreenView(navController)
            }
        }

        // Ranking Screen
        composable(Route.Ranking.route) {
            AppHeader("Ranking", navController) { 
                RankingScreenView(navController)
            }
        }

        // Auth Screen
        composable(Route.AuthPage.route) {
            AuthScreenView(navController)
        }

        // Event Invitation Screen
        composable(Route.EventInvitation.route) {
            EventInvitationScreenView(navController)
        }
    }
}