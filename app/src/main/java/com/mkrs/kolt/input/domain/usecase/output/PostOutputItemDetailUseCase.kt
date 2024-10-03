package com.mkrs.kolt.input.domain.usecase.output

import com.mkrs.kolt.base.webservices.MKTGenericResponse
import com.mkrs.kolt.input.domain.entity.OutputDetailRequest
import com.mkrs.kolt.input.domain.models.OutputPrinterModel
import com.mkrs.kolt.input.domain.repositories.OutputRepository

/****
 * Project: Kolt
 * Dev: Oscar Balderas Vazquez
 * From: com.mkrs.kolt.input.domain.usecase.output
 * Date: 25 / 09 / 2024
 *****/

class PostOutputItemDetailUseCase(private val outputRepository: OutputRepository) {
    suspend fun execute(
        detail: OutputDetailRequest,
        isDummy: Boolean = false
    ): DetailItemOutputResult {
        return when (val result = outputRepository.postDetail(detail, isDummy)) {
            is MKTGenericResponse.Success -> DetailItemOutputResult.Success(result.content)
            is MKTGenericResponse.Failed -> DetailItemOutputResult.Fail(result.errorMsg)
        }
    }
}

sealed class DetailItemOutputResult {
    data class Success(val result: OutputPrinterModel) : DetailItemOutputResult()
    data class Fail(val error: String) : DetailItemOutputResult()
}