package com.hyber.android.hybersdkandroid.core

import android.content.Context
import com.hyber.android.hybersdkandroid.add.Answer
import com.hyber.android.hybersdkandroid.add.GetInfo
import com.hyber.android.hybersdkandroid.logger.PushKLoggerSdk
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.net.URL
import java.nio.charset.Charset
import java.security.MessageDigest
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLSocketFactory

//class for communication with push rest server (REST API)
internal class PushKApi {

    //class init for creation answers
    private var answerForm: Answer = Answer()
    private var osVersionClass: GetInfo = GetInfo()

    //parameters for procedures
    private val osVersion: String = osVersionClass.getAndroidVersion()

    //function for create special token for another procedures
    private fun hash(sss: String): String {
        return try {
            val bytes = sss.toByteArray()
            val md = MessageDigest.getInstance("SHA-256")
            val digest = md.digest(bytes)
            val resp: String = digest.fold("", { str, it -> str + "%02x".format(it) })
            PushKLoggerSdk.debug("Result: OK, Function: hash, Class: PushKApi, input: $sss, output: $resp")
            resp
        } catch (e: Exception) {
            PushKLoggerSdk.debug("Result: FAILED, Function: hash, Class: PushKApi, input: $sss, output: failed")
            "failed"
        }
    }

    //POST procedure for new registration
    fun hDeviceRegister(
        xPlatformClientAPIKey: String,
        X_Push_Session_Id: String,
        X_Push_App_Fingerprint: String,
        device_Name: String,
        device_Type: String,
        os_Type: String,
        sdk_Version: String,
        user_Pass: String,
        user_Phone: String,
        context: Context
    ): PushKDataApi2 {
        var functionNetAnswer = HyberFunAnswerRegister()
        var functionCodeAnswer = 0

        val threadNetF1 = Thread(Runnable {
            try {
                PushKLoggerSdk.debug("Result: Start step1, Function: push_device_register, Class: PushKApi, xPlatformClientAPIKey: $xPlatformClientAPIKey, X_Push_Session_Id: $X_Push_Session_Id, X_Push_App_Fingerprint: $X_Push_App_Fingerprint, device_Name: $device_Name, device_Type: $device_Type, os_Type: $os_Type, sdk_Version: $sdk_Version, user_Pass: $user_Pass, user_Phone: $user_Phone")

                val message =
                    "{\"userPhone\":\"$user_Phone\",\"userPass\":\"$user_Pass\",\"osType\":\"$os_Type\",\"osVersion\":\"$osVersion\",\"deviceType\":\"$device_Type\",\"deviceName\":\"$device_Name\",\"sdkVersion\":\"$sdk_Version\"}"
                //val message = "{\"userPhone\":\"$user_Phone\",\"userPass\":\"$user_Pass\",\"osType\":\"$os_Type\",\"osVersion\":\"$os_version\",\"deviceType\":\"$device_Type\",\"deviceName\":\"$device_Name\",\"sdkVersion\":\"$sdk_Version\"}"

                PushKLoggerSdk.debug("Result: Start step2, Function: push_device_register, Class: PushKApi, message: $message")

                val currentTimestamp = System.currentTimeMillis()
                val postData: ByteArray = message.toByteArray(Charset.forName("UTF-8"))
                val mURL = URL(PushSdkParameters.branch_current_active.fun_pushsdk_url_registration)
                val connectorWebPlatform = mURL.openConnection() as HttpsURLConnection
                connectorWebPlatform.doOutput = true
                connectorWebPlatform.setRequestProperty("Content-Language", "en-US")
                connectorWebPlatform.setRequestProperty(
                    "X-Hyber-Client-API-Key",
                    xPlatformClientAPIKey
                )
                connectorWebPlatform.setRequestProperty("Content-Type", "application/json")
                connectorWebPlatform.setRequestProperty("X-Hyber-Session-Id", X_Push_Session_Id)
                connectorWebPlatform.setRequestProperty(
                    "X-Hyber-App-Fingerprint",
                    X_Push_App_Fingerprint
                )
                connectorWebPlatform.sslSocketFactory =
                    SSLSocketFactory.getDefault() as SSLSocketFactory

                with(connectorWebPlatform) {
                    requestMethod = "POST"
                    doOutput = true
                    val wr = DataOutputStream(outputStream)

                    wr.write(postData)

                    wr.flush()

                    PushKLoggerSdk.debug("Result: Finished step3, Function: push_device_register, Class: PushKApi, Response Code : $responseCode")

                    functionCodeAnswer = responseCode
                    if (responseCode == 200) {

                        BufferedReader(InputStreamReader(inputStream)).use {
                            val response = StringBuffer()

                            var inputLine = it.readLine()
                            while (inputLine != null) {
                                response.append(inputLine)
                                inputLine = it.readLine()
                            }
                            it.close()

                            PushKLoggerSdk.debug("Result: Finished step4, Function: push_device_register, Class: PushKApi, Response : $response")

                            functionNetAnswer = answerForm.registerProcedureAnswer2(
                                responseCode.toString(),
                                response.toString(),
                                context
                            )
                        }
                    } else {
                        functionNetAnswer = answerForm.registerProcedureAnswer2(
                            responseCode.toString(),
                            "unknown",
                            context
                        )

                    }
                }
            } catch (e: Exception) {

                PushKLoggerSdk.debug("Result: Failed step5, Function: push_device_register, Class: PushKApi, exception: ${e.stackTrace}")

                functionNetAnswer = answerForm.registerProcedureAnswer2(
                    "705",
                    "unknown",
                    context
                )
            }
        })

        threadNetF1.start()
        threadNetF1.join()

        return PushKDataApi2(functionNetAnswer.code, functionNetAnswer, 0)
    }

