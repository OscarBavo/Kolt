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
    data class ErrorCustom(val msg: String) : TransferUIState()
    object SuccessCode : TransferUIState()
    data class SuccessDetail(val detail: FinalProductModel) : TransferUIState()

    data class AddQuantity(val quantity: String) : TransferUIState()
    data class EqualsQuantity(val quantity: String) : TransferUIState()
    data class UpperQuantity(
        val quantity: String,
        val typeQuantity: TransferViewModel.TypeQuantity
    ) : TransferUIState()

    data class  IsEnableTransfer(val isReadyToPrinter:Boolean):TransferUIState()
    data class  Printing(val labels:MutableList<String>):TransferUIState()

    data class TransferDone(val date:String):TransferUIState()

}
