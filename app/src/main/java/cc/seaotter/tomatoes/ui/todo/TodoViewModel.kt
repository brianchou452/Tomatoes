package cc.seaotter.tomatoes.ui.todo

import cc.seaotter.tomatoes.data.Todo
import cc.seaotter.tomatoes.data.service.DatabaseService
import cc.seaotter.tomatoes.data.service.LogService
import cc.seaotter.tomatoes.ui.TomatoesViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TodoViewModel @Inject constructor(
    logService: LogService,
    private val databaseService: DatabaseService
) : TomatoesViewModel(logService) {
    val todos = databaseService.todos


    fun addTodo(todo: Todo) {
        val newTodo = todo.copy(durationPerTomato = TOMATO_DURATION)
        launchCatching {
            databaseService.save(newTodo)
        }
    }

    fun onTodoChange(todo: Todo) {
        launchCatching {
            databaseService.update(todo)
        }
    }

    companion object {
        private const val TOMATO_DURATION = 25 * 60 * 1000L
    }

}
