package com.hyber.android.hybersdkandroid.core

import android.content.Context
import com.google.firebase.iid.FirebaseInstanceId
import com.hyber.android.hybersdkandroid.logger.HyberLoggerSdk
import java.util.*


//function for initialization different parameters
internal class Initialization(val context: Context) {
    private val sharedPreference: SharedPreference = SharedPreference(context)

    private fun hSdkUpdateFirebaseAuto() {
        HyberLoggerSdk.debug("Initialization.hSdkUpdateFirebaseAuto start")
        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener { instanceIdResult ->
            val token = instanceIdResult.token
            if (token != "") {
                sharedPreference.saveString("firebase_registration_token", token)
                HyberLoggerSdk.debug("Initialization.hSdkUpdateFirebaseAuto.Firebase token: $token")
            } else {
                HyberLoggerSdk.debug("Initialization.hSdkUpdateFirebaseAuto.Firebase token empty")
            }
        }
        HyberLoggerSdk.debug("Initialization.hSdkUpdateFirebaseAuto finished")
    }

    fun hSdkUpdateFirebaseManual(x_token: String): String {
        sharedPreference.saveString("firebase_registration_token", x_token)
        HyberLoggerSdk.debug("Initialization.hSdkUpdateFirebaseManual.Firebase token: $x_token")
        return x_token
    }


    fun hSdkGetParametersFromLocal(): HyberOperativeData {
        HyberLoggerSdk.debug("Initialization.hSdkGetParametersFromLocal started")

        val loadedDataLocalOperation = HyberOperativeData()
        val registrationStatus: Boolean = sharedPreference.getValueBool("registrationstatus", false)
        loadedDataLocalOperation.registrationStatus = registrationStatus
        hSdkUpdateFirebaseAuto()

        //1
        val firebaseRegistrationToken: String = sharedPreference.getValueString("firebase_registration_token")!!.toString()
        loadedDataLocalOperation.firebase_registration_token = firebaseRegistrationToken

        if (registrationStatus) {

            //2
            val hyberUuid: String = sharedPreference.getValueString("hyber_uuid")!!.toString()
            loadedDataLocalOperation.hyber_uuid = hyberUuid

            //3
            val devId: String = sharedPreference.getValueString("deviceId")!!.toString()
            loadedDataLocalOperation.deviceId = devId

            //4
            val hyberUserMsisdn: String =
                sharedPreference.getValueString("hyber_user_msisdn")!!.toString()
            loadedDataLocalOperation.hyber_user_msisdn = hyberUserMsisdn

            //5
            val hyberUserPassword: String =
                sharedPreference.getValueString("hyber_user_Password")!!.toString()
            loadedDataLocalOperation.hyber_user_Password = hyberUserPassword

            //6
            val hyberRegistrationToken: String =
                sharedPreference.getValueString("hyber_registration_token")!!.toString()
            loadedDataLocalOperation.hyber_registration_token = hyberRegistrationToken

            //7
            val hyberUserId: String =
                sharedPreference.getValueString("hyber_user_id")!!.toString()
            loadedDataLocalOperation.hyber_user_id = hyberUserId

            //8
            val hyberRegistrationCreatedAt: String =
                sharedPreference.getValueString("hyber_registration_createdAt")!!.toString()
            loadedDataLocalOperation.hyber_registration_createdAt = hyberRegistrationCreatedAt
        }
        HyberLoggerSdk.debug("Initialization.paramsLoader finished: hyberUuid=${loadedDataLocalOperation.hyber_uuid}, devId=${loadedDataLocalOperation.deviceId}, hyberUserMsisdn=${loadedDataLocalOperation.hyber_user_msisdn}, hyberUserPassword=${loadedDataLocalOperation.hyber_user_Password}, hyberRegistrationToken=${loadedDataLocalOperation.hyber_registration_token}, hyberUserId=${loadedDataLocalOperation.hyber_user_id}, hyberRegistrationCreatedAt=${loadedDataLocalOperation.hyber_registration_createdAt}")

        return loadedDataLocalOperation
    }

    fun hSdkInitSaveToLocal(
        deviceId: String,
        hyber_user_msisdn: String,
        hyber_user_Password: String,
        hyber_registration_token: String,
        hyber_user_id: String,
        hyber_registration_createdAt: String,
        registrationStatus: Boolean
    ) {
        HyberLoggerSdk.debug("Initialization.hSdkInitSaveToLocal  started")
        val hyberUuid = UUID.randomUUID().toString()
        HyberLoggerSdk.debug("Initialization.hSdkInit  hyberUuid=$hyberUuid, deviceId=$deviceId, hyber_user_msisdn=$hyber_user_msisdn, hyber_user_Password=$hyber_user_Password, hyber_registration_token=$hyber_registration_token, hyber_user_id=$hyber_user_id, hyber_registration_createdAt=$hyber_registration_createdAt")
        sharedPreference.saveString("hyber_uuid", hyberUuid)
        sharedPreference.saveString("deviceId", deviceId)
        sharedPreference.saveString("hyber_user_msisdn", hyber_user_msisdn)
        sharedPreference.saveString("hyber_user_Password", hyber_user_Password)
        sharedPreference.saveString("hyber_registration_token", hyber_registration_token)
        sharedPreference.saveString("hyber_user_id", hyber_user_id)
        sharedPreference.saveString("hyber_registration_createdAt", hyber_registration_createdAt)
        sharedPreference.save("registrationstatus", registrationStatus)
    }


    fun clearData() {
        sharedPreference.clearSharedPreference()
        HyberLoggerSdk.debug("Initialization.clearData  processed")
    }
}
