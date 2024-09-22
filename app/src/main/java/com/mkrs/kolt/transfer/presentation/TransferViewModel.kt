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
import com.mkrs.kolt.utils.CONSTANST.Companion.VERIFY_TOTAL_OK
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
    private var totalLabels = mutableListOf<String>()
    private var code: String = ""
    private var itemCode: String = ""
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
        this.finalProductModel.itemCode = itemCode
    }

    fun resetFinalProductModel() {
        this.finalProductModel = FinalProductModel()
        code = ""
        quantity = VERIFY_TOTAL_OK
    }

    fun setNoState() {
        mutableTransferUIState.postValue(TransferUIState.NoState)
    }

    private fun saveCode(code: String, itemCode: String) {
        this.code = code
        this.itemCode = itemCode
    }

    fun saveCoWorker(coWorker: String) {
        this.coWorker = coWorker
        saveReadyPrinter(true, ReadyPrinter.COWORKER)
    }

    fun getCoworker() = this.coWorker

    fun getLabels() = this.totalLabels

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

    fun resetQuantity() {
        this.quantityDone = 0.0
        this.quantityReject = 0.0
        this.quantityDiff = 0.0
        this.quantitySCRAP = 0.0
        this.quantity = 0.0
        this.quantityPrinter = 0.0
        this.totalLabels.clear()
        this.totalLabels = mutableListOf()
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

    fun getCodePT(code: String, isDummy: Boolean) {
        viewModelScope.launch {
            mutableTransferUIState.postValue(TransferUIState.Loading)
            when (val response = getCodePTUseCase.execute(code, isDemo = isDummy)) {
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
                        saveCode(code, response.data)
                        mutableTransferUIState.postValue(TransferUIState.SuccessCode)
                    }
                }

                is CodePTResult.Fail -> {
                    mutableTransferUIState.postValue(TransferUIState.Error("No se encontro la información del código: $code"))
                }
            }
        }
    }

    fun getDetailInventory(codigoUnico: String, isDemo: Boolean) {
        viewModelScope.launch {
            mutableTransferUIState.postValue(TransferUIState.Loading)
            val request = TransferInventoryRequest(itemCode = code, distNumber = codigoUnico)
            when (val response = postDetailInventoryUseCase.execute(request, isDemo)) {
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

    fun createTransfer(isDummy: Boolean) {
        viewModelScope.launch {
            mutableTransferUIState.postValue(TransferUIState.Loading)
            when (val response = postTransferUseCase.execute(createPost(), isDummy)) {
                is TransferResult.Success -> {
                    if (response.transfer.Result == context.getString(R.string.generic_ok)) {
                        mutableTransferUIState.postValue(TransferUIState.TransferDone(response.transfer.FechaHora))
                    } else {
                        mutableTransferUIState.postValue(TransferUIState.ErrorCustom(response.transfer.Message))
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
        if (quantity == 0.0) return
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

    fun initReadyPrinter() {
        this.isOperardor = false
        this.isPerforadora = false
        this.isLabelReady = false
    }

    private fun isAvailableToPrinter() {
        val printer = isOperardor && isPerforadora && isLabelReady
        mutableTransferUIState.postValue(TransferUIState.IsEnableTransfer(printer))
    }

    fun replaceDataPrinter(date: String) {
        viewModelScope.launch {
            // mutableTransferUIState.postValue(TransferUIState.Loading)

            var initLabel = 0
            val dataLabel: Array<String> = context.resources.getStringArray(R.array.data_replace)
            while (initLabel < totalLabel) {
                val quantityLabel =
                    if (initLabel == totalLabel - 1) remaindersLabel else quantityPrinter
                var label = context.getString(R.string.label_printer_one)
                val dateTime = date.split(" ", ignoreCase = false)
                var dateMonth = ""
                var dateHour = ""
                if (dateTime.isNotEmpty()) {
                    if (dateTime.size == 1) {
                        dateMonth = dateTime[0]
                    } else {
                        dateMonth = dateTime[0]
                        dateHour = dateTime[1]
                    }
                }
                label = label.replace(dataLabel[0], dateMonth)
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
                label = label.replace(dataLabel[12], dateHour)
                totalLabels.add(label)
                initLabel++
            }
            mutableTransferUIState.postValue(TransferUIState.Printing(totalLabels))
        }
    }

    private fun createPost(): TransferRequest {
        val lineas: MutableList<LineasED> = mutableListOf()
        val whsCode =
            if (finalProductModel.code.startsWith(context.getString(R.string.is_pt_03))) context.getString(
                R.string.whs_code_pt_03
            ) else context.getString(R.string.whs_code_pt_02)
        val transferMP = LineasED(
            whsCode = whsCode,
            code = finalProductModel.code,
            itemCode = finalProductModel.itemCode,
            batchNumber = finalProductModel.codeUnique,
            manSerialNum = finalProductModel.mnfSerial,
            quantity = quantityDone
        )
        lineas.add(transferMP)
        if (quantityReject > 0.0) {
            val transferRej = LineasED(
                whsCode = context.getString(R.string.whs_code_rej_04),
                code = finalProductModel.code,
                itemCode = finalProductModel.itemCode,
                batchNumber = finalProductModel.codeUnique,
                manSerialNum = finalProductModel.mnfSerial,
                quantity = quantityReject
            )
            lineas.add(transferRej)
        }
        if (quantitySCRAP > 0.0) {
            val transferSCRAP = LineasED(
                whsCode = context.getString(R.string.whs_code_scrap_05),
                code = finalProductModel.code,
                itemCode = finalProductModel.itemCode,
                batchNumber = finalProductModel.codeUnique,
                manSerialNum = finalProductModel.mnfSerial,
                quantity = quantitySCRAP
            )
            lineas.add(transferSCRAP)
        }
        if (quantityDiff > 0.0) {
            val transferDiff = LineasED(
                whsCode = context.getString(R.string.whs_code_diff_06),
                code = finalProductModel.code,
                itemCode = finalProductModel.itemCode,
                batchNumber = finalProductModel.codeUnique,
                manSerialNum = finalProductModel.mnfSerial,
                quantity = quantityDiff
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