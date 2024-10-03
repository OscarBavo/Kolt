package com.mkrs.kolt.input.domain.usecase.output

import com.mkrs.kolt.base.webservices.MKTGenericResponse
import com.mkrs.kolt.base.webservices.common.ErrorResponse
import com.mkrs.kolt.input.domain.repositories.OutputRepository

/****
 * Project: Kolt
 * Dev: Oscar Balderas Vazquez
 * From: com.mkrs.kolt.input.domain.usecase.output
 * Date:  / 09 / 2024
 * Devuelve fecha del sistema y proveedor
 *****/
class GetOutputDateUseCase(private val outputRepository: OutputRepository) {
    suspend fun execute(keyWms: String, isDummy: Boolean): DateOutputResult {
        return when (val result = outputRepository.getDate(keyWms, isDummy)) {
            is MKTGenericResponse.Success -> DateOutputResult.Success(result.content)
            is MKTGenericResponse.Failed -> DateOutputResult.Fail(result.errorMsg)
        }
    }
}

sealed class DateOutputResult {
    data class Success(val result: ErrorResponse) : DateOutputResult()
    data class Fail(val error: String) : DateOutputResult()
}