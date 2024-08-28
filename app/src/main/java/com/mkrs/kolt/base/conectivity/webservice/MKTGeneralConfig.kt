package com.mkrs.kolt.base.conectivity.webservice

/****
 * Project: Kolt
 * Dev: Oscar Balderas Vazquez
 * From: com.mkrs.kolt.base.webservices
 * Date: 01 / 06 / 2024
 *****/
class MKTGeneralConfig {

    companion object {
        const val CODE_SUCCESS = 0
        const val CODE_ERROR_COMMON = -1
        const val CODE_ERROR_500 = 500
        const val TIME_OUT = 120000
        const val CODE_ERROR_UNAUTHORIZED_LOCATION = 511
        const val EMPTY_TEXT = ""
        const val DATA = "data"
        const val RESPONSE_CODE = "response"
        const val MESSAGE = "message"
        const val DEFAULT_NET_ERROR = "Lo sentimos tenemos problemas técnicos"
        const val CONTENT_TYPE = "Content-Type"
        const val ACCEPT = "Accept"
        const val ACCEPT_JSON = "*/*"
        const val NGROK_WARNING = "ngrok-skip-browser-warning"
        const val NGROK_WARNING_NUM = "91100"
        const val APPLICATION_JSON = "application/json"
        const val APPLICATION_JSON_CHAR = "application/json; charset=UTF-8"

    }
}