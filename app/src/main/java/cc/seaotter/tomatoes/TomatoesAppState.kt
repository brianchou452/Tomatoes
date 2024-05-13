package cc.seaotter.tomatoes

import android.content.res.Resources
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import cc.seaotter.tomatoes.ui.common.snackbar.SnackbarManager
import cc.seaotter.tomatoes.ui.common.snackbar.SnackbarMessage.Companion.toMessage
import cc.seaotter.tomatoes.ui.navigation.TomatoesNavigationActions
import cc.seaotter.tomatoes.ui.navigation.TomatoesRoute
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

@Stable
class TomatoesAppState(
    val snackbarHostState: SnackbarHostState,
    val navActions: TomatoesNavigationActions,
    val isShowBottomNavigationBar: MutableState<Boolean>,
    private val snackbarManager: SnackbarManager,
    private val resources: Resources,
    coroutineScope: CoroutineScope
) {
    init {
        coroutineScope.launch {
            snackbarManager.snackbarMessages.filterNotNull().collect { snackbarMessage ->
                val text = snackbarMessage.toMessage(resources)
                snackbarHostState.showSnackbar(text)
                snackbarManager.clearSnackbarState()
            }
        }
    }

    fun navigateAndPopUp(route: String, popUp: String) {
        if (route == TomatoesRoute.TODO) {
            isShowBottomNavigationBar.value = true
        }
        navActions.navigateAndPopUp(route, popUp)
    }


}
