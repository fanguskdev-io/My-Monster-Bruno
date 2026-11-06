package com.fanguskdev.mymonsterbruno.game

import kotlin.random.Random

/** The countdown starts at ten (ADR 0023): ten numbers on the panel, and the red button for the zero. */
internal const val COUNTDOWN_FROM = 10

/** The numbered buttons of the panel, one to ten, in any order: so she has to find each number, not just go along. */
internal fun countdownPanel(random: Random): List<Int> = (1..COUNTDOWN_FROM).shuffled(random)

/**
 * The countdown of Bruno's rocket: she touches ten, nine... down to one, and then the zero, the red button. A number that
 * is not the one that goes now does nothing but ask for it again.
 */
internal class Countdown {
    /** The number that goes now: ten first, zero last, and below zero once the rocket has taken off. */
    var next = COUNTDOWN_FROM
        private set

    val done: Boolean get() = next < 0

    /** She touches [number]: whether it was the one that goes now; if it was, the next one goes. */
    fun touch(number: Int): Boolean {
        if (number != next) return false
        next--
        return true
    }
}
