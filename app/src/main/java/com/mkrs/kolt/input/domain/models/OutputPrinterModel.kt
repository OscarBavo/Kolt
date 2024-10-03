package com.mkrs.kolt.input.domain.models


/****
 * Project: Kolt
 * Dev: Oscar Balderas Vazquez
 * From: com.mkrs.kolt.input.domain.models
 * Date: 29 / 09 / 2024
 *****/
class OutputPrinterModel(
    var whsCode: String = "",
    var itemCodeMP: String = "",
    var itemCodePT: String = "",
    var batchNumber: String = "",
    var manufacturerSerialNumber: String = "",
    var quantity: Double = 0.0,
    val supportCatNumber: String = "",
    val order: String = "",
    var pieces: String = "",
    var stdPack: String = "",
    var itemName: String = ""
)