package com.sk.android.sksdkandroid.add

import android.content.Context
import com.sk.android.sksdkandroid.core.SharedPreference
import java.util.*
import com.sk.android.sksdkandroid.core.SkParameters

//function for initialization different parameters
//
internal class RewriteParams(val context: Context) {
    private val sharedPreference: SharedPreference = SharedPreference(context)
    internal val paramsglobal: SkParameters = SkParameters

    fun rewrite_sk_user_msisdn(sk_user_msisdn:String) {
        paramsglobal.sk_user_msisdn = sk_user_msisdn
        sharedPreference.save("sk_user_msisdn", sk_user_msisdn)
    }

    fun rewrite_sk_user_password(sk_user_password:String) {
        paramsglobal.sk_user_Password = sk_user_password
        sharedPreference.save("sk_user_Password", sk_user_password)
    }

    fun rewrite_sk_registration_token(sk_registration_token:String) {
        paramsglobal.sk_registration_token = sk_registration_token
        sharedPreference.save("sk_registration_token", sk_registration_token)
    }

    fun rewrite_sk_user_id(sk_user_id:String) {
        paramsglobal.sk_user_id = sk_user_id
        sharedPreference.save("sk_user_id", sk_user_id)
    }

    fun rewrite_sk_device_id(deviceId:String) {
        paramsglobal.deviceId = deviceId
        sharedPreference.save("deviceId", deviceId)
    }

    fun rewrite_sk_create_at(sk_registration_createdAt:String) {
        paramsglobal.sk_registration_createdAt = sk_registration_createdAt
        sharedPreference.save("sk_registration_createdAt", sk_registration_createdAt)
    }

    fun rewrite_api_registrationstatus(registrationstatus:Boolean) {
        paramsglobal.registrationstatus = registrationstatus
        sharedPreference.save("registrationstatus", registrationstatus)
    }

    fun rewrite_firebase_token(fb_token_new:String) {
        paramsglobal.firebase_registration_token = fb_token_new
        sharedPreference.save("firebase_registration_token", fb_token_new)
    }

    fun init_notification_params(push_priority: Int){

    }

    // sharedPreference.save("ddd","sss")
}