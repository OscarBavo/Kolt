package com.mkrs.kolt.base.webservices.transfer

import com.google.gson.Gson
import com.mkrs.kolt.base.conectivity.webservice.APIKolt.Transfer.Companion.GET_CODE
import com.mkrs.kolt.base.conectivity.webservice.MKTGeneralConfig.Companion.APPLICATION_JSON
import com.mkrs.kolt.base.conectivity.webservice.MKTGeneralConfig.Companion.CONTENT_TYPE
import com.mkrs.kolt.base.conectivity.webservice.MKTWebService
import com.mkrs.kolt.base.webservices.common.ErrorResponse
import com.mkrs.kolt.transfer.webservices.models.TransferUniqueCodeResponse
import java.lang.Exception

/****
 * Project: Kolt
 * Dev: Oscar Balderas Vazquez
 * From: com.mkrs.kolt.base.webservices.transfer
 * Date: 18 / 06 / 2024
 *****/
class MKTTransferGetCodeService(
    val clavePM: String
) : MKTWebService<TransferUniqueCodeResponse>(GET_CODE, CODE) {
    companion object {
        const val TAG = "MKTTransferGetCodeService"
        const val CODE = "0"
        const val PM_KEY = "ClavePM"
    }

    override fun buildRequest() {
        addHeader(CONTENT_TYPE, APPLICATION_JSON)
        addParams(PM_KEY, clavePM)
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
            if (response.ErrorCode == statusCode) {
                this.response.Result = TransferUniqueCodeResponse(response.Result)
            }
        } catch (ex: Exception) {
            val response = Gson().fromJson(responseString, ErrorResponse::class.java)
            this.response.ErrorCode = response.Message
        } finally {
            getListener()?.onFinish(this.response)
        }
    }
}