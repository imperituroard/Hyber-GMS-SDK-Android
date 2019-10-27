package com.sk.android.sksdkandroid

import android.util.Log
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import android.content.ContentValues.TAG
import android.content.Context
import android.app.NotificationManager
import java.util.Random
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import com.sk.android.sksdkandroid.core.SkApi
import com.sk.android.sksdkandroid.core.SkParameters
//import com.sk.android.sksdkandroid.core.SkParameters
import com.sk.android.sksdkandroid.add.RewriteParams
import com.sk.android.sksdkandroid.add.SkParsing
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import java.util.concurrent.Executors
import android.os.Messenger
import android.content.Intent
import android.app.PendingIntent
import android.app.Notification
import android.os.Build
import com.sk.android.sksdkandroid.add.SkInternal
import com.sk.android.sksdkandroid.core.Initialization
import com.sk.android.sksdkandroid.core.SkPublicParams


public class SkFirebaseService (): FirebaseMessagingService() {

    private val COUNT_PLUS = 1
    private val COUNT_MINUS = 2
    private val SET_COUNT = 0
    private val GET_COUNT = 3

    private var count = 0

    private var messanger: Messenger? = null
    private var toActivityMessenger: Messenger? = null

    private val MSG_KEY = "yo"


    private var api:SkApi = SkApi()
    private var parsing: SkParsing = SkParsing()


    //private var init_sk: Initialization = Initialization(applicationContext)
    //private var sk_main: HyberSK = HyberSK(init_sk.paramsglobal.sk_user_msisdn, init_sk.paramsglobal.sk_user_Password, applicationContext)
    //private var sk_update_params: RewriteParams = RewriteParams(applicationContext)



    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "MyService onCreate")
        var es = Executors.newFixedThreadPool(1)
        var someRes = Any()

    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "MyService onDestroy")
        var someRes = null
    }

    override fun onNewToken(s: String) {
        super.onNewToken(s)
        Log.d(
            TAG,
            "Result: Start step1, Function: onNewToken, Class: SkFirebaseService, new_token: $s"
        )
        //SkParameters.firebase_registration_token = s
        //sk_update_params.rewrite_firebase_token(s)
        //sk_main.sk_update_registration()
        api.sk_device_update(
            SkParameters.sk_registration_token,
            SkParameters.firebase_registration_token,
            SkParameters.sk_deviceName,
            SkParameters.sk_deviceType,
            SkParameters.sk_osType,
            SkParameters.sdkversion,
            s
        )
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
        if (remoteMessage.data.size > 0) {
            try {

                api.sk_message_dr(
                    parsing.parse_message_id(remoteMessage.data.toString()),
                    SkParameters.firebase_registration_token,
                    SkParameters.sk_registration_token
                )
                println("delivery report success: messid ${remoteMessage.messageId.toString()}, fbtoken: ${SkParameters.firebase_registration_token}, sktoken: ${ SkParameters.sk_registration_token}")
            }
            catch (e: Exception) {
                println("failed")
            }

            try {
                SkPushMess.message = remoteMessage.data.toString()
                val intent = Intent()
                intent.action = "com.sk.android.sksdkandroid.Push"
                //intent.putExtra("Data", 1000)
                //intent.flags = Intent.FLAG_INCLUDE_STOPPED_PACKAGES
                sendBroadcast(intent)

            } catch (e: Exception)
            {
                SkPushMess.message = ""
            }

                Log.d(TAG, "Message data payload: " + remoteMessage.data.toString())
        }

        // Check if message contains a notification payload.
        if (remoteMessage.notification != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.notification!!.body!!)

            try {
                sendNotification(remoteMessage)
            }catch (ee: Exception){}

            /*
            try {
                api.sk_message_dr(
                    parsing.parse_message_id(remoteMessage.data.toString()),
                    SkParameters.firebase_registration_token,
                    SkParameters.sk_registration_token
                )
                println("delivery report syccess: messid ${remoteMessage.messageId.toString()}, fbtoken: ${SkParameters.firebase_registration_token}, sktoken: ${ SkParameters.sk_registration_token}")
            }
            catch (e: Exception) {
                println("failed")
            }

             */

        }
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }


    fun sendNotification(remoteMessage: RemoteMessage) {
            val notif: SkPublicParams = SkPublicParams()
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(0 /* ID of notification */, notif.notificationBuilder(applicationContext, remoteMessage.notification!!.body.toString()).build())
    }

    private companion object {
        private val TAG = "FCMService"
    }
}

