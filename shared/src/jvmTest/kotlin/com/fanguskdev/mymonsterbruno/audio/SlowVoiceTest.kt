package com.fanguskdev.mymonsterbruno.audio

import kotlinx.coroutines.runBlocking
import kotlin.math.PI
import kotlin.math.sin
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** Bruno, slower (ADR 0042): each line said slow is stretched once, played in its place and kept for the next time. */
class SlowVoiceTest {
    private class Recorder : SoundPlayer {
        val played = mutableListOf<String>()
        val sizes = mutableListOf<Int>()

        override fun load(name: String, wav: ByteArray) = Unit

        override fun play(name: String, volume: Float) {
            played += "play $name"
        }

        override fun playOnce(name: String, wav: ByteArray, volume: Float) {
            played += "once $name"
            sizes += wav.size
        }

        override fun stop(name: String) = Unit

        override fun stopAll() = Unit

        override fun release() = Unit
    }

    private val line = Pcm(ShortArray(4410) { (8000 * sin(2 * PI * 220 * it / 22050)).toInt().toShort() }, 1, 22050).toWav()

    @Test
    fun aSlowLineIsStretchedOnceAndPlayedInItsPlace() = runBlocking {
        var reads = 0
        val player = Recorder()
        val audio = Audio(player, this, read = {
            reads++
            line
        })
        audio.slow = true
        audio.say("b_hola")
        assertEquals(listOf("once voz_despacio"), player.played)
        assertTrue(player.sizes.single() > line.size * 1.15, "más larga: ${player.sizes.single()} de ${line.size}")
        audio.say("b_hola")
        assertEquals(1, reads, "la segunda vez ya está lista")
        audio.slow = false
        audio.say("b_hola")
        assertEquals("play b_hola", player.played.last(), "sin la voz despacio, la de siempre")
    }
}
