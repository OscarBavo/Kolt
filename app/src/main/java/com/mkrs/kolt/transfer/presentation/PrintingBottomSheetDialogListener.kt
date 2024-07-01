package com.mkrs.kolt.transfer.presentation

/****
 * Project: Kolt
 * Dev: Oscar Balderas Vazquez
 * From: com.mkrs.kolt.transfer.presentation
 * Date: 29 / 06 / 2024
 *****/
interface PrintingBottomSheetDialogListener {

    fun onPrintingSuccess(result: String)

    fun onDismissWithOutAnswer()

    fun onDismiss() {}
}