package com.mkrs.kolt.dashboard.home.webservice

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.mkrs.kolt.R
import com.mkrs.kolt.base.MKTBottomSheetDialogFragment
import com.mkrs.kolt.base.conectivity.webservice.APIKolt
import com.mkrs.kolt.databinding.FragmentBottomSheetConfigWebServiceBinding
import com.mkrs.kolt.preferences.di.HomeModule
import com.mkrs.kolt.preferences.di.PreferenceModule
import com.mkrs.kolt.preferences.presentation.PreferencesViewModel
import com.mkrs.kolt.utils.toEditable

/**
 * A simple [Fragment] subclass.
 * Use the [ConfigWebServiceBottomSheet.newInstance] factory method to
 * create an instance of this fragment.
 */
class ConfigWebServiceBottomSheet :
    MKTBottomSheetDialogFragment(R.layout.fragment_bottom_sheet_config_web_service) {

    private val preferencesViewModel by activityViewModels<PreferencesViewModel> {
        PreferenceModule.providePreferenceVMFactory(
            HomeModule.provideHomePReferences(requireActivity(), "Impresoras")
        )
    }
    private lateinit var binding: FragmentBottomSheetConfigWebServiceBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bottom_sheet_config_web_service, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentBottomSheetConfigWebServiceBinding.bind(view)
        initView()
    }

    private fun initView() {
        binding.btnCancel.setOnClickListener {
            this.dismiss()
        }
        binding.apply {

            tieTextIpWebService.text = preferencesViewModel.getString(
                getString(R.string.key_pass_web_service),
                getString(R.string.default_web_service)
            ).toEditable()

            tieTextIpWebService.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    validateData()
                }
                return@setOnEditorActionListener true
            }


            btnSave.setOnClickListener {
                validateData()
            }
        }
    }

    private fun validateData() {
        hideKeyboard()
        if (binding.tieTextIpWebService.text.toString().isEmpty()) {
            binding.tilIpWebService.error = getString(R.string.error_generic)
        } else {
            preferencesViewModel.saveString(
                getString(R.string.key_pass_web_service),
                binding.tieTextIpWebService.text.toString()
            )
            APIKolt.update(binding.tieTextIpWebService.text.toString())
        }
        showMessageDone(binding.btnSave)
    }

    companion object {

        const val TAG = "WEB_SERVICE_CONFIG"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ConfigWebServiceBottomSheet().apply {

            }
    }
}