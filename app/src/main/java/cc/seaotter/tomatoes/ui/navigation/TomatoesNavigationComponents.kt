package cc.seaotter.tomatoes.ui.navigation

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import cc.seaotter.tomatoes.ui.achievement.AchievementScreen
import cc.seaotter.tomatoes.ui.history.HistoryScreen
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
                }
            )
        }
    }
}


@Composable
fun TomatoesNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = TomatoesRoute.TODO,
    ) {
        composable(TomatoesRoute.TODO) {
            TodoScreen()
        }
        composable(TomatoesRoute.HISTORY) {
            HistoryScreen()
        }
        composable(TomatoesRoute.ACHIEVEMENT) {
            AchievementScreen()
        }
    }
}