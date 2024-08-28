package com.mkrs.kolt.base.webservices.transfer

import com.google.gson.Gson
import com.mkrs.kolt.base.conectivity.webservice.APIKolt.Transfer.Companion.POST_TRANSFER
import com.mkrs.kolt.base.conectivity.webservice.MKTGeneralConfig
import com.mkrs.kolt.base.conectivity.webservice.MKTWebService
import com.mkrs.kolt.base.webservices.common.ErrorResponse
import com.mkrs.kolt.base.webservices.entity.TransferRequest
import com.mkrs.kolt.transfer.webservices.models.TransferResponse
import com.mkrs.kolt.utils.toJsonString
import java.lang.Exception

/****
 * Project: Kolt
 * Dev: Oscar Balderas Vazquez
 * From: com.mkrs.kolt.base.webservices.transfer
 * Date: 20 / 06 / 2024
 *****/
class MKTTransferPostTransferService(private val request: TransferRequest) :
    MKTWebService<TransferResponse>(
        POST_TRANSFER,
        CODE
    ) {
    companion object {
        const val TAG = "MKTTransferPOSTUniqueCodeService"
        const val CODE = "0"
    }

    override fun buildRequest() {
        addHeader(MKTGeneralConfig.CONTENT_TYPE, MKTGeneralConfig.APPLICATION_JSON_CHAR)
        addHeader(MKTGeneralConfig.ACCEPT, MKTGeneralConfig.ACCEPT_JSON)
        addHeader(MKTGeneralConfig.NGROK_WARNING, MKTGeneralConfig.NGROK_WARNING_NUM)
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
                this.response.Result = TransferResponse(response)
            }
        } catch (ex: Exception) {
            val response = Gson().fromJson(responseString, ErrorResponse::class.java)
            this.response.ErrorCode = this.serviceUrl ?: ""
        } finally {

            getListener()?.onFinish(this.response)
        }
    }
}