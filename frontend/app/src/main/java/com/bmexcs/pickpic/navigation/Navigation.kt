package com.bmexcs.pickpic.navigation
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.bmexcs.pickpic.presentation.screens.events.EventScreen
import com.bmexcs.pickpic.presentation.screens.home.HomePageScreen
import com.bmexcs.pickpic.presentation.screens.profile.ProfileScreen
import com.bmexcs.pickpic.presentation.screens.ranking.RankingScreen
import com.bmexcs.pickpic.presentation.screens.support.SupportScreen
import com.bmexcs.pickpic.presentation.screens.auth.AuthScreen

@Composable
fun Navigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = AuthPage) {
        composable<HomePage> { HomePageScreen(
            onClickHomePage = {navController.navigate(route = HomePage)},
            onClickProfile = {navController.navigate(route = Profile)},
            onClickSupport = {navController.navigate(route = Support)},
            onClickEvent = {navController.navigate(route = Event)},
            onClickRanking = {navController.navigate(route = Ranking)}
        ) }
        composable<Profile> { ProfileScreen(
            onClickHomePage = {navController.navigate(route = HomePage)},
            onClickProfile = {navController.navigate(route = Profile)},
            onClickSupport = {navController.navigate(route = Support)},
            onClickEvent = {navController.navigate(route = Event)},
            onClickRanking = {navController.navigate(route = Ranking)}
        ) }
        composable<Support> { SupportScreen(
            onClickHomePage = {navController.navigate(route = HomePage)},
            onClickProfile = {navController.navigate(route = Profile)},
            onClickSupport = {navController.navigate(route = Support)},
            onClickEvent = {navController.navigate(route = Event)},
            onClickRanking = {navController.navigate(route = Ranking)}
        ) }
        composable<Event> { EventScreen(
            onClickHomePage = {navController.navigate(route = HomePage)},
            onClickProfile = {navController.navigate(route = Profile)},
            onClickSupport = {navController.navigate(route = Support)},
            onClickEvent = {navController.navigate(route = Event)},
            onClickRanking = {navController.navigate(route = Ranking)}
        ) }
        composable<Ranking> { RankingScreen(
            onClickHomePage = {navController.navigate(route = HomePage)},
            onClickProfile = {navController.navigate(route = Profile)},
            onClickSupport = {navController.navigate(route = Support)},
            onClickEvent = {navController.navigate(route = Event)},
            onClickRanking = {navController.navigate(route = Ranking)}
        ) }
        composable<AuthPage> { AuthScreen(
            onClickHomePage = {navController.navigate(route = HomePage)}
        ) }
        // Add more destinations similarly.
    }
}
