package com.mkrs.kolt.transfer.domain.models

/****
 * Project: Kolt
 * Dev: Oscar Balderas Vazquez
 * From: com.mkrs.kolt.transfer.domain.models
 * Date: 27 / 05 / 2024
 *****/
data class FinalProductModel(
    val itemCode: String = "",
    val mnfSerial: String = "",
    val quantity: String = "",
    val itemName: String = "",
    val suppCatNum: String = "",
    val uPedidoProg: String = ""
)
