package com.mkrs.kolt.base.webservices.entity

/****
 * Project: Kolt
 * Dev: Oscar Balderas Vazquez
 * From: com.mkrs.kolt.base.webservices.entity
 * Date: 19 / 06 / 2024
 *****/
data class TransferUniqueCodeResponse(
    val ItemCode: String = "",
    val ItemName: String = "",
    val Quantity: String = "",
    val MnfSerial: String = "",
    val SuppCatNum: String = "",
    val PedidoProg: String = "",
    val Response:ErrorResponse
)

data class ErrorResponse(
    val DocNum: String = "",
    val ErrorCode: String = "",
    val EsError: Boolean = false,
    val Message: String = "",
    val ObjType: Int = 0,
    val Result: String = ""
)
