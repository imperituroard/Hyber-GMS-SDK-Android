package com.hyber.android.hybersdkandroid

import android.R.string.cancel
import android.content.Intent
import android.os.IBinder
//import android.R
//import androidx.core.app.NotificationCompat
import android.app.PendingIntent
//import androidx.core.app.ApplicationProvider.getApplicationContext
import android.app.Service.START_REDELIVER_INTENT
import android.content.Context.NOTIFICATION_SERVICE
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.app.NotificationCompat;
import android.app.NotificationManager
import android.app.Service
import android.app.Notification
import android.os.Build
import com.hyber.android.hybersdkandroid.add.RewriteParams
import com.hyber.android.hybersdkandroid.core.Initialization

public class HyberMessaging : Service() {
    private var notificationManager: NotificationManager? = null

    override fun onCreate() {
        super.onCreate()

        //notificationManager = this.getSystemService(this.NOTIFICATION_SERVICE) as NotificationManager
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        /*
        if (Build.VERSION.SDK_INT <= 25) {
            //Send Foreground Notification
            sendNotification("", "", "")


            val init_hyber: Initialization = Initialization(applicationContext)
            init_hyber.hyber_init2()

            //return Service.START_STICKY;
            return super.onStartCommand(intent, flags, startId);
            //return START_REDELIVER_INTENT
        } else {
            val init_hyber: Initialization = Initialization(applicationContext)
            init_hyber.hyber_init2()

            return super.onStartCommand(intent, flags, startId);
        }
        */

        val init_hyber: Initialization = Initialization(applicationContext)
        init_hyber.hyber_init2()

        return super.onStartCommand(intent, flags, startId);


    }

    //Send custom notification
    fun sendNotification(Ticker: String, Title: String, Text: String) {

        //These three lines makes Notification to open main activity after clicking on it
        val notificationIntent = Intent(this, applicationContext::class.java)
        notificationIntent.setAction(Intent.ACTION_MAIN)
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