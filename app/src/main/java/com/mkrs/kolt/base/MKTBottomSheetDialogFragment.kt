package com.mkrs.kolt.base

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.annotation.LayoutRes
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.mkrs.kolt.R
import com.mkrs.kolt.utils.adjustSheetSize

/****
 * Project: Kolt
 * Dev: Oscar Balderas Vazquez
 * From: com.mkrs.kolt.base
 * Date: 06 / 05 / 2024
 *****/
open class MKTBottomSheetDialogFragment(@LayoutRes val layoutResId: Int) : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(layoutResId, container, false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setCancelable(false)
        dialog.adjustSheetSize()
        return dialog
    }

    fun showAlert(view:View, msg: String,actionText:String, listener:View.OnClickListener){
        Snackbar.make(view, msg, Snackbar.LENGTH_LONG)
            .setAction(actionText, listener)
            .show()
    }

    fun showAlert(msg: String, view: View) {
        Snackbar.make(view, msg, Snackbar.LENGTH_LONG).show()
    }

    fun hideKeyboard(){
        val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
    }

    fun showMessageDone(view:View) {
        showAlert(
            view,
            resources.getString(R.string.update_info_generic),
            resources.getString(R.string.text_general_accept)
        ) { this.dismiss() }
    }
}