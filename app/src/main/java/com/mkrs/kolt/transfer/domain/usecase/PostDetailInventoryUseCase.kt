package com.mkrs.kolt.transfer.domain.usecase

import com.mkrs.kolt.base.webservices.MKTGenericResponse
import com.mkrs.kolt.transfer.domain.models.FinalProductModel
import com.mkrs.kolt.transfer.domain.repositories.TransferRepository

/****
 * Project: Kolt
 * Dev: Oscar Balderas Vazquez
 * From: com.mkrs.kolt.transfer.domain.usecase
 * Date: 01 / 06 / 2024
 *****/

class PostDetailInventoryUseCase(private val transferRepository: TransferRepository) {
    suspend fun execute(
        claveMaterial: String,
        uniqueCode: String
    ): DetailInventoryResult {
        return when (val result =
            transferRepository.postDetailInventory(claveMaterial, uniqueCode)) {
            is MKTGenericResponse.Success -> DetailInventoryResult.Success(result.content)
            is MKTGenericResponse.Failed -> DetailInventoryResult.Fail(result.errorMsg)
        }
    }
}

sealed class DetailInventoryResult {
    data class Success(val finalProductModel: FinalProductModel) : DetailInventoryResult()
    data class Fail(val errorMsg: String) : DetailInventoryResult()
}