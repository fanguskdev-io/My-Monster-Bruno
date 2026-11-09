package com.fanguskdev.mymonsterbruno.audio

import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.test.Test
import kotlin.test.assertEquals

/** The soft music of the islands (ADR 0038): the one of where she is, once it is loaded, and never with it off. */
class MusicTest {
    private class Recorder : SoundPlayer {
        val calls = mutableListOf<String>()

        override fun load(name: String, wav: ByteArray) = Unit

        override fun play(name: String, volume: Float) {
            calls += "play $name"
        }

        override fun loop(name: String, volume: Float) {
            calls += "loop $name"
        }

        override fun stop(name: String) {
            calls += "stop $name"
        }

        override fun stopAll() {
            calls += "stopAll"
        }

        override fun release() = Unit
    }

    private val piece = Pcm(ShortArray(1600), channels = 1, rate = 16000).toWav()

    @Test
    fun theMusicOfWhereSheIsOnceLoadedAndOnlyWhenItIsOn() {
        val player = Recorder()
        val audio = Audio(player, CoroutineScope(EmptyCoroutineContext))
        audio.music(Music.LETTERS)
        assertEquals(emptyList(), player.calls, "sin cargar, nada")
        audio.loaded("musica_letras", piece)
        audio.loaded("musica_numeros", piece)
        assertEquals(listOf("loop musica_letras"), player.calls, "en cuanto se carga, suena en bucle")
        audio.music(Music.LETTERS)
        assertEquals(1, player.calls.size, "la misma, sin empezar otra vez")
        audio.music(Music.NUMBERS)
        assertEquals(listOf("stop musica_letras", "loop musica_numeros"), player.calls.drop(1), "otra isla, otra música")
        player.calls.clear()
        audio.musicOn = false
        audio.musicOn = true
        audio.music(null)
        assertEquals(listOf("stop musica_numeros", "loop musica_numeros", "stop musica_numeros"), player.calls)
    }

    @Test
    fun afterTheBackgroundItSoundsAgain() {
        val player = Recorder()
        val audio = Audio(player, CoroutineScope(EmptyCoroutineContext))
        audio.loaded("musica_colores", piece)
        audio.music(Music.COLORS)
        audio.pause()
        audio.resume()
        assertEquals(listOf("loop musica_colores", "stopAll", "loop musica_colores"), player.calls)
        player.calls.clear()
        audio.musicOn = false
        audio.pause()
        audio.resume()
        assertEquals(listOf("stop musica_colores", "stopAll"), player.calls, "apagada, no vuelve")
    }
}
