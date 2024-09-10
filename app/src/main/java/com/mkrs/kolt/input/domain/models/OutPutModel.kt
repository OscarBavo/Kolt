package com.mkrs.kolt.input.domain.models

/****
 * Project: Kolt
 * Dev: Oscar Balderas Vazquez
 * From: com.mkrs.kolt.input.domain.models
 * Date: 29 / 08 / 2024
 *****/
data class OutPutModel(
    var date: String = "",
    var emit: String = "",
    var receiver: String = "",
    var reference: String = "",
    var keyPT: String = "",
    var keyUnique: String = "",
    var perfo: String = "",
    var coworker: String = "",
    var shipTo: String = ""
)