package com.mkrs.kolt.transfer.domain.models

import com.mkrs.kolt.transfer.presentation.TransferViewModel

/****
 * Project: Kolt
 * Dev: Oscar Balderas Vazquez
 * From: com.mkrs.kolt.transfer.domain.models
 * Date: 07 / 06 / 2024
 *****/
sealed class TransferUIState {
    object Loading : TransferUIState()
    object NoState : TransferUIState()
    data class NoExistsPT(val msg: String) : TransferUIState()
    data class NoExistsDetail(val msg: String) : TransferUIState()
    data class Error(val msg: String) : TransferUIState()
    object SuccessCode : TransferUIState()
    data class SuccessDetail(val detail: FinalProductModel) : TransferUIState()

    data class UpperQuantity(val typeQuantity: TransferViewModel.TypeQuantity) : TransferUIState()
    object EqualsQuantity : TransferUIState()
    data class AddQuantity(val quantity:String) : TransferUIState()
}
