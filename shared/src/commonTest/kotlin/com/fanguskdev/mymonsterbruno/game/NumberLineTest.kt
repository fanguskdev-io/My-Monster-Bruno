package com.fanguskdev.mymonsterbruno.game

import com.fanguskdev.mymonsterbruno.audio.Voice
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

/** «El anterior y el siguiente» (ADR 0026): the questions of the number line, and the days it takes turns with the countdown. */
class NumberLineTest {
    @Test
    fun fiveQuestionsWithBeforeAndAfterMixed() {
        repeat(300) { seed ->
            val hops = hops(Random(seed))
            assertEquals(HOPS, hops.size)
            assertTrue(hops.count { it.side == Side.BEFORE } >= 2, "pocas de antes: $hops")
            assertTrue(hops.count { it.side == Side.AFTER } >= 2, "pocas de después: $hops")
            assertEquals(hops.size, hops.toSet().size, "pregunta repetida: $hops")
            hops.zipWithNext().forEach { (a, b) -> assertNotEquals(a.square, b.square, "dos seguidas desde la misma casilla: $hops") }
        }
    }

    @Test
    fun theAnswerIsAlwaysBesideBrunoAndOnTheLine() {
        repeat(300) { seed ->
            for (hop in hops(Random(seed))) {
                assertTrue(hop.answer in 0..LINE_LAST, "fuera de la recta: $hop")
                assertEquals(1, kotlin.math.abs(hop.answer - hop.square))
            }
        }
        assertEquals(4, Hop(5, Side.BEFORE).answer)
        assertEquals(6, Hop(5, Side.AFTER).answer)
    }

    @Test
    fun everyQuestionHasItsLines() {
        for (side in Side.entries) {
            for (square in side.squares) {
                val hop = Hop(square, side)
                assertTrue(Voice.askSide(hop) in Voice.ALL, "falta ${Voice.askSide(hop)}")
                assertTrue(Voice.answerSide(hop) in Voice.ALL, "falta ${Voice.answerSide(hop)}")
            }
        }
        for (number in 0..LINE_LAST) assertTrue(Voice.thatSquare(number) in Voice.ALL)
        assertEquals("b_antes_del_5", Voice.askSide(Hop(5, Side.BEFORE)))
        assertEquals("n_despues_del_0", Voice.answerSide(Hop(0, Side.AFTER)))
    }

    @Test
    fun theCountdownTheLineAndTheSumsTakeTurnsByTheDaysSheGoes() {
        var data = SaveData()
        assertEquals(NumbersGame.COUNTDOWN, data.numbersGameToday, "sin haber ido, la cuenta atrás")
        data = data.numbersOn(20261106)
        assertEquals(NumbersGame.COUNTDOWN, data.numbersGameToday, "el primer día, la cuenta atrás")
        data = data.numbersOn(20261106)
        assertEquals(NumbersGame.COUNTDOWN, data.numbersGameToday, "el mismo día, la misma")
        data = data.numbersOn(20261108)
        assertEquals(NumbersGame.NUMBER_LINE, data.numbersGameToday, "el siguiente día que va, aunque sea dos días después, la recta")
        data = data.numbersOn(20261115)
        assertEquals(NumbersGame.SUMS, data.numbersGameToday, "el siguiente, las sumas")
        data = data.numbersOn(20261116)
        assertEquals(NumbersGame.COUNTDOWN, data.numbersGameToday, "y otra vez la cuenta atrás")
        assertEquals(4, data.numbersDays)
    }
}
