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
    data class OutputTotalItems(val total: Int) : OutputUiState()
    data class GetDate(val date: String) : OutputUiState()
    object ErrorReference : OutputUiState()
    object SaveReference : OutputUiState()
    object SaveOutPutKeyPT : OutputUiState()
    data class QuantityAvailable(val quantity: String) : OutputUiState()
    data class ErrorQuantityAvailable(val quantity: String) : OutputUiState()
    data class GetRFC(val rfc: String) : OutputUiState()
    data class ErrorOutPutKeyPT(val message: String) : OutputUiState()
    object SaveOutputPerfo : OutputUiState()
    object ErrorOutputPerfo : OutputUiState()
    object ErrorOutputCoWorker : OutputUiState()
    object SaveOutputCoworker : OutputUiState()
    object SaveOutputQuantity : OutputUiState()
    data class ErrorOutputQuantityUpper(val quantity: String) : OutputUiState()
    data class ErrorOutputQuantityLowerZero(val quantity: String) : OutputUiState()
    object ErrorOutputQuantity : OutputUiState()

    data class OutputCreateToPrinter(val dateTime:String):OutputUiState()
    data class ErrorOutputCreateToPrinter(val error: String):OutputUiState()
}