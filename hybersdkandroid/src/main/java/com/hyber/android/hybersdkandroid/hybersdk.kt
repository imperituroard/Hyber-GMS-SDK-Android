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
}

lateinit var HyberDatabase: HyberOperativeData

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
            val initHyberParams2 = Initialization(context)
            val localDataLoaded = initHyberParams2.hSdkGetParametersFromLocal()

            return if (localDataLoaded.registrationStatus) {
                val queue = QueueProc()
                val anss = queue.hyberDeviceMessQueue(
                    localDataLoaded.firebase_registration_token,
                    localDataLoaded.hyber_registration_token, context
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
    private var initHObject: Initialization = Initialization(context)
    private var localDeviceInfo: GetInfo = GetInfo()
    private var apiHyberData: HyberApi = HyberApi()
    private var answerAny: Answer = Answer()
    private var rewriteParams: RewriteParams = RewriteParams(context)
    private var parsing: HyberParsing = HyberParsing()
    private var hyberInternalParamsObject: PushSdkParameters = PushSdkParameters
    private var hyberDeviceType: String = ""

    //main class initialization
    init {
        this.context = context
        HyberPushMess.log_level_active = log_level
        hyberDeviceType = localDeviceInfo.getPhoneType(context)
        PushSdkParameters.branch_current_active = platform_branch
        try {
            val localDataLoaded = initHObject.hSdkGetParametersFromLocal()
            if (localDataLoaded.registrationStatus) {
                this.hyber_update_registration()
            }
        } catch (e: Exception) {
            HyberLoggerSdk.error("HyberSDK.init registration update problem $e")
        }
    }




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
            initHObject.hSdkGetParametersFromLocal()
            val xHyberSessionId = HyberDatabase.firebase_registration_token
            HyberLoggerSdk.debug("Start hyber_register_new: X_Hyber_Client_API_Key: ${X_Hyber_Client_API_Key}, X_Hyber_App_Fingerprint: ${X_Hyber_App_Fingerprint}, registrationstatus: ${HyberDatabase.registrationStatus}, X_Hyber_Session_Id: $xHyberSessionId")

            if (HyberDatabase.registrationStatus) {
                return answerAny.hyberRegisterNewRegisterExists2(
                    HyberDatabase.deviceId,
                    HyberDatabase.hyber_registration_token,
                    HyberDatabase.hyber_user_id,
                    HyberDatabase.hyber_user_msisdn,
                    HyberDatabase.hyber_registration_createdAt
                )
            } else {

                if (xHyberSessionId != "" && xHyberSessionId != " ") {
                    val respHyber: HyberDataApi2 = apiHyberData.hDeviceRegister(
                        X_Hyber_Client_API_Key,
                        xHyberSessionId,
                        X_Hyber_App_Fingerprint,
                        PushSdkParameters.hyber_deviceName,
                        hyberDeviceType,
                        PushSdkParameters.hyber_osType,
                        PushSdkParameters.sdkVersion,
                        user_password,
                        user_msisdn,
                        context
                    )
                    rewriteParams.rewriteHyberUserMsisdn(user_msisdn)
                    rewriteParams.rewriteHyberUserPassword(user_password)

                    HyberLoggerSdk.debug("hyber_register_new response: $respHyber")
                    HyberLoggerSdk.debug("uuid: ${HyberDatabase.hyber_uuid}")

                    var regStatus = false
                    if (respHyber.code == 200) {
                        regStatus = true
                    }

                    initHObject.hSdkInitSaveToLocal(
                        respHyber.body.deviceId,
                        user_msisdn,
                        user_password,
                        respHyber.body.token,
                        respHyber.body.userId,
                        respHyber.body.createdAt,
                        regStatus
                    )
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
            val localDataLoaded = initHObject.hSdkGetParametersFromLocal()
            HyberLoggerSdk.debug("Start hyber_register_new: X_Hyber_Client_API_Key: ${X_Hyber_Client_API_Key}, X_Hyber_App_Fingerprint: ${X_Hyber_App_Fingerprint}, registrationstatus: ${localDataLoaded.registrationStatus}, X_Hyber_Session_Id: $X_FCM_token")

            if (localDataLoaded.registrationStatus) {
                return answerAny.hyberRegisterNewRegisterExists2(
                    localDataLoaded.deviceId,
                    localDataLoaded.hyber_registration_token,
                    localDataLoaded.hyber_user_id,
                    localDataLoaded.hyber_user_msisdn,
                    localDataLoaded.hyber_registration_createdAt
                )

            } else {
                initHObject.hSdkUpdateFirebaseManual(X_FCM_token)
                if (X_FCM_token != "" && X_FCM_token != " ") {
                    val respHyber: HyberDataApi2 = apiHyberData.hDeviceRegister(
                        X_Hyber_Client_API_Key,
                        X_FCM_token,
                        X_Hyber_App_Fingerprint,
                        PushSdkParameters.hyber_deviceName,
                        hyberDeviceType,
                        PushSdkParameters.hyber_osType,
                        PushSdkParameters.sdkVersion,
                        user_password,
                        user_msisdn,
                        context
                    )
                    rewriteParams.rewriteHyberUserMsisdn(user_msisdn)
                    rewriteParams.rewriteHyberUserPassword(user_password)

                    HyberLoggerSdk.debug("hyber_register_new response: $respHyber")
                    HyberLoggerSdk.debug("uuid: ${localDataLoaded.hyber_uuid}")

                    var regStatus = false
                    if (respHyber.code == 200) {
                        regStatus = true
                    }

                    initHObject.hSdkInitSaveToLocal(
                        respHyber.body.deviceId,
                        user_msisdn,
                        user_password,
                        respHyber.body.token,
                        respHyber.body.userId,
                        respHyber.body.createdAt,
                        regStatus
                    )

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
            val localDataLoaded = initHObject.hSdkGetParametersFromLocal()
            val xHyberSessionId = localDataLoaded.firebase_registration_token
            if (localDataLoaded.registrationStatus) {
                HyberLoggerSdk.debug("Start hyber_clear_current_device: firebase_registration_token: ${xHyberSessionId}, hyber_registration_token: ${localDataLoaded.hyber_registration_token}, registrationstatus: ${localDataLoaded.registrationStatus}, deviceId: ${localDataLoaded.deviceId}")

                val hyberAnswer: HyberDataApi = apiHyberData.hDeviceRevoke(
                    "[\"${localDataLoaded.deviceId}\"]",
                    xHyberSessionId,
                    localDataLoaded.hyber_registration_token
                )
                HyberLoggerSdk.debug("hyber_answer : $hyberAnswer")

                if (hyberAnswer.code == 200) {
                    HyberLoggerSdk.debug("start clear data")
                    val deviceId = localDataLoaded.deviceId
                    initHObject.clearData()
                    return answerAny.generalAnswer(
                        "200",
                        "{\"device\":\"$deviceId\"}",
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
            val localDataLoaded = initHObject.hSdkGetParametersFromLocal()
            HyberLoggerSdk.debug("Start hyber_get_message_history request: firebase_registration_token: ${localDataLoaded.firebase_registration_token}, hyber_registration_token: ${localDataLoaded.hyber_registration_token}, period_in_seconds: $period_in_seconds")
            if (localDataLoaded.registrationStatus) {
                val messHistHyber: HyberFunAnswerGeneral = apiHyberData.hGetMessageHistory(
                    localDataLoaded.firebase_registration_token, //_xHyberSessionId
                    localDataLoaded.hyber_registration_token,
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
            val localDataLoaded = initHObject.hSdkGetParametersFromLocal()
            HyberLoggerSdk.debug("Start hyber_get_device_all_from_hyber request: firebase_registration_token: ${localDataLoaded.firebase_registration_token}, hyber_registration_token: ${localDataLoaded.hyber_registration_token}")

            if (localDataLoaded.registrationStatus) {
                val deviceAllHyber: HyberDataApi = apiHyberData.hGetDeviceAll(
                    localDataLoaded.firebase_registration_token, //_xHyberSessionId
                    localDataLoaded.hyber_registration_token
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
            if (HyberDatabase.registrationStatus) {
                val resss: HyberDataApi = apiHyberData.hDeviceUpdate(
                    HyberDatabase.hyber_registration_token,
                    HyberDatabase.firebase_registration_token, //_xHyberSessionId
                    hyberInternalParamsObject.hyber_deviceName,
                    hyberDeviceType,
                    hyberInternalParamsObject.hyber_osType,
                    hyberInternalParamsObject.sdkVersion,
                    HyberDatabase.firebase_registration_token
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
            val localDataLoaded = initHObject.hSdkGetParametersFromLocal()
            if (localDataLoaded.registrationStatus) {
                val respp: HyberDataApi = apiHyberData.hMessageCallback(
                    message_id,
                    message_text,
                    localDataLoaded.firebase_registration_token, //_xHyberSessionId
                    localDataLoaded.hyber_registration_token
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
            val localDataLoaded = initHObject.hSdkGetParametersFromLocal()
            if (localDataLoaded.registrationStatus) {
                if (localDataLoaded.hyber_registration_token != "" && localDataLoaded.firebase_registration_token != "") {
                    val respp1: HyberDataApi = apiHyberData.hMessageDr(
                        message_id,
                        localDataLoaded.firebase_registration_token, //_xHyberSessionId
                        localDataLoaded.hyber_registration_token
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
            if (HyberDatabase.registrationStatus) {
                val deviceAllHyber: HyberDataApi = apiHyberData.hGetDeviceAll(
                    HyberDatabase.firebase_registration_token, //_xHyberSessionId
                    HyberDatabase.hyber_registration_token
                )

                val deviceList: String = parsing.parseIdDevicesAll(deviceAllHyber.body)

                val hyberAnswer: HyberDataApi = apiHyberData.hDeviceRevoke(
                    deviceList,
                    HyberDatabase.firebase_registration_token, //_xHyberSessionId
                    HyberDatabase.hyber_registration_token
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
            val localDataLoaded = initHObject.hSdkGetParametersFromLocal()
            if (localDataLoaded.registrationStatus) {
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

        val localDataLoaded = initHObject.hSdkGetParametersFromLocal()
        return if (localDataLoaded.registrationStatus) {
            rewriteParams.rewriteHyberUserPassword(newPassword)
            answerAny.generalAnswer("200", "{}", "Success")
        } else {
            answerNotRegistered
        }
    }


    //11hyber
    fun hyber_check_queue(): HyberFunAnswerGeneral {
        try {


            val localDataLoaded = initHObject.hSdkGetParametersFromLocal()

            if (localDataLoaded.registrationStatus) {
                if (localDataLoaded.firebase_registration_token != "" && localDataLoaded.hyber_registration_token != "") {
                    val queue = QueueProc()
                    val answerData = queue.hyberDeviceMessQueue(
                        localDataLoaded.firebase_registration_token,
                        localDataLoaded.hyber_registration_token, context
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
