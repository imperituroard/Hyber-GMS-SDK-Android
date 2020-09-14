package com.hyber.android.hybersdkandroid


import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.hyber.android.hybersdkandroid.core.Initialization

class HyberMessaging : Service() {
    private var notificationManager: NotificationManager? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (Build.VERSION.SDK_INT <= 25) {
            //Send Foreground Notification
            sendNotification("", "", "")
            val initHyber = Initialization(applicationContext)
            initHyber.hSdkInit2()
        } else {
            val initHyber = Initialization(applicationContext)
            initHyber.hSdkInit2()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    //Send custom notification
    @Suppress("unused")
    fun sendNotification(Ticker: String, Title: String, Text: String) {

        //These three lines makes Notification to open main activity after clicking on it
        val notificationIntent = Intent(this, applicationContext::class.java)
        notificationIntent.action = Intent.ACTION_MAIN
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER)

        val contentIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(this, "DEFAULT_NOTIFICATION_ID")
        builder.setContentIntent(contentIntent)
            .setOngoing(true)   //Can't be swiped out
            //.setSmallIcon(R.mipmap.sym_def_app_icon)
            //.setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.large))   // большая картинка
            .setTicker(Ticker)
            .setContentTitle(Title) //Заголовок
            .setContentText(Text) // Текст уведомления
            .setWhen(System.currentTimeMillis())

        val notification: Notification
        notification = if (Build.VERSION.SDK_INT <= 15) {
            builder.notification // API-15 and lower
        } else {
            builder.build()
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
        const val DEFAULT_NOTIFICATION_ID = 103
    }
}