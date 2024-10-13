package com.mkrs.kolt.dashboard.home.printer

/****
 * Project: Kolt
 * Dev: Oscar Balderas Vazquez
 * From: com.mkrs.kolt.dashboard.home.printer
 * Date: 08 / 05 / 2024
 *****/
sealed class PrinterUIState {
    object Loading : PrinterUIState()
    object NoState : PrinterUIState()
    data class Printed(val msgPrinter: String) : PrinterUIState()
    data class Error(val message: String) : PrinterUIState()
}