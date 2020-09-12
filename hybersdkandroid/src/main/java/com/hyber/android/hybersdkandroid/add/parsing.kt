package com.hyber.android.hybersdkandroid.add

import android.content.ContentValues.TAG
import android.util.Log

internal class HyberParsing {

    fun parseIdDevicesAll(input_json: String): String {
        var restParsingStr = "["
        val regex =
            """"id":\s(\d+),\s|"id":(\d+),\s|"id":(\d+),|"id" :(\d+),|"id":(\d+) ,""".toRegex()
        val matchResults = regex.findAll(input_json)
        for (matchedText in matchResults) {
            val value: String =
                matchedText.value.replace("\"", "").replace(",", "").replace("id", "")
                    .replace(" ", "").replace(":", "")
            restParsingStr = "$restParsingStr\"$value\", "
        }
        restParsingStr = restParsingStr.dropLast(2) + "]"
        return restParsingStr
    }

    fun parseMessageId(input_json: String): String {

        Log.d(
            TAG,
            "Result: Function: parse_message_id, Class: HyberParsing, input_json: $input_json"
        )
        val regex = ""","messageId":"(.+)",|messageId=(.+),|"messageId":"(.+)"""".toRegex()
        val matchResults = regex.find(input_json)
        val (res) = matchResults!!.destructured
        return res
    }

}
