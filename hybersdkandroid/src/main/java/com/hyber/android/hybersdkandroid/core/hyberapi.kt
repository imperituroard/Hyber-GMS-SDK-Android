package com.hyber.android.hybersdkandroid.core

import android.content.Context
import com.hyber.android.hybersdkandroid.add.Answer
import java.security.MessageDigest
import java.net.URL
import javax.net.ssl.HttpsURLConnection
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.DataOutputStream
import javax.net.ssl.SSLSocketFactory
import java.nio.charset.Charset
import android.util.Log
import android.content.ContentValues.TAG
import com.hyber.android.hybersdkandroid.add.GetInfo
import java.lang.Exception

//class for communication with hyber rest server (REST API)
internal class HyberApi() {

    //class init for creation answers
    private var answ_form: Answer = Answer()
    private var os_version_class: GetInfo = GetInfo()

    //parameters for procedures
    private val os_version: String = os_version_class.get_android_version()

    //function for create special token for another procedures
    private fun hash(sss: String): String {
        try {
            val bytes = sss.toString().toByteArray()
            val md = MessageDigest.getInstance("SHA-256")
            val digest = md.digest(bytes)
            val resp: String = digest.fold("", { str, it -> str + "%02x".format(it) })
            Log.d(TAG, "Result: OK, Function: hash, Class: HyberApi, input: $sss, output: $resp")
            return resp
        } catch (e: Exception) {
            Log.d(
                TAG,
                "Result: FAILED, Function: hash, Class: HyberApi, input: $sss, output: failed"
            )
            return "failed"
        }
    }

    //POST procedure for new registration
    fun hyber_device_register(
        X_Hyber_Client_API_Key: String,
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
        var function_net_answer: HyberFunAnswerRegister =
            HyberFunAnswerRegister(0, "", "", "", "", "", "", "")
        var function_code_answer: Int = 0

        val thread_net_f1 = Thread(Runnable {
            try {
                Log.d(
                    TAG,
                    "Result: Start step1, Function: hyber_device_register, Class: HyberApi, X_Hyber_Client_API_Key: $X_Hyber_Client_API_Key, X_Hyber_Session_Id: $X_Hyber_Session_Id, X_Hyber_App_Fingerprint: $X_Hyber_App_Fingerprint, device_Name: $device_Name, device_Type: $device_Type, os_Type: $os_Type, sdk_Version: $sdk_Version, user_Pass: $user_Pass, user_Phone: $user_Phone"
                )
                val message =
                    "{\"userPhone\":\"" + user_Phone + "\",\"userPass\":\"" + user_Pass + "\",\"osType\":\"" + os_Type + "\",\"osVersion\":\"" + os_version + "\",\"deviceType\":\"" + device_Type + "\",\"deviceName\":\"" + device_Name + "\",\"sdkVersion\":\"" + sdk_Version + "\"}"
                //val message = "{\"userPhone\":\"$user_Phone\",\"userPass\":\"$user_Pass\",\"osType\":\"$os_Type\",\"osVersion\":\"$os_version\",\"deviceType\":\"$device_Type\",\"deviceName\":\"$device_Name\",\"sdkVersion\":\"$sdk_Version\"}"

                Log.d(
                    TAG,
                    "Result: Start step2, Function: hyber_device_register, Class: HyberApi, message: $message"
                );

                val currentTimestamp = System.currentTimeMillis()
                val postData: ByteArray = message.toByteArray(Charset.forName("UTF-8"))
                val mURL = URL(HyberParameters.hyber_url_registration)
                val urlc = mURL.openConnection() as HttpsURLConnection
                urlc.doOutput = true
                urlc.setRequestProperty("Content-Language", "en-US");
                urlc.setRequestProperty("X-Hyber-Client-API-Key", X_Hyber_Client_API_Key);
                urlc.setRequestProperty("Content-Type", "application/json");
                urlc.setRequestProperty("X-Hyber-Session-Id", X_Hyber_Session_Id);
                urlc.setRequestProperty("X-Hyber-App-Fingerprint", X_Hyber_App_Fingerprint);
                urlc.sslSocketFactory = SSLSocketFactory.getDefault() as SSLSocketFactory;

                with(urlc as HttpsURLConnection) {
                    // optional default is GET
                    requestMethod = "POST"
                    doOutput = true
                    //doInput = true
                    //useCaches = false
                    //setRequestProperty("","")
                    Log.d(TAG, "URL : $url")

                    Log.d(TAG, "start DataOutputStream")
                    val wr: DataOutputStream = DataOutputStream(outputStream)
                    Log.d(TAG, "start write")

                    wr.write(postData)

                    Log.d(TAG, "end write")
                    wr.flush();
                    Log.d(
                        TAG,
                        "Result: Finished step3, Function: hyber_device_register, Class: HyberApi, Response Code : ${responseCode.toString()}"
                    )
                    function_code_answer = responseCode;
                    if (responseCode == 200) {

                        BufferedReader(InputStreamReader(inputStream)).use {
                            val response = StringBuffer()

                            var inputLine = it.readLine()
                            while (inputLine != null) {
                                response.append(inputLine)
                                inputLine = it.readLine()
                            }
                            it.close()
                            Log.d(
                                TAG,
                                "Result: Finished step4, Function: hyber_device_register, Class: HyberApi, Response : ${response.toString()}"
                            )
                            function_net_answer = answ_form.register_procedure_answer2(
                                responseCode.toString(),
                                response.toString(),
                                context
                            )
                        }
                    } else {
                        function_net_answer = answ_form.register_procedure_answer2(
                            responseCode.toString(),
                            "unknown",
                            context
                        )

                    }
                }
            } catch (e: Exception) {

                Log.d(
                    TAG,
                    "Result: Failed step5, Function: hyber_device_register, Class: HyberApi, exception: ${e.stackTrace.toString()}"
                )
                function_net_answer = answ_form.register_procedure_answer2(
                    "705",
                    "unknown",
                    context
                )
            }
        })

        thread_net_f1.start()
        thread_net_f1.join()

        return HyberDataApi2(function_net_answer.code, function_net_answer, 0)
    }

