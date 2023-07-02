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
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import es.uam.eps.dadm.cards.R
import es.uam.eps.dadm.cards.activities.settings.SettingsActivity
import es.uam.eps.dadm.cards.database.CardDatabase
import es.uam.eps.dadm.cards.databinding.FragmentDeckEditBinding
import es.uam.eps.dadm.cards.model.Card
import es.uam.eps.dadm.cards.model.CardType
import es.uam.eps.dadm.cards.model.Deck
import es.uam.eps.dadm.cards.views.ConfirmDeleteDialogFragment
import java.util.concurrent.Executors

class DeckEditFragment : Fragment() {

    private lateinit var deck: Deck
    private lateinit var cards: List<Card>
    private lateinit var binding: FragmentDeckEditBinding
    lateinit var name: String
    private val executor = Executors.newSingleThreadExecutor()
    private val viewModel: DeckEditViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_deck_edit,
            container,
            false
        )

        val args = DeckEditFragmentArgs.fromBundle(requireArguments())
        viewModel.loadDeckId(args.deckId)

        viewModel.deck.observe(viewLifecycleOwner){
            deck = it.first().deck
            cards = it.first().cards
            binding.deck = deck
            name = deck.name
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()

        val nameTextWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                deck.name = s.toString()
            }
        }
        binding.name.addTextChangedListener(nameTextWatcher)

        binding.acceptButton.setOnClickListener {
            executor.execute {
                CardDatabase.getInstance(requireContext()).cardDao.update(deck)
            }
            it.findNavController().navigate(R.id.action_deckEditFragment_to_deckListFragment)
        }
        binding.cancelButton.setOnClickListener {
            deck.name = name
            it.findNavController().navigate(R.id.action_deckEditFragment_to_deckListFragment)
        }

        binding.deleteDeck.setOnClickListener{
            val dialog = ConfirmDeleteDialogFragment()
            dialog.show(childFragmentManager, "confirmDelete")
        }

        binding.editIntervalModifier.setOnClickListener {
            val matureCards = cards.filter { card -> card.type == CardType.MATURE.toString()}
            if (matureCards.isEmpty()) {
                val alert = AlertDialog.Builder(context)
                alert.setTitle(getString(R.string.warning))
                alert.setMessage(getString(R.string.warning_interval_modifier))
                alert.show()
            } else {
                it.findNavController().navigate(
                    DeckEditFragmentDirections.actionDeckEditFragmentToDeckEditIntervalModifierFragment(
                        deck.id
                    )
                )
            }
        }
        binding.editCards.setOnClickListener {
            it.findNavController().navigate(
                DeckEditFragmentDirections.actionDeckEditFragmentToCardListFragment(deck.id)
            )
        }
    }

    override fun onResume() {
        super.onResume()
        if (!SettingsActivity.getDeckIntervalModifier(requireContext())) {
            binding.editIntervalModifier.visibility = View.GONE
        } else {
            binding.editIntervalModifier.visibility = View.VISIBLE
        }
    }
}