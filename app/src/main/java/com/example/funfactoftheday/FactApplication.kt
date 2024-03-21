package com.example.funfactoftheday

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.example.funfactoftheday.database.AppDatabase
import com.example.funfactoftheday.database.AppRepository
import com.example.funfactoftheday.notifications.FunFactNotificationService
import com.google.android.material.color.DynamicColors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class FactApplication: Application() {
    // No need to cancel this scope as it'll be torn down with the process
    val applicationScope = CoroutineScope(SupervisorJob())

    // Using by lazy so the database and the repository are only created when they're needed
    // rather than when the application starts
    val database by lazy {AppDatabase.getDatabase(this, applicationScope)}
    val repository by lazy { AppRepository(database.appDao()) }

    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)
        createNotificationChannel()
    }

    private fun createNotificationChannel(){
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            val channel = NotificationChannel(
                FunFactNotificationService.Fun_Fact_Channel_ID,
                "Fun Fact Push Notifications",
                NotificationManager.IMPORTANCE_LOW
            )
            channel.description = "Sends facts as notifications, adjustable in the settings of app"
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

}