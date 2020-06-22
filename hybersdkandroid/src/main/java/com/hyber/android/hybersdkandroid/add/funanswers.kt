package com.hyber.android.hybersdkandroid.add

import android.content.Context
import com.hyber.android.hybersdkandroid.core.Initialization
import com.hyber.android.hybersdkandroid.core.HyberParameters
import kotlinx.serialization.*
import kotlinx.serialization.json.JSON
import com.hyber.android.hybersdkandroid.core.HyberFunAnswerGeneral
import com.hyber.android.hybersdkandroid.core.HyberFunAnswerRegister

internal class Answer() {

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

    fun hyber_register_new_register_exists(paramsgl: HyberParameters): String {

        @Serializable
        data class Body1(
            @SerialName("deviceId")
            val deviceId: String,
            @SerialName("uuid")
            val uuid: String
        )

        @Serializable
        data class MyModel(
            val result: String,
            val description: String,
            val code: Int,
            @SerialName("body")
            val body: Body1
        )

        val jsonData = JSON.stringify(
            MyModel.serializer(),
            MyModel(
                "Exists",
                "Device already registered. Nothing to do",
                701,
                Body1(paramsgl.deviceId, paramsgl.hyber_uuid)
            )
        )
        return jsonData
    }

    fun hyber_register_new_register_exists2(
        paramsgl: HyberParameters,
        context: Context
    ): HyberFunAnswerRegister {
        val init_hyber: Initialization = Initialization(context)

        @Serializable
        data class Body1(
            @SerialName("deviceId")
            val deviceId: String,
            @SerialName("uuid")
            val uuid: String
        )

        return HyberFunAnswerRegister(
            code = 701,
            deviceId = init_hyber.paramsglobal.deviceId,
            token = init_hyber.paramsglobal.hyber_registration_token,
            userId = init_hyber.paramsglobal.hyber_user_id,
            userPhone = init_hyber.paramsglobal.hyber_user_msisdn,
            createdAt = init_hyber.paramsglobal.hyber_registration_createdAt,
            result = "Exists",
            description = "Device already registered. Nothing to do"
        )
    }

    fun hyber_registration_notregistered(paramsgl: HyberParameters): String {

        @Serializable
        data class Body1(
            @SerialName("deviceId")
            val deviceId: String,
            @SerialName("uuid")
            val uuid: String
        )

        @Serializable
        data class MyModel(
            val result: String,
            val description: String,
            val code: Int,
            @SerialName("body")
            val body: Body1
        )

        val jsonData = JSON.stringify(
            MyModel.serializer(),
            MyModel(
                "Not Registered",
                "Device not registered. Nothing to do",
                701,
                Body1(paramsgl.deviceId, paramsgl.hyber_uuid)
            )
        )
        return jsonData
    }

    fun register_procedure_answer2(
        resp_code: String,
        resp_body: String,
        context: Context
    ): HyberFunAnswerRegister {

        val hyber_rewrite: RewriteParams = RewriteParams(context)
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
            ).toString()
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

            hyber_rewrite.rewrite_hyber_user_id(parent.profile.userId)
            hyber_rewrite.rewrite_hyber_registration_token(parent.session.token)
            hyber_rewrite.rewrite_hyber_create_at(parent.profile.createdAt)
            hyber_rewrite.rewrite_hyber_device_id(parent.device.deviceId)
            hyber_rewrite.rewrite_api_registrationstatus(true)

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

        //return "MyModel.serializer(a)"

        //Response : {"session": {"token": "5d54b26ccde343348c70c2f79c81d37f"}, "profile": {"userId": 21, "userPhone": "380991234567", "createdAt": "2019-09-07T14:10:49.599893+00"}, "device": {"deviceId": 65}}
    }


    fun register_procedure_answer(resp_code: String, resp_body: String, context: Context): String {

        val hyber_rewrite: RewriteParams = RewriteParams(context)

        if (resp_code == "200") {
            val parent = JSON.parse(ParentRegistration.serializer(), resp_body)
            val jsonData = JSON.stringify(
                NewRegistrationOk.serializer(),
                NewRegistrationOk(
                    "Ok",
                    200,
                    "Success",
                    RegAnswSuccess(
                        parent.device.deviceId,
                        parent.session.token,
                        parent.profile.userId,
                        parent.profile.userPhone,
                        parent.profile.createdAt
                    )
                )
            )

            hyber_rewrite.rewrite_hyber_user_id(parent.profile.userId)
            hyber_rewrite.rewrite_hyber_registration_token(parent.session.token)
            hyber_rewrite.rewrite_hyber_create_at(parent.profile.createdAt)
            hyber_rewrite.rewrite_hyber_device_id(parent.device.deviceId)
            hyber_rewrite.rewrite_api_registrationstatus(true)

            return jsonData
        } else if (resp_code == "401") {
            val resp401 = JSON.stringify(
                NewRegistrationFailed.serializer(),
                NewRegistrationFailed(
                    "Failed",
                    401,
                    "(Client error) authentication error, probably errors",
                    "unknown"
                )
            )
            return resp401
        } else if (resp_code == "400") {
            val resp401 = JSON.stringify(
                NewRegistrationFailed.serializer(),
                NewRegistrationFailed(
                    "Failed",
                    400,
                    "(Client error) request validation error",
                    "unknown"
                )
            )
            return resp401
        } else if (resp_code == "500") {
            val resp401 = JSON.stringify(
                NewRegistrationFailed.serializer(),
                NewRegistrationFailed(
                    "Failed",
                    500,
                    "(Server error)",
                    "unknown"
                )
            )
            return resp401
        } else if (resp_code == "700") {
            val resp401 = JSON.stringify(
                NewRegistrationFailed.serializer(),
                NewRegistrationFailed(
                    "Failed",
                    700,
                    "Internal SDK error",
                    "unknown"
                )
            )
            return resp401
        } else {

            val resp710 = JSON.stringify(
                NewRegistrationFailed.serializer(),
                NewRegistrationFailed(
                    "Failed",
                    710,
                    "Unknown error",
                    "unknown"
                )
            )
            return resp710
        }

        //return "MyModel.serializer(a)"

        //Response : {"session": {"token": "5d54b26ccde343348c70c2f79c81d37f"}, "profile": {"userId": 21, "userPhone": "380991234567", "createdAt": "2019-09-07T14:10:49.599893+00"}, "device": {"deviceId": 65}}
    }

    fun general_answer(
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