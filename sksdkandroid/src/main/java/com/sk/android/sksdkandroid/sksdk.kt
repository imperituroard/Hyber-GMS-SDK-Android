package com.sk.android.sksdkandroid

import android.app.Activity
import android.content.Context
import com.sk.android.sksdkandroid.add.Answer
import android.util.Log
import android.content.ContentValues.TAG
import java.lang.Exception
import com.sk.android.sksdkandroid.add.GetInfo
import com.sk.android.sksdkandroid.add.RewriteParams
import com.sk.android.sksdkandroid.add.SkParsing
import com.sk.android.sksdkandroid.core.*
import com.sk.android.sksdkandroid.core.Initialization
import com.sk.android.sksdkandroid.core.SkApi
import com.sk.android.sksdkandroid.core.SkDataApi
import kotlin.properties.Delegates
import com.sk.android.sksdkandroid.add.SkStorage

object SkPushMess {
    var message: String? = null   //global variable
}

class HyberSK(user_msisdn: String="unknown", user_password: String="unknown", context: Context) {

    //any classes initialization
    private var context: Context by Delegates.notNull()
    private var init_sk: Initialization = Initialization(context)
    private var local_device_info: GetInfo = GetInfo()
    private var user_msisdn: String by Delegates.notNull()
    private var user_password: String by Delegates.notNull()
    private var apisk: SkApi = SkApi()
    private var answ: Answer = Answer()
    private var rewrite_params: RewriteParams = RewriteParams(context)
    private var parsing: SkParsing = SkParsing()

    //main class initialization
    init {
        this.context = context
        this.user_msisdn = user_msisdn
        this.user_password = user_password
        init_sk.sk_init(
            "android",
            user_msisdn,
            user_password,
            local_device_info.get_phone_type(context),
            local_device_info.getDeviceName().toString()
        )
        try{
        if (init_sk.paramsglobal.registrationstatus==true){
            this.sk_update_registration()
        }}catch (e:Exception) {
            println("registration update problem")
        }
    }

    //private var sk_storage: SkStorage =SkStorage(context)
    //parameter for device identification
    private var X_Hyber_Session_Id: String = init_sk.paramsglobal.firebase_registration_token

    private var answer_not_registered: SkFunAnswerGeneral = SkFunAnswerGeneral(704, "Failed", "Registration data not found", "Not registered")
    private var answer_not_known: SkFunAnswerGeneral = SkFunAnswerGeneral(710, "Failed", "Unknown error", "unknown")
    //(sk_osType1:String, sk_user_msisdn1: String, sk_user_Password1: String, sk_deviceType1: String, sk_deviceName1: String) {

    //answer codes
    //200 - Ok

    //answers from remote server
    //401 HTTP code – (Client error) authentication error, probably errors
    //400 HTTP code – (Client error) request validation error, probably errors
    //500 HTTP code – (Server error) 

    //sdk errors
    //700 - internal SDK error
    //701 - already exists
    //704 - not registered
    //705 - remote server error
    //710 - unknown error

    //{
    //    "result":"Ok",
    //    "description":"",
    //    "code":200,
    //    "body":{
    //}
    //}



    //1
    private fun initsdk(
        sk_osType: String,
        sk_user_msisdn: String,
        sk_user_Password: String,
        sk_deviceType: String
    ) {
        init_sk.sk_init(
            "android",
            sk_user_msisdn,
            sk_user_Password,
            sk_deviceType,
            local_device_info.getDeviceName().toString()
        )
    }

