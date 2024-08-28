package com.mkrs.kolt.base.webservices.common.request

import com.mkrs.kolt.utils.toJsonString

/****
 * Project: Kolt
 * Dev: Oscar Balderas Vazquez
 * From: com.mkrs.kolt.base.generic.webservice.common.request
 * Date: 06 / 06 / 2024
 *****/
class MKTBodyRequest<T>(
    url: String, tag: String, responseModel: Class<*>, private val requestModel: RequestModel
) : MKTRequest<T>(url, tag, responseModel) {
    override fun execute() {
        buildRequest()
        val request = toJsonString(requestModel.request)
        when (requestModel.requestType) {
            MKTGenericBodyRequestType.POST -> post(request)
            MKTGenericBodyRequestType.PATCH -> patch(request)
        }
    }
}

data class RequestModel(val requestType: MKTGenericBodyRequestType, val request: Any)