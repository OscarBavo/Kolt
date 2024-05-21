package com.mkrs.kolt.base

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.annotation.LayoutRes
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
open class MKTFragment(@LayoutRes layoutResId: Int) : Fragment() {
    var activity: MKTActivity? = null
    var isMainView: Boolean = false
    var isRequiredMessageReturn: Boolean = false
    lateinit var progressDialog: Dialog
    var currenttitle = 0
    var currentStringTitle = ""

    @LayoutRes
    var contentLayoutResId: Int = layoutResId

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return if (contentLayoutResId != 0) {
            inflater.inflate(contentLayoutResId, container, false)
        } else {
            super.onCreateView(inflater, container, savedInstanceState)
        }
    }

    override fun onResume() {
        super.onResume()
        onFragmentResume()
    }

    override fun onDetach() {
        callBackFragmentOnFragmentResume()
        super.onDetach()
    }


    private fun callBackFragmentOnFragmentResume() {
        val fragments = getFragments()

        for(fragment in fragments) {
            if ((fragment as MKTFragment) != this && fragment.context != null) {
                fragment.onFragmentResume()
                break
            }
        }
    }

    private fun onFragmentResume() {
        if (currenttitle != 0) {
            setTitle(currenttitle)
        } else if (currentStringTitle != null) {
            setTitle(currentStringTitle)
        }
    }

    private fun setTitle(titleId: Int) {
        if (activity != null && titleId != 0) {
            activity?.setTitle(titleId)
        }
    }

    private fun setTitle(titleId: String) {
        if (activity != null) {
            activity?.title = titleId
        }
    }

    private fun getFragments(): List<Fragment> {
        val fragments = arrayListOf<Fragment>()

        if (activity != null && activity?.supportFragmentManager != null)
            fragments.addAll(activity?.supportFragmentManager!!.fragments)

        return fragments
    }

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
        activity?.supportFragmentManager?.popBackStack(
            NONE,
            FragmentManager.POP_BACK_STACK_INCLUSIVE
        )
    }

    private fun isShowingDialog(): Boolean = progressDialog.isShowing


}