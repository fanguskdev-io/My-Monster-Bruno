package com.fanguskdev.mymonsterbruno.game

import com.fanguskdev.mymonsterbruno.ui.Island
import com.fanguskdev.mymonsterbruno.ui.suggestedIsland
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/** What the grown-ups choose that comes out (ADR 0041): islands off the map, and consonants out of the letters. */
class ShownTest {
    private val vowelsKnown = VOWELS.associate { it.toString() to 1 }

    @Test
    fun aHiddenLetterIsNeverAskedNorOnThePlate() {
        val noM = setOf('m')
        repeat(30) { seed ->
            for (round in letterRounds(Random(seed), vowelsKnown, noM)) {
                assertTrue(round.wanted != 'm' && 'm' !in round.cookies, "semilla $seed")
            }
            assertFalse('m' in tracePicks(vowelsKnown, Random(seed), noM), "ni se traza")
        }
    }

    @Test
    fun aHiddenLetterDoesNotHoldBackTheNextOnes() {
        val knowsAllButM = (VOWELS + CONSONANTS - 'm').associate { it.toString() to 1 }
        val next = (1..20).flatMap { leastPractised(knowsAllButM, 3, Random(it), setOf('m')) }.toSet()
        assertTrue(next.all { it in NEXT_CONSONANTS }, "sin la M, después de la P, la L y la S vienen la T, la N y la D")
        val withM = (1..20).map { leastPractised(knowsAllButM, 3, Random(it)) }
        assertTrue(
            withM.all { picks -> picks.first() == 'm' && picks.none { it in NEXT_CONSONANTS } },
            "con la M sin practicar, primero la M, y aún no la T, la N y la D",
        )
    }

    @Test
    fun theTogglesAndTheLastIslandStays() {
        val all = Island.entries.map { it.name }
        var data = SaveData()
        data = data.toggledLetter('m').toggledLetter('t')
        assertEquals(setOf('m', 't'), data.hidden)
        assertEquals(setOf('t'), data.toggledLetter('m').hidden)
        for (name in all) data = data.toggledIsland(name, all)
        assertEquals(all.size - 1, data.hiddenIslands.size, "en el mapa queda al menos una")
        assertEquals(all - data.hiddenIslands.toSet(), listOf(Island.HOME.name))
        assertEquals(Island.HOME, data.suggestedIsland(20261109), "y Bruno solo sugiere esa")
        assertEquals(setOf<Char>(), SaveData(hiddenLetters = listOf("a", "x", "mm")).hidden, "las vocales y lo raro no cuentan")
    }
}
