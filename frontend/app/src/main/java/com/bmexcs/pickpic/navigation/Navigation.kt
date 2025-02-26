package com.bmexcs.pickpic.navigation
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.bmexcs.pickpic.presentation.screens.events.EventScreenView
import com.bmexcs.pickpic.presentation.screens.home.HomePageScreenView
import com.bmexcs.pickpic.presentation.screens.profile.ProfileScreenView
import com.bmexcs.pickpic.presentation.screens.ranking.RankingScreenView
import com.bmexcs.pickpic.presentation.screens.support.SupportScreenView
import com.bmexcs.pickpic.presentation.screens.auth.AuthScreenView

@Composable
fun Navigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = AuthPage) {
        composable<HomePage> { HomePageScreenView(
            onClickHomePage = {navController.navigate(route = HomePage)},
            onClickProfile = {navController.navigate(route = Profile)},
            onClickSupport = {navController.navigate(route = Support)},
            onClickEvent = {navController.navigate(route = Event)},
            onClickRanking = {navController.navigate(route = Ranking)}
        ) }
        composable<Profile> { ProfileScreenView(
            onClickHomePage = {navController.navigate(route = HomePage)},
            onClickProfile = {navController.navigate(route = Profile)},
            onClickSupport = {navController.navigate(route = Support)},
            onClickEvent = {navController.navigate(route = Event)},
            onClickRanking = {navController.navigate(route = Ranking)}
        ) }
        composable<Support> { SupportScreenView(
            onClickHomePage = {navController.navigate(route = HomePage)},
            onClickProfile = {navController.navigate(route = Profile)},
            onClickSupport = {navController.navigate(route = Support)},
            onClickEvent = {navController.navigate(route = Event)},
            onClickRanking = {navController.navigate(route = Ranking)}
        ) }
        composable<Event> { EventScreenView(
            onClickHomePage = {navController.navigate(route = HomePage)},
            onClickProfile = {navController.navigate(route = Profile)},
            onClickSupport = {navController.navigate(route = Support)},
            onClickEvent = {navController.navigate(route = Event)},
            onClickRanking = {navController.navigate(route = Ranking)}
        ) }
        composable<Ranking> { RankingScreenView(
            onClickHomePage = {navController.navigate(route = HomePage)},
            onClickProfile = {navController.navigate(route = Profile)},
            onClickSupport = {navController.navigate(route = Support)},
            onClickEvent = {navController.navigate(route = Event)},
            onClickRanking = {navController.navigate(route = Ranking)}
        ) }
        composable<AuthPage> { AuthScreenView(
            onClickHomePage = {navController.navigate(route = HomePage)}
        ) }
        // Add more destinations similarly.
    }
}
