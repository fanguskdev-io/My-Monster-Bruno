package com.fanguskdev.mymonsterbruno.game

import kotlin.test.Test
import kotlin.test.assertEquals

/** The feeling of the day (ADR 0019): what the game keeps of how she felt. */
class MoodTest {
    @Test
    fun oneFeelingADayTheLastSheChose() {
        val save = SaveData().feltOn(20261104, Feeling.SADNESS).feltOn(20261104, Feeling.JOY).feltOn(20261105, Feeling.CALM)
        assertEquals(listOf(20261105 to Feeling.CALM, 20261104 to Feeling.JOY), save.feltDays())
    }

    @Test
    fun onlyTheLastDaysAreKept() {
        var save = SaveData()
        for (day in 1..20) save = save.feltOn(20261100 + day, Feeling.entries[day % Feeling.entries.size])
        val days = save.feltDays().map { it.first }
        assertEquals(MOOD_DAYS, days.size)
        assertEquals(20261120, days.first(), "el último, el primero")
        assertEquals(20261120 - MOOD_DAYS + 1, days.last())
    }

    @Test
    fun aBrokenDayIsLeftOut() {
        val save = SaveData(moods = mapOf("20261104" to "alegria", "ayer" to "calma", "20261103" to "aburrimiento"))
        assertEquals(listOf(20261104 to Feeling.JOY), save.feltDays())
    }
}
