package com.mkrs.kolt.input.presentation.output

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mkrs.kolt.input.domain.models.OutPutModel
import com.mkrs.kolt.input.domain.usecase.output.DateOutputResult
import com.mkrs.kolt.input.domain.usecase.output.GetOutputCodePTUseCase
import com.mkrs.kolt.input.domain.usecase.output.GetOutputDateUseCase
import com.mkrs.kolt.input.domain.usecase.output.PostCreateOutputUseCase
import com.mkrs.kolt.input.domain.usecase.output.PostOutputItemDetailUseCase
import com.mkrs.kolt.utils.CONSTANST.Companion.WHS_CODE_DATE
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

    var whsCode: String = ""
    var itemCodeMP: String = ""
    var itemCodePT: String = ""
    var batchNumber: String = ""
    var manufacturerSerialNumber: String = ""
    var quantity: Double = 0.0
    val outputViewState: LiveData<OutputUiState>
        get() = mutableOutUiState

    fun setNoState() {
        mutableOutUiState.postValue(OutputUiState.NoState)
    }

    fun getDate(isDemo:Boolean=false){
        viewModelScope.launch {
            mutableOutUiState.postValue(OutputUiState.Loading)
            when(val response=getOutputDateUseCase.execute(WHS_CODE_DATE, isDemo)){
                is DateOutputResult.Success-> {
                    if(!response.result.EsError){
                        mutableOutUiState.postValue(OutputUiState.GetDate(response.result.FechaHora))
                    }else{
                        mutableOutUiState.postValue(OutputUiState.GetDate(getLocalDate()))
                    }
                }
                is DateOutputResult.Fail->{
                    mutableOutUiState.postValue(OutputUiState.GetDate(getLocalDate()))
                }
            }
        }
    }

    private fun getLocalDate():String {
        val c = Calendar.getInstance()
        val dateformat = SimpleDateFormat("dd-MMM-yyyy hh:mm:ss aa")
        val datetime: String = dateformat.format(c.getTime())
        return datetime
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

}