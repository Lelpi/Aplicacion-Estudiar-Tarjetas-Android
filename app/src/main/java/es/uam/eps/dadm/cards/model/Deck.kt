package es.uam.eps.dadm.cards.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "decks_table")
class Deck(
    var name: String,
    @PrimaryKey
    var id: String = UUID.randomUUID().toString(),
    var intervalModifier: Double = 1.0
) {
    constructor() : this(
        "name"
    )

    override fun toString() = "deck | $name | $id | $intervalModifier"
}