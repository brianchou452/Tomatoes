package cc.seaotter.tomatoes.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import cc.seaotter.tomatoes.R

object TomatoesRoute {
    const val LOGIN = "LoginScreen"
    const val TODO = "Todo"
    const val COUNTDOWN = "CountDown"
    const val TODO_ID = "todoId"
    const val HISTORY = "History"
    const val ACHIEVEMENT = "Achievement"
}

data class TomatoesTopLevelDestination(
    val route: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val iconTextId: Int
)

class TomatoesNavigationActions(private val navController: NavHostController) {
    fun navigateTo(destination: TomatoesTopLevelDestination) {
        navController.navigate(destination.route) {
            // Pop up to the start destination of the graph to
            // avoid building up a large stack of destinations
            // on the back stack as users select items
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            // Avoid multiple copies of the same destination when
            // re-selecting the same item
            launchSingleTop = true
            // Restore state when re-selecting a previously selected item
            restoreState = true
        }
    }

    fun popUp() {
        navController.popBackStack()
    }

    fun navigate(route: String) {
        navController.navigate(route) { launchSingleTop = true }
    }

    fun navigateAndPopUp(route: String, popUp: String) {
        navController.navigate(route) {
            launchSingleTop = true
            popUpTo(popUp) { inclusive = true }
        }
    }

    fun clearAndNavigate(route: String) {
        navController.navigate(route) {
            launchSingleTop = true
            popUpTo(0) { inclusive = true }
        }
    }
}

val TOP_LEVEL_DESTINATIONS = listOf(
    TomatoesTopLevelDestination(
        route = TomatoesRoute.TODO,
        selectedIcon = Icons.Default.Done,
        unselectedIcon = Icons.Default.Done,
        iconTextId = R.string.tab_todo
    ),
    TomatoesTopLevelDestination(
        route = TomatoesRoute.HISTORY,
        selectedIcon = Icons.Default.DateRange,
        unselectedIcon = Icons.Default.DateRange,
        iconTextId = R.string.tab_history
    ),
    TomatoesTopLevelDestination(
        route = TomatoesRoute.ACHIEVEMENT,
        selectedIcon = Icons.Outlined.AccountCircle,
        unselectedIcon = Icons.Outlined.AccountCircle,
        iconTextId = R.string.tab_achievement
    )
)