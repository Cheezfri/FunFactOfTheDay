package com.example.funfactoftheday.notifications

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.funfactoftheday.MainActivity
import com.example.funfactoftheday.R

class FunFactNotificationService(
    private val context: Context
){
    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun showNotification(factName: String){
        val activityIntent = Intent(context, MainActivity::class.java)
        //this intent opens the homepage of app
        val activityPendingIntent = PendingIntent.getActivity(
            context,
            1,
            activityIntent,
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
        )

        //this intent is the "Get Another Fun Fact" Button
        val newFactIntent = PendingIntent.getBroadcast(
            context,
            2,
            Intent(context, FactNotificationReceiver::class.java),
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)

        val notification = NotificationCompat.Builder(context, Fun_Fact_Channel_ID)
            .setSmallIcon(R.drawable.custom_checkbox_heart)
            .setContentTitle("Fun Fact of the Day!")
            .setContentText(factName)
            .setContentIntent(activityPendingIntent)
            .addAction(
                R.drawable.custom_checkbox_heart,
                "Get another Fun Fact?",
                newFactIntent
            )
            .setAutoCancel(true)
            .build()

        notificationManager.notify(1000, notification)
    }

    companion object{
        const val Fun_Fact_Channel_ID = "fun_fact_channel"
    }
}