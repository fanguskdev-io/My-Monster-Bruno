package com.fanguskdev.mymonsterbruno.audio

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runInterruptible
import kotlinx.coroutines.withContext
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * The check of the desktop version (ADR 0027): on the Mac, Apple's recognizer of Spanish of Spain, on the Mac itself and
 * never over the internet, as on her iPhone. The game reaches it through a small program of its own, tools/reconocer,
 * which `desktopApp:run` builds and names in [RECOGNIZER]. Without it (another computer, no Xcode, the tests) there is no
 * check, and Bruno takes what he heard, as he always did.
 */
@Composable
internal actual fun rememberWordCheck(): WordCheck {
    val check = remember { recognizer()?.let(::MacWordCheck) ?: NoWordCheck() }
    DisposableEffect(check) { onDispose(check::release) }
    return check
}

/** The system property with the path of the recognizer the desktop version was run with. */
const val RECOGNIZER = "bruno.reconocedor"

/** The recognizer the desktop version was run with, if it is there and can be run. */
private fun recognizer(): File? = System.getProperty(RECOGNIZER)?.let(::File)?.takeIf { it.canExecute() }

/**
 * The check through [recognizer]: `--permiso` asks macOS for it the first time and says whether it can; with a WAV and
 * the word, it writes its guesses, one a line, best first. Her voice goes to a WAV in the temporary folder, deleted as
 * soon as it answers. [heardMillis] and [permissionMillis] are how long each may take before the game gives up.
 */
internal class MacWordCheck(
    private val recognizer: File,
    private val heardMillis: Long = HEARD_MILLIS,
    private val permissionMillis: Long = PERMISSION_MILLIS,
) : WordCheck {
    /** The recognizer at work, if it is: stopped when the screen goes. */
    @Volatile
    private var running: Process? = null

    override suspend fun allowed(): Boolean = call(listOf(PERMISSION), permissionMillis) != null

    override suspend fun heard(voice: Pcm, word: String): List<String>? = withContext(Dispatchers.IO) {
        val wav = File.createTempFile("palabra", ".wav")
        try {
            wav.writeBytes(voice.toWav())
            call(listOf(wav.path, word), heardMillis)?.lines()?.map(String::trim)?.filter(String::isNotEmpty)?.ifEmpty { null }
        } finally {
            wav.delete()
        }
    }

    override fun release() {
        running?.destroy()
    }

    /** Runs the recognizer with [arguments] for [millis] at most: what it wrote, if it ended well; null if not. */
    private suspend fun call(arguments: List<String>, millis: Long): String? = runInterruptible(Dispatchers.IO) {
        val process = ProcessBuilder(listOf(recognizer.path) + arguments)
            .redirectInput(ProcessBuilder.Redirect.from(File(NOTHING)))
            .redirectError(ProcessBuilder.Redirect.DISCARD)
            .start()
        running = process
        try {
            val ended = process.waitFor(millis, TimeUnit.MILLISECONDS)
            if (ended && process.exitValue() == 0) process.inputStream.bufferedReader(Charsets.UTF_8).readText() else null
        } finally {
            process.destroy()
            if (running === process) running = null
        }
    }

    private companion object {
        const val PERMISSION = "--permiso"

        /** The empty input the recognizer reads, as it asks for nothing. */
        const val NOTHING = "/dev/null"

        /** How long the game waits for what the recognizer heard: she is waiting for Bruno. */
        const val HEARD_MILLIS = 7000L

        /** How long it waits, the first time, for a grown-up to answer macOS's dialog. */
        const val PERMISSION_MILLIS = 310_000L
    }
}
