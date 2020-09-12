package com.hyber.android.hybersdkandroid.core

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import com.google.firebase.iid.FirebaseInstanceId
import java.util.*


//function for initialization different parameters
internal class Initialization(val context: Context) {
    private val sharedPreference: SharedPreference = SharedPreference(context)
    internal val parametersGlobal: PushSdkParameters = PushSdkParameters

    fun hSdkUpdateFirebaseAuto() {
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

    fun hSdkUpdateFirebaseManual(x_token: String) {
        sharedPreference.save("firebase_registration_token", x_token)
        parametersGlobal.firebase_registration_token = x_token
        Log.d(TAG, "hyber_init.Firebase token: $x_token")
    }

    fun hSdkInit(
        hOsType1: String,
        hDeviceType1: String,
        hDeviceName1: String,
        hUrlsInfo: UrlsPlatformList
    ) {
        val registrationStatus: Boolean =
            sharedPreference.getValueBoolien("registrationstatus", false)
        parametersGlobal.registrationStatus = registrationStatus

        parametersGlobal.branch_current_active = hUrlsInfo

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

            sharedPreference.save("hyber_deviceType", hDeviceType1)
            parametersGlobal.hyber_deviceType = hDeviceType1

            sharedPreference.save("hyber_deviceName", hDeviceName1)
            parametersGlobal.hyber_deviceName = hDeviceName1

        } else {

            val hyber_uuid: String = sharedPreference.getValueString("hyber_uuid")!!.toString()
            parametersGlobal.hyber_uuid = hyber_uuid

            val devid: String = sharedPreference.getValueString("deviceId")!!.toString()
            parametersGlobal.deviceId = devid


            val hyberUserMsisdn: String =
                sharedPreference.getValueString("hyber_user_msisdn")!!.toString()
            parametersGlobal.hyber_user_msisdn = hyberUserMsisdn

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


    fun hSdkInit2() {
        val registrationStatus: Boolean =
        sharedPreference.getValueBoolien("registrationstatus", false)
        parametersGlobal.registrationStatus = registrationStatus

        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener { instanceIdResult ->
            val token = instanceIdResult.token
            sharedPreference.save("firebase_registration_token", token)
            parametersGlobal.firebase_registration_token = token

        }

        if (!registrationStatus) {
            val hyber_uuid = UUID.randomUUID().toString()
            sharedPreference.save("hyber_uuid", hyber_uuid)
            parametersGlobal.hyber_uuid = hyber_uuid


        } else {

            val hSdkUuid: String = sharedPreference.getValueString("hyber_uuid")!!.toString()
            parametersGlobal.hyber_uuid = hSdkUuid

            val devSdkId: String = sharedPreference.getValueString("deviceId")!!.toString()
            parametersGlobal.deviceId = devSdkId


            val hSdkUserMsisdn: String =
                sharedPreference.getValueString("hyber_user_msisdn")!!.toString()
            parametersGlobal.hyber_user_msisdn = hSdkUserMsisdn

            val hyberUserPassword: String =
                sharedPreference.getValueString("hyber_user_Password")!!.toString()
            parametersGlobal.hyber_user_Password = hyberUserPassword

            val hyberDeviceType: String =
                sharedPreference.getValueString("hyber_deviceType")!!.toString()
            parametersGlobal.hyber_deviceType = hyberDeviceType

            val hyberDeviceName: String =
                sharedPreference.getValueString("hyber_deviceName")!!.toString()
            parametersGlobal.hyber_deviceName = hyberDeviceName

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


    fun hSdkInit3() {
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

            val hyberRegistrationCreatedAt: String =
                sharedPreference.getValueString("hyber_registration_createdAt")!!.toString()
            parametersGlobal.hyber_registration_createdAt = hyberRegistrationCreatedAt

        }
    }

    fun clearData() {
        sharedPreference.clearSharedPreference()
    }

}