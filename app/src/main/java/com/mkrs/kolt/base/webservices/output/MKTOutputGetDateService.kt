package com.mkrs.kolt.base.webservices.output

import com.google.gson.Gson
import com.mkrs.kolt.base.conectivity.webservice.APIKolt.InOut.Companion.GET_OUT_RFC
import com.mkrs.kolt.base.conectivity.webservice.MKTGeneralConfig
import com.mkrs.kolt.base.conectivity.webservice.MKTWebService
import com.mkrs.kolt.base.webservices.common.ErrorResponse
import com.mkrs.kolt.input.webservices.models.OutputDateResponse
import com.mkrs.kolt.utils.CONSTANST.Companion.CODE
import java.lang.Exception

/****
 * Project: Kolt
 * Dev: Oscar Balderas Vazquez
 * From: com.mkrs.kolt.base.webservices.output
 * Date: 25 / 09 / 2024
 *****/
class MKTOutputGetDateService(private val whsCode: String) :
    MKTWebService<OutputDateResponse>(GET_OUT_RFC, CODE) {
    companion object {
        const val TAG = "MKTOutputGetDateService"
        const val WHS_CODE = "CodigoAlmacen"

    }

    override fun buildRequest() {
        addHeader(MKTGeneralConfig.CONTENT_TYPE, MKTGeneralConfig.APPLICATION_JSON_CHAR)
        addHeader(MKTGeneralConfig.NGROK_WARNING, MKTGeneralConfig.NGROK_WARNING_NUM)
        addHeader(MKTGeneralConfig.ACCEPT, MKTGeneralConfig.ACCEPT_JSON)
        addParams(WHS_CODE, whsCode)
    }

    override fun execute() {
        buildRequest()
        get()
    }

    override fun onSuccess(statusCode: String?, responseString: String?) {
        try {
            val response = Gson().fromJson(responseString, ErrorResponse::class.java)
            this.response.ErrorCode = response.ErrorCode
            this.response.Message = response.Message
            this.response.Result = OutputDateResponse(response)

        } catch (ex: Exception) {
            val response = Gson().fromJson(responseString, ErrorResponse::class.java)
            this.response.ErrorCode = response.ErrorCode
            this.response.Result = OutputDateResponse(response)
        } finally {
            getListener()?.onFinish(this.response)
        }
    }
}