package com.mkrs.kolt.input.domain.repositories

import com.mkrs.kolt.base.webservices.MKTGenericResponse
import com.mkrs.kolt.base.webservices.common.ErrorResponse
import com.mkrs.kolt.input.domain.entity.InputRequest

/****
 * Project: Kolt
 * Dev: Oscar Balderas Vazquez
 * From: com.mkrs.kolt.input.domain.repositories
 * Date: 29 / 08 / 2024
 *****/
interface InputRepository {
    suspend fun getCodePT(
        claveMaterial: String,
        isDummy: Boolean = false
    ): MKTGenericResponse<String>

    suspend fun createIn(
        request: InputRequest,
        isDummy: Boolean = false
    ): MKTGenericResponse<ErrorResponse>
}