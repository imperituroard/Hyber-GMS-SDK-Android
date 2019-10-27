package com.sk.android.sksdkandroidpr

import android.content.Intent
import android.content.BroadcastReceiver
import android.content.Context


class BootBroadcast3 : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val serviceLauncher = Intent(context, MainActivity::class.java)
        context.startService(serviceLauncher)
    }
}