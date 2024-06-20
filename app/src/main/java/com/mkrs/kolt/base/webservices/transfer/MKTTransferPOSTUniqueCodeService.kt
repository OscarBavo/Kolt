package com.mkrs.kolt.base.webservices.transfer

import com.google.gson.Gson
import com.mkrs.kolt.base.conectivity.webservice.APIKolt.Transfer.Companion.POST_DETAIL
import com.mkrs.kolt.base.conectivity.webservice.MKTGeneralConfig
import com.mkrs.kolt.base.conectivity.webservice.MKTWebService
import com.mkrs.kolt.base.webservices.common.ErrorResponse
import com.mkrs.kolt.base.webservices.entity.TransferInventoryRequest
import com.mkrs.kolt.base.webservices.entity.TransferUniqueCodeResponse
import com.mkrs.kolt.utils.toJsonString
import java.lang.Exception

/****
 * Project: Kolt
 * Dev: Oscar Balderas Vazquez
 * From: com.mkrs.kolt.base.webservices.transfer
 * Date: 19 / 06 / 2024
 *****/
class MKTTransferPOSTUniqueCodeService(private val request: TransferInventoryRequest) :
    MKTWebService<TransferUniqueCodeResponse>(POST_DETAIL, CODE) {

    companion object {
        const val TAG = "MKTTransferPOSTUniqueCodeService"
        const val CODE = "0"
    }

    override fun buildRequest() {
        addHeader(MKTGeneralConfig.CONTENT_TYPE, MKTGeneralConfig.APPLICATION_JSON_CHAR)
        addHeader(MKTGeneralConfig.ACCEPT, MKTGeneralConfig.ACCEPT_JSON)
    }

    override fun execute() {
        buildRequest()
        post(toJsonString(request))
    }

    override fun onSuccess(statusCode: String?, responseString: String?) {
        try {
            val response = Gson().fromJson(responseString, TransferUniqueCodeResponse::class.java)
            this.response.ErrorCode = response.Response.ErrorCode
            this.response.Message = response.Response.Message
            if (!response.Response.EsError) {
                this.response.Result = response
            }
        } catch (ex: Exception) {
            val response = Gson().fromJson(responseString, ErrorResponse::class.java)
            this.response.ErrorCode = response.Message
        } finally {
            getListener()?.onFinish(this.response)
        }
    }
}