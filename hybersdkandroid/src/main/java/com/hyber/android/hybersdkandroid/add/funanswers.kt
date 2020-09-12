package com.hyber.android.hybersdkandroid.add

import android.content.Context
import com.hyber.android.hybersdkandroid.core.HyberFunAnswerGeneral
import com.hyber.android.hybersdkandroid.core.HyberFunAnswerRegister
import com.hyber.android.hybersdkandroid.core.PushSdkParameters
import com.hyber.android.hybersdkandroid.core.Initialization
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JSON

internal class Answer {

    @Serializable
    data class ParentRegistration(
        @SerialName("session")
        val session: SessionClassNe,
        @SerialName("profile")
        val profile: ProfileClassNe,
        @SerialName("device")
        val device: DeviceClassNe
    )

    @Serializable
    data class SessionClassNe(
        @SerialName("token")
        val token: String
    )

    @Serializable
    data class ProfileClassNe(
        @SerialName("userId")
        val userId: String,
        @SerialName("userPhone")
        val userPhone: String,
        @SerialName("createdAt")
        val createdAt: String
    )

    @Serializable
    data class DeviceClassNe(
        @SerialName("deviceId")
        val deviceId: String
    )

    @Serializable
    data class RegAnswSuccess(
        @SerialName("deviceId")
        val deviceId: String,
        @SerialName("token")
        val token: String,
        @SerialName("userId")
        val userId: String,
        @SerialName("userPhone")
        val userPhone: String,
        @SerialName("createdAt")
        val createdAt: String
    )

    @Serializable
    data class NewRegistrationOk(
        val result: String,
        val code: Int,
        val description: String,
        @SerialName("body")
        val body: RegAnswSuccess
    )

    @Serializable
    data class NewRegistrationFailed(
        val result: String,
        val code: Int,
        val description: String,
        val body: String
    )

    fun hyberRegisterNewRegisterExists2(
        paramsgl: PushSdkParameters,
        context: Context
    ): HyberFunAnswerRegister {
        val initHyber = Initialization(context)

        return HyberFunAnswerRegister(
            code = 701,
            deviceId = initHyber.parametersGlobal.deviceId,
            token = initHyber.parametersGlobal.hyber_registration_token,
            userId = initHyber.parametersGlobal.hyber_user_id,
            userPhone = initHyber.parametersGlobal.hyber_user_msisdn,
            createdAt = initHyber.parametersGlobal.hyber_registration_createdAt,
            result = "Exists",
            description = "Device already registered. Nothing to do"
        )
    }

    fun registerProcedureAnswer2(
        resp_code: String,
        resp_body: String,
        context: Context
    ): HyberFunAnswerRegister {

        val hyberRewrite = RewriteParams(context)
        val anss: HyberFunAnswerRegister

        if (resp_code == "200") {
            val parent = JSON.parse(ParentRegistration.serializer(), resp_body)
            val jsonBody = JSON.stringify(
                RegAnswSuccess.serializer(),
                RegAnswSuccess(
                    parent.device.deviceId,
                    parent.session.token,
                    parent.profile.userId,
                    parent.profile.userPhone,
                    parent.profile.createdAt
                )
            )
            anss = HyberFunAnswerRegister(
                code = 200,
                description = "Success",
                result = "Ok",
                deviceId = parent.device.deviceId,
                token = parent.session.token,
                userId = parent.profile.userId,
                userPhone = parent.profile.userPhone,
                createdAt = parent.profile.createdAt
            )

            hyberRewrite.rewriteHyberUserId(parent.profile.userId)
            hyberRewrite.rewriteHyberRegistrationToken(parent.session.token)
            hyberRewrite.rewriteHyberCreateAt(parent.profile.createdAt)
            hyberRewrite.rewriteHyberDeviceId(parent.device.deviceId)
            hyberRewrite.rewriteApiRegistrationStatus(true)

            return anss
        } else if (resp_code == "401") {

            anss = HyberFunAnswerRegister(
                code = 401,
                description = "(Client error) authentication error, probably errors",
                result = "Failed",
                deviceId = "unknown",
                token = "unknown",
                userId = "unknown",
                userPhone = "unknown",
                createdAt = "unknown"
            )

            return anss
        } else if (resp_code == "400") {
            anss = HyberFunAnswerRegister(
                code = 400,
                description = "(Client error) request validation error",
                result = "Failed",
                deviceId = "unknown",
                token = "unknown",
                userId = "unknown",
                userPhone = "unknown",
                createdAt = "unknown"
            )

            return anss
        } else if (resp_code == "500") {

            anss = HyberFunAnswerRegister(
                code = 500,
                description = "(Server error)",
                result = "Failed",
                deviceId = "unknown",
                token = "unknown",
                userId = "unknown",
                userPhone = "unknown",
                createdAt = "unknown"
            )
            return anss

        } else if (resp_code == "700") {
            anss = HyberFunAnswerRegister(
                code = 700,
                description = "Internal SDK error",
                result = "Failed",
                deviceId = "unknown",
                token = "unknown",
                userId = "unknown",
                userPhone = "unknown",
                createdAt = "unknown"
            )
            return anss
        } else {

            anss = HyberFunAnswerRegister(
                code = 710,
                description = "Unknown error",
                result = "Failed",
                deviceId = "unknown",
                token = "unknown",
                userId = "unknown",
                userPhone = "unknown",
                createdAt = "unknown"
            )

            return anss
        }

    }


    fun generalAnswer(
        resp_code: String,
        body_json: String,
        description: String
    ): HyberFunAnswerGeneral {
        val resp: HyberFunAnswerGeneral
        if (resp_code == "200") {
            resp = HyberFunAnswerGeneral(200, "OK", "Success", body_json)
        } else if (resp_code == "400") {
            resp = HyberFunAnswerGeneral(400, "Failed", "Failed", "unknown")
        } else {
            resp = HyberFunAnswerGeneral(resp_code.toInt(), "Failed", description, body_json)
        }

        return resp
    }

}