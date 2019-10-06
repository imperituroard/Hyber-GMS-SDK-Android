package com.sk.android.sksdkandroid.core

import android.content.Context

internal class SpAccess(val context: Context) {
    private val sharedPreference:SharedPreference=SharedPreference(context)

    fun saveData(data: String, keyy: String){
        sharedPreference.save(keyy,data)
        sharedPreference.save(keyy,data)
        //Toast.makeText(this@MainActivity,"Data Stored",Toast.LENGTH_SHORT).show()
    }

}

