package com.hyber.android.hybersdkandroid.logger

import android.os.Debug
import android.util.Log
import com.hyber.android.hybersdkandroid.core.PushSdkParametersPublic
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*

internal object HyberLoggerSdk {

    fun error(message: String){
        val formatDate = SimpleDateFormat.getDateTimeInstance().format(Calendar.getInstance().time)
        Log.e(PushSdkParametersPublic.TAG_LOGGING, "$formatDate $message")
    }

    fun debug(message: String){
        val formatDate = SimpleDateFormat.getDateTimeInstance().format(Calendar.getInstance().time)
        Log.d(PushSdkParametersPublic.TAG_LOGGING, "$formatDate $message")
    }

}