package com.mkrs.kolt.transfer.presentation

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.mkrs.kolt.R
import com.mkrs.kolt.base.MKTActivity
import com.mkrs.kolt.base.MKTBottomSheetDialogFragment
import com.mkrs.kolt.dashboard.home.printer.PrinterUIState
import com.mkrs.kolt.dashboard.home.printer.PrinterViewModel
import com.mkrs.kolt.databinding.BottomSheetTransferConfirmationBinding
import com.mkrs.kolt.preferences.di.HomeModule
import com.mkrs.kolt.preferences.di.PreferenceModule
import com.mkrs.kolt.preferences.presentation.PreferencesViewModel
import com.mkrs.kolt.transfer.di.TransferModule
import com.mkrs.kolt.transfer.domain.models.TransferUIState
import com.mkrs.kolt.utils.disable
import com.mkrs.kolt.utils.emptyString
import com.mkrs.kolt.utils.enable
import com.mkrs.kolt.utils.enableOrDisable
import com.mkrs.kolt.utils.isDigitGeneric
import com.mkrs.kolt.utils.toEditable

/**
 * Use the [BottomSheetTransferConfirmation.newInstance] factory method to
 * create an instance of this fragment.
 */
class BottomSheetTransferConfirmation :
    MKTBottomSheetDialogFragment(R.layout.bottom_sheet_transfer_confirmation) {

    private lateinit var binding: BottomSheetTransferConfirmationBinding
    private var activity: MKTActivity? = null
    private val printerViewModel by activityViewModels<PrinterViewModel>()
    private val vmFactory by lazy {
        TransferModule.providesTransferViewModelFactory(requireActivity().application)
    }
    private val transferViewModel: TransferViewModel by activityViewModels { vmFactory }

    private val preferencesViewModel by activityViewModels<PreferencesViewModel> {
        PreferenceModule.providePreferenceVMFactory(
            HomeModule.provideHomePreferences(
                requireActivity(),
                getString(R.string.data_name_printer)
            )
        )
    }
    private var isDemo = false

    private lateinit var bottomSheetListener: PrintingBottomSheetDialogListener

    private val uIStateObserver = Observer<PrinterUIState> { state ->
        when (state) {
            is PrinterUIState.Loading -> activity?.showDialog()
            is PrinterUIState.NoState -> {
                activity?.dismissDialog()
            }

            is PrinterUIState.Printed -> {
                activity?.dismissDialog()
                printerViewModel.printNoState()
                transferViewModel.setNoState()
                questionLabelOk()
            }

            is PrinterUIState.Error -> {
                activity?.dismissDialog()
                printerViewModel.printNoState()
                showAlert(state.message, binding.btnSave)
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
                this.dismiss()
                activity?.alertDialog?.dismiss()
                bottomSheetListener.onPrintingSuccess(getString(R.string.generic_ok))
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
                    printingLabel(transferViewModel.getLabels())
                },
                getString(R.string.generic_no),
                true,
                { _, _ ->
                    it.alertDialog.dismiss()
                    bottomSheetListener.onPrintingSuccess(getString(R.string.generic_ok))
                }
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(
            R.layout.bottom_sheet_transfer_confirmation,
            container,
            false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = BottomSheetTransferConfirmationBinding.bind(view)
        initView()
        initLoading()
        initListener()
        loadLiveData()
    }

    private fun loadLiveData() {
        transferViewModel.transferViewState.observe(viewLifecycleOwner) {
            observeTransferState(it)
        }
        printerViewModel.printerUIState.observe(viewLifecycleOwner, uIStateObserver)
        isDemo = preferencesViewModel.getInt(getString(R.string.key_pass_is_demo), 0) == 1
    }

    private fun observeTransferState(uiState: TransferUIState) {
        when (uiState) {
            is TransferUIState.Loading -> {
                activity?.showDialog()
            }

            is TransferUIState.NoState -> {
                activity?.dismissDialog()
            }

            is TransferUIState.IsEnableTransfer -> {
                activity?.dismissDialog()
                binding.btnSave.enableOrDisable { uiState.isReadyToPrinter }
            }

            is TransferUIState.Printing -> {
                activity?.dismissDialog()
                printingLabel(uiState.labels)
            }

            is TransferUIState.TransferDone -> {
                activity?.dismissDialog()
                transferViewModel.replaceDataPrinter(uiState.date)
            }

            is TransferUIState.Error -> {
                activity?.dismissDialog()
                activity?.showAlertComplete(
                    getString(R.string.generic_information),
                    uiState.msg,
                    getString(R.string.generic_ok),
                    true,
                    { _, _ -> },
                    "",
                    false,
                    null
                )
                transferViewModel.setNoState()
            }

            is TransferUIState.ErrorCustom -> {
                activity?.dismissDialog()
                activity?.showAlert(uiState.msg, binding.root)
                transferViewModel.setNoState()
            }

            else -> {
                activity?.dismissDialog()
                transferViewModel.setNoState()
            }
        }
    }

    private fun printingLabel(labels: MutableList<String>) {
        val ipPort = printerViewModel.getDataPrinter(preferencesViewModel, resources)
        labels.forEach {
            printerViewModel.printTest(ipPort[0], ipPort[1].toInt(), it)
        }
    }

    private fun initListener() {
        binding.tieTextTeamWorker.doOnTextChanged { text, _, _, _ ->
            if (!text.isNullOrEmpty()) {
                transferViewModel.saveCoWorker(text.toString())
            } else {
                transferViewModel.saveReadyPrinter(false, TransferViewModel.ReadyPrinter.COWORKER)
            }
        }

        binding.tieTextPerforadora.doOnTextChanged { perforadora, _, _, _ ->
            if (!perforadora.isNullOrEmpty()) {
                transferViewModel.savePerforadora(perforadora = perforadora.toString())
            } else {
                transferViewModel.saveReadyPrinter(
                    false,
                    TransferViewModel.ReadyPrinter.PERFORADORA
                )
            }
        }

        binding.tieTextPerforadora.setOnEditorActionListener { perf, actionId, keyEvent ->
            if (keyEvent != null && keyEvent.keyCode == KeyEvent.KEYCODE_ENTER) {
                if (perf.text.toString().isNotEmpty()) {
                    transferViewModel.saveReadyPrinter(
                        true,
                        TransferViewModel.ReadyPrinter.PERFORADORA
                    )
                }
            } else if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_GO) {
                if (perf.text.toString().isNotEmpty()) {
                    transferViewModel.saveReadyPrinter(
                        true,
                        TransferViewModel.ReadyPrinter.PERFORADORA
                    )
                }
            }
            return@setOnEditorActionListener false
        }

        binding.tieTextStandardPack.doOnTextChanged { stdPack, _, _, _ ->
            validateStdPack(stdPack.toString())
        }

        binding.btnSave.setOnClickListener {
            transferViewModel.createTransfer(isDemo)
        }
    }

    private fun validateStdPack(stdPack: String) {
        if (stdPack.isEmpty()) {
            binding.tieTextStandardPack.requestFocus()
            transferViewModel.saveReadyPrinter(false, TransferViewModel.ReadyPrinter.LABELS)
        } else if (stdPack == ZERO) {
            binding.btnSave.disable()
            transferViewModel.saveReadyPrinter(false, TransferViewModel.ReadyPrinter.LABELS)
        } else {
            if (isDigitGeneric(stdPack)) {
                createNote(stdPack)
            }
        }

    }

    private fun createNote(stdPack: String) {
        transferViewModel.saveReadyPrinter(false, TransferViewModel.ReadyPrinter.LABELS)
        val labels = if (stdPack.toDouble() == 0.0) {
            ""
        } else {
            transferViewModel.createNotePrinter(stdPack.toDouble())
        }
        binding.tvNote.text = getString(R.string.label_note_printer, labels)
    }

    private fun initView() {
        binding.apply {
            transferViewModel.initReadyPrinter()
            tvTextNoPart.text = transferViewModel.finalProductModel.itemCode
            tvTextBatch.text = transferViewModel.finalProductModel.mnfSerial
            tvPt.text = transferViewModel.finalProductModel.itemName
            tvTextOrder.text = transferViewModel.finalProductModel.uPedidoProg
            tvTextPieces.text = transferViewModel.finalProductModel.pieces
            tieTextStandardPack.text = transferViewModel.finalProductModel.pieces.toEditable()
            tvTextUniqueCode.text = transferViewModel.finalProductModel.codeUnique
            tvNote.text = getString(R.string.label_note_printer, emptyString())
            createNote(transferViewModel.finalProductModel.pieces)
            btnSave.disable()
            if (transferViewModel.getCoworker().isNotEmpty()) {
                tieTextTeamWorker.text = transferViewModel.getCoworker().toEditable()
                tieTextTeamWorker.selectAll()
                transferViewModel.saveReadyPrinter(true, TransferViewModel.ReadyPrinter.COWORKER)
            }
            tilTeamWorker.enable()
            tilTeamWorker.requestFocus()
        }
    }

    private fun initLoading() {
        activity = requireActivity() as? MKTActivity
        activity?.initDialog()
    }

    companion object {
        const val TAG = "BOTTOMSHEETTRANSFERCONFIRMATION"
        const val ZERO = "0"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment BottomSheetTransferConfirmation.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(
            fragmentManager: FragmentManager,
            bottomSheetDialogListener: PrintingBottomSheetDialogListener
        ) {
            val dialog = BottomSheetTransferConfirmation()
            dialog.setListener(bottomSheetDialogListener)
            dialog.show(fragmentManager, TAG)
            BottomSheetTransferConfirmation().apply {

            }
        }
    }

    private fun setListener(listener: PrintingBottomSheetDialogListener) {
        bottomSheetListener = listener
    }
}