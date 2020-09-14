package com.hyber.android.hybersdkandroid

import android.content.Context
import com.hyber.android.hybersdkandroid.add.Answer
import com.hyber.android.hybersdkandroid.add.GetInfo
import com.hyber.android.hybersdkandroid.add.HyberParsing
import com.hyber.android.hybersdkandroid.add.RewriteParams
import com.hyber.android.hybersdkandroid.core.*
import com.hyber.android.hybersdkandroid.logger.HyberLoggerSdk
import kotlin.properties.Delegates

@Suppress("SpellCheckingInspection")
object HyberPushMess {
    var message: String? = null   //global variable for push messages
    var log_level_active: String = "error" //global variable sdk log level
    var hyberInternalParamsObject: PushSdkParameters = PushSdkParameters
}

@Suppress("SpellCheckingInspection", "unused", "FunctionName")
class HyberSDKQueue {

    fun hyber_check_queue(context: Context): HyberFunAnswerGeneral {
        val answerNotKnown = HyberFunAnswerGeneral(710, "Failed", "Unknown error", "unknown")
        try {
            val answ = Answer()
            val answerNotRegistered = HyberFunAnswerGeneral(
                704,
                "Failed",
                "Registration data not found",
                "Not registered"
            )
            val hyberInternalParamsObjectQueue = PushSdkParameters
            val initHyberParams2 = Initialization(context, hyberInternalParamsObjectQueue)
            initHyberParams2.hSdkInit2()
            return if (hyberInternalParamsObjectQueue.registrationStatus) {
                val queue = QueueProc()
                val anss = queue.hyberDeviceMessQueue(
                    hyberInternalParamsObjectQueue.firebase_registration_token,
                    hyberInternalParamsObjectQueue.hyber_registration_token, context
                )
                HyberLoggerSdk.debug("HyberSDKQueue.hyber_check_queue response: $anss")
                answ.generalAnswer("200", "{}", "Success")
            } else {
                answerNotRegistered
            }
        } catch (e: Exception) {
            return answerNotKnown
        }
    }
}

