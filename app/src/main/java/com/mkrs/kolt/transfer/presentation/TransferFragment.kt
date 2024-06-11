package com.mkrs.kolt.transfer.presentation

import android.os.Bundle
import android.view.View
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.mkrs.kolt.R
import com.mkrs.kolt.base.MKTActivity
import com.mkrs.kolt.base.MKTFragment
import com.mkrs.kolt.base.viewBinding
import com.mkrs.kolt.dashboard.home.printer.PrinterUIState
import com.mkrs.kolt.dashboard.home.printer.PrinterViewModel
import com.mkrs.kolt.databinding.FragmentTransferBinding
import com.mkrs.kolt.preferences.di.HomeModule
import com.mkrs.kolt.preferences.di.PreferenceModule
import com.mkrs.kolt.preferences.presentation.PreferencesViewModel
import com.mkrs.kolt.transfer.di.TransferModule
import com.mkrs.kolt.transfer.domain.models.FinalProductModel
import com.mkrs.kolt.transfer.domain.models.TransferUIState
import com.mkrs.kolt.utils.disable
import com.mkrs.kolt.utils.enable
import com.mkrs.kolt.utils.toEditable

/**
 * Use the [TransferFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TransferFragment : MKTFragment(R.layout.fragment_transfer) {

    private val transferBinding by viewBinding(FragmentTransferBinding::bind)
    private val vmFactory by lazy {
        TransferModule.providesTransferViewModelFactory(requireActivity().application)
    }
    private val transferViewModel: TransferViewModel by activityViewModels { vmFactory }

    private val preferencesViewModel by activityViewModels<PreferencesViewModel> {
        PreferenceModule.providePreferenceVMFactory(
            HomeModule.provideHomePReferences(requireActivity(), "Impresoras")
        )
    }

    private val printerViewModel by activityViewModels<PrinterViewModel>()

    private val uIStateObserver = Observer<PrinterUIState> { state ->
        when (state) {
            is PrinterUIState.Loading -> activity?.showDialog()
            is PrinterUIState.NoState -> {
                activity?.dismissDialog()
            }

            is PrinterUIState.Printed -> {
                activity?.dismissDialog()
                showAlert(getString(R.string.success_printer), transferBinding.btnNext)
                showDetailPT()
            }

            is PrinterUIState.Error -> {
                activity?.dismissDialog()
                showAlert(state.message, transferBinding.btnNext)
            }

        }
    }

    private fun showDetailPT() {
        transferViewModel.setNoState()
        activity?.let {
            BottomSheetTransferConfirmation.newInstance()
                .show(it.supportFragmentManager, BottomSheetTransferConfirmation.TAG)
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity = requireActivity() as MKTActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupBar(resources.getString(R.string.title_fragment_transfer), true)
        initDialog()
        initView()
        loadLiveData()
    }

    private fun loadLiveData() {
        transferViewModel.transferViewState.observe(viewLifecycleOwner) {
            observeTransferState(it)
        }
        printerViewModel.printerUIState.observe(viewLifecycleOwner, uIStateObserver)
    }

    private fun observeTransferState(uiState: TransferUIState) {
        when (uiState) {
            is TransferUIState.Loading -> {
                showDialog()
            }

            is TransferUIState.NoState -> {
                dismissDialog()
            }

            is TransferUIState.SuccessCode -> {
                dismissDialog()
                transferBinding.tieTextKeyItem.disable()
                transferBinding.tieTextUniqueCode.enable()
                transferBinding.btnClean.enable()
                transferBinding.tieTextUniqueCode.requestFocus()
                transferViewModel.setNoState()
            }

            is TransferUIState.NoExistsPT -> {
                dismissDialog()
                showAlert(uiState.msg, transferBinding.tieTextKeyItem)
                transferBinding.tieTextKeyItem.text = emptyString()
                transferBinding.tieTextKeyItem.requestFocus()
                transferViewModel.setNoState()
            }

            is TransferUIState.NoExistsDetail -> {
                dismissDialog()
                showAlert(uiState.msg, transferBinding.tieTextUniqueCode)
                transferBinding.tieTextUniqueCode.text = emptyString()
                transferBinding.tieTextUniqueCode.enable()
                transferViewModel.setNoState()
            }

            is TransferUIState.SuccessDetail -> {
                dismissDialog()
                loadDetails(uiState.detail)
                transferViewModel.setNoState()
            }

            is TransferUIState.Error -> {
                dismissDialog()
                showAlert(uiState.msg, transferBinding.btnNext)
                transferViewModel.setNoState()
            }

            is TransferUIState.UpperQuantity -> {
                dismissDialog()
                hideKeyboard()
                transferBinding.tvTextTotal.text = uiState.quantity
                showError(uiState.typeQuantity)
                transferBinding.btnNext.isEnabled = false
                transferViewModel.setNoState()
            }

            is TransferUIState.EqualsQuantity -> {
                dismissDialog()
                hideKeyboard()
                transferBinding.tvTextTotal.text = uiState.quantity
                transferBinding.btnNext.isEnabled = true
                transferViewModel.setNoState()
            }

            is TransferUIState.AddQuantity -> {
                dismissDialog()
                transferBinding.tvTextTotal.text = uiState.quantity
                transferViewModel.setNoState()
            }

            is TransferUIState.SendToPrinter -> {
                dismissDialog()
                hideKeyboard()
                createConectionPrinter(uiState.label)
            }
        }
    }

    private fun createConectionPrinter(label: String) {
        val ipPort = printerViewModel.getDataPrinter(preferencesViewModel, resources)

        printerViewModel.printTest(ipPort[0], ipPort[1].toInt(), label)
    }

    private fun showError(typeQuantity: TransferViewModel.TypeQuantity) {
        when (typeQuantity) {
            TransferViewModel.TypeQuantity.DONE -> transferBinding.tieTextDoneProduct.requestFocus()

            TransferViewModel.TypeQuantity.DIFF -> transferBinding.tieTextDifferent.requestFocus()

            TransferViewModel.TypeQuantity.REJECT -> transferBinding.tieTextReject.requestFocus()

            TransferViewModel.TypeQuantity.SCRAP -> transferBinding.tieTextScrap.requestFocus()
        }
    }

    private fun loadDetails(detail: FinalProductModel) {
        transferBinding.apply {
            tvBatchRollData.text = detail.mnfSerial
            tvPiecesData.text = detail.quantity
            tvBatchDetailData.text = detail.suppCatNum

            tieTextDoneProduct.enable()
            tieTextReject.enable()
            tieTextDifferent.enable()
            tieTextScrap.enable()

            tieTextDoneProduct.requestFocus()

        }

    }

    private fun initView() {
        transferBinding.apply {
            tilKeyItem.enable()
            tieTextKeyItem.requestFocus()
            btnClean.disable()
            // btnNext.disable()
        }
        initListener()
    }

    private fun initListener() {
        transferBinding.tieTextKeyItem.doOnTextChanged { code, _, _, _ ->
            if (code.isNullOrEmpty()) {
                transferBinding.tilKeyItem.error = emptyString()
                transferBinding.tieTextKeyItem.requestFocus()
            } else if (isLetter(code.toString())) {
                transferBinding.tilKeyItem.error = "solo ingresar números"
                transferBinding.tieTextKeyItem.requestFocus()
            } else if (code.length == CODE_MAX_LENGTH && isDigit(code.toString())) {
                transferViewModel.getCodePT(code = code.toString())
            }
        }

        transferBinding.tieTextUniqueCode.doOnTextChanged { uniqueCode, _, _, _ ->
            if (!uniqueCode.isNullOrEmpty()) {
                if (uniqueCode.toString().length == CODE_MAX_LENGTH) {
                    hideKeyboard()
                    validateUniqueCode(uniqueCode.toString())
                } else {
                    if (!isCodeFill(uniqueCode.toString())) {
                        hideKeyboard()
                        transferBinding.tilUniqueCode.error =
                            getString(R.string.unique_code_error_init)
                        showAlert(
                            getString(R.string.unique_code_error_init),
                            transferBinding.tieTextUniqueCode
                        )
                    } else {
                        transferBinding.tilUniqueCode.error = null
                    }
                }
            } else {
                transferBinding.tilUniqueCode.error = emptyString()
            }
        }

        transferBinding.tieTextDoneProduct.doOnTextChanged { qaDone, _, _, _ ->
            if (!qaDone.isNullOrEmpty()) {
                transferViewModel.saveQuantityDone(qaDone.toString())
            }
        }

        transferBinding.tieTextReject.doOnTextChanged { qaRJ, _, _, _ ->
            if (!qaRJ.isNullOrEmpty()) {
                transferViewModel.saveQuantityReject(qaRJ.toString())
            }
        }
        transferBinding.tieTextDifferent.doOnTextChanged { qaDiff, _, _, _ ->
            if (!qaDiff.isNullOrEmpty()) {
                transferViewModel.saveQuantityDiff(qaDiff.toString())
            }
        }

        transferBinding.tieTextScrap.doOnTextChanged { qaScrap, _, _, _ ->
            if (!qaScrap.isNullOrEmpty()) {
                transferViewModel.saveQuantitySCRAP(qaScrap.toString())
            }
        }

        transferBinding.btnClean.setOnClickListener {
            resetView()
        }

        transferBinding.btnNext.setOnClickListener {
            transferViewModel.updateDataFinalProduct(getString(R.string.label_printer_one))
        }

    }

    private fun resetView() {
        transferViewModel.saveQuantityDone(VERIFY_TOTAL_OK.toString())
        transferViewModel.saveQuantityDiff(VERIFY_TOTAL_OK.toString())
        transferViewModel.saveQuantityReject(VERIFY_TOTAL_OK.toString())
        transferViewModel.saveQuantitySCRAP(VERIFY_TOTAL_OK.toString())
        transferBinding.tieTextUniqueCode.text = emptyString()
        transferBinding.tieTextKeyItem.text = emptyString()
        transferBinding.tieTextDoneProduct.text = emptyString()
        transferBinding.tieTextDifferent.text = emptyString()
        transferBinding.tieTextReject.text = emptyString()
        transferBinding.tieTextScrap.text = emptyString()
        transferBinding.tvPiecesData.text = emptyString()
        transferBinding.tvBatchRollData.text = emptyString()
        transferBinding.tvBatchDetailData.text = emptyString()
        transferBinding.tvTextTotal.text = "".toEditable()
        transferViewModel.resetFinalProductModel()
        transferBinding.tieTextUniqueCode.disable()
        transferBinding.tieTextDoneProduct.disable()
        transferBinding.tieTextDifferent.disable()
        transferBinding.tieTextReject.disable()
        transferBinding.tieTextScrap.disable()
        transferBinding.btnClean.disable()
        transferBinding.btnNext.disable()
        transferBinding.tieTextKeyItem.enable()
        transferBinding.tieTextKeyItem.requestFocus()

    }

    private fun validateUniqueCode(uniqueCode: String) {
        if (isCode(uniqueCode)) {
            transferViewModel.getDetailInventory(uniqueCode)
        } else {
            transferBinding.tilUniqueCode.error = getString(R.string.unique_code_error_init)
            showAlert(
                getString(R.string.unique_code_error_init),
                transferBinding.tieTextUniqueCode
            )
        }

    }

    companion object {

        const val CODE_MAX_LENGTH = 7
        const val VERIFY_TOTAL_OK = 0.0

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment TransferFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
            TransferFragment().apply {

            }
    }

    private fun isCode(word: String): Boolean {
        val regex = "^[A-Za-z]{1,2}[\\d]{5}\$".toRegex()
        return regex.matches(word)
    }

    private fun isCodeFill(word: String): Boolean {
        val regex = "^[A-Za-z]{1,2}[\\d]*\$".toRegex()
        return regex.matches(word)
    }

    private fun isLetter(word: String): Boolean {
        val regex = "^[A-Za-z]*$".toRegex()
        return regex.matches(word)
    }

    private fun isDigit(word: String): Boolean {
        val regex = "^[\\d]{7}$".toRegex()
        return regex.matches(word)
    }
}