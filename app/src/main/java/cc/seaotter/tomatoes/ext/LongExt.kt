package cc.seaotter.tomatoes.ext


import java.util.Locale

fun Long.formatTime(): String {
    if (this < 0) {
        return "00:00"
    }
    val totalSeconds = this / 1000
    val minutes = totalSeconds / 60
    val remainingSeconds = totalSeconds % 60
    return String.format(Locale.getDefault(), "%02d:%02d", minutes, remainingSeconds)
}

fun Long.formatTimeWithText(): String {
    if (this < 0) {
        return "0 分"
    }
    val totalSeconds = this / 1000
    val minutes = totalSeconds / 60
    if (minutes < 60) {
        return String.format(Locale.getDefault(), "%d 分", minutes)
    } else {
        val hours = minutes / 60
        val remainingMinutes = minutes % 60
        return String.format(Locale.getDefault(), "%d 時 %d 分", hours, remainingMinutes)
    }
}