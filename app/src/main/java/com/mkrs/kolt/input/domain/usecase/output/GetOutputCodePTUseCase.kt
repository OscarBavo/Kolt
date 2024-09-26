package com.mkrs.kolt.input.domain.usecase.output

import com.mkrs.kolt.base.webservices.MKTGenericResponse
import com.mkrs.kolt.input.domain.repositories.OutputRepository

/****
 * Project: Kolt
 * Dev: Oscar Balderas Vazquez
 * From: com.mkrs.kolt.input.domain.usecase.output
 * Date: 25 / 09 / 2024
 *****/
class GetOutputCodePTUseCase(private val outputRepository: OutputRepository) {
    suspend fun execute(code: String, isDemo: Boolean): CodeOutputResult {
        return when (val result = outputRepository.getCodePT(code, isDemo)) {
            is MKTGenericResponse.Success -> CodeOutputResult.Success(result.content)
            is MKTGenericResponse.Failed -> CodeOutputResult.Fail(result.errorMsg)
        }
    }
}

sealed class CodeOutputResult {
    data class Success(val data: String) : CodeOutputResult()
    data class Fail(val data: String) : CodeOutputResult()
}