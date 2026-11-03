package com.fanguskdev.mymonsterbruno.audio

import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/** What is done with her voice in «Habla con Bruno» (ADR 0011) before Bruno says it back. */
class MicrophoneTest {
    private val rate = 22050

    /** Half a second of silence, half a second of a loud tone and half a second of silence. */
    private fun word(): Pcm {
        val silence = ShortArray(rate / 2)
        val tone = ShortArray(rate / 2) { if (it % 50 < 25) 8000 else -8000 }
        return Pcm(silence + tone + silence, 1, rate)
    }

    @Test
    fun theSilenceAtBothEndsGoesWithALittleMargin() {
        val trimmed = assertNotNull(word().trimmed())
        // The half second of the word and 60 ms on each side.
        assertEquals(500L + 2 * 60, trimmed.millis, "dura ${trimmed.millis} ms")
        assertNull(Pcm(ShortArray(rate), 1, rate).trimmed(), "solo silencio")
        assertNull(Pcm(ShortArray(rate) { 300 }, 1, rate).trimmed(), "solo un murmullo del aparato")
    }

    @Test
    fun itIsMadeAsLoudAsTheGameVoices() {
        val loud = word().normalized()
        assertEquals((0.9f * Short.MAX_VALUE).toInt(), loud.samples.maxOf { abs(it.toInt()) })
        assertEquals(0, Pcm(ShortArray(10), 1, rate).normalized().samples.maxOf { abs(it.toInt()) }, "el silencio sigue en silencio")
    }

    @Test
    fun brunosVoiceIsHigherAndQuicker() {
        val voice = word()
        val higher = voice.higher(1.35f)
        assertEquals((voice.samples.size / 1.35f).toInt(), higher.samples.size)
        assertEquals(voice.rate, higher.rate)
        assertTrue(higher.millis < voice.millis)
        assertEquals(voice.samples.size, voice.higher(1f).samples.size)
    }

    @Test
    fun twoChannelsBecomeOne() {
        val stereo = Pcm(shortArrayOf(100, 300, -200, -400), 2, rate)
        assertEquals(listOf<Short>(200, -300), stereo.mono().samples.toList())
    }

    @Test
    fun itGoesBackToAWavThePlayersRead() {
        val voice = word().higher(1.35f)
        val read = assertNotNull(readWav(voice.toWav()))
        assertEquals(1, read.channels)
        assertEquals(rate, read.rate)
        assertEquals(voice.samples.toList(), read.samples.toList())
    }
}