    //POST
    fun hDeviceRevoke(
        dev_list: String,
        X_Push_Session_Id: String,
        X_Push_Auth_Token: String
    ): PushKDataApi {

        var functionNetAnswer2 = String()

        val threadNetF2 = Thread(Runnable {

            try {

                PushKLoggerSdk.debug("Result: Start step1, Function: push_device_revoke, Class: PushKApi, dev_list: $dev_list, X_Push_Session_Id: $X_Push_Session_Id, X_Push_Auth_Token: $X_Push_Auth_Token")

                val message2 = "{\"devices\":$dev_list}"

                PushKLoggerSdk.debug("Result: Start step2, Function: push_device_revoke, Class: PushKApi, message2: $message2")

                val currentTimestamp2 = System.currentTimeMillis() // We want timestamp in seconds
                val authToken = hash("$X_Push_Auth_Token:$currentTimestamp2")
                val postData2: ByteArray = message2.toByteArray(Charset.forName("UTF-8"))
                val mURL2 = URL(PushSdkParameters.branch_current_active.fun_pushsdk_url_revoke)

                val connectorWebPlatform = mURL2.openConnection() as HttpsURLConnection
                connectorWebPlatform.doOutput = true
                connectorWebPlatform.setRequestProperty("Content-Language", "en-US")
                connectorWebPlatform.setRequestProperty("Content-Type", "application/json")
                connectorWebPlatform.setRequestProperty("X-Hyber-Session-Id", X_Push_Session_Id)
                connectorWebPlatform.setRequestProperty(
                    "X-Hyber-Timestamp",
                    currentTimestamp2.toString()
                )
                connectorWebPlatform.setRequestProperty("X-Hyber-Auth-Token", authToken)

                connectorWebPlatform.sslSocketFactory =
                    SSLSocketFactory.getDefault() as SSLSocketFactory

                with(connectorWebPlatform) {
                    requestMethod = "POST"
                    doOutput = true
                    val wr = DataOutputStream(outputStream)
                    wr.write(postData2)
                    wr.flush()

                    try {
                        BufferedReader(InputStreamReader(inputStream)).use {
                            val response = StringBuffer()

                            var inputLine = it.readLine()
                            while (inputLine != null) {
                                response.append(inputLine)
                                inputLine = it.readLine()
                            }
                            it.close()
                            PushKLoggerSdk.debug("Response : $response")
                        }
                    } catch (e: Exception) {
                        PushKLoggerSdk.debug("Failed")
                    }

                    functionNetAnswer2 = responseCode.toString()
                }
            } catch (e: Exception) {
                functionNetAnswer2 = "500"
            }
        })

        threadNetF2.start()
        threadNetF2.join()

        return PushKDataApi(functionNetAnswer2.toInt(), "{}", 0)
    }

