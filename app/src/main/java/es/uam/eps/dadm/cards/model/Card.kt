package es.uam.eps.dadm.cards.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime
import java.util.*
import kotlin.math.max
import kotlin.math.roundToLong

@Entity(tableName = "cards_table")
open class Card(
    @ColumnInfo(name = "card_question")
    var question: String,
    var answer: String,
    var deckId: String,
    var date: String = LocalDateTime.now().toString(),
    @PrimaryKey
    var id: String = UUID.randomUUID().toString(),
    var repetitions: Int = 0,
    var interval: Long = 1L,
    var nextPracticeDate: String = date,
    var lastPracticeDates: String = "",
    var easiness: Double = 2.5,
    var type: String = CardType.UNSEEN.toString(),
    var timesStudied: Int = 0,
    var timesCorrect: Int = 0
) {

    constructor() : this(
        "question",
        "answer",
        "0"
    )

    var answered = false
    var quality = -1

    fun update(currentDate: LocalDateTime, intervalModifier: Double){
        easiness = max(1.3, easiness + 0.1 - (5 - quality) * (0.08 + (5 - quality) * 0.02))

        repetitions = if (quality < 3) {
            0
        }
        else {
            repetitions + 1
        }

        interval = if (repetitions <= 1) {
            1
        } else if (repetitions == 2) {
            6
        } else {
            (interval * easiness).roundToLong()
        }

        interval = (interval * intervalModifier).roundToLong()
        if (interval < 1) {
            interval = 1
        }

        nextPracticeDate = currentDate.plusDays(interval).toString()

        type = if (currentDate.plusDays(-21) >= LocalDateTime.parse(date)) {
            CardType.MATURE.toString()
        } else {
            CardType.YOUNG.toString()
        }
    }

    override fun toString() = "card | $question | $answer | $date | $id | $easiness | $repetitions | $interval | " +
            "$nextPracticeDate | $type | $timesStudied | $timesCorrect | $deckId"

    fun isDue(date: LocalDateTime) = date >= LocalDateTime.parse(nextPracticeDate)

}