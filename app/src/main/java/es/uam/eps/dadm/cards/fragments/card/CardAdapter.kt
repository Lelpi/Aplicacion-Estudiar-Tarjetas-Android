package es.uam.eps.dadm.cards.fragments.card

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import es.uam.eps.dadm.cards.databinding.ListItemCardBinding
import es.uam.eps.dadm.cards.model.Card

class CardAdapter : RecyclerView.Adapter<CardAdapter.CardHolder>() {
    var data = listOf<Card>()
    private lateinit var binding: ListItemCardBinding

    inner class CardHolder(view: View) : RecyclerView.ViewHolder(view) {
        private var local = binding
        fun bind(card: Card) {
            local.card = card
            local.listItemCheckbox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    local.listItemNextDate.visibility = View.VISIBLE
                    local.listItemEasiness.visibility = View.VISIBLE
                    local.listItemRepetitions.visibility = View.VISIBLE
                } else {
                    local.listItemNextDate.visibility = View.GONE
                    local.listItemEasiness.visibility = View.GONE
                    local.listItemRepetitions.visibility = View.GONE
                }
            }
            itemView.setOnClickListener {
                it.findNavController()
                    .navigate(
                        CardListFragmentDirections.actionCardListFragmentToCardEditFragment(
                            card.id
                        )
                    )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        binding = ListItemCardBinding.inflate(layoutInflater, parent, false)
        return CardHolder(binding.root)
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: CardHolder, position: Int) {
        holder.bind(data[position])
    }
}