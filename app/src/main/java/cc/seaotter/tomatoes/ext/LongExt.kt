package cc.seaotter.tomatoes.ext


import java.util.Locale

fun Long.formatTime(): String {
    if (this < 0) {
        return "00:00"
    }
    val totalSeconds = this / 1000
    val minutes = totalSeconds / 3600
    val remainingSeconds = totalSeconds % 60
    return String.format(Locale.getDefault(), "%02d:%02d", minutes, remainingSeconds)
}