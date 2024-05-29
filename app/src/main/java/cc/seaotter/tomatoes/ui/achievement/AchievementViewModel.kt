package cc.seaotter.tomatoes.ui.achievement

import cc.seaotter.tomatoes.data.service.AccountService
import cc.seaotter.tomatoes.data.service.DatabaseService
import cc.seaotter.tomatoes.data.service.LogService
import cc.seaotter.tomatoes.ui.TomatoesViewModel
import cc.seaotter.tomatoes.ui.navigation.TomatoesRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AchievementViewModel @Inject constructor(
    private val accountService: AccountService,
    private val databaseService: DatabaseService,
    logService: LogService
) : TomatoesViewModel(logService) {
    fun signOut(
        clearAndNavigate: (String) -> Unit
    ) {
        launchCatching {
            accountService.signOut()
        }
        clearAndNavigate(TomatoesRoute.LOGIN)
    }
}