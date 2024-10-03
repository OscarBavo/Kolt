package com.mkrs.kolt.input.presentation.output

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mkrs.kolt.R
import com.mkrs.kolt.base.webservices.common.ErrorResponse
import com.mkrs.kolt.input.domain.entity.OutputDetailRequest
import com.mkrs.kolt.input.domain.entity.OutputRequest
import com.mkrs.kolt.input.domain.models.OutPutModel
import com.mkrs.kolt.input.domain.models.OutputPrinterModel
import com.mkrs.kolt.input.domain.usecase.output.CreateOutputResult
import com.mkrs.kolt.input.domain.usecase.output.DateOutputResult
import com.mkrs.kolt.input.domain.usecase.output.DetailItemOutputResult
import com.mkrs.kolt.input.domain.usecase.output.GetOutputDateUseCase
import com.mkrs.kolt.input.domain.usecase.output.PostCreateOutputUseCase
import com.mkrs.kolt.input.domain.usecase.output.PostOutputItemDetailUseCase
import com.mkrs.kolt.utils.CONSTANST
import com.mkrs.kolt.utils.CONSTANST.Companion.CODE_START_WITH
import com.mkrs.kolt.utils.CONSTANST.Companion.GENERIC_RFC
import com.mkrs.kolt.utils.CONSTANST.Companion.NO_QUANTITY_AVAILABLE
import com.mkrs.kolt.utils.CONSTANST.Companion.VERIFY_TOTAL_OK
import com.mkrs.kolt.utils.CONSTANST.Companion.WHS_CODE_DATE
import com.mkrs.kolt.utils.CONSTANST.Companion.WHS_CODE_THREE
import com.mkrs.kolt.utils.CONSTANST.Companion.WHS_CODE_TWO
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar

/****
 * Project: Kolt
 * Dev: Oscar Balderas Vazquez
 * From: com.mkrs.kolt.input.presentation.output
 * Date: 23 / 09 / 2024
 *****/
