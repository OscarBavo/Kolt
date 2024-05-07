package com.mkrs.kolt.utils

import android.app.Dialog
import android.view.View
import android.widget.FrameLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog

/****
 * Project: Kolt
 * Dev: Oscar Balderas Vazquez
 * From: com.mkrs.kolt.utils
 * Date: 06 / 05 / 2024
 *****/

fun Dialog.adjustSheetSize() {
    setOnShowListener {
        val sheetDialog = it as BottomSheetDialog
        val frameLayout =
            sheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout?
        if (frameLayout != null) {
            val behavior = BottomSheetBehavior.from(frameLayout)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.isHideable = true
        }
    }
}