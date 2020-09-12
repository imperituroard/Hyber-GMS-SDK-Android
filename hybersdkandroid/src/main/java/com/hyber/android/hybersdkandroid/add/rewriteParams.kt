package com.hyber.android.hybersdkandroid.add

import android.content.Context
import com.hyber.android.hybersdkandroid.core.HyberParameters
import com.hyber.android.hybersdkandroid.core.SharedPreference

//function for initialization different parameters
//
internal class RewriteParams(val context: Context) {
    private val sharedPreference: SharedPreference = SharedPreference(context)
    private val paramsglobal: HyberParameters = HyberParameters

    fun rewriteHyberUserMsisdn(hyber_user_msisdn: String) {
        paramsglobal.hyber_user_msisdn = hyber_user_msisdn
        sharedPreference.save("hyber_user_msisdn", hyber_user_msisdn)
    }

    fun rewriteHyberUserPassword(hyber_user_password: String) {
        paramsglobal.hyber_user_Password = hyber_user_password
        sharedPreference.save("hyber_user_Password", hyber_user_password)
    }

    fun rewriteHyberRegistrationToken(hyber_registration_token: String) {
        paramsglobal.hyber_registration_token = hyber_registration_token
        sharedPreference.save("hyber_registration_token", hyber_registration_token)
    }

    fun rewriteHyberUserId(hyber_user_id: String) {
        paramsglobal.hyber_user_id = hyber_user_id
        sharedPreference.save("hyber_user_id", hyber_user_id)
    }

    fun rewriteHyberDeviceId(deviceId: String) {
        paramsglobal.deviceId = deviceId
        sharedPreference.save("deviceId", deviceId)
    }

    fun rewriteHyberCreateAt(hyber_registration_createdAt: String) {
        paramsglobal.hyber_registration_createdAt = hyber_registration_createdAt
        sharedPreference.save("hyber_registration_createdAt", hyber_registration_createdAt)
    }

    fun rewriteApiRegistrationStatus(registrationstatus: Boolean) {
        paramsglobal.registrationStatus = registrationstatus
        sharedPreference.save("registrationstatus", registrationstatus)
    }

    fun rewriteFirebaseToken(fb_token_new: String) {
        paramsglobal.firebase_registration_token = fb_token_new
        sharedPreference.save("firebase_registration_token", fb_token_new)
    }

}