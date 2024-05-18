package cc.seaotter.tomatoes.data

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class TodoHistory(
    @DocumentId val id: String = "",
    @ServerTimestamp val finishedAt: Date = Date(),
    val title: String = "",
    val todoId: String = "",
    val userId: String = "",
    val duration: Long = 0
)