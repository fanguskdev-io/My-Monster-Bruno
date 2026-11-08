package com.fanguskdev.mymonsterbruno.game

import kotlin.random.Random

/**
 * «Dentro y fuera» (ADR 0028), on the island of shapes: where she has to put Bruno or his ball, as it is said in class:
 * inside the big box or the small one, on the table or under it, and out of the box. [word] names its lines and its count
 * in the save.
 */
internal enum class Place(val word: String) {
    BIG_BOX("caja_grande"),
    SMALL_BOX("caja_pequena"),
    ON_TABLE("encima"),
    UNDER_TABLE("debajo"),
    OUT_OF_BOX("fuera"),
}

/** What she puts somewhere: Bruno himself, or his ball. [word] names its lines. */
internal enum class Thing(val word: String) {
    BRUNO("bruno"),
    BALL("pelota"),
    ;

    val other: Thing get() = if (this == BRUNO) BALL else BRUNO
}

/** A round of «Dentro y fuera»: where [thing] has to go. */
internal data class Placing(val thing: Thing, val place: Place) {
    /**
     * Whether leaving the thing [at] a place (null: on the floor) is what was asked. Out of the box is anywhere that is not
     * a box: the floor, the table or under it.
     */
    fun rightAt(at: Place?): Boolean = if (place == Place.OUT_OF_BOX) at != Place.BIG_BOX && at != Place.SMALL_BOX else at == place
}

/** The rounds of a visit: every place once, in any order, and Bruno and the ball by turns, starting with either. */
internal fun placings(random: Random): List<Placing> {
    val first = if (random.nextBoolean()) Thing.BRUNO else Thing.BALL
    return Place.entries.shuffled(random).mapIndexed { i, place -> Placing(if (i % 2 == 0) first else first.other, place) }
}
