package com.mkrs.kolt.input.domain.entity

import com.google.gson.annotations.SerializedName

/****
 * Project: Kolt
 * Dev: Oscar Balderas Vazquez
 * From: com.mkrs.kolt.input.domain.entity
 * Date: 25 / 09 / 2024
 *****/
class OutputDetailRequest(
    @SerializedName("ItemCodePT")
    val itemCodeTP: String = "",
    @SerializedName("DistNumber")
    val DistNumber: String = "",
    @SerializedName("WhsCode")
    val whsCode: String = ""
)