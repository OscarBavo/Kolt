package com.mkrs.kolt.input.domain.entity

import com.google.gson.annotations.SerializedName
import com.mkrs.kolt.base.webservices.entity.LineasED

/****
 * Project: Kolt
 * Dev: Oscar Balderas Vazquez
 * From: com.mkrs.kolt.input.domain.entity
 * Date: 20 / 09 / 2024
 *****/
class InputRequest (
    @SerializedName("Referencia")
    val reference: String = "",
    @SerializedName("Lineas")
    val lines: MutableList<LineasED>
)