    //GET
    fun hGetMessageHistory(
        X_Push_Session_Id: String,
        X_Push_Auth_Token: String,
        period_in_seconds: Int
    ): HyberFunAnswerGeneral {

        var functionNetAnswer3 = String()
        var functionCodeAnswer3 = 0

        val threadNetF3 = Thread(Runnable {
            try {

                //this timestamp for URL.
                val currentTimestamp1 = (System.currentTimeMillis() / 1000L) - period_in_seconds // We want timestamp in seconds

                PushKLoggerSdk.debug("Result: val currentTimestamp1, Function: hGetMessageHistory, Class: PushKApi, currentTimestamp1: $currentTimestamp1")

                //this timestamp for token
                val currentTimestamp2 = System.currentTimeMillis() / 1000L

                PushKLoggerSdk.debug("Result: val currentTimestamp2, Function: hGetMessageHistory, Class: PushKApi, currentTimestamp2: $currentTimestamp2")

                val authToken = hash("$X_Push_Auth_Token:$currentTimestamp2")

                PushKLoggerSdk.debug("Result: val authToken, Function: hGetMessageHistory, Class: PushKApi, authToken: $authToken")


                PushKLoggerSdk.debug("\nSent 'GET' request to push_get_device_all with : X_Push_Session_Id : $X_Push_Session_Id; X_Push_Auth_Token : $X_Push_Auth_Token; period_in_seconds : $period_in_seconds")

                val mURL2 = URL(PushSdkParameters.branch_current_active.pushsdk_url_message_history + currentTimestamp1.toString())

                with(mURL2.openConnection() as HttpsURLConnection) {
                    requestMethod = "GET"  // optional default is GET

                    //doOutput = true
                    setRequestProperty("Content-Language", "en-US")
                    setRequestProperty("Content-Type", "application/json")
                    setRequestProperty("X-Hyber-Session-Id", X_Push_Session_Id)
                    setRequestProperty("X-Hyber-Timestamp", currentTimestamp2.toString())
                    setRequestProperty("X-Hyber-Auth-Token", authToken)

                    sslSocketFactory = SSLSocketFactory.getDefault() as SSLSocketFactory

                    requestMethod = "GET"

                    PushKLoggerSdk.debug("Sent 'GET' request to URL : $url; Response Code : $responseCode")
                    functionCodeAnswer3 = responseCode

                    inputStream.bufferedReader().use {
                        functionNetAnswer3 = it.readLine().toString()
                        PushKLoggerSdk.debug("Result: val functionNetAnswer3, Function: hGetMessageHistory, Class: PushKApi, functionNetAnswer3: $functionNetAnswer3")
                    }
                }
            } catch (e: Exception) {
                PushKLoggerSdk.debug("Result: Failed step5, Function: push_device_register, Class: PushKApi, exception: ${e.stackTrace}")
                functionCodeAnswer3 = 700
                functionNetAnswer3 = "Failed"
            }
        })

        threadNetF3.start()
        threadNetF3.join()
        return HyberFunAnswerGeneral(functionCodeAnswer3, "OK", "Processed", functionNetAnswer3)

    }


    //GET
    fun hGetDeviceAll(X_Push_Session_Id: String, X_Push_Auth_Token: String): PushKDataApi {

        try {

            var functionNetAnswer4 = String()
            var functionCodeAnswer4 = 0

            val threadNetF4 = Thread(Runnable {


                try {

                    val currentTimestamp2 =
                        System.currentTimeMillis() // We want timestamp in seconds

                    val authToken = hash("$X_Push_Auth_Token:$currentTimestamp2")


                    PushKLoggerSdk.debug("Result: Start step1, Function: push_get_device_all, Class: PushKApi, X_Push_Session_Id: $X_Push_Session_Id, X_Push_Auth_Token: $X_Push_Auth_Token, currentTimestamp2: $currentTimestamp2, auth_token: $authToken")


                    val mURL2 =
                        URL(PushSdkParameters.branch_current_active.fun_pushsdk_url_get_device_all)


                    with(mURL2.openConnection() as HttpsURLConnection) {
                        requestMethod = "GET"  // optional default is GET
                        //doOutput = true
                        setRequestProperty("Content-Language", "en-US")
                        setRequestProperty("Content-Type", "application/json")
                        setRequestProperty("X-Hyber-Session-Id", X_Push_Session_Id)
                        setRequestProperty("X-Hyber-Timestamp", currentTimestamp2.toString())
                        setRequestProperty("X-Hyber-Auth-Token", authToken)

                        sslSocketFactory = SSLSocketFactory.getDefault() as SSLSocketFactory

                        PushKLoggerSdk.debug("\nSent 'GET' request to URL : $url; Response Code : $responseCode")
                        functionCodeAnswer4 = responseCode

                        //if (responseCode==401) { init_push.clearData() }

                        inputStream.bufferedReader().use {

                            functionNetAnswer4 = it.readLine().toString()

                            PushKLoggerSdk.debug("Result: Finish step2, Function: push_get_device_all, Class: PushKApi, function_net_answer4: $functionNetAnswer4")
                        }
                    }
                } catch (e: Exception) {
                    PushKLoggerSdk.debug("Result: Failed step3, Function: push_get_device_all, Class: PushKApi, exception: $e")


                    functionNetAnswer4 = "Failed"
                }
            })

            threadNetF4.start()
            threadNetF4.join()
            return PushKDataApi(functionCodeAnswer4, functionNetAnswer4, 0)

        } catch (e: Exception) {
            return PushKDataApi(700, "Failed", 0)
        }

    }

