package com.hyber.android.hybersdkandroid.core

import android.content.Context
import com.google.firebase.iid.FirebaseInstanceId
import com.hyber.android.hybersdkandroid.logger.HyberLoggerSdk
import java.util.*


//function for initialization different parameters
internal class Initialization(val context: Context) {
    private val sharedPreference: SharedPreference = SharedPreference(context)

    fun hSdkUpdateFirebaseAuto() {

        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener { instanceIdResult ->
            val token = instanceIdResult.token
            if (token != "") {
                sharedPreference.save("firebase_registration_token", token)
                PushSdkParameters.firebase_registration_token = token
                HyberLoggerSdk.debug("Initialization.hSdkUpdateFirebaseAuto.Firebase token: $token")
            } else {
                val firebaseRegistrationToken: String =
                    sharedPreference.getValueString("firebase_registration_token")!!.toString()
                PushSdkParameters.firebase_registration_token = firebaseRegistrationToken
                HyberLoggerSdk.debug("Initialization.hSdkUpdateFirebaseAuto.Firebase token empty loaded: $firebaseRegistrationToken")
            }
        }
    }

    fun hSdkUpdateFirebaseManual(x_token: String) {
        sharedPreference.save("firebase_registration_token", x_token)
        PushSdkParameters.firebase_registration_token = x_token
        HyberLoggerSdk.debug("Initialization.hSdkUpdateFirebaseManual.Firebase token: $x_token")
    }

    private fun paramsLoader() {
        HyberLoggerSdk.debug("Initialization.paramsLoader started")

        val hyberUuid: String = sharedPreference.getValueString("hyber_uuid")!!.toString()
        PushSdkParameters.hyber_uuid = hyberUuid

        val devId: String = sharedPreference.getValueString("deviceId")!!.toString()
        PushSdkParameters.deviceId = devId

        val hyberUserMsisdn: String =
            sharedPreference.getValueString("hyber_user_msisdn")!!.toString()
        PushSdkParameters.hyber_user_msisdn = hyberUserMsisdn

        val hyberUserPassword: String =
            sharedPreference.getValueString("hyber_user_Password")!!.toString()
        PushSdkParameters.hyber_user_Password = hyberUserPassword

        val hyberDeviceType: String =
            sharedPreference.getValueString("hyber_deviceType")!!.toString()
        PushSdkParameters.hyber_deviceType = hyberDeviceType

        val hyberDeviceName: String =
            sharedPreference.getValueString("hyber_deviceName")!!.toString()
        PushSdkParameters.hyber_deviceName = hyberDeviceName

        val hyberOsType: String = sharedPreference.getValueString("hyber_osType")!!.toString()
        PushSdkParameters.hyber_osType = hyberOsType

        val hyberRegistrationToken: String =
            sharedPreference.getValueString("hyber_registration_token")!!.toString()
        PushSdkParameters.hyber_registration_token = hyberRegistrationToken

        val hyberUserId: String =
            sharedPreference.getValueString("hyber_user_id")!!.toString()
        PushSdkParameters.hyber_user_id = hyberUserId

        val hyberRegistrationCreatedAt: String =
            sharedPreference.getValueString("hyber_registration_createdAt")!!.toString()
        PushSdkParameters.hyber_registration_createdAt = hyberRegistrationCreatedAt
        HyberLoggerSdk.debug("Initialization.paramsLoader finished: hyberUuid=$hyberUuid, devId=$devId, hyberUserMsisdn=$hyberUserMsisdn, hyberUserPassword=$hyberUserPassword, hyberDeviceType=$hyberDeviceType, hyberDeviceName=$hyberDeviceName, hyberOsType=$hyberOsType, hyberRegistrationToken=$hyberRegistrationToken, hyberUserId=$hyberUserId, hyberRegistrationCreatedAt=$hyberRegistrationCreatedAt")
    }

    fun hSdkInit(
        hOsType1: String,
        hDeviceType1: String,
        hDeviceName1: String,
        hUrlsInfo: UrlsPlatformList
    ) {
        HyberLoggerSdk.debug("Initialization.hSdkInit  started")
        val registrationStatus: Boolean = sharedPreference.getValueBool("registrationstatus", false)
        PushSdkParameters.registrationStatus = registrationStatus
        PushSdkParameters.branch_current_active = hUrlsInfo
        PushSdkParameters.hyber_osType = hOsType1
        PushSdkParameters.hyber_deviceType = hDeviceType1
        PushSdkParameters.hyber_deviceName = hDeviceName1

        HyberLoggerSdk.debug("Initialization.hSdkInit  registrationstatus: $registrationStatus")
        HyberLoggerSdk.debug("Initialization.hSdkInit  hUrlsInfo=$hUrlsInfo, hOsType1=$hOsType1, hDeviceType1=$hDeviceType1, hDeviceName1=$hDeviceName1")

        hSdkUpdateFirebaseAuto()

        if (!registrationStatus) {
            val hyberUuid = UUID.randomUUID().toString()
            sharedPreference.save("hyber_uuid", hyberUuid)
            PushSdkParameters.hyber_uuid = hyberUuid
            sharedPreference.save("hyber_osType", hOsType1)
            sharedPreference.save("hyber_deviceType", hDeviceType1)
            sharedPreference.save("hyber_deviceName", hDeviceName1)
        } else {
            paramsLoader()
        }
    }

    fun hSdkInit2() {
        HyberLoggerSdk.debug("Initialization.hSdkInit2  started")
        val registrationStatus: Boolean = sharedPreference.getValueBool("registrationstatus", false)
        PushSdkParameters.registrationStatus = registrationStatus
        HyberLoggerSdk.debug("Initialization.hSdkInit2  getValueBool: $registrationStatus")

        hSdkUpdateFirebaseAuto()

        if (!registrationStatus) {
            val hyberUuid = UUID.randomUUID().toString()
            sharedPreference.save("hyber_uuid", hyberUuid)
            PushSdkParameters.hyber_uuid = hyberUuid
        } else {
            paramsLoader()
        }
    }

    fun clearData() {
        sharedPreference.clearSharedPreference()
        PushSdkParameters.registrationStatus = false
        HyberLoggerSdk.debug("Initialization.clearData  processed")
    }
}