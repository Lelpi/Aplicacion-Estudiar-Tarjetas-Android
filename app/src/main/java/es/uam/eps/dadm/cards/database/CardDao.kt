package es.uam.eps.dadm.cards.database

import androidx.lifecycle.LiveData
import androidx.room.*
import es.uam.eps.dadm.cards.model.Card
import es.uam.eps.dadm.cards.model.Deck

@Dao
interface CardDao {
    @Query("SELECT * FROM cards_table")
    fun getCards(): LiveData<List<Card>>

    @Query("SELECT * FROM cards_table WHERE deckId = :id")
    fun getCardsFromDeck(id: String): LiveData<List<Card>>

    @Query("SELECT * FROM cards_table WHERE id = :id")
    fun getCard(id: String): LiveData<Card?>

    @Insert
    fun add(card: Card)

    @Insert
    fun add(cards: List<Card>)

    @Update
    fun update(card: Card)

    @Delete
    fun delete(card: Card)

    @Delete
    fun delete(cards: List<Card>)

    @Query("SELECT * FROM decks_table")
    fun getDecks(): LiveData<List<Deck>>

    @Query("SELECT * FROM decks_table WHERE id = :id")
    fun getDeck(id: String): LiveData<Deck?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun add(deck: Deck)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addDecks(decks: List<Deck>)

    @Update
    fun update(deck: Deck)

    @Delete
    fun delete(deck: Deck)

    @Transaction
    @Query("SELECT * FROM decks_table")
    fun getDecksWithCards(): LiveData<List<DeckWithCards>>

    @Transaction
    @Query("SELECT * FROM decks_table WHERE id = :id")
    fun getDeckWithCards(id: String): LiveData<List<DeckWithCards>>

    @Query("DELETE FROM cards_table")
    fun deleteAllCards()

    @Query("DELETE FROM decks_table")
    fun deleteAllDecks()
}