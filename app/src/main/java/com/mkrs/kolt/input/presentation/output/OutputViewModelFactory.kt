package com.mkrs.kolt.input.presentation.output

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mkrs.kolt.input.domain.usecase.output.GetOutputCodePTUseCase
import com.mkrs.kolt.input.domain.usecase.output.GetOutputDateUseCase
import com.mkrs.kolt.input.domain.usecase.output.PostCreateOutputUseCase
import com.mkrs.kolt.input.domain.usecase.output.PostOutputItemDetailUseCase
import com.mkrs.kolt.input.presentation.input.InputViewModel

/****
 * Project: Kolt
 * Dev: Oscar Balderas Vazquez
 * From: com.mkrs.kolt.input.presentation.output
 * Date: 25 / 09 / 2024
 *****/
class OutputViewModelFactory(
    private val context: Application, private val getOutputCodePTUseCase: GetOutputCodePTUseCase,
    private val getOutputDateUseCase: GetOutputDateUseCase,
    private val postOutputItemDetailUseCase: PostOutputItemDetailUseCase,
    private val postCreateOutputUseCase: PostCreateOutputUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OutputViewModel::class.java)) {
            return OutputViewModel(
                context,
                getOutputCodePTUseCase,
                getOutputDateUseCase,
                postOutputItemDetailUseCase,
                postCreateOutputUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknow ViewModel class")
    }
}