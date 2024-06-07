package com.mkrs.kolt.transfer.domain.usecase

import com.mkrs.kolt.transfer.domain.repositories.TransferRepository

/****
 * Project: Kolt
 * Dev: Oscar Balderas Vazquez
 * From: com.mkrs.kolt.transfer.domain.usecase
 * Date: 01 / 06 / 2024
 *****/
class GetCodePTUseCase(private val transferRepository: TransferRepository) {
    suspend fun execute(codePT: String) = transferRepository.getCodePT(codePT)


}