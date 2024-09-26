package com.mkrs.kolt.input.domain.models

import com.google.gson.annotations.SerializedName

/****
 * Project: Kolt
 * Dev: Oscar Balderas Vazquez
 * From: com.mkrs.kolt.input.domain.models
 * Date: 29 / 08 / 2024
 *****/
data class OutPutModel(
    @SerializedName("WhsCode")
    var whsCode: String = "",
    @SerializedName("ItemCodeMP")
    var itemCodeMP: String = "",
    @SerializedName("ItemCodePT")
    var itemCodePT: String = "",
    @SerializedName("BatchNumber")
    var batchNumber: String = "",
    @SerializedName("ManufacturerSerialNumber")
    var manufacturerSerialNumber: String = "",
    @SerializedName("Quantity")
    var quantity: Double = 0.0
)