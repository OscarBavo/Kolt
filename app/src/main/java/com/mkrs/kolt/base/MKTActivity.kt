package com.mkrs.kolt.base

import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.mkrs.kolt.R

/****
 * Project: Kolt
 * Dev: Oscar Balderas Vazquez
 * From: com.mkrs.kolt.base
 * Date: 30 / 04 / 2024
 *****/
open class MKTActivity : AppCompatActivity() {
    lateinit var navController: NavController
    var showingBar: Boolean = false
    private lateinit var alertDialog: AlertDialog
    lateinit var progressDialog: Dialog

    companion object {
        private const val NONE = 0
    }

    fun setupActionBar() {
        when (showingBar) {
            true -> {
                this.supportActionBar?.show()
            }

            false -> {
                this.supportActionBar?.hide()
            }
        }
    }

    fun initDialog() {
        progressDialog = Dialog(this)
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        progressDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        progressDialog.setContentView(R.layout.kolt_loading)
        progressDialog.setCancelable(false)
    }

    fun initNavController(idNavController: Int, idNavGraph: Int, idFragmentInit: Int) {
        navController = Navigation.findNavController(this, idNavController)
        val navGraph = navController.navInflater.inflate(idNavGraph)
        navGraph.setStartDestination(idFragmentInit)
    }

    fun showAlertComplete(
        titleAlert: String? = "",
        message: String,
        okButtonText: String,
        showingOkBtn: Boolean = false,
        onClickListener: DialogInterface.OnClickListener?,
        cancelButtonText: String,
        showingNoBtn: Boolean = false,
        noListener: DialogInterface.OnClickListener?
    ) {
        if (!isFinishing) {

            val builder = AlertDialog.Builder(this)
            if (titleAlert != null) {
                if (titleAlert.isNotEmpty())
                    builder.setTitle(titleAlert)
            }
            builder.setMessage(message)
                .setCancelable(false)
            if (showingOkBtn)
                builder.setPositiveButton(okButtonText, onClickListener)
            if (showingNoBtn)
                builder.setNegativeButton(cancelButtonText, noListener)

            alertDialog = builder.show()
            alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE)

        }
    }

    fun showDialog() {
        if (!this.isFinishing) {
            progressDialog.show()
        }
    }

    fun dismissDialog() {
        if (progressDialog.isShowing) {
            progressDialog.dismiss()
        }
    }

    private fun isShowingDialog(): Boolean = progressDialog.isShowing

    public fun setFragment(fragment: MKTFragment, TAG:String) {
        try {
            cleanBackStack()
            this.supportFragmentManager.beginTransaction()
                .replace(R.id.containerMain, fragment, TAG)
                .commit()
        } catch (_: IllegalStateException) {
        }
    }

    private fun cleanBackStack() {
        this.supportFragmentManager.popBackStack(NONE, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }
}