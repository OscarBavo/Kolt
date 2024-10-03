package com.mkrs.kolt.input.domain.usecase

import com.mkrs.kolt.base.webservices.MKTGenericResponse
import com.mkrs.kolt.input.domain.repositories.InputRepository

/****
 * Project: Kolt
 * Dev: Oscar Balderas Vazquez
 * From: com.mkrs.kolt.input.domain.usecase
 * Date: 21 / 09 / 2024
 *****/
class GetInCodePTUseCase(private val inputRepository: InputRepository) {
    suspend fun execute(code: String, isDemo: Boolean): CodeInPTResult {
        return when (val result = inputRepository.getCodePT(code, isDemo)) {
            is MKTGenericResponse.Success -> CodeInPTResult.Success(result.content)
            is MKTGenericResponse.Failed -> CodeInPTResult.Fail(result.errorMsg)
        }
    }
}
sealed class CodeInPTResult {
    data class Success(val data: String) : CodeInPTResult()
    data class Fail(val data: String) : CodeInPTResult()
}