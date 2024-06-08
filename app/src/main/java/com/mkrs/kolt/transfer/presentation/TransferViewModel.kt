package com.mkrs.kolt.transfer.presentation

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mkrs.kolt.R
import com.mkrs.kolt.transfer.domain.models.FinalProductModel
import com.mkrs.kolt.transfer.domain.models.TransferUIState
import com.mkrs.kolt.transfer.domain.usecase.DetailInventoryResult
import com.mkrs.kolt.transfer.domain.usecase.GetCodePTUseCase
import com.mkrs.kolt.transfer.domain.usecase.GetCodePTUseCase.CodePTResult
import com.mkrs.kolt.transfer.domain.usecase.PostDetailInventoryUseCase
import kotlinx.coroutines.launch

/****
 * Project: Kolt
 * Dev: Oscar Balderas Vazquez
 * From: com.mkrs.kolt.transfer.presentation
 * Date: 01 / 06 / 2024
 *****/
class TransferViewModel(
    private val context: Application,
    private val getCodePTUseCase: GetCodePTUseCase,
    private val postDetailInventoryUseCase: PostDetailInventoryUseCase
) : ViewModel() {

    private val mutableTransferUIState = MutableLiveData<TransferUIState>()
    private var finalProductModel: FinalProductModel = FinalProductModel()
    private var code: String = ""

    val transferViewState: LiveData<TransferUIState>
        get() = mutableTransferUIState

    private fun saveFinalProductModel(finalProductModel: FinalProductModel) {
        this.finalProductModel = finalProductModel
    }

    fun setNoState() {
        mutableTransferUIState.postValue(TransferUIState.NoState)
    }

    private fun saveCode(code: String) {
        this.code = code
    }

    fun getCodePT(code: String) {
        viewModelScope.launch {
            mutableTransferUIState.postValue(TransferUIState.Loading)
            when (val response = getCodePTUseCase.execute(code)) {
                is CodePTResult.Success -> {
                    if (response.data == context.getString(R.string.not_found_data_code_pt)) {
                        mutableTransferUIState.postValue(
                            TransferUIState.NoExistsPT(
                                context.getString(
                                    R.string.not_found_data_code_p_msg
                                )
                            )
                        )
                    } else {
                        saveCode(code)
                        mutableTransferUIState.postValue(TransferUIState.SuccessCode)
                    }
                }

                is CodePTResult.Fail -> {
                    mutableTransferUIState.postValue(TransferUIState.Error(response.errorMsg))
                }
            }
        }
    }

    fun getDetailInventory(codigoUnico: String) {
        viewModelScope.launch {
            when (val response = postDetailInventoryUseCase.execute(code, codigoUnico)) {
                is DetailInventoryResult.Success -> {
                    if (response.finalProductModel.quantity == context.getString(R.string.quantity_detail)) {
                        mutableTransferUIState.postValue(TransferUIState.Error(context.getString(R.string.quantity_detail_msg)))
                    } else {
                        saveFinalProductModel(response.finalProductModel)
                        mutableTransferUIState.postValue(TransferUIState.SuccessDetail(response.finalProductModel))
                    }
                }

                is DetailInventoryResult.Fail -> {
                    mutableTransferUIState.postValue(TransferUIState.Error(response.errorMsg))
                }
            }
        }
    }
}