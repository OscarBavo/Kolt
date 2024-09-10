package com.mkrs.kolt.input.presentation

/****
 * Project: Kolt
 * Dev: Oscar Balderas Vazquez
 * From: com.mkrs.kolt.input.presentation
 * Date: 29 / 08 / 2024
 *****/
sealed class InOutputUiState {
    object Loading : InOutputUiState()
    object NoState : InOutputUiState()
    data class Error(val msg: String) : InOutputUiState()
    data class ErrorCustom(val msg: String) : InOutputUiState()
    object SaveReference : InOutputUiState()
    object SaveKeyItem : InOutputUiState()
    object SaveKeyUnique : InOutputUiState()
    object SavePieces : InOutputUiState()
    object SaveBatchRoll : InOutputUiState()


    object SaveOutPutReference : InOutputUiState()
    object SaveOutPutKeyPT : InOutputUiState()
    object SaveOutPutKeyUnique : InOutputUiState()
    object SaveOutPutPerfo : InOutputUiState()
    object SaveOutputCoworker : InOutputUiState()
    object SaveOutputTo : InOutputUiState()
}