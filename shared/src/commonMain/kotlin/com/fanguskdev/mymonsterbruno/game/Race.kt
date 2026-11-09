package com.fanguskdev.mymonsterbruno.game

import kotlin.random.Random

/** A place in a race (ADR 0037): first, second or third, with its [word] for the save and the lines, and its [number]. */
internal enum class Ordinal(val word: String, val number: Int) {
    FIRST("primero", 1),
    SECOND("segundo", 2),
    THIRD("tercero", 3),
}

/**
 * A race of three animals (ADR 0037): the [runners] in their lanes, top to bottom; the place each one arrives in, by lane
 * ([places]: 1 for the first); and the place Bruno asks about.
 */
internal data class Race(val runners: List<Pet>, val places: List<Int>, val asked: Ordinal) {
    /** The lane of the animal that arrived in [place]. */
    fun laneOf(place: Ordinal): Int = places.indexOf(place.number)

    /** The place the animal of [lane] arrived in. */
    fun placeOf(lane: Int): Ordinal = Ordinal.entries.first { it.number == places[lane] }
}

/** The animals that race, the ones that run: the fish stays in its bowl. */
internal val RUNNERS = listOf(Pet.CAT, Pet.DOG, Pet.RABBIT, Pet.TURTLE, Pet.BIRD)

/** How many lanes, and animals, a race has. */
internal const val LANES = 3

/** The races of a visit: three, each with three different animals arriving in another order, and a question each about each place. */
internal fun raceRounds(random: Random): List<Race> = Ordinal.entries.shuffled(random).map { asked ->
    Race(RUNNERS.shuffled(random).take(LANES), (1..LANES).shuffled(random), asked)
}
