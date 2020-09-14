package com.hyber.android.hybersdkandroid.add

import android.content.Context
import com.hyber.android.hybersdkandroid.core.HyberFunAnswerGeneral
import com.hyber.android.hybersdkandroid.core.HyberFunAnswerRegister
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
    data class RegAnswerSuccess(
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

    fun hyberRegisterNewRegisterExists2(
        deviceId: String,
        hyber_registration_token: String,
        hyber_user_id: String,
        hyber_user_msisdn: String,
        hyber_registration_createdAt: String
    ): HyberFunAnswerRegister {

        return HyberFunAnswerRegister(
            code = 701,
            deviceId = deviceId,
            token = hyber_registration_token,
            userId = hyber_user_id,
            userPhone = hyber_user_msisdn,
            createdAt = hyber_registration_createdAt,
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
        val answerRegistrar: HyberFunAnswerRegister

        when (resp_code) {
            "200" -> {
                val parent = JSON.parse(ParentRegistration.serializer(), resp_body)
                val jsonBody = JSON.stringify(
                    RegAnswerSuccess.serializer(),
                    RegAnswerSuccess(
                        parent.device.deviceId,
                        parent.session.token,
                        parent.profile.userId,
                        parent.profile.userPhone,
                        parent.profile.createdAt
                    )
                )
                answerRegistrar = HyberFunAnswerRegister(
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

                return answerRegistrar
            }
            "401" -> {

                answerRegistrar = HyberFunAnswerRegister(
                    code = 401,
                    description = "(Client error) authentication error, probably errors",
                    result = "Failed",
                    deviceId = "unknown",
                    token = "unknown",
                    userId = "unknown",
                    userPhone = "unknown",
                    createdAt = "unknown"
                )

                return answerRegistrar
            }
            "400" -> {
                answerRegistrar = HyberFunAnswerRegister(
                    code = 400,
                    description = "(Client error) request validation error",
                    result = "Failed",
                    deviceId = "unknown",
                    token = "unknown",
                    userId = "unknown",
                    userPhone = "unknown",
                    createdAt = "unknown"
                )

                return answerRegistrar
            }
            "500" -> {

                answerRegistrar = HyberFunAnswerRegister(
                    code = 500,
                    description = "(Server error)",
                    result = "Failed",
                    deviceId = "unknown",
                    token = "unknown",
                    userId = "unknown",
                    userPhone = "unknown",
                    createdAt = "unknown"
                )
                return answerRegistrar

            }
            "700" -> {
                answerRegistrar = HyberFunAnswerRegister(
                    code = 700,
                    description = "Internal SDK error",
                    result = "Failed",
                    deviceId = "unknown",
                    token = "unknown",
                    userId = "unknown",
                    userPhone = "unknown",
                    createdAt = "unknown"
                )
                return answerRegistrar
            }
            else -> {

                answerRegistrar = HyberFunAnswerRegister(
                    code = 710,
                    description = "Unknown error",
                    result = "Failed",
                    deviceId = "unknown",
                    token = "unknown",
                    userId = "unknown",
                    userPhone = "unknown",
                    createdAt = "unknown"
                )
                return answerRegistrar
            }
        }
    }

    fun generalAnswer(
        resp_code: String,
        body_json: String,
        description: String
    ): HyberFunAnswerGeneral {

        return when (resp_code) {
            "200" -> {
                HyberFunAnswerGeneral(200, "OK", "Success", body_json)
            }
            "400" -> {
                HyberFunAnswerGeneral(400, "Failed", "Failed", "unknown")
            }
            else -> {
                HyberFunAnswerGeneral(resp_code.toInt(), "Failed", description, body_json)
            }
        }
    }

}