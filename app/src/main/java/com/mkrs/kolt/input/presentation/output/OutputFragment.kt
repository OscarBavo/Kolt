package com.mkrs.kolt.input.presentation.output

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.mkrs.kolt.R
import com.mkrs.kolt.base.MKTActivity
import com.mkrs.kolt.base.MKTFragment
import com.mkrs.kolt.dashboard.home.printer.PrinterUIState
import com.mkrs.kolt.dashboard.home.printer.PrinterViewModel
import com.mkrs.kolt.databinding.FragmentOutputBinding
import com.mkrs.kolt.input.di.OutputModule
import com.mkrs.kolt.preferences.di.HomeModule
import com.mkrs.kolt.preferences.di.PreferenceModule
import com.mkrs.kolt.preferences.presentation.PreferencesViewModel
import com.mkrs.kolt.utils.CONSTANST
import com.mkrs.kolt.utils.CONSTANST.Companion.LIST_EMPTY
import com.mkrs.kolt.utils.CONSTANST.Companion.REFERENCE_MAX_LENGTH
import com.mkrs.kolt.utils.disable
import com.mkrs.kolt.utils.emptyString
import com.mkrs.kolt.utils.emptyStringEditable
import com.mkrs.kolt.utils.enable
import com.mkrs.kolt.utils.enableOrDisable
import com.mkrs.kolt.utils.isCode
import com.mkrs.kolt.utils.isCodeFill
import com.mkrs.kolt.utils.isDigit
import com.mkrs.kolt.utils.isLetter
import com.mkrs.kolt.utils.toEditable

class OutputFragment : MKTFragment(R.layout.fragment_output) {

    private lateinit var binding: FragmentOutputBinding
    private var isDemo = false
    private val printerViewModel by activityViewModels<PrinterViewModel>()

    private val preferencesViewModel by activityViewModels<PreferencesViewModel> {
        PreferenceModule.providePreferenceVMFactory(
            HomeModule.provideHomePreferences(requireActivity(), "Impresoras")
        )
    }

    private val vmFactory by lazy {
        OutputModule.providesOutputViewModelFactory(requireActivity().application)
    }

    private val outputViewModel: OutputViewModel by activityViewModels { vmFactory }


    private val uIStateObserver = Observer<PrinterUIState> { state ->
        when (state) {
            is PrinterUIState.Loading -> activity?.showDialog()
            is PrinterUIState.NoState -> {
                activity?.dismissDialog()
            }

            is PrinterUIState.Printed -> {
                activity?.dismissDialog()
                printerViewModel.printNoState()
                outputViewModel.setNoState()
                questionLabelOk()
            }

            is PrinterUIState.Error -> {
                activity?.dismissDialog()
                printerViewModel.printNoState()
                showAlert(state.message, binding.btnOutputSave)
                questionLabelOk()
            }

        }
    }

    private fun questionLabelOk() {
        activity?.showAlertComplete(
            getString(R.string.generic_information),
            getString(R.string.success_label_question),
            getString(R.string.generic_yes),
            true,
            { _, _ ->
                activity?.alertDialog?.dismiss()
                clearView()
                activity?.onBackPressed()
            },
            getString(R.string.generic_no),
            true,
            { _, _ ->
                activity?.alertDialog?.dismiss()
                showRetrievePrinterLabel()
            })
    }

