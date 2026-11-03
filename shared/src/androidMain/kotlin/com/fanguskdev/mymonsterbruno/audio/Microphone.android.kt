package com.fanguskdev.mymonsterbruno.audio

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.withTimeoutOrNull
import java.util.concurrent.Executors

@Composable
internal actual fun rememberMicrophone(): Microphone {
    val context = LocalContext.current
    val microphone = remember { AndroidMicrophone(context.applicationContext) }
    // A new contract each time would register the launcher again at every recomposition.
    val request = remember { ActivityResultContracts.RequestPermission() }
    val launcher = rememberLauncherForActivityResult(request, microphone::answered)
    DisposableEffect(microphone) {
        microphone.launcher = launcher
        onDispose(microphone::release)
    }
    return microphone
}

/**
 * The microphone on Android: an AudioRecord read on a thread of its own while she talks, at most [MAX_SECONDS]. The first
 * time, Android's own dialog asks for it. Called on the main thread, where Android also answers.
 */
private class AndroidMicrophone(private val context: Context) : Microphone {
    /** Opens Android's dialog: there is one while the screen that remembers this microphone is on. */
    var launcher: ActivityResultLauncher<String>? = null

    /** The question Android's dialog has not answered yet. */
    private var answer: CompletableDeferred<Boolean>? = null

    /** One take at a time: a new one waits on this thread until the one before has let the microphone go. */
    private val worker = Executors.newSingleThreadExecutor { Thread(it, "microphone") }
    private var take: Take? = null

    override suspend fun allowed(): Boolean = granted(context) || (answer ?: ask()).await()

    private fun ask(): CompletableDeferred<Boolean> {
        val question = CompletableDeferred<Boolean>()
        answer = question
        val asked = launcher?.let { runCatching { it.launch(Manifest.permission.RECORD_AUDIO) }.isSuccess } ?: false
        if (!asked) answered(false)
        return question
    }

    /** What she (or a grown-up) answered in Android's dialog. */
    fun answered(granted: Boolean) {
        answer?.complete(granted)
        answer = null
    }

    override fun start() {
        take?.end()
        val next = Take(context)
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
        launcher = null
        answered(false)
    }

    /** What she says while her finger is down: read until [end], or until [MAX_SECONDS] are full. */
    private class Take(private val context: Context) : Runnable {
        @Volatile
        private var listening = true

        /** What it heard, once it lets the microphone go: 16-bit PCM, one channel at [RATE]; null if nothing. */
        val heard = CompletableDeferred<Pcm?>()

        fun end() {
            listening = false
        }

        override fun run() {
            heard.complete(runCatching { listen() }.getOrNull())
        }

        private fun listen(): Pcm? {
            if (!listening || !granted(context)) return null
            val minimum = AudioRecord.getMinBufferSize(RATE, CHANNEL, ENCODING)
            if (minimum <= 0) return null
            val record = AudioRecord(MediaRecorder.AudioSource.VOICE_RECOGNITION, RATE, CHANNEL, ENCODING, maxOf(minimum, BUFFER_BYTES))
            try {
                if (record.state != AudioRecord.STATE_INITIALIZED) return null
                record.startRecording()
                val samples = ShortArray(RATE * MAX_SECONDS)
                var size = 0
                while (listening && size < samples.size) {
                    val read = record.read(samples, size, minOf(CHUNK, samples.size - size))
                    if (read <= 0) break
                    size += read
                }
                return if (size > 0) Pcm(samples.copyOf(size), 1, RATE) else null
            } finally {
                runCatching { record.stop() }
                record.release()
            }
        }
    }

    private companion object {
        /** As the game's voices: 22050 samples a second, one channel of 16 bits. */
        const val RATE = 22050
        const val CHANNEL = AudioFormat.CHANNEL_IN_MONO
        const val ENCODING = AudioFormat.ENCODING_PCM_16BIT
        const val MAX_SECONDS = 4

        /** What is read at a time (46 ms): she stops, and it lets the microphone go within that. */
        const val CHUNK = 1024

        /** What Android keeps for the thread while it is busy: half a second, at two bytes a sample. */
        const val BUFFER_BYTES = RATE

        /** How long [stop] waits for the take to let the microphone go before it gives up on it. */
        const val STOP_MILLIS = 1000L

        fun granted(context: Context) = context.checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
    }
}
