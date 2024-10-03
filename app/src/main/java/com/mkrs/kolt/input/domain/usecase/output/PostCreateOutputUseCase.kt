package com.mkrs.kolt.input.domain.usecase.output

import com.mkrs.kolt.base.webservices.MKTGenericResponse
import com.mkrs.kolt.base.webservices.common.ErrorResponse
import com.mkrs.kolt.input.domain.entity.OutputRequest
import com.mkrs.kolt.input.domain.repositories.OutputRepository

/****
 * Project: Kolt
 * Dev: Oscar Balderas Vazquez
 * From: com.mkrs.kolt.input.domain.usecase.output
 * Date: 25 / 09 / 2024
 *****/

class PostCreateOutputUseCase(private val outputRepository: OutputRepository) {
    suspend fun execute(
        outputRequest: OutputRequest,
        isDummy: Boolean = false
    ): CreateOutputResult {
        return when (val result = outputRepository.postCreateOutput(outputRequest, isDummy)) {
            is MKTGenericResponse.Success -> CreateOutputResult.Success(result.content)
            is MKTGenericResponse.Failed -> CreateOutputResult.Fail(result.errorMsg)
        }
    }
}

sealed class CreateOutputResult {
    data class Success(val result: ErrorResponse) : CreateOutputResult()
    data class Fail(val error: String) : CreateOutputResult()
}