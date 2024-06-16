package cc.seaotter.tomatoes.ui.splash


import android.util.Log
import cc.seaotter.tomatoes.data.service.AccountService
import cc.seaotter.tomatoes.data.service.LogService
import cc.seaotter.tomatoes.ui.TomatoesViewModel
import cc.seaotter.tomatoes.ui.navigation.TomatoesRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject


@HiltViewModel
class SplashViewModel @Inject constructor(
    private val accountService: AccountService,
    logService: LogService
) : TomatoesViewModel(logService) {
    private val _loading = MutableStateFlow(true)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    fun onAppStart(): String {
        Log.d("SplashViewModel", "onAppStart: ")
        if (accountService.hasUser) {
            Log.d("SplashViewModel", "hasUser: ")
            _loading.value = false
            return TomatoesRoute.TODO
        } else {
            _loading.value = false
            return TomatoesRoute.LOGIN
        }
    }

}