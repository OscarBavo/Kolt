package com.mkrs.kolt.base.webservices

/****
 * Project: Kolt
 * Dev: Oscar Balderas Vazquez
 * From: com.mkrs.kolt.base
 * Date: 27 / 05 / 2024
 *****/
sealed class GenericResponse<T> {
    data class Success<T>(val content:T, val code: Int? = null, val msg: String? = null) :
        GenericResponse<T>()

    data class Failed<T>(
        val errorMsg: String,
        val content: T? = null,
        val code: Int? = null,
        val codeMsg: String? = null
    ) : GenericResponse<T>()
}
