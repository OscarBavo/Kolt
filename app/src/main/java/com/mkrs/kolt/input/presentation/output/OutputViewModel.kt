package com.mkrs.kolt.input.presentation.output

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/****
 * Project: Kolt
 * Dev: Oscar Balderas Vazquez
 * From: com.mkrs.kolt.input.presentation.output
 * Date: 23 / 09 / 2024
 *****/
class OutputViewModel(private val context: Application):ViewModel(){
    private val mutableOutUiState=MutableLiveData<OutputUiState>()

    val outputViewState: LiveData<OutputUiState>
        get() = mutableOutUiState

    fun setNoState() {
        mutableOutUiState.postValue(OutputUiState.NoState)
    }

}