package es.uam.eps.dadm.cards.fragments.deck

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import es.uam.eps.dadm.cards.R
import es.uam.eps.dadm.cards.database.CardDatabase
import es.uam.eps.dadm.cards.databinding.FragmentDeckListBinding
import es.uam.eps.dadm.cards.model.Deck
import java.util.concurrent.Executors

class DeckListFragment : Fragment() {

    private lateinit var adapter: DeckAdapter
    private val executor = Executors.newSingleThreadExecutor()
    private val viewModel by lazy {
        ViewModelProvider(this)[DeckListViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = DataBindingUtil.inflate<FragmentDeckListBinding>(
            inflater,
            R.layout.fragment_deck_list,
            container,
            false
        )

        adapter = DeckAdapter()
        adapter.data = emptyList()
        binding.deckListRecyclerView.adapter = adapter

        viewModel.decks.observe(viewLifecycleOwner){
            adapter.data = it
            adapter.notifyDataSetChanged()
        }

        binding.newDeckFab.setOnClickListener {
            val deck = Deck("")
            executor.execute {
                CardDatabase.getInstance(requireContext()).cardDao.add(deck)
            }
            it.findNavController().navigate(
                DeckListFragmentDirections.actionDeckListFragmentToDeckEditFragment(deck.id)
            )
        }

        return binding.root
    }
}