package com.mkrs.kolt.base.webservices.common.request

import com.mkrs.kolt.base.webservices.common.GenericParams
import com.mkrs.kolt.base.webservices.entity.MKTResponse
import com.mkrs.kolt.base.webservices.common.MKTFailureCatchService
import com.mkrs.kolt.base.conectivity.webservice.MKTGeneralConfig.Companion.APPLICATION_JSON
import com.mkrs.kolt.base.conectivity.webservice.MKTGeneralConfig.Companion.CODE_ERROR_COMMON
import com.mkrs.kolt.base.conectivity.webservice.MKTGeneralConfig.Companion.CONTENT_TYPE
import com.mkrs.kolt.base.conectivity.webservice.MKTGeneralConfig.Companion.DATA
import com.mkrs.kolt.base.conectivity.webservice.MKTGeneralConfig.Companion.DEFAULT_NET_ERROR
import com.mkrs.kolt.base.conectivity.webservice.MKTGeneralConfig.Companion.MESSAGE
import com.mkrs.kolt.base.conectivity.webservice.MKTGeneralConfig.Companion.RESPONSE_CODE
import com.mkrs.kolt.utils.getType
import com.mkrs.kolt.utils.parse
import org.json.JSONObject
import java.lang.Exception

/****
 * Project: Kolt
 * Dev: Oscar Balderas Vazquez
 * From: com.mkrs.kolt.base.generic.webservice
 * Date: 06 / 06 / 2024
 *****/
abstract class MKTRequest<T>(
    url: String,
    tag: String,
    private val responseModel: Class<*>,
    private val headerParams: List<GenericParams> = listOf()
) : MKTFailureCatchService<T>(url, tag) {
    override fun buildRequest() {
        addHeader(CONTENT_TYPE, APPLICATION_JSON)

        if (headerParams.isNotEmpty()) {
            headerParams.forEach { params -> addParams(params.name, params.value) }
        }
    }

    override fun onSuccess(statusCode: String?, responseString: String?) {
        try {
            val json = JSONObject(responseString ?: "")
            if (json.has(DATA)) {
                responseString?.let {
                    val objResponse: MKTResponse<T> = parse(
                        it, getType(MKTResponse::class.java, responseModel)
                    )
                    response.ErrorCode = objResponse.ErrorCode
                    response.Message = objResponse.Message
                    response.Result = objResponse.Result
                }
            } else {
                response.ErrorCode = json.getString(RESPONSE_CODE)
                response.Message = json.getString(MESSAGE)
            }
        } catch (ex: Exception) {
            response.Message = DEFAULT_NET_ERROR
            response.ErrorCode = CODE_ERROR_COMMON.toString()
        } finally {
            getListener()?.onFinish(this.response)
        }
    }
}