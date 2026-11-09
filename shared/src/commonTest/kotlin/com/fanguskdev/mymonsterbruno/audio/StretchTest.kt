package com.fanguskdev.mymonsterbruno.audio

import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue

/** The slower voice (ADR 0042): longer, at the same pitch and as loud. */
class StretchTest {
    private val rate = 22050

    private fun tone(hertz: Double, seconds: Double) =
        Pcm(ShortArray((rate * seconds).toInt()) { (8000 * sin(2 * PI * hertz * it / rate)).toInt().toShort() }, 1, rate)

    /** How many times a second [samples] goes from below zero to zero or above. */
    private fun pitch(samples: ShortArray): Double {
        var ups = 0
        for (i in 1 until samples.size) if (samples[i - 1] < 0 && samples[i] >= 0) ups++
        return ups * rate.toDouble() / samples.size
    }

    private fun loudness(samples: ShortArray) = sqrt(samples.sumOf { it.toDouble() * it } / samples.size)

    @Test
    fun longerAtTheSamePitchAndAsLoud() {
        val voice = tone(220.0, 1.0)
        val long = stretch(voice, 1.2f)
        assertEquals((voice.samples.size * 1.2f).toInt().toDouble(), long.samples.size.toDouble(), 1.0)
        val middle = long.samples.copyOfRange(long.samples.size / 4, long.samples.size * 3 / 4)
        assertTrue(abs(pitch(middle) - 220.0) < 220.0 * 0.04, "el mismo tono: ${pitch(middle)}")
        val before = loudness(voice.samples.copyOfRange(voice.samples.size / 4, voice.samples.size * 3 / 4))
        assertTrue(abs(loudness(middle) - before) < before * 0.15, "igual de fuerte: ${loudness(middle)} y $before")
    }

    @Test
    fun asItIsWhenThereIsNothingToStretch() {
        val voice = tone(220.0, 0.5)
        assertSame(voice, stretch(voice, 1f))
        val stereo = Pcm(ShortArray(4000), channels = 2, rate = rate)
        assertSame(stereo, stretch(stereo, 1.2f))
        val click = Pcm(ShortArray(100), channels = 1, rate = rate)
        assertSame(click, stretch(click, 1.2f))
    }
}
