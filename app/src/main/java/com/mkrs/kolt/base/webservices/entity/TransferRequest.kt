package com.mkrs.kolt.base.webservices.entity

import com.google.gson.annotations.SerializedName

/****
 * Project: Kolt
 * Dev: Oscar Balderas Vazquez
 * From: com.mkrs.kolt.base.webservices.entity
 * Date: 20 / 06 / 2024
 *****/
class TransferRequest(
    @SerializedName("Referencia")
    val reference: String = "",
    @SerializedName("Lineas")
    val lines: MutableList<LineasED>
)

class LineasED(
    @SerializedName("WhsCode")
    val whsCode: String = "",
    @SerializedName("ItemCodeMP")
    val code: String = "",
    @SerializedName("ItemCodePT")
    val itemCode: String = "",
    @SerializedName("BatchNuber")
    val batchNumber: String = "",
    @SerializedName("ManufacturerSerialNumber")
    val manSerialNum: String = "",
    @SerializedName("Quantity")
    val quantity: Double = 0.0
)