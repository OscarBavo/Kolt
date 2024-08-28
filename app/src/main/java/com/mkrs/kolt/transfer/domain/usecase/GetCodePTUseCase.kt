package com.mkrs.kolt.transfer.domain.usecase

import com.mkrs.kolt.base.webservices.MKTGenericResponse
import com.mkrs.kolt.transfer.domain.repositories.TransferRepository

/****
 * Project: Kolt
 * Dev: Oscar Balderas Vazquez
 * From: com.mkrs.kolt.transfer.domain.usecase
 * Date: 07 / 06 / 2024
 *****/
class GetCodePTUseCase(private val transferRepository: TransferRepository) {
    suspend fun execute(codePT: String, isDemo: Boolean):
            CodePTResult {
        return when (val result = transferRepository.getCodePT(codePT, isDummy = isDemo)) {
            is MKTGenericResponse.Success -> CodePTResult.Success(result.content)
            is MKTGenericResponse.Failed -> CodePTResult.Fail(result.errorMsg)
        }
    }

    sealed class CodePTResult {
        data class Success(val data: String) : CodePTResult()
        data class Fail(val errorMsg: String) : CodePTResult()
    }
}