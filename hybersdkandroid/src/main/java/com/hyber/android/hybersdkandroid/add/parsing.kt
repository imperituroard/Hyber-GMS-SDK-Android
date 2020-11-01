package com.hyber.android.hybersdkandroid.add

import com.hyber.android.hybersdkandroid.logger.HyberLoggerSdk

internal class HyberParsing {

    fun parseIdDevicesAll(input_json: String): String {
        HyberLoggerSdk.debug("Result: Function: parseIdDevicesAll, Class: HyberParsing, input_json: $input_json")
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
        HyberLoggerSdk.debug("Result: Function: parseIdDevicesAll, Class: HyberParsing, output: $restParsingStr")
        return restParsingStr
    }

    fun parseMessageId(input_json: String): String {
        HyberLoggerSdk.debug("Result: Function: parseMessageId, Class: HyberParsing, input_json: $input_json")
        val regex = ""","messageId":"(.+)",|messageId=(.+),|"messageId":"(.+)"""".toRegex()
        val matchResults = regex.find(input_json)
        val (res) = matchResults!!.destructured
        HyberLoggerSdk.debug("Result: Function: parseMessageId, Class: HyberParsing, output: $res")
        return res
    }


    fun parseImageUrl(input_json: String): String {
        HyberLoggerSdk.debug("Result: Function: parseImageUrl, Class: HyberParsing, input_json: $input_json")
        var matchResults = input_json.substringAfter(""""image":"""").substringBefore(""""""")
        if (matchResults.contains("\\/")) {
            matchResults = matchResults.replace("\\/", "/")
        }
        HyberLoggerSdk.debug("Result: Function: parseMessageId, Class: HyberParsing, output: $matchResults")
        return matchResults
    }
}
