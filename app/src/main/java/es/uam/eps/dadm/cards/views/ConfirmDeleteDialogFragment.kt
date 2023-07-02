package es.uam.eps.dadm.cards.views

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import es.uam.eps.dadm.cards.fragments.deck.DeckEditViewModel
import es.uam.eps.dadm.cards.R
import es.uam.eps.dadm.cards.database.CardDatabase
import es.uam.eps.dadm.cards.fragments.card.CardEditViewModel
import es.uam.eps.dadm.cards.fragments.card.CardEditFragmentDirections
import java.util.concurrent.Executors

class ConfirmDeleteDialogFragment : DialogFragment() {

    private val executor = Executors.newSingleThreadExecutor()
    private val cardViewModel: CardEditViewModel by viewModels(ownerProducer={requireParentFragment()})
    private val deckViewModel: DeckEditViewModel by viewModels(ownerProducer={requireParentFragment()})

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setTitle(getString(R.string.delete))
                .setMessage(getString(R.string.confirm_delete))
                .setPositiveButton(getString(R.string.accept_card_edit_button_title)
                ) { _, _ ->
                    val deckWithCards = deckViewModel.deck.value?.first()
                    val card = cardViewModel.card.value

                    if (deckWithCards != null){
                        executor.execute {
                            CardDatabase.getInstance(requireContext()).cardDao.delete(deckWithCards.cards)
                            CardDatabase.getInstance(requireContext()).cardDao.delete(deckWithCards.deck)
                        }
                        NavHostFragment.findNavController(this).navigate(R.id.action_deckEditFragment_to_deckListFragment)
                    } else if (card != null){
                        executor.execute{
                            CardDatabase.getInstance(requireContext()).cardDao.delete(card)
                        }
                        NavHostFragment.findNavController(this).navigate(
                            CardEditFragmentDirections.actionCardEditFragmentToCardListFragment(card.deckId)
                        )
                    }
                }
                .setNegativeButton(getString(R.string.cancel_card_edit_button_title)
                ) { _, _ ->
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}