package cc.seaotter.tomatoes.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import cc.seaotter.tomatoes.ui.navigation.TomatoesBottomNavigationBar
import cc.seaotter.tomatoes.ui.navigation.TomatoesNavHost
import cc.seaotter.tomatoes.ui.navigation.TomatoesNavigationActions
import cc.seaotter.tomatoes.ui.navigation.TomatoesRoute
import cc.seaotter.tomatoes.ui.navigation.TomatoesTopLevelDestination


@Composable
fun TomatoesApp() {
    val navController = rememberNavController()
    val navigationActions = remember(navController) {
        TomatoesNavigationActions(navController)
    }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val selectedDestination =
        navBackStackEntry?.destination?.route ?: TomatoesRoute.TODO

    TomatoesAppContent(
        navController = navController,
        selectedDestination = selectedDestination,
        navigateToTopLevelDestination = navigationActions::navigateTo
    )
}

@Composable
fun TomatoesAppContent(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    selectedDestination: String,
    navigateToTopLevelDestination: (TomatoesTopLevelDestination) -> Unit
) {
    Row(modifier = modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceContainerLow)
        ) {
            TomatoesNavHost(
                navController = navController,
                modifier = Modifier.weight(1f),
            )
            TomatoesBottomNavigationBar(
                selectedDestination = selectedDestination,
                navigateToTopLevelDestination = navigateToTopLevelDestination
            )
        }
    }
}