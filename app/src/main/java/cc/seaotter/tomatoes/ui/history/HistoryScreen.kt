package cc.seaotter.tomatoes.ui.history

import android.util.Log
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.himanshoe.kalendar.Kalendar
import com.himanshoe.kalendar.KalendarEvent
import com.himanshoe.kalendar.KalendarType
import com.himanshoe.kalendar.ui.firey.DaySelectionMode
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn

@Composable
fun HistoryScreen() {
    val events1 = (0..5).map {
        KalendarEvent(
            date = Clock.System.todayIn(
                TimeZone.currentSystemDefault()
            ).plus(it, DateTimeUnit.DAY),
            eventName = it.toString(),
        )
    }

    Spacer(modifier = Modifier.padding(vertical = 8.dp))
    Kalendar(
        currentDay = Clock.System.todayIn(
            TimeZone.currentSystemDefault()
        ),
        kalendarType = KalendarType.Firey,
        daySelectionMode = DaySelectionMode.Range,
        events = com.himanshoe.kalendar.KalendarEvents(events1 + events1 + events1),
        onRangeSelected = { range, rangeEvents ->
            Log.d(":onRangeSelected", range.toString())
            Log.d(":onRangeSelected", rangeEvents.toString())
        }
    )
}

