package com.fanguskdev.mymonsterbruno.audio

import androidx.compose.runtime.Composable
import kotlin.math.max
import kotlin.math.min

/**
 * Whether she said the word Bruno asked for in «Habla con Bruno» (ADR 0015): the speech recognition of the device
 * itself, never over the internet. Where it cannot check (no recognizer on the device, the desktop, no permission), it
 * says so, and Bruno takes what he heard, as he always did.
 */
interface WordCheck {
    /** Whether it can check here. The first time, the platform may ask for its own permission. */
    suspend fun allowed(): Boolean

    /** What the recognizer understood in [voice], its guesses best first, with [word] as a hint; null if it cannot tell. */
    suspend fun heard(voice: Pcm, word: String): List<String>?

    fun release()
}

/** The check of this platform, for as long as the screen that uses it is on: it is let go when the screen goes. */
@Composable
internal expect fun rememberWordCheck(): WordCheck

/** A check that never can: the tests, the screenshots, a launch without sound and the desktop. */
class NoWordCheck : WordCheck {
    override suspend fun allowed() = false

    override suspend fun heard(voice: Pcm, word: String): List<String>? = null

    override fun release() = Unit
}

/**
 * Whether any of the [guesses] of the recognizer is [word], or near enough for a girl of four: about one letter wrong
 * in each three («mapoza» is «mariposa»), but «pera» is never «manzana». A word inside a longer guess counts too.
 */
fun saysWord(guesses: List<String>, word: String): Boolean {
    val target = simple(word)
    if (target.isEmpty()) return false
    val allowed = max(1, target.length * 2 / 5)
    return guesses.any { guess ->
        val words = simple(guess).split(' ').filter { it.isNotEmpty() }
        (words + words.joinToString("")).any { distance(it, target) <= allowed }
    }
}

/** [text] in small letters, without accents and with nothing but letters and single spaces. */
private fun simple(text: String): String = text.lowercase()
    .map {
        when (it) {
            'á', 'à', 'ä' -> 'a'
            'é', 'è', 'ë' -> 'e'
            'í', 'ì', 'ï' -> 'i'
            'ó', 'ò', 'ö' -> 'o'
            'ú', 'ù', 'ü' -> 'u'
            else -> if (it.isLetter()) it else ' '
        }
    }
    .joinToString("")
    .trim()
    .replace(Regex(" +"), " ")

/** How many letters have to change, go or come for [a] to be [b] (Levenshtein). */
private fun distance(a: String, b: String): Int {
    var previous = IntArray(b.length + 1) { it }
    for (i in 1..a.length) {
        val current = IntArray(b.length + 1)
        current[0] = i
        for (j in 1..b.length) {
            val change = if (a[i - 1] == b[j - 1]) 0 else 1
            current[j] = min(min(previous[j] + 1, current[j - 1] + 1), previous[j - 1] + change)
        }
        previous = current
    }
    return previous[b.length]
}
