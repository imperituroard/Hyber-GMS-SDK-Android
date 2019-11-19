package com.sk.android.sksdkandroid.core

import android.content.Context
import java.util.*
import com.google.firebase.iid.FirebaseInstanceId


//function for initialization different parameters
//
internal class Initialization(val context: Context) {
    private val sharedPreference: SharedPreference = SharedPreference(context)
    internal val paramsglobal: HyberParameters = HyberParameters

    fun hyber_init(hyber_osType1:String, hyber_user_msisdn1: String, hyber_user_Password1: String, hyber_deviceType1: String, hyber_deviceName1: String) {
        val registrationstatus:Boolean = sharedPreference.getValueBoolien("registrationstatus", false)
        paramsglobal.registrationstatus = registrationstatus

        val token = FirebaseInstanceId.getInstance().token

        //var sk_uuid:String = sharedPreference.getValueString("sk_uuid").toString()
        if (registrationstatus == false) {
            val hyber_uuid = UUID.randomUUID().toString()
            sharedPreference.save("sk_uuid", hyber_uuid)
            paramsglobal.hyber_uuid = hyber_uuid

            sharedPreference.save("sk_user_msisdn", hyber_user_msisdn1)
            paramsglobal.hyber_user_msisdn = hyber_user_msisdn1

            sharedPreference.save("sk_osType", hyber_osType1)
            paramsglobal.hyber_osType = hyber_osType1

            sharedPreference.save("sk_user_Password", hyber_user_Password1)
            paramsglobal.hyber_user_Password = hyber_user_Password1

            sharedPreference.save("sk_deviceType", hyber_deviceType1)
            paramsglobal.hyber_deviceType = hyber_deviceType1

            sharedPreference.save("sk_deviceName", hyber_deviceName1)
            paramsglobal.hyber_deviceName = hyber_deviceName1

            sharedPreference.save("firebase_registration_token", token.toString())
            paramsglobal.firebase_registration_token = token.toString()

        } else {

            val hyber_uuid:String = sharedPreference.getValueString("sk_uuid")!!.toString()
            paramsglobal.hyber_uuid = hyber_uuid

            val devid:String = sharedPreference.getValueString("deviceId")!!.toString()
            paramsglobal.deviceId = devid


            val hyber_user_msisdn:String = sharedPreference.getValueString("sk_user_msisdn")!!.toString()
            paramsglobal.hyber_user_msisdn = hyber_user_msisdn

            val hyber_user_Password:String = sharedPreference.getValueString("sk_user_Password")!!.toString()
            paramsglobal.hyber_user_Password = hyber_user_Password

            val hyber_deviceType:String = sharedPreference.getValueString("sk_deviceType")!!.toString()
            paramsglobal.hyber_deviceType = hyber_deviceType

            val hyber_deviceName:String = sharedPreference.getValueString("sk_deviceName")!!.toString()
            paramsglobal.hyber_deviceName = hyber_deviceName

            val hyber_osType:String = sharedPreference.getValueString("sk_osType")!!.toString()
            paramsglobal.hyber_osType = hyber_osType

            val hyber_registration_token:String = sharedPreference.getValueString("sk_registration_token")!!.toString()
            paramsglobal.hyber_registration_token = hyber_registration_token

            val hyber_user_id:String = sharedPreference.getValueString("sk_user_id")!!.toString()
            paramsglobal.hyber_user_id = hyber_user_id

            val hyber_registration_createdAt:String = sharedPreference.getValueString("sk_registration_createdAt")!!.toString()
            paramsglobal.hyber_registration_createdAt = hyber_registration_createdAt

            sharedPreference.save("firebase_registration_token", token.toString())
            paramsglobal.firebase_registration_token = token.toString()
        }
    }


    fun hyber_init2() {
        val registrationstatus:Boolean = sharedPreference.getValueBoolien("registrationstatus", false)
        paramsglobal.registrationstatus = registrationstatus

        val token = FirebaseInstanceId.getInstance().token

        //var sk_uuid:String = sharedPreference.getValueString("sk_uuid").toString()
        if (registrationstatus == false) {
            val hyber_uuid = UUID.randomUUID().toString()
            sharedPreference.save("sk_uuid", hyber_uuid)
            paramsglobal.hyber_uuid = hyber_uuid

            sharedPreference.save("firebase_registration_token", token.toString())
            paramsglobal.firebase_registration_token = token.toString()

        } else {

            val hyber_uuid:String = sharedPreference.getValueString("sk_uuid")!!.toString()
            paramsglobal.hyber_uuid = hyber_uuid

            val devid:String = sharedPreference.getValueString("deviceId")!!.toString()
            paramsglobal.deviceId = devid


            val hyber_user_msisdn:String = sharedPreference.getValueString("sk_user_msisdn")!!.toString()
            paramsglobal.hyber_user_msisdn = hyber_user_msisdn

            val hyber_user_Password:String = sharedPreference.getValueString("sk_user_Password")!!.toString()
            paramsglobal.hyber_user_Password = hyber_user_Password

            val hyber_deviceType:String = sharedPreference.getValueString("sk_deviceType")!!.toString()
            paramsglobal.hyber_deviceType = hyber_deviceType

            val hyber_deviceName:String = sharedPreference.getValueString("sk_deviceName")!!.toString()
            paramsglobal.hyber_deviceName = hyber_deviceName

            val hyber_osType:String = sharedPreference.getValueString("sk_osType")!!.toString()
            paramsglobal.hyber_osType = hyber_osType

            val hyber_registration_token:String = sharedPreference.getValueString("sk_registration_token")!!.toString()
            paramsglobal.hyber_registration_token = hyber_registration_token

            val hyber_user_id:String = sharedPreference.getValueString("sk_user_id")!!.toString()
            paramsglobal.hyber_user_id = hyber_user_id

            val hyber_registration_createdAt:String = sharedPreference.getValueString("sk_registration_createdAt")!!.toString()
            paramsglobal.hyber_registration_createdAt = hyber_registration_createdAt

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