    //POST
    fun hyber_device_revoke(
        dev_list: String,
        X_Hyber_Session_Id: String,
        X_Hyber_Auth_Token: String
    ): HyberDataApi {

        var function_net_answer2: String = String()

        val thread_net_f2 = Thread(Runnable {

            try {

                Log.d(
                    TAG,
                    "Result: Start step1, Function: hyber_device_revoke, Class: HyberApi, dev_list: $dev_list, X_Hyber_Session_Id: $X_Hyber_Session_Id, X_Hyber_Auth_Token: $X_Hyber_Auth_Token"
                )

                //val stringBuilder = StringBuilder("{\"name\":\"Cedric Beust\", \"age\":23}")
                //val message = "{\"name\":\"Cedric Beust\", \"age\":23}"
                val message2 = "{\"devices\":" + dev_list.toString() + "}"

                Log.d(
                    TAG,
                    "Result: Start step2, Function: hyber_device_revoke, Class: HyberApi, message2: $message2"
                )

                val currentTimestamp2 = System.currentTimeMillis() // We want timestamp in seconds
                //val date = Date(currentTimestamp * 1000) // Timestamp must be in ms to be converted to Date

                println(currentTimestamp2)
                //println(date)

                val auth_token = hash(X_Hyber_Auth_Token + ":" + currentTimestamp2.toString())


                val postData2: ByteArray = message2.toByteArray(Charset.forName("UTF-8"))

                //println(stringBuilder)

                val mURL2 = URL(HyberParameters.hyber_url_revoke)

                val urlc2 = mURL2.openConnection() as HttpsURLConnection
                urlc2.doOutput = true
                urlc2.setRequestProperty("Content-Language", "en-US");
                //urlc.setRequestProperty("X-Hyber-Client-API-Key", "test");
                urlc2.setRequestProperty("Content-Type", "application/json");
                urlc2.setRequestProperty("X-Hyber-Session-Id", X_Hyber_Session_Id);
                //urlc.setRequestProperty("X-Hyber-App-Fingerprint", "1");
                urlc2.setRequestProperty("X-Hyber-Timestamp", currentTimestamp2.toString());
                urlc2.setRequestProperty("X-Hyber-Auth-Token", auth_token);

                urlc2.sslSocketFactory = SSLSocketFactory.getDefault() as SSLSocketFactory;


                with(urlc2 as HttpsURLConnection) {
                    // optional default is GET
                    requestMethod = "POST"
                    doOutput = true
                    //doInput = true
                    //useCaches = false
                    //setRequestProperty("","")

                    //val wr = OutputStreamWriter(getOutputStream());

                    val wr: DataOutputStream = DataOutputStream(outputStream)

                    println("URL3 : $url")
                    wr.write(postData2);

                    println("URL2 : $url")

                    wr.flush();

                    Log.d(TAG, "URL : $url");

                    Log.d(TAG, "Response Code : $responseCode");

                    try {
                        BufferedReader(InputStreamReader(inputStream)).use {
                            val response = StringBuffer()

                            var inputLine = it.readLine()
                            while (inputLine != null) {
                                response.append(inputLine)
                                inputLine = it.readLine()
                            }
                            it.close()
                            println("Response : $response")
                        }
                    } catch (e: Exception) {
                        println("Failed")
                    }

                    function_net_answer2 = responseCode.toString()
                }
            } catch (e: Exception) {
                function_net_answer2 = "500"
            }
        })

        thread_net_f2.start()
        thread_net_f2.join()

        return HyberDataApi(function_net_answer2.toInt(), "{}", 0)
    }

