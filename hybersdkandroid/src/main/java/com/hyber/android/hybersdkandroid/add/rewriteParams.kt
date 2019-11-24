package com.hyber.android.hybersdkandroid.add

import android.content.Context
import com.hyber.android.hybersdkandroid.core.SharedPreference
import com.hyber.android.hybersdkandroid.core.HyberParameters

//function for initialization different parameters
//
internal class RewriteParams(val context: Context) {
    private val sharedPreference: SharedPreference = SharedPreference(context)
    internal val paramsglobal: HyberParameters = HyberParameters

    fun rewrite_hyber_user_msisdn(hyber_user_msisdn: String) {
        paramsglobal.hyber_user_msisdn = hyber_user_msisdn
        sharedPreference.save("hyber_user_msisdn", hyber_user_msisdn)
    }

    fun rewrite_hyber_user_password(hyber_user_password: String) {
        paramsglobal.hyber_user_Password = hyber_user_password
        sharedPreference.save("hyber_user_Password", hyber_user_password)
    }

    fun rewrite_hyber_registration_token(hyber_registration_token: String) {
        paramsglobal.hyber_registration_token = hyber_registration_token
        sharedPreference.save("hyber_registration_token", hyber_registration_token)
    }

    fun rewrite_hyber_user_id(hyber_user_id: String) {
        paramsglobal.hyber_user_id = hyber_user_id
        sharedPreference.save("hyber_user_id", hyber_user_id)
    }

    fun rewrite_hyber_device_id(deviceId: String) {
        paramsglobal.deviceId = deviceId
        sharedPreference.save("deviceId", deviceId)
    }

    fun rewrite_hyber_create_at(hyber_registration_createdAt: String) {
        paramsglobal.hyber_registration_createdAt = hyber_registration_createdAt
        sharedPreference.save("hyber_registration_createdAt", hyber_registration_createdAt)
    }

    fun rewrite_api_registrationstatus(registrationstatus: Boolean) {
        paramsglobal.registrationstatus = registrationstatus
        sharedPreference.save("registrationstatus", registrationstatus)
    }

    fun rewrite_firebase_token(fb_token_new: String) {
        paramsglobal.firebase_registration_token = fb_token_new
        sharedPreference.save("firebase_registration_token", fb_token_new)
    }

    fun init_notification_params(push_priority: Int) {

    }

    fun rewrite_hyber_branch(hyber_branch: String) {
        paramsglobal.branch = hyber_branch
        sharedPreference.save("hyber_branch", hyber_branch)
    }

    // sharedPreference.save("ddd","sss")
}