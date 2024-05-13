package cc.seaotter.tomatoes.ui

import android.Manifest
import android.content.res.Resources
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import cc.seaotter.tomatoes.TomatoesAppState
import cc.seaotter.tomatoes.ui.common.permission.PermissionDialog
import cc.seaotter.tomatoes.ui.common.permission.RationaleDialog
import cc.seaotter.tomatoes.ui.common.snackbar.SnackbarManager
import cc.seaotter.tomatoes.ui.navigation.TomatoesBottomNavigationBar
import cc.seaotter.tomatoes.ui.navigation.TomatoesNavHost
import cc.seaotter.tomatoes.ui.navigation.TomatoesNavigationActions
import cc.seaotter.tomatoes.ui.navigation.TomatoesRoute
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import kotlinx.coroutines.CoroutineScope


@Composable
fun TomatoesApp() {
    val navController = rememberNavController()
    val navigationActions = remember(navController) {
        TomatoesNavigationActions(navController)
    }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val selectedDestination =
        navBackStackEntry?.destination?.route ?: TomatoesRoute.TODO

    val appState = rememberAppState(
        navigationActions = navigationActions
    )

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        RequestNotificationPermissionDialog()
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = appState.snackbarHostState,
                modifier = Modifier.padding(8.dp),
                snackbar = { snackbarData ->
                    Snackbar(snackbarData, contentColor = MaterialTheme.colorScheme.onPrimary)
                }
            )
        }
    ) { innerPaddingModifier ->
        TomatoesAppContent(
            modifier = Modifier.padding(innerPaddingModifier),
            navController = navController,
            selectedDestination = selectedDestination,
            appState = appState
        )
    }


}

@Composable
fun TomatoesAppContent(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    selectedDestination: String,
    appState: TomatoesAppState
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
                appState = appState
            )
            if (appState.isShowBottomNavigationBar.value) {
                TomatoesBottomNavigationBar(
                    selectedDestination = selectedDestination,
                    navigateToTopLevelDestination = appState.navActions::navigateTo
                )
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun RequestNotificationPermissionDialog() {
    val permissionState =
        rememberPermissionState(permission = Manifest.permission.POST_NOTIFICATIONS)

    if (!permissionState.status.isGranted) {
        if (permissionState.status.shouldShowRationale) RationaleDialog()
        else PermissionDialog { permissionState.launchPermissionRequest() }
    }
}

@Composable
fun rememberAppState(
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    navigationActions: TomatoesNavigationActions,
    isShowBottomNavigationBar: MutableState<Boolean> = remember { mutableStateOf(false) },
    snackbarManager: SnackbarManager = SnackbarManager,
    resources: Resources = resources(),
    coroutineScope: CoroutineScope = rememberCoroutineScope()
) =
    remember(snackbarHostState, navigationActions, snackbarManager, resources, coroutineScope) {
        TomatoesAppState(
            snackbarHostState,
            navigationActions,
            isShowBottomNavigationBar,
            snackbarManager,
            resources,
            coroutineScope
        )
    }

@Composable
@ReadOnlyComposable
fun resources(): Resources {
    LocalConfiguration.current
    return LocalContext.current.resources
}