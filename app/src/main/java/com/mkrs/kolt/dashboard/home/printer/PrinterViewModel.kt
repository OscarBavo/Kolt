package com.mkrs.kolt.dashboard.home.printer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/****
 * Project: Kolt
 * Dev: Oscar Balderas Vazquez
 * From: com.mkrs.kolt.dashboard.home.printer
 * Date: 08 / 05 / 2024
 *****/
class PrinterViewModel : ViewModel() {
    private val mutablePrinterUiState = MutableLiveData<PrinterUIState>()
    val dashboardHomeUIState: LiveData<PrinterUIState>
        get() = mutablePrinterUiState
}