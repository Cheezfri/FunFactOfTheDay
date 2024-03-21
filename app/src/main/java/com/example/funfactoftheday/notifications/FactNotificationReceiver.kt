package com.example.funfactoftheday.notifications

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class FactNotificationReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(1000)


        val service = FunFactNotificationService(context)
        service.showNotification("Test Fact ${Math.random() * 10}")
    }

}