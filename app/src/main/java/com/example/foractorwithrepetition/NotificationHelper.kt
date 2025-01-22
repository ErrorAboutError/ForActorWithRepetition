package com.example.foractorwithrepetition

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import java.util.UUID

// Класс для работы с уведомлениями
class NotificationHelper(private val context: Context) {

    private val channelId = "event_notifications"
    private val channelName = "Event Notifications"

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
        val manager = context.getSystemService(NotificationManager::class.java)
        manager?.createNotificationChannel(channel)
    }

    fun sendNotification(title: String, message: String) {
        val notificationId = UUID.randomUUID().hashCode() // Генерация уникального ID
        val intent = Intent(context, MainActivity::class.java) // Укажите нужный класс
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE )
        // Добавление действий
        val openActionIntent = Intent(context, ActivityWithDrawerNavigation::class.java)
        val openPendingIntent = PendingIntent.getActivity(
            context,
            1,
            openActionIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val dismissActionIntent = Intent(context, ActivityWithDrawerNavigation::class.java)

        val dismissPendingIntent = PendingIntent.getBroadcast(
            context,
            2,
            dismissActionIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .addAction(android.R.drawable.ic_menu_view,
                "Открыть",
                openPendingIntent)
            .addAction(
                android.R.drawable.ic_menu_close_clear_cancel,
                "Отменить",
                dismissPendingIntent
            )
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setVibrate(longArrayOf(0, 500, 250, 500))
            .build()
        val manager = context.getSystemService(NotificationManager::class.java)
        manager?.notify(notificationId, notification)

    }
}