package es.uam.eps.dadm.cards.fragments.card

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
import es.uam.eps.dadm.cards.databinding.FragmentCardListBinding
import es.uam.eps.dadm.cards.model.Card
import java.util.concurrent.Executors

class CardListFragment: Fragment() {

    private lateinit var adapter: CardAdapter
    private lateinit var binding: FragmentCardListBinding
    private val executor = Executors.newSingleThreadExecutor()
    private val viewModel by lazy {
        ViewModelProvider(this)[CardListViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_card_list,
            container,
            false
        )

        val args = CardListFragmentArgs.fromBundle(requireArguments())
        viewModel.loadDeckId(args.deckId)

        adapter = CardAdapter()
        adapter.data = emptyList()
        binding.cardListRecyclerView.adapter = adapter

        viewModel.cards.observe(viewLifecycleOwner) {
            adapter.data = it
            adapter.notifyDataSetChanged()
        }

        binding.newCardFab.setOnClickListener {
            val card = Card("", "", args.deckId)
            executor.execute {
                CardDatabase.getInstance(requireContext()).cardDao.add(card)
            }
            it.findNavController().navigate(
                CardListFragmentDirections.actionCardListFragmentToCardEditFragment(
                    card.id
                )
            )
        }

        return binding.root
    }
}