    //1
    fun sk_register_new(X_Hyber_Client_API_Key: String, X_Hyber_App_Fingerprint: String): SkFunAnswerRegister {
        try {
            if (init_sk.paramsglobal.registrationstatus == true) {
                return answ.sk_register_new_register_exists2(init_sk.paramsglobal,context)
            } else {
                val answ_svyazcom: SkDataApi2 = apisk.sk_device_register(
                    X_Hyber_Client_API_Key,
                    X_Hyber_Session_Id,
                    X_Hyber_App_Fingerprint,
                    init_sk.paramsglobal.sk_deviceName,
                    init_sk.paramsglobal.sk_deviceType,
                    init_sk.paramsglobal.sk_osType,
                    init_sk.paramsglobal.sdkversion,
                    init_sk.paramsglobal.sk_user_Password,
                    init_sk.paramsglobal.sk_user_msisdn,
                    context
                )
                Log.d(TAG, "sk_register_new response: $answ_svyazcom");
                Log.d(TAG, "uuid: ${init_sk.paramsglobal.sk_uuid}");
                return SkFunAnswerRegister(
                    code = answ_svyazcom.code,
                    result = answ_svyazcom.body.result,
                    description = answ_svyazcom.body.description,
                    deviceId = answ_svyazcom.body.deviceId,
                    token = answ_svyazcom.body.token,
                    userId = answ_svyazcom.body.userId,
                    userPhone = answ_svyazcom.body.userPhone,
                    createdAt = answ_svyazcom.body.createdAt
                )
            }
        } catch (e: Exception) {
            return answ.register_procedure_answer2("700", "unknown", context)
        }
        //apisk.sk_device_register()
    }

    //2
    fun sk_clear_current_device(): SkFunAnswerGeneral {
        try {
            if (init_sk.paramsglobal.registrationstatus == true) {
                val sk_answer: SkDataApi = apisk.sk_device_revoke(
                    "[\"${init_sk.paramsglobal.deviceId}\"]",
                    X_Hyber_Session_Id,
                    init_sk.paramsglobal.sk_registration_token
                )
                Log.d(TAG, "sk_answer : ${sk_answer.toString()}");

                if (sk_answer.code == 200) {
                    Log.d(TAG, "start clear data");
                    init_sk.clearData()
                    return answ.general_answer("200", "{\"device\":\"${init_sk.paramsglobal.deviceId}\"}", "Success")
                } else {
                    if  (sk_answer.code == 401){
                        try{ init_sk.clearData()} catch (ee:Exception){}
                    }
                    return answ.general_answer(sk_answer.code.toString(), "{\"body\":\"unknown\"}", "Some problem")
                }
            } else {
                return answer_not_registered
            }
        } catch (e: Exception) {
            return answer_not_known
        }
    }

    //return all message history till time
    //3
    fun sk_get_message_history(period_in_seconds: Int): SkFunAnswerGeneral {
        try {
            if (init_sk.paramsglobal.registrationstatus == true) {
                val mess_hist_svyazcom: SkFunAnswerGeneral = apisk.sk_get_message_history(
                    X_Hyber_Session_Id,
                    init_sk.paramsglobal.sk_registration_token,
                    period_in_seconds
                )
                println(mess_hist_svyazcom)
                if  (mess_hist_svyazcom.code == 401){
                    try{ init_sk.clearData()} catch (ee:Exception){}
                }
                return answ.general_answer(mess_hist_svyazcom.code.toString(), mess_hist_svyazcom.body, "Success")
            } else {
                return answer_not_registered
            }
        } catch (e: Exception) {
            return answer_not_known
        }
    }

    //4
    fun sk_get_device_all_from_sk(): SkFunAnswerGeneral {
        try {
            if (init_sk.paramsglobal.registrationstatus == true) {
                val device_all_svyazcom: SkDataApi = apisk.sk_get_device_all(
                    X_Hyber_Session_Id,
                    init_sk.paramsglobal.sk_registration_token
                )
                Log.d(TAG, "device_all_svyazcom : $device_all_svyazcom");
                if  (device_all_svyazcom.code == 401){
                    try{ init_sk.clearData()} catch (ee:Exception){}
                }
                return answ.general_answer(device_all_svyazcom.code.toString(), device_all_svyazcom.body, "Success")
            } else {
                return answer_not_registered
            }
        } catch (e: Exception) {
            return answer_not_known
        }
    }

    private fun init_firebase(context: Context, activity: Activity, icon: Int) {
        //val fireb: MyFirebaseInstanceIdService = MyFirebaseInstanceIdService(context, activity, icon)
        //FirebaseApp.initializeApp(context)
        println("Token firebase: ${init_sk.paramsglobal.firebase_registration_token}")
    }

