package com.mkrs.kolt.input.domain.entity

import com.google.gson.annotations.SerializedName

/****
 * Project: Kolt
 * Dev: Oscar Balderas Vazquez
 * From: com.mkrs.kolt.input.domain.entity
 * Date: 25 / 09 / 2024
 *****/
class OutputDetailRequest(
    @SerializedName("ItemCodeMP")
    val itemCodeMP: String = "",
    @SerializedName("DistNumber")
    val distNumber: String = "",
    @SerializedName("WhsCode")
    val whsCode: String = ""
)