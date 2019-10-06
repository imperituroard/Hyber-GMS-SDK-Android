package com.sk.android.sksdkandroid.add

import android.content.ContentValues.TAG
import android.util.Log

internal class SkParsing() {

    fun parse_id_devices_all(input_json: String):String
    {
        var respstr:String = "["
        val regex = """"id":\s(\d+),\s|"id":(\d+),\s|"id":(\d+),|"id" :(\d+),|"id":(\d+) ,""".toRegex()
        val matchResults = regex.findAll(input_json)
        for (matchedText in matchResults) {
            val value: String = matchedText.value.replace("\"", "").replace(",","").replace("id","").replace(" ","").replace(":","")
            respstr = respstr + "\"" + value + "\", "
        }
        respstr = respstr.dropLast(2) + "]"
        return respstr
    }

    fun parse_message_id(input_json: String):String
    {

        Log.d(
            TAG,
            "Result: Function: parse_message_id, Class: SkParsing, input_json: $input_json"
        )
        var respstr:String = String()
        val regex = ""","messageId":"(.+)",|messageId=(.+),|"messageId":"(.+)"""".toRegex()
        //val regex = """messageId=(.+),""".toRegex()
        val matchResults = regex.find(input_json)
        val (res) = matchResults!!.destructured
        return res
    }

}
