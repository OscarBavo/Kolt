package com.mkrs.kolt.transfer.presentation

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
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

        transferBinding.tbTransfer.title = getString(R.string.btn_title_transfer)
        setSupportActionBar(transferBinding.tbTransfer)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId > 0) {
            messageOut()
        }
        return super.onOptionsItemSelected(item)
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
            getString(R.string.title_exit_transfer),
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
        private const val HOME_TAG = "HOME"
        const val USER_TRANSFER = "USER"
    }
}