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
    fun theBalloonsAreFiveColoursEachWithTwoOthers() {
        repeat(200) { seed ->
            val rounds = balloonRounds(Random(seed))
            assertEquals(BALLOON_ROUNDS, rounds.size)
            assertEquals(BALLOON_ROUNDS, rounds.map { it.wanted }.toSet().size, "un color pedido dos veces: $rounds")
            for (round in rounds) {
                assertEquals(BALLOONS, round.balloons.toSet().size, "globos repetidos: $round")
                assertEquals(1, round.balloons.count { it == round.wanted })
            }
        }
    }

    @Test
    fun theSeriesAreTwoOfTwoColoursAndTwoOfThreeAndTheAnswerIsAmongTheChoices() {
        repeat(200) { seed ->
            val rounds = seriesRounds(Random(seed))
            assertEquals(SERIES_SIZES, rounds.map { it.pattern.size })
            for (round in rounds) {
                assertEquals(round.pattern.size, round.pattern.toSet().size, "un color repetido en el patrón: $round")
                assertEquals(SERIES_CHOICES, round.choices.toSet().size, "opciones repetidas: $round")
                assertTrue(round.pattern.all { it in round.choices })
                assertTrue(round.answer in round.choices)
                // The row shows the whole pattern at least once, and something more.
                assertTrue(round.shown in round.pattern.size + 1..round.pattern.size + 2, "$round")
                assertEquals(round.pattern[round.shown % round.pattern.size], round.answer)
            }
        }
        val reds = SeriesRound(listOf(Hue.RED, Hue.BLUE), shown = 3, choices = listOf(Hue.RED, Hue.BLUE, Hue.GREEN))
        assertEquals(listOf(Hue.RED, Hue.BLUE, Hue.RED), (0 until 3).map { reds.at(it) })
        assertEquals(Hue.BLUE, reds.answer)
    }

    @Test
    fun theHouseIsBuiltInOrderAndItsPieceIsAlwaysAmongTheThree() {
        repeat(200) { seed ->
            val rounds = houseRounds(Random(seed))
            assertEquals(HousePart.entries, rounds.map { it.part })
            for (round in rounds) {
                assertEquals(HOUSE_PIECES, round.pieces.toSet().size, "piezas repetidas: $round")
                assertEquals(1, round.pieces.count { it == round.part.shape })
            }
        }
        assertEquals(
            listOf(Shape.SQUARE, Shape.TRIANGLE, Shape.RECTANGLE, Shape.CIRCLE, Shape.SQUARE, Shape.RECTANGLE),
            HousePart.entries.map { it.shape },
        )
    }

    @Test
    fun everyVowelHasItsWord() {
        assertEquals(VOWELS, Word.entries.map { it.vowel })
        assertEquals(Word.OSO, Word.of('o'))
    }
}
