package com.hyber.android.hybersdkandroid

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import com.hyber.android.hybersdkandroid.add.Answer
import com.hyber.android.hybersdkandroid.add.GetInfo
import com.hyber.android.hybersdkandroid.add.HyberParsing
import com.hyber.android.hybersdkandroid.add.RewriteParams
import com.hyber.android.hybersdkandroid.core.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.properties.Delegates

object HyberPushMess {
    var message: String? = null   //global variable
}

class HyberSDKQueue {
    fun hyber_check_queue(context: Context): HyberFunAnswerGeneral {
        val answerNotKnown =
            HyberFunAnswerGeneral(710, "Failed", "Unknown error", "unknown")

        try {
            val answ = Answer()
            val answerNotRegistered = HyberFunAnswerGeneral(
                704,
                "Failed",
                "Registration data not found",
                "Not registered"
            )

            val initHyberParams2 = Initialization(context)
            initHyberParams2.hyber_init3()
            if (initHyberParams2.parametersGlobal.registrationStatus) {
                val queue = QueueProc()
                val anss = queue.hyberDeviceMessQueue(
                    initHyberParams2.parametersGlobal.firebase_registration_token,
                    initHyberParams2.parametersGlobal.hyber_registration_token, context
                )
                println(anss)
                return answ.generalAnswer("200", "{}", "Success")
            } else {
                return answerNotRegistered
            }
        } catch (e: Exception) {
            //println("failed rewrite password")
            return answerNotKnown
        }
    }
}

