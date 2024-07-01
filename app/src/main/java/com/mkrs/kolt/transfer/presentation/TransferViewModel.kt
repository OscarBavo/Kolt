package com.mkrs.kolt.transfer.presentation

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mkrs.kolt.R
import com.mkrs.kolt.base.webservices.entity.LineasED
import com.mkrs.kolt.base.webservices.entity.TransferInventoryRequest
import com.mkrs.kolt.base.webservices.entity.TransferRequest
import com.mkrs.kolt.transfer.domain.models.FinalProductModel
import com.mkrs.kolt.transfer.domain.models.TransferUIState
import com.mkrs.kolt.transfer.domain.usecase.DetailInventoryResult
import com.mkrs.kolt.transfer.domain.usecase.GetCodePTUseCase
import com.mkrs.kolt.transfer.domain.usecase.GetCodePTUseCase.CodePTResult
import com.mkrs.kolt.transfer.domain.usecase.PostDetailInventoryUseCase
import com.mkrs.kolt.transfer.domain.usecase.PostTransferUseCase
import com.mkrs.kolt.transfer.domain.usecase.TransferResult
import com.mkrs.kolt.transfer.presentation.TransferFragment.Companion.VERIFY_TOTAL_OK
import com.mkrs.kolt.utils.emptyString
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
    private val postDetailInventoryUseCase: PostDetailInventoryUseCase,
    private val postTransferUseCase: PostTransferUseCase
) : ViewModel() {

    private val mutableTransferUIState = MutableLiveData<TransferUIState>()
    var finalProductModel: FinalProductModel = FinalProductModel()
    private var code: String = ""
    private var codeUnique: String = ""
    private var coWorker: String = ""
    private var perforadora: String = ""
    private var quantity: Double = 0.0
    private var quantityPrinter: Double = 0.0
    private var quantityDone: Double = 0.0
    private var quantityReject: Double = 0.0
    private var quantityDiff: Double = 0.0
    private var quantitySCRAP: Double = 0.0
    private var isOperardor = false
    private var isPerforadora = false
    private var isLabelReady = false
    private var totalLabel = 0
    private var remaindersLabel = 0


    val transferViewState: LiveData<TransferUIState>
        get() = mutableTransferUIState

    private fun saveFinalProductModel(finalProductModel: FinalProductModel) {
        this.finalProductModel = finalProductModel
        this.finalProductModel.code = code
        this.finalProductModel.codeUnique = codeUnique
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

    fun saveCoWorker(coWorker: String) {
        this.coWorker = coWorker
        saveReadyPrinter(true, ReadyPrinter.COWORKER)
    }

    fun savePerforadora(perforadora: String) {
        this.perforadora = perforadora
        saveReadyPrinter(true, ReadyPrinter.PERFORADORA)
    }

    private fun saveCodeUnique(code: String) {
        this.codeUnique = code
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
            val request = TransferInventoryRequest(itemCode = code, distNumber = codigoUnico)
            when (val response = postDetailInventoryUseCase.execute(request)) {
                is DetailInventoryResult.Success -> {
                    if (response.finalProductModel.quantity == context.getString(R.string.quantity_detail)) {
                        mutableTransferUIState.postValue(TransferUIState.Error(context.getString(R.string.quantity_detail_msg)))
                    } else {
                        saveCodeUnique(codigoUnico)
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

    fun createTransfer() {
        viewModelScope.launch {
         //   mutableTransferUIState.postValue(TransferUIState.Loading)
            when (val response = postTransferUseCase.execute(createPost(), true)) {
                is TransferResult.Success -> {
                    if (response.transfer.Result.toInt() > 0) {
                        mutableTransferUIState.postValue(TransferUIState.TransferDone(response.transfer.DocNum))
                    } else {
                        mutableTransferUIState.postValue(TransferUIState.Error(response.transfer.Message))
                    }
                }

                is TransferResult.Fail -> {
                    mutableTransferUIState.postValue(TransferUIState.Error(response.message))
                }
            }
        }
    }

    fun updateDataFinalProduct() {
        finalProductModel.pieces = quantityDone.toString()
        finalProductModel.stdPack = quantityDone.toString()
        finalProductModel.piecesPT = quantityDone.toString()
        finalProductModel.notePT =
            context.getString(R.string.label_note_printer, quantity.toString())
    }

    fun createNotePrinter(stdPack: Double): String {
        val labels = (quantityDone / stdPack).toInt()
        val remainders = quantityDone % stdPack
        val piecesLabel = labels * stdPack
        remaindersLabel = if (remainders.toInt() == 0) stdPack.toInt() else remainders.toInt()
        quantityPrinter = stdPack
        totalLabel = if (remainders > 0.0) {
            labels + 1
        } else {
            labels
        }
        saveReadyPrinter(true, ReadyPrinter.LABELS)
        return if (remainders > 0.0) {
            context.getString(
                R.string.label_note_printer_calculate,
                labels.toString(),
                piecesLabel.toString()
            ) + context.getString(
                R.string.label_note_printer_calculate_remainders,
                remainders.toString()
            )
        } else {
            context.getString(
                R.string.label_note_printer_calculate,
                labels.toString(),
                stdPack.toString()
            )
        }
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

    fun saveReadyPrinter(isReady: Boolean, readyPrinter: ReadyPrinter) {
        when (readyPrinter) {
            ReadyPrinter.COWORKER -> this.isOperardor = isReady
            ReadyPrinter.PERFORADORA -> this.isPerforadora = isReady
            ReadyPrinter.LABELS -> this.isLabelReady = isReady
        }
        isAvailableToPrinter()
    }

    private fun isAvailableToPrinter() {
        val printer = isOperardor && isPerforadora && isLabelReady
        mutableTransferUIState.postValue(TransferUIState.IsEnableTransfer(printer))
    }

    fun replaceDataPrinter(date:String) {
        viewModelScope.launch {
           // mutableTransferUIState.postValue(TransferUIState.Loading)
            val labels = mutableListOf<String>()
            var initLabel = 0
            val dataLabel: Array<String> = context.resources.getStringArray(R.array.data_replace)
            while (initLabel < totalLabel) {
                val quantityLabel =
                    if (initLabel == totalLabel - 1) remaindersLabel else quantityPrinter
                var label = context.getString(R.string.label_printer_one)
                label = label.replace(dataLabel[0], date)
                label = label.replace(dataLabel[1], finalProductModel.itemName)
                label = label.replace(dataLabel[2], finalProductModel.suppCatNum)
                label = label.replace(dataLabel[3], coWorker)
                label = label.replace(dataLabel[4], code)
                label = label.replace(dataLabel[5], finalProductModel.uPedidoProg)
                label = label.replace(dataLabel[6], finalProductModel.mnfSerial)
                label = label.replace(dataLabel[7], emptyString())
                label = label.replace(dataLabel[8], finalProductModel.codeUnique)
                label = label.replace(dataLabel[9], perforadora)
                label = label.replace(dataLabel[10], "$quantityLabel")
                label = label.replace(dataLabel[11], "${initLabel + 1}/$totalLabel")
                labels.add(label)
                initLabel++
            }
            mutableTransferUIState.postValue(TransferUIState.Printing(labels))
        }
    }

    private fun createPost(): TransferRequest {
        val lineas: MutableList<LineasED> = mutableListOf()
        val whsCode =
            if (finalProductModel.code.startsWith(context.getString(R.string.is_pt_03))) context.getString(
                R.string.whs_code_pt_03
            ) else context.getString(R.string.whs_code_pt_02)
        val transferMP = LineasED(
            whsCode,
            finalProductModel.code,
            finalProductModel.codeUnique,
            finalProductModel.mnfSerial,
            finalProductModel.suppCatNum,
            quantityDone
        )
        lineas.add(transferMP)
        if (quantityReject > 0.0) {
            val transferRej = LineasED(
                context.getString(R.string.whs_code_rej_04),
                finalProductModel.code,
                finalProductModel.codeUnique,
                finalProductModel.mnfSerial,
                finalProductModel.suppCatNum,
                quantityReject
            )
            lineas.add(transferRej)
        }
        if (quantitySCRAP > 0.0) {
            val transferSCRAP = LineasED(
                context.getString(R.string.whs_code_scrap_05),
                finalProductModel.code,
                finalProductModel.codeUnique,
                finalProductModel.mnfSerial,
                finalProductModel.suppCatNum,
                quantitySCRAP
            )
            lineas.add(transferSCRAP)
        }
        if (quantityDiff > 0.0) {
            val transferDiff = LineasED(
                context.getString(R.string.whs_code_diff_06),
                finalProductModel.code,
                finalProductModel.codeUnique,
                finalProductModel.mnfSerial,
                finalProductModel.suppCatNum,
                quantityDiff
            )
            lineas.add(transferDiff)
        }
        return TransferRequest("", lineas)
    }

    enum class TypeQuantity {
        DONE, REJECT, DIFF, SCRAP
    }

    enum class ReadyPrinter {
        COWORKER, PERFORADORA, LABELS
    }
}