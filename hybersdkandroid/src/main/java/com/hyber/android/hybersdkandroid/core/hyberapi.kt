package com.hyber.android.hybersdkandroid.core

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import com.hyber.android.hybersdkandroid.add.Answer
import com.hyber.android.hybersdkandroid.add.GetInfo
import com.hyber.android.hybersdkandroid.logger.HyberLoggerSdk
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.net.URL
import java.nio.charset.Charset
import java.security.MessageDigest
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLSocketFactory

//class for communication with hyber rest server (REST API)
internal class HyberApi {

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
            HyberLoggerSdk.debug("Result: OK, Function: hash, Class: HyberApi, input: $sss, output: $resp")
            resp
        } catch (e: Exception) {
            HyberLoggerSdk.debug("Result: FAILED, Function: hash, Class: HyberApi, input: $sss, output: failed")
            "failed"
        }
    }

    //POST procedure for new registration
    fun hDeviceRegister(
        xPlatformClientAPIKey: String,
        X_Hyber_Session_Id: String,
        X_Hyber_App_Fingerprint: String,
        device_Name: String,
        device_Type: String,
        os_Type: String,
        sdk_Version: String,
        user_Pass: String,
        user_Phone: String,
        context: Context
    ): HyberDataApi2 {
        var functionNetAnswer = HyberFunAnswerRegister()
        var functionCodeAnswer = 0

        val threadNetF1 = Thread(Runnable {
            try {
                HyberLoggerSdk.debug("Result: Start step1, Function: hyber_device_register, Class: HyberApi, xPlatformClientAPIKey: $xPlatformClientAPIKey, X_Hyber_Session_Id: $X_Hyber_Session_Id, X_Hyber_App_Fingerprint: $X_Hyber_App_Fingerprint, device_Name: $device_Name, device_Type: $device_Type, os_Type: $os_Type, sdk_Version: $sdk_Version, user_Pass: $user_Pass, user_Phone: $user_Phone")

                val message =
                    "{\"userPhone\":\"$user_Phone\",\"userPass\":\"$user_Pass\",\"osType\":\"$os_Type\",\"osVersion\":\"$osVersion\",\"deviceType\":\"$device_Type\",\"deviceName\":\"$device_Name\",\"sdkVersion\":\"$sdk_Version\"}"
                //val message = "{\"userPhone\":\"$user_Phone\",\"userPass\":\"$user_Pass\",\"osType\":\"$os_Type\",\"osVersion\":\"$os_version\",\"deviceType\":\"$device_Type\",\"deviceName\":\"$device_Name\",\"sdkVersion\":\"$sdk_Version\"}"

                HyberLoggerSdk.debug("Result: Start step2, Function: hyber_device_register, Class: HyberApi, message: $message")

                val currentTimestamp = System.currentTimeMillis()
                val postData: ByteArray = message.toByteArray(Charset.forName("UTF-8"))
                val mURL = URL(PushSdkParameters.branch_current_active.fun_hyber_url_registration)
                val connectorWebPlatform = mURL.openConnection() as HttpsURLConnection
                connectorWebPlatform.doOutput = true
                connectorWebPlatform.setRequestProperty("Content-Language", "en-US")
                connectorWebPlatform.setRequestProperty(
                    "X-Hyber-Client-API-Key",
                    xPlatformClientAPIKey
                )
                connectorWebPlatform.setRequestProperty("Content-Type", "application/json")
                connectorWebPlatform.setRequestProperty("X-Hyber-Session-Id", X_Hyber_Session_Id)
                connectorWebPlatform.setRequestProperty(
                    "X-Hyber-App-Fingerprint",
                    X_Hyber_App_Fingerprint
                )
                connectorWebPlatform.sslSocketFactory =
                    SSLSocketFactory.getDefault() as SSLSocketFactory

                with(connectorWebPlatform) {
                    requestMethod = "POST"
                    doOutput = true
                    val wr = DataOutputStream(outputStream)

                    wr.write(postData)

                    wr.flush()

                    HyberLoggerSdk.debug("Result: Finished step3, Function: hyber_device_register, Class: HyberApi, Response Code : $responseCode")

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

                            HyberLoggerSdk.debug("Result: Finished step4, Function: hyber_device_register, Class: HyberApi, Response : $response")

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

                HyberLoggerSdk.debug("Result: Failed step5, Function: hyber_device_register, Class: HyberApi, exception: ${e.stackTrace}")

                functionNetAnswer = answerForm.registerProcedureAnswer2(
                    "705",
                    "unknown",
                    context
                )
            }
        })

        threadNetF1.start()
        threadNetF1.join()

        return HyberDataApi2(functionNetAnswer.code, functionNetAnswer, 0)
    }

    //POST
    fun hDeviceRevoke(
        dev_list: String,
        X_Hyber_Session_Id: String,
        X_Hyber_Auth_Token: String
    ): HyberDataApi {

        var functionNetAnswer2 = String()

        val threadNetF2 = Thread(Runnable {

            try {

                HyberLoggerSdk.debug("Result: Start step1, Function: hyber_device_revoke, Class: HyberApi, dev_list: $dev_list, X_Hyber_Session_Id: $X_Hyber_Session_Id, X_Hyber_Auth_Token: $X_Hyber_Auth_Token")

                val message2 = "{\"devices\":$dev_list}"

                HyberLoggerSdk.debug("Result: Start step2, Function: hyber_device_revoke, Class: HyberApi, message2: $message2")

                val currentTimestamp2 = System.currentTimeMillis() // We want timestamp in seconds
                val authToken = hash("$X_Hyber_Auth_Token:$currentTimestamp2")
                val postData2: ByteArray = message2.toByteArray(Charset.forName("UTF-8"))
                val mURL2 = URL(PushSdkParameters.branch_current_active.fun_hyber_url_revoke)

                val connectorWebPlatform = mURL2.openConnection() as HttpsURLConnection
                connectorWebPlatform.doOutput = true
                connectorWebPlatform.setRequestProperty("Content-Language", "en-US")
                connectorWebPlatform.setRequestProperty("Content-Type", "application/json")
                connectorWebPlatform.setRequestProperty("X-Hyber-Session-Id", X_Hyber_Session_Id)
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
                            HyberLoggerSdk.debug("Response : $response")
                        }
                    } catch (e: Exception) {
                        HyberLoggerSdk.debug("Failed")
                    }

                    functionNetAnswer2 = responseCode.toString()
                }
            } catch (e: Exception) {
                functionNetAnswer2 = "500"
            }
        })

        threadNetF2.start()
        threadNetF2.join()

        return HyberDataApi(functionNetAnswer2.toInt(), "{}", 0)
    }

    //GET
    fun hGetMessageHistory(
        X_Hyber_Session_Id: String,
        X_Hyber_Auth_Token: String,
        period_in_seconds: Int
    ): HyberFunAnswerGeneral {

        var functionNetAnswer3 = String()
        var functionCodeAnswer3 = 0

        val threadNetF3 = Thread(Runnable {
            try {

                val currentTimestamp2 =
                    System.currentTimeMillis() - period_in_seconds // We want timestamp in seconds

                val authToken = hash("$X_Hyber_Auth_Token:$currentTimestamp2")

                HyberLoggerSdk.debug("\nSent 'GET' request to hyber_get_device_all with : X_Hyber_Session_Id : $X_Hyber_Session_Id; X_Hyber_Auth_Token : $X_Hyber_Auth_Token; period_in_seconds : $period_in_seconds")

                val mURL2 =
                    URL(PushSdkParameters.branch_current_active.hyber_url_message_history + currentTimestamp2.toString())

                with(mURL2.openConnection() as HttpsURLConnection) {
                    requestMethod = "GET"  // optional default is GET

                    //doOutput = true
                    setRequestProperty("Content-Language", "en-US")
                    setRequestProperty("Content-Type", "application/json")
                    setRequestProperty("X-Hyber-Session-Id", X_Hyber_Session_Id)
                    setRequestProperty("X-Hyber-Timestamp", currentTimestamp2.toString())
                    setRequestProperty("X-Hyber-Auth-Token", authToken)

                    sslSocketFactory = SSLSocketFactory.getDefault() as SSLSocketFactory

                    requestMethod = "GET"

                    HyberLoggerSdk.debug("Sent 'GET' request to URL : $url; Response Code : $responseCode")
                    functionCodeAnswer3 = responseCode

                    inputStream.bufferedReader().use {
                        functionNetAnswer3 = it.readLine().toString()
                    }
                }
            } catch (e: Exception) {

                HyberLoggerSdk.debug("Result: Failed step5, Function: hyber_device_register, Class: HyberApi, exception: ${e.stackTrace}")
                functionCodeAnswer3 = 700
                functionNetAnswer3 = "Failed"

            }

        })

        threadNetF3.start()
        threadNetF3.join()
        return HyberFunAnswerGeneral(functionCodeAnswer3, "OK", "Processed", functionNetAnswer3)

    }


    //GET
    fun hGetDeviceAll(X_Hyber_Session_Id: String, X_Hyber_Auth_Token: String): HyberDataApi {

        try {

            var functionNetAnswer4 = String()
            var functionCodeAnswer4 = 0

            val threadNetF4 = Thread(Runnable {


                try {

                    val currentTimestamp2 =
                        System.currentTimeMillis() // We want timestamp in seconds

                    val authToken = hash("$X_Hyber_Auth_Token:$currentTimestamp2")


                    HyberLoggerSdk.debug("Result: Start step1, Function: hyber_get_device_all, Class: HyberApi, X_Hyber_Session_Id: $X_Hyber_Session_Id, X_Hyber_Auth_Token: $X_Hyber_Auth_Token, currentTimestamp2: $currentTimestamp2, auth_token: $authToken")


                    val mURL2 =
                        URL(PushSdkParameters.branch_current_active.fun_hyber_url_get_device_all)


                    with(mURL2.openConnection() as HttpsURLConnection) {
                        requestMethod = "GET"  // optional default is GET
                        //doOutput = true
                        setRequestProperty("Content-Language", "en-US")
                        setRequestProperty("Content-Type", "application/json")
                        setRequestProperty("X-Hyber-Session-Id", X_Hyber_Session_Id)
                        setRequestProperty("X-Hyber-Timestamp", currentTimestamp2.toString())
                        setRequestProperty("X-Hyber-Auth-Token", authToken)

                        sslSocketFactory = SSLSocketFactory.getDefault() as SSLSocketFactory

                        HyberLoggerSdk.debug("\nSent 'GET' request to URL : $url; Response Code : $responseCode")
                        functionCodeAnswer4 = responseCode

                        //if (responseCode==401) { init_hyber.clearData() }

                        inputStream.bufferedReader().use {

                            functionNetAnswer4 = it.readLine().toString()

                            HyberLoggerSdk.debug("Result: Finish step2, Function: hyber_get_device_all, Class: HyberApi, function_net_answer4: $functionNetAnswer4")
                        }
                    }
                } catch (e: Exception) {
                    HyberLoggerSdk.debug("Result: Failed step3, Function: hyber_get_device_all, Class: HyberApi, exception: $e")


                    functionNetAnswer4 = "Failed"
                }
            })

            threadNetF4.start()
            threadNetF4.join()
            return HyberDataApi(functionCodeAnswer4, functionNetAnswer4, 0)

        } catch (e: Exception) {
            return HyberDataApi(700, "Failed", 0)
        }

    }

    //POST
    fun hDeviceUpdate(
        X_Hyber_Auth_Token: String,
        X_Hyber_Session_Id: String,
        device_Name: String,
        device_Type: String,
        os_Type: String,
        sdk_Version: String,
        fcm_Token: String
    ): HyberDataApi {

        var functionNetAnswer5 = String()
        var functionCodeAnswer5 = 0

        val threadNetF5 = Thread(Runnable {

            try {
                val message =
                    "{\"fcmToken\": \"$fcm_Token\",\"osType\": \"$os_Type\",\"osVersion\": \"$osVersion\",\"deviceType\": \"$device_Type\",\"deviceName\": \"$device_Name\",\"sdkVersion\": \"$sdk_Version\" }"

                HyberLoggerSdk.debug(message)

                val currentTimestamp2 = System.currentTimeMillis() // We want timestamp in seconds

                val authToken = hash("$X_Hyber_Auth_Token:$currentTimestamp2")

                val postData: ByteArray = message.toByteArray(Charset.forName("UTF-8"))

                val mURL = URL(PushSdkParameters.branch_current_active.fun_hyber_url_device_update)

                val connectorWebPlatform = mURL.openConnection() as HttpsURLConnection
                connectorWebPlatform.doOutput = true
                connectorWebPlatform.setRequestProperty("Content-Language", "en-US")
                connectorWebPlatform.setRequestProperty("Content-Type", "application/json")
                connectorWebPlatform.setRequestProperty("X-Hyber-Session-Id", X_Hyber_Session_Id)
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
                HyberLoggerSdk.debug("Result: Failed step5, Function: hyber_device_register, Class: HyberApi, exception: ${e.stackTrace}")

                functionNetAnswer5 = "Failed"
            }


        })

        threadNetF5.start()
        threadNetF5.join()

        return HyberDataApi(functionCodeAnswer5, functionNetAnswer5, 0)


    }

    //POST
    fun hMessageCallback(
        message_id: String,
        hyber_answer: String,
        X_Hyber_Session_Id: String,
        X_Hyber_Auth_Token: String
    ): HyberDataApi {

        var functionNetAnswer6 = String()
        var functionCodeAnswer6 = 0

        val threadNetF6 = Thread(Runnable {

            try {
                val message2 = "{\"messageId\": \"$message_id\", \"answer\": \"$hyber_answer\"}"
                HyberLoggerSdk.debug("Body message to hyber : $message2")
                val currentTimestamp2 = System.currentTimeMillis() // We want timestamp in seconds

                val authToken = hash("$X_Hyber_Auth_Token:$currentTimestamp2")


                val postData2: ByteArray = message2.toByteArray(Charset.forName("UTF-8"))

                val mURL2 =
                    URL(PushSdkParameters.branch_current_active.fun_hyber_url_message_callback)

                val connectorWebPlatform = mURL2.openConnection() as HttpsURLConnection
                connectorWebPlatform.doOutput = true
                connectorWebPlatform.setRequestProperty("Content-Language", "en-US")
                connectorWebPlatform.setRequestProperty("Content-Type", "application/json")
                connectorWebPlatform.setRequestProperty("X-Hyber-Session-Id", X_Hyber_Session_Id)
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
                    HyberLoggerSdk.debug("URL : $url")
                    HyberLoggerSdk.debug("Response Code : $responseCode")
                    functionCodeAnswer6 = responseCode
                    BufferedReader(InputStreamReader(inputStream)).use {
                        val response = StringBuffer()

                        var inputLine = it.readLine()
                        while (inputLine != null) {
                            response.append(inputLine)
                            inputLine = it.readLine()
                        }
                        it.close()
                        HyberLoggerSdk.debug("Response : $response")

                        functionNetAnswer6 = response.toString()
                    }
                }

            } catch (e: Exception) {
                HyberLoggerSdk.debug("Result: Failed step5, Function: hyber_device_register, Class: HyberApi, exception: ${e.stackTrace}")
            }
        })

        threadNetF6.start()
        threadNetF6.join()
        return HyberDataApi(functionCodeAnswer6, functionNetAnswer6, 0)


    }

    //POST
    fun hMessageDr(
        message_id: String,
        X_Hyber_Session_Id: String,
        X_Hyber_Auth_Token: String
    ): HyberDataApi {

        if (X_Hyber_Session_Id != "" && X_Hyber_Auth_Token != "" && message_id != "") {

            var functionNetAnswer7 = String()

            val threadNetF7 = Thread(Runnable {

                try {
                    val message2 = "{\"messageId\": \"$message_id\"}"

                    HyberLoggerSdk.debug("Body message to hyber : $message2")
                    val currentTimestamp2 =
                        System.currentTimeMillis() // We want timestamp in seconds

                    HyberLoggerSdk.debug("Timestamp : $currentTimestamp2")

                    val authToken = hash("$X_Hyber_Auth_Token:$currentTimestamp2")

                    val postData2: ByteArray = message2.toByteArray(Charset.forName("UTF-8"))

                    val mURL2 =
                        URL(PushSdkParameters.branch_current_active.fun_hyber_url_message_dr)

                    val connectorWebPlatform = mURL2.openConnection() as HttpsURLConnection
                    connectorWebPlatform.doOutput = true
                    connectorWebPlatform.setRequestProperty("Content-Language", "en-US")
                    connectorWebPlatform.setRequestProperty("Content-Type", "application/json")
                    connectorWebPlatform.setRequestProperty(
                        "X-Hyber-Session-Id",
                        X_Hyber_Session_Id
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
                        HyberLoggerSdk.debug("URL : $url")
                        HyberLoggerSdk.debug("Response Code : $responseCode")

                        BufferedReader(InputStreamReader(inputStream)).use {
                            val response = StringBuffer()

                            var inputLine = it.readLine()
                            while (inputLine != null) {
                                response.append(inputLine)
                                inputLine = it.readLine()
                            }
                            it.close()
                            HyberLoggerSdk.debug("Response : $response")
                        }
                        functionNetAnswer7 = responseCode.toString()
                    }
                } catch (e: Exception) {
                    functionNetAnswer7 = "500"
                }
            })
            threadNetF7.start()
            threadNetF7.join()
            return HyberDataApi(functionNetAnswer7.toInt(), "{}", 0)
        } else {
            return HyberDataApi(700, "{}", 0)
        }
    }
}