package com.bmexcs.pickpic.navigation

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
import com.bmexcs.pickpic.presentation.screens.QrInviteView
import com.bmexcs.pickpic.presentation.screens.CreateEventScreenView
import com.bmexcs.pickpic.presentation.screens.EventInvitationScreenView
import com.bmexcs.pickpic.presentation.screens.InviteScreenView
import com.bmexcs.pickpic.presentation.shared.PickPicScaffold

@Composable
fun Navigation(
    navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Route.Auth.route
    ) {
        // Home Screen
        composable(Route.Home.route) {
            PickPicScaffold("Home Page", navController) {
                HomePageScreenView(navController)
            }
        }

        // Profile Screen
        composable(Route.Profile.route) {
            PickPicScaffold("Profile", navController) {
                ProfileScreenView(navController)
            }
        }

        // Support Screen
        composable(Route.Support.route) {
            PickPicScaffold("Support", navController) {
                SupportScreenView(navController)
            }
        }

        // Event Screen
        composable(Route.Event.route) {
            PickPicScaffold("Events", navController) {
                EventScreenView(navController)
            }
        }

        // Ranking Screen
        composable(Route.Ranking.route) {
            PickPicScaffold("Ranking", navController) {
                RankingScreenView(navController)
            }
        }

        // Auth Screen
        composable(Route.Auth.route) {
            AuthScreenView(navController)
        }

        // Event Invitation Accept Screen
        composable(Route.EventInvitation.route) {
            PickPicScaffold("Invites", navController) {
                EventInvitationScreenView(navController)
            }
        }

        // Create Event Screen
        composable(Route.CreateEvent.route) {
            PickPicScaffold("Create Event", navController) {
                CreateEventScreenView(navController)
            }
        }

        // Invite Screen
        composable("invite/{eventId}") { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId") ?: ""
            PickPicScaffold("Invite Friends", navController) {
                InviteScreenView(navController, eventId) // Pass eventId to InviteScreenView
            }
        }

        // QR Invite Screen
        composable("qrInviteView/{eventId}") { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId") ?: ""
            QrInviteView(navController = navController)
        }
    }
}
