package com.fanguskdev.mymonsterbruno.audio

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.io.File
import java.nio.file.Files
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * The check of the desktop version (ADR 0027), with a make-believe recognizer: a small script that answers as
 * tools/reconocer would, so that it runs anywhere, without a microphone and without asking macOS for anything.
 */
class MacWordCheckTest {
    private val folder = Files.createTempDirectory("reconocedor").toFile()

    private val voice = Pcm(ShortArray(VOICE_SAMPLES) { (it % 50 * 100).toShort() }, channels = 1, rate = 22050)

    @AfterTest
    fun clean() {
        folder.deleteRecursively()
    }

    /** A recognizer that runs [script] with sh: the WAV of her voice is $1 and the word Bruno asked for, $2. */
    private fun recognizer(script: String): File = File(folder, "reconocer").apply {
        writeText("#!/bin/sh\n$script\n")
        setExecutable(true)
    }

    @Test
    fun itTakesTheGuessesBestFirstFromHerVoiceAndThenDeletesIt() = runBlocking {
        val copy = File(folder, "copia.wav")
        val path = File(folder, "ruta")
        val check = MacWordCheck(
            recognizer("""cp "$1" "${copy.path}"; echo "$1" > "${path.path}"; echo "Globo"; echo " un $2 "; echo"""),
        )
        assertEquals(listOf("Globo", "un globo"), check.heard(voice, "globo"))
        assertTrue(readWav(copy.readBytes())?.samples?.contentEquals(voice.samples) == true, "no le llegó su voz")
        assertFalse(File(path.readText().trim()).exists(), "su voz se quedó en la carpeta temporal")
    }

    @Test
    fun withoutAnAnswerItCannotTell() = runBlocking {
        assertNull(MacWordCheck(recognizer("exit 2")).heard(voice, "globo"), "sin permiso")
        assertNull(MacWordCheck(recognizer("exit 1")).heard(voice, "globo"), "no ha podido")
        assertNull(MacWordCheck(recognizer("exit 0")).heard(voice, "globo"), "sin apuestas")
    }

    @Test
    fun itCanCheckOnlyIfTheRecognizerSaysSo() = runBlocking {
        assertTrue(MacWordCheck(recognizer("""[ "$1" = "--permiso" ]""")).allowed())
        assertFalse(MacWordCheck(recognizer("exit 1")).allowed())
    }

    @Test
    fun aRecognizerThatTakesTooLongIsLeftBehind() = runBlocking {
        val started = System.nanoTime()
        assertNull(MacWordCheck(recognizer("exec sleep 30"), heardMillis = 300).heard(voice, "globo"))
        assertTrue(System.nanoTime() - started < GIVE_UP_NANOS, "esperó demasiado")
    }

    @Test
    fun whenTheScreenGoesTheRecognizerStops() = runBlocking {
        val check = MacWordCheck(recognizer("exec sleep 30"), heardMillis = 20_000)
        val started = System.nanoTime()
        val heard = async(Dispatchers.Default) { check.heard(voice, "globo") }
        delay(500)
        check.release()
        assertNull(heard.await())
        assertTrue(System.nanoTime() - started < GIVE_UP_NANOS, "no se paró")
    }

    private companion object {
        /** A tenth of a second of her voice. */
        const val VOICE_SAMPLES = 2205

        const val GIVE_UP_NANOS = 5_000_000_000L
    }
}
