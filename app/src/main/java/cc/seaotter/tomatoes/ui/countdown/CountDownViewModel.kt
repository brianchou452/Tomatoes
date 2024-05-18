package cc.seaotter.tomatoes.ui.countdown

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import cc.seaotter.tomatoes.data.Alarm
import cc.seaotter.tomatoes.data.AlarmType
import cc.seaotter.tomatoes.data.Todo
import cc.seaotter.tomatoes.data.TodoHistory
import cc.seaotter.tomatoes.data.service.DatabaseService
import cc.seaotter.tomatoes.data.service.LogService
import cc.seaotter.tomatoes.services.alarm.AlarmScheduler
import cc.seaotter.tomatoes.ui.TomatoesViewModel
import cc.seaotter.tomatoes.ui.common.snackbar.SnackbarManager
import cc.seaotter.tomatoes.ui.common.snackbar.SnackbarMessage
import cc.seaotter.tomatoes.ui.navigation.TomatoesRoute.TODO_ID
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CountDownViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    @ApplicationContext context: Context,
    private val databaseService: DatabaseService,
    logService: LogService
) : TomatoesViewModel(logService) {
    val uiState = mutableStateOf(CountDownUiState())

    private val alarmScheduler: AlarmScheduler = AlarmScheduler(context.applicationContext)
    private var alarm: Alarm = Alarm()

    private var timerJob: Job? = null
    private val _timer = MutableStateFlow(0L)
    val timer = _timer.asStateFlow()

    private var numberOfTomatoesFinished = 0


    init {
        val todoId = savedStateHandle.get<String>(TODO_ID)
        if (todoId != null) {
            launchCatching {
                uiState.value = uiState.value.copy(
                    todo = databaseService.getTodo(todoId) ?: Todo()
                )
                setNewAlarm(uiState.value.todo.durationPerTomato, AlarmType.TOMATO)
            }
        }
    }

    fun startTimer() {
        onTimerStart()
        alarmScheduler.schedule(alarm)

        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_timer.value >= 0) {
                delay(1000)
                _timer.value -= 1 * 1000
            }
            onTimerFinished()
        }
    }


    fun cancelTimer() {
        timerJob?.cancel()
        alarmScheduler.cancel(alarm)
        onTimerCancel()
    }

    private fun onTimerStart() {
        when (uiState.value.screenState) {
            CountDownScreenState.BEGIN_TOMATO -> {
                uiState.value = uiState.value.copy(
                    screenState = CountDownScreenState.IN_TOMATO
                )
            }

            CountDownScreenState.END_TOMATO_BEGIN_SHORT_BREAK -> {
                uiState.value = uiState.value.copy(
                    screenState = CountDownScreenState.IN_SHORT_BREAK
                )
            }

            CountDownScreenState.END_TOMATO_BEGIN_LONG_BREAK -> {
                uiState.value = uiState.value.copy(
                    screenState = CountDownScreenState.IN_LONG_BREAK
                )
            }

            CountDownScreenState.END_BREAK -> {
                uiState.value = uiState.value.copy(
                    screenState = CountDownScreenState.IN_TOMATO
                )
            }

            else -> {
                Log.e("CountDownViewModel", "onTimerStart: Invalid screen state")
            }
        }
    }

    private fun onTimerFinished() {
        when (uiState.value.screenState) {
            CountDownScreenState.IN_TOMATO -> {
                onFinishedTomato()
            }

            CountDownScreenState.IN_SHORT_BREAK, CountDownScreenState.IN_LONG_BREAK -> {
                uiState.value = uiState.value.copy(
                    screenState = CountDownScreenState.END_BREAK
                )
                setNewAlarm(uiState.value.todo.durationPerTomato, AlarmType.TOMATO)
            }

            else -> {
                Log.e("CountDownViewModel", "onTimerFinished: Invalid screen state")
            }
        }

    }

    private fun onTimerCancel() {
        when (uiState.value.screenState) {
            CountDownScreenState.IN_TOMATO -> {
                setNewAlarm(uiState.value.todo.durationPerTomato, AlarmType.TOMATO)
                uiState.value = uiState.value.copy(
                    screenState = CountDownScreenState.BEGIN_TOMATO
                )
                saveTodoHistory(
                    duration = uiState.value.todo.durationPerTomato - _timer.value
                )
            }

            CountDownScreenState.IN_SHORT_BREAK, CountDownScreenState.IN_LONG_BREAK -> {
                setNewAlarm(uiState.value.todo.durationPerTomato, AlarmType.TOMATO)
                uiState.value = uiState.value.copy(
                    screenState = CountDownScreenState.BEGIN_TOMATO
                )
            }

            else -> {
                Log.e("CountDownViewModel", "onTimerCancel: Invalid screen state")
            }
        }
    }

    private fun onFinishedTomato() {
        numberOfTomatoesFinished++
        updateRemainingTomatoes()
        saveTodoHistory()
        if (uiState.value.todo.completedTomatoes == uiState.value.todo.numOfTomatoes) {
            SnackbarManager.showMessage(SnackbarMessage.StringSnackbar("所有番茄完成了！"))
            // TODO: 提示使用者已經達成設定的目標，要不要繼續。
        }
        if (numberOfTomatoesFinished % TOMATO_CYCLE == 0) {
            uiState.value = uiState.value.copy(
                screenState = CountDownScreenState.END_TOMATO_BEGIN_LONG_BREAK
            )
            setNewAlarm(LONG_BREAK_DURATION, AlarmType.BREAK)
        } else {
            uiState.value = uiState.value.copy(
                screenState = CountDownScreenState.END_TOMATO_BEGIN_SHORT_BREAK
            )
            setNewAlarm(SHORT_BREAK_DURATION, AlarmType.BREAK)
        }
    }

    private fun updateRemainingTomatoes() {
        uiState.value = uiState.value.copy(
            todo = uiState.value.todo.copy(
                completedTomatoes = uiState.value.todo.completedTomatoes + 1
            )
        )
        launchCatching {
            databaseService.update(uiState.value.todo)
        }
    }

    private fun setNewAlarm(
        duration: Long, type: AlarmType
    ) {
        alarm = Alarm(
            uiState.value.todo.id, uiState.value.todo.title, duration, type
        )
        _timer.value = duration
    }

    private fun saveTodoHistory(
        duration: Long = uiState.value.todo.durationPerTomato
    ) {
        launchCatching {
            databaseService.save(
                TodoHistory(
                    title = uiState.value.todo.title,
                    todoId = uiState.value.todo.id,
                    duration = duration,
                )
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }


    companion object {
        private const val TOMATO_CYCLE = 4
        private const val SHORT_BREAK_DURATION = 5 * 1000L
        private const val LONG_BREAK_DURATION = 7 * 1000L
    }
}

data class CountDownUiState(
    val todo: Todo = Todo(),
    val screenState: CountDownScreenState = CountDownScreenState.BEGIN_TOMATO,
)

enum class CountDownScreenState {
    BEGIN_TOMATO, IN_TOMATO, END_TOMATO_BEGIN_SHORT_BREAK, END_TOMATO_BEGIN_LONG_BREAK, IN_SHORT_BREAK, IN_LONG_BREAK, END_BREAK
}