    //POST
    fun hDeviceUpdate(
        X_Push_Auth_Token: String,
        X_Push_Session_Id: String,
        device_Name: String,
        device_Type: String,
        os_Type: String,
        sdk_Version: String,
        fcm_Token: String
    ): PushKDataApi {

        var functionNetAnswer5 = String()
        var functionCodeAnswer5 = 0

        val threadNetF5 = Thread(Runnable {

            try {
                val message =
                    "{\"fcmToken\": \"$fcm_Token\",\"osType\": \"$os_Type\",\"osVersion\": \"$osVersion\",\"deviceType\": \"$device_Type\",\"deviceName\": \"$device_Name\",\"sdkVersion\": \"$sdk_Version\" }"

                PushKLoggerSdk.debug(message)

                val currentTimestamp2 = System.currentTimeMillis() // We want timestamp in seconds

                val authToken = hash("$X_Push_Auth_Token:$currentTimestamp2")

                val postData: ByteArray = message.toByteArray(Charset.forName("UTF-8"))

                val mURL = URL(PushSdkParameters.branch_current_active.fun_pushsdk_url_device_update)

                val connectorWebPlatform = mURL.openConnection() as HttpsURLConnection
                connectorWebPlatform.doOutput = true
                connectorWebPlatform.setRequestProperty("Content-Language", "en-US")
                connectorWebPlatform.setRequestProperty("Content-Type", "application/json")
                connectorWebPlatform.setRequestProperty("X-Hyber-Session-Id", X_Push_Session_Id)
                connectorWebPlatform.setRequestProperty("X-Hyber-Auth-Token", authToken)
                connectorWebPlatform.setRequestProperty(
                    "X-Hyber-Timestamp",
                    currentTimestamp2.toString()
                )
                connectorWebPlatform.sslSocketFactory =
                    SSLSocketFactory.getDefault() as SSLSocketFactory

                with(connectorWebPlatform) {
                    requestMethod = "POST"
                    doOutput = true

                    val wr = DataOutputStream(outputStream)

                    wr.write(postData)

                    wr.flush()

                    functionCodeAnswer5 = responseCode

                    BufferedReader(InputStreamReader(inputStream)).use {
                        val response = StringBuffer()

                        var inputLine = it.readLine()
                        while (inputLine != null) {
                            response.append(inputLine)
                            inputLine = it.readLine()
                        }
                        it.close()
                        functionNetAnswer5 = response.toString()
                    }
                }

            } catch (e: Exception) {
                PushKLoggerSdk.debug("Result: Failed step5, Function: push_device_register, Class: PushKApi, exception: ${e.stackTrace}")

                functionNetAnswer5 = "Failed"
            }


        })

        threadNetF5.start()
        threadNetF5.join()

        return PushKDataApi(functionCodeAnswer5, functionNetAnswer5, 0)


    }

