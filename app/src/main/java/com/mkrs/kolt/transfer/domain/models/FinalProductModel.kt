package com.mkrs.kolt.transfer.domain.models

/****
 * Project: Kolt
 * Dev: Oscar Balderas Vazquez
 * From: com.mkrs.kolt.transfer.domain.models
 * Date: 27 / 05 / 2024
 *****/
data class FinalProductModel(
    var itemCodeMP: String = "",
    val mnfSerial: String = "",
    val quantity: String = "",
    val itemName: String = "",
    val suppCatNum: String = "",
    val uPedidoProg: String = "",
    var pieces: String = "",
    var stdPack: String = "",
    var piecesPT: String = "",
    var notePT: String = "",
    var codeUnique: String = "",
    var codePT: String = ""
)
