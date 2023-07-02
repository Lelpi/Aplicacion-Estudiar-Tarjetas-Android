package es.uam.eps.dadm.cards.fragments.deck

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import es.uam.eps.dadm.cards.R
import es.uam.eps.dadm.cards.database.CardDatabase
import es.uam.eps.dadm.cards.databinding.FragmentDeckEditIntervalModifierBinding
import es.uam.eps.dadm.cards.model.Card
import es.uam.eps.dadm.cards.model.CardType
import es.uam.eps.dadm.cards.model.Deck
import java.util.concurrent.Executors
import kotlin.math.log

class DeckEditIntervalModifierFragment : Fragment() {

    private lateinit var binding: FragmentDeckEditIntervalModifierBinding
    private lateinit var deck: Deck
    private lateinit var matureCards: List<Card>
    private var desiredRetentionPercentage: String? = null
    private var currentRetentionPercentage: Double? = null
    private val executor = Executors.newSingleThreadExecutor()
    private val viewModel: DeckEditIntervalModifierViewModel by lazy {
        ViewModelProvider(this)[DeckEditIntervalModifierViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_deck_edit_interval_modifier,
            container,
            false)

        val args = DeckEditIntervalModifierFragmentArgs.fromBundle(requireArguments())
        viewModel.loadDeckId(args.deckId)
        viewModel.deck.observe(viewLifecycleOwner){
            deck = it.first().deck
            binding.currentIntervalModifier.text = getString(R.string.deck_interval_modifier, deck.intervalModifier)

            matureCards = it.first().cards.filter { card -> card.type == CardType.MATURE.toString()}

            val percentages = mutableListOf<Double>()
            matureCards.forEach { card ->
                percentages += card.timesCorrect.toDouble() / card.timesStudied.toDouble()
            }
            currentRetentionPercentage = percentages.sum() * 100 / percentages.size
            binding.currentRetentionPercentage.text = getString(R.string.current_retention_percentage, currentRetentionPercentage)

            binding.desiredRetentionPercentage.setText("%.2f".format(currentRetentionPercentage))
            desiredRetentionPercentage = currentRetentionPercentage.toString()
        }
        return binding.root
    }

    override fun onStart() {
        super.onStart()

        val retentionTextWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                desiredRetentionPercentage = s.toString()
            }
        }
        binding.desiredRetentionPercentage.addTextChangedListener(retentionTextWatcher)

        binding.acceptButton.setOnClickListener {
            try {
                if (desiredRetentionPercentage!!.toDouble() <= currentRetentionPercentage!!
                    || desiredRetentionPercentage!!.toDouble() >= 100)
                {
                    throw java.lang.NumberFormatException()
                }
            } catch (e: java.lang.NumberFormatException) {
                val alert = AlertDialog.Builder(context)
                alert.setTitle(getString(R.string.warning))
                alert.setMessage(getString(R.string.non_valid_retention_percentage))
                alert.show()
                return@setOnClickListener
            }

            deck.intervalModifier = log(desiredRetentionPercentage!!.toDouble()/100, 10.0) / log(currentRetentionPercentage!!/100, 10.0)
            executor.execute {
                CardDatabase.getInstance(requireContext()).cardDao.update(deck)
            }
            it.findNavController().
                navigate(
                    DeckEditIntervalModifierFragmentDirections.actionDeckEditIntervalModifierFragmentToDeckEditFragment(
                        deck.id
                    )
                )
        }
        binding.cancelButton.setOnClickListener {
            it.findNavController().
                navigate(
                    DeckEditIntervalModifierFragmentDirections.actionDeckEditIntervalModifierFragmentToDeckEditFragment(
                        deck.id
                    )
                )
        }
        binding.restoreButton.setOnClickListener {
            deck.intervalModifier = 1.0
            executor.execute {
                CardDatabase.getInstance(requireContext()).cardDao.update(deck)
            }
            binding.invalidateAll()
        }
    }
}



