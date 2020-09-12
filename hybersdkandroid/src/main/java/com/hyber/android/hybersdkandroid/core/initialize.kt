package com.hyber.android.hybersdkandroid.core

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import com.google.firebase.iid.FirebaseInstanceId
import java.util.*


//function for initialization different parameters
//
internal class Initialization(val context: Context) {
    private val sharedPreference: SharedPreference = SharedPreference(context)
    internal val parametersGlobal: HyberParameters = HyberParameters

    fun hUpdateFirebaseAuto() {
        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener { instanceIdResult ->
            val token = instanceIdResult.token
            // Do whatever you want with your token now
            // i.e. store it on SharedPreferences or DB
            // or directly send it to server
            if (token != "") {
                sharedPreference.save("firebase_registration_token", token)
                parametersGlobal.firebase_registration_token = token
                Log.d(TAG, "hyber_init.Firebase token: $token")
            }
        }
    }

    fun hUpdateFirebaseManual(x_token: String) {
        sharedPreference.save("firebase_registration_token", x_token)
        parametersGlobal.firebase_registration_token = x_token
        Log.d(TAG, "hyber_init.Firebase token: $x_token")
    }

    fun hInit(
        hOsType1: String,
        hyber_deviceType1: String,
        hyber_deviceName1: String,
        hyberUrlsInfo: UrlsPlatformList
    ) {
        val registrationStatus: Boolean =
            sharedPreference.getValueBoolien("registrationstatus", false)
        parametersGlobal.registrationStatus = registrationStatus

        parametersGlobal.branch_current_active = hyberUrlsInfo

        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener { instanceIdResult ->
            val token = instanceIdResult.token
            if (token != "") {
                sharedPreference.save("firebase_registration_token", token)
                parametersGlobal.firebase_registration_token = token
                Log.d(TAG, "hyber_init.Firebase token: $token")
            } else {
                val firebase_registration_token: String =
                    sharedPreference.getValueString("firebase_registration_token")!!.toString()
                parametersGlobal.firebase_registration_token = firebase_registration_token
                Log.d(TAG, "hyber_init.Firebase token: $token")
            }
        }

        if (!registrationStatus) {
            val hyber_uuid = UUID.randomUUID().toString()
            sharedPreference.save("hyber_uuid", hyber_uuid)
            parametersGlobal.hyber_uuid = hyber_uuid

            sharedPreference.save("hyber_osType", hOsType1)
            parametersGlobal.hyber_osType = hOsType1

            sharedPreference.save("hyber_deviceType", hyber_deviceType1)
            parametersGlobal.hyber_deviceType = hyber_deviceType1

            sharedPreference.save("hyber_deviceName", hyber_deviceName1)
            parametersGlobal.hyber_deviceName = hyber_deviceName1

        } else {

            val hyber_uuid: String = sharedPreference.getValueString("hyber_uuid")!!.toString()
            parametersGlobal.hyber_uuid = hyber_uuid

            val devid: String = sharedPreference.getValueString("deviceId")!!.toString()
            parametersGlobal.deviceId = devid


            val hyber_user_msisdn: String =
                sharedPreference.getValueString("hyber_user_msisdn")!!.toString()
            parametersGlobal.hyber_user_msisdn = hyber_user_msisdn

            val hyber_user_Password: String =
                sharedPreference.getValueString("hyber_user_Password")!!.toString()
            parametersGlobal.hyber_user_Password = hyber_user_Password

            val hyber_deviceType: String =
                sharedPreference.getValueString("hyber_deviceType")!!.toString()
            parametersGlobal.hyber_deviceType = hyber_deviceType

            val hyber_deviceName: String =
            sharedPreference.getValueString("hyber_deviceName")!!.toString()
            parametersGlobal.hyber_deviceName = hyber_deviceName

            val hyber_osType: String = sharedPreference.getValueString("hyber_osType")!!.toString()
            parametersGlobal.hyber_osType = hyber_osType

            val hyber_registration_token: String =
                sharedPreference.getValueString("hyber_registration_token")!!.toString()
            parametersGlobal.hyber_registration_token = hyber_registration_token

            val hyber_user_id: String =
                sharedPreference.getValueString("hyber_user_id")!!.toString()
            parametersGlobal.hyber_user_id = hyber_user_id

            val hyber_registration_createdAt: String =
                sharedPreference.getValueString("hyber_registration_createdAt")!!.toString()
            parametersGlobal.hyber_registration_createdAt = hyber_registration_createdAt

        }
    }


