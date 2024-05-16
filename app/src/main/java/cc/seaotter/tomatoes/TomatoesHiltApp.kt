package cc.seaotter.tomatoes

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import cc.seaotter.tomatoes.services.notification.Notification
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class TomatoesHiltApp : Application() {
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val channelId = Notification.ALARM_CHANNEL_ID
        val channelName = Notification.ALARM_CHANNEL_NAME
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager.createNotificationChannel(channel)
    }
}