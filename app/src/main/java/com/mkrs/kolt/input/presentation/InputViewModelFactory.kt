package com.mkrs.kolt.input.presentation

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mkrs.kolt.input.domain.usecase.GetInCodePTUseCase
import com.mkrs.kolt.input.domain.usecase.PostAddInUseCase

/****
 * Project: Kolt
 * Dev: Oscar Balderas Vazquez
 * From: com.mkrs.kolt.input.presentation
 * Date: 29 / 08 / 2024
 *****/
class InputViewModelFactory(
    private val context: Application,
    private val getInCodePTUseCase: GetInCodePTUseCase,
    private val postAddInUseCase: PostAddInUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InputViewModel::class.java)) {
            return InputViewModel(context, getInCodePTUseCase,postAddInUseCase) as T
        }
        throw IllegalArgumentException("Unknow ViewModel class")
    }
}