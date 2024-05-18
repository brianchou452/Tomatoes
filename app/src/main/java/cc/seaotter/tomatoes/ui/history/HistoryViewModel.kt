package cc.seaotter.tomatoes.ui.history


import android.util.Log
import cc.seaotter.tomatoes.data.TodoHistory
import cc.seaotter.tomatoes.data.service.DatabaseService
import cc.seaotter.tomatoes.data.service.LogService
import cc.seaotter.tomatoes.ui.TomatoesViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.todayIn
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val databaseService: DatabaseService,
    logService: LogService
) : TomatoesViewModel(logService) {


    private val queryRangeState: MutableStateFlow<QueryRangeState>
    val todoHistories: Flow<List<TodoHistory>>

    init {
        val now = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val queryStartDate = LocalDate(now.year, now.month.minus(1), 1)
        val endMonth = LocalDate(now.year, now.month.plus(2), 1)
        val queryEndDate = endMonth.minus(1, DateTimeUnit.DAY)
        queryRangeState = MutableStateFlow(QueryRangeState(queryStartDate, queryEndDate))

        todoHistories = queryRangeState.flatMapLatest { state ->
            databaseService.getTodoHistories(state.startDate, state.endDate)
        }
    }

    fun updateQueryDate(date: LocalDate) {
        // fixme: 換個可以節省API call的方式
        val newQueryStartDate = LocalDate(date.year, date.month.minus(1), 1)
        val endMonth = LocalDate(date.year, date.month.plus(2), 1)
        val newQueryEndDate = endMonth.minus(1, DateTimeUnit.DAY)

        if (newQueryStartDate != queryRangeState.value.startDate ||
            newQueryEndDate != queryRangeState.value.endDate
        ) {
            queryRangeState.value = queryRangeState.value.copy(
                startDate = newQueryStartDate,
                endDate = newQueryEndDate
            )
            Log.d("HistoryViewModel", "updateQueryDate: $newQueryStartDate - $newQueryEndDate")
        }
    }

}

data class QueryRangeState(
    val startDate: LocalDate,
    val endDate: LocalDate
)