    fun hyber_init2() {
        val registrationstatus: Boolean =
        sharedPreference.getValueBoolien("registrationstatus", false)
        parametersGlobal.registrationStatus = registrationstatus

        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener { instanceIdResult ->
            val token = instanceIdResult.token
            sharedPreference.save("firebase_registration_token", token)
            parametersGlobal.firebase_registration_token = token

        }

        if (!registrationstatus) {
            val hyber_uuid = UUID.randomUUID().toString()
            sharedPreference.save("hyber_uuid", hyber_uuid)
            parametersGlobal.hyber_uuid = hyber_uuid


        } else {

            val hyber_uuid: String = sharedPreference.getValueString("hyber_uuid")!!.toString()
            parametersGlobal.hyber_uuid = hyber_uuid

            val devid: String = sharedPreference.getValueString("deviceId")!!.toString()
            parametersGlobal.deviceId = devid


            val hyber_user_msisdn: String =
                sharedPreference.getValueString("hyber_user_msisdn")!!.toString()
            parametersGlobal.hyber_user_msisdn = hyber_user_msisdn

            val hyber_user_Password: String =
                sharedPreference.getValueString("hyber_user_Password")!!.toString()
            parametersGlobal.hyber_user_Password = hyber_user_Password

            val hyber_deviceType: String =
                sharedPreference.getValueString("hyber_deviceType")!!.toString()
            parametersGlobal.hyber_deviceType = hyber_deviceType

            val hyber_deviceName: String =
                sharedPreference.getValueString("hyber_deviceName")!!.toString()
            parametersGlobal.hyber_deviceName = hyber_deviceName

            val hyber_osType: String = sharedPreference.getValueString("hyber_osType")!!.toString()
            parametersGlobal.hyber_osType = hyber_osType

            val hyber_registration_token: String =
                sharedPreference.getValueString("hyber_registration_token")!!.toString()
            parametersGlobal.hyber_registration_token = hyber_registration_token

            val hyber_user_id: String =
                sharedPreference.getValueString("hyber_user_id")!!.toString()
            parametersGlobal.hyber_user_id = hyber_user_id

            val hyber_registration_createdAt: String =
                sharedPreference.getValueString("hyber_registration_createdAt")!!.toString()
            parametersGlobal.hyber_registration_createdAt = hyber_registration_createdAt

        }
    }


    fun hyber_init3() {
        val registrationstatus: Boolean =
            sharedPreference.getValueBoolien("registrationstatus", false)
        parametersGlobal.registrationStatus = registrationstatus


        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener { instanceIdResult ->
            val token = instanceIdResult.token
            // Do whatever you want with your token now
            // i.e. store it on SharedPreferences or DB
            // or directly send it to server
            if (token != "") {
                sharedPreference.save("firebase_registration_token", token)
                parametersGlobal.firebase_registration_token = token
            } else {
                val firebase_registration_token: String =
                    sharedPreference.getValueString("firebase_registration_token")!!.toString()
                parametersGlobal.firebase_registration_token = firebase_registration_token
            }
        }

        if (!registrationstatus) {
            val hyber_uuid = UUID.randomUUID().toString()
            sharedPreference.save("hyber_uuid", hyber_uuid)
            parametersGlobal.hyber_uuid = hyber_uuid

        } else {

            val hyber_uuid: String = sharedPreference.getValueString("hyber_uuid")!!.toString()
            parametersGlobal.hyber_uuid = hyber_uuid

            val devid: String = sharedPreference.getValueString("deviceId")!!.toString()
            parametersGlobal.deviceId = devid


            val hyber_user_msisdn: String =
                sharedPreference.getValueString("hyber_user_msisdn")!!.toString()
            parametersGlobal.hyber_user_msisdn = hyber_user_msisdn

            val hyber_user_Password: String =
                sharedPreference.getValueString("hyber_user_Password")!!.toString()
            parametersGlobal.hyber_user_Password = hyber_user_Password

            val hyber_deviceType: String =
                sharedPreference.getValueString("hyber_deviceType")!!.toString()
            parametersGlobal.hyber_deviceType = hyber_deviceType

            val hyber_deviceName: String =
                sharedPreference.getValueString("hyber_deviceName")!!.toString()
            parametersGlobal.hyber_deviceName = hyber_deviceName

            val hyber_osType: String = sharedPreference.getValueString("hyber_osType")!!.toString()
            parametersGlobal.hyber_osType = hyber_osType

            val hyber_registration_token: String =
                sharedPreference.getValueString("hyber_registration_token")!!.toString()
            parametersGlobal.hyber_registration_token = hyber_registration_token

            val hyber_user_id: String =
                sharedPreference.getValueString("hyber_user_id")!!.toString()
            parametersGlobal.hyber_user_id = hyber_user_id

            val hyber_registration_createdAt: String =
                sharedPreference.getValueString("hyber_registration_createdAt")!!.toString()
            parametersGlobal.hyber_registration_createdAt = hyber_registration_createdAt

        }
    }

    fun clearData() {
        sharedPreference.clearSharedPreference()
    }

}