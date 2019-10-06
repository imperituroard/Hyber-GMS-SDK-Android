package com.sk.android.sksdkandroid.core

import android.content.Context
import java.util.*

internal class Protection(val context: Context) {
    private val sharedPreference: SharedPreference = SharedPreference(context)

    fun check_registration() {
        var uuid = sharedPreference.getValueString("registerstatus")
        if (uuid == "") {
            uuid = UUID.randomUUID().toString()
            sharedPreference.save("uuid", uuid)
        }
    }
    // sharedPreference.save("ddd","sss")
}