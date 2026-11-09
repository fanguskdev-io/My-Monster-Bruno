package com.fanguskdev.mymonsterbruno.game

import com.fanguskdev.mymonsterbruno.audio.Voice
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/** The stories for bedtime (ADR 0045): one a day, each night the next, each with its lines. */
class TaleTest {
    @Test
    fun oneADayAndEachNightTheNext() {
        var data = SaveData()
        assertEquals(Tale.STAR, data.taleTonight)
        assertFalse(data.heardTale(20261109))
        data = data.taleTold(20261109)
        assertTrue(data.heardTale(20261109), "ese día ya no hay otro")
        assertFalse(data.heardTale(20261110))
        assertEquals(Tale.FISH, data.taleTonight)
        data = data.taleTold(20261110).taleTold(20261111)
        assertEquals(Tale.STAR, data.taleTonight, "y vuelta a empezar")
    }

    @Test
    fun everyPageHasItsLine() {
        for (tale in Tale.entries) {
            val lines = (0 until TALE_PAGES).map { Voice.taleLine(tale, it) }
            assertEquals(TALE_PAGES, lines.toSet().size)
            assertTrue(lines.all { it in Voice.ALL }, "las frases de ${tale.key}")
        }
        assertTrue(Voice.TALE_INTRO in Voice.ALL && Voice.TALE_END in Voice.ALL)
        assertEquals("n_cuento_luna_6", Voice.taleLine(Tale.MOON, 5))
    }
}
