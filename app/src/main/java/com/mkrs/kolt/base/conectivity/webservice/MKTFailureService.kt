package com.mkrs.kolt.base.conectivity.webservice

/****
 * Project: Kolt
 * Dev: Oscar Balderas Vazquez
 * From: com.mkrs.kolt.base.generic.webservice
 * Date: 05 / 06 / 2024
 *****/
abstract class MKTFailureService {
    protected abstract fun onFailure(code: String?, response: String?, throwable: Throwable?)
}