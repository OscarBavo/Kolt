package com.mkrs.kolt.base.webservices.common

import com.mkrs.kolt.base.conectivity.webservice.MKTWebService
import com.mkrs.kolt.base.conectivity.webservice.MKTGeneralConfig

/****
 * Project: Kolt
 * Dev: Oscar Balderas Vazquez
 * From: com.mkrs.kolt.base.generic.webservice
 * Date: 05 / 06 / 2024
 *****/
abstract class MKTFailureCatchService<T>(
    url: String,
    tag: String,
    codeSuccess: String = MKTGeneralConfig.CODE_SUCCESS.toString()
) : MKTWebService<T>(url, tag, codeSuccess){

    override fun onFailure(code: String?, response: String?, throwable: Throwable?) {
        if(!response.isNullOrEmpty()){
            getListener()?.onFinish(this.response)
        }else{
            super.onFailure(code, response, throwable)
        }
    }
}