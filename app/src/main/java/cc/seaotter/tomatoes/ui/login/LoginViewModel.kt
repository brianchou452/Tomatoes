package cc.seaotter.tomatoes.ui.login

import android.util.Log
import cc.seaotter.tomatoes.data.service.AccountService
import cc.seaotter.tomatoes.data.service.LogService
import cc.seaotter.tomatoes.ui.TomatoesViewModel
import cc.seaotter.tomatoes.ui.navigation.TomatoesRoute
import com.google.firebase.auth.FirebaseAuthException
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class LoginViewModel @Inject constructor(
    private val accountService: AccountService,
    logService: LogService
) : TomatoesViewModel(logService) {
    fun createAnonymousAccount(openAndPopUp: (String, String) -> Unit) {
        launchCatching(snackbar = false) {
            try {
                accountService.createAnonymousAccount()
            } catch (ex: FirebaseAuthException) {
                throw ex
            }
            Log.d("LoginViewModel", "createAnonymousAccount")
            openAndPopUp(TomatoesRoute.TODO, TomatoesRoute.LOGIN)
        }
    }

    fun signInWithGoogle(credential: String, openAndPopUp: (String, String) -> Unit) {
        launchCatching {
            accountService.authenticate(credential)
            openAndPopUp(TomatoesRoute.TODO, TomatoesRoute.LOGIN)
        }
    }
}