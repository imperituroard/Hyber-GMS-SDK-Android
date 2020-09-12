package com.hyber.android.hybersdkandroid

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.hyber.android.hybersdkandroid.add.HyberParsing
import com.hyber.android.hybersdkandroid.add.RewriteParams
import com.hyber.android.hybersdkandroid.core.HyberApi
import com.hyber.android.hybersdkandroid.core.PushSdkParameters
import com.hyber.android.hybersdkandroid.core.HyberPublicParams
import java.util.concurrent.Executors


internal class HyberFirebaseService : FirebaseMessagingService() {

    private var api: HyberApi = HyberApi()
    private var parsing: HyberParsing = HyberParsing()


    //private var init_hyber: Initialization = Initialization(applicationContext)
    //private var hyber_main: HyberSDK = HyberSDK(init_hyber.paramsglobal.hyber_user_msisdn, init_hyber.paramsglobal.hyber_user_Password, applicationContext)
    //private var hyber_update_params: RewriteParams = RewriteParams(applicationContext)


    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "MyService onCreate")
        var es = Executors.newFixedThreadPool(1)
        var someRes = Any()

    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "MyService onDestroy")
        var someRes: Nothing? = null
    }

    override fun onNewToken(s: String) {
        super.onNewToken(s)
        Log.d(
            TAG,
            "Result: Start step1, Function: onNewToken, Class: HyberFirebaseService, new_token: $s"
        )
        //HyberParameters.firebase_registration_token = s
        //hyber_update_params.rewrite_firebase_token(s)
        //hyber_main.hyber_update_registration()

        try {
            if (s != "") {
                val hyberUpdateParams = RewriteParams(applicationContext)
                hyberUpdateParams.rewriteFirebaseToken(s)
                Log.d(TAG, "HyberFirebaseService.onNewToken local update: success")
            }

        } catch (e: Exception) {
            Log.d(TAG, "HyberFirebaseService.onNewToken local update: unknown error")
        }

        try {
            if (PushSdkParameters.hyber_registration_token != "" && PushSdkParameters.firebase_registration_token != "") {
                api.hDeviceUpdate(
                    PushSdkParameters.hyber_registration_token,
                    PushSdkParameters.firebase_registration_token,
                    PushSdkParameters.hyber_deviceName,
                    PushSdkParameters.hyber_deviceType,
                    PushSdkParameters.hyber_osType,
                    PushSdkParameters.sdkVersion,
                    s
                )
                Log.d(TAG, "HyberFirebaseService.onNewToken update: success")
            } else {
                Log.d(TAG, "HyberFirebaseService.onNewToken update: failed")
            }

        } catch (e: Exception) {
            Log.d(TAG, "HyberFirebaseService.onNewToken update: unknown error")
        }





        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
    }


    override fun onMessageReceived(remoteMessage: RemoteMessage) {

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
        Log.d(TAG, "From: " + remoteMessage.from!!)

        // Check if message contains a data payload.
        if (remoteMessage.data.isNotEmpty()) {
            try {

                if (PushSdkParameters.firebase_registration_token != "" && PushSdkParameters.hyber_registration_token != "") {

                    api.hMessageDr(
                        parsing.parseMessageId(remoteMessage.data.toString()),
                        PushSdkParameters.firebase_registration_token,
                        PushSdkParameters.hyber_registration_token
                    )
                    Log.d(TAG, "delivery report success: messid ${remoteMessage.messageId.toString()}, fbtoken: ${PushSdkParameters.firebase_registration_token}, hybertoken: ${PushSdkParameters.hyber_registration_token}")
                } else {
                    Log.d(TAG, "delivery report failed: messid ${remoteMessage.messageId.toString()}, fbtoken: ${PushSdkParameters.firebase_registration_token}, hybertoken: ${PushSdkParameters.hyber_registration_token}")
                }
            } catch (e: Exception) {
                Log.d(TAG, "onMessageReceived: failed")
            }

            try {
                HyberPushMess.message = remoteMessage.data.toString()
                val intent = Intent()
                intent.action = "com.hyber.android.hybersdkandroid.Push"
                //intent.putExtra("Data", 1000)
                //intent.flags = Intent.FLAG_INCLUDE_STOPPED_PACKAGES
                sendBroadcast(intent)

            } catch (e: Exception) {
                HyberPushMess.message = ""
            }

            Log.d(TAG, "Message data payload: " + remoteMessage.data.toString())
        }

        // Check if message contains a notification payload.
        if (remoteMessage.notification != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.notification!!.body!!)

            try {
                sendNotification(remoteMessage)
            } catch (ee: Exception) {
            }

            /*
            try {
                api.hyber_message_dr(
                    parsing.parse_message_id(remoteMessage.data.toString()),
                    HyberParameters.firebase_registration_token,
                    HyberParameters.hyber_registration_token
                )
                println("delivery report success: messid ${remoteMessage.messageId.toString()}, fbtoken: ${HyberParameters.firebase_registration_token}, hybertoken: ${ HyberParameters.hyber_registration_token}")
            }
            catch (e: Exception) {
                println("failed")
            }

             */

        }
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }


    private fun sendNotification(remoteMessage: RemoteMessage) {
        val notificationObject = HyberPublicParams()
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(
            0 /* ID of notification */,
            notificationObject.notificationBuilder(
                applicationContext,
                remoteMessage.notification!!.body.toString()
            ).build()
        )
    }

    private companion object {
        private const val TAG = "FCMService"
    }
}

