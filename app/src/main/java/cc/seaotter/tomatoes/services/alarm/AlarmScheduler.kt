package cc.seaotter.tomatoes.services.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.util.Log
import cc.seaotter.tomatoes.data.Alarm
import cc.seaotter.tomatoes.data.AlarmType
import cc.seaotter.tomatoes.ext.formatTime

class AlarmScheduler(
    private val context: Context
) {

    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    fun schedule(alarmItem: Alarm) {

        if (EXACT_ALARM_MODE) {
            val alarmTime = System.currentTimeMillis() + alarmItem.duration
            alarmManager.setAlarmClock(
                AlarmManager.AlarmClockInfo(alarmTime, getPendingIntent(alarmItem)),
                getPendingIntent(alarmItem)
            )
        } else {
            val alarmTime = SystemClock.elapsedRealtime() + alarmItem.duration
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.ELAPSED_REALTIME_WAKEUP, alarmTime, PendingIntent.getBroadcast(
                    context,
                    alarmItem.hashCode(),
                    getIntent(alarmItem),
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            )
        }

        Log.d("AlarmScheduler", "Alarm set after ${alarmItem.duration.formatTime()}")
    }

    private fun getIntent(alarmItem: Alarm): Intent {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            when (alarmItem.type) {
                AlarmType.TOMATO -> putExtra("EXTRA_MESSAGE", "休息一下吧！")
                AlarmType.BREAK -> putExtra("EXTRA_MESSAGE", "準備好繼續了嗎？")
            }
        }
        return intent
    }

    private fun getPendingIntent(alarmItem: Alarm): PendingIntent {
        return PendingIntent.getBroadcast(
            context,
            alarmItem.hashCode(),
            getIntent(alarmItem),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    fun cancel(alarmItem: Alarm) {
        alarmManager.cancel(
            PendingIntent.getBroadcast(
                context,
                alarmItem.hashCode(),
                Intent(context, AlarmReceiver::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
        Log.d("AlarmScheduler", "Alarm cancelled")
    }

    companion object {
        private const val EXACT_ALARM_MODE = true
    }
}