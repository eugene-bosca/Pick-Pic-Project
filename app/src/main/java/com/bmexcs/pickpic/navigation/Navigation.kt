package com.bmexcs.pickpic.navigation
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.bmexcs.pickpic.activities.events.EventScreen
import com.bmexcs.pickpic.activities.main.HomePageScreen
import com.bmexcs.pickpic.activities.profile.ProfileScreen
import com.bmexcs.pickpic.activities.ranking.RankingScreen
import com.bmexcs.pickpic.activities.support.SupportScreen

@Composable
fun Navigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = HomePage) {
        composable<HomePage> { HomePageScreen() }
        composable<Profile> { ProfileScreen() }
        composable<Support> { SupportScreen() }
        composable<Event> { EventScreen() }
        composable<Ranking> { RankingScreen() }
        // Add more destinations similarly.
    }
}
