package com.mkrs.kolt.input.presentation

import android.content.Intent
import android.os.Bundle
import androidx.activity.addCallback
import com.mkrs.kolt.MainActivity
import com.mkrs.kolt.R
import com.mkrs.kolt.base.MKTActivity
import com.mkrs.kolt.base.viewBindings
import com.mkrs.kolt.databinding.ActivityInputBinding

class InputActivity : MKTActivity() {
    private val inputBinding by viewBindings(ActivityInputBinding::inflate)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(inputBinding.root)
        initNavController(R.id.fr_input, R.navigation.input_nav, R.id.inputFragment)
        showingBar = true
        setSupportActionBar(inputBinding.tbInput)
        initBackPress()
        setupActionBar()
        initDialog()

        this.let {
            val flow = it.intent.getIntExtra(INOUT_FLOW, IN_FLOW)
            var title = if (flow == IN_FLOW) {
                initNavController(R.id.fr_input, R.navigation.input_nav, R.id.inputFragment)
                getString(R.string.btn_title_in)
            } else {
                initNavController(R.id.fr_input, R.navigation.input_nav, R.id.outputFragment)
                getString(R.string.btn_title_out)
            }

            setTitle(title)
        }
    }

    private fun initBackPress() {
        onBackPressedDispatcher.addCallback(this) {
            navController.currentDestination?.id.let { origin ->
                when (origin) {
                    R.id.inputFragment -> {
                        messageOut(getString(R.string.btn_title_in))
                    }

                    R.id.outputFragment -> {
                        messageOut(getString(R.string.btn_title_out))
                    }

                    else -> {}
                }
            }
        }
    }

    private fun messageOut(title: String) {
        showAlertComplete(
            getString(R.string.title_on_back_press_general),
            getString(R.string.title_exit_in_out, title),
            getString(R.string.generic_yes),
            true,
            { _, _ ->
                startActivity(Intent(this, MainActivity::class.java))
                this.finish()
            }, getString(R.string.generic_no),
            true,
            { _, _ -> }
        )
    }

    companion object {
        const val INPUT_TAG = "INPUT"
        const val OUTPUT_TAG = "OUTPUT"
        const val INOUT_FLOW = "FLOW"
        const val IN_FLOW = 1
        const val OUT_FLOW = 2
    }
}