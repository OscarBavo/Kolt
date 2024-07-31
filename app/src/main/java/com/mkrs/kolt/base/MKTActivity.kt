package com.mkrs.kolt.base

import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.Window
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.snackbar.Snackbar
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
    lateinit var alertDialog: AlertDialog
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
        val navHosFragment =
            supportFragmentManager.findFragmentById(idNavController) as NavHostFragment
        navController = navHosFragment.navController
        val navGraph = navController.navInflater.inflate(idNavGraph)
        navGraph.setStartDestination(idFragmentInit)
        navController.setGraph(navGraph, intent.extras)
    }

    fun showAlertComplete(
        titleAlert: String? = "",
        message: String,
        okButtonText: String,
        showingOkBtn: Boolean = false,
        onClickListener: DialogInterface.OnClickListener?,
        cancelButtonText: String,
        showingNoBtn: Boolean = false,
        noListener: DialogInterface.OnClickListener?,
        layoutType: UserLayout = UserLayout.NO_LAYOUT
    ) {
        if (!isFinishing) {

            val builder = AlertDialog.Builder(this)
            if (titleAlert != null) {
                if (titleAlert.isNotEmpty())
                    builder.setTitle(titleAlert)
            }
            val inflater = this.layoutInflater
            builder.setMessage(message)
                .setCancelable(false)
            if (showingOkBtn) {
                builder.setPositiveButton(okButtonText, onClickListener)
                when (layoutType) {
                    UserLayout.PASS -> builder.setView(
                        inflater.inflate(
                            R.layout.dialog_pass_config,
                            null
                        )
                    )

                    UserLayout.COWORKER -> builder.setView(
                        inflater.inflate(
                            R.layout.dialog_user_config,
                            null
                        )
                    )

                    UserLayout.ABOUT_US -> {
                        builder.setView(inflater.inflate(R.layout.dialog_about_us, null))
                    }

                    else -> {}
                }
            }
            if (showingNoBtn) {
                builder.setNegativeButton(cancelButtonText, noListener)
            }

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

    fun setFragment(fragment: MKTFragment, TAG: String) {
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

    fun showAlert(msg: String, view: View) {
        Snackbar.make(view, msg, Snackbar.LENGTH_LONG).show()
    }

    fun hideKeyboard() {
        val view = currentFocus
        if (view != null) {
            val inputManager: InputMethodManager =
                getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(view.windowToken, NONE)
        }
    }
}

enum class UserLayout {
    PASS, COWORKER, NO_LAYOUT, ABOUT_US
}