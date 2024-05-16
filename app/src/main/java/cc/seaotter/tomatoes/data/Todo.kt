package cc.seaotter.tomatoes.data

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date


data class Todo(
    @DocumentId val id: String = "",
    @ServerTimestamp val createdAt: Date = Date(),
    var title: String = "",
    val numOfTomatoes: Int = 0,
    val completedTomatoes: Int = 0,
    val durationPerTomato: Long = 0,
    val completed: Boolean = false,
    val userId: String = ""
)