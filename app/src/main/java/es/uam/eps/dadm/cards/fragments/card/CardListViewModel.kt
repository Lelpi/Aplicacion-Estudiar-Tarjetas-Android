package es.uam.eps.dadm.cards.fragments.card

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import es.uam.eps.dadm.cards.model.Card
import es.uam.eps.dadm.cards.database.CardDatabase

class CardListViewModel(application: Application): AndroidViewModel(application) {

    private val context = getApplication<Application>().applicationContext

    private val deckId = MutableLiveData<String>()

    val cards: LiveData<List<Card>> = Transformations.switchMap(deckId) {
        CardDatabase.getInstance(context).cardDao.getCardsFromDeck(it)
    }

    fun loadDeckId(id: String) {
        deckId.value = id
    }
}