class HyberSDK(
    context: Context,
    platform_branch: UrlsPlatformList = HyberParametersPublic.branchMasterValue
) {

    //any classes initialization
    private var context: Context by Delegates.notNull()
    private var initHObject: Initialization = Initialization(context)
    private var localDeviceInfo: GetInfo = GetInfo()
    private var apiHyberData: HyberApi = HyberApi()
    private var answerAny: Answer = Answer()
    private var rewriteParams: RewriteParams = RewriteParams(context)
    private var parsing: HyberParsing = HyberParsing()
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    //main class initialization
    init {
        this.context = context
        initHObject.hInit(
            "android",
            localDeviceInfo.getPhoneType(context),
            localDeviceInfo.getDeviceName().toString(),
            platform_branch
        )
        try {
            if (initHObject.parametersGlobal.registrationStatus) {
                this.hyber_update_registration()
            }
        } catch (e: Exception) {
            println("registration update problem")
        }
    }

    //private var hyber_storage: HyberStorage = HyberStorage(context)
    //parameter for device identification

    private var _xHyberSessionId: String = initHObject.parametersGlobal.firebase_registration_token

    private var answerNotRegistered: HyberFunAnswerGeneral =
        HyberFunAnswerGeneral(704, "Failed", "Registration data not found", "Not registered")
    private var answerNotKnown: HyberFunAnswerGeneral =
        HyberFunAnswerGeneral(710, "Failed", "Unknown error", "unknown")

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

    //network errors
    //901 - failed registration with firebase

    //{
    //    "result":"Ok",
    //    "description":"",
    //    "code":200,
    //    "body":{
    //}
    //}


    //1
    fun hyber_register_new(
        X_Hyber_Client_API_Key: String,
        X_Hyber_App_Fingerprint: String,
        user_msisdn: String,
        user_password: String
    ): HyberFunAnswerRegister {
        logger.debug("test logger")
        try {
            Log.d(
                TAG,
                "Start hyber_register_new: X_Hyber_Client_API_Key: ${X_Hyber_Client_API_Key}, X_Hyber_App_Fingerprint: ${X_Hyber_App_Fingerprint}, registrationstatus: ${initHObject.parametersGlobal.registrationStatus}, X_Hyber_Session_Id: $_xHyberSessionId"
            )
            if (initHObject.parametersGlobal.registrationStatus) {
                return answerAny.hyberRegisterNewRegisterExists2(
                    initHObject.parametersGlobal,
                    context
                )
            } else {
                initHObject.hUpdateFirebaseAuto()
                _xHyberSessionId = initHObject.parametersGlobal.firebase_registration_token
                if (_xHyberSessionId != "" && _xHyberSessionId != " ") {
                    val respHyber: HyberDataApi2 = apiHyberData.hDeviceRegister(
                        X_Hyber_Client_API_Key,
                        _xHyberSessionId,
                        X_Hyber_App_Fingerprint,
                        initHObject.parametersGlobal.hyber_deviceName,
                        initHObject.parametersGlobal.hyber_deviceType,
                        initHObject.parametersGlobal.hyber_osType,
                        initHObject.parametersGlobal.sdkVersion,
                        user_password,
                        user_msisdn,
                        context
                    )
                    Log.d(TAG, "hyber_register_new response: $respHyber")
                    Log.d(TAG, "uuid: ${initHObject.parametersGlobal.hyber_uuid}")
                    return HyberFunAnswerRegister(
                        code = respHyber.code,
                        result = respHyber.body.result,
                        description = respHyber.body.description,
                        deviceId = respHyber.body.deviceId,
                        token = respHyber.body.token,
                        userId = respHyber.body.userId,
                        userPhone = respHyber.body.userPhone,
                        createdAt = respHyber.body.createdAt
                    )
                } else {
                    return answerAny.registerProcedureAnswer2(
                        "901",
                        "X_Hyber_Session_Id is empty. Maybe firebase registration problem",
                        context
                    )
                }
            }
        } catch (e: Exception) {
            return answerAny.registerProcedureAnswer2("700", "unknown", context)
        }
        //apihyber.hyber_device_register()
    }


    //1-1
    //registration procedure with direct FCM token input
    //hyber_register_new2(
    fun hyber_register_new2(
        X_Hyber_Client_API_Key: String,    // APP API key on hyber platform
        X_Hyber_App_Fingerprint: String,   // App Fingerprint key
        user_msisdn: String,               // User MSISDN
        user_password: String,             // User Password
        X_FCM_token: String                // FCM firebase token
    ): HyberFunAnswerRegister {
        try {
            Log.d(
                TAG,
                "Start hyber_register_new: X_Hyber_Client_API_Key: ${X_Hyber_Client_API_Key}, X_Hyber_App_Fingerprint: ${X_Hyber_App_Fingerprint}, registrationstatus: ${initHObject.parametersGlobal.registrationStatus}, X_Hyber_Session_Id: $_xHyberSessionId"
            )
            if (initHObject.parametersGlobal.registrationStatus) {
                return answerAny.hyberRegisterNewRegisterExists2(
                    initHObject.parametersGlobal,
                    context
                )
            } else {
                initHObject.hUpdateFirebaseManual(X_FCM_token)
                if (X_FCM_token != "" && X_FCM_token != " ") {
                    val respHyber: HyberDataApi2 = apiHyberData.hDeviceRegister(
                        X_Hyber_Client_API_Key,
                        X_FCM_token,
                        X_Hyber_App_Fingerprint,
                        initHObject.parametersGlobal.hyber_deviceName,
                        initHObject.parametersGlobal.hyber_deviceType,
                        initHObject.parametersGlobal.hyber_osType,
                        initHObject.parametersGlobal.sdkVersion,
                        user_password,
                        user_msisdn,
                        context
                    )

                    Log.d(TAG, "hyber_register_new response: $respHyber")
                    Log.d(TAG, "uuid: ${initHObject.parametersGlobal.hyber_uuid}")
                    return HyberFunAnswerRegister(
                        code = respHyber.code,
                        result = respHyber.body.result,
                        description = respHyber.body.description,
                        deviceId = respHyber.body.deviceId,
                        token = respHyber.body.token,
                        userId = respHyber.body.userId,
                        userPhone = respHyber.body.userPhone,
                        createdAt = respHyber.body.createdAt
                    )
                } else {
                    return answerAny.registerProcedureAnswer2(
                        "901",
                        "X_Hyber_Session_Id is empty. Maybe firebase registration problem",
                        context
                    )
                }
            }
        } catch (e: Exception) {
            return answerAny.registerProcedureAnswer2("700", "unknown", context)
        }
    }

    //2
    fun hyber_clear_current_device(): HyberFunAnswerGeneral {
        try {
            if (initHObject.parametersGlobal.registrationStatus) {
                val hyber_answer: HyberDataApi = apiHyberData.hDeviceRevoke(
                    "[\"${initHObject.parametersGlobal.deviceId}\"]",
                    _xHyberSessionId,
                    initHObject.parametersGlobal.hyber_registration_token
                )
                Log.d(TAG, "hyber_answer : $hyber_answer")

                if (hyber_answer.code == 200) {
                    Log.d(TAG, "start clear data")
                    initHObject.clearData()
                    return answerAny.generalAnswer(
                        "200",
                        "{\"device\":\"${initHObject.parametersGlobal.deviceId}\"}",
                        "Success"
                    )
                } else {
                    if (hyber_answer.code == 401) {
                        try {
                            initHObject.clearData()
                        } catch (ee: Exception) {
                        }
                    }
                    return answerAny.generalAnswer(
                        hyber_answer.code.toString(),
                        "{\"body\":\"unknown\"}",
                        "Some problem"
                    )
                }
            } else {
                return answerNotRegistered
            }
        } catch (e: Exception) {
            return answerNotKnown
        }
    }

    //return all message history till time
    //3
    fun hyber_get_message_history(period_in_seconds: Int): HyberFunAnswerGeneral {
        try {
            if (initHObject.parametersGlobal.registrationStatus) {
                val mess_hist_hyber: HyberFunAnswerGeneral = apiHyberData.hGetMessageHistory(
                    _xHyberSessionId,
                    initHObject.parametersGlobal.hyber_registration_token,
                    period_in_seconds
                )
                println(mess_hist_hyber)
                if (mess_hist_hyber.code == 401) {
                    try {
                        initHObject.clearData()
                    } catch (ee: Exception) {
                    }
                }
                return answerAny.generalAnswer(
                    mess_hist_hyber.code.toString(),
                    mess_hist_hyber.body,
                    "Success"
                )
            } else {
                return answerNotRegistered
            }
        } catch (e: Exception) {
            return answerNotKnown
        }
    }

    //4
    fun hyber_get_device_all_from_hyber(): HyberFunAnswerGeneral {
        try {
            if (initHObject.parametersGlobal.registrationStatus) {
                val device_all_hyber: HyberDataApi = apiHyberData.hGetDeviceAll(
                    _xHyberSessionId,
                    initHObject.parametersGlobal.hyber_registration_token
                )
                Log.d(TAG, "device_all_hyber : $device_all_hyber")
                if (device_all_hyber.code == 401) {
                    try {
                        initHObject.clearData()
                    } catch (ee: Exception) {
                    }
                }
                return answerAny.generalAnswer(
                    device_all_hyber.code.toString(),
                    device_all_hyber.body,
                    "Success"
                )
            } else {
                return answerNotRegistered
            }
        } catch (e: Exception) {
            return answerNotKnown
        }
    }

    //5
    private fun hyber_update_registration(): HyberFunAnswerGeneral {
        try {
            if (initHObject.parametersGlobal.registrationStatus) {
                val resss: HyberDataApi = apiHyberData.hDeviceUpdate(
                    initHObject.parametersGlobal.hyber_registration_token,
                    _xHyberSessionId,
                    initHObject.parametersGlobal.hyber_deviceName,
                    initHObject.parametersGlobal.hyber_deviceType,
                    initHObject.parametersGlobal.hyber_osType,
                    initHObject.parametersGlobal.sdkVersion,
                    initHObject.parametersGlobal.firebase_registration_token
                )
                if (resss.code == 401) {
                    try {
                        initHObject.clearData()
                    } catch (ee: Exception) {
                    }
                }
                return answerAny.generalAnswer(resss.code.toString(), resss.body, "Success")
            } else {
                return answerNotRegistered
            }
        } catch (e: Exception) {
            return answerNotKnown
        }
    }

    //6
    fun hyber_send_message_callback(
        message_id: String,
        message_text: String
    ): HyberFunAnswerGeneral {
        try {
            if (initHObject.parametersGlobal.registrationStatus) {
                val respp: HyberDataApi = apiHyberData.hMessageCallback(
                    message_id,
                    message_text,
                    _xHyberSessionId,
                    initHObject.parametersGlobal.hyber_registration_token
                )
                if (respp.code == 401) {
                    try {
                        initHObject.clearData()
                    } catch (ee: Exception) {
                    }
                }
                return answerAny.generalAnswer(respp.code.toString(), respp.body, "Success")
            } else {
                return answerNotRegistered
            }
        } catch (e: Exception) {
            return answerNotKnown
        }
    }

    //7
    fun hyber_message_delivery_report(message_id: String): HyberFunAnswerGeneral {
        try {
            if (initHObject.parametersGlobal.registrationStatus) {
                if (initHObject.parametersGlobal.hyber_registration_token != "" && _xHyberSessionId != "") {
                    val respp1: HyberDataApi = apiHyberData.hMessageDr(
                        message_id,
                        _xHyberSessionId,
                        initHObject.parametersGlobal.hyber_registration_token
                    )
                    if (respp1.code == 401) {
                        try {
                            initHObject.clearData()
                        } catch (ee: Exception) {
                        }
                    }
                    return answerAny.generalAnswer(respp1.code.toString(), respp1.body, "Success")
                } else {
                    return answerAny.generalAnswer(
                        "700",
                        "{}",
                        "Failed. firebase_registration_token or hyber_registration_token empty"
                    )
                }
            } else {
                return answerNotRegistered
            }
        } catch (e: Exception) {
            return answerNotKnown
        }
    }


    //8 delete all devices
    fun hyber_clear_all_device(): HyberFunAnswerGeneral {
        try {
            if (initHObject.parametersGlobal.registrationStatus) {
                val device_all_hyber: HyberDataApi = apiHyberData.hGetDeviceAll(
                    _xHyberSessionId,
                    initHObject.parametersGlobal.hyber_registration_token
                )

                val deviceList: String = parsing.parseIdDevicesAll(device_all_hyber.body)

                val hyberAnswer: HyberDataApi = apiHyberData.hDeviceRevoke(
                    deviceList,
                    _xHyberSessionId,
                    initHObject.parametersGlobal.hyber_registration_token
                )
                Log.d(TAG, "hyber_answer : $hyberAnswer")

                if (hyberAnswer.code == 200) {
                    Log.d(TAG, "start clear data")
                    initHObject.clearData()
                    return answerAny.generalAnswer("200", "{\"devices\":$deviceList}", "Success")
                } else {
                    if (hyberAnswer.code == 401) {
                        try {
                            initHObject.clearData()
                        } catch (ee: Exception) {
                        }
                    }
                    return answerAny.generalAnswer(
                        hyberAnswer.code.toString(),
                        "{\"body\":\"unknown\"}",
                        "Some problem"
                    )

                }
            } else {
                return answerNotRegistered
            }

        } catch (e: Exception) {
            return answerNotKnown
        }
    }

    //9temp
    fun rewrite_msisdn(newmsisdn: String): HyberFunAnswerGeneral {
        try {
            if (initHObject.parametersGlobal.registrationStatus) {
                rewriteParams.rewriteHyberUserMsisdn(newmsisdn)
                return answerAny.generalAnswer("200", "{}", "Success")
            } else {
                return answerNotRegistered
            }
        } catch (e: Exception) {
            return answerNotKnown
        }
    }

    //10temp
    fun rewrite_password(newpassword: String): HyberFunAnswerGeneral {
        try {
            if (initHObject.parametersGlobal.registrationStatus) {
                rewriteParams.rewriteHyberUserPassword(newpassword)
                return answerAny.generalAnswer("200", "{}", "Success")
            } else {
                return answerNotRegistered
            }
        } catch (e: Exception) {
            //println("failed rewrite password")
            return answerNotKnown
        }
    }


    //11hyber
    fun hyber_check_queue(): HyberFunAnswerGeneral {
        try {
            val inithyber_params2 = Initialization(context)
            inithyber_params2.hyber_init3()
            if (inithyber_params2.parametersGlobal.registrationStatus) {
                if (inithyber_params2.parametersGlobal.firebase_registration_token != "" && inithyber_params2.parametersGlobal.hyber_registration_token != "") {
                    val queue = QueueProc()
                    val anss = queue.hyberDeviceMessQueue(
                        inithyber_params2.parametersGlobal.firebase_registration_token,
                        inithyber_params2.parametersGlobal.hyber_registration_token, context
                    )
                    println(anss)
                    return answerAny.generalAnswer("200", "{}", "Success")
                } else {
                    return answerAny.generalAnswer(
                        "700",
                        "{}",
                        "Failed. firebase_registration_token or hyber_registration_token empty"
                    )
                }

            } else {
                return answerNotRegistered
            }
        } catch (e: Exception) {
            return answerNotKnown
        }
    }

}

