package com.example.funfactoftheday.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

//This class is triggered every alarmManager.setInexactRepeating is set
class AlarmReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val extras = intent!!.getStringArrayExtra("EXTRA_MESSAGE")?: return
        val message = extras.randomOrNull()!!.toString()
        val service = FunFactNotificationService(context!!)
        service.showNotification(message)
        println("Alarm Triggered $message")
    }
}