class OutputViewModel(
    private val context: Application,
    private val getOutputDateUseCase: GetOutputDateUseCase,
    private val postOutputItemDetailUseCase: PostOutputItemDetailUseCase,
    private val postCreateOutputUseCase: PostCreateOutputUseCase
) : ViewModel() {
    private val mutableOutUiState = MutableLiveData<OutputUiState>()
    private val listOutItem: MutableList<OutPutModel> = mutableListOf()
    private var outputModel: OutPutModel = OutPutModel()
    private var totalLabels = mutableListOf<String>()
    private var outputPrinterModel: OutputPrinterModel = OutputPrinterModel()

    private var whsCode: String = ""
    private var itemCodeMP: String = ""
    private var itemCodePT: String = ""
    private var batchNumber: String = ""
    private var perfo: String = ""
    private var coWorker: String = ""
    private var manufacturerSerialNumber: String = ""
    private var quantityAvailable: Double = 0.0
    private var quantity: Double = 0.0
    private var reference: String = ""
    private var datePrinting: String = ""
    private var hourPrinting: String = ""
    private var receiver: String = ""
    val outputViewState: LiveData<OutputUiState>
        get() = mutableOutUiState

    fun setNoState() {
        mutableOutUiState.postValue(OutputUiState.NoState)
    }

    fun getItemsOutput() = listOutItem.size
    fun getLabelsOutput() = totalLabels
    fun saveReference(reference: String) {
        if (reference.length < CONSTANST.REFERENCE_MAX_LENGTH) {
            mutableOutUiState.postValue(OutputUiState.ErrorReference)
        } else {
            this.reference = reference
            mutableOutUiState.postValue(OutputUiState.SaveReference)
        }
    }

    fun saveKeyUnique(keyUnique: String, isDummy: Boolean = false) {
        viewModelScope.launch {
            mutableOutUiState.postValue(OutputUiState.Loading)
            val outputDetailRequest =
                OutputDetailRequest(itemCodeMP = itemCodePT, distNumber = keyUnique, whsCode)
            when (val response =
                postOutputItemDetailUseCase.execute(outputDetailRequest, isDummy)) {
                is DetailItemOutputResult.Success -> {
                    itemCodeMP = response.result.itemCodeMP
                    val quantityResult = getQuantity(response.result)
                    if (quantityResult == NO_QUANTITY_AVAILABLE) {
                        mutableOutUiState.postValue(
                            OutputUiState.ErrorQuantityAvailable(
                                NO_QUANTITY_AVAILABLE
                            )
                        )
                    } else {
                        quantity = quantityResult.toDouble()
                        quantityAvailable = quantityResult.toDouble()
                        batchNumber = keyUnique
                        mutableOutUiState.postValue(OutputUiState.QuantityAvailable(quantityResult))
                    }
                }

                is DetailItemOutputResult.Fail -> {
                    mutableOutUiState.postValue(
                        OutputUiState.ErrorQuantityAvailable(
                            NO_QUANTITY_AVAILABLE
                        )
                    )
                }
            }
        }
    }

    fun getDate(isDemo: Boolean = false, isGetRFC: Boolean = false) {
        viewModelScope.launch {
            mutableOutUiState.postValue(OutputUiState.Loading)
            val whsCodeSelect = if (isGetRFC) whsCode else WHS_CODE_DATE
            when (val response = getOutputDateUseCase.execute(whsCodeSelect, isDemo)) {
                is DateOutputResult.Success -> {
                    val result = getDateOrRFC(response.result, isGetRFC, response.result.EsError)
                    if (isGetRFC) {
                        mutableOutUiState.postValue(OutputUiState.GetRFC(result))
                    } else {
                        datePrinting = result
                        mutableOutUiState.postValue(OutputUiState.GetDate(result))
                    }
                }

                is DateOutputResult.Fail -> {
                    val result = getDateOrRFCLocal(isGetRFC)
                    receiver = result
                    if (isGetRFC) {
                        mutableOutUiState.postValue(OutputUiState.GetRFC(result))
                    } else {
                        datePrinting = result
                        mutableOutUiState.postValue(OutputUiState.GetDate(result))
                    }
                }
            }
        }
    }

    fun getCodePT(code: String) {
        itemCodePT = code
        whsCode = if (itemCodePT.startsWith(CODE_START_WITH)) WHS_CODE_TWO else WHS_CODE_THREE
        mutableOutUiState.postValue(OutputUiState.SaveOutPutKeyPT)
    }

    fun savePerfo(perfo: String) {
        if (perfo.isNullOrEmpty()) {
            mutableOutUiState.postValue(OutputUiState.ErrorOutputPerfo)
        } else {
            this.perfo = perfo
            mutableOutUiState.postValue(OutputUiState.SaveOutputPerfo)
        }
    }

    fun saveCoWorker(coWorker: String) {
        if (coWorker.isNullOrEmpty()) {
            mutableOutUiState.postValue(OutputUiState.ErrorOutputCoWorker)
        } else {
            this.coWorker = coWorker
            mutableOutUiState.postValue(OutputUiState.SaveOutputCoworker)
        }
    }

    fun saveQuantity(quantity: String) {
        if (quantity.isNullOrEmpty()) {
            mutableOutUiState.postValue(OutputUiState.ErrorOutputQuantity)
        } else if (quantity.toDouble() > quantityAvailable) {
            mutableOutUiState.postValue(OutputUiState.ErrorOutputQuantityUpper(quantity))
        } else if (quantity.toDouble() <= VERIFY_TOTAL_OK) {
            mutableOutUiState.postValue(OutputUiState.ErrorOutputQuantityLowerZero(quantity))
        } else {
            this.quantity = quantity.toDouble()
            mutableOutUiState.postValue(OutputUiState.SaveOutputQuantity)
        }
    }


    fun nextOutData() {
        saveDataOut()
        mutableOutUiState.postValue(OutputUiState.OutputTotalItems(listOutItem.size))
    }

    fun saveDataOut(){
        replaceDataPrinter()
        outputModel.itemCodeMP = itemCodeMP
        outputModel.itemCodePT = itemCodePT
        outputModel.batchNumber = batchNumber
        outputModel.manufacturerSerialNumber = manufacturerSerialNumber
        outputModel.quantity = quantity
        listOutItem.add(outputModel)
        resetData()
    }

    fun resetData() {
        whsCode = ""
        itemCodeMP = ""
        itemCodePT = ""
        batchNumber = ""
        manufacturerSerialNumber = ""
        quantity = 0.0
        outputModel = OutPutModel()
        outputPrinterModel = OutputPrinterModel()
    }

    private fun getLocalDate(): String {
        val c = Calendar.getInstance()
        val dateformat = SimpleDateFormat("dd-MMM-yyyy")
        val datetime: String = dateformat.format(c.getTime())
        return datetime
    }

    private fun getDateOrRFC(
        errorResponse: ErrorResponse,
        isRFC: Boolean,
        isError: Boolean
    ): String {
        return if (isError) {
            getDateOrRFCLocal(isRFC)
        } else if (isRFC) {
            errorResponse.Result
        } else {
            errorResponse.FechaHora
        }
    }

    private fun getQuantity(outputPrinterModel: OutputPrinterModel): String {
        this.outputPrinterModel = outputPrinterModel
        this.manufacturerSerialNumber = outputPrinterModel.manufacturerSerialNumber
        return this.outputPrinterModel.quantity.toString()
    }

    private fun getDateOrRFCLocal(isRFC: Boolean): String {
        return if (isRFC) {
            GENERIC_RFC
        } else {
            getLocalDate()
        }
    }

    private fun replaceDataPrinter() {
        viewModelScope.launch {
            mutableOutUiState.postValue(OutputUiState.Loading)
            val dataLabel: Array<String> = context.resources.getStringArray(R.array.data_replace)

            var label = context.getString(R.string.label_printer_one)
            val dateTime = datePrinting.split(" ", ignoreCase = false)
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
            label = label.replace(dataLabel[1], outputPrinterModel.itemName)
            label = label.replace(dataLabel[2], outputPrinterModel.supportCatNumber)
            label = label.replace(dataLabel[3], coWorker)
            label = label.replace(dataLabel[4], outputPrinterModel.itemCodePT)
            label = label.replace(dataLabel[5], outputPrinterModel.order)
            label = label.replace(dataLabel[6], outputPrinterModel.manufacturerSerialNumber)
            label = label.replace(dataLabel[7], receiver)
            label = label.replace(dataLabel[8], outputPrinterModel.itemCodeMP)
            label = label.replace(dataLabel[9], perfo)
            label = label.replace(dataLabel[10], quantity.toString())
            label = label.replace(dataLabel[11], "1 de 1")
            label = label.replace(dataLabel[12], dateHour)
            totalLabels.add(label)

        }
    }

    fun saveOutput(isDummy: Boolean) {
        viewModelScope.launch {
            mutableOutUiState.postValue(OutputUiState.Loading)
            val outputRequest = OutputRequest(reference, listOutItem)
            when (val response = postCreateOutputUseCase.execute(outputRequest, isDummy)) {
                is CreateOutputResult.Success -> {
                    mutableOutUiState.postValue(OutputUiState.OutputCreateToPrinter(response.result.DocNum))
                }
                is CreateOutputResult.Fail -> {
                    mutableOutUiState.postValue(OutputUiState.ErrorOutputCreateToPrinter(response.error))
                }
            }
        }
    }
}