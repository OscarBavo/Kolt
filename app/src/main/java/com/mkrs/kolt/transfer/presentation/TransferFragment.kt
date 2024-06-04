package com.mkrs.kolt.transfer.presentation

import android.os.Bundle
import android.view.View
import androidx.core.widget.doOnTextChanged
import com.mkrs.kolt.R
import com.mkrs.kolt.base.MKTFragment
import com.mkrs.kolt.base.viewBinding
import com.mkrs.kolt.databinding.FragmentTransferBinding
import com.mkrs.kolt.utils.disable
import com.mkrs.kolt.utils.enable

/**
 * Use the [TransferFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TransferFragment : MKTFragment(R.layout.fragment_transfer) {

    private val transferBinding by viewBinding(FragmentTransferBinding::bind)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupBar(resources.getString(R.string.title_fragment_transfer), true)
        initDialog()
        initView()
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
        transferBinding.tieTextKeyItem.doOnTextChanged { text, _, _, _ ->
            if (!text.isNullOrEmpty()) {

            }
        }
    }

    companion object {
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
}