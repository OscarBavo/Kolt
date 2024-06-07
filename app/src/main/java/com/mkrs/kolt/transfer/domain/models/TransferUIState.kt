package com.mkrs.kolt.transfer.domain.models

/****
 * Project: Kolt
 * Dev: Oscar Balderas Vazquez
 * From: com.mkrs.kolt.transfer.domain.models
 * Date: 07 / 06 / 2024
 *****/
sealed class TransferUIState {
    object Loading : TransferUIState()
    object NoState : TransferUIState()
    data class Error(val msg: String) : TransferUIState()
    object SuccessCode : TransferUIState()
    data class SuccessDetail(val detail: FinalProductModel) : TransferUIState()
}
