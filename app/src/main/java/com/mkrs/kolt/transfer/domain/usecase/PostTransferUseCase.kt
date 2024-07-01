package com.mkrs.kolt.transfer.domain.usecase

import com.mkrs.kolt.base.webservices.MKTGenericResponse
import com.mkrs.kolt.base.webservices.common.ErrorResponse
import com.mkrs.kolt.base.webservices.entity.TransferRequest
import com.mkrs.kolt.transfer.domain.repositories.TransferRepository

/****
 * Project: Kolt
 * Dev: Oscar Balderas Vazquez
 * From: com.mkrs.kolt.transfer.domain.usecase
 * Date: 20 / 06 / 2024
 *****/
class PostTransferUseCase(private val transferRepository: TransferRepository) {
    suspend fun execute(request: TransferRequest, isDummy: Boolean = false): TransferResult {
        return when (val result = transferRepository.postTransfer(request, isDummy)) {
            is MKTGenericResponse.Success -> TransferResult.Success(result.content)
            is MKTGenericResponse.Failed -> TransferResult.Fail(result.errorMsg)
        }
    }
}

sealed class TransferResult {
    data class Success(val transfer: ErrorResponse) : TransferResult()
    data class Fail(val message: String) : TransferResult()
}