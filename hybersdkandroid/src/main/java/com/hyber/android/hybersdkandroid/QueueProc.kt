package com.hyber.android.hybersdkandroid

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.util.Log
import com.hyber.android.hybersdkandroid.core.HyberApi
import com.hyber.android.hybersdkandroid.core.HyberDataApi
import com.hyber.android.hybersdkandroid.core.PushSdkParameters
import com.hyber.android.hybersdkandroid.core.Initialization
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
        try {
            val bytes = sss.toByteArray()
            val md = MessageDigest.getInstance("SHA-256")
            val digest = md.digest(bytes)
            val resp: String = digest.fold("", { str, it -> str + "%02x".format(it) })
            Log.d(
                ContentValues.TAG,
                "Result: OK, Function: hash, Class: HyberApi, input: $sss, output: $resp"
            )
            return resp
        } catch (e: Exception) {
            Log.d(
                ContentValues.TAG,
                "Result: FAILED, Function: hash, Class: HyberApi, input: $sss, output: failed"
            )
            return "failed"
        }
    }

    private fun processHyberQueue(
        queue: String,
        X_Hyber_Session_Id: String,
        X_Hyber_Auth_Token: String,
        context: Context
    ) {
        val apiHyber = HyberApi()
        val initHyberParams = Initialization(context)

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
        data class Qulist(
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
            val messages: List<Qulist>
        )

        val parent = JSON.parse(ParentQu.serializer(), queue)

        if (parent.messages.isNotEmpty()) {
            val list = parent.messages
            Thread.sleep(2000)
            //inithyber_params.hyber_init3()
            list.forEach {
                println("fb token: ${PushSdkParameters.firebase_registration_token}")
                apiHyber.hMessageDr(it.messageId, X_Hyber_Session_Id, X_Hyber_Auth_Token)
                println(it.messageId)
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

                Log.d(
                    ContentValues.TAG,
                    "Result: Start step1, Function: hyber_device_mess_queue, Class: HyberApi, X_Hyber_Session_Id: $X_Hyber_Session_Id, X_Hyber_Auth_Token: $X_Hyber_Auth_Token"
                )

                //val stringBuilder = StringBuilder("{\"name\":\"Cedric Beust\", \"age\":23}")
                //val message = "{\"name\":\"Cedric Beust\", \"age\":23}"
                val message2 = "{}"

                Log.d(
                    ContentValues.TAG,
                    "Result: Start step2, Function: hyber_device_mess_queue, Class: HyberApi, message2: $message2"
                )

                val currentTimestamp2 = System.currentTimeMillis() // We want timestamp in seconds
                //val date = Date(currentTimestamp * 1000) // Timestamp must be in ms to be converted to Date

                println(currentTimestamp2)
                //println(date)
                //currentTimestamp2 = 1

                //println(currentTimestamp2)

                val auth_token = hash("$X_Hyber_Auth_Token:$currentTimestamp2")

                val postData2: ByteArray = message2.toByteArray(Charset.forName("UTF-8"))

                //println(stringBuilder)

                val mURL2 = URL(hyberUrlMessQueue)

                val urlc2 = mURL2.openConnection() as HttpsURLConnection
                urlc2.doOutput = true
                urlc2.setRequestProperty("Content-Language", "en-US")
                //urlc.setRequestProperty("X-Hyber-Client-API-Key", "test");
                urlc2.setRequestProperty("Content-Type", "application/json")
                urlc2.setRequestProperty("X-Hyber-Session-Id", X_Hyber_Session_Id)
                urlc2.setRequestProperty("X-Hyber-Timestamp", currentTimestamp2.toString())
                urlc2.setRequestProperty("X-Hyber-Auth-Token", auth_token)

                urlc2.sslSocketFactory = SSLSocketFactory.getDefault() as SSLSocketFactory

                with(urlc2) {
                    // optional default is GET
                    requestMethod = "POST"
                    doOutput = true
                    //doInput = true
                    //useCaches = false
                    //setRequestProperty("","")

                    //val wr = OutputStreamWriter(getOutputStream());

                    val wr = DataOutputStream(outputStream)

                    println("URL3 : $url")
                    wr.write(postData2)

                    println("URL2 : $url")

                    wr.flush()

                    Log.d(ContentValues.TAG, "URL : $url")

                    Log.d(ContentValues.TAG, "Response Code : $responseCode")

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
                                    //intent.putExtra("Data", 1000)
                                    //intent.flags = Intent.FLAG_INCLUDE_STOPPED_PACKAGES
                                    context.sendBroadcast(intent)
                                }
                            } catch (e: Exception) {
                                HyberPushMess.message = ""
                            }

                            processHyberQueue(
                                response.toString(),
                                X_Hyber_Session_Id,
                                X_Hyber_Auth_Token,
                                context
                            )

                            println("Response : $response")
                            //function_net_answer6 = responseCode.toString() + " " + "Response : $response"
                            //function_net_answer6 = response.toString()
                        }
                    } catch (e: Exception) {
                        println("Failed")
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