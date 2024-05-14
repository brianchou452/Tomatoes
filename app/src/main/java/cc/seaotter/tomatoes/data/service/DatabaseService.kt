package cc.seaotter.tomatoes.data.service

import cc.seaotter.tomatoes.data.Todo
import kotlinx.coroutines.flow.Flow

interface DatabaseService {
    val todos: Flow<List<Todo>>
    suspend fun getTodo(todoId: String): Todo?
    suspend fun save(todo: Todo): String
    suspend fun update(todo: Todo)
    suspend fun delete(todoId: String)
}
