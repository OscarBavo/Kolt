package com.mkrs.kolt.transfer.presentation

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import com.mkrs.kolt.MainActivity
import com.mkrs.kolt.R
import com.mkrs.kolt.base.MKTActivity
import com.mkrs.kolt.base.MKTFragment
import com.mkrs.kolt.base.UserLayout
import com.mkrs.kolt.base.viewBinding
import com.mkrs.kolt.databinding.FragmentTransferBinding
import com.mkrs.kolt.preferences.di.HomeModule
import com.mkrs.kolt.preferences.di.PreferenceModule
import com.mkrs.kolt.preferences.presentation.PreferencesViewModel
import com.mkrs.kolt.transfer.di.TransferModule
import com.mkrs.kolt.transfer.domain.models.FinalProductModel
import com.mkrs.kolt.transfer.domain.models.TransferUIState
import com.mkrs.kolt.utils.CONSTANST.Companion.CODE_MAX_LENGTH
import com.mkrs.kolt.utils.CONSTANST.Companion.CODE_UNIQUE_MAX_LENGTH
import com.mkrs.kolt.utils.disable
import com.mkrs.kolt.utils.emptyStringEditable
import com.mkrs.kolt.utils.enable
import com.mkrs.kolt.utils.isCode
import com.mkrs.kolt.utils.isCodeFill
import com.mkrs.kolt.utils.isDigit
import com.mkrs.kolt.utils.isLetter
import com.mkrs.kolt.utils.toEditable

