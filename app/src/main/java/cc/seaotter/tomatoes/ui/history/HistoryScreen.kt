package cc.seaotter.tomatoes.ui.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cc.seaotter.tomatoes.data.TodoHistory
import cc.seaotter.tomatoes.ext.formatTimeWithText
import com.himanshoe.kalendar.Kalendar
import com.himanshoe.kalendar.KalendarEvent
import com.himanshoe.kalendar.KalendarEvents
import com.himanshoe.kalendar.KalendarType
import com.himanshoe.kalendar.ui.firey.DaySelectionMode
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.todayIn
import java.time.ZoneId

@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val todoHistories = viewModel.todoHistories.collectAsStateWithLifecycle(emptyList())
    val currentDay: MutableState<LocalDate> = remember {
        mutableStateOf(Clock.System.todayIn(TimeZone.currentSystemDefault()))
    }

    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(8.dp)
            .fillMaxSize()
    ) {
        CalenderCard(
            updateDateRange = viewModel::updateQueryDate,
            todoHistories = todoHistories,
            currentDay = currentDay
        )
        HorizontalDivider(
            modifier = Modifier.padding(8.dp)
        )
        HistoryList(
            todoHistories = todoHistories,
            currentDay = currentDay
        )
    }

}

@Composable
fun CalenderCard(
    updateDateRange: (date: LocalDate) -> Unit,
    todoHistories: State<List<TodoHistory>>,
    currentDay: MutableState<LocalDate>
) {
    val events = todoHistories.value.map {
        val date = it.finishedAt.toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime()
            .toLocalDate()
        KalendarEvent(
            date = LocalDate(date.year, date.month, date.dayOfMonth),
            eventName = it.title,
        )
    }

    Card {
        Kalendar(
            currentDay = Clock.System.todayIn(
                TimeZone.currentSystemDefault()
            ),
            kalendarType = KalendarType.Firey,
            daySelectionMode = DaySelectionMode.Single,
            events = KalendarEvents(events),
            onDayClick = { day, _ ->
                updateDateRange(day)
                currentDay.value = day
            },
        )
    }
}

@Composable
fun HistoryList(
    todoHistories: State<List<TodoHistory>>,
    currentDay: MutableState<LocalDate>
) {
    val todayList = todoHistories.value.filter {
        it.finishedAt.toInstant().atZone(ZoneId.systemDefault())
            .toLocalDate() == currentDay.value.toJavaLocalDate()
    }
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        items(todayList, key = { it.id }) { todoHistory ->
            HistoryItem(todoHistory = todoHistory)
        }
    }
}

@Composable
fun HistoryItem(
    todoHistory: TodoHistory
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(text = todoHistory.title)
            Text(text = todoHistory.duration.formatTimeWithText())
        }
    }
}

