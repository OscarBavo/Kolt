package com.mkrs.kolt.dashboard.presentacion

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.mkrs.kolt.MainActivity
import com.mkrs.kolt.R
import com.mkrs.kolt.base.MKTActivity
import com.mkrs.kolt.base.MKTFragment
import com.mkrs.kolt.databinding.FragmentMainMenuBinding

class MainMenuFragment : MKTFragment() {

    private lateinit var binding: FragmentMainMenuBinding

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            (activity as? MainActivity)?.mainOnBackPress()
        }
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String="", param2: String="") =
            MainMenuFragment().apply {
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        activity = requireActivity() as? MKTActivity
        isMainView = true
        isRequiredMessageReturn = true
        // Inflate the layout for this fragment

        binding = FragmentMainMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupBar(resources.getString(R.string.title_dashboard), true)
        binding.btnTransfer.setOnClickListener {
            showAlert("Hola", binding.btnTransfer)
        }
        registerForContextMenu(binding.btnTransfer)
    }

   /* override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.main_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }*/

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressedCallback.handleOnBackPressed()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
/*
    override fun onBackReturn() {
        super.onBackReturn()
        showAlert("back", binding.btnTransfer)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }*/


}