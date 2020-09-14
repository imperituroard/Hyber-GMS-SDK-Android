package com.hyber.android.hybersdkandroid.add

import android.content.Context
import com.hyber.android.hybersdkandroid.core.SharedPreference

//function for initialization different parameters
//
internal class RewriteParams(val context: Context) {
    private val sharedPreference: SharedPreference = SharedPreference(context)

    fun rewriteHyberUserMsisdn(hyber_user_msisdn: String) {
        sharedPreference.saveString("hyber_user_msisdn", hyber_user_msisdn)
    }

    fun rewriteHyberUserPassword(hyber_user_password: String) {
        sharedPreference.saveString("hyber_user_Password", hyber_user_password)
    }

    fun rewriteHyberRegistrationToken(hyber_registration_token: String) {
        sharedPreference.saveString("hyber_registration_token", hyber_registration_token)
    }

    fun rewriteHyberUserId(hyber_user_id: String) {
        sharedPreference.saveString("hyber_user_id", hyber_user_id)
    }

    fun rewriteHyberDeviceId(deviceId: String) {
        sharedPreference.saveString("deviceId", deviceId)
    }

    fun rewriteHyberCreateAt(hyber_registration_createdAt: String) {
        sharedPreference.saveString("hyber_registration_createdAt", hyber_registration_createdAt)
    }

    fun rewriteApiRegistrationStatus(registrationstatus: Boolean) {
        sharedPreference.save("registrationstatus", registrationstatus)
    }

    fun rewriteFirebaseToken(fb_token_new: String) {
        sharedPreference.saveString("firebase_registration_token", fb_token_new)
    }

}