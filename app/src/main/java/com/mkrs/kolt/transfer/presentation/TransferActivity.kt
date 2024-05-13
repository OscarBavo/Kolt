package com.mkrs.kolt.transfer.presentation

import android.content.Intent
import android.os.Bundle
import androidx.activity.addCallback
import com.mkrs.kolt.MainActivity
import com.mkrs.kolt.R
import com.mkrs.kolt.base.MKTActivity
import com.mkrs.kolt.base.viewBindings
import com.mkrs.kolt.databinding.ActivityTransferBinding

class TransferActivity : MKTActivity() {

    private val transferBinding by viewBindings(ActivityTransferBinding::inflate)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(transferBinding.root)
        initNavController(R.id.fr_transfer, R.navigation.transfer_nav, R.id.transferFragment)

        showingBar = true
        initBackPress()
        setupActionBar()
        initDialog()
    }

    private fun initBackPress() {
        onBackPressedDispatcher.addCallback(this) {
            navController.currentDestination?.id.let { origin ->
                when (origin) {
                    R.id.transferFragment -> {
                        messageOut()
                    }

                    else -> {
                        navController.popBackStack()
                    }
                }
            }
        }
    }

    private fun messageOut() {
        showAlertComplete(
            getString(R.string.title_on_back_press_general),
            "Deseas salir de Transferencia",
            "Si",
            true,
            { _, _ ->
                startActivity(Intent(this, MainActivity::class.java))
                this.finish()
            }, "No",
            true,
            {_,_->}
        )
    }
}