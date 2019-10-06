package com.sk.android.sksdkandroid.core

import android.content.Context
import com.sk.android.sksdkandroid.core.SharedPreference
import java.util.*
import com.google.firebase.iid.FirebaseInstanceId


//function for initialization different parameters
//
internal class Initialization(val context: Context) {
    private val sharedPreference: SharedPreference = SharedPreference(context)
    internal val paramsglobal: SkParameters = SkParameters

    fun sk_init(sk_osType1:String, sk_user_msisdn1: String, sk_user_Password1: String, sk_deviceType1: String, sk_deviceName1: String) {
        val registrationstatus:Boolean = sharedPreference.getValueBoolien("registrationstatus", false)
        paramsglobal.registrationstatus = registrationstatus

        val token = FirebaseInstanceId.getInstance().token

        //var sk_uuid:String = sharedPreference.getValueString("sk_uuid").toString()
        if (registrationstatus == false) {
            val sk_uuid = UUID.randomUUID().toString()
            sharedPreference.save("sk_uuid", sk_uuid)
            paramsglobal.sk_uuid = sk_uuid

            sharedPreference.save("sk_user_msisdn", sk_user_msisdn1)
            paramsglobal.sk_user_msisdn = sk_user_msisdn1

            sharedPreference.save("sk_osType", sk_osType1)
            paramsglobal.sk_osType = sk_osType1

            sharedPreference.save("sk_user_Password", sk_user_Password1)
            paramsglobal.sk_user_Password = sk_user_Password1

            sharedPreference.save("sk_deviceType", sk_deviceType1)
            paramsglobal.sk_deviceType = sk_deviceType1

            sharedPreference.save("sk_deviceName", sk_deviceName1)
            paramsglobal.sk_deviceName = sk_deviceName1

            sharedPreference.save("firebase_registration_token", token.toString())
            paramsglobal.firebase_registration_token = token.toString()

        } else {

            val sk_uuid:String = sharedPreference.getValueString("sk_uuid")!!.toString()
            paramsglobal.sk_uuid = sk_uuid

            val devid:String = sharedPreference.getValueString("deviceId")!!.toString()
            paramsglobal.deviceId = devid


            val sk_user_msisdn:String = sharedPreference.getValueString("sk_user_msisdn")!!.toString()
            paramsglobal.sk_user_msisdn = sk_user_msisdn

            val sk_user_Password:String = sharedPreference.getValueString("sk_user_Password")!!.toString()
            paramsglobal.sk_user_Password = sk_user_Password

            val sk_deviceType:String = sharedPreference.getValueString("sk_deviceType")!!.toString()
            paramsglobal.sk_deviceType = sk_deviceType

            val sk_deviceName:String = sharedPreference.getValueString("sk_deviceName")!!.toString()
            paramsglobal.sk_deviceName = sk_deviceName

            val sk_osType:String = sharedPreference.getValueString("sk_osType")!!.toString()
            paramsglobal.sk_osType = sk_osType

            val sk_registration_token:String = sharedPreference.getValueString("sk_registration_token")!!.toString()
            paramsglobal.sk_registration_token = sk_registration_token

            val sk_user_id:String = sharedPreference.getValueString("sk_user_id")!!.toString()
            paramsglobal.sk_user_id = sk_user_id

            val sk_registration_createdAt:String = sharedPreference.getValueString("sk_registration_createdAt")!!.toString()
            paramsglobal.sk_registration_createdAt = sk_registration_createdAt

            sharedPreference.save("firebase_registration_token", token.toString())
            paramsglobal.firebase_registration_token = token.toString()
        }
    }

    fun clearData(){
        sharedPreference.clearSharedPreference()
    }

    fun firebase_init(context: Context) {

    }

    // sharedPreference.save("ddd","sss")
}