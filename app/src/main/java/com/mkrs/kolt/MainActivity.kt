package com.mkrs.kolt

import android.os.Bundle
import androidx.activity.addCallback
import androidx.fragment.app.FragmentManager
import com.mkrs.kolt.base.MKTActivity
import com.mkrs.kolt.databinding.ActivityMainBinding
import com.mkrs.kolt.dashboard.presentacion.MainMenuFragment
import com.mkrs.kolt.utils.visible

class MainActivity : MKTActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var fragment: MainMenuFragment

    companion object {
        private const val HOME_TAG = "HOME"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        toSection(R.id.action_homme)
        onBackPressedDispatcher.addCallback(this) {
            mainOnBackPress()
        }
    }

    fun mainOnBackPress() {
        binding.navMainMenu.visible()
        verifyFragmentManagerBackStackEntryCount()
    }

    private fun verifyFragmentManagerBackStackEntryCount() {
        val homeId = R.id.action_homme
        when {
            binding.navMainMenu.menu.findItem(homeId).isChecked && supportFragmentManager.backStackEntryCount <= 0 -> {
                showMessageOut()
            }

            else -> {
                supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                toSection(homeId, false)
            }
        }
    }

    private fun toSection(ItemId: Int, isDifferentFlow: Boolean = false) {
        setBottomNavigationItemCheck(ItemId)
        when (ItemId) {
            R.id.action_print_config -> {
                //init bottomsheet printer
            }

            R.id.action_config -> {
                //init bottomsheet web services config
            }

            R.id.action_homme -> {
                launchMain()
            }
        }
    }

    private fun showMessageOut() {
        showAlertComplete(resources.getString(R.string.title_on_back_press_general),
            resources.getString(R.string.title_on_back_press_general_message),
            resources.getString(
                R.string.title_on_back_press_out
            ),
            true,
            { _, _ -> this.finishAffinity() },
            resources.getString(R.string.title_on_back_press_cancel),
            true,
            { _, _ -> })
    }

    private fun launchMain(args: Bundle? = null) {
        setFragment(MainMenuFragment.newInstance(), HOME_TAG)
    }

    private fun setBottomNavigationItemCheck(itemId: Int) {
        binding.navMainMenu.menu.findItem(itemId).isChecked = true
    }

}