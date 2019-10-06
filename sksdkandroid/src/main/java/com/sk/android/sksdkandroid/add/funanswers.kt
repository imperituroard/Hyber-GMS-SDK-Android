package com.sk.android.sksdkandroid.add

import android.content.Context
import com.sk.android.sksdkandroid.core.Initialization
import com.sk.android.sksdkandroid.core.SkParameters
import kotlinx.serialization.*
import kotlinx.serialization.json.JSON
import com.sk.android.sksdkandroid.core.SkFunAnswerGeneral
import com.sk.android.sksdkandroid.core.SkFunAnswerRegister

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

    fun sk_register_new_register_exists(paramsgl: SkParameters): String {

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
                Body1(paramsgl.deviceId, paramsgl.sk_uuid)
            )
        )
        return jsonData
    }

    fun sk_register_new_register_exists2(paramsgl: SkParameters, context: Context): SkFunAnswerRegister {
        var init_sk: Initialization = Initialization(context)
        @Serializable
        data class Body1(
            @SerialName("deviceId")
            val deviceId: String,
            @SerialName("uuid")
            val uuid: String
        )

        return SkFunAnswerRegister(
            code = 701,
            deviceId = init_sk.paramsglobal.deviceId,
            token = init_sk.paramsglobal.sk_registration_token,
            userId = init_sk.paramsglobal.sk_user_id,
            userPhone = init_sk.paramsglobal.sk_user_msisdn,
            createdAt = init_sk.paramsglobal.sk_registration_createdAt,
            result = "Exists",
            description = "Device already registered. Nothing to do"
        )
    }

    fun sk_registration_notregistered(paramsgl: SkParameters): String {

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
                Body1(paramsgl.deviceId, paramsgl.sk_uuid)
            )
        )
        return jsonData
    }

    fun register_procedure_answer2(resp_code: String, resp_body: String, context: Context): SkFunAnswerRegister {

        val sk_rewrite: RewriteParams = RewriteParams(context)
        val anss: SkFunAnswerRegister

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
                )).toString()
            anss = SkFunAnswerRegister(
                code = 200,
                description = "Success",
                result = "Ok",
                deviceId = parent.device.deviceId,
                token = parent.session.token,
                userId = parent.profile.userId,
                userPhone = parent.profile.userPhone,
                createdAt = parent.profile.createdAt
            )

            sk_rewrite.rewrite_sk_user_id(parent.profile.userId)
            sk_rewrite.rewrite_sk_registration_token(parent.session.token)
            sk_rewrite.rewrite_sk_create_at(parent.profile.createdAt)
            sk_rewrite.rewrite_sk_device_id(parent.device.deviceId)
            sk_rewrite.rewrite_api_registrationstatus(true)

            return anss
        } else if (resp_code == "401") {

            anss = SkFunAnswerRegister(
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
        }
        else if (resp_code == "400") {
            anss = SkFunAnswerRegister(
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

            anss = SkFunAnswerRegister(
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
            anss = SkFunAnswerRegister(
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
        }
        else {

            anss = SkFunAnswerRegister(
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

        val sk_rewrite: RewriteParams = RewriteParams(context)

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

            sk_rewrite.rewrite_sk_user_id(parent.profile.userId)
            sk_rewrite.rewrite_sk_registration_token(parent.session.token)
            sk_rewrite.rewrite_sk_create_at(parent.profile.createdAt)
            sk_rewrite.rewrite_sk_device_id(parent.device.deviceId)
            sk_rewrite.rewrite_api_registrationstatus(true)

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
        }
        else if (resp_code == "400") {
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
        }
        else {

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

    fun general_answer(resp_code: String, body_json: String, description: String): SkFunAnswerGeneral{
        val resp: SkFunAnswerGeneral
        if (resp_code=="200"){
            resp = SkFunAnswerGeneral(200, "OK", "Success", body_json)
        } else if (resp_code=="400"){
            resp = SkFunAnswerGeneral(400, "Failed", "Failed", "unknown")
        } else {
            resp = SkFunAnswerGeneral(resp_code.toInt(), "Failed", description, body_json)
        }

        return resp
    }

}