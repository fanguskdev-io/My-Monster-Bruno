package com.fanguskdev.mymonsterbruno.audio

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.math.PI
import kotlin.math.sin
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * The voice in the background (ADR 0045): on Android the game goes on running when she leaves it, and the story at bedtime
 * went on telling itself; now nothing is said until she is back, and the line cut by leaving is said again.
 */
class BackgroundVoiceTest {
    private class Recorder : SoundPlayer {
        val played = mutableListOf<String>()

        override fun load(name: String, wav: ByteArray) = Unit

        override fun play(name: String, volume: Float) {
            played += name
        }

        override fun stop(name: String) = Unit

        override fun stopAll() = Unit

        override fun release() = Unit
    }

    /** A line of a fifth of a second. */
    private val line = Pcm(ShortArray(4410) { (8000 * sin(2 * PI * 220 * it / 22050)).toInt().toShort() }, 1, 22050).toWav()

    @Test
    fun inTheBackgroundNothingIsSaidUntilSheIsBack() = runBlocking {
        val player = Recorder()
        val audio = Audio(player, this)
        audio.loaded("n_hola", line)
        audio.pause()
        val saying = launch { audio.say("n_hola") }
        delay(600)
        assertTrue(player.played.isEmpty(), "en el fondo no dice nada")
        audio.resume()
        saying.join()
        assertEquals(listOf("n_hola"), player.played, "al volver, sí")
    }

    @Test
    fun theStoryTellsAgainTheLineThatLeavingCut() = runBlocking {
        val player = Recorder()
        val audio = Audio(player, this)
        audio.loaded("n_cuento_pez_1", line)
        val story = launch { audio.tell("n_cuento_pez_1") }
        delay(100)
        audio.pause()
        delay(700)
        assertEquals(1, player.played.size, "cortada al irse")
        audio.resume()
        story.join()
        assertEquals(listOf("n_cuento_pez_1", "n_cuento_pez_1"), player.played, "y dicha otra vez al volver")
    }

    @Test
    fun aLineCutByAnotherIsNotSaidAgain() = runBlocking {
        val player = Recorder()
        val audio = Audio(player, this)
        audio.loaded("n_a", line)
        audio.loaded("n_b", line)
        var told = true
        val first = launch { told = audio.say("n_a") }
        delay(100)
        audio.say("n_b")
        first.join()
        assertFalse(told, "la cortó otra")
        assertEquals(listOf("n_a", "n_b"), player.played)
    }
}
