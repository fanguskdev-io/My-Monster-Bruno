package com.fanguskdev.mymonsterbruno.game

/** The letters of the Spanish alphabet and the vowels with an accent, as «Escribir su nombre» traces them (ADR 0017). */
internal const val NAME_ALPHABET = "abcdefghijklmnñopqrstuvwxyzáéíóú"

/** No more than the bunting of the room holds. */
internal const val MAX_NAME_LETTERS = 10

/**
 * The letters of her [name] that she traces, in order: those of the bunting (letters only, no spaces), in small letters
 * as the glyphs are kept, and only those the game knows how to trace (a «ü» or a foreign letter is left out).
 */
internal fun nameLetters(name: String): List<Char> =
    name.lowercase().filter { it.isLetter() }.take(MAX_NAME_LETTERS).filter { it in NAME_ALPHABET }.toList()

/** The letter [letter] is, without its accent, to count it with the letters of the island: «á» is «a». */
internal fun baseLetter(letter: Char): Char = when (letter) {
    'á' -> 'a'
    'é' -> 'e'
    'í' -> 'i'
    'ó' -> 'o'
    'ú' -> 'u'
    else -> letter
}
