package es.uam.eps.dadm.cards.activities.settings

import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.EditTextPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import es.uam.eps.dadm.cards.R


class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        private const val MAX_NUMBER_CARDS_KEY = "max_number_cards"
        private const val MAX_NUMBER_CARDS_DEFAULT = ""
        private const val BOARD_KEY = "board"
        private const val BOARD_DEFAULT = false
        private const val LOGGED_IN_KEY = "logged_in_key"
        private const val LOGGED_IN_DEFAULT = false
        private const val SKIP_TUTORIAL_KEY = "skip_tutorial_key"
        private const val SKIP_TUTORIAL_DEFAULT = false
        private const val DECK_INTERVAL_MODIFIER_KEY = "interval_modifier"
        private const val DECK_INTERVAL_MODIFIER_DEFAULT = false

        fun getMaximumNumberOfCards(context: Context): String? {
            return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(MAX_NUMBER_CARDS_KEY, MAX_NUMBER_CARDS_DEFAULT)
        }

        fun getBoard(context: Context): Boolean {
            return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(BOARD_KEY, BOARD_DEFAULT)
        }

        fun getDeckIntervalModifier(context: Context): Boolean {
            return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(DECK_INTERVAL_MODIFIER_KEY, DECK_INTERVAL_MODIFIER_DEFAULT)
        }

        fun getSkipTutorial(context: Context): Boolean {
            return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(SKIP_TUTORIAL_KEY, SKIP_TUTORIAL_DEFAULT)
        }

        fun getLoggedIn(context: Context): Boolean {
            return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(LOGGED_IN_KEY, LOGGED_IN_DEFAULT)
        }

        fun setLoggedIn(context: Context, loggedIn: Boolean) {
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            val editor = sharedPreferences.edit()
            editor.putBoolean(LOGGED_IN_KEY, loggedIn)
            editor.apply()
        }

        fun setSkipTutorial(context: Context, skipTutorial: Boolean) {
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            val editor = sharedPreferences.edit()
            editor.putBoolean(SKIP_TUTORIAL_KEY, skipTutorial)
            editor.apply()
        }
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)

            preferenceManager.findPreference<EditTextPreference>("max_number_cards")
                ?.setOnBindEditTextListener {
                    it.inputType = InputType.TYPE_CLASS_NUMBER
                }
        }
    }

}