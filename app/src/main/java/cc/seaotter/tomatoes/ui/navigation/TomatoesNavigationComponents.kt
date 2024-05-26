package cc.seaotter.tomatoes.ui.navigation

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import cc.seaotter.tomatoes.TomatoesAppState
import cc.seaotter.tomatoes.ui.achievement.AchievementScreen
import cc.seaotter.tomatoes.ui.countdown.CountDownScreen
import cc.seaotter.tomatoes.ui.history.HistoryScreen
import cc.seaotter.tomatoes.ui.login.LoginScreen
import cc.seaotter.tomatoes.ui.navigation.TomatoesRoute.TODO_ID
import cc.seaotter.tomatoes.ui.splash.SplashScreen
import cc.seaotter.tomatoes.ui.todo.TodoScreen


@Composable
fun TomatoesBottomNavigationBar(
    selectedDestination: String,
    navigateToTopLevelDestination: (TomatoesTopLevelDestination) -> Unit
) {
    NavigationBar(modifier = Modifier.fillMaxWidth()) {
        TOP_LEVEL_DESTINATIONS.forEach { tomatoesDestination ->
            NavigationBarItem(
                selected = selectedDestination == tomatoesDestination.route,
                onClick = { navigateToTopLevelDestination(tomatoesDestination) },
                icon = {
                    Icon(
                        imageVector = tomatoesDestination.selectedIcon,
                        contentDescription = stringResource(id = tomatoesDestination.iconTextId)
                    )
                },
                label = { Text(text = stringResource(id = tomatoesDestination.iconTextId)) }
            )
        }
    }
}


@Composable
fun TomatoesNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    appState: TomatoesAppState
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = TomatoesRoute.SPLASH,
    ) {
        tomatoesGraph(appState.navActions)
    }
}

fun NavGraphBuilder.tomatoesGraph(navAction: TomatoesNavigationActions) {
    composable(TomatoesRoute.SPLASH) {
        SplashScreen(openAndPopUp = { route, popUp -> navAction.navigateAndPopUp(route, popUp) })
    }
    composable(TomatoesRoute.LOGIN) {
        LoginScreen(openAndPopUp = { route, popUp -> navAction.navigateAndPopUp(route, popUp) })
    }
    composable(TomatoesRoute.TODO) {
        TodoScreen(
            navigateToCountDown = { todoID ->
                navAction.navigate("${TomatoesRoute.COUNTDOWN}/$todoID")
            }
        )
    }
    composable(TomatoesRoute.HISTORY) {
        HistoryScreen()
    }
    composable(TomatoesRoute.ACHIEVEMENT) {
        AchievementScreen(
            clearAndNavigate = { route -> navAction.clearAndNavigate(route) }
        )
    }
    composable(
        route = "${TomatoesRoute.COUNTDOWN}/{$TODO_ID}",
        arguments = listOf(navArgument(TODO_ID) { type = NavType.StringType })
    ) {
        CountDownScreen(
            popUpCountDown = { navAction.popUp() }
        )
    }
}
