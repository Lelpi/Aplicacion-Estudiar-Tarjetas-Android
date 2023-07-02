package es.uam.eps.dadm.cards.fragments.deck

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import es.uam.eps.dadm.cards.database.CardDatabase
import es.uam.eps.dadm.cards.model.Deck

class DeckListViewModel(application: Application): AndroidViewModel(application) {

    private val context = getApplication<Application>().applicationContext

    val decks: LiveData<List<Deck>> = CardDatabase.getInstance(context).cardDao.getDecks()
}