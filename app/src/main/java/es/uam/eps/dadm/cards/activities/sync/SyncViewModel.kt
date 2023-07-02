package es.uam.eps.dadm.cards.activities.sync

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import es.uam.eps.dadm.cards.model.Card
import es.uam.eps.dadm.cards.model.Deck
import es.uam.eps.dadm.cards.database.CardDatabase

class SyncViewModel(application: Application): AndroidViewModel(application) {
    private val context = getApplication<Application>().applicationContext

    val cards: LiveData<List<Card>> = CardDatabase.getInstance(context).cardDao.getCards()
    val decks: LiveData<List<Deck>> = CardDatabase.getInstance(context).cardDao.getDecks()

}