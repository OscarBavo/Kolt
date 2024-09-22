package com.mkrs.kolt.input.presentation

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mkrs.kolt.base.webservices.entity.LineasED
import com.mkrs.kolt.input.domain.entity.InputRequest
import com.mkrs.kolt.input.domain.models.InputModel
import com.mkrs.kolt.utils.CONSTANST.Companion.WHS_CODE_IN

/****
 * Project: Kolt
 * Dev: Oscar Balderas Vazquez
 * From: com.mkrs.kolt.input.presentation
 * Date: 29 / 08 / 2024
 *****/
class InputViewModel(private val context: Application) : ViewModel() {
    private val mutableInOutUiState = MutableLiveData<InOutputUiState>()
    private val listAddItem: MutableList<InputModel> = mutableListOf()
    var inputModel: InputModel = InputModel()
    var reference: String = ""
    var keyItem: String = ""
    var keyUnique: String = ""
    var pieces: Double = 0.0
    var batchRoll: String = ""
    var numPart: String = "" //Se devuelve en endpoint

    val inOutViewState: LiveData<InOutputUiState>
        get() = mutableInOutUiState

    fun setNoState() {
        mutableInOutUiState.postValue(InOutputUiState.NoState)
    }

    fun getItemsAdded() = listAddItem.size

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

    fun saveBatchRoll(batchRoll: String, addDefinitive: Boolean = false) {
        if (!addDefinitive) {
            val inputAdded = isBatchRepeated(batchRoll)
            if (inputAdded == null) {
                this.batchRoll = batchRoll
                saveData()
            } else {
                mutableInOutUiState.postValue(InOutputUiState.ErrorBatchRoll(inputAdded))
            }
        } else {
            this.batchRoll = batchRoll
            saveData()
        }

    }

    fun isBatchRepeated(batchRoll: String) = listAddItem.find { it.batchRoll == batchRoll }
    fun resetData() {
        keyItem = ""
        keyUnique = ""
        pieces = 0.0
        batchRoll = ""
        numPart = ""
    }

    fun saveData() {
        inputModel.keyUnique = keyUnique
        inputModel.keyItem = keyItem
        inputModel.Qty = pieces
        inputModel.batchRoll = batchRoll

        listAddItem.add(inputModel)
        resetData()
        mutableInOutUiState.postValue(InOutputUiState.SaveAll(listAddItem.size))

    }

    fun saveIn() {
        val lineas = createRequest()
        val request = InputRequest(reference = reference, lines = lineas)

    }

    fun resetAllData() {
        resetData()
        reference = ""
        listAddItem.clear()
    }

    private fun createRequest(): MutableList<LineasED> {
        val lines: MutableList<LineasED> = mutableListOf()
        listAddItem.forEach { item ->
            val line =
                LineasED(
                    WHS_CODE_IN,
                    item.keyItem,
                    item.numPart,
                    item.keyUnique,
                    item.batchRoll,
                    item.Qty
                )
            lines.add(line)
        }
        lines.sortedByDescending { it.code }
        lines.sortedByDescending { it.batchNumber }
        lines.reverse()
        return lines
    }

}