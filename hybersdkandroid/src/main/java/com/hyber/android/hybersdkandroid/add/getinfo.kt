package com.hyber.android.hybersdkandroid.add

import android.os.Build
import android.text.TextUtils
import android.content.ContentValues
import android.util.Log
import android.content.Context
import android.content.res.Configuration

internal class GetInfo {

    //var context: Context= Context()
    /** Returns the consumer friendly device name  */
    fun getDeviceName(): String? {
        val manufacturer = Build.MANUFACTURER
        val model = Build.MODEL
        try {
            return if (model.startsWith(manufacturer)) {
                capitalize(model)
            } else capitalize(manufacturer) + " " + model
        } catch (e: Exception) {
            return "unknown"
        }
    }

    fun getAndroidVersion(): String {
        try {
            return Build.VERSION.RELEASE
        } catch (e: java.lang.Exception) {
            return "unknown"
        }
    }

    //get device type (phone or tablet)
    fun getPhoneType(context: Context): String {
        try {
            val flagisTab: Boolean =
                context.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK >= Configuration.SCREENLAYOUT_SIZE_LARGE
            if (flagisTab) {
                Log.d(
                    ContentValues.TAG,
                    "Result: Function: get_phone_type, Class: GetInfo, flagisTab: $flagisTab, answer: tablet"
                )
                return "tablet"
            } else {
                Log.d(
                    ContentValues.TAG,
                    "Result: Function: get_phone_type, Class: GetInfo, flagisTab: $flagisTab, answer: phone"
                )
                return "phone"
            }
        } catch (e: java.lang.Exception) {
            return "unknown"
        }
    }

    //current release android SDK (not module sdk)
    fun getAndroidSdk(): Int {
        return Build.VERSION.SDK_INT
    }

    private fun getImsi() {
        //val phoneMgr = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    }

    private fun capitalize(str: String): String? {
        if (TextUtils.isEmpty(str)) {
            return str
        }
        val arr = str.toCharArray()
        var capitalizeNext = true

        val phrase = StringBuilder()
        for (c in arr) {
            if (capitalizeNext && Character.isLetter(c)) {
                phrase.append(Character.toUpperCase(c))
                capitalizeNext = false
                continue
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true
            }
            phrase.append(c)
        }
        return phrase.toString()
    }


}