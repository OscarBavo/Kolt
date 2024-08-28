package com.mkrs.kolt.base.webservices.entity

import com.mkrs.kolt.base.webservices.common.ErrorResponse

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
    val Response: ErrorResponse
)
