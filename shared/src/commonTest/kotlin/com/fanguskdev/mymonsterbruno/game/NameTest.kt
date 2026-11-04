package com.fanguskdev.mymonsterbruno.game

import com.fanguskdev.mymonsterbruno.audio.Voice
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** Writing her name (ADR 0017): which letters she traces, and that each of them has its strokes and its voice. */
class NameTest {
    @Test
    fun herNameIsTracedLetterByLetterAsTheBuntingShowsIt() {
        assertEquals("lucía".toList(), nameLetters("Lucía"))
        assertEquals("maríajosé".toList(), nameLetters("María José"), "sin el espacio")
        assertEquals("ñuria".toList(), nameLetters("ÑURIA"))
        assertEquals("ana".toList(), nameLetters(" Ana-"))
        assertEquals(MAX_NAME_LETTERS, nameLetters("Maximilianaaaa").size, "lo que cabe en el banderín")
        assertEquals("zo".toList(), nameLetters("Zoë"), "una letra que no se sabe trazar se deja")
        assertTrue(nameLetters("").isEmpty())
    }

    @Test
    fun everyLetterOfANameHasItsStrokesAndItsVoice() {
        for (letter in NAME_ALPHABET) {
            assertTrue(Glyphs.has(letter), "la $letter no tiene trazo")
            assertTrue(Voice.letterName(letter) in Voice.ALL, "la $letter no tiene voz")
        }
        assertEquals(NAME_ALPHABET.length, NAME_ALPHABET.map(Voice::letterName).toSet().size, "dos letras con la misma voz")
    }

    @Test
    fun anAccentedLetterCountsAsItsLetter() {
        assertEquals("aeiou", "áéíóú".map(::baseLetter).joinToString(""))
        assertEquals('ñ', baseLetter('ñ'), "la eñe es otra letra")
    }
}
