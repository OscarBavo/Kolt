package com.mkrs.kolt.input.domain.entity

import com.google.gson.annotations.SerializedName
import com.mkrs.kolt.base.webservices.common.ErrorResponse

/****
 * Project: Kolt
 * Dev: Oscar Balderas Vazquez
 * From: com.mkrs.kolt.input.domain.entity
 * Date: 29 / 09 / 2024
 *****/
data class OutPutDetailResponse(
    @SerializedName("ItemCode")
    val itemCode: String = "",
    @SerializedName("ItemName")
    val itemName: String = "",
    @SerializedName("Quantity")
    val quantity: String = "",
    @SerializedName("MnfSerial")
    val mnfSerial: String = "",
    @SerializedName("SuppCatNum")
    val supportCatNumber: String = "",
    @SerializedName("PedidoProg")
    val order: String = "",
    @SerializedName("Response")
    val response: ErrorResponse
)
