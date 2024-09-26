package com.mkrs.kolt.input.presentation.output

/****
 * Project: Kolt
 * Dev: Oscar Balderas Vazquez
 * From: com.mkrs.kolt.input.presentation.output
 * Date: 23 / 09 / 2024
 *****/

sealed class OutputUiState {
    object Loading : OutputUiState()
    object NoState : OutputUiState()
    data class Error(val msg: String) : OutputUiState()
    object SaveOutPutReference : OutputUiState()
    object SaveOutPutKeyPT : OutputUiState()
    object SaveOutPutKeyUnique : OutputUiState()
    object SaveOutPutPerfo : OutputUiState()
    object SaveOutputCoworker : OutputUiState()
    object SaveOutputTo : OutputUiState()
    data class OutputTotalItems(val total: Int) : OutputUiState()
    data class GetDate(val date:String):OutputUiState()
}