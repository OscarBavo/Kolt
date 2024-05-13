package com.mkrs.kolt.base

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.Window
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.snackbar.Snackbar
import com.mkrs.kolt.R

/****
 * Project: Kolt
 * Dev: Oscar Balderas Vazquez
 * From: com.mkrs.kolt.base
 * Date: 30 / 04 / 2024
 *****/
open class MKTFragment : Fragment() {
    var activity: MKTActivity? = null
    var isMainView: Boolean = false
    var isRequiredMessageReturn: Boolean = false
    lateinit var progressDialog: Dialog

    companion object {
        private const val NONE = 0
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

            it.supportActionBar?.setDisplayHomeAsUpEnabled(true)
            it.supportActionBar?.setBackgroundDrawable(
                ColorDrawable(
                    Color.parseColor(
                        resources.getString(R.string.color_title_bar)
                    )
                )
            )
        }


        setHasOptionsMenu(true)
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

    fun setFragment(fragment: MKTFragment, TAG: String) {
        try {
            cleanBackStack()
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.containerMain, fragment, TAG)
                ?.commit()
        } catch (_: IllegalStateException) {
        }
    }

    private fun cleanBackStack() {
        activity?.supportFragmentManager?.popBackStack(NONE, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }
    private fun isShowingDialog(): Boolean = progressDialog.isShowing
}