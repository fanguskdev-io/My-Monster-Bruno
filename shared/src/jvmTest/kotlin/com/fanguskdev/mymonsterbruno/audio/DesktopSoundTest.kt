package com.fanguskdev.mymonsterbruno.audio

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** The sound of the desktop: one line, mixed by hand, and orders that never make the game wait. */
class DesktopSoundTest {
    @Test
    fun soundsAddUpWithoutOverflowingAndGoWhenTheyEnd() {
        val long = Playing("long", ShortArray(4) { 20000 }, 1f)
        val short = Playing("short", ShortArray(2) { 20000 }, 1f)
        val playing = mutableListOf(long, short)
        val out = ShortArray(3)
        mix(playing, out)
        assertEquals(listOf<Short>(32767, 32767, 20000), out.toList())
        assertEquals(listOf(long), playing)
        mix(playing, out)
        assertEquals(listOf<Short>(20000, 0, 0), out.toList())
        assertTrue(playing.isEmpty())
    }

    @Test
    fun theVolumeScalesTheSound() {
        val out = ShortArray(2)
        mix(mutableListOf(Playing("soft", shortArrayOf(1000, -1000), 0.5f)), out)
        assertEquals(listOf<Short>(500, -500), out.toList())
    }

    @Test
    fun theMusicGoesBackToItsStartAndStays() {
        val music = Playing("music", shortArrayOf(1, 2, 3), 1f, loops = true)
        val playing = mutableListOf(music)
        val out = ShortArray(7)
        mix(playing, out)
        assertEquals(listOf<Short>(1, 2, 3, 1, 2, 3, 1), out.toList())
        assertEquals(listOf(music), playing)
        mix(playing, out)
        assertEquals(listOf<Short>(2, 3, 1, 2, 3, 1, 2), out.toList())
    }

    @Test
    fun aSlowerRateIsFilledInWithStraightLines() {
        val half = Pcm(shortArrayOf(0, 100), channels = 1, rate = LINE_RATE / 2)
        assertEquals(listOf<Short>(0, 50, 100, 100), toLineFormat(half).toList())
    }

    @Test
    fun anythingElseBecomesOneChannelAtTheRateOfTheLine() {
        val stereo = Pcm(shortArrayOf(100, 300, 100, 300, 100, 300, 100, 300), channels = 2, rate = LINE_RATE * 2)
        assertEquals(listOf<Short>(200, 200), toLineFormat(stereo).toList())
        val same = Pcm(shortArrayOf(1, 2, 3), channels = 1, rate = LINE_RATE)
        assertEquals(listOf<Short>(1, 2, 3), toLineFormat(same).toList())
    }

    /**
     * The strawberries: a count that cuts the one before, again and again. With a Clip for each sound, the thread of the
     * sound waited up to two seconds for Java Sound at each cut and fell further and further behind, silent; now it takes
     * the orders within a moment, as it writes a chunk. The first touch waits while the sound card wakes up, a second and
     * a half if it was asleep, and a busy computer can hold the card now and then; so what counts is how long a touch
     * usually waits, and that none is left waiting for good. The sounds are silence, so nothing is heard if the computer
     * plays them.
     */
    @Test
    fun cuttingASoundNeverHoldsTheOthersUp() {
        val player = DesktopSoundPlayer()
        val wav = wav(Pcm(ShortArray(LINE_RATE / 2), channels = 1, rate = LINE_RATE))
        for (name in listOf("fresa", "n_num_1", "n_num_2", "b_fresas_2")) player.load(name, wav)
        val waits = mutableListOf<Long>()
        try {
            repeat(TAPS) { n ->
                player.stop("n_num_${n % 2 + 1}")
                player.play("fresa")
                player.play("n_num_${(n + 1) % 2 + 1}")
                if (n % 5 == 0) player.play("b_fresas_2")
                val start = System.nanoTime()
                while (player.backlog > 0 && System.nanoTime() - start < GIVE_UP_NANOS) Thread.sleep(1)
                waits += System.nanoTime() - start
                Thread.sleep(PAUSE_MILLIS)
            }
        } finally {
            player.release()
        }
        val usual = waits.sorted()[waits.size / 2]
        assertTrue(usual < USUAL_WAIT_NANOS, "el sonido suele tardar ${usual / 1_000_000} ms en atender un toque")
        assertTrue(waits.max() < GIVE_UP_NANOS, "un toque se quedó ${waits.max() / 1_000_000} ms sin atender")
    }

    /** A mono 16-bit WAV of [pcm], as tools/voces.py writes them. */
    private fun wav(pcm: Pcm): ByteArray {
        val data = pcm.samples.size * 2
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
        int32(24, pcm.rate)
        int32(28, pcm.rate * 2)
        int16(32, 2)
        int16(34, 16)
        text(36, "data")
        int32(40, data)
        pcm.samples.forEachIndexed { i, s -> int16(44 + 2 * i, s.toInt()) }
        return bytes
    }

    private companion object {
        const val TAPS = 30
        const val PAUSE_MILLIS = 60L

        /** The line takes the orders as it writes a chunk, some 23 ms; with a Clip for each sound, a cut took up to 2 s. */
        const val USUAL_WAIT_NANOS = 250_000_000L
        const val GIVE_UP_NANOS = 3_000_000_000L
    }
}
