package es.uam.eps.dadm.cards.database

import androidx.room.Embedded
import androidx.room.Relation
import es.uam.eps.dadm.cards.model.Card
import es.uam.eps.dadm.cards.model.Deck

data class DeckWithCards(
    @Embedded
    val deck: Deck,
    @Relation(
        parentColumn = "id",
        entityColumn = "deckId"
    )
    val cards: List<Card>
)