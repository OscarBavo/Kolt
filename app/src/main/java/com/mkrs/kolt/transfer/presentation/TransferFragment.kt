package com.mkrs.kolt.transfer.presentation

import android.os.Bundle
import android.view.View
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import com.mkrs.kolt.R
import com.mkrs.kolt.base.MKTActivity
import com.mkrs.kolt.base.MKTFragment
import com.mkrs.kolt.base.viewBinding
import com.mkrs.kolt.databinding.FragmentTransferBinding
import com.mkrs.kolt.transfer.di.TransferModule
import com.mkrs.kolt.transfer.domain.models.FinalProductModel
import com.mkrs.kolt.transfer.domain.models.TransferUIState
import com.mkrs.kolt.utils.disable
import com.mkrs.kolt.utils.enable

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
            btnNext.disable()
        }
        initListener()
    }

    private fun initListener() {
        transferBinding.tieTextKeyItem.doOnTextChanged { code, _, _, _ ->
            if (!code.isNullOrEmpty() && code.toString().length == CODE_MAX_LENGTH) {
                hideKeyboard()
                transferViewModel.getCodePT(code.toString())
            }
        }

        transferBinding.tieTextUniqueCode.doOnTextChanged { uniqueCode, _, _, _ ->
            if (!uniqueCode.isNullOrEmpty() && uniqueCode.toString().length == CODE_MAX_LENGTH) {
                hideKeyboard()
                validateUniqueCode(uniqueCode.toString())

            }
        }
    }

    private fun validateUniqueCode(uniqueCode: String) {
        when {
            isOnlyLetters(
                uniqueCode.substring(
                    INIT_CODE_UNIQUE,
                    SECOND_CODE_UNIQUE
                )
            ) -> transferViewModel.getDetailInventory(
                uniqueCode
            )

            isOnlyLetters(
                uniqueCode.substring(
                    INIT_CODE_UNIQUE,
                    FIRST_CODE_UNIQUE
                )
            ) -> transferViewModel.getDetailInventory(
                uniqueCode
            )

            else -> {
                showAlert(
                    getString(R.string.unique_code_error_init),
                    transferBinding.tieTextUniqueCode
                )
            }
        }
    }

    companion object {

        const val CODE_MAX_LENGTH = 7
        const val INIT_CODE_UNIQUE = 0
        const val FIRST_CODE_UNIQUE = 1
        const val SECOND_CODE_UNIQUE = 2

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

    private fun isOnlyLetters(word: String): Boolean {
        val regex = "^[A-Za-z]*$".toRegex()
        return regex.matches(word)
    }
}