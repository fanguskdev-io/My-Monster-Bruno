package com.fanguskdev.mymonsterbruno.game

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** Her week in the grown-ups' corner (ADR 0040): the days back, what she played and what she missed. */
class WeekTest {
    @Test
    fun theDaysGoBackAcrossMonthsAndYears() {
        for (day in listOf(19700101, 20261109, 20261231, 20270101, 20280229, 20280301, 21000301)) {
            assertEquals(day, dayOf(dayNumber(day)), "$day")
        }
        assertEquals(20270101, dayOf(dayNumber(20261231) + 1))
        assertEquals(20280229, dayOf(dayNumber(20280301) - 1), "2028 es bisiesto")
        assertEquals(listOf(20261027, 20261028, 20261029, 20261030, 20261031, 20261101, 20261102), weekUpTo(20261102))
    }

    @Test
    fun itRemembersOnlyTheLastSevenDays() {
        var data = SaveData()
        for (day in listOf(20261101, 20261103, 20261107, 20261109)) data = data.logged(day) { it.copy(seconds = it.seconds + 60) }
        assertEquals(setOf("20261103", "20261107", "20261109"), data.days.keys, "el 1 ya queda lejos del 9")
        val week = data.week(20261109)
        assertEquals(7, week.size)
        assertEquals(20261103, week.first().first)
        assertEquals(listOf(60, 0, 0, 0, 60, 0, 60), week.map { it.second.seconds })
    }

    @Test
    fun playingAndFinishingIslandsAreLogged() {
        var data = Session.tick(SaveData(), 20261109, 30)
        data = Session.tick(data, 20261109, 30)
        assertEquals(60, data.days["20261109"]?.seconds)
        assertEquals(60, data.playedSeconds)
    }

    @Test
    fun whatSheMissedMostComesFirst() {
        var data = SaveData()
        fun miss(day: Int, what: String) {
            data = data.logged(day) { it.copy(misses = it.misses.plusOne(what)) }
        }
        miss(20261105, Miss.letter('m'))
        miss(20261106, Miss.letter('m'))
        miss(20261109, Miss.letter('m'))
        miss(20261109, Miss.shape(Shape.TRIANGLE))
        miss(20261108, Miss.shape(Shape.TRIANGLE))
        miss(20261109, Miss.place(Place.UNDER_TABLE))
        miss(20261109, Miss.ordinal(Ordinal.SECOND))
        assertEquals(
            listOf("letra:m" to 3, "forma:triangulo" to 2, "puesto:segundo" to 1),
            data.hardest(20261109),
            "los tres que más, y entre iguales por su nombre",
        )
        assertTrue(SaveData().hardest(20261109).isEmpty())
        assertEquals(listOf("forma:triangulo" to 2), data.hardest(20261113, count = 1), "cuatro días después, solo cuentan el 8 y el 9")
    }
}
