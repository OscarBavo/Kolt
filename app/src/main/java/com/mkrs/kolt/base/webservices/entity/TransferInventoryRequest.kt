package com.mkrs.kolt.base.webservices.entity

import com.google.gson.annotations.SerializedName

/****
 * Project: Kolt
 * Dev: Oscar Balderas Vazquez
 * From: com.mkrs.kolt.base.webservices.entity
 * Date: 19 / 06 / 2024
 *****/
class TransferInventoryRequest(
    @SerializedName("ItemCodeMP")
    val itemCode: String = "",
    @SerializedName("DistNumber")
    val distNumber: String = "",
    @SerializedName("WhsCode")
    val whsCode: String = "01"
)