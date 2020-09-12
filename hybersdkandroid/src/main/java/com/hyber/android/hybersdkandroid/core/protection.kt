package com.hyber.android.hybersdkandroid.core

import android.content.Context
import java.util.*

internal class Protection(val context: Context) {
    private val sharedPreference: SharedPreference = SharedPreference(context)

    fun checkRegistration() {
        var uuid = sharedPreference.getValueString("registerstatus")
        if (uuid == "") {
            uuid = UUID.randomUUID().toString()
            sharedPreference.save("uuid", uuid)
        }
    }
    // sharedPreference.save("ddd","sss")
}