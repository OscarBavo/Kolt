package com.mkrs.kolt.transfer.domain.usecase

import com.mkrs.kolt.base.GenericResponse
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
    ): GenericResponse<FinalProductModel> {
        return transferRepository.postDetailInventory(claveMaterial, uniqueCode)
    }
}