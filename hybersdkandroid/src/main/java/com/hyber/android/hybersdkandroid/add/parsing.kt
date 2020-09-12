package com.hyber.android.hybersdkandroid.add

import android.content.ContentValues.TAG
import android.util.Log

internal class HyberParsing {

    fun parseIdDevicesAll(input_json: String): String {
        var respstr = "["
        val regex =
            """"id":\s(\d+),\s|"id":(\d+),\s|"id":(\d+),|"id" :(\d+),|"id":(\d+) ,""".toRegex()
        val matchResults = regex.findAll(input_json)
        for (matchedText in matchResults) {
            val value: String =
                matchedText.value.replace("\"", "").replace(",", "").replace("id", "")
                    .replace(" ", "").replace(":", "")
            respstr = "$respstr\"$value\", "
        }
        respstr = respstr.dropLast(2) + "]"
        return respstr
    }

    fun parseMessageId(input_json: String): String {

        Log.d(
            TAG,
            "Result: Function: parse_message_id, Class: HyberParsing, input_json: $input_json"
        )
        var respstr = String()
        val regex = ""","messageId":"(.+)",|messageId=(.+),|"messageId":"(.+)"""".toRegex()
        //val regex = """messageId=(.+),""".toRegex()
        val matchResults = regex.find(input_json)
        val (res) = matchResults!!.destructured
        return res
    }

}
