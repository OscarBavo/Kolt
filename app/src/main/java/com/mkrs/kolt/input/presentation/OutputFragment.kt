package com.mkrs.kolt.input.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.mkrs.kolt.R
import com.mkrs.kolt.base.MKTActivity
import com.mkrs.kolt.base.MKTFragment
import com.mkrs.kolt.databinding.FragmentOutputBinding
import com.mkrs.kolt.input.di.InputModule
import com.mkrs.kolt.preferences.di.HomeModule
import com.mkrs.kolt.preferences.di.PreferenceModule
import com.mkrs.kolt.preferences.presentation.PreferencesViewModel
import com.mkrs.kolt.utils.disable
import com.mkrs.kolt.utils.enable

class OutputFragment : MKTFragment(R.layout.fragment_output) {

    private lateinit var binding: FragmentOutputBinding

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
        binding = FragmentOutputBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initDialog()
        initView()
        initListener()
        loadLiveData()
        binding.tieOutputReferData.requestFocus()
    }

    private fun loadLiveData() {
        inputViewModel.inOutViewState.observe(viewLifecycleOwner) {
            observerOutputState(it)
        }
    }


    private fun observerOutputState(state: InOutputUiState?) {
        when (state) {
            is InOutputUiState.Loading -> showDialog()
            is InOutputUiState.NoState -> dismissDialog()
            is InOutputUiState.Error -> {
                dismissDialog()
                showAlert(msg = state.msg, view = binding.btnOutputNext)
                inputViewModel.setNoState()
            }

            is InOutputUiState.SaveOutPutReference -> {
                binding.btnOutputClean.enable()
                binding.tieOutputKeyPtData.enable()
                binding.tieOutputKeyPtData.requestFocus()
            }

            is InOutputUiState.SaveOutPutKeyPT -> {
                binding.tieOutputUniqueCodeData.enable()
                binding.tieOutputUniqueCodeData.requestFocus()
            }

            is InOutputUiState.SaveOutPutKeyUnique -> {
                binding.tieOutputPerfoData.enable()
                binding.tieOutputPerfoData.requestFocus()
            }

            is InOutputUiState.SaveOutPutPerfo -> {
                binding.tieOutputCoworkerData.enable()
                binding.tieOutputCoworkerData.requestFocus()
            }

            is InOutputUiState.SaveOutputCoworker -> {
                binding.tieOutputTo.enable()
                binding.tieOutputTo.requestFocus()
            }

            is InOutputUiState.SaveOutputTo -> {
                binding.btnOutputNext.enable()
                binding.btnOutputSave.enable()
            }

            else -> {
                dismissDialog()
                inputViewModel.setNoState()
            }
        }
    }

    private fun initListener() {
    }

    private fun initView() {
        binding.tvOutputLabelsData.text = "10"
        binding.tvOutputDateData.text = "29/08/2024"
        binding.tvOutputEmitData.text = "KOLT TECHNOLOGY SA de CV"
        binding.tvOutputReceiveData.text = "MKRS"

        binding.tieOutputKeyPtData.disable()
        binding.tieOutputUniqueCodeData.disable()
        binding.tieOutputPerfoData.disable()
        binding.tieOutputCoworkerData.disable()
        binding.tieOutputTo.disable()

        binding.btnOutputClean.disable()
        binding.btnOutputNext.disable()
        binding.btnOutputSave.disable()

    }
}