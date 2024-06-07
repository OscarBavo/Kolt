package com.mkrs.kolt.base.webservices.entity

/****
 * Project: Kolt
 * Dev: Oscar Balderas Vazquez
 * From: com.mkrs.kolt.base.generic.webservice
 * Date: 05 / 06 / 2024
 *****/

class MKTResponse<T> {
    var responseCode: String = ""
    var msg: String? = null
    var data: T? = null
    var tag: String? = null
}