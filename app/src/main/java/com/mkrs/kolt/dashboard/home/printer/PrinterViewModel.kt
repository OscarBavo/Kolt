package com.mkrs.kolt.dashboard.home.printer

import android.content.res.Resources
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mkrs.kolt.R
import com.mkrs.kolt.preferences.presentation.PreferencesViewModel
import com.mkrs.kolt.utils.MKTTCPSocket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/****
 * Project: Kolt
 * Dev: Oscar Balderas Vazquez
 * From: com.mkrs.kolt.dashboard.home.printer
 * Date: 08 / 05 / 2024
 *****/
class PrinterViewModel : ViewModel() {
    private val mutablePrinterUiState = MutableLiveData<PrinterUIState>()
    val printerUIState: LiveData<PrinterUIState>
        get() = mutablePrinterUiState

    fun printTest(ip: String, port: Int, data: String) {
        viewModelScope.launch {
            CoroutineScope(Dispatchers.IO).launch {
                val printer = MKTTCPSocket()
                printer.sendDataPrinter(data, ip, port, mutablePrinterUiState)
            }
        }
    }

    fun getDataPrinter(preferencesViewModel: PreferencesViewModel, res: Resources) =
        preferencesViewModel.getString(
            res.getString(R.string.key_value_printer),
            res.getString(R.string.default_value_printer)
        ).split(res.getString(R.string.title_split_ip))
}