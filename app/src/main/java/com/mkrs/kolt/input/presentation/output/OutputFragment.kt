package com.mkrs.kolt.input.presentation.output

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
    private var isDemo = false

    private val preferencesViewModel by activityViewModels<PreferencesViewModel> {
        PreferenceModule.providePreferenceVMFactory(
            HomeModule.provideHomePreferences(requireActivity(), "Impresoras")
        )
    }

    private val vmFactory by lazy {
        InputModule.providesInputViewModelFactory(requireActivity().application)
    }

    private val outputViewModel: OutputViewModel by activityViewModels { vmFactory }


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
        outputViewModel.outputViewState.observe(viewLifecycleOwner) {
            observerOutputState(it)
        }
        outputViewModel.getDate(false)

        isDemo = preferencesViewModel.getInt(getString(R.string.key_pass_is_demo), 0) == 1
    }


    private fun observerOutputState(state: OutputUiState?) {
        when (state) {
            is OutputUiState.Loading -> showDialog()
            is OutputUiState.NoState -> dismissDialog()
            is OutputUiState.Error -> {
                dismissDialog()
                showAlert(msg = state.msg, view = binding.btnOutputNext)
                outputViewModel.setNoState()
            }

            is OutputUiState.SaveOutPutReference -> {
                dismissDialog()
                binding.btnOutputClean.enable()
                binding.tieOutputKeyPtData.enable()
                binding.tieOutputKeyPtData.requestFocus()
            }

            is OutputUiState.SaveOutPutKeyPT -> {
                dismissDialog()
                binding.tieOutputUniqueCodeData.enable()
                binding.tieOutputUniqueCodeData.requestFocus()
            }

            is OutputUiState.SaveOutPutKeyUnique -> {
                dismissDialog()
                binding.tieOutputPerfoData.enable()
                binding.tieOutputPerfoData.requestFocus()
            }

            is OutputUiState.SaveOutPutPerfo -> {
                dismissDialog()
                binding.tieOutputCoworkerData.enable()
                binding.tieOutputCoworkerData.requestFocus()
            }

            is OutputUiState.SaveOutputCoworker -> {
                dismissDialog()
                binding.tieOutputTo.enable()
                binding.tieOutputTo.requestFocus()
            }

            is OutputUiState.SaveOutputTo -> {
                dismissDialog()
                binding.btnOutputNext.enable()
                binding.btnOutputSave.enable()
            }

            is OutputUiState.GetDate -> {
                dismissDialog()
                binding.tvOutputDateData.text = state.date
            }

            else -> {
                dismissDialog()
            }
        }
    }

    private fun initListener() {
    }

    private fun initView() {
        binding.tvOutputEmitData.text = getString(R.string.title_output_kolt)

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