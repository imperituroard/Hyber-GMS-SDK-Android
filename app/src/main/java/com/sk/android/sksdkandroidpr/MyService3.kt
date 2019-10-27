package com.sk.android.sksdkandroidpr

import android.R.string.cancel
import android.content.Intent
import android.os.IBinder
import android.R
import androidx.core.app.NotificationCompat
import android.app.PendingIntent
//import androidx.core.app.ApplicationProvider.getApplicationContext
import android.app.Service.START_REDELIVER_INTENT
import android.content.Context.NOTIFICATION_SERVICE
import androidx.core.content.ContextCompat.getSystemService
import android.app.NotificationManager
import android.app.Service
import android.app.Notification

class MyService3 : Service() {
    private var notificationManager: NotificationManager? = null

    override fun onCreate() {
        super.onCreate()

        //notificationManager = this.getSystemService(this.NOTIFICATION_SERVICE) as NotificationManager
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        //Send Foreground Notification
        sendNotification("", "", "")

        //Task
        //doTask()

        //return Service.START_STICKY;
        return super.onStartCommand(intent, flags, startId);
        //return START_REDELIVER_INTENT
    }

    //Send custom notification
    fun sendNotification(Ticker: String, Title: String, Text: String) {

        //These three lines makes Notification to open main activity after clicking on it
        val notificationIntent = Intent(this, MainActivity::class.java)
        notificationIntent.setAction(Intent.ACTION_MAIN)
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER)

        val contentIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(this)
        builder.setContentIntent(contentIntent)
            .setOngoing(true)   //Can't be swiped out
            //.setSmallIcon(R.mipmap.sym_def_app_icon)
            //.setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.large))   // большая картинка
            .setTicker(Ticker)
            .setContentTitle(Title) //Заголовок
            .setContentText(Text) // Текст уведомления
            .setWhen(System.currentTimeMillis())

        val notification: Notification
        if (android.os.Build.VERSION.SDK_INT <= 15) {
            notification = builder.notification // API-15 and lower
        } else {
            notification = builder.build()
        }

        startForeground(DEFAULT_NOTIFICATION_ID, notification)
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()

        //Removing any notifications
        notificationManager!!.cancel(DEFAULT_NOTIFICATION_ID)

        //Disabling service
        stopSelf()
    }

    companion object {
        val DEFAULT_NOTIFICATION_ID = 103
    }
}