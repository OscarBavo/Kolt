package com.mkrs.kolt.input.presentation

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.activityViewModels
import com.mkrs.kolt.R
import com.mkrs.kolt.base.MKTActivity
import com.mkrs.kolt.base.MKTFragment
import com.mkrs.kolt.databinding.FragmentInputBinding
import com.mkrs.kolt.input.di.InputModule
import com.mkrs.kolt.preferences.di.HomeModule
import com.mkrs.kolt.preferences.di.PreferenceModule
import com.mkrs.kolt.preferences.presentation.PreferencesViewModel
import com.mkrs.kolt.utils.disable
import com.mkrs.kolt.utils.enable

class InputFragment : MKTFragment(R.layout.fragment_input) {

    private lateinit var binding: FragmentInputBinding
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
    }

    private fun observerInputState(state: InOutputUiState?) {
        when (state) {
            is InOutputUiState.Loading -> showDialog()
            is InOutputUiState.NoState -> dismissDialog()
            is InOutputUiState.SaveReference -> {
                binding.btnClean.enable()
                binding.tieKeyItemData.enable()
                binding.tieKeyItemData.requestFocus()
            }

            is InOutputUiState.SaveKeyItem -> {
                binding.tieUniqueCodeData.enable()
                binding.tieUniqueCodeData.requestFocus()
            }

            is InOutputUiState.SaveKeyUnique -> {
                binding.tiePiecesData.enable()
                binding.tiePiecesData.requestFocus()
            }

            is InOutputUiState.SavePieces -> {
                binding.tieBatchRollData.enable()
                binding.tieBatchRollData.requestFocus()
            }

            is InOutputUiState.Error -> {
                dismissDialog()
                showAlert(msg = state.msg, view = binding.btnNext)
                inputViewModel.setNoState()
            }

            else -> {}
        }
    }

    private fun initListener() {

        binding.btnClean.setOnClickListener {
            inputViewModel.resetData()
        }

        binding.tieReferData.setOnEditorActionListener { textView, actionId, keyEvent ->
            if (keyEvent != null && keyEvent.keyCode == KeyEvent.KEYCODE_ENTER) {
                inputViewModel.saveReference(textView.toString())
            } else if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE) {
                inputViewModel.saveReference(textView.toString())
            }
            return@setOnEditorActionListener false
        }

        binding.tieKeyItemData.setOnEditorActionListener { textView, actionId, keyEvent ->
            if (keyEvent != null && keyEvent.keyCode == KeyEvent.KEYCODE_ENTER) {
                inputViewModel.saveKeyItem(textView.toString())
            } else if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE) {
                inputViewModel.saveKeyItem(textView.toString())
            }
            return@setOnEditorActionListener false
        }

        binding.tieUniqueCodeData.setOnEditorActionListener { textView, actionId, keyEvent ->
            if (keyEvent != null && keyEvent.keyCode == KeyEvent.KEYCODE_ENTER) {
                inputViewModel.saveKeyUnique(textView.toString())
            } else if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE) {
                inputViewModel.saveKeyUnique(textView.toString())
            }
            return@setOnEditorActionListener false
        }

        binding.tiePiecesData.setOnEditorActionListener { textView, actionId, keyEvent ->
            if (keyEvent != null && keyEvent.keyCode == KeyEvent.KEYCODE_ENTER) {
                inputViewModel.savePieces(textView.toString().toDouble())
            } else if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE) {
                inputViewModel.savePieces(textView.toString().toDouble())
            }
            return@setOnEditorActionListener false
        }
        binding.tieBatchRollData.setOnEditorActionListener { textView, actionId, keyEvent ->
            if (keyEvent != null && keyEvent.keyCode == KeyEvent.KEYCODE_ENTER) {
                inputViewModel.saveBatchRoll(textView.toString())
            } else if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE) {
                inputViewModel.saveBatchRoll(textView.toString())
            }
            return@setOnEditorActionListener false
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
        }
    }


}