    //GET
    fun hyber_get_message_history(
        X_Hyber_Session_Id: String,
        X_Hyber_Auth_Token: String,
        period_in_seconds: Int
    ): HyberFunAnswerGeneral {

        var function_net_answer3: String = String()
        var final_answer: HyberFunAnswerGeneral
        var function_code_answer3: Int = 0

        val thread_net_f3 = Thread(Runnable {
            try {

                val currentTimestamp2 =
                    System.currentTimeMillis() - period_in_seconds // We want timestamp in seconds
                //val date = Date(currentTimestamp * 1000) // Timestamp must be in ms to be converted to Date

                println(currentTimestamp2)
                //println(date)

                val auth_token = hash(X_Hyber_Auth_Token + ":" + currentTimestamp2.toString())

                Log.d(
                    TAG,
                    "\nSent 'GET' request to hyber_get_device_all with : X_Hyber_Session_Id : $X_Hyber_Session_Id; X_Hyber_Auth_Token : $X_Hyber_Auth_Token; period_in_seconds : ${period_in_seconds.toString()}"
                );


                val mURL2 =
                    URL(HyberParameters.hyber_url_message_history(currentTimestamp2.toString()))

                //val urlc2 = mURL2.openConnection() as HttpsURLConnection

                with(mURL2.openConnection() as HttpsURLConnection) {
                    requestMethod = "GET"  // optional default is GET

                    //doOutput = true
                    setRequestProperty("Content-Language", "en-US");
                    //urlc.setRequestProperty("X-Hyber-Client-API-Key", "test");
                    setRequestProperty("Content-Type", "application/json");
                    setRequestProperty("X-Hyber-Session-Id", X_Hyber_Session_Id);
                    //urlc.setRequestProperty("X-Hyber-App-Fingerprint", "1");
                    setRequestProperty("X-Hyber-Timestamp", currentTimestamp2.toString());
                    setRequestProperty("X-Hyber-Auth-Token", auth_token);

                    sslSocketFactory = SSLSocketFactory.getDefault() as SSLSocketFactory;

                    requestMethod = "GET"

                    Log.d(TAG, "\nSent 'GET' request to URL : $url; Response Code : $responseCode");
                    function_code_answer3 = responseCode

                    inputStream.bufferedReader().use {

                        //println(it.readLine())
                        function_net_answer3 = it.readLine().toString()

                    }
                }
            } catch (e: Exception) {

                Log.d(
                    TAG,
                    "Result: Failed step5, Function: hyber_device_register, Class: HyberApi, exception: ${e.stackTrace.toString()}"
                )
                function_code_answer3 = 700
                function_net_answer3 = "Failed"

                //final_answer = HyberFunAnswerGeneral(700, "Failed", "Rest Api thread exception", "{}")
            }

        })

        thread_net_f3.start()
        thread_net_f3.join()
        return HyberFunAnswerGeneral(function_code_answer3, "OK", "Processed", function_net_answer3)

    }