@Suppress("SpellCheckingInspection", "unused", "FunctionName", "MemberVisibilityCanBePrivate")
class HyberSDK(
    context: Context,
    platform_branch: UrlsPlatformList = PushSdkParametersPublic.branchMasterValue,
    log_level: String = "error"
) {

    //any classes initialization
    private var context: Context by Delegates.notNull()
    private var initHObject: Initialization = Initialization(context, HyberPushMess.hyberInternalParamsObject)
    private var localDeviceInfo: GetInfo = GetInfo()
    private var apiHyberData: HyberApi = HyberApi()
    private var answerAny: Answer = Answer()
    private var rewriteParams: RewriteParams = RewriteParams(context)
    private var parsing: HyberParsing = HyberParsing()

    //main class initialization
    init {
        this.context = context
        HyberPushMess.log_level_active = log_level
        initHObject.hSdkInit(
            "android",
            localDeviceInfo.getPhoneType(context),
            localDeviceInfo.getDeviceName().toString(),
            platform_branch
        )
        try {
            if (HyberPushMess.hyberInternalParamsObject.registrationStatus) {
                this.hyber_update_registration()
            }
        } catch (e: Exception) {
            HyberLoggerSdk.error("HyberSDK.init registration update problem $e")
        }
    }

    private var _xHyberSessionId: String = HyberPushMess.hyberInternalParamsObject.firebase_registration_token


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
        try {
            HyberLoggerSdk.debug("Start hyber_register_new: X_Hyber_Client_API_Key: ${X_Hyber_Client_API_Key}, X_Hyber_App_Fingerprint: ${X_Hyber_App_Fingerprint}, registrationstatus: ${HyberPushMess.hyberInternalParamsObject.registrationStatus}, X_Hyber_Session_Id: $_xHyberSessionId")

            if (HyberPushMess.hyberInternalParamsObject.registrationStatus) {
                return answerAny.hyberRegisterNewRegisterExists2(
                    HyberPushMess.hyberInternalParamsObject,
                    context,
                    HyberPushMess.hyberInternalParamsObject
                )
            } else {
                initHObject.hSdkUpdateFirebaseAuto()
                _xHyberSessionId = HyberPushMess.hyberInternalParamsObject.firebase_registration_token
                if (_xHyberSessionId != "" && _xHyberSessionId != " ") {
                    val respHyber: HyberDataApi2 = apiHyberData.hDeviceRegister(
                        X_Hyber_Client_API_Key,
                        _xHyberSessionId,
                        X_Hyber_App_Fingerprint,
                        HyberPushMess.hyberInternalParamsObject.hyber_deviceName,
                        HyberPushMess.hyberInternalParamsObject.hyber_deviceType,
                        HyberPushMess.hyberInternalParamsObject.hyber_osType,
                        HyberPushMess.hyberInternalParamsObject.sdkVersion,
                        user_password,
                        user_msisdn,
                        context
                    )
                    rewriteParams.rewriteHyberUserMsisdn(user_msisdn)
                    rewriteParams.rewriteHyberUserPassword(user_password)

                    HyberLoggerSdk.debug("hyber_register_new response: $respHyber")
                    HyberLoggerSdk.debug("uuid: ${HyberPushMess.hyberInternalParamsObject.hyber_uuid}")

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

            HyberLoggerSdk.debug("Start hyber_register_new: X_Hyber_Client_API_Key: ${X_Hyber_Client_API_Key}, X_Hyber_App_Fingerprint: ${X_Hyber_App_Fingerprint}, registrationstatus: ${HyberPushMess.hyberInternalParamsObject.registrationStatus}, X_Hyber_Session_Id: $_xHyberSessionId")

            if (HyberPushMess.hyberInternalParamsObject.registrationStatus) {
                return answerAny.hyberRegisterNewRegisterExists2(
                    HyberPushMess.hyberInternalParamsObject,
                    context,
                    HyberPushMess.hyberInternalParamsObject
                )

            } else {
                initHObject.hSdkUpdateFirebaseManual(X_FCM_token)
                if (X_FCM_token != "" && X_FCM_token != " ") {
                    val respHyber: HyberDataApi2 = apiHyberData.hDeviceRegister(
                        X_Hyber_Client_API_Key,
                        X_FCM_token,
                        X_Hyber_App_Fingerprint,
                        HyberPushMess.hyberInternalParamsObject.hyber_deviceName,
                        HyberPushMess.hyberInternalParamsObject.hyber_deviceType,
                        HyberPushMess.hyberInternalParamsObject.hyber_osType,
                        HyberPushMess.hyberInternalParamsObject.sdkVersion,
                        user_password,
                        user_msisdn,
                        context
                    )
                    rewriteParams.rewriteHyberUserMsisdn(user_msisdn)
                    rewriteParams.rewriteHyberUserPassword(user_password)

                    HyberLoggerSdk.debug("hyber_register_new response: $respHyber")
                    HyberLoggerSdk.debug("uuid: ${HyberPushMess.hyberInternalParamsObject.hyber_uuid}")

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
            if (HyberPushMess.hyberInternalParamsObject.registrationStatus) {
                initHObject.hSdkUpdateFirebaseAuto()
                HyberLoggerSdk.debug("Start hyber_clear_current_device: firebase_registration_token: ${HyberPushMess.hyberInternalParamsObject.firebase_registration_token}, hyber_registration_token: ${HyberPushMess.hyberInternalParamsObject.hyber_registration_token}, registrationstatus: ${HyberPushMess.hyberInternalParamsObject.registrationStatus}, deviceId: ${HyberPushMess.hyberInternalParamsObject.deviceId}")

                val hyberAnswer: HyberDataApi = apiHyberData.hDeviceRevoke(
                    "[\"${HyberPushMess.hyberInternalParamsObject.deviceId}\"]",
                    HyberPushMess.hyberInternalParamsObject.firebase_registration_token,
                    HyberPushMess.hyberInternalParamsObject.hyber_registration_token
                )
                HyberLoggerSdk.debug("hyber_answer : $hyberAnswer")

                if (hyberAnswer.code == 200) {
                    HyberLoggerSdk.debug("start clear data")

                    initHObject.clearData()
                    return answerAny.generalAnswer(
                        "200",
                        "{\"device\":\"${HyberPushMess.hyberInternalParamsObject.deviceId}\"}",
                        "Success"
                    )
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

    //return all message history till time
    //3
    fun hyber_get_message_history(period_in_seconds: Int): HyberFunAnswerGeneral {
        try {
            HyberLoggerSdk.debug("Start hyber_get_message_history request: firebase_registration_token: ${HyberPushMess.hyberInternalParamsObject.firebase_registration_token}, hyber_registration_token: ${HyberPushMess.hyberInternalParamsObject.hyber_registration_token}, period_in_seconds: $period_in_seconds")
            if (HyberPushMess.hyberInternalParamsObject.registrationStatus) {
                initHObject.hSdkUpdateFirebaseAuto()
                val messHistHyber: HyberFunAnswerGeneral = apiHyberData.hGetMessageHistory(
                    HyberPushMess.hyberInternalParamsObject.firebase_registration_token, //_xHyberSessionId
                    HyberPushMess.hyberInternalParamsObject.hyber_registration_token,
                    period_in_seconds
                )
                HyberLoggerSdk.debug("hyber_get_message_history mess_hist_hyber: $messHistHyber")

                if (messHistHyber.code == 401) {
                    try {
                        initHObject.clearData()
                    } catch (ee: Exception) {
                    }
                }
                return answerAny.generalAnswer(
                    messHistHyber.code.toString(),
                    messHistHyber.body,
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
            HyberLoggerSdk.debug("Start hyber_get_device_all_from_hyber request: firebase_registration_token: ${HyberPushMess.hyberInternalParamsObject.firebase_registration_token}, hyber_registration_token: ${HyberPushMess.hyberInternalParamsObject.hyber_registration_token}")

            if (HyberPushMess.hyberInternalParamsObject.registrationStatus) {
                initHObject.hSdkUpdateFirebaseAuto()
                val deviceAllHyber: HyberDataApi = apiHyberData.hGetDeviceAll(
                    HyberPushMess.hyberInternalParamsObject.firebase_registration_token, //_xHyberSessionId
                    HyberPushMess.hyberInternalParamsObject.hyber_registration_token
                )
                HyberLoggerSdk.debug("device_all_hyber : $deviceAllHyber")

                if (deviceAllHyber.code == 401) {
                    try {
                        initHObject.clearData()
                    } catch (ee: Exception) {
                    }
                }
                return answerAny.generalAnswer(
                    deviceAllHyber.code.toString(),
                    deviceAllHyber.body,
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
    fun hyber_update_registration(): HyberFunAnswerGeneral {
        try {
            if (HyberPushMess.hyberInternalParamsObject.registrationStatus) {
                initHObject.hSdkUpdateFirebaseAuto()
                val resss: HyberDataApi = apiHyberData.hDeviceUpdate(
                    HyberPushMess.hyberInternalParamsObject.hyber_registration_token,
                    HyberPushMess.hyberInternalParamsObject.firebase_registration_token, //_xHyberSessionId
                    HyberPushMess.hyberInternalParamsObject.hyber_deviceName,
                    HyberPushMess.hyberInternalParamsObject.hyber_deviceType,
                    HyberPushMess.hyberInternalParamsObject.hyber_osType,
                    HyberPushMess.hyberInternalParamsObject.sdkVersion,
                    HyberPushMess.hyberInternalParamsObject.firebase_registration_token
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
            if (HyberPushMess.hyberInternalParamsObject.registrationStatus) {
                initHObject.hSdkUpdateFirebaseAuto()
                val respp: HyberDataApi = apiHyberData.hMessageCallback(
                    message_id,
                    message_text,
                    HyberPushMess.hyberInternalParamsObject.firebase_registration_token, //_xHyberSessionId
                    HyberPushMess.hyberInternalParamsObject.hyber_registration_token
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
            if (HyberPushMess.hyberInternalParamsObject.registrationStatus) {
                initHObject.hSdkUpdateFirebaseAuto()
                if (HyberPushMess.hyberInternalParamsObject.hyber_registration_token != "" && _xHyberSessionId != "") {
                    val respp1: HyberDataApi = apiHyberData.hMessageDr(
                        message_id,
                        HyberPushMess.hyberInternalParamsObject.firebase_registration_token, //_xHyberSessionId
                        HyberPushMess.hyberInternalParamsObject.hyber_registration_token
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
            if (HyberPushMess.hyberInternalParamsObject.registrationStatus) {
                initHObject.hSdkUpdateFirebaseAuto()
                val deviceAllHyber: HyberDataApi = apiHyberData.hGetDeviceAll(
                    HyberPushMess.hyberInternalParamsObject.firebase_registration_token, //_xHyberSessionId
                    HyberPushMess.hyberInternalParamsObject.hyber_registration_token
                )

                val deviceList: String = parsing.parseIdDevicesAll(deviceAllHyber.body)

                val hyberAnswer: HyberDataApi = apiHyberData.hDeviceRevoke(
                    deviceList,
                    HyberPushMess.hyberInternalParamsObject.firebase_registration_token, //_xHyberSessionId
                    HyberPushMess.hyberInternalParamsObject.hyber_registration_token
                )

                HyberLoggerSdk.debug("hyber_answer : $hyberAnswer")

                if (hyberAnswer.code == 200) {
                    HyberLoggerSdk.debug("start clear data")
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
        return try {
            if (HyberPushMess.hyberInternalParamsObject.registrationStatus) {
                rewriteParams.rewriteHyberUserMsisdn(newmsisdn)
                answerAny.generalAnswer("200", "{}", "Success")
            } else {
                answerNotRegistered
            }
        } catch (e: Exception) {
            answerNotKnown
        }
    }

    //10temp
    fun rewrite_password(newPassword: String): HyberFunAnswerGeneral {
        return if (HyberPushMess.hyberInternalParamsObject.registrationStatus) {
            rewriteParams.rewriteHyberUserPassword(newPassword)
            answerAny.generalAnswer("200", "{}", "Success")
        } else {
            answerNotRegistered
        }
    }


    //11hyber
    fun hyber_check_queue(): HyberFunAnswerGeneral {
        try {

            val initHParams2 = Initialization(context, HyberPushMess.hyberInternalParamsObject)
            initHParams2.hSdkInit2()
            if (HyberPushMess.hyberInternalParamsObject.registrationStatus) {
                if (HyberPushMess.hyberInternalParamsObject.firebase_registration_token != "" && HyberPushMess.hyberInternalParamsObject.hyber_registration_token != "") {
                    val queue = QueueProc()
                    val answerData = queue.hyberDeviceMessQueue(
                        HyberPushMess.hyberInternalParamsObject.firebase_registration_token,
                        HyberPushMess.hyberInternalParamsObject.hyber_registration_token, context
                    )

                    HyberLoggerSdk.debug("hyber_check_queue answerData: $answerData")

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
