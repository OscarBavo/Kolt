package com.mkrs.kolt.input.presentation.output

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mkrs.kolt.R
import com.mkrs.kolt.base.webservices.common.ErrorResponse
import com.mkrs.kolt.input.domain.entity.OutputDetailRequest
import com.mkrs.kolt.input.domain.models.OutPutModel
import com.mkrs.kolt.input.domain.usecase.output.CodeOutputResult
import com.mkrs.kolt.input.domain.usecase.output.DateOutputResult
import com.mkrs.kolt.input.domain.usecase.output.DetailItemOutputResult
import com.mkrs.kolt.input.domain.usecase.output.GetOutputCodePTUseCase
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
    private val context: Application, private val getOutputCodePTUseCase: GetOutputCodePTUseCase,
    private val getOutputDateUseCase: GetOutputDateUseCase,
    private val postOutputItemDetailUseCase: PostOutputItemDetailUseCase,
    private val postCreateOutputUseCase: PostCreateOutputUseCase
) : ViewModel() {
    private val mutableOutUiState = MutableLiveData<OutputUiState>()
    private val listOutItem: MutableList<OutPutModel> = mutableListOf()
    var outputModel: OutPutModel = OutPutModel()

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
    val outputViewState: LiveData<OutputUiState>
        get() = mutableOutUiState

    fun setNoState() {
        mutableOutUiState.postValue(OutputUiState.NoState)
    }

    fun getItemsOutput() = listOutItem.size
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
            val outputDetailRequest = OutputDetailRequest(itemCodeMP, keyUnique, whsCode)
            when (val response =
                postOutputItemDetailUseCase.execute(outputDetailRequest, isDummy)) {
                is DetailItemOutputResult.Success -> {
                    val quantity = getQuantity(response.result)
                    if (quantity == NO_QUANTITY_AVAILABLE) {
                        mutableOutUiState.postValue(
                            OutputUiState.ErrorQuantityAvailable(
                                NO_QUANTITY_AVAILABLE
                            )
                        )
                    } else {
                        quantityAvailable = quantity.toDouble()
                        batchNumber = keyUnique
                        mutableOutUiState.postValue(OutputUiState.QuantityAvailable(quantity))
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
                        mutableOutUiState.postValue(OutputUiState.GetDate(result))
                    }
                }

                is DateOutputResult.Fail -> {
                    val result = getDateOrRFCLocal(isGetRFC)
                    if (isGetRFC) {
                        mutableOutUiState.postValue(OutputUiState.GetRFC(result))
                    } else {
                        mutableOutUiState.postValue(OutputUiState.GetDate(result))
                    }
                }
            }
        }
    }

    fun getCodePT(code: String, isDummy: Boolean = false) {
        viewModelScope.launch {
            mutableOutUiState.postValue(OutputUiState.Loading)
            when (val response = getOutputCodePTUseCase.execute(code, isDummy)) {
                is CodeOutputResult.Success -> {
                    if (response.data == context.getString(R.string.not_found_data_code_pt)) {
                        mutableOutUiState.postValue(
                            OutputUiState.ErrorOutPutKeyPT(
                                context.getString(
                                    R.string.not_found_data_code_p_msg
                                )
                            )
                        )
                    } else {
                        itemCodeMP = code
                        itemCodePT = response.data
                        whsCode =
                            if (code.startsWith(CODE_START_WITH)) WHS_CODE_TWO else WHS_CODE_THREE
                        mutableOutUiState.postValue(OutputUiState.SaveOutPutKeyPT)
                    }
                }

                is CodeOutputResult.Fail -> {
                    mutableOutUiState.postValue(
                        OutputUiState.Error(
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
            mutableOutUiState.postValue(OutputUiState.SaveOutputQuantity)
        }
    }


    fun saveData() {
        outputModel.itemCodeMP = itemCodeMP
        outputModel.itemCodePT = itemCodePT
        outputModel.batchNumber = batchNumber
        outputModel.manufacturerSerialNumber = manufacturerSerialNumber
        outputModel.quantity = quantity

        listOutItem.add(outputModel)
        resetData()
        mutableOutUiState.postValue(OutputUiState.OutputTotalItems(listOutItem.size))
    }

    fun resetData() {
        whsCode = ""
        itemCodeMP = ""
        itemCodePT = ""
        batchNumber = ""
        manufacturerSerialNumber = ""
        quantity = 0.0
        outputModel = OutPutModel()
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

    private fun getQuantity(errorResponse: ErrorResponse): String {
        return if (!errorResponse.EsError) {
            errorResponse.Result
        } else {
            NO_QUANTITY_AVAILABLE
        }
    }

    private fun getDateOrRFCLocal(isRFC: Boolean): String {
        return if (isRFC) {
            GENERIC_RFC
        } else {
            getLocalDate()
        }
    }

}