    //GET
    fun hyber_get_device_all(X_Hyber_Session_Id: String, X_Hyber_Auth_Token: String): HyberDataApi {

        try {

            var function_net_answer4: String = String()
            var function_code_answer4: Int = 0

            val thread_net_f4 = Thread(Runnable {


                try {

                    val currentTimestamp2 =
                        System.currentTimeMillis() // We want timestamp in seconds
                    //val date = Date(currentTimestamp * 1000) // Timestamp must be in ms to be converted to Date

                    println(currentTimestamp2)
                    //println(date)

                    val auth_token = hash(X_Hyber_Auth_Token + ":" + currentTimestamp2.toString())

                    Log.d(
                        TAG,
                        "Result: Start step1, Function: hyber_get_device_all, Class: HyberApi, X_Hyber_Session_Id: $X_Hyber_Session_Id, X_Hyber_Auth_Token: $X_Hyber_Auth_Token, currentTimestamp2: $currentTimestamp2, auth_token: $auth_token"
                    )

                    val mURL2 = URL(HyberParameters.hyber_url_getdeviceall)

                    //val urlc2 = mURL2.openConnection() as HttpsURLConnection


                    with(mURL2.openConnection() as HttpsURLConnection) {
                        requestMethod = "GET"  // optional default is GET
                        //doOutput = true
                        setRequestProperty("Content-Language", "en-US");
                        //urlc.setRequestProperty("X-Hyber-Client-API-Key", "test");
                        setRequestProperty("Content-Type", "application/json");
                        setRequestProperty("X-Hyber-Session-Id", X_Hyber_Session_Id);
                        //urlc.setRequestProperty("X-Hyber-App-Fingerprint", "1");
                        setRequestProperty("X-Hyber-Timestamp", currentTimestamp2.toString());
                        setRequestProperty("X-Hyber-Auth-Token", auth_token);

                        sslSocketFactory = SSLSocketFactory.getDefault() as SSLSocketFactory;

                        //requestMethod = "GET"

                        Log.d(
                            TAG,
                            "\nSent 'GET' request to URL : $url; Response Code : $responseCode"
                        );
                        function_code_answer4 = responseCode;

                        //if (responseCode==401) { init_hyber.clearData() }

                        inputStream.bufferedReader().use {

                            //println(it.readLine())
                            function_net_answer4 = it.readLine().toString()

                            Log.d(
                                TAG,
                                "Result: Finish step2, Function: hyber_get_device_all, Class: HyberApi, function_net_answer4: $function_net_answer4"
                            )
                        }
                    }
                } catch (e: Exception) {
                    Log.d(
                        TAG,
                        "Result: Failed step3, Function: hyber_get_device_all, Class: HyberApi, exception: $e"
                    )
                    function_net_answer4 = "Failed"
                }
            })

            thread_net_f4.start()
            thread_net_f4.join()
            return HyberDataApi(function_code_answer4, function_net_answer4, 0)

        } catch (e: Exception) {
            return HyberDataApi(700, "Failed", 0)
        }

    }

