package com.mkrs.kolt.transfer.presentation

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mkrs.kolt.transfer.domain.usecase.GetCodePTUseCase
import com.mkrs.kolt.transfer.domain.usecase.PostDetailInventoryUseCase

/****
 * Project: Kolt
 * Dev: Oscar Balderas Vazquez
 * From: com.mkrs.kolt.transfer.presentation
 * Date: 07 / 06 / 2024
 *****/
class TransferViewModelFactory(
    private var context:Application,
    private var getCodePTUseCase: GetCodePTUseCase,
    private var postDetailInventoryUseCase: PostDetailInventoryUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransferViewModel::class.java)) {
            return TransferViewModel(context, getCodePTUseCase, postDetailInventoryUseCase) as T
        }
        throw IllegalArgumentException("Unknow ViewModel class")
    }
}