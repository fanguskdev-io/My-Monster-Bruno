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

/** The colours of the island of colours (ADR 0007), with the word that names them: «el globo rojo». */
internal enum class Hue(val word: String) {
    RED("rojo"),
    YELLOW("amarillo"),
    BLUE("azul"),
    GREEN("verde"),
    ORANGE("naranja"),
    PURPLE("morado"),
}

/** A round of the balloons: Bruno wants [wanted] and there are three balloons, [balloons], one of them his colour. */
internal data class BalloonRound(val wanted: Hue, val balloons: List<Hue>)

/** The five rounds of the balloons: five different colours, each with two others to choose from. */
internal fun balloonRounds(random: Random): List<BalloonRound> = Hue.entries.shuffled(random).take(BALLOON_ROUNDS).map { wanted ->
    val others = (Hue.entries - wanted).shuffled(random).take(BALLOONS - 1)
    BalloonRound(wanted, (others + wanted).shuffled(random))
}

/**
 * A series of balloons: [pattern] over and over, [shown] of them in a row and then a gap, which takes [answer]. She picks
 * it among three [choices]: the colours of the pattern and, when it has two, another one.
 */
internal data class SeriesRound(val pattern: List<Hue>, val shown: Int, val choices: List<Hue>) {
    /** The colour of the [i]th balloon of the row. */
    fun at(i: Int): Hue = pattern[i % pattern.size]

    val answer: Hue get() = at(shown)
}

/**
 * The four series: two of two colours (red, blue, red…) and two of three, as they make them in the classroom. The row
 * shows the pattern once and a bit more, so the gap is not always its first colour.
 */
internal fun seriesRounds(random: Random): List<SeriesRound> = SERIES_SIZES.map { size ->
    val pattern = Hue.entries.shuffled(random).take(size)
    val extra = (Hue.entries - pattern.toSet()).shuffled(random).take(SERIES_CHOICES - size)
    SeriesRound(pattern, shown = size + 1 + random.nextInt(2), choices = (pattern + extra).shuffled(random))
}

internal const val BALLOONS = 3
internal const val BALLOON_ROUNDS = 5
internal val SERIES_SIZES = listOf(2, 2, 3, 3)
internal const val SERIES_CHOICES = 3
