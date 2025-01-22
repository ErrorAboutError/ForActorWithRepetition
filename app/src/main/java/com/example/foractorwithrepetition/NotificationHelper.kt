package com.example.foractorwithrepetition

import android.R
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
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

    fun sendNotification(title: String, message: String, coordinate: String) {
        val notificationId = UUID.randomUUID().hashCode() // Генерация уникального ID
        val intent = Intent(context, MainActivity::class.java) // Укажите нужный класс
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE )
        // Добавление карты в оповещение
        val map = Intent(Intent.ACTION_VIEW)
        map.setData(Uri.parse("geo:${coordinate }"))
        val mapPI = PendingIntent.getActivity(context, 0, map, PendingIntent.FLAG_IMMUTABLE)
        val action: NotificationCompat.Action = NotificationCompat.Action(
            com.example.foractorwithrepetition.R.drawable.theatre,
            "На карте",
            mapPI
        )
        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .addAction(action)
            .build()

        val manager = context.getSystemService(NotificationManager::class.java)
        manager?.notify(notificationId, notification)
    }
}