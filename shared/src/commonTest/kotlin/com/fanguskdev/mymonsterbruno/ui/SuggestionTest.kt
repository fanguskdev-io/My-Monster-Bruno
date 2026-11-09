package com.fanguskdev.mymonsterbruno.ui

import com.fanguskdev.mymonsterbruno.audio.Voice
import com.fanguskdev.mymonsterbruno.game.SaveData
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** The island Bruno suggests on the map (ADR 0035): the one she has done least and, among equals, another each day. */
class SuggestionTest {
    @Test
    fun theIslandSheHasFinishedFewestTimes() {
        val twice = Island.entries.associate { it.name to 2 }
        val save = SaveData(islands = twice + (Island.SHAPES.name to 1))
        for (day in 20261109..20261111) assertEquals(Island.SHAPES, save.suggestedIsland(day), "el día $day")
        assertEquals(Island.WORDS, SaveData(islands = twice - Island.WORDS.name).suggestedIsland(20261109), "la que nunca ha terminado")
    }

    @Test
    fun amongEqualsAnotherOneEachDay() {
        val firstWeek = (20261101..20261107).map { SaveData().suggestedIsland(it) }
        assertEquals(Island.entries.toSet(), firstWeek.toSet(), "sin terminar ninguna, en siete días seguidos salen las siete")
        val once = Island.entries.associate { it.name to 1 } - Island.VOWELS.name - Island.COLORS.name
        val two = (20261109..20261110).map { SaveData(islands = once).suggestedIsland(it) }
        assertEquals(setOf(Island.VOWELS, Island.COLORS), two.toSet(), "las dos que no ha terminado, un día cada una")
    }

    @Test
    fun brunoHasALineForEachIsland() {
        val lines = Island.entries.map { it.suggestionLine }
        assertEquals(lines.size, lines.toSet().size, "una frase distinta por isla")
        assertTrue(lines.all { it in Voice.ALL }, "y todas se cargan al abrir el juego")
    }
}
