package cc.seaotter.tomatoes.ui.achievement

import androidx.compose.runtime.mutableStateOf
import cc.seaotter.tomatoes.R
import cc.seaotter.tomatoes.data.service.AccountService
import cc.seaotter.tomatoes.data.service.DatabaseService
import cc.seaotter.tomatoes.data.service.LogService
import cc.seaotter.tomatoes.ui.TomatoesViewModel
import cc.seaotter.tomatoes.ui.navigation.TomatoesRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.todayIn
import javax.inject.Inject

@HiltViewModel
class AchievementViewModel @Inject constructor(
    private val accountService: AccountService,
    private val databaseService: DatabaseService,
    logService: LogService
) : TomatoesViewModel(logService) {

    val uiState = mutableStateOf(
        AchievementUiState()
    )

    init {
        initAchievement()
        loadAchievement(TimeRange.DAY)
    }

    private fun initAchievement() {
        launchCatching {
            uiState.value = uiState.value.copy(
                totalTomatoes = databaseService.getTotalTomatoes()
            )
        }
    }

    fun loadAchievement(range: TimeRange) {
        val endDate = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val startDate = when (range) {
            TimeRange.DAY -> endDate.minus(1, DateTimeUnit.DAY)
            TimeRange.WEEK -> endDate.minus(1, DateTimeUnit.WEEK)
            TimeRange.MONTH -> endDate.minus(1, DateTimeUnit.MONTH)
            TimeRange.YEAR -> endDate.minus(1, DateTimeUnit.YEAR)
        }
        launchCatching {
            uiState.value = uiState.value.copy(
                tomatoesInRange = databaseService.getTotalTomatoes(start = startDate, end = endDate)
            )
        }
    }

    fun signOut(
        clearAndNavigate: (String) -> Unit
    ) {
        launchCatching {
            accountService.signOut()
        }
        clearAndNavigate(TomatoesRoute.LOGIN)
    }

}

data class AchievementUiState(
    val totalTomatoes: Int = 0,
    val tomatoesInRange: Int = 0,
    val totalFocusDays: Int = 0,
    val totalFocusTime: Long = 0,
    val focusTimeInRange: Long = 0,
    val timeRange: TimeRange = TimeRange.DAY
)

enum class TimeRange(val value: Int) {
    DAY(R.string.day),
    WEEK(R.string.week),
    MONTH(R.string.month),
    YEAR(R.string.year)
}