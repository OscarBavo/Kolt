package com.mkrs.kolt.base.webservices.common.request

import com.mkrs.kolt.base.webservices.common.GenericParams

/****
 * Project: Kolt
 * Dev: Oscar Balderas Vazquez
 * From: com.mkrs.kolt.base.generic.webservice
 * Date: 02 / 06 / 2024
 *****/
abstract class MKTURLRequest<T>(
    url: String,
    tag: String,
    responseModel: Class<*>,
    private val requestType: MKTGenericURLRequestType,
    headerParam: List<GenericParams> = listOf()
) : MKTRequest<T>(url, tag, responseModel, headerParam) {
    override fun execute() {
        buildRequest()
        when (requestType) {
            MKTGenericURLRequestType.GET -> get()
            MKTGenericURLRequestType.DELETE -> delete()
        }
    }
}