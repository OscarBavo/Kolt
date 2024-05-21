package com.mkrs.kolt.transfer.presentation

import android.os.Bundle
import android.view.View
import com.mkrs.kolt.R
import com.mkrs.kolt.base.MKTFragment
import com.mkrs.kolt.base.viewBinding
import com.mkrs.kolt.databinding.FragmentTransferBinding

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
        initListener()
    }

    private fun initListener() {
        transferBinding.apply {

        }
    }

    /* override fun onCreateView(
         inflater: LayoutInflater, container: ViewGroup?,
         savedInstanceState: Bundle?
     ): View? {
         activity = requireActivity() as? MKTActivity
         // Inflate the layout for this fragment
         return inflater.inflate(R.layout.fragment_transfer, container, false)
     }*/

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