    //POST
    fun hMessageCallback(
        message_id: String,
        push_answer: String,
        X_Push_Session_Id: String,
        X_Push_Auth_Token: String
    ): PushKDataApi {

        var functionNetAnswer6 = String()
        var functionCodeAnswer6 = 0

        val threadNetF6 = Thread(Runnable {

            try {
                val message2 = "{\"messageId\": \"$message_id\", \"answer\": \"$push_answer\"}"
                PushKLoggerSdk.debug("Body message to push server : $message2")
                val currentTimestamp2 = System.currentTimeMillis() // We want timestamp in seconds

                val authToken = hash("$X_Push_Auth_Token:$currentTimestamp2")


                val postData2: ByteArray = message2.toByteArray(Charset.forName("UTF-8"))

                val mURL2 =
                    URL(PushSdkParameters.branch_current_active.fun_pushsdk_url_message_callback)

                val connectorWebPlatform = mURL2.openConnection() as HttpsURLConnection
                connectorWebPlatform.doOutput = true
                connectorWebPlatform.setRequestProperty("Content-Language", "en-US")
                connectorWebPlatform.setRequestProperty("Content-Type", "application/json")
                connectorWebPlatform.setRequestProperty("X-Hyber-Session-Id", X_Push_Session_Id)
                connectorWebPlatform.setRequestProperty(
                    "X-Hyber-Timestamp",
                    currentTimestamp2.toString()
                )
                connectorWebPlatform.setRequestProperty("X-Hyber-Auth-Token", authToken)

                connectorWebPlatform.sslSocketFactory =
                    SSLSocketFactory.getDefault() as SSLSocketFactory


                with(connectorWebPlatform) {
                    requestMethod = "POST"
                    doOutput = true

                    val wr = DataOutputStream(outputStream)

                    wr.write(postData2)
                    wr.flush()
                    PushKLoggerSdk.debug("URL : $url")
                    PushKLoggerSdk.debug("Response Code : $responseCode")
                    functionCodeAnswer6 = responseCode
                    BufferedReader(InputStreamReader(inputStream)).use {
                        val response = StringBuffer()

                        var inputLine = it.readLine()
                        while (inputLine != null) {
                            response.append(inputLine)
                            inputLine = it.readLine()
                        }
                        it.close()
                        PushKLoggerSdk.debug("Response : $response")

                        functionNetAnswer6 = response.toString()
                    }
                }

            } catch (e: Exception) {
                PushKLoggerSdk.debug("Result: Failed step5, Function: push_device_register, Class: PushKApi, exception: ${e.stackTrace}")
            }
        })

        threadNetF6.start()
        threadNetF6.join()
        return PushKDataApi(functionCodeAnswer6, functionNetAnswer6, 0)


    }

    //POST
    fun hMessageDr(
        message_id: String,
        X_Push_Session_Id: String,
        X_Push_Auth_Token: String
    ): PushKDataApi {

        if (X_Push_Session_Id != "" && X_Push_Auth_Token != "" && message_id != "") {

            var functionNetAnswer7 = String()

            val threadNetF7 = Thread(Runnable {

                try {
                    val message2 = "{\"messageId\": \"$message_id\"}"

                    PushKLoggerSdk.debug("Body message to push server : $message2")
                    val currentTimestamp2 =
                        System.currentTimeMillis() // We want timestamp in seconds

                    PushKLoggerSdk.debug("Timestamp : $currentTimestamp2")

                    val authToken = hash("$X_Push_Auth_Token:$currentTimestamp2")

                    val postData2: ByteArray = message2.toByteArray(Charset.forName("UTF-8"))

                    val mURL2 =
                        URL(PushSdkParameters.branch_current_active.fun_pushsdk_url_message_dr)

                    val connectorWebPlatform = mURL2.openConnection() as HttpsURLConnection
                    connectorWebPlatform.doOutput = true
                    connectorWebPlatform.setRequestProperty("Content-Language", "en-US")
                    connectorWebPlatform.setRequestProperty("Content-Type", "application/json")
                    connectorWebPlatform.setRequestProperty(
                        "X-Hyber-Session-Id",
                        X_Push_Session_Id
                    )
                    connectorWebPlatform.setRequestProperty(
                        "X-Hyber-Timestamp",
                        currentTimestamp2.toString()
                    )
                    connectorWebPlatform.setRequestProperty("X-Hyber-Auth-Token", authToken)

                    connectorWebPlatform.sslSocketFactory =
                        SSLSocketFactory.getDefault() as SSLSocketFactory


                    with(connectorWebPlatform) {
                        requestMethod = "POST"
                        doOutput = true
                        val wr = DataOutputStream(outputStream)
                        wr.write(postData2)
                        wr.flush()
                        PushKLoggerSdk.debug("URL : $url")
                        PushKLoggerSdk.debug("Response Code : $responseCode")

                        BufferedReader(InputStreamReader(inputStream)).use {
                            val response = StringBuffer()

                            var inputLine = it.readLine()
                            while (inputLine != null) {
                                response.append(inputLine)
                                inputLine = it.readLine()
                            }
                            it.close()
                            PushKLoggerSdk.debug("Response : $response")
                        }
                        functionNetAnswer7 = responseCode.toString()
                    }
                } catch (e: Exception) {
                    functionNetAnswer7 = "500"
                }
            })
            threadNetF7.start()
            threadNetF7.join()
            return PushKDataApi(functionNetAnswer7.toInt(), "{}", 0)
        } else {
            return PushKDataApi(700, "{}", 0)
        }
    }
}