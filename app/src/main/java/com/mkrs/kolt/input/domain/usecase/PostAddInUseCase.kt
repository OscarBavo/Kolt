package com.mkrs.kolt.input.domain.usecase

import com.mkrs.kolt.base.webservices.MKTGenericResponse
import com.mkrs.kolt.base.webservices.common.ErrorResponse
import com.mkrs.kolt.input.domain.entity.InputRequest
import com.mkrs.kolt.input.domain.repositories.InputRepository

/****
 * Project: Kolt
 * Dev: Oscar Balderas Vazquez
 * From: com.mkrs.kolt.input.domain.usecase
 * Date: 21 / 09 / 2024
 *****/
class PostAddInUseCase(private val inputRepository: InputRepository) {

    suspend fun execute(request: InputRequest, isDemo: Boolean): CreateInResult {
        return when (val result = inputRepository.createIn(request, isDemo)) {
            is MKTGenericResponse.Success -> CreateInResult.Success(result.content)
            is MKTGenericResponse.Failed -> CreateInResult.Fail(result.errorMsg)
        }
    }
}

sealed class CreateInResult {
    data class Success(val data: ErrorResponse) : CreateInResult()
    data class Fail(val error: String) : CreateInResult()
}
