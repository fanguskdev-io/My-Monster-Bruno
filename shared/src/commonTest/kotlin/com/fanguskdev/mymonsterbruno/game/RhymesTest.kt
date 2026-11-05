package com.fanguskdev.mymonsterbruno.game

import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/** The rhymes of the island of words (ADR 0021): the pairs, and that only one card rhymes with Bruno's word. */
class RhymesTest {
    @Test
    fun everyPairRhymesAndNoTwoPairsEndTheSame() {
        val pairs = RhymeWord.entries.groupBy { it.ending }
        assertEquals(10, pairs.size)
        for ((ending, words) in pairs) {
            assertEquals(2, words.size, "la pareja de -$ending: $words")
            for (word in words) assertTrue(word.word.endsWith(ending), "${word.word} no termina en $ending")
        }
        assertEquals(RhymeWord.entries.size, RhymeWord.entries.map { it.key }.toSet().size)
    }

    @Test
    fun fiveRoundsOfThePairsSheHasFoundLeastWithOnlyOneThatRhymes() {
        repeat(200) { seed ->
            val random = Random(seed)
            val rounds = rhymeRounds(random)
            assertEquals(RHYME_ROUNDS, rounds.size)
            assertEquals(RHYME_ROUNDS, rounds.map { it.said.pair }.toSet().size, "parejas repetidas")
            for (round in rounds) {
                assertFalse(round.said in round.choices, "la palabra de Bruno está entre las cartas")
                for (count in MIN_CHOICES..MAX_CHOICES) {
                    val cards = round.choices.narrowed(round.answer, count)
                    assertEquals(count, cards.size)
                    assertEquals(listOf(round.answer), cards.filter { it.ending == round.said.ending }, "una sola rima: $cards")
                }
            }
            // With five pairs already found, the other five come first.
            val found = rounds.associate { it.said.pair to 1 }
            val next = rhymeRounds(random, found)
            assertTrue(next.none { it.said.pair in found }, "repite una que ya encontró")
        }
    }
}
