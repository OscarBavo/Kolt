package com.mkrs.kolt.input.presentation.input

import com.mkrs.kolt.input.domain.models.InputModel

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
    object SaveReference : InOutputUiState()
    object ErrorReference : InOutputUiState()
    object SaveKeyItem : InOutputUiState()
    data class ErrorSaveKeyItem(val msg: String) : InOutputUiState()
    object SaveKeyUnique : InOutputUiState()
    object SavePieces : InOutputUiState()
    object SaveBatchRoll : InOutputUiState()
    data class ErrorBatchRoll(val inputModel: InputModel) : InOutputUiState()
    data class ErrorRegexBatchRoll(val errorBatch: String) : InOutputUiState()
    data class SaveAll(val totalBatch: Int) : InOutputUiState()
    data class CreateInReady(val docNum: Int) : InOutputUiState()
}