package com.mkrs.kolt.input.presentation.input

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import com.mkrs.kolt.R
import com.mkrs.kolt.base.MKTActivity
import com.mkrs.kolt.base.MKTFragment
import com.mkrs.kolt.databinding.FragmentInputBinding
import com.mkrs.kolt.input.di.InputModule
import com.mkrs.kolt.input.domain.models.InputModel
import com.mkrs.kolt.preferences.di.HomeModule
import com.mkrs.kolt.preferences.di.PreferenceModule
import com.mkrs.kolt.preferences.presentation.PreferencesViewModel
import com.mkrs.kolt.utils.CONSTANST.Companion.CODE_MAX_LENGTH
import com.mkrs.kolt.utils.CONSTANST.Companion.CODE_UNIQUE_MAX_LENGTH
import com.mkrs.kolt.utils.disable
import com.mkrs.kolt.utils.emptyStringEditable
import com.mkrs.kolt.utils.enable
import com.mkrs.kolt.utils.isCode
import com.mkrs.kolt.utils.isCodeFill
import com.mkrs.kolt.utils.isDigit
import com.mkrs.kolt.utils.isLetter

class InputFragment : MKTFragment(R.layout.fragment_input) {

    private lateinit var binding: FragmentInputBinding
    private var isDemo = false
    private val preferencesViewModel by activityViewModels<PreferencesViewModel> {
        PreferenceModule.providePreferenceVMFactory(
            HomeModule.provideHomePreferences(requireActivity(), "Impresoras")
        )
    }

    private val vmFactory by lazy {
        InputModule.providesInputViewModelFactory(requireActivity().application)
    }

    private val inputViewModel: InputViewModel by activityViewModels { vmFactory }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity = requireActivity() as? MKTActivity
        isMainView = true

