package es.uam.eps.dadm.cards.fragments.study

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import es.uam.eps.dadm.cards.database.CardDatabase
import es.uam.eps.dadm.cards.model.Card
import es.uam.eps.dadm.cards.model.Deck
import java.time.LocalDateTime
import java.util.concurrent.Executors

class StudyViewModel(application: Application) : AndroidViewModel(application) {

    private val executor = Executors.newSingleThreadExecutor()
    private val context = getApplication<Application>().applicationContext
    var card: Card? = null
    var cards: LiveData<List<Card>> = CardDatabase.getInstance(context).cardDao.getCards()

    var dueCard: LiveData<Card?> = Transformations.map(cards) {
        it.filter { card -> card.isDue(LocalDateTime.now()) }.run {
            if (any()) random() else null
        }
    }

    var nDueCards: LiveData<Int> = Transformations.map(cards) {
        it.filter { card -> card.isDue(LocalDateTime.now()) }.size
    }

    private val _limit = MutableLiveData<String>()
    val limit: LiveData<String>
        get() = _limit

    fun updateLimit(limit: String){
        _limit.value = limit
    }

    private val deckId = MutableLiveData<String>()

    val deck: LiveData<Deck> = Transformations.switchMap(deckId) {
        CardDatabase.getInstance(context).cardDao.getDeck(it)
    }

    fun loadDeckId(id: String) {
        deckId.value = id
    }

    fun update(quality: Int, intervalModifier: Double) {
        card?.quality = quality
        card?.answered = false
        card?.update(LocalDateTime.now(), intervalModifier)
        executor.execute {
            CardDatabase.getInstance(context).cardDao.update(card!!)
        }
    }
}