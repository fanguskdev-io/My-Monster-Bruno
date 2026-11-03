package com.fanguskdev.mymonsterbruno.audio

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.withTimeoutOrNull
import java.util.concurrent.Executors
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.DataLine
import javax.sound.sampled.TargetDataLine

@Composable
internal actual fun rememberMicrophone(): Microphone {
    val microphone = remember { DesktopMicrophone() }
    DisposableEffect(microphone) { onDispose(microphone::release) }
    return microphone
}

/**
 * The microphone on the desktop: a line from the sound card, read on a thread of its own while she talks, at most
 * [MAX_SECONDS]. macOS asks for the microphone itself the first time; a computer without one hears nothing.
 */
private class DesktopMicrophone : Microphone {
    /** One take at a time: a new one waits on this thread until the one before has closed its line. */
    private val worker = Executors.newSingleThreadExecutor { Thread(it, "microphone").apply { isDaemon = true } }
    private var take: Take? = null

    override suspend fun allowed(): Boolean =
        runCatching { AudioSystem.isLineSupported(DataLine.Info(TargetDataLine::class.java, FORMAT)) }.getOrDefault(false)

    override fun start() {
        take?.end()
        val next = Take()
        take = next
        runCatching { worker.execute(next) }.onFailure { next.heard.complete(null) }
    }

    override suspend fun stop(): Pcm? {
        val current = take ?: return null
        take = null
        current.end()
        return withTimeoutOrNull(STOP_MILLIS) { current.heard.await() }
    }

    override fun release() {
        take?.end()
        take = null
        worker.shutdown()
    }

    /** What she says while her finger is down: read until [end], or until [MAX_SECONDS] are full. */
    private class Take : Runnable {
        @Volatile
        private var listening = true

        /** What it heard, once its line is closed: 16-bit PCM, one channel at [RATE]; null if nothing. */
        val heard = CompletableDeferred<Pcm?>()

        fun end() {
            listening = false
        }

        override fun run() {
            heard.complete(runCatching { listen() }.getOrNull())
        }

        private fun listen(): Pcm? {
            if (!listening) return null
            val line = AudioSystem.getTargetDataLine(FORMAT)
            line.open(FORMAT)
            try {
                line.start()
                val bytes = ByteArray(RATE * MAX_SECONDS * BYTES_PER_SAMPLE)
                var size = 0
                while (listening && size < bytes.size) {
                    val read = line.read(bytes, size, minOf(CHUNK_BYTES, bytes.size - size))
                    if (read <= 0) break
                    size += read
                }
                val samples = ShortArray(size / BYTES_PER_SAMPLE) {
                    ((bytes[BYTES_PER_SAMPLE * it].toInt() and 0xFF) or (bytes[BYTES_PER_SAMPLE * it + 1].toInt() shl 8)).toShort()
                }
                return if (samples.isNotEmpty()) Pcm(samples, 1, RATE) else null
            } finally {
                line.close()
            }
        }
    }

    private companion object {
        /** As the game's voices: 22050 samples a second, one channel of 16 bits, signed and little endian. */
        const val RATE = 22050
        const val BYTES_PER_SAMPLE = 2
        val FORMAT = AudioFormat(RATE.toFloat(), 16, 1, true, false)
        const val MAX_SECONDS = 4

        /** What is read at a time (46 ms): she stops, and the line closes within that. */
        const val CHUNK_BYTES = 2048

        /** How long [stop] waits for the take to close its line before it gives up on it. */
        const val STOP_MILLIS = 1000L
    }
}