        binding = FragmentInputBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initDialog()
        initView()
        initListener()
        loadLiveData()
        binding.tieReferData.requestFocus()
    }

    private fun loadLiveData() {
        inputViewModel.inOutViewState.observe(viewLifecycleOwner) {
            observerInputState(it)
        }
        isDemo = preferencesViewModel.getInt(getString(R.string.key_pass_is_demo), 0) == 1
    }

    private fun observerInputState(state: InOutputUiState?) {
        when (state) {
            is InOutputUiState.Loading -> showDialog()
            is InOutputUiState.NoState -> dismissDialog()
            is InOutputUiState.ErrorSaveKeyItem -> {
                dismissDialog()
                binding.tieKeyItemData.error = state.msg
                inputViewModel.setNoState()
            }

            is InOutputUiState.ErrorReference -> {
                dismissDialog()
                binding.tilRefer.error = getString(R.string.title_error_empty_reference)
                binding.tieReferData.requestFocus()
                inputViewModel.setNoState()
            }

            is InOutputUiState.ErrorRegexBatchRoll -> {
                dismissDialog()
                binding.tilBatchRoll.error = getString(R.string.title_error_bad_batch, state.errorBatch)
                binding.tilBatchRoll.requestFocus()
                inputViewModel.setNoState()
            }

            is InOutputUiState.SaveReference -> {
                dismissDialog()
                binding.btnClean.enable()
                binding.tieKeyItemData.enable()
                binding.tieReferData.setTextAppearance(R.style.input_text_ready)
                binding.tieKeyItemData.requestFocus()
                inputViewModel.setNoState()
            }

            is InOutputUiState.SaveKeyItem -> {
                dismissDialog()
                binding.tieUniqueCodeData.enable()
                binding.tieKeyItemData.setTextAppearance(R.style.input_text_ready)
                binding.tieUniqueCodeData.requestFocus()
                inputViewModel.setNoState()
            }

            is InOutputUiState.SaveKeyUnique -> {
                dismissDialog()
                binding.tiePiecesData.enable()
                binding.tieUniqueCodeData.setTextAppearance(R.style.input_text_ready)
                binding.tiePiecesData.requestFocus()
                inputViewModel.setNoState()
            }

            is InOutputUiState.SavePieces -> {
                dismissDialog()
                binding.tieBatchRollData.enable()
                binding.tiePiecesData.setTextAppearance(R.style.input_text_ready)
                binding.tieBatchRollData.requestFocus()
                inputViewModel.setNoState()
            }

            is InOutputUiState.Error -> {
                dismissDialog()
                showAlert(msg = state.msg, view = binding.btnNext)
                inputViewModel.setNoState()
            }

            is InOutputUiState.SaveBatchRoll -> {
                dismissDialog()
                binding.btnNext.enable()
                binding.btnNext.requestFocus()
                inputViewModel.setNoState()
            }

            is InOutputUiState.SaveAll -> {
                dismissDialog()
                binding.tvNoBatchData.text = state.totalBatch.toString()
                binding.tieKeyItemData.requestFocus()
                resetView()
                binding.btnSave.enable()
                inputViewModel.setNoState()
            }

            is InOutputUiState.ErrorBatchRoll -> {
                dismissDialog()
                showMessageAddOrDeleteBatch(state.inputModel)
                inputViewModel.setNoState()
            }

            is InOutputUiState.CreateInReady -> {
                dismissDialog()
                resetView()
                createdInReady(state.docNum)
                inputViewModel.setNoState()
            }

            else -> {
                dismissDialog()
            }
        }
    }

    private fun createdInReady(docNum: Int) {
        activity?.let {
            it.showAlertComplete(
                getString(R.string.generic_information),
                getString(R.string.title_created_in, docNum.toString()),
                getString(R.string.generic_ok),
                true, { _, _ -> resetIn() }, "", false, null
            )
        }
    }

    private fun resetIn() {
        activity?.let {
            it.showAlertComplete(
                getString(R.string.generic_information),
                getString(R.string.title_create_in_again),
                getString(R.string.generic_yes),
                true,
                { _, _ -> resetAllView() },
                getString(R.string.generic_no),
                true,
                { _, _ -> it.onBackPressed() }
            )
        }
    }

    private fun resetAllView() {
        resetView()
        binding.btnSave.disable()
        binding.btnNext.disable()
        binding.btnClean.disable()
        binding.tieReferData.text = emptyStringEditable()
        binding.tieReferData.enable()
        binding.tieKeyItemData.disable()
    }

    private fun showMessageAddOrDeleteBatch(inputModel: InputModel) {
        activity?.let {
            it.showAlertComplete(
                getString(R.string.generic_information),
                getString(
                    R.string.title_message_batch_repeated,
                    inputModel.batchRoll,
                    inputModel.Qty.toString()
                ),
                getString(R.string.generic_yes),
                true,
                { _, _ ->
                    inputViewModel.saveBatchRoll(binding.tieBatchRollData.text.toString(), true)
                },
                getString(R.string.generic_no),
                true,
                { _, _ ->
                    binding.tieBatchRollData.error = getString(R.string.title_batch_repeated)
                    binding.tieBatchRollData.requestFocus()
                })
        }
    }

    private fun initListener() {

        binding.btnClean.setOnClickListener {
            inputViewModel.resetData()
            resetView()
        }

        binding.btnSave.setOnClickListener {
            inputViewModel.saveIn(isDemo)
        }

        binding.tieReferData.doOnTextChanged { code, _, _, _ ->
            binding.tilRefer.error = null
            inputViewModel.saveReference(code.toString())
        }

        binding.tieReferData.setOnEditorActionListener { textView, actionId, keyEvent ->
            if (keyEvent != null && keyEvent.keyCode == KeyEvent.KEYCODE_ENTER) {
                inputViewModel.saveReference(textView.text.toString())
            } else if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE) {
                inputViewModel.saveReference(textView.text.toString())
            }
            return@setOnEditorActionListener false
        }

        binding.tieKeyItemData.doOnTextChanged { code, _, _, _ ->
            validateKeyItem(code.toString())
        }

        binding.tieKeyItemData.setOnEditorActionListener { textView, actionId, keyEvent ->
            if (keyEvent != null && keyEvent.keyCode == KeyEvent.KEYCODE_ENTER) {
                validateKeyItem(textView.text.toString())
            } else if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE) {
                validateKeyItem(textView.text.toString())
            }
            return@setOnEditorActionListener false
        }

        binding.tieUniqueCodeData.doOnTextChanged { uniqueCode, _, _, _ ->
            validateTextUniqueCode(uniqueCode = uniqueCode.toString())
        }

        binding.tieUniqueCodeData.setOnEditorActionListener { textView, actionId, keyEvent ->
            if (keyEvent != null && keyEvent.keyCode == KeyEvent.KEYCODE_ENTER) {
                validateTextUniqueCode(uniqueCode = textView.text.toString())
            } else if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE) {
                validateTextUniqueCode(uniqueCode = textView.text.toString())
            }
            return@setOnEditorActionListener false
        }

        binding.tiePiecesData.setOnEditorActionListener { textView, actionId, keyEvent ->
            if (keyEvent != null && keyEvent.keyCode == KeyEvent.KEYCODE_ENTER) {
                inputViewModel.savePieces(textView.text.toString().toDouble())
            } else if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE) {
                inputViewModel.savePieces(textView.text.toString().toDouble())
            }
            return@setOnEditorActionListener false
        }
        binding.tieBatchRollData.setOnEditorActionListener { textView, actionId, keyEvent ->
            if (keyEvent != null && keyEvent.keyCode == KeyEvent.KEYCODE_ENTER) {
                inputViewModel.saveBatchRoll(textView.text.toString())
            } else if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE) {
                inputViewModel.saveBatchRoll(textView.text.toString())
            }
            return@setOnEditorActionListener false
        }

        binding.btnNext.setOnClickListener {
            if (binding.tieReferData.text.toString().isEmpty()) {
                binding.tieReferData.requestFocus()
            } else if (binding.tieKeyItemData.text.toString().isEmpty()) {
                binding.tieKeyItemData.requestFocus()
            } else if (binding.tieUniqueCodeData.text.toString().isEmpty()) {
                binding.tieUniqueCodeData.requestFocus()
            } else if (binding.tiePiecesData.text.toString().isEmpty()) {
                binding.tiePiecesData.requestFocus()
            } else if (binding.tieBatchRollData.text.toString().isEmpty()) {
                binding.tiePiecesData.requestFocus()
            } else if (inputViewModel.isBatchRepeated(binding.tieBatchRollData.text.toString()) != null) {
                inputViewModel.saveBatchRoll(binding.tieBatchRollData.text.toString(), false)
            } else {
                inputViewModel.saveData()
            }
        }
    }

    private fun resetView() {
        binding.tieKeyItemData.text = emptyStringEditable()
        binding.tieKeyItemData.setTextAppearance(R.style.input_text)
        binding.tieUniqueCodeData.text = emptyStringEditable()
        binding.tieUniqueCodeData.setTextAppearance(R.style.input_text)
        binding.tiePiecesData.text = emptyStringEditable()
        binding.tiePiecesData.setTextAppearance(R.style.input_text)
        binding.tieBatchRollData.text = emptyStringEditable()
        binding.tieBatchRollData.setTextAppearance(R.style.input_text)

        binding.tieReferData.disable()
        binding.tieUniqueCodeData.disable()
        binding.tiePiecesData.disable()
        binding.tieBatchRollData.disable()
        binding.tieKeyItemData.requestFocus()
    }

    private fun validateKeyItem(code: String) {
        if (code.isEmpty()) {
            binding.tilKeyItem.error = emptyStringEditable()
            binding.tieKeyItemData.requestFocus()
        } else if (isLetter(code)) {
            binding.tilKeyItem.error = getString(R.string.label_error_digits_item_code)
            binding.tieKeyItemData.requestFocus()
        } else if (code.length == CODE_MAX_LENGTH && isDigit(code)) {
            inputViewModel.getCodePT(code, isDemo)
        }
    }

    private fun initView() {
        binding.apply {
            btnClean.disable()
            btnNext.disable()
            btnSave.disable()
            tieReferData.enable()
            tieKeyItemData.enable()
            tieUniqueCodeData.enable()
            tiePiecesData.enable()
            tieBatchRollData.enable()
            tvNoBatchData.text = inputViewModel.getItemsAdded().toString()
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
                    binding.tilUniqueCode.error =
                        getString(R.string.unique_code_error_init)
                    showAlert(
                        getString(R.string.unique_code_error_init),
                        binding.tieUniqueCodeData
                    )
                } else {
                    binding.tilUniqueCode.error = null
                }
            }
        } else {
            binding.tilUniqueCode.error = emptyStringEditable()
        }
    }

    private fun validateUniqueCode(uniqueCode: String) {
        if (isCode(uniqueCode)) {
            inputViewModel.saveKeyUnique(uniqueCode)
        } else {
            binding.tilUniqueCode.error = getString(R.string.unique_code_error_init)
            showAlert(
                getString(R.string.unique_code_error_init),
                binding.tieUniqueCodeData
            )
        }
    }
}