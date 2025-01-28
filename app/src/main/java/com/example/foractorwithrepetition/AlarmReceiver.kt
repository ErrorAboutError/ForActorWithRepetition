package com.example.foractorwithrepetition

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent


//Позволяет запускать определенные действия (например, уведомления или выполнение задач) в
// заданное время или через определенные промежутки времени.
//Фоновая работа: Помогает выполнять задачи даже тогда, когда приложение не активно или закрыто.
// Это полезно для отправки напоминаний или обновления данных. 🔔
//Гибкость: Можно настроить повторы срабатываний (например, каждый день, каждую неделю и т.д.)
// при помощи различных типов AlarmManager.
class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val rehearsalName = intent?.getStringExtra("rehearsal_name") ?: "Просто репетиция"
        if (context != null) {
            // Отправка уведомления через NotificationHelper
            val notificationHelper = NotificationHelper(context)
            notificationHelper.sendNotification("Время репетиции", rehearsalName, intent?.getStringExtra("coordinate").toString())
        }
    }
}