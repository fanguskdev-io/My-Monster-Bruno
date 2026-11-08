package com.fanguskdev.mymonsterbruno.game

import com.fanguskdev.mymonsterbruno.audio.Voice
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** «La asamblea de Bruno» (ADR 0030): the day of the week of a date, the days around it, and their lines. */
class AssemblyTest {
    @Test
    fun theDayOfTheWeekOfADate() {
        assertEquals(Weekday.THURSDAY, weekdayOf(19700101))
        assertEquals(Weekday.MONDAY, weekdayOf(20261102))
        assertEquals(Weekday.SATURDAY, weekdayOf(20261107))
        assertEquals(Weekday.SUNDAY, weekdayOf(20261108))
        assertEquals(Weekday.TUESDAY, weekdayOf(20000229), "un 29 de febrero")
        assertEquals(Weekday.FRIDAY, weekdayOf(20240301), "el día después de un 29 de febrero")
        assertEquals(Weekday.THURSDAY, weekdayOf(20260101))
        assertEquals(Weekday.WEDNESDAY, weekdayOf(20261230))
        // Seven days in a row, each the next of the one before, across the end of a month and of a year.
        val week = listOf(20261228, 20261229, 20261230, 20261231, 20270101, 20270102, 20270103).map(::weekdayOf)
        assertEquals(Weekday.entries.toList(), week)
    }

    @Test
    fun theDayBeforeAndTheDayAfter() {
        assertEquals(Weekday.SUNDAY, Weekday.MONDAY.yesterday)
        assertEquals(Weekday.TUESDAY, Weekday.MONDAY.tomorrow)
        assertEquals(Weekday.MONDAY, Weekday.SUNDAY.tomorrow)
        for (day in Weekday.entries) assertEquals(day, day.tomorrow.yesterday)
    }

    @Test
    fun everyDayAndWeatherHasItsLines() {
        for (day in Weekday.entries) {
            assertTrue(Voice.todayIs(day) in Voice.ALL)
            assertTrue(Voice.aroundToday(day) in Voice.ALL)
            assertTrue(Voice.thatDay(day) in Voice.ALL)
        }
        for (weather in Weather.entries) assertTrue(Voice.weatherIs(weather) in Voice.ALL)
        assertEquals("b_hoy_es_miercoles", Voice.todayIs(Weekday.WEDNESDAY))
    }
}
