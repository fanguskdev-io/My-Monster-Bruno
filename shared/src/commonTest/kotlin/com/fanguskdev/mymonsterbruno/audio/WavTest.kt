package com.fanguskdev.mymonsterbruno.audio

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class WavTest {
    /** A mono 16-bit WAV of [samples] at [rate], as tools/voces.py and tools/sonidos.py write them. */
    private fun wav(samples: ShortArray, rate: Int): ByteArray {
        val data = samples.size * 2
        val bytes = ByteArray(44 + data)
        fun text(at: Int, value: String) = value.encodeToByteArray().copyInto(bytes, at)
        fun int16(at: Int, value: Int) {
            bytes[at] = value.toByte()
            bytes[at + 1] = (value shr 8).toByte()
        }
        fun int32(at: Int, value: Int) {
            int16(at, value and 0xFFFF)
            int16(at + 2, value ushr 16)
        }
        text(0, "RIFF")
        int32(4, 36 + data)
        text(8, "WAVE")
        text(12, "fmt ")
        int32(16, 16)
        int16(20, 1)
        int16(22, 1)
        int32(24, rate)
        int32(28, rate * 2)
        int16(32, 2)
        int16(34, 16)
        text(36, "data")
        int32(40, data)
        samples.forEachIndexed { i, s -> int16(44 + 2 * i, s.toInt()) }
        return bytes
    }

    @Test
    fun readsTheSamplesAndHowLongTheyLast() {
        val samples = ShortArray(22050) { (it % 100 - 50).toShort() }
        val pcm = readWav(wav(samples, 22050))
        requireNotNull(pcm)
        assertEquals(1, pcm.channels)
        assertEquals(22050, pcm.rate)
        assertEquals(samples.toList(), pcm.samples.toList())
        assertEquals(1000L, pcm.millis)
    }

    @Test
    fun whatIsNotAWavIsNull() {
        assertNull(readWav(ByteArray(10)))
        assertNull(readWav("RIFF....AVI LIST".encodeToByteArray()))
    }
}
