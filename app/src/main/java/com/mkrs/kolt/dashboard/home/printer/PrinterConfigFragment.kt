package com.mkrs.kolt.dashboard.home.printer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import com.mkrs.kolt.R
import com.mkrs.kolt.base.MKTBottomSheetDialogFragment
import com.mkrs.kolt.databinding.FragmentPrinterConfigBinding
import com.mkrs.kolt.preferences.di.HomeModule
import com.mkrs.kolt.preferences.di.PreferenceModule
import com.mkrs.kolt.preferences.presentation.PreferencesViewModel
import com.mkrs.kolt.utils.disable
import com.mkrs.kolt.utils.enable
import java.util.regex.Matcher
import java.util.regex.Pattern


/**
 * Use the [PrinterConfigFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PrinterConfigFragment : MKTBottomSheetDialogFragment(R.layout.fragment_printer_config) {

    private lateinit var binding: FragmentPrinterConfigBinding
    private val preferencesViewModel by activityViewModels<PreferencesViewModel> {
        PreferenceModule.providePreferenceVMFactory(
            HomeModule.provideHomePReferences(requireActivity(), "Impresoras")
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_printer_config, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentPrinterConfigBinding.bind(view)
        initView()
    }

    private fun initView() {
        binding.apply {
            val ipPort = preferencesViewModel.getString(
                resources.getString(R.string.key_value_printer),
                resources.getString(R.string.default_value_printer)
            ).split(resources.getString(R.string.title_split_ip))

            tieTextIpPrinter.setText(ipPort[0])
            tieTextPortPrinter.setText(ipPort[1])
            btnCancel.setOnClickListener {
                dismiss()
            }

            btnSave.setOnClickListener {
                saveData()
            }

            tieTextIpPrinter.setOnEditorActionListener { textIp, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    if (!validateIP(textIp.text.toString())) {
                        tilIpPrinter.error =
                            resources.getString(R.string.title_generic_input_data_required)
                    } else {
                        tieTextPortPrinter.requestFocus()
                        tilIpPrinter.error = null
                    }
                    return@setOnEditorActionListener true
                } else return@setOnEditorActionListener false
            }

            tieTextPortPrinter.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    saveData()
                }
                return@setOnEditorActionListener true
            }

            tieTextIpPrinter.doOnTextChanged { ip, _, _, _ ->
                if (!validateIP(ip.toString())) {
                    tilIpPrinter.error =
                        resources.getString(R.string.title_generic_input_data_required)
                    btnSave.disable()
                } else {
                    tilIpPrinter.error = null
                    btnSave.enable()
                }
            }

            tieTextPortPrinter.doOnTextChanged { _, _, _, count ->
                if (count > 0) {
                    tilPortPrinter.error = null
                    btnSave.enable()
                } else {
                    tilPortPrinter.error =
                        resources.getString(R.string.title_generic_input_data_required)
                    btnSave.disable()
                }
            }
        }

    }

    private fun validateData(): Boolean {
        return binding.tieTextIpPrinter.text.toString()
            .isEmpty() || binding.tieTextPortPrinter.text.toString().isEmpty()
    }

    private fun saveData() {
        hideKeyboard()
        if (validateData()) {
            binding.apply {
                btnSave.disable()
                tilIpPrinter.error =
                    resources.getString(R.string.title_generic_input_data_required)
                tilIpPrinter.error =
                    resources.getString(R.string.title_generic_input_data_required)
            }
            return
        }
        var ipPortPrinter = binding.tieTextIpPrinter.text.toString()
        ipPortPrinter += resources.getString(R.string.title_split_ip)
        ipPortPrinter += binding.tieTextPortPrinter.text.toString()

        preferencesViewModel.saveString(
            resources.getString(R.string.key_value_printer),
            ipPortPrinter
        )
        showAlert(
            binding.btnSave,
            resources.getString(R.string.update_info_generic),
            resources.getString(R.string.text_general_accept)
        ) { this.dismiss() }
    }

    private fun validateIP(ip: String): Boolean {
        val ipConfig: Pattern = Pattern.compile(
            "((25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9])\\.(25[0-5]|2[0-4]"
                    + "[0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]"
                    + "[0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}"
                    + "|[1-9][0-9]|[0-9]))"
        )
        val matcher: Matcher = ipConfig.matcher(ip)
        return matcher.matches()

    }

    companion object {

        const val TAG = "PRINTER_CONFIG"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment PrinterConfigFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
            PrinterConfigFragment().apply {

            }
    }
}