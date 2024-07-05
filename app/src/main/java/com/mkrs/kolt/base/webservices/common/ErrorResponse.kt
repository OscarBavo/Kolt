package com.mkrs.kolt.base.webservices.common

/****
 * Project: Kolt
 * Dev: Oscar Balderas Vazquez
 * From: com.mkrs.kolt.base.webservices.common
 * Date: 18 / 06 / 2024
 *****/
data class ErrorResponse(
    val DocNum: String = "",
    val ErrorCode: String = "",
    val EsError: Boolean = false,
    val Message: String = "",
    val ObjType: Int = 0,
    val Result: String = "",
    val FechaHora: String = "04/07/2024 19:53:00 pm"
)
