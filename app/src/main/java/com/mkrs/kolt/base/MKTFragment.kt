package com.mkrs.kolt.base

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.Window
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.mkrs.kolt.R

/****
 * Project: Kolt
 * Dev: Oscar Balderas Vazquez
 * From: com.mkrs.kolt.base
 * Date: 30 / 04 / 2024
 *****/
class MKTFragment : Fragment() {
    var activity: MKTActivity? = null
    lateinit var progressDialog: Dialog

    override fun onAttach(context: Context) {
        super.onAttach(context)
        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    /**
                     * Aqui debe de ir cualquier duda para cuando el usaurio quiera salir
                     */
                    if (isEnabled) {
                        isEnabled = false
                        requireActivity().onBackPressedDispatcher.onBackPressed()
                    }
                }
            })
    }

    fun initDialog() {
        progressDialog = Dialog(requireContext())
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        progressDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        progressDialog.setContentView(R.layout.kolt_loading)
        progressDialog.setCancelable(false)

    }

    fun setupBar(title: String = "", isShowing: Boolean = false) {
        activity?.let {
            it.supportActionBar?.title = title
            if (isShowing)
                it.supportActionBar?.show()
            else
                it.supportActionBar?.hide()
        }
    }

    fun showDialog() {
        if (!requireActivity().isFinishing) {
            progressDialog.show()
        }
    }

    fun dismissDialog() {
        if (isShowingDialog()) {
            progressDialog.dismiss()
        }
    }

    fun showAlert(msg: String, view: View) {
        Snackbar.make(view, msg, Snackbar.LENGTH_LONG).show()
    }

    private fun isShowingDialog(): Boolean = progressDialog.isShowing
}