package com.mkrs.kolt.input.domain.repositories

import com.mkrs.kolt.base.webservices.MKTGenericResponse
import com.mkrs.kolt.base.webservices.common.ErrorResponse
import com.mkrs.kolt.input.domain.entity.OutputDetailRequest
import com.mkrs.kolt.input.domain.entity.OutputRequest
import com.mkrs.kolt.input.domain.models.OutputPrinterModel

/****
 * Project: Kolt
 * Dev: Oscar Balderas Vazquez
 * From: com.mkrs.kolt.input.domain.repositories
 * Date: 25 / 09 / 2024
 *****/
interface OutputRepository {
    suspend fun getCodePT(
        claveMaterial: String,
        isDummy: Boolean = false
    ): MKTGenericResponse<String>

    suspend fun getDate(
        keyWms: String,
        isDummy: Boolean = false
    ): MKTGenericResponse<ErrorResponse>

    suspend fun postDetail(
        detail: OutputDetailRequest,
        isDummy: Boolean = false
    ): MKTGenericResponse<OutputPrinterModel>

    suspend fun postCreateOutput(
        outputRequest: OutputRequest,
        isDummy: Boolean = false
    ): MKTGenericResponse<ErrorResponse>
}