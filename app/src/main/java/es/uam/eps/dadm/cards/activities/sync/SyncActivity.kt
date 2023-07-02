package es.uam.eps.dadm.cards.activities.sync

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import es.uam.eps.dadm.cards.model.Card
import es.uam.eps.dadm.cards.model.Deck
import es.uam.eps.dadm.cards.R
import es.uam.eps.dadm.cards.database.CardDatabase
import es.uam.eps.dadm.cards.databinding.ActivitySyncBinding
import java.util.concurrent.Executors

class SyncActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    lateinit var binding: ActivitySyncBinding
    private lateinit var cards: List<Card>
    private lateinit var decks: List<Deck>
    private val executor = Executors.newSingleThreadExecutor()
    private val viewModel by lazy {
        ViewModelProvider(this)[SyncViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sync)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        viewModel.cards.observe(this){
            cards = it
        }
        viewModel.decks.observe(this){
            decks = it
        }

        auth = Firebase.auth

        binding.upload.setOnClickListener {
            val database: DatabaseReference = FirebaseDatabase.getInstance().reference
                .child("users").child(auth.currentUser!!.uid)
            val values = mapOf("decks" to decks, "cards" to cards)
            database.setValue(values).addOnCompleteListener {
                Snackbar.make(
                    binding.root,
                    getString(R.string.operation_completed),
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }

        binding.download.setOnClickListener {
            val database: DatabaseReference = FirebaseDatabase.getInstance().reference
                .child("users").child(auth.currentUser!!.uid)

            database.addListenerForSingleValueEvent(object: ValueEventListener{
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
                        Snackbar.make(
                            binding.root,
                            getString(R.string.operation_completed),
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Snackbar.make(
                        binding.root,
                        getString(R.string.operation_cancelled),
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            })
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
}