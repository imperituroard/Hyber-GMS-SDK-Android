package com.sk.android.sksdkandroid.add
import android.app.Notification
import android.os.Build

internal object SkInternal {

    fun notification_priority_old(prio: Int): Int {

        if (Build.VERSION.SDK_INT >= 21) {
            when (prio) {
                0 -> return Notification.DEFAULT_VIBRATE
                1 -> return Notification.VISIBILITY_PUBLIC
                2 -> return Notification.VISIBILITY_PUBLIC
                else -> return Notification.DEFAULT_VIBRATE
            }
        }
        else
        {
            when (prio) {
                0 -> return Notification.PRIORITY_DEFAULT
                1 -> return Notification.PRIORITY_HIGH
                2 -> return Notification.PRIORITY_MAX
                else -> return Notification.PRIORITY_DEFAULT
            }
        }

    }

}