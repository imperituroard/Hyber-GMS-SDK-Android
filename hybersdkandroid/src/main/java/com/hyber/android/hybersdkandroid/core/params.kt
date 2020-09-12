@file:Suppress("unused", "SpellCheckingInspection")

package com.hyber.android.hybersdkandroid.core

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import com.hyber.android.hybersdkandroid.R
import com.hyber.android.hybersdkandroid.add.HyberInternal


open class HyberPublicParams {

    open fun notificationBuilder(
        context: Context,
        notificationTextMess: String
    ): NotificationCompat.Builder {
        val intent = Intent(context, context::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            context, 0 /* Request code */, intent,
            PendingIntent.FLAG_ONE_SHOT
        )
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        return NotificationCompat.Builder(context, "hyber.push.hyber")
            .setContentText(notificationTextMess)
            .setAutoCancel(true)
            .setSmallIcon(R.drawable.googleg_standard_color_18)
            .setPriority(HyberInternal.notificationPriorityOld(PushSdkParameters.push_notification_display_priority))
            .setSound(defaultSoundUri)
            //.setVibrate(longArrayOf(1000))
            .setContentIntent(pendingIntent)
    }
}


//URLs DATA for Hyber platform for different branches
object PushSdkParametersPublic {
    val branchMasterValue: UrlsPlatformList = UrlsPlatformList(
        fun_hyber_url_device_update = "https://push.hyber.im/api/2.3/device/update",
        fun_hyber_url_registration = "https://push.hyber.im/api/2.3/device/registration",
        fun_hyber_url_revoke = "https://push.hyber.im/api/2.3/device/revoke",
        fun_hyber_url_get_device_all = "https://push.hyber.im/api/2.3/device/all",
        fun_hyber_url_message_callback = "https://push.hyber.im/api/2.3/message/callback",
        fun_hyber_url_message_dr = "https://push.hyber.im/api/2.3/message/dr",
        fun_hyber_url_mess_queue = "https://push.hyber.im/api/2.3/message/queue",
        hyber_url_message_history = "https://push.hyber.im/api/2.3/message/history?startDate="
    )
    val branchTestValue: UrlsPlatformList = UrlsPlatformList(
        fun_hyber_url_device_update = "https://test-push.hyber.im/api/2.3/device/update",
        fun_hyber_url_registration = "https://test-push.hyber.im/api/2.3/device/registration",
        fun_hyber_url_revoke = "https://test-push.hyber.im/api/2.3/device/revoke",
        fun_hyber_url_get_device_all = "https://test-push.hyber.im/api/2.3/device/all",
        fun_hyber_url_message_callback = "https://test-push.hyber.im/api/2.3/message/callback",
        fun_hyber_url_message_dr = "https://test-push.hyber.im/api/2.3/message/dr",
        fun_hyber_url_mess_queue = "https://test-push.hyber.im/api/2.3/message/queue",
        hyber_url_message_history = "https://test-push.hyber.im/api/2.3/message/history?startDate="
    )
    const val TAG_LOGGING = "HyberPushSDK"
}

internal object PushSdkParameters {

    //uuid generates only one time
    var hyber_uuid: String = String()

    //its deviceId which we receive from server with answer for hyber_register_new()
    var deviceId: String = String()

    //is procedure for register new device completed or not
    // (true - devise exist on server. )
    // false - it s new device and we need to complete hyber_register_new()
    var registrationStatus: Boolean = false

    var sdkVersion: String = "1.0.0.11"
    var hyber_osType: String = String()
    var hyber_deviceName: String = String()
    var hyber_deviceType: String = String()
    var hyber_user_Password: String = String()
    var hyber_user_msisdn: String = String()
    var hyber_registration_token: String = String()
    var hyber_user_id: String = String()
    var hyber_registration_createdAt: String = String()
    var firebase_registration_token: String = String()
    var hyber_registration_time: String = String()

    var push_notification_display_priority: Int = 2

    //platform url branches. It can be rewrite by Hyber SDK initiation
    var branch_current_active: UrlsPlatformList = PushSdkParametersPublic.branchMasterValue



}

interface HyberAp
enum class HyberApC : HyberAp {
    BODY
}

internal data class HyberDataApi(
    val code: Int,
    val body: String,
    val time: Int
)


data class HyberFunAnswerRegister(
    val code: Int = 0,
    val result: String = "",
    val description: String = "",
    val deviceId: String = "",
    val token: String = "",
    val userId: String = "",
    val userPhone: String = "",
    val createdAt: String = ""
)

internal data class HyberDataApi2(
    val code: Int,
    val body: HyberFunAnswerRegister,
    val time: Int
)


data class HyberFunAnswerGeneral(
    val code: Int,
    val result: String,
    val description: String,
    val body: String
)

data class UrlsPlatformList(
    val fun_hyber_url_device_update: String,
    val fun_hyber_url_registration: String,
    val fun_hyber_url_revoke: String,
    val fun_hyber_url_get_device_all: String,
    val fun_hyber_url_message_callback: String,
    val fun_hyber_url_message_dr: String,
    val fun_hyber_url_mess_queue: String,
    val hyber_url_message_history: String
)
