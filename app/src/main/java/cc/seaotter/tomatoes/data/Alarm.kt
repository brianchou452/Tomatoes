package cc.seaotter.tomatoes.data

import cc.seaotter.tomatoes.data.AlarmType.TOMATO

data class Alarm(
    val todoId: String = "",
    val title: String = "",
    val duration: Long = 0,
    val type: AlarmType = TOMATO
)

enum class AlarmType {
    TOMATO, BREAK
}