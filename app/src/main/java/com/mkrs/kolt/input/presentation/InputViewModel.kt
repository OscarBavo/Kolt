package com.mkrs.kolt.input.presentation

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mkrs.kolt.R
import com.mkrs.kolt.base.webservices.entity.LineasED
import com.mkrs.kolt.input.domain.entity.InputRequest
import com.mkrs.kolt.input.domain.models.InputModel
import com.mkrs.kolt.input.domain.usecase.CreateInResult
import com.mkrs.kolt.input.domain.usecase.GetInCodePTUseCase
import com.mkrs.kolt.input.domain.usecase.PostAddInUseCase
import com.mkrs.kolt.utils.CONSTANST.Companion.REFERENCE_MAX_LENGTH
import com.mkrs.kolt.utils.CONSTANST.Companion.WHS_CODE_IN
import com.mkrs.kolt.utils.isBatchValid
import kotlinx.coroutines.launch

/****
 * Project: Kolt
 * Dev: Oscar Balderas Vazquez
 * From: com.mkrs.kolt.input.presentation
 * Date: 29 / 08 / 2024
 *****/
class InputViewModel(
    private val context: Application,
    private val getInCodePTUseCase: GetInCodePTUseCase,
    private val postAddInUseCase: PostAddInUseCase
) : ViewModel() {
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
        if (reference.length < REFERENCE_MAX_LENGTH) {
            mutableInOutUiState.postValue(InOutputUiState.ErrorReference)
        } else if (reference.length > REFERENCE_MAX_LENGTH) {
            mutableInOutUiState.postValue(InOutputUiState.ErrorReference)
        } else {
            this.reference = reference
            mutableInOutUiState.postValue(InOutputUiState.SaveReference)
        }
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
        if (!isBatchValid(batchRoll)) {
            mutableInOutUiState.postValue(InOutputUiState.ErrorRegexBatchRoll(batchRoll))
        } else if (!addDefinitive) {
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
        inputModel = InputModel()
    }

    fun saveData() {
        inputModel.keyUnique = keyUnique
        inputModel.keyItem = keyItem
        inputModel.Qty = pieces
        inputModel.batchRoll = batchRoll
        inputModel.numPart = numPart

        listAddItem.add(inputModel)
        resetData()
        mutableInOutUiState.postValue(InOutputUiState.SaveAll(listAddItem.size))
    }

    fun getCodePT(code: String, isDummy: Boolean = false) {
        viewModelScope.launch {
            mutableInOutUiState.postValue(InOutputUiState.Loading)
            when (val response = getInCodePTUseCase.execute(code, isDummy)) {
                is GetInCodePTUseCase.CodeInPTResult.Success -> {
                    if (response.data == context.getString(R.string.not_found_data_code_pt)) {
                        mutableInOutUiState.postValue(
                            InOutputUiState.ErrorSaveKeyItem(
                                context.getString(
                                    R.string.not_found_data_code_p_msg
                                )
                            )
                        )
                    } else {
                        keyItem = code
                        numPart = response.data
                        mutableInOutUiState.postValue(InOutputUiState.SaveKeyItem)
                    }
                }

                is GetInCodePTUseCase.CodeInPTResult.Fail -> {
                    mutableInOutUiState.postValue(
                        InOutputUiState.Error(
                            context.getString(
                                R.string.error_get_code_pt_general,
                                code
                            )
                        )
                    )
                }
            }
        }
    }

    fun saveIn(isDummy: Boolean = false) {
        viewModelScope.launch {
            mutableInOutUiState.postValue(InOutputUiState.Loading)
            val lines = createRequest()
            val request = InputRequest(reference = reference, lines = lines)
            when (val response = postAddInUseCase.execute(request, isDummy)) {
                is CreateInResult.Success -> {
                    if (response.data.Result == context.getString(R.string.generic_ok)) {
                        mutableInOutUiState.postValue(InOutputUiState.CreateInReady(response.data.DocNum.toInt()))
                        resetAllData()
                    } else {
                        mutableInOutUiState.postValue(InOutputUiState.Error(response.data.Message))
                    }
                }

                is CreateInResult.Fail -> {
                    mutableInOutUiState.postValue(InOutputUiState.Error(response.error))
                }
            }
        }
    }

    private fun resetAllData() {
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