package cc.seaotter.tomatoes.data.service

import cc.seaotter.tomatoes.data.Todo
import cc.seaotter.tomatoes.data.TodoHistory
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

interface DatabaseService {
    val todos: Flow<List<Todo>>
    suspend fun getTodo(todoId: String): Todo?
    suspend fun save(todo: Todo): String
    suspend fun update(todo: Todo)
    suspend fun delete(todoId: String)
    suspend fun save(todoHistory: TodoHistory)
    suspend fun getTodoHistories(start: LocalDate, end: LocalDate): Flow<List<TodoHistory>>
}
