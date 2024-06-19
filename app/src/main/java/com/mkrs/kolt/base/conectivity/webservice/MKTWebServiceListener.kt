package com.mkrs.kolt.base.conectivity.webservice

import com.mkrs.kolt.base.webservices.entity.MKTResponse

/****
 * Project: Kolt
 * Dev: Oscar Balderas Vazquez
 * From: com.mkrs.kolt.base.generic.webservice
 * Date: 05 / 06 / 2024
 *****/
interface MKTWebServiceListener<T> {
    fun onFinish(response: MKTResponse<T>?)
}