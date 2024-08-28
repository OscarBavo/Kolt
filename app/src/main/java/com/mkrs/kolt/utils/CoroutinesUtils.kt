package com.mkrs.kolt.utils

import com.mkrs.kolt.base.conectivity.webservice.MKTGeneralConfig
import com.mkrs.kolt.base.conectivity.webservice.MKTGeneralConfig.Companion.CODE_ERROR_COMMON
import com.mkrs.kolt.base.conectivity.webservice.MKTWebService
import com.mkrs.kolt.base.conectivity.webservice.MKTWebServiceListener
import com.mkrs.kolt.base.webservices.entity.MKTResponse
import kotlinx.coroutines.suspendCancellableCoroutine

/****
 * Project: Kolt
 * Dev: Oscar Balderas Vazquez
 * From: com.mkrs.kolt.utils
 * Date: 06 / 06 / 2024
 *****/
suspend inline fun <reified T> convertToSuspend(
    services: MKTWebService<T>,
    codesuccess: String = MKTGeneralConfig.CODE_SUCCESS.toString(),
    assertResultSetNotNul: Boolean = true
): MKTResponse<T> {
    return suspendCancellableCoroutine { continuation ->
        continuation.invokeOnCancellation {
            services.setListener(null)
        }
        services.setListener(
            object : MKTWebServiceListener<T> {
                override fun onFinish(response: MKTResponse<T>?) {
                    continuation.resume(MKTResponse<T>().apply {
                        ErrorCode = response?.ErrorCode ?: ""
                        Message = response?.Message
                        if (assertResultSetNotNul) {
                            if (response?.Result == null) {
                                ErrorCode = CODE_ERROR_COMMON.toString()
                            }
                            if (ErrorCode != codesuccess) {
                                Result = null
                            }
                        }
                        Result = when (response?.Result) {
                            null -> null
                            is Array<*> -> convertToArrayList(response.Result as Array<*>) as T
                            else -> response.Result
                        }
                    }, null)
                }
            }
        )
        services.start()
    }
}

inline fun <reified T> convertToArrayList(array: Array<out T>): List<T> {
    return array.toList()
}