package cc.seaotter.tomatoes.data.service.impl

import cc.seaotter.tomatoes.data.Todo
import cc.seaotter.tomatoes.data.service.AccountService
import cc.seaotter.tomatoes.data.service.DatabaseService
import cc.seaotter.tomatoes.data.service.trace
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.dataObjects
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class DatabaseServiceImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: AccountService
) : DatabaseService {

    private val collection
        get() = firestore.collection(TODO_COLLECTION)
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
        trace(SAVE_TASK_TRACE) {
            val updatedTodo = todo.copy(userId = auth.currentUserId)
            firestore.collection(TODO_COLLECTION).add(updatedTodo).await().id
        }

    override suspend fun update(todo: Todo): Unit =
        trace(UPDATE_TASK_TRACE) {
            firestore.collection(TODO_COLLECTION).document(todo.id).set(todo).await()
        }

    override suspend fun delete(todoId: String) {
        firestore.collection(TODO_COLLECTION).document(todoId).delete().await()
    }


    companion object {
        private const val USER_ID_FIELD = "userId"
        private const val COMPLETED_FIELD = "completed"
        private const val CREATED_AT_FIELD = "createdAt"
        private const val TODO_COLLECTION = "todos"
        private const val SAVE_TASK_TRACE = "saveTodo"
        private const val UPDATE_TASK_TRACE = "updateTodo"
    }
}
