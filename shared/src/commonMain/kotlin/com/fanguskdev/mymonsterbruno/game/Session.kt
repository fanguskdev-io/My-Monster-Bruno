package com.fanguskdev.mymonsterbruno.game

/**
 * The minutes of play of each day (ADR 0006): they add up while the game is on screen and, when they reach what the
 * grown-ups chose, Bruno goes to sleep at the end of what she is doing. The next day starts from zero.
 */
internal object Session {
    /** [data] on [day]: the same, or with the minutes of another day forgotten. */
    fun onDay(data: SaveData, day: Int): SaveData =
        if (data.playedDay == day) data else data.copy(playedDay = day, playedSeconds = 0, extraSeconds = 0)

    /** [seconds] more played on [day]. */
    fun tick(data: SaveData, day: Int, seconds: Int): SaveData =
        onDay(data, day).let { it.copy(playedSeconds = it.playedSeconds + seconds) }

    /** Whether the time of [day] is over. */
    fun timeUp(data: SaveData, day: Int): Boolean =
        onDay(data, day).let { it.playedSeconds >= it.dailyMinutes * SECONDS_IN_MINUTE + it.extraSeconds }

    /** A grown-up gives [minutes] more for [day] only, counted from what she has played. */
    fun moreTime(data: SaveData, day: Int, minutes: Int): SaveData = onDay(data, day).let {
        val limit = it.dailyMinutes * SECONDS_IN_MINUTE + it.extraSeconds
        val from = maxOf(limit, it.playedSeconds)
        it.copy(extraSeconds = from + minutes * SECONDS_IN_MINUTE - it.dailyMinutes * SECONDS_IN_MINUTE)
    }

    /** The minutes played on [day], rounded down. */
    fun minutesPlayed(data: SaveData, day: Int): Int = onDay(data, day).playedSeconds / SECONDS_IN_MINUTE

    const val SECONDS_IN_MINUTE = 60
}