    private fun showRetrievePrinterLabel() {
        activity?.let {
            it.showAlertComplete(
                getString(R.string.generic_information),
                getString(R.string.retrieve_printer_label),
                getString(R.string.generic_yes),
                true, { _, _ ->
                    it.alertDialog.dismiss()
                    printingLabel(outputViewModel.getLabelsOutput())
                },
                getString(R.string.generic_no),
                true,
                { _, _ ->
                    it.alertDialog.dismiss()
                    activity?.onBackPressed()
                }
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity = requireActivity() as? MKTActivity
        binding = FragmentOutputBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupBar(resources.getString(R.string.btn_title_out), true)
        initDialog()
        initView()
        initListener()
        loadLiveData()
        binding.tieOutputReferData.requestFocus()
    }

    private fun loadLiveData() {
        outputViewModel.outputViewState.observe(viewLifecycleOwner) {
            observerOutputState(it)
        }

        isDemo = preferencesViewModel.getInt(getString(R.string.key_pass_is_demo), 0) == 1
        printerViewModel.printerUIState.observe(viewLifecycleOwner, uIStateObserver)

        outputViewModel.getDate(isDemo)
    }


    private fun observerOutputState(state: OutputUiState?) {
        this.hideKeyboard()
        when (state) {
            is OutputUiState.Loading -> showDialog()
            is OutputUiState.NoState -> dismissDialog()
            is OutputUiState.Error -> {
                dismissDialog()
                showAlert(msg = state.msg, view = binding.btnOutputNext)
                outputViewModel.setNoState()
            }

            is OutputUiState.GetDate -> {
                dismissDialog()
                binding.tvOutputDateData.text = state.date
                binding.tieOutputReferData.enable(focus = true)
            }

            is OutputUiState.OutputTotalItems -> {
                dismissDialog()
                binding.tvOutputLabelsData.text = state.total.toString()
                clearData()
            }


            is OutputUiState.SaveReference -> {
                dismissDialog()
                binding.btnOutputClean.enable()
                binding.tieOutputReferData.setTextAppearance(R.style.input_text_ready)
                binding.tieOutputKeyPtData.enable(focus = true)
            }

            is OutputUiState.ErrorReference -> {
                dismissDialog()
                binding.tilOutputRefer.error = getString(R.string.title_error_empty_reference)
                binding.tieOutputReferData.requestFocus()
            }

            is OutputUiState.SaveOutPutKeyPT -> {
                dismissDialog()
                binding.tieOutputKeyPtData.setTextAppearance(R.style.input_text_ready)
                outputViewModel.getDate(isDemo, isGetRFC = true)
            }

            is OutputUiState.ErrorOutPutKeyPT -> {
                dismissDialog()
                binding.tilOutputKeyPt.error = state.message
                binding.tieOutputKeyPtData.requestFocus()
            }

            is OutputUiState.GetRFC -> {
                dismissDialog()
                binding.tieOutputKeyPtData.setTextAppearance(R.style.input_text_ready)
                binding.tvOutputReceiveData.text = state.rfc
                binding.tieOutputUniqueCodeData.enable(focus = true)
            }

            is OutputUiState.QuantityAvailable -> {
                dismissDialog()
                binding.tieOutputUniqueCodeData.setTextAppearance(R.style.input_text_ready)
                binding.tieOutputTo.text = state.quantity.toEditable()
                binding.tieOutputPerfoData.enable(focus = true)
            }

            is OutputUiState.ErrorQuantityAvailable -> {
                dismissDialog()
                binding.tilOutputUniqueCode.error = getString(R.string.not_found_data_code_p_msg)
                binding.tieOutputUniqueCodeData.enable(focus = true)
            }

            is OutputUiState.SaveOutputPerfo -> {
                dismissDialog()
                this.activity?.runOnUiThread {
                    binding.tieOutputPerfoData.setTextAppearance(R.style.input_text_ready)
                    binding.tieOutputCoworkerData.enable(focus = true)
                }
            }

            is OutputUiState.ErrorOutputPerfo -> {
                dismissDialog()
                binding.tilOutputPerfo.error = getString(R.string.title_error_perfo)
                binding.tieOutputPerfoData.enable(focus = true)
                this.hideKeyboard()
            }

            is OutputUiState.SaveOutputCoworker -> {
                dismissDialog()
                binding.tieOutputCoworkerData.setTextAppearance(R.style.input_text_ready)
                binding.tieOutputTo.enable(focus = true)
                binding.tieOutputTo.requestFocus()
            }

            is OutputUiState.ErrorOutputCoWorker -> {
                dismissDialog()
                binding.tilOutputCoworker.error = getString(R.string.title_worker_name)
                binding.tieOutputCoworkerData.enable(focus = true)
            }

            is OutputUiState.ErrorOutputQuantityUpper -> {
                dismissDialog()
                binding.tilOutputTo.error =
                    getString(R.string.title_error_quantity_upper, state.quantity)
                binding.tieOutputTo.setTextAppearance(R.style.input_text)
                binding.tieOutputTo.enable(focus = true)
            }


            is OutputUiState.ErrorOutputQuantityLowerZero -> {
                dismissDialog()
                binding.tilOutputTo.error = getString(R.string.title_error_quantity, state.quantity)
                binding.tieOutputTo.setTextAppearance(R.style.input_text)
                binding.tieOutputTo.enable(focus = true)
            }

            is OutputUiState.ErrorOutputQuantity -> {
                dismissDialog()
                binding.tilOutputTo.error = getString(R.string.title_error_quantity_empty)
                binding.tieOutputTo.setTextAppearance(R.style.input_text)
                binding.tieOutputTo.enable(focus = true)
            }

            is OutputUiState.SaveOutputQuantity -> {
                dismissDialog()
                binding.tieOutputTo.setTextAppearance(R.style.input_text_ready)
                binding.btnOutputNext.enable()
                binding.btnOutputSave.enable()
            }

            is OutputUiState.ErrorOutputCreateToPrinter -> {
                dismissDialog()
                showAlert(state.error, binding.btnOutputSave)
            }

            is OutputUiState.OutputCreateToPrinter -> {
                dismissDialog()
                activity?.showAlertComplete(
                    getString(R.string.generic_information),
                    getString(R.string.title_created_out, state.dateTime),
                    getString(R.string.generic_ok),
                    true,
                    { _, _ -> printingLabel(outputViewModel.getLabelsOutput()) },
                    emptyString(),
                    false,
                    { _, _ -> })

            }

            else -> {
                dismissDialog()
            }
        }
    }

    private fun initListener() {
        //region reference
        binding.tieOutputReferData.doOnTextChanged { code, _, _, _ ->
            outputViewModel.setReadyToSave(false)
            binding.tilOutputRefer.error = emptyStringEditable()
            if (code.toString().length == REFERENCE_MAX_LENGTH) {
                outputViewModel.saveReference(code.toString())
            }
        }
        binding.tieOutputReferData.setOnEditorActionListener { textView, actionId, keyEvent ->
            outputViewModel.setReadyToSave(false)
            if (keyEvent != null && keyEvent.keyCode == KeyEvent.KEYCODE_ENTER) {
                outputViewModel.saveReference(textView.text.toString())
            } else if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE) {
                outputViewModel.saveReference(textView.text.toString())
            }
            return@setOnEditorActionListener false
        }
        //endregion

        //region keycode
        binding.tieOutputKeyPtData.doOnTextChanged { code, _, _, count ->
            binding.tilOutputKeyPt.error = emptyStringEditable()
            if (count > 0)
                validateKeyItem(code = code.toString())

            if (!code.isNullOrEmpty()) {
                validateKeyItem(code = code.toString())
            }
        }

        binding.tieOutputKeyPtData.setOnEditorActionListener { code, actionId, keyEvent ->
            outputViewModel.setReadyToSave(false)
            if (keyEvent != null && keyEvent.keyCode == KeyEvent.KEYCODE_ENTER) {
                validateKeyItem(code.text.toString())
            } else if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE) {
                validateKeyItem(code.text.toString())
            }
            return@setOnEditorActionListener false
        }
        //endregion

        //region keyunique
        binding.tieOutputUniqueCodeData.doOnTextChanged { code, _, _, _ ->
            validateTextUniqueCode(uniqueCode = code.toString())
        }

        binding.tieOutputUniqueCodeData.setOnEditorActionListener { code, actionId, keyEvent ->
            outputViewModel.setReadyToSave(false)
            if (keyEvent != null && keyEvent.keyCode == KeyEvent.KEYCODE_ENTER) {
                validateTextUniqueCode(code.text.toString())
            } else if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE) {
                validateTextUniqueCode(code.text.toString())
            }
            return@setOnEditorActionListener true
        }
        //endregion

