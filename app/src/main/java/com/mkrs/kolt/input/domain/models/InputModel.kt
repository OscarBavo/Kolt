package com.mkrs.kolt.input.domain.models

/****
 * Project: Kolt
 * Dev: Oscar Balderas Vazquez
 * From: com.mkrs.kolt.input.domain.models
 * Date: 29 / 08 / 2024
 *****/
data class InputModel(
    var keyItem: String = "",
    var keyUnique: String = "",
    var Qty: Double = 0.0,
    var batchRoll: String = "",
    var labelBatch: String = "",
    var numPart: String = ""
)