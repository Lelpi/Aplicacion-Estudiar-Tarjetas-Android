package es.uam.eps.dadm.cards.activities.title

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import es.uam.eps.dadm.cards.R
import es.uam.eps.dadm.cards.activities.login.EmailPasswordActivity
import es.uam.eps.dadm.cards.activities.settings.SettingsActivity
import es.uam.eps.dadm.cards.activities.sync.SyncActivity
import es.uam.eps.dadm.cards.databinding.ActivityTitleBinding
import es.uam.eps.dadm.cards.views.DrawerLocker


class TitleActivity : AppCompatActivity(), DrawerLocker {
    lateinit var binding: ActivityTitleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_title)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        binding.navView.setupWithNavController(navHostFragment.navController)

        setupMenu()
        PreferenceManager.setDefaultValues(
            this,
            R.xml.root_preferences,
            false
        )
    }

    private fun setupMenu() {
        addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.fragment_card_list, menu)
            }
            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.settings -> {
                        startActivity(Intent(this@TitleActivity, SettingsActivity::class.java))
                    }
                    R.id.account -> {
                        startActivity(Intent(this@TitleActivity, EmailPasswordActivity::class.java))
                    }
                    R.id.sync -> {
                        startActivity(Intent(this@TitleActivity, SyncActivity::class.java))
                    }
                }
                return true
            }
        })
    }

    override fun setDrawerEnabled(enabled: Boolean) {
        val lockMode = if (enabled) DrawerLayout.LOCK_MODE_UNLOCKED else DrawerLayout.LOCK_MODE_LOCKED_CLOSED
        binding.drawerLayout.setDrawerLockMode(lockMode)
    }
}