package com.mkrs.kolt.base.webservices.entity

/****
 * Project: Kolt
 * Dev: Oscar Balderas Vazquez
 * From: com.mkrs.kolt.base.generic.webservice
 * Date: 05 / 06 / 2024
 *****/

class MKTResponse<T> {
    var EsError: Boolean = true
    var ErrorCode: String = ""
    var Message: String? = null
    var DocNum: Int = 0
    var ObjType: Int = 0
    var Result: T? = null
}
/*
{
  "EsError": true,
  "ErrorCode": 0,
  "Message": "string",
  "DocNum": 0,
  "ObjType": 0,
  "Result": "string"
}
 */