package com.fanguskdev.mymonsterbruno.game

import kotlin.random.Random

/** The number line of her class (ADR 0026): squares from zero to [LINE_LAST]. */
internal const val LINE_LAST = 10

/** The questions Bruno asks on the number line, each from a different square. */
internal const val HOPS = 5

/** Whether Bruno asks for the number that goes before the one he is on, or the one that goes after. [word] names its lines. */
internal enum class Side(val word: String) {
    BEFORE("antes"),
    AFTER("despues"),
    ;

    /** The squares Bruno can ask from: there is always a number on this side of them. */
    val squares: IntRange get() = if (this == BEFORE) 1..LINE_LAST else 0 until LINE_LAST
}

/** A question of the number line: Bruno is on [square] and asks for the number on [side] of it. */
internal data class Hop(val square: Int, val side: Side) {
    val answer: Int get() = if (side == Side.BEFORE) square - 1 else square + 1
}

/**
 * The questions of a visit: «antes» and «después» mixed, at least two of each, never the same question twice, and never
 * twice in a row from the same square.
 */
internal fun hops(random: Random): List<Hop> {
    val sides = (listOf(Side.BEFORE, Side.BEFORE, Side.AFTER, Side.AFTER) + Side.entries.random(random)).shuffled(random)
    val asked = mutableListOf<Hop>()
    for (side in sides) {
        val square = side.squares.filter { Hop(it, side) !in asked && it != asked.lastOrNull()?.square }.random(random)
        asked += Hop(square, side)
    }
    return asked
}

/**
 * [this] on a [day] she goes to the island of the numbers: one more day there if it is another day than the last. The
 * second game of the island takes turns by those days (ADR 0026 and 0029, [numbersGameToday]): by the days she goes and
 * not by the calendar, so that they take turns even if she does not play every day.
 */
internal fun SaveData.numbersOn(day: Int): SaveData = if (numbersDay == day) this else copy(numbersDay = day, numbersDays = numbersDays + 1)
