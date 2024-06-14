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
import com.mkrs.kolt.transfer.presentation.TransferFragment.Companion.VERIFY_TOTAL_OK
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
    private var quantity: Double = 0.0
    private var quantityDone: Double = 0.0
    private var quantityReject: Double = 0.0
    private var quantityDiff: Double = 0.0
    private var quantitySCRAP: Double = 0.0

    val transferViewState: LiveData<TransferUIState>
        get() = mutableTransferUIState

    private fun saveFinalProductModel(finalProductModel: FinalProductModel) {
        this.finalProductModel = finalProductModel
    }

    fun resetFinalProductModel() {
        this.finalProductModel = FinalProductModel()
        code = ""
        quantity = VERIFY_TOTAL_OK
    }

    fun setNoState() {
        mutableTransferUIState.postValue(TransferUIState.NoState)
    }

    private fun saveCode(code: String) {
        this.code = code
    }

    private fun saveQuantity(quantity: String) {
        this.quantity = quantity.toDouble()
    }

    fun saveQuantityDone(quantity: String) {
        this.quantityDone = quantity.toDouble()
        isAvailableQuantity(TypeQuantity.DONE)
    }

    fun saveQuantityReject(quantity: String) {
        this.quantityReject = quantity.toDouble()
        isAvailableQuantity(TypeQuantity.REJECT)
    }

    fun saveQuantityDiff(quantity: String) {
        this.quantityDiff = quantity.toDouble()
        isAvailableQuantity(TypeQuantity.DIFF)
    }

    fun saveQuantitySCRAP(quantity: String) {
        this.quantitySCRAP = quantity.toDouble()
        isAvailableQuantity(TypeQuantity.SCRAP)
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
            mutableTransferUIState.postValue(TransferUIState.Loading)
            when (val response = postDetailInventoryUseCase.execute(code, codigoUnico)) {
                is DetailInventoryResult.Success -> {
                    if (response.finalProductModel.quantity == context.getString(R.string.quantity_detail)) {
                        mutableTransferUIState.postValue(TransferUIState.Error(context.getString(R.string.quantity_detail_msg)))
                    } else {
                        saveQuantity(response.finalProductModel.quantity)
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

    fun updateDataFinalProduct() {
        finalProductModel.pieces = quantity.toString()
        finalProductModel.stdPack = quantity.toString()
        finalProductModel.piecesPT = quantity.toString()
        finalProductModel.notePT =
            context.getString(R.string.label_note_printer, quantity.toString())
    }


    private fun isAvailableQuantity(typeQuantity: TypeQuantity) {
        val quantityTotal = quantityDone + quantityReject + quantityDiff + quantitySCRAP
        if (quantity - quantityTotal < VERIFY_TOTAL_OK) {
            mutableTransferUIState.postValue(
                TransferUIState.UpperQuantity(
                    quantityTotal.toString(),
                    typeQuantity
                )
            )
        } else if ((quantity - quantityTotal) == VERIFY_TOTAL_OK) {
            mutableTransferUIState.postValue(TransferUIState.EqualsQuantity(quantityTotal.toString()))
        } else {
            mutableTransferUIState.postValue(TransferUIState.AddQuantity(quantityTotal.toString()))
        }
    }

    enum class TypeQuantity {
        DONE, REJECT, DIFF, SCRAP
    }
}