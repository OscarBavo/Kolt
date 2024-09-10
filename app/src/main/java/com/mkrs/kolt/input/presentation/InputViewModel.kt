package com.mkrs.kolt.input.presentation

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mkrs.kolt.input.domain.models.InputModel

/****
 * Project: Kolt
 * Dev: Oscar Balderas Vazquez
 * From: com.mkrs.kolt.input.presentation
 * Date: 29 / 08 / 2024
 *****/
class InputViewModel(private val context: Application) : ViewModel() {
    private val mutableInOutUiState = MutableLiveData<InOutputUiState>()
    var inputModel: InputModel = InputModel()
    var reference: String = ""
    var keyItem: String = ""
    var keyUnique: String = ""
    var pieces: Double = 0.0
    var batchRoll: String = ""

    val inOutViewState: LiveData<InOutputUiState>
        get() = mutableInOutUiState

    fun setNoState() {
        mutableInOutUiState.postValue(InOutputUiState.NoState)
    }

    fun saveReference(reference: String) {
        this.reference = reference
        mutableInOutUiState.postValue(InOutputUiState.SaveReference)
    }

    fun saveKeyItem(keyItem: String) {
        this.keyItem = keyItem
        mutableInOutUiState.postValue(InOutputUiState.SaveKeyItem)
    }

    fun saveKeyUnique(keyUnique: String) {
        this.keyUnique = keyUnique
        mutableInOutUiState.postValue(InOutputUiState.SaveKeyUnique)
    }

    fun savePieces(pieces: Double = 0.0) {
        this.pieces = pieces
        mutableInOutUiState.postValue(InOutputUiState.SavePieces)
    }

    fun saveBatchRoll(batchRoll: String) {
        this.batchRoll = batchRoll
        mutableInOutUiState.postValue(InOutputUiState.SaveBatchRoll)
    }

    fun resetData() {
        reference = ""
        keyItem = ""
        keyUnique = ""
        pieces = 0.0
        batchRoll = ""
    }

    private fun saveData() {
        inputModel.reference = reference
        inputModel.keyUnique = keyUnique
        inputModel.keyItem = keyItem
        inputModel.Qty = pieces
        inputModel.batchRoll = batchRoll
    }
}