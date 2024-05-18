package cc.seaotter.tomatoes.data.service.impl

import android.util.Log
import cc.seaotter.tomatoes.data.Todo
import cc.seaotter.tomatoes.data.TodoHistory
import cc.seaotter.tomatoes.data.service.AccountService
import cc.seaotter.tomatoes.data.service.DatabaseService
import cc.seaotter.tomatoes.data.service.trace
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.dataObjects
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.tasks.await
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toJavaLocalDate
import java.time.ZoneId
import java.util.Date
import javax.inject.Inject


class DatabaseServiceImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: AccountService
) : DatabaseService {

    private val historyCollection
        get() = firestore.collection(HISTORY_COLLECTION)
            .whereEqualTo(USER_ID_FIELD, auth.currentUserId)

    @OptIn(ExperimentalCoroutinesApi::class)
    override val todos: Flow<List<Todo>>
        get() =
            auth.currentUser.flatMapLatest { user ->
                firestore
                    .collection(TODO_COLLECTION)
                    .whereEqualTo(USER_ID_FIELD, user.id)
                    .orderBy(CREATED_AT_FIELD, Query.Direction.DESCENDING)
                    .dataObjects()
            }

    override suspend fun getTodo(todoId: String): Todo? =
        firestore.collection(TODO_COLLECTION).document(todoId).get().await().toObject()

    override suspend fun save(todo: Todo): String =
        trace(SAVE_TODO_TRACE) {
            val updatedTodo = todo.copy(userId = auth.currentUserId)
            firestore.collection(TODO_COLLECTION).add(updatedTodo).await().id
        }

    override suspend fun update(todo: Todo): Unit =
        trace(UPDATE_TODO_TRACE) {
            firestore.collection(TODO_COLLECTION).document(todo.id).set(todo).await()
        }

    override suspend fun delete(todoId: String) {
        firestore.collection(TODO_COLLECTION).document(todoId).delete().await()
    }

    override suspend fun save(todoHistory: TodoHistory) {
        return trace(SAVE_HISTORY_TRACE) {
            val updatedHistory = todoHistory.copy(userId = auth.currentUserId)
            firestore.collection(HISTORY_COLLECTION).add(updatedHistory).await()
        }
    }

    override suspend fun getTodoHistories(
        start: LocalDate,
        end: LocalDate
    ): Flow<List<TodoHistory>> {
        val startDate =
            Date.from(start.toJavaLocalDate().atStartOfDay(ZoneId.systemDefault()).toInstant())
        val endDate =
            Date.from(end.toJavaLocalDate().atStartOfDay(ZoneId.systemDefault()).toInstant())
        trace(GET_HISTORIES_TRACE) {
            val historyMap: MutableMap<String, TodoHistory> = mutableMapOf()
            val list = MutableStateFlow<List<TodoHistory>>(emptyList())

            historyCollection
                .whereGreaterThanOrEqualTo(FINISHED_AT_FIELD, startDate)
                .whereLessThanOrEqualTo(FINISHED_AT_FIELD, endDate)
                .orderBy(FINISHED_AT_FIELD, Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener { documents ->
                    documents.forEach { document ->
                        val history = document.toObject<TodoHistory>()
                        val key = history.todoId + history.finishedAt.toInstant()
                            .atZone(ZoneId.systemDefault()).toLocalDate().toString()
                        if (historyMap.containsKey(key)) {
                            historyMap[key] = historyMap[key]!!
                                .copy(duration = historyMap[key]!!.duration + history.duration)
                        } else {
                            historyMap[key] = history
                        }
                    }
                    list.value = historyMap.values.toList()
                    Log.d("DatabaseServiceImpl", "getTodoHistories: $start, $end")
                    Log.d("DatabaseServiceImpl", "getTodoHistories: ${list.value}")
                }
                .addOnFailureListener { exception ->
                    throw exception
                }

            return list.asStateFlow()
        }
    }

    companion object {
        private const val USER_ID_FIELD = "userId"
        private const val CREATED_AT_FIELD = "createdAt"
        private const val FINISHED_AT_FIELD = "finishedAt"
        private const val TODO_COLLECTION = "todos"
        private const val HISTORY_COLLECTION = "histories"
        private const val SAVE_TODO_TRACE = "saveTodo"
        private const val UPDATE_TODO_TRACE = "updateTodo"
        private const val SAVE_HISTORY_TRACE = "saveHistory"
        private const val GET_HISTORIES_TRACE = "getHistories"
    }
}
