package es.uam.eps.dadm.cards.fragments.deck

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import es.uam.eps.dadm.cards.database.CardDatabase
import es.uam.eps.dadm.cards.database.DeckWithCards

class DeckEditViewModel(application: Application): AndroidViewModel(application) {
    private val context = getApplication<Application>().applicationContext

    private val deckId = MutableLiveData<String>()

    val deck: LiveData<List<DeckWithCards>> = Transformations.switchMap(deckId) {
        CardDatabase.getInstance(context).cardDao.getDeckWithCards(it)
    }

    fun loadDeckId(id: String) {
        deckId.value = id
    }
}