package com.mkrs.kolt.dashboard.home.presentacion

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import com.google.android.material.textfield.TextInputLayout
import com.mkrs.kolt.MainActivity
import com.mkrs.kolt.R
import com.mkrs.kolt.transfer.presentation.TransferActivity
import com.mkrs.kolt.base.MKTActivity
import com.mkrs.kolt.base.MKTFragment
import com.mkrs.kolt.base.UserLayout
import com.mkrs.kolt.base.conectivity.webservice.APIKolt
import com.mkrs.kolt.databinding.FragmentMainMenuBinding
import com.mkrs.kolt.preferences.di.HomeModule
import com.mkrs.kolt.preferences.di.PreferenceModule
import com.mkrs.kolt.preferences.presentation.PreferencesViewModel
import com.mkrs.kolt.transfer.presentation.TransferActivity.Companion.USER_TRANSFER

class MainMenuFragment : MKTFragment(R.layout.fragment_main_menu) {

    private lateinit var binding: FragmentMainMenuBinding
    private var urlAPI = ""

    private val preferencesViewModel by activityViewModels<PreferencesViewModel> {
        PreferenceModule.providePreferenceVMFactory(
            HomeModule.provideHomePreferences(requireActivity(), "Impresoras")
        )
    }

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            (activity as? MainActivity)?.mainOnBackPress()
        }
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            MainMenuFragment().apply {
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        activity = requireActivity() as? MKTActivity
        isMainView = true
        isRequiredMessageReturn = true
        // Inflate the layout for this fragment

        binding = FragmentMainMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupBar(resources.getString(R.string.title_dashboard), true)
        initAPI()
        binding.btnTransfer.setOnClickListener {
            consultWorker()

        }
        binding.btnAboutUs.setOnClickListener {
            showAboutUs()
        }
        registerForContextMenu(binding.btnTransfer)
    }

    private fun initAPI() {
        urlAPI = preferencesViewModel.getString(
            getString(R.string.key_pass_web_service),
            getString(R.string.default_web_service)
        )
        APIKolt.update(urlAPI)
    }

    private fun showAboutUs() {
        activity?.let {
            it.showAlertComplete(
                resources.getString(R.string.title_about_us),
                "",
                resources.getString(R.string.generic_ok),
                true, { _, _ -> it.alertDialog.dismiss() }, "", false, null, UserLayout.ABOUT_US
            )
        }
    }

    private fun consultWorker() {
        activity?.let {
            it.showAlertComplete(
                resources.getString(R.string.title_on_back_press_general),
                resources.getString(R.string.title_worker_name),
                resources.getString(
                    R.string.text_general_app_continue
                ),
                showingOkBtn = true, null,
                resources.getString(R.string.title_on_back_press_cancel),
                showingNoBtn = true,
                noListener = { _, _ -> }, UserLayout.COWORKER
            )
            val positiveButton = it.alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
            val pass = it.alertDialog.findViewById<EditText>(R.id.tie_user_config)
            val tilPass = it.alertDialog.findViewById<TextInputLayout>(R.id.til_user_config)
            pass?.requestFocus()
            pass?.doOnTextChanged { _, _, _, count ->
                if (count > 0) {
                    tilPass?.error = null
                }
            }
            pass?.setOnEditorActionListener { tvPass, actionId, keyEvent ->
                if (keyEvent != null && keyEvent.keyCode == KeyEvent.KEYCODE_ENTER) {
                    initTransfer(tvPass.text.toString())

                } else if (actionId == EditorInfo.IME_ACTION_GO || actionId == EditorInfo.IME_ACTION_DONE) {
                    if (tvPass.text.toString().isNotEmpty()) {
                        initTransfer(tvPass.text.toString())
                    }
                }
                return@setOnEditorActionListener true
            }
            positiveButton.setOnClickListener {
                if (pass?.text.isNullOrEmpty()) {
                    tilPass?.error = resources.getString(R.string.title_generic_input_data_required)
                } else {
                    initTransfer(pass?.text.toString())
                }
            }
        }
    }

    private fun initTransfer(user: String) {
        val intent = Intent(requireContext(), TransferActivity::class.java).apply {
            putExtra(USER_TRANSFER, user)
        }
        startActivity(intent)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressedCallback.handleOnBackPressed()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}