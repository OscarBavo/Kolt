package com.mkrs.kolt.dashboard.home.printer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.mkrs.kolt.R
import com.mkrs.kolt.base.MKTActivity
import com.mkrs.kolt.base.MKTBottomSheetDialogFragment
import com.mkrs.kolt.databinding.FragmentPrinterConfigBinding
import com.mkrs.kolt.preferences.di.HomeModule
import com.mkrs.kolt.preferences.di.PreferenceModule
import com.mkrs.kolt.preferences.presentation.PreferencesViewModel
import com.mkrs.kolt.utils.disable
import com.mkrs.kolt.utils.enable
import com.mkrs.kolt.utils.toEditable
import java.util.regex.Matcher
import java.util.regex.Pattern


/**
 * Use the [PrinterConfigFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PrinterConfigFragment : MKTBottomSheetDialogFragment(R.layout.fragment_printer_config) {

    private lateinit var binding: FragmentPrinterConfigBinding
    private val printerViewModel by activityViewModels<PrinterViewModel>()
    private var activity: MKTActivity? = null
    private val preferencesViewModel by activityViewModels<PreferencesViewModel> {
        PreferenceModule.providePreferenceVMFactory(
            HomeModule.provideHomePReferences(requireActivity(), "Impresoras")
        )
    }

    private val uIStateObserver = Observer<PrinterUIState> { state ->
        when (state) {
            is PrinterUIState.Loading -> activity?.showDialog()
            is PrinterUIState.NoState -> {
                activity?.dismissDialog()
            }

            is PrinterUIState.Printed -> {
                activity?.dismissDialog()
                showAlert("Impresion correcta", binding.btnTestPrinter)
            }

            is PrinterUIState.Error -> {
                activity?.dismissDialog()
                showAlert(state.message, binding.btnTestPrinter)
            }

        }
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
        loadViewModel()
        initView()

        initLoading()
    }

    private fun initLoading() {
        activity = requireActivity() as? MKTActivity
        activity?.initDialog()
    }

    private fun loadViewModel() {
        printerViewModel.printerUIState.observe(viewLifecycleOwner, uIStateObserver)
    }

    private fun initView() {
        binding.apply {
            val ipPort = printerViewModel.getDataPrinter(preferencesViewModel, resources)

            tieTextIpPrinter.text = ipPort[0].toEditable()
            tieTextPortPrinter.text = ipPort[1].toEditable()
            btnCancel.setOnClickListener {
                dismiss()
            }

            btnSave.setOnClickListener {
                saveData()
            }

            btnTestPrinter.setOnClickListener {
                val ip = printerViewModel.getDataPrinter(preferencesViewModel, resources)[0]
                val port =
                    printerViewModel.getDataPrinter(preferencesViewModel, resources)[1].toInt()
                printerViewModel.printTest(ip, port, resources.getString(R.string.label_test))
            }

            tieTextIpPrinter.setOnEditorActionListener { textIp, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
                    if (!validateIP(textIp.text.toString())) {
                        tilIpPrinter.error =
                            resources.getString(R.string.title_generic_input_data_required)
                    } else {
                        saveData()
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
        activity?.showDialog()
        if (validateData()) {
            binding.apply {
                btnSave.disable()
                tilIpPrinter.error =
                    resources.getString(R.string.title_generic_input_data_required)
                tilIpPrinter.error =
                    resources.getString(R.string.title_generic_input_data_required)
            }
            activity?.dismissDialog()
            return
        }
        var ipPortPrinter = binding.tieTextIpPrinter.text.toString()
        ipPortPrinter += resources.getString(R.string.title_split_ip)
        ipPortPrinter += binding.tieTextPortPrinter.text.toString()

        preferencesViewModel.saveString(
            resources.getString(R.string.key_value_printer),
            ipPortPrinter
        )
        activity?.dismissDialog()
        showMessageDone(binding.btnSave)
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