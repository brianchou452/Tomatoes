package cc.seaotter.tomatoes.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cc.seaotter.tomatoes.data.service.LogService
import cc.seaotter.tomatoes.ui.common.snackbar.SnackbarManager
import cc.seaotter.tomatoes.ui.common.snackbar.SnackbarMessage.Companion.toSnackbarMessage
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


open class TomatoesViewModel(private val logService: LogService) : ViewModel() {
    fun launchCatching(snackbar: Boolean = true, block: suspend CoroutineScope.() -> Unit) =
        viewModelScope.launch(
            CoroutineExceptionHandler { context, throwable ->
                if (snackbar) {
                    SnackbarManager.showMessage(throwable.toSnackbarMessage())
                }
                logService.logNonFatalCrash(throwable)
                Log.e("TomatoesViewModel", throwable.message ?: "Unknown error", throwable)
            },
            block = block
        )
}