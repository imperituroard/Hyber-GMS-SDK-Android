package com.hyber.android.hybersdkandroid

import android.content.Context
import android.content.Intent
import com.hyber.android.hybersdkandroid.core.HyberApi
import com.hyber.android.hybersdkandroid.core.HyberDataApi
import com.hyber.android.hybersdkandroid.core.PushSdkParameters
import com.hyber.android.hybersdkandroid.logger.HyberLoggerSdk
import kotlinx.serialization.Optional
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JSON
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.net.URL
import java.nio.charset.Charset
import java.security.MessageDigest
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLSocketFactory

internal class QueueProc {

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

    private fun processHyberQueue(
        queue: String,
        X_Hyber_Session_Id: String,
        X_Hyber_Auth_Token: String
    ) {
        val apiHyber = HyberApi()

        @Serializable
        data class Empty(
            @Optional
            val url: String? = null,
            @Optional
            val button: String? = null,
            @Optional
            val text: String? = null,
            @Optional
            val messageId: String? = null
        )

        @Serializable
        data class QueryList(
            @SerialName("phone")
            val phone: String,
            @SerialName("messageId")
            val messageId: String,
            @SerialName("title")
            val title: String,
            @SerialName("body")
            val body: String,
            @SerialName("image")
            val image: Empty?,
            @SerialName("button")
            val button: Empty?,
            @SerialName("partner")
            val partner: String,
            @SerialName("time")
            val time: String
        )

        @Serializable
        data class ParentQu(
            @Optional
            val messages: List<QueryList>
        )
        val parent = JSON.parse(ParentQu.serializer(), queue)
        if (parent.messages.isNotEmpty()) {
            val list = parent.messages
            Thread.sleep(2000)
            //initHyber_params.hyber_init3()
            list.forEach {
                HyberLoggerSdk.debug("fb token: $X_Hyber_Session_Id")
                apiHyber.hMessageDr(it.messageId, X_Hyber_Session_Id, X_Hyber_Auth_Token)
                HyberLoggerSdk.debug("Result: Start step2, Function: processHyberQueue, Class: HyberApi, message: ${it.messageId}")
            }
        }
    }


    internal fun hyberDeviceMessQueue(
        X_Hyber_Session_Id: String,
        X_Hyber_Auth_Token: String,
        context: Context
    ): HyberDataApi {
        var functionNetAnswer2 = String()

        val threadNetF2 = Thread(Runnable {

            val hyberUrlMessQueue: String = PushSdkParameters.branch_current_active.fun_hyber_url_mess_queue

            try {
                HyberLoggerSdk.debug("Result: Start step1, Function: hyber_device_mess_queue, Class: HyberApi, X_Hyber_Session_Id: $X_Hyber_Session_Id, X_Hyber_Auth_Token: $X_Hyber_Auth_Token")

                val message2 = "{}"

                HyberLoggerSdk.debug("Result: Start step2, Function: hyber_device_mess_queue, Class: HyberApi, message2: $message2")

                val currentTimestamp2 = System.currentTimeMillis() // We want timestamp in seconds
                //val date = Date(currentTimestamp * 1000) // Timestamp must be in ms to be converted to Date

                HyberLoggerSdk.debug("QueueProc.hyberDeviceMessQueue \"currentTimestamp2 : $currentTimestamp2\"")

                val authToken = hash("$X_Hyber_Auth_Token:$currentTimestamp2")

                val postData2: ByteArray = message2.toByteArray(Charset.forName("UTF-8"))

                val mURL2 = URL(hyberUrlMessQueue)

                val urlConnectorPlatform = mURL2.openConnection() as HttpsURLConnection
                urlConnectorPlatform.doOutput = true
                urlConnectorPlatform.setRequestProperty("Content-Language", "en-US")
                urlConnectorPlatform.setRequestProperty("Content-Type", "application/json")
                urlConnectorPlatform.setRequestProperty("X-Hyber-Session-Id", X_Hyber_Session_Id)
                urlConnectorPlatform.setRequestProperty("X-Hyber-Timestamp", currentTimestamp2.toString())
                urlConnectorPlatform.setRequestProperty("X-Hyber-Auth-Token", authToken)

                urlConnectorPlatform.sslSocketFactory = SSLSocketFactory.getDefault() as SSLSocketFactory

                with(urlConnectorPlatform) {
                    requestMethod = "POST"
                    doOutput = true
                    val wr = DataOutputStream(outputStream)
                    wr.write(postData2)
                    wr.flush()

                    HyberLoggerSdk.debug("QueueProc.hyberDeviceMessQueue \"URL : $url\"")
                    HyberLoggerSdk.debug("QueueProc.hyberDeviceMessQueue \"Response Code : $responseCode\"")
                    try {
                        BufferedReader(InputStreamReader(inputStream)).use {
                            val response = StringBuffer()
                            var inputLine = it.readLine()
                            while (inputLine != null) {
                                response.append(inputLine)
                                inputLine = it.readLine()
                            }
                            it.close()

                            try {
                                if (response.toString() != """{"messages":[]}""") {
                                    HyberPushMess.message = response.toString()
                                    val intent = Intent()
                                    intent.action = "com.hyber.android.hybersdkandroid.Push"
                                    context.sendBroadcast(intent)
                                }
                            } catch (e: Exception) {
                                HyberPushMess.message = ""
                            }

                            processHyberQueue(
                                response.toString(),
                                X_Hyber_Session_Id,
                                X_Hyber_Auth_Token
                            )
                            HyberLoggerSdk.debug("QueueProc.hyberDeviceMessQueue Response : $response")
                        }
                    } catch (e: Exception) {
                        HyberLoggerSdk.debug("QueueProc.hyberDeviceMessQueue response: unknown Fail")
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

}