/**
 * Use the [TransferFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TransferFragment : MKTFragment(R.layout.fragment_transfer),
    PrintingBottomSheetDialogListener {

    private val transferBinding by viewBinding(FragmentTransferBinding::bind)
    private var isDemo = false
    private val vmFactory by lazy {
        TransferModule.providesTransferViewModelFactory(requireActivity().application)
    }
    private val transferViewModel: TransferViewModel by activityViewModels { vmFactory }

    private val preferencesViewModel by activityViewModels<PreferencesViewModel> {
        PreferenceModule.providePreferenceVMFactory(
            HomeModule.provideHomePreferences(requireActivity(), "Impresoras")
        )
    }


    private fun showDetailPT() {
        transferViewModel.setNoState()
        activity?.let {
            BottomSheetTransferConfirmation.newInstance(it.supportFragmentManager, this)
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity = requireActivity() as MKTActivity
        activity?.let { act ->
            act.intent.extras?.let { ext ->
                transferViewModel.saveCoWorker(ext.getString(TransferActivity.USER_TRANSFER) ?: "")
            }
        }
        isDemo = preferencesViewModel.getInt(getString(R.string.key_pass_is_demo), 0) == 1
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupBar(resources.getString(R.string.title_fragment_transfer), true)
        initDialog()
        initView()
        initListener()
        loadLiveData()
        transferBinding.tieTextKeyItem.requestFocus()
    }

    private fun loadLiveData() {
        transferViewModel.transferViewState.observe(viewLifecycleOwner) {
            observeTransferState(it)
        }
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
                transferBinding.tilKeyItem.disable()
                transferBinding.tieTextKeyItem.setTextAppearance(R.style.input_text_ready)
                transferBinding.tieTextUniqueCode.enable()
                transferBinding.btnClean.enable()
                transferBinding.tieTextUniqueCode.requestFocus()
                transferViewModel.setNoState()
            }

            is TransferUIState.NoExistsPT -> {
                dismissDialog()
                showAlert(uiState.msg, transferBinding.tieTextKeyItem)
                transferBinding.tieTextKeyItem.text = emptyStringEditable()
                transferBinding.tieTextKeyItem.requestFocus()
                transferViewModel.setNoState()
            }

            is TransferUIState.NoExistsDetail -> {
                dismissDialog()
                showAlert(uiState.msg, transferBinding.tieTextUniqueCode)
                transferBinding.tieTextUniqueCode.text = emptyStringEditable()
                transferBinding.tieTextUniqueCode.enable()
                transferViewModel.setNoState()
            }

            is TransferUIState.SuccessDetail -> {
                dismissDialog()
                loadDetails(detail = uiState.detail)
                transferViewModel.setNoState()
            }

            is TransferUIState.Error -> {
                dismissDialog()
                showAlert(msg = uiState.msg, view = transferBinding.btnNext)
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
                transferBinding.btnNext.isEnabled = false
                transferBinding.tvTextTotal.text = uiState.quantity
                transferViewModel.setNoState()
            }

            else -> {}
        }
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

            tieTextUniqueCode.setTextAppearance(R.style.input_text_ready)
            tieTextDoneProduct.requestFocus()
            tieTextDoneProduct.text = detail.quantity.toEditable()
            tieTextDoneProduct.selectAll()


        }

    }

    private fun initView() {
        transferBinding.apply {
            btnClean.disable()
            tieTextDoneProduct.disable()
            tieTextReject.disable()
            tieTextDifferent.disable()
            tieTextScrap.disable()
            tilKeyItem.enable()
        }

    }

    private fun initListener() {
        transferBinding.tieTextKeyItem.doOnTextChanged { codePT, _, _, _ ->
            validateKeyItem(codePT.toString())
        }

        transferBinding.tieTextKeyItem.setOnEditorActionListener { textKeyPT, actionId, keyEvent ->
            if (keyEvent != null && keyEvent.keyCode == KeyEvent.KEYCODE_ENTER) {
                validateKeyItem(textKeyPT.toString())
            } else if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE) {
                validateKeyItem(textKeyPT.toString())
            }
            return@setOnEditorActionListener false
        }

        transferBinding.tieTextUniqueCode.doOnTextChanged { uniqueCode, _, _, _ ->
            validateTextUniqueCode(uniqueCode = uniqueCode.toString())
        }

        transferBinding.tieTextUniqueCode.setOnEditorActionListener { uniqueCode, actionId, keyEvent ->
            if (keyEvent != null && keyEvent.keyCode == KeyEvent.KEYCODE_ENTER) {
                validateTextUniqueCode(uniqueCode.toString())
            } else if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE) {
                validateTextUniqueCode(uniqueCode.toString())
            }

            return@setOnEditorActionListener false
        }

        transferBinding.tieTextDoneProduct.doOnTextChanged { qaDone, _, _, _ ->
            if (!qaDone.isNullOrEmpty()) {
                transferViewModel.saveQuantityDone(qaDone.toString())
            } else {
                transferViewModel.saveQuantityDone("0")
            }
        }
        transferBinding.tieTextDoneProduct.setOnEditorActionListener { doneQuantity, actionId, keyEvent ->
            if (keyEvent != null && keyEvent.keyCode == KeyEvent.KEYCODE_ENTER) {
                if (!doneQuantity.text.isNullOrEmpty()) {
                    transferViewModel.saveQuantityDone(doneQuantity.text.toString())
                } else {
                    transferViewModel.saveQuantityDone("0")
                }
            } else if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE) {
                if (!doneQuantity.text.isNullOrEmpty()) {
                    transferViewModel.saveQuantityDone(doneQuantity.text.toString())
                } else {
                    transferViewModel.saveQuantityDone("0")
                }
            }
            return@setOnEditorActionListener false
        }

        transferBinding.tieTextReject.doOnTextChanged { qaRJ, _, _, _ ->
            if (!qaRJ.isNullOrEmpty()) {
                transferViewModel.saveQuantityReject(qaRJ.toString())
            } else {
                transferViewModel.saveQuantityReject("0")
            }
        }

        transferBinding.tieTextReject.setOnEditorActionListener { qaRJ, actionId, keyEvent ->
            if (keyEvent != null && keyEvent.keyCode == KeyEvent.KEYCODE_ENTER) {
                if (!qaRJ.text.isNullOrEmpty()) {
                    transferViewModel.saveQuantityReject(qaRJ.text.toString())
                } else {
                    transferViewModel.saveQuantityReject("0")
                }
            } else if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE) {
                if (!qaRJ.text.isNullOrEmpty()) {
                    transferViewModel.saveQuantityReject(qaRJ.text.toString())
                } else {
                    transferViewModel.saveQuantityReject("0")
                }
            }
            return@setOnEditorActionListener false
        }

        transferBinding.tieTextDifferent.doOnTextChanged { qaDiff, _, _, _ ->
            if (!qaDiff.isNullOrEmpty()) {
                transferViewModel.saveQuantityDiff(qaDiff.toString())
            } else {
                transferViewModel.saveQuantityDiff("0")
            }
        }

        transferBinding.tieTextDifferent.setOnEditorActionListener { qaDiff, actionId, keyEvent ->
            if (keyEvent != null && keyEvent.keyCode == KeyEvent.KEYCODE_ENTER) {
                if (!qaDiff.text.isNullOrEmpty()) {
                    transferViewModel.saveQuantityDiff(qaDiff.text.toString())
                } else {
                    transferViewModel.saveQuantityDiff("0")
                }
            } else if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE) {
                if (!qaDiff.text.isNullOrEmpty()) {
                    transferViewModel.saveQuantityDiff(qaDiff.text.toString())
                } else {
                    transferViewModel.saveQuantityDiff("0")
                }
            }
            return@setOnEditorActionListener false
        }

        transferBinding.tieTextScrap.doOnTextChanged { qaScrap, _, _, _ ->
            if (!qaScrap.isNullOrEmpty()) {
                transferViewModel.saveQuantitySCRAP(qaScrap.toString())
            } else {
                transferViewModel.saveQuantitySCRAP("0")
            }
        }

        transferBinding.tieTextScrap.setOnEditorActionListener { qaScrap, actionId, keyEvent ->
            if (keyEvent != null && keyEvent.keyCode == KeyEvent.KEYCODE_ENTER) {
                if (!qaScrap.text.isNullOrEmpty()) {
                    transferViewModel.saveQuantitySCRAP(qaScrap.text.toString())
                } else {
                    transferViewModel.saveQuantitySCRAP("0")
                }
            } else if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE) {
                if (!qaScrap.text.isNullOrEmpty()) {
                    transferViewModel.saveQuantitySCRAP(qaScrap.text.toString())
                } else {
                    transferViewModel.saveQuantitySCRAP("0")
                }
            }
            return@setOnEditorActionListener false
        }

        transferBinding.btnClean.setOnClickListener {
            resetView()
        }

        transferBinding.btnNext.setOnClickListener {
            transferViewModel.updateDataFinalProduct()
            showDetailPT()
        }

    }

    private fun validateTextUniqueCode(uniqueCode: String) {
        if (uniqueCode.isNotEmpty()) {
            if (uniqueCode.length == CODE_UNIQUE_MAX_LENGTH) {
                hideKeyboard()
                validateUniqueCode(uniqueCode)
            } else {
                if (!isCodeFill(uniqueCode)) {
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
            transferBinding.tilUniqueCode.error = emptyStringEditable()
        }
    }

    private fun validateKeyItem(codePT: String) {
        if (codePT.isEmpty()) {
            transferBinding.tilKeyItem.error = emptyStringEditable()
            transferBinding.tieTextKeyItem.requestFocus()
        } else if (isLetter(codePT)) {
            transferBinding.tilKeyItem.error = getString(R.string.label_error_digits_item_code)
            transferBinding.tieTextKeyItem.requestFocus()
        } else if (codePT.length == CODE_MAX_LENGTH && isDigit(codePT)) {
            transferViewModel.getCodePT(codePT = codePT, isDemo)
        }
    }

    private fun resetView() {
        transferViewModel.resetQuantity()
        transferViewModel.setNoState()
        transferBinding.tieTextUniqueCode.text = emptyStringEditable()
        transferBinding.tieTextKeyItem.text = emptyStringEditable()
        transferBinding.tieTextDoneProduct.text = emptyStringEditable()
        transferBinding.tieTextDifferent.text = emptyStringEditable()
        transferBinding.tieTextReject.text = emptyStringEditable()
        transferBinding.tieTextScrap.text = emptyStringEditable()
        transferBinding.tvPiecesData.text = emptyStringEditable()
        transferBinding.tvBatchRollData.text = emptyStringEditable()
        transferBinding.tvBatchDetailData.text = emptyStringEditable()
        transferBinding.tvTextTotal.text = emptyStringEditable()
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
            transferViewModel.getDetailInventory(uniqueCode, isDemo)
        } else {
            transferBinding.tilUniqueCode.error = getString(R.string.unique_code_error_init)
            showAlert(
                getString(R.string.unique_code_error_init),
                transferBinding.tieTextUniqueCode
            )
        }

    }

    companion object {

        @JvmStatic
        fun newInstance() =
            TransferFragment().apply {

            }
    }

    override fun onPrintingSuccess(result: String) {
        if (result == getString(R.string.generic_ok)) {
            activity?.showAlertComplete(
                getString(R.string.title_transfer_complete),
                getString(R.string.title_add_another_transfer),
                getString(R.string.generic_yes),
                true,
                { _, _ ->
                    resetView()
                },
                getString(R.string.generic_no),
                true,
                { _, _ ->
                    startActivity(Intent(requireContext(), MainActivity::class.java))
                    activity?.finishAffinity()

                }, UserLayout.NO_LAYOUT
            )
        } else {
            showAlert(getString(R.string.title_transfer_complete), transferBinding.btnNext)
        }
    }

    override fun onDismissWithOutAnswer() {

    }


}