package com.mkrs.kolt.transfer.webservices.models

/****
 * Project: Kolt
 * Dev: Oscar Balderas Vazquez
 * From: com.mkrs.kolt.transfer.webservices.models
 * Date: 17 / 06 / 2024
 *****/
data class TransferenciaED(
    val Referencia: String = "",
    val lineas: MutableList<LineasED> = mutableListOf()
)

data class LineasED(
    val WhsCode: String,
    val ItemCodeMP: String,
    val ItemCodePT: String,
    val BatchNuber: String,
    val ManufacturerSerialNumber: String,
    val Quantity: Double
)