package es.uam.eps.dadm.cards.activities.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import es.uam.eps.dadm.cards.R
import es.uam.eps.dadm.cards.activities.settings.SettingsActivity
import es.uam.eps.dadm.cards.activities.title.TitleActivity
import es.uam.eps.dadm.cards.database.CardDatabase
import es.uam.eps.dadm.cards.databinding.ActivityEmailPasswordBinding
import es.uam.eps.dadm.cards.model.Card
import es.uam.eps.dadm.cards.model.Deck
import java.util.concurrent.Executors

class EmailPasswordActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val executor = Executors.newSingleThreadExecutor()
    lateinit var binding: ActivityEmailPasswordBinding
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var cards: List<Card>
    private lateinit var decks: List<Deck>
    private val viewModel by lazy {
        ViewModelProvider(this)[EmailPasswordViewModel::class.java]
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_email_password)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        viewModel.cards.observe(this){
            cards = it
        }
        viewModel.decks.observe(this){
            decks = it
        }
    }

    public override fun onStart() {
        super.onStart()
        auth = Firebase.auth
        updateUI(auth.currentUser)

        val emailTextWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) { }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                email = s.toString()
            }
        }
        binding.email.addTextChangedListener(emailTextWatcher)

        val passwordTextWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) { }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                password = s.toString()
            }
        }
        binding.password.addTextChangedListener(passwordTextWatcher)

        binding.logInButton.setOnClickListener {
            if (this::email.isInitialized && this::password.isInitialized && email != "" && password != "") {
                signIn(email, password)
            } else {
                Snackbar.make(binding.root, getString(R.string.check_email_password), Snackbar.LENGTH_LONG).show()
            }
        }

        binding.registerButton.setOnClickListener {
            if (this::email.isInitialized && this::password.isInitialized && email != "" && password != "") {
                createAccount(email, password)
            } else {
                Snackbar.make(binding.root, getString(R.string.check_email_password), Snackbar.LENGTH_LONG).show()
            }
        }

        binding.logOutButton.setOnClickListener {
            val database: DatabaseReference = FirebaseDatabase.getInstance().reference
                .child("users").child(auth.currentUser!!.uid)
            val values = mapOf("decks" to decks, "cards" to cards)
            database.setValue(values)
            auth.signOut()
            updateUI(auth.currentUser)
            SettingsActivity.setLoggedIn(applicationContext, false)
        }
    }

    private fun createAccount(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    updateUI(auth.currentUser)
                    SettingsActivity.setLoggedIn(applicationContext, true)
                    syncCards()
                } else {
                    Snackbar.make(
                        binding.root,
                        task.exception?.message ?: getString(R.string.authentication_failed),
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }
    }

    private fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    SettingsActivity.setLoggedIn(applicationContext, true)
                    syncCards()
                    startActivity(Intent(applicationContext, TitleActivity::class.java))
                } else {
                    Snackbar.make(
                        binding.root,
                        task.exception?.message ?: getString(R.string.authentication_failed),
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null){
            binding.welcomeText.text = getString(R.string.welcome_text, user.email)
            binding.logOut.visibility = View.VISIBLE
            binding.logInRegister.visibility = View.GONE
        } else {
            binding.logOut.visibility = View.GONE
            binding.logInRegister.visibility = View.VISIBLE
        }
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

    override fun onBackPressed() {
        if (SettingsActivity.getLoggedIn(applicationContext)){
            super.onBackPressed()
        } else {
            startActivity(Intent(applicationContext, TitleActivity::class.java))
        }
    }

    private fun syncCards() {
        val database: DatabaseReference = FirebaseDatabase.getInstance().reference
            .child("users").child(auth.currentUser!!.uid)

        database.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val listOfCards = mutableListOf<Card>()
                val listOfDecks = mutableListOf<Deck>()

                for (card in snapshot.child("cards").children) {
                    card.getValue(Card::class.java)?.let {
                        listOfCards.add(it)
                    }
                }
                for (deck in snapshot.child("decks").children) {
                    deck.getValue(Deck::class.java)?.let {
                        listOfDecks.add(it)
                    }
                }

                executor.execute{
                    CardDatabase.getInstance(applicationContext).cardDao.deleteAllCards()
                    CardDatabase.getInstance(applicationContext).cardDao.deleteAllDecks()
                    CardDatabase.getInstance(applicationContext).cardDao.add(listOfCards)
                    CardDatabase.getInstance(applicationContext).cardDao.addDecks(listOfDecks)
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}