package com.fanguskdev.mymonsterbruno.game

import com.fanguskdev.mymonsterbruno.audio.Voice
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** «Sumas hasta 3» (ADR 0029): the sums of a visit, and that each has its lines. */
class SumsTest {
    @Test
    fun theThreeSumsUpToThreeOnceEach() {
        val orders = mutableSetOf<List<Sum>>()
        repeat(100) { seed ->
            val rounds = sumRounds(Random(seed))
            assertEquals(SUMS.toSet(), rounds.toSet())
            assertEquals(SUMS.size, rounds.size)
            orders += rounds
        }
        assertTrue(orders.size > 1, "siempre en el mismo orden")
        for (sum in SUMS) assertTrue(sum.has >= 1 && sum.more >= 1 && sum.total <= SUM_MAX, "$sum")
        assertEquals(listOf(2, 3, 3), SUMS.map { it.total })
    }

    @Test
    fun everySumHasItsLines() {
        for (sum in SUMS) {
            assertTrue(Voice.brunoHas(sum.has) in Voice.ALL)
            assertTrue(Voice.giveMore(sum.more) in Voice.ALL)
            assertTrue(Voice.sumIs(sum) in Voice.ALL)
            for (n in 1..sum.total) assertTrue(Voice.number(n) in Voice.ALL)
            assertTrue(Voice.strawberries(sum.total) in Voice.ALL)
        }
        assertEquals("n_suma_1_2", Voice.sumIs(Sum(1, 2)))
    }
}