        //region perforadora
        binding.tieOutputPerfoData.doOnTextChanged { _, _, _, _ ->
            binding.tilOutputPerfo.error = emptyStringEditable()
        }

        binding.tieOutputPerfoData.setOnEditorActionListener { code, actionId, keyEvent ->
            outputViewModel.setReadyToSave(false)
            if (keyEvent != null && keyEvent.keyCode == KeyEvent.KEYCODE_ENTER) {
                outputViewModel.savePerfo(code.text.toString())
            } else if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE) {
                outputViewModel.savePerfo(code.text.toString())
            }
            return@setOnEditorActionListener false
        }
        //endregion

        //region coworker
        binding.tieOutputCoworkerData.doOnTextChanged { _, _, _, _ ->
            binding.tilOutputCoworker.error = emptyStringEditable()
        }

        binding.tieOutputCoworkerData.setOnEditorActionListener { code, actionId, keyEvent ->
            outputViewModel.setReadyToSave(false)
            if (keyEvent != null && keyEvent.keyCode == KeyEvent.KEYCODE_ENTER) {
                outputViewModel.saveCoWorker(code.text.toString())
            } else if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE) {
                outputViewModel.saveCoWorker(code.text.toString())
            }
            return@setOnEditorActionListener false
        }
        //endregion

        //region quantity
        binding.tieOutputTo.doOnTextChanged { code, _, _, count ->
            if (count > 0) {
                binding.tilOutputTo.error = emptyStringEditable()
            } else if (!code.isNullOrEmpty()) {
                outputViewModel.saveQuantity(code.toString())
            }
        }

        binding.tieOutputTo.setOnEditorActionListener { code, actionId, keyEvent ->
            if (keyEvent != null && keyEvent.keyCode == KeyEvent.KEYCODE_ENTER) {
                outputViewModel.saveQuantity(code.text.toString())
            } else if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE) {
                outputViewModel.saveQuantity(code.text.toString())
            }
            return@setOnEditorActionListener false
        }
        //endregion

        binding.btnOutputClean.setOnClickListener {
            clearData()
        }

        binding.btnOutputNext.setOnClickListener {
            outputViewModel.nextOutData()
        }

        binding.btnOutputSave.setOnClickListener {
            if (outputViewModel.getIsReadyToSave()) {
                outputViewModel.saveOutData()
                binding.tvOutputLabelsData.text = outputViewModel.getItemsOutput().toString()
                outputViewModel.saveOutput(isDemo)
            }
        }

    }
    private fun initView() {
        binding.tvOutputEmitData.text = getString(R.string.title_output_kolt)
        binding.tvOutputLabelsData.text = CONSTANST.EMPTY_DATA

        binding.tieOutputKeyPtData.disable()
        binding.tieOutputUniqueCodeData.disable()
        binding.tieOutputPerfoData.disable()
        binding.tieOutputCoworkerData.disable()
        binding.tieOutputTo.disable()

        binding.btnOutputClean.disable()
        binding.btnOutputNext.disable()
        binding.btnOutputSave.disable()
    }

    private fun validateKeyItem(code: String) {
        outputViewModel.setReadyToSave(false)
        if (code.isEmpty()) {
            binding.tilOutputKeyPt.error = getString(R.string.title_empty_pt_message)
            binding.tieOutputKeyPtData.requestFocus()
        } else if (isLetter(code)) {
            binding.tilOutputKeyPt.error = getString(R.string.label_error_digits_item_code)
            binding.tieOutputKeyPtData.requestFocus()
        } else if (code.length == CONSTANST.CODE_MAX_LENGTH && isDigit(code)) {
            outputViewModel.getCodePT(code)
        }
    }

    private fun validateTextUniqueCode(uniqueCode: String) {
        if (uniqueCode.isNotEmpty()) {
            outputViewModel.setReadyToSave(false)
            if (uniqueCode.length == CONSTANST.CODE_UNIQUE_MAX_LENGTH) {
                hideKeyboard()
                validateUniqueCode(uniqueCode)
            } else {
                if (!isCodeFill(uniqueCode)) {
                    hideKeyboard()
                    binding.tilOutputUniqueCode.error =
                        getString(R.string.unique_code_error_init)
                    showAlert(
                        getString(R.string.unique_code_error_init),
                        binding.tilOutputUniqueCode
                    )
                } else {
                    binding.tilOutputUniqueCode.error = emptyStringEditable()
                }
            }
        } else {
            binding.tilOutputUniqueCode.error = emptyStringEditable()
        }
    }

    private fun validateUniqueCode(uniqueCode: String) {
        if (isCode(uniqueCode)) {
            outputViewModel.setReadyToSave(false)
            outputViewModel.saveKeyUnique(uniqueCode, isDemo)
        } else {
            outputViewModel.setReadyToSave(false)
            binding.tilOutputUniqueCode.error = getString(R.string.unique_code_error_init)
            showAlert(
                getString(R.string.unique_code_error_init),
                binding.tilOutputUniqueCode
            )
        }
    }

    private fun clearData() {

        binding.tieOutputKeyPtData.text = emptyStringEditable()
        binding.tieOutputUniqueCodeData.text = emptyStringEditable()
        binding.tieOutputUniqueCodeData.text = emptyStringEditable()
        binding.tieOutputTo.text = emptyStringEditable()

        binding.tieOutputKeyPtData.error = null
        binding.tieOutputUniqueCodeData.error = null
        binding.tieOutputPerfoData.error = null
        binding.tieOutputCoworkerData.error = null
        binding.tieOutputUniqueCodeData.error = null
        binding.tieOutputTo.error = null


        binding.tieOutputKeyPtData.setTextAppearance(R.style.input_text)
        binding.tieOutputUniqueCodeData.setTextAppearance(R.style.input_text)
        binding.tieOutputUniqueCodeData.setTextAppearance(R.style.input_text)
        binding.tieOutputTo.setTextAppearance(R.style.input_text)

        outputViewModel.resetData()

        binding.btnOutputSave.enableOrDisable { outputViewModel.getItemsOutput() > LIST_EMPTY }
        binding.btnOutputNext.disable()

        binding.tieOutputKeyPtData.enable(focus = true)
    }

    private fun clearView() {
        clearData()
        outputViewModel.resetDefaultValues()
        binding.tieOutputReferData.text = emptyStringEditable()
        binding.tieOutputReferData.setTextAppearance(R.style.input_text)
        binding.tieOutputPerfoData.setTextAppearance(R.style.input_text)
        binding.tieOutputCoworkerData.setTextAppearance(R.style.input_text)
        binding.tvOutputLabelsData.text = "0"
        binding.tieOutputPerfoData.text = emptyStringEditable()
        binding.tieOutputCoworkerData.text = emptyStringEditable()
        outputViewModel.setNoState()
        binding.tilOutputRefer.enable(focus = true)
    }

    private fun printingLabel(labels: MutableList<String>) {
        val ipPort = printerViewModel.getDataPrinter(preferencesViewModel, resources)
        labels.forEach {
            printerViewModel.printTest(ipPort[0], ipPort[1].toInt(), it)
        }
    }
}