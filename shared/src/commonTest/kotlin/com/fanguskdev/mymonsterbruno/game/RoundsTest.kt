package com.fanguskdev.mymonsterbruno.game

import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class RoundsTest {
    @Test
    fun comeletrasAsksForEachVowelOnceWithTwoOthersOnThePlate() {
        repeat(200) { seed ->
            val rounds = letterRounds(Random(seed))
            assertEquals(VOWELS.toSet(), rounds.map { it.wanted }.toSet())
            for (round in rounds) {
                assertEquals(COOKIES, round.cookies.size)
                assertEquals(COOKIES, round.cookies.toSet().size, "galletas repetidas: $round")
                assertEquals(1, round.cookies.count { it == round.wanted })
                assertTrue(round.cookies.all { it in VOWELS })
            }
        }
    }

    @Test
    fun theStrawberriesGoFromOneToFiveAndNeverStartWithFive() {
        repeat(200) { seed ->
            val rounds = countRounds(Random(seed))
            assertEquals((1..MAX_COUNT).toList(), rounds.sorted())
            assertNotEquals(MAX_COUNT, rounds.first())
        }
    }

    @Test
    fun theVowelsToTraceAreTheLeastTraced() {
        val traced = mapOf("a" to 4, "e" to 3, "i" to 0, "o" to 2, "u" to 0)
        repeat(50) { seed ->
            assertEquals(setOf('i', 'u'), tracePicks(traced, Random(seed)).toSet())
        }
        assertEquals(TRACED_LETTERS, tracePicks(emptyMap(), Random(1)).size)
    }

    @Test
    fun everyVowelHasItsWord() {
        assertEquals(VOWELS, Word.entries.map { it.vowel })
        assertEquals(Word.OSO, Word.of('o'))
    }
}
