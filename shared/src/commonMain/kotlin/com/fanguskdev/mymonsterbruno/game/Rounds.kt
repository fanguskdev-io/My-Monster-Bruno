package com.fanguskdev.mymonsterbruno.game

import kotlin.random.Random

/** The vowels, the first letters of the classroom of four-year-olds (docs/diseno.md). */
internal val VOWELS = listOf('a', 'e', 'i', 'o', 'u')

/** The word of each vowel, the one Bruno says and the bubble draws: «la a de avión». */
internal enum class Word(val vowel: Char) {
    AVION('a'),
    ELEFANTE('e'),
    IGLU('i'),
    OSO('o'),
    UVA('u'),
    ;

    companion object {
        fun of(vowel: Char): Word = entries.first { it.vowel == vowel }
    }
}

/** A round of Comeletras: Bruno wants [wanted] and the plate has three cookies, [cookies], one of them his. */
internal data class LetterRound(val wanted: Char, val cookies: List<Char>)

/** The five rounds of Comeletras: each vowel once, in any order, with two other vowels on the plate. */
internal fun letterRounds(random: Random): List<LetterRound> = VOWELS.shuffled(random).map { wanted ->
    val others = (VOWELS - wanted).shuffled(random).take(COOKIES - 1)
    LetterRound(wanted, (others + wanted).shuffled(random))
}

/** How many strawberries Bruno wants in each round: one to five, each once, never five in the first round. */
internal fun countRounds(random: Random): List<Int> {
    val rounds = (1..MAX_COUNT).shuffled(random).toMutableList()
    if (rounds.first() == MAX_COUNT) rounds.add(rounds.removeAt(0))
    return rounds
}

/** The two vowels to trace: the ones she has traced least, ties at random. */
internal fun tracePicks(traced: Map<String, Int>, random: Random): List<Char> =
    VOWELS.shuffled(random).sortedBy { traced[it.toString()] ?: 0 }.take(TRACED_LETTERS)

internal const val COOKIES = 3
internal const val MAX_COUNT = 5
internal const val TRACED_LETTERS = 2
