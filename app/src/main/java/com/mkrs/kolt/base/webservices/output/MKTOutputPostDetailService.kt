package com.mkrs.kolt.base.webservices.output

import com.google.gson.Gson
import com.mkrs.kolt.base.conectivity.webservice.APIKolt.InOut.Companion.POST_OUT_DETAIL
import com.mkrs.kolt.base.conectivity.webservice.MKTGeneralConfig
import com.mkrs.kolt.base.conectivity.webservice.MKTWebService
import com.mkrs.kolt.base.webservices.common.ErrorResponse
import com.mkrs.kolt.input.domain.entity.OutputDetailRequest
import com.mkrs.kolt.input.webservices.models.OutputDateResponse
import com.mkrs.kolt.utils.CONSTANST.Companion.CODE
import com.mkrs.kolt.utils.toJsonString
import java.lang.Exception

/****
 * Project: Kolt
 * Dev: Oscar Balderas Vazquez
 * From: com.mkrs.kolt.base.webservices.output
 * Date: 25 / 09 / 2024
 *****/
class MKTOutputPostDetailService(private val request: OutputDetailRequest) :
    MKTWebService<OutputDateResponse>(
        POST_OUT_DETAIL,
        CODE
    ) {
    override fun buildRequest() {
        addHeader(MKTGeneralConfig.CONTENT_TYPE, MKTGeneralConfig.APPLICATION_JSON_CHAR)
        addHeader(MKTGeneralConfig.NGROK_WARNING, MKTGeneralConfig.NGROK_WARNING_NUM)
        addHeader(MKTGeneralConfig.ACCEPT, MKTGeneralConfig.ACCEPT_JSON)
    }

    override fun execute() {
        buildRequest()
        post(toJsonString(request))
    }

    override fun onSuccess(statusCode: String?, responseString: String?) {
        try {
            val response = Gson().fromJson(responseString, ErrorResponse::class.java)
            this.response.ErrorCode = response.ErrorCode
            this.response.Message = response.Message
            if (!response.EsError) {
                this.response.Result = OutputDateResponse(response)
            }
        } catch (ex: Exception) {
            val response = Gson().fromJson(responseString, ErrorResponse::class.java)
            this.response.ErrorCode = this.serviceUrl ?: ""
            this.response.Result = OutputDateResponse(response)
        } finally {
            getListener()?.onFinish(this.response)
        }
    }
}