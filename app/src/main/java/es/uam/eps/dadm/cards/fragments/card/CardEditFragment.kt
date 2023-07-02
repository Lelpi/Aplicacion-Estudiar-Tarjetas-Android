package es.uam.eps.dadm.cards.fragments.card

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
import es.uam.eps.dadm.cards.views.ConfirmDeleteDialogFragment
import es.uam.eps.dadm.cards.R
import es.uam.eps.dadm.cards.database.CardDatabase
import es.uam.eps.dadm.cards.databinding.FragmentCardEditBinding
import es.uam.eps.dadm.cards.model.Card
import java.util.concurrent.Executors

class CardEditFragment : Fragment() {

    private lateinit var card: Card
    private lateinit var binding: FragmentCardEditBinding
    private lateinit var question: String
    private lateinit var answer: String
    private val executor = Executors.newSingleThreadExecutor()
    private val viewModel: CardEditViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_card_edit,
            container,
            false
        )

        val args = CardEditFragmentArgs.fromBundle(requireArguments())
        viewModel.loadCardId(args.cardId)

        viewModel.card.observe(viewLifecycleOwner){
            card = it
            binding.card = card
            question = card.question
            answer = card.answer
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()

        val questionTextWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) { }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                card.question = s.toString()
            }
        }
        binding.question.addTextChangedListener(questionTextWatcher)

        val answerTextWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) { }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                card.answer = s.toString()
            }
        }
        binding.answer.addTextChangedListener(answerTextWatcher)

        binding.acceptButton.setOnClickListener {
            executor.execute {
                CardDatabase.getInstance(requireContext()).cardDao.update(card)
            }
            it.findNavController().navigate(
                CardEditFragmentDirections.actionCardEditFragmentToCardListFragment(
                    card.deckId
                )
            )
        }
        binding.cancelButton.setOnClickListener {
            card.question = question
            card.answer = answer
            it.findNavController().navigate(
                CardEditFragmentDirections.actionCardEditFragmentToCardListFragment(
                    card.deckId
                )
            )
        }

        binding.deleteCard.setOnClickListener {
            val dialog = ConfirmDeleteDialogFragment()
            dialog.show(childFragmentManager, "confirmDelete")
        }
    }
}