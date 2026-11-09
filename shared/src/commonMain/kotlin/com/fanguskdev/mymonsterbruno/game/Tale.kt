package com.fanguskdev.mymonsterbruno.game

/** The stories for bedtime (ADR 0045), one each night in turns: [key] names their lines. */
internal enum class Tale(val key: String) {
    STAR("estrella"),
    FISH("pez"),
    MOON("luna"),
}

/** How many pages, and lines, each story has. */
internal const val TALE_PAGES = 6

/** The story of tonight: the next one after the ones she has heard. */
internal val SaveData.taleTonight: Tale get() = Tale.entries[talesTold % Tale.entries.size]

/** Whether she has heard a story on [day] (yyyymmdd): one a day. */
internal fun SaveData.heardTale(day: Int): Boolean = taleDay == day

/** The save once she has heard a story on [day]. */
internal fun SaveData.taleTold(day: Int): SaveData = copy(taleDay = day, talesTold = talesTold + 1)
