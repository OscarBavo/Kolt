package com.mkrs.kolt.transfer.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import com.mkrs.kolt.R
import com.mkrs.kolt.base.MKTActivity
import com.mkrs.kolt.base.MKTBottomSheetDialogFragment
import com.mkrs.kolt.databinding.BottomSheetTransferConfirmationBinding
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
    private val vmFactory by lazy {
        TransferModule.providesTransferViewModelFactory(requireActivity().application)
    }
    private val transferViewModel: TransferViewModel by activityViewModels { vmFactory }

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
                binding.btnSave.enableOrDisable { uiState.isReadyToPrinter }
            }

            else -> {
                transferViewModel.setNoState()
            }
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

        binding.tieTextStandardPack.doOnTextChanged { stdPack, _, _, _ ->
            validateStdPack(stdPack.toString())
        }
    }

    private fun validateStdPack(stdPack: String) {
        if (stdPack.isNullOrEmpty()) {
            binding.tieTextStandardPack.requestFocus()
            transferViewModel.saveReadyPrinter(false, TransferViewModel.ReadyPrinter.LABELS)
        } else if (stdPack == "0") {
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

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment BottomSheetTransferConfirmation.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
            BottomSheetTransferConfirmation().apply {

            }
    }
}