    //POST
    fun hyber_device_update(
        X_Hyber_Auth_Token: String,
        X_Hyber_Session_Id: String,
        device_Name: String,
        device_Type: String,
        os_Type: String,
        sdk_Version: String,
        fcm_Token: String
    ): HyberDataApi {

        var function_net_answer5: String = String()
        var function_code_answer5: Int = 0

        val thread_net_f5 = Thread(Runnable {

            try {
                val message =
                    "{\"fcmToken\": \"" + fcm_Token + "\",\"osType\": \"" + os_Type + "\",\"osVersion\": \"$os_version\",\"deviceType\": \"" + device_Type + "\",\"deviceName\": \"" + device_Name + "\",\"sdkVersion\": \"" + sdk_Version + "\" }"
                println(message)


                val currentTimestamp = System.currentTimeMillis()

                val currentTimestamp2 = System.currentTimeMillis() // We want timestamp in seconds
                //val date = Date(currentTimestamp * 1000) // Timestamp must be in ms to be converted to Date

                println(currentTimestamp2)
                println(X_Hyber_Session_Id)
                //println(date)

                val auth_token = hash(X_Hyber_Auth_Token + ":" + currentTimestamp2.toString())

                println(auth_token)

                val postData: ByteArray = message.toByteArray(Charset.forName("UTF-8"))

                val mURL = URL(HyberParameters.hyber_url_device_update)

                val urlc = mURL.openConnection() as HttpsURLConnection
                urlc.doOutput = true
                urlc.setRequestProperty("Content-Language", "en-US");
                //urlc.setRequestProperty("X-Hyber-Client-API-Key", X_Hyber_Client_API_Key);
                urlc.setRequestProperty("Content-Type", "application/json");
                urlc.setRequestProperty("X-Hyber-Session-Id", X_Hyber_Session_Id);
                //urlc.setRequestProperty("X-Hyber-App-Fingerprint", X_Hyber_App_Fingerprint);
                urlc.setRequestProperty("X-Hyber-Auth-Token", auth_token);
                urlc.setRequestProperty("X-Hyber-Timestamp", currentTimestamp2.toString());
                urlc.sslSocketFactory = SSLSocketFactory.getDefault() as SSLSocketFactory;

                with(urlc as HttpsURLConnection) {
                    // optional default is GET
                    requestMethod = "POST"
                    doOutput = true
                    //doInput = true
                    //useCaches = false
                    //setRequestProperty("","")


                    println("URL : $url")

                    //val wr = OutputStreamWriter(getOutputStream());

                    val wr: DataOutputStream = DataOutputStream(outputStream)

                    //println("URL3 : $url")
                    wr.write(postData);

                    //println("URL2 : $url")

                    wr.flush();

                    //println("URL : $url")
                    //println("Response Code : $responseCode")
                    function_code_answer5 = responseCode;

                    //if (responseCode==401) { init_hyber.clearData() }

                    BufferedReader(InputStreamReader(inputStream)).use {
                        val response = StringBuffer()

                        var inputLine = it.readLine()
                        while (inputLine != null) {
                            response.append(inputLine)
                            inputLine = it.readLine()
                        }
                        it.close()
                        //println("Response : $response")
                        function_net_answer5 = response.toString()
                    }
                }

            } catch (e: Exception) {

                Log.d(
                    TAG,
                    "Result: Failed step5, Function: hyber_device_register, Class: HyberApi, exception: ${e.stackTrace.toString()}"
                )
                function_net_answer5 = "Failed"
            }


        })

        thread_net_f5.start()
        thread_net_f5.join()

        return HyberDataApi(function_code_answer5, function_net_answer5, 0)


    }

    //POST
    fun hyber_message_callback(
        message_id: String,
        hyber_answer: String,
        X_Hyber_Session_Id: String,
        X_Hyber_Auth_Token: String
    ): HyberDataApi {


        var function_net_answer6: String = String()
        var function_code_answer6: Int = 0

        val thread_net_f6 = Thread(Runnable {

            try {

                //val stringBuilder = StringBuilder("{\"name\":\"Cedric Beust\", \"age\":23}")
                //val message = "{\"name\":\"Cedric Beust\", \"age\":23}"
                val message2 = "{\"messageId\": \"$message_id\", \"answer\": \"$hyber_answer\"}"

                Log.d(TAG, "Body message to hyber : $message2");

                val currentTimestamp2 = System.currentTimeMillis() // We want timestamp in seconds
                //val date = Date(currentTimestamp * 1000) // Timestamp must be in ms to be converted to Date

                println(currentTimestamp2)
                //println(date)

                val auth_token = hash(X_Hyber_Auth_Token + ":" + currentTimestamp2.toString())


                val postData2: ByteArray = message2.toByteArray(Charset.forName("UTF-8"))

                //println(stringBuilder)

                val mURL2 = URL(HyberParameters.hyber_url_message_callback)

                val urlc2 = mURL2.openConnection() as HttpsURLConnection
                urlc2.doOutput = true
                urlc2.setRequestProperty("Content-Language", "en-US");
                //urlc.setRequestProperty("X-Hyber-Client-API-Key", "test");
                urlc2.setRequestProperty("Content-Type", "application/json");
                urlc2.setRequestProperty("X-Hyber-Session-Id", X_Hyber_Session_Id);
                //urlc.setRequestProperty("X-Hyber-App-Fingerprint", "1");
                urlc2.setRequestProperty("X-Hyber-Timestamp", currentTimestamp2.toString());
                urlc2.setRequestProperty("X-Hyber-Auth-Token", auth_token);

                urlc2.sslSocketFactory = SSLSocketFactory.getDefault() as SSLSocketFactory;


                with(urlc2 as HttpsURLConnection) {
                    // optional default is GET
                    requestMethod = "POST"
                    doOutput = true
                    //doInput = true
                    //useCaches = false
                    //setRequestProperty("","")

                    //val wr = OutputStreamWriter(getOutputStream());

                    val wr: DataOutputStream = DataOutputStream(outputStream)

                    println("URL3 : $url")
                    wr.write(postData2);
                    println("URL2 : $url")
                    wr.flush();
                    Log.d(TAG, "URL : $url");
                    Log.d(TAG, "Response Code : $responseCode");
                    function_code_answer6 = responseCode;
                    // if (responseCode==401) { init_hyber.clearData() }

                    BufferedReader(InputStreamReader(inputStream)).use {
                        val response = StringBuffer()

                        var inputLine = it.readLine()
                        while (inputLine != null) {
                            response.append(inputLine)
                            inputLine = it.readLine()
                        }
                        it.close()
                        println("Response : $response")
                        //function_net_answer6 = responseCode.toString() + " " + "Response : $response"
                        function_net_answer6 = response.toString()
                    }
                }

            } catch (e: Exception) {

                Log.d(
                    TAG,
                    "Result: Failed step5, Function: hyber_device_register, Class: HyberApi, exception: ${e.stackTrace.toString()}"
                )

            }
        })

        thread_net_f6.start()
        thread_net_f6.join()
        return HyberDataApi(function_code_answer6, function_net_answer6, 0)


    }