    //5
    fun sk_update_registration(): SkFunAnswerGeneral {
        try {
            if (init_sk.paramsglobal.registrationstatus == true) {
                val resss: SkDataApi = apisk.sk_device_update(
                    init_sk.paramsglobal.sk_registration_token,
                    X_Hyber_Session_Id,
                    init_sk.paramsglobal.sk_deviceName,
                    init_sk.paramsglobal.sk_deviceType,
                    init_sk.paramsglobal.sk_osType,
                    init_sk.paramsglobal.sdkversion,
                    init_sk.paramsglobal.firebase_registration_token
                )
                if  (resss.code == 401){
                    try{ init_sk.clearData()} catch (ee:Exception){}
                }
                return answ.general_answer(resss.code.toString(), resss.body, "Success")
            } else {
                return answer_not_registered
            }
        } catch (e: Exception) {
            return answer_not_known
        }
    }

    //6
    fun sk_send_message_callback(message_id: String, message_text: String): SkFunAnswerGeneral {
        try {
            if (init_sk.paramsglobal.registrationstatus == true) {
                val respp: SkDataApi = apisk.sk_message_callback(
                    message_id,
                    message_text,
                    X_Hyber_Session_Id,
                    init_sk.paramsglobal.sk_registration_token
                )
                if  (respp.code == 401){
                    try{ init_sk.clearData()} catch (ee:Exception){}
                }
                return answ.general_answer(respp.code.toString(), respp.body, "Success")
            } else {
                return answer_not_registered
            }
        } catch (e: Exception) {
            return answer_not_known
        }
    }

    //7
    fun sk_message_delivery_report(message_id: String): SkFunAnswerGeneral {
        try {
            if (init_sk.paramsglobal.registrationstatus == true) {
                val respp1: SkDataApi = apisk.sk_message_dr(
                    message_id,
                    X_Hyber_Session_Id,
                    init_sk.paramsglobal.sk_registration_token
                )
                if  (respp1.code == 401){
                    try{ init_sk.clearData()} catch (ee:Exception){}
                }
                return answ.general_answer(respp1.code.toString(), respp1.body, "Success")
            } else {
                return answer_not_registered
            }
        } catch (e: Exception) {
            return answer_not_known
        }
    }


    //8 delete all devices
    fun sk_clear_all_device(): SkFunAnswerGeneral {
        try {
            if (init_sk.paramsglobal.registrationstatus == true) {
                val device_all_svyazcom: SkDataApi = apisk.sk_get_device_all(
                    X_Hyber_Session_Id,
                    init_sk.paramsglobal.sk_registration_token
                )

                val device_list: String = parsing.parse_id_devices_all(device_all_svyazcom.body)

                val sk_answer: SkDataApi = apisk.sk_device_revoke(
                    device_list,
                    X_Hyber_Session_Id,
                    init_sk.paramsglobal.sk_registration_token
                )
                Log.d(TAG, "sk_answer : $sk_answer");

                if (sk_answer.code == 200) {
                    Log.d(TAG, "start clear data");
                    init_sk.clearData()
                    return answ.general_answer("200", "{\"devices\":$device_list}", "Success")
                } else {
                    if  (sk_answer.code == 401){
                        try{ init_sk.clearData()} catch (ee:Exception){}
                    }
                    return answ.general_answer(sk_answer.code.toString(), "{\"body\":\"unknown\"}", "Some problem")

                }
            } else {
                return answer_not_registered
            }

        } catch (e: Exception) {
            return answer_not_known
        }
    }

    //9temp
    fun rewrite_msisdn(newmsisdn: String): SkFunAnswerGeneral {
        try {
            if (init_sk.paramsglobal.registrationstatus == true) {
            rewrite_params.rewrite_sk_user_msisdn(newmsisdn)
            return answ.general_answer("200",  "{}", "Success")
            } else {
                return answer_not_registered
            }
        } catch (e: Exception) {
            //println("failed rewrite msisdn")
            return answer_not_known
        }
    }

    //10temp
    fun rewrite_password(newpassword: String): SkFunAnswerGeneral {
        try {
            if (init_sk.paramsglobal.registrationstatus == true) {
            rewrite_params.rewrite_sk_user_password(newpassword)
            return answ.general_answer("200",  "{}", "Success")
        } else {
            return answer_not_registered
        }
        } catch (e: Exception) {
            //println("failed rewrite password")
            return answer_not_known
        }
    }

}

