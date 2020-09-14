package com.hyber.android.hybersdkandroid

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.hyber.android.hybersdkandroid.add.GetInfo
import com.hyber.android.hybersdkandroid.add.HyberParsing
import com.hyber.android.hybersdkandroid.add.RewriteParams
import com.hyber.android.hybersdkandroid.core.HyberApi
import com.hyber.android.hybersdkandroid.core.PushSdkParameters
import com.hyber.android.hybersdkandroid.core.HyberPublicParams
import com.hyber.android.hybersdkandroid.logger.HyberLoggerSdk


internal class HyberFirebaseService : FirebaseMessagingService() {

    private var api: HyberApi = HyberApi()
    private var parsing: HyberParsing = HyberParsing()
    private var getDevInform: GetInfo = GetInfo()

    override fun onCreate() {
        super.onCreate()
        HyberLoggerSdk.debug("HyberFirebaseService.onCreate : MyService onCreate")
    }

    override fun onDestroy() {
        super.onDestroy()
        HyberLoggerSdk.debug("HyberFirebaseService.onDestroy : MyService onDestroy")
    }

    override fun onNewToken(s: String) {
        super.onNewToken(s)
        HyberLoggerSdk.debug("HyberFirebaseService.onNewToken : Result: Start step1, Function: onNewToken, Class: HyberFirebaseService, new_token: $s")

        try {
            if (s != "") {
                val hyberUpdateParams = RewriteParams(applicationContext)
                hyberUpdateParams.rewriteFirebaseToken(s)
                HyberLoggerSdk.debug("HyberFirebaseService.onNewToken : local update: success")
            }
        } catch (e: Exception) {
            HyberLoggerSdk.debug("HyberFirebaseService.onNewToken : local update: unknown error")
        }

        try {
            if (HyberDatabase.hyber_registration_token != "" && HyberDatabase.firebase_registration_token != "") {
                val answerPlatform = api.hDeviceUpdate(
                    HyberDatabase.hyber_registration_token,
                    HyberDatabase.firebase_registration_token,
                    PushSdkParameters.hyber_deviceName,
                    getDevInform.getPhoneType(applicationContext),
                    PushSdkParameters.hyber_osType,
                    PushSdkParameters.sdkVersion,
                    s
                )
                HyberLoggerSdk.debug("HyberFirebaseService.onNewToken : update success $answerPlatform")
            } else {
                HyberLoggerSdk.debug("HyberFirebaseService.onNewToken : update: failed")
            }

        } catch (e: Exception) {
            HyberLoggerSdk.debug("HyberFirebaseService.onNewToken : update: unknown error")
        }

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
    }


    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        HyberLoggerSdk.debug("HyberFirebaseService.onMessageReceived : started")

        super.onMessageReceived(remoteMessage)

        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        HyberLoggerSdk.debug("From: " + remoteMessage.from!!)

        // Check if message contains a data payload.
        if (remoteMessage.data.isNotEmpty()) {
            try {

                if (HyberDatabase.firebase_registration_token != "" && HyberDatabase.hyber_registration_token != "") {
                    val hyberAnswer = api.hMessageDr(
                        parsing.parseMessageId(remoteMessage.data.toString()),
                        HyberDatabase.firebase_registration_token,
                        HyberDatabase.hyber_registration_token
                    )
                    HyberLoggerSdk.debug("From Message Delivery Report: $hyberAnswer")
                    HyberLoggerSdk.debug("delivery report success: messid ${remoteMessage.messageId.toString()}, token: ${HyberDatabase.firebase_registration_token}, hyberToken: ${HyberDatabase.hyber_registration_token}")
                } else {
                    HyberLoggerSdk.debug("delivery report failed: messid ${remoteMessage.messageId.toString()}, token: ${HyberDatabase.firebase_registration_token}, hyberToken: ${HyberDatabase.hyber_registration_token}")
                }
            } catch (e: Exception) {
                HyberLoggerSdk.debug("onMessageReceived: failed")
            }

            try {
                HyberPushMess.message = remoteMessage.data.toString()
                val intent = Intent()
                intent.action = "com.hyber.android.hybersdkandroid.Push"
                sendBroadcast(intent)

            } catch (e: Exception) {
                HyberPushMess.message = ""
            }
            HyberLoggerSdk.debug("Message data payload: " + remoteMessage.data.toString())
        }

        // Check if message contains a notification payload.
        if (remoteMessage.notification != null) {
            HyberLoggerSdk.debug("Message Notification Body: " + remoteMessage.notification!!.body!!)

            try {
                sendNotification(remoteMessage)
            } catch (ee: Exception) {
                HyberLoggerSdk.debug("Notification payload sendNotification: Unknown Fail")
            }
        }
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    private fun sendNotification(remoteMessage: RemoteMessage) {
        val notificationObject = HyberPublicParams()
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(
            137923, // ID of notification
            notificationObject.notificationBuilder(
                applicationContext,
                remoteMessage.notification!!.body.toString()
            ).build()
        )
    }
}