    //POST
    fun hyber_message_dr(
        message_id: String,
        X_Hyber_Session_Id: String,
        X_Hyber_Auth_Token: String
    ): HyberDataApi {

        if (X_Hyber_Session_Id != "" && X_Hyber_Auth_Token != "" && message_id != "") {

            var function_net_answer7: String = String()

            val thread_net_f7 = Thread(Runnable {

                try {
                    //val message2 = StringBuilder("{\"messageId\": \"$message_id\"}")
                    val message2 = "{\"messageId\": \"$message_id\"}"

                    Log.d(TAG, "Body message to hyber : $message2");

                    val currentTimestamp2 =
                        System.currentTimeMillis() // We want timestamp in seconds
                    //val date = Date(currentTimestamp * 1000) // Timestamp must be in ms to be converted to Date

                    Log.d(TAG, "Timestamp : $currentTimestamp2");

                    val auth_token = hash(X_Hyber_Auth_Token + ":" + currentTimestamp2.toString())


                    val postData2: ByteArray = message2.toByteArray(Charset.forName("UTF-8"))

                    val mURL2 = URL(HyberParameters.hyber_url_message_dr)

                    val urlc2 = mURL2.openConnection() as HttpsURLConnection
                    urlc2.doOutput = true
                    urlc2.setRequestProperty("Content-Language", "en-US");
                    //urlc.setRequestProperty("X-Hyber-Client-API-Key", "test");
                    urlc2.setRequestProperty("Content-Type", "application/json");
                    urlc2.setRequestProperty("X-Hyber-Session-Id", X_Hyber_Session_Id);
                    //urlc.setRequestProperty("X-Hyber-App-Fingerprint", "1");
                    urlc2.setRequestProperty("X-Hyber-Timestamp", currentTimestamp2.toString());
                    urlc2.setRequestProperty("X-Hyber-Auth-Token", auth_token);

                    urlc2.sslSocketFactory = SSLSocketFactory.getDefault() as SSLSocketFactory;


                    with(urlc2 as HttpsURLConnection) {
                        // optional default is GET
                        requestMethod = "POST"
                        doOutput = true
                        //doInput = true
                        //useCaches = false
                        //setRequestProperty("","")

                        //val wr = OutputStreamWriter(getOutputStream());

                        val wr: DataOutputStream = DataOutputStream(outputStream)
                        wr.write(postData2);
                        wr.flush();
                        Log.d(TAG, "URL : $url");
                        Log.d(TAG, "Response Code : $responseCode");
                        //if (responseCode==401) { init_hyber.clearData() }

                        BufferedReader(InputStreamReader(inputStream)).use {
                            val response = StringBuffer()

                            var inputLine = it.readLine()
                            while (inputLine != null) {
                                response.append(inputLine)
                                inputLine = it.readLine()
                            }
                            it.close()
                            Log.d(TAG, "Response : $response");
                        }
                        function_net_answer7 = responseCode.toString()
                    }
                } catch (e: Exception) {
                    function_net_answer7 = "500"
                }
            })
            thread_net_f7.start()
            thread_net_f7.join()
            return HyberDataApi(function_net_answer7.toInt(), "{}", 0)
        } else {
            return HyberDataApi(700, "{}", 0)
        }
    }
}