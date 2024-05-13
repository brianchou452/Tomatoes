package cc.seaotter.tomatoes.ui.splash


import android.util.Log
import androidx.compose.runtime.mutableStateOf
import cc.seaotter.tomatoes.data.service.AccountService
import cc.seaotter.tomatoes.data.service.LogService
import cc.seaotter.tomatoes.ui.TomatoesViewModel
import cc.seaotter.tomatoes.ui.navigation.TomatoesRoute
import com.google.firebase.auth.FirebaseAuthException
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class SplashViewModel @Inject constructor(
//    configurationService: ConfigurationService,
    private val accountService: AccountService,
    logService: LogService
) : TomatoesViewModel(logService) {
    val showError = mutableStateOf(false)


    fun onAppStart(openAndPopUp: (String, String) -> Unit) {
        Log.d("SplashViewModel", "onAppStart: ")
        showError.value = false
        if (accountService.hasUser) {
            Log.d("SplashViewModel", "hasUser: ")
            openAndPopUp(TomatoesRoute.TODO, TomatoesRoute.SPLASH)
        } else createAnonymousAccount(openAndPopUp)
    }

    private fun createAnonymousAccount(openAndPopUp: (String, String) -> Unit) {
        launchCatching(snackbar = false) {
            try {
                accountService.createAnonymousAccount()
            } catch (ex: FirebaseAuthException) {
                showError.value = true
                throw ex
            }
            Log.d("SplashViewModel", "createAnonymousAccount: ")
            openAndPopUp(TomatoesRoute.TODO, TomatoesRoute.SPLASH)
        }
    }
}