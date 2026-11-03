package com.fanguskdev.mymonsterbruno.audio

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/** When Bruno takes the word she said as the one he asked for (ADR 0015): lenient with a girl of four, never with another word. */
class WordCheckTest {
    @Test
    fun theWordItselfCountsHoweverTheRecognizerWritesIt() {
        assertTrue(saysWord(listOf("Manzana."), "manzana"))
        assertTrue(saysWord(listOf("una manzana"), "manzana"))
        assertTrue(saysWord(listOf("MARIPOSA"), "mariposa"))
        assertTrue(saysWord(listOf("pera", "manzana"), "manzana"), "basta con que la adivine una de sus apuestas")
    }

    @Test
    fun aWordSaidAsAFourYearOldSaysItCountsToo() {
        assertTrue(saysWord(listOf("mapoza"), "mariposa"))
        assertTrue(saysWord(listOf("efelante"), "elefante"))
        assertTrue(saysWord(listOf("zol"), "sol"))
        assertTrue(saysWord(listOf("manana"), "manzana"))
    }

    @Test
    fun anotherWordNeverCounts() {
        assertFalse(saysWord(listOf("pera"), "manzana"))
        assertFalse(saysWord(listOf("pelota"), "manzana"))
        assertFalse(saysWord(listOf("luna"), "sol"))
        assertFalse(saysWord(listOf("globo"), "oso"))
        assertFalse(saysWord(emptyList(), "manzana"), "si no ha entendido nada, no es la palabra")
        assertFalse(saysWord(listOf(""), "manzana"))
    }
}
