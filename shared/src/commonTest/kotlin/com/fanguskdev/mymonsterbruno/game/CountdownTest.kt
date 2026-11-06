package com.fanguskdev.mymonsterbruno.game

import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/** The countdown of Bruno's rocket (ADR 0023). */
class CountdownTest {
    @Test
    fun thePanelHasOneToTenOnceEachInAnyOrder() {
        val panels = (0 until 50).map { countdownPanel(Random(it)) }
        for (panel in panels) assertEquals((1..COUNTDOWN_FROM).toList(), panel.sorted())
        assertTrue(panels.toSet().size > 1, "siempre en el mismo orden")
    }

    @Test
    fun fromTenToZeroAndOnlyTheOneThatGoes() {
        val countdown = Countdown()
        assertFalse(countdown.touch(1), "el uno no va primero")
        assertFalse(countdown.touch(0), "ni el cero")
        for (number in COUNTDOWN_FROM downTo 1) {
            assertEquals(number, countdown.next)
            assertTrue(countdown.touch(number))
            assertFalse(countdown.touch(number), "el mismo otra vez")
        }
        assertFalse(countdown.done)
        assertTrue(countdown.touch(0))
        assertTrue(countdown.done)
    }
}
