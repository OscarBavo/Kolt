package com.mkrs.kolt

import android.app.AlertDialog
import android.os.Bundle
import android.view.KeyEvent
import android.widget.EditText
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.FragmentManager
import com.google.android.material.textfield.TextInputLayout
import com.mkrs.kolt.base.MKTActivity
import com.mkrs.kolt.base.UserLayout
import com.mkrs.kolt.databinding.ActivityMainBinding
import com.mkrs.kolt.dashboard.home.presentacion.MainMenuFragment
import com.mkrs.kolt.dashboard.home.printer.PrinterConfigFragment
import com.mkrs.kolt.dashboard.home.webservice.ConfigWebServiceBottomSheet
import com.mkrs.kolt.preferences.di.HomeModule
import com.mkrs.kolt.preferences.di.PreferenceModule
import com.mkrs.kolt.preferences.presentation.PreferencesViewModel
import com.mkrs.kolt.utils.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : MKTActivity() {

    private lateinit var binding: ActivityMainBinding


    private val preferencesViewModel by viewModels<PreferencesViewModel> {
        PreferenceModule.providePreferenceVMFactory(
            HomeModule.provideHomePReferences(this, getString(R.string.config_printer))
        )
    }

    companion object {
        private const val HOME_TAG = "HOME"
        private const val VALIDATE_PRINTER = 0
        private const val VALIDATE_WEB_SERVICE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        toSection(R.id.action_home)
        onBackPressedDispatcher.addCallback(this) {
            mainOnBackPress()
        }
        binding.navMainMenu.setOnItemSelectedListener { item ->
            toSection(item.itemId, false)
            return@setOnItemSelectedListener true
        }
        binding.tbMain.title = getString(R.string.title_dashboard)
    }

    fun mainOnBackPress() {
        binding.navMainMenu.visible()
        verifyFragmentManagerBackStackEntryCount()
    }

    private fun verifyFragmentManagerBackStackEntryCount() {
        val homeId = R.id.action_home
        when {
            binding.navMainMenu.menu.findItem(homeId).isChecked && supportFragmentManager.backStackEntryCount <= 0 -> {
                showMessageOut()
            }

            else -> {
                supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                toSection(homeId, false)
            }
        }
    }

    private fun toSection(ItemId: Int, isDifferentFlow: Boolean = false) {

        setBottomNavigationItemCheck(ItemId)
        when (ItemId) {
            R.id.action_print_config -> {
                showPassRequired(
                    resources.getString(R.string.title_configure_printer),
                    VALIDATE_PRINTER
                )
            }

            R.id.action_config -> {
                showPassRequired(
                    resources.getString(R.string.title_configure_web_service),
                    VALIDATE_WEB_SERVICE
                )
            }

            R.id.action_home -> {
                launchMain()
            }
        }
    }

    private fun showPassRequired(dataValidate: String, typeValidate: Int) {

        val passOpen = preferencesViewModel.getString(
            resources.getString(R.string.key_pass_config_open),
            resources.getString(R.string.default_pass_config_open)
        )
        showAlertComplete(
            resources.getString(R.string.title_on_back_press_general),
            resources.getString(R.string.title_configure, dataValidate),
            resources.getString(
                R.string.text_general_app_continue
            ),
            showingOkBtn = true, null,
            resources.getString(R.string.title_on_back_press_cancel),
            showingNoBtn = true,
            noListener = { _, _ -> }, UserLayout.PASS
        )
        val positiveButton = this.alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
        val pass = this.alertDialog.findViewById<EditText>(R.id.tie_pass_config)
        val tilPass = this.alertDialog.findViewById<TextInputLayout>(R.id.til_pass_config)
        pass?.requestFocus()
        pass?.doOnTextChanged { _, _, _, count -> if (count > 0) tilPass?.error = null }
        pass?.setOnEditorActionListener { tvPass, _, keyEvent ->
            if (keyEvent.keyCode == KeyEvent.KEYCODE_ENTER) {
                if (!nextFlow(passOpen, tvPass.text.toString(), typeValidate)) {
                    tilPass?.error = resources.getString(R.string.title_error_password)
                }
            }
            return@setOnEditorActionListener true
        }
        positiveButton.setOnClickListener {
            if (pass?.text.isNullOrEmpty()) {
                tilPass?.error = resources.getString(R.string.title_generic_input_data_required)
            } else {
                if (!nextFlow(passOpen, pass?.text.toString(), typeValidate)) {
                    tilPass?.error = resources.getString(R.string.title_error_password)
                }

            }
        }
    }

    private fun nextFlow(passOpen: String, pass: String, typeValidate: Int): Boolean {
        return if (passOpen == pass) {
            when (typeValidate) {
                VALIDATE_PRINTER -> {
                    this.alertDialog.dismiss()
                    PrinterConfigFragment.newInstance()
                        .show(supportFragmentManager, PrinterConfigFragment.TAG)
                }

                VALIDATE_WEB_SERVICE -> {
                    this.alertDialog.dismiss()
                    ConfigWebServiceBottomSheet.newInstance("", "")
                        .show(supportFragmentManager, ConfigWebServiceBottomSheet.TAG)
                }
            }
            true
        } else {
            false
        }
    }

    private fun showMessageOut() {
        showAlertComplete(resources.getString(R.string.title_on_back_press_general),
            resources.getString(R.string.title_on_back_press_general_message),
            resources.getString(
                R.string.title_on_back_press_out
            ),
            true,
            { _, _ -> this.finishAffinity() },
            resources.getString(R.string.title_on_back_press_cancel),
            true,
            { _, _ -> })
    }

    private fun launchMain(args: Bundle? = null) {
        setFragment(MainMenuFragment.newInstance(), HOME_TAG)
    }

    private fun setBottomNavigationItemCheck(itemId: Int) {
        binding.navMainMenu.menu.findItem(itemId).isChecked = true
    }

}