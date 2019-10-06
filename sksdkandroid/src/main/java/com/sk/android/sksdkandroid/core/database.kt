package com.sk.android.sksdkandroid.core

import android.content.Context
import android.content.SharedPreferences
import java.lang.Exception

/**
 * Created by Kolincodes on 10/05/2018.
 */

internal class SharedPreference(val context: Context) {
    private val PREFS_NAME = "skdatabase"
    val sharedPref: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun save(KEY_NAME: String, text: String) {

        val editor: SharedPreferences.Editor = sharedPref.edit()

        editor.putString(KEY_NAME, text)

        editor!!.commit()
    }

    fun save(KEY_NAME: String, value: Int) {
        val editor: SharedPreferences.Editor = sharedPref.edit()

        editor.putInt(KEY_NAME, value)

        editor.commit()
    }

    fun save(KEY_NAME: String, status: Boolean) {

        val editor: SharedPreferences.Editor = sharedPref.edit()

        editor.putBoolean(KEY_NAME, status!!)

        editor.commit()
    }

    fun getValueString(KEY_NAME: String): String? {

        try {

            return sharedPref.getString(KEY_NAME, null)
        }catch (e: Exception) {
            return ""
        }

    }

    fun getValueInt(KEY_NAME: String): Int {
        try {

        return sharedPref.getInt(KEY_NAME, 0)
    }catch (e: Exception) {
        return 0
    }
    }

    fun getValueBoolien(KEY_NAME: String, defaultValue: Boolean): Boolean {

        try {

            return sharedPref.getBoolean(KEY_NAME, defaultValue)

    }catch (e: Exception) {
        return false
    }
    }

    fun clearSharedPreference() {

        val editor: SharedPreferences.Editor = sharedPref.edit()

        //sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

        editor.clear()
        editor.commit()
    }

    fun removeValue(KEY_NAME: String) {

        val editor: SharedPreferences.Editor = sharedPref.edit()

        editor.remove(KEY_NAME)
        editor.commit()
    }
}
