package com.example.funfactoftheday.notifications

import android.app.AlarmManager
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import timber.log.Timber
import java.time.LocalDateTime

class BootCompletedReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if(intent?.action == Intent.ACTION_BOOT_COMPLETED){
            Timber.e("I'm Rebooted!")
            context.let {
                val factsSharedPreferences = context.getSharedPreferences("FactsSharedPreferences", Context.MODE_PRIVATE)
                val settingsSharedPreferences = context.getSharedPreferences("SettingsSpinnerSharedPreferences", Context.MODE_PRIVATE)
                val facts = factsSharedPreferences.getStringSet("TestStringSet", HashSet<String>())

                val interval = when (settingsSharedPreferences.getInt("how_often", 0)){
                    0-> AlarmManager.INTERVAL_HALF_DAY
                    1-> AlarmManager.INTERVAL_DAY
                    2 -> AlarmManager.INTERVAL_DAY * 3
                    3 -> AlarmManager.INTERVAL_DAY * 6
                    else -> AlarmManager.INTERVAL_DAY
                }

                val scheduler = AndroidAlarmScheduler(context, interval)
                var fact:String? = ""
                if(!facts.isNullOrEmpty()){
                    fact = facts.randomOrNull()
                }

                val notificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.cancel(1000)

                val service = FunFactNotificationService(context)
                service.showNotification("$fact")
                if (!facts.isNullOrEmpty()) {
                    for(fact in facts){
                        Timber.e("TF: $fact")
                    }
                    val alarmItem = AlarmItem(
                        time = LocalDateTime.now().plusSeconds(5.toLong()),
                        messages = facts.toTypedArray())
                    alarmItem?.let(scheduler::schedule)
                }
            }
        }
    }
}