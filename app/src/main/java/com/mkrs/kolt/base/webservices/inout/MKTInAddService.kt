package com.mkrs.kolt.base.webservices.inout

import com.google.gson.Gson
import com.mkrs.kolt.base.conectivity.webservice.APIKolt.InOut.Companion.POST_IN
import com.mkrs.kolt.base.conectivity.webservice.MKTGeneralConfig
import com.mkrs.kolt.base.conectivity.webservice.MKTWebService
import com.mkrs.kolt.base.webservices.common.ErrorResponse
import com.mkrs.kolt.input.domain.entity.InputRequest
import com.mkrs.kolt.input.webservices.models.InAddResponse
import com.mkrs.kolt.utils.CONSTANST.Companion.CODE
import com.mkrs.kolt.utils.toJsonString
import java.lang.Exception

/****
 * Project: Kolt
 * Dev: Oscar Balderas Vazquez
 * From: com.mkrs.kolt.base.webservices.inout
 * Date: 21 / 09 / 2024
 *****/
class MKTInAddService(private val request: InputRequest) :
    MKTWebService<InAddResponse>(POST_IN, CODE) {

    companion object {
        const val TAG = "MKTTransferPOSTUniqueCodeService"

    }

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
                this.response.Result = InAddResponse(response)
            }
        } catch (ex: Exception) {
            val response = Gson().fromJson(responseString, ErrorResponse::class.java)
            this.response.ErrorCode = this.serviceUrl ?: ""
            this.response.Result = InAddResponse(response)
        } finally {
            getListener()?.onFinish(this.response)
        }
    }
}