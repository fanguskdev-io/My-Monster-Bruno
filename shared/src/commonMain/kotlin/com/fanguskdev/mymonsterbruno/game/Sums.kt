package com.fanguskdev.mymonsterbruno.game

import kotlin.random.Random

/** A sum of strawberries (ADR 0029): Bruno has [has] and she gives him [more]; up to three, as at four years old. */
internal data class Sum(val has: Int, val more: Int) {
    val total: Int get() = has + more

    /** Its name in the save and in its line: «1_2» for one and two. */
    val key: String get() = "${has}_$more"
}

/** The sums up to three: one and one, one and two, and two and one. */
internal val SUMS = listOf(Sum(1, 1), Sum(1, 2), Sum(2, 1))

/** The highest number she chooses from: the cards go from one to three. */
internal const val SUM_MAX = 3

/** The sums of a visit: the three, in any order. */
internal fun sumRounds(random: Random): List<Sum> = SUMS.shuffled(random)

/** The second game of the island of numbers, after the strawberries: they take turns by the days she goes (ADR 0029, 0037). */
internal enum class NumbersGame { COUNTDOWN, NUMBER_LINE, SUMS, RACE }

/**
 * The second game of the island of numbers today: the countdown the first day she goes there, the number line the next
 * day she goes, the sums the next, the race the next, and again; by the days she goes and not by the calendar (ADR 0026,
 * 0029 and 0037).
 */
internal val SaveData.numbersGameToday: NumbersGame
    get() = NumbersGame.entries[(numbersDays - 1).coerceAtLeast(0) % NumbersGame.entries.size]
