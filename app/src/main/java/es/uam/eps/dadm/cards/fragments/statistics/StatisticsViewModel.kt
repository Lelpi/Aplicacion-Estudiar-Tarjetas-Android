package es.uam.eps.dadm.cards.fragments.statistics

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import es.uam.eps.dadm.cards.database.CardDatabase
import es.uam.eps.dadm.cards.model.Card

class StatisticsViewModel(application: Application): AndroidViewModel(application) {
    private val context = getApplication<Application>().applicationContext

    val cards: LiveData<List<Card>> = CardDatabase.getInstance(context).cardDao.getCards()

    private val _tabLayout = MutableLiveData(0)
    val tabLayout: LiveData<Int>
        get() = _tabLayout

    fun updateTabLayout(tabLayout: Int){
        _tabLayout.value = tabLayout
    }
}