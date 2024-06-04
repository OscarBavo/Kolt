package com.mkrs.kolt.transfer.presentation

import androidx.lifecycle.ViewModel
import com.mkrs.kolt.base.GenericResponse
import com.mkrs.kolt.transfer.domain.usecase.GetCodePTUseCase
import com.mkrs.kolt.transfer.domain.usecase.PostDetailInventoryUseCase

/****
 * Project: Kolt
 * Dev: Oscar Balderas Vazquez
 * From: com.mkrs.kolt.transfer.presentation
 * Date: 01 / 06 / 2024
 *****/
class TransferViewModel(
    private val getCodePTUseCase: GetCodePTUseCase,
    private val postDetailInventoryUseCase: PostDetailInventoryUseCase
) : ViewModel() {

    suspend fun getCodePT(code: String) {
        when (val response = getCodePTUseCase.execute(code)) {
            is GenericResponse.success -> {}
            is GenericResponse.failed -> {}
            else -> {}
        }
    }
}