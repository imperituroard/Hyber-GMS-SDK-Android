package com.hyber.android.hybersdkandroid.core

import android.content.Context
import android.content.SharedPreferences
import java.lang.Exception


@Suppress("unused")
internal class SharedPreference(val context: Context) {
    private val preferenceDatabase = "hyberdatabase"
    private val sharedPref: SharedPreferences =
        context.getSharedPreferences(preferenceDatabase, Context.MODE_PRIVATE)

    fun save(KEY_NAME: String, text: String) {
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.putString(KEY_NAME, text)
        editor.apply()
    }

    fun save(KEY_NAME: String, value: Int) {
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.putInt(KEY_NAME, value)
        editor.apply()
    }

    fun save(KEY_NAME: String, status: Boolean) {
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.putBoolean(KEY_NAME, status)
        editor.apply()
    }

    fun getValueString(KEY_NAME: String): String? {
        return try {
            sharedPref.getString(KEY_NAME, null)
        } catch (e: Exception) {
            ""
        }
    }

    fun getValueInt(KEY_NAME: String): Int {
        return try {
            sharedPref.getInt(KEY_NAME, 0)
        } catch (e: Exception) {
            0
        }
    }

    fun getValueBool(KEY_NAME: String, defaultValue: Boolean): Boolean {
        return try {
            sharedPref.getBoolean(KEY_NAME, defaultValue)
        } catch (e: Exception) {
            false
        }
    }

    fun clearSharedPreference() {
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.clear()
        editor.apply()
    }

    fun removeValue(KEY_NAME: String) {
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.remove(KEY_NAME)
        editor.apply()
    }
}
