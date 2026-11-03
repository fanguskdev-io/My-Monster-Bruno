package com.fanguskdev.mymonsterbruno.audio

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.os.Build
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.speech.RecognitionListener
import android.speech.RecognitionSupport
import android.speech.RecognitionSupportCallback
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.withTimeoutOrNull
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.concurrent.Executors

@Composable
internal actual fun rememberWordCheck(): WordCheck {
    val context = LocalContext.current
    val check = remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) AndroidWordCheck(context.applicationContext) else NoWordCheck()
    }
    DisposableEffect(check) { onDispose(check::release) }
    return check
}

/**
 * The check on Android 13 and later: the recognizer that runs on the device itself, never one over the internet, fed with
 * her voice through a pipe instead of a microphone. It checks only if Spanish of Spain is installed already: it never
 * downloads it. Called on the main thread, as the recognizer wants, which also answers there.
 */
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
private class AndroidWordCheck(private val context: Context) : WordCheck {
    /** Writes her voice into the pipe, waiting whenever it is full until the recognizer reads on. */
    private val writer = Executors.newSingleThreadExecutor { Thread(it, "word check") }

    /** Whether [allowed] found Spanish of Spain installed: until it does, [heard] asks nothing. */
    private var installed = false

    /** The answer the recognizer has not given yet: given up on when the screen goes. */
    private var answer: CompletableDeferred<List<String>?>? = null

    override suspend fun allowed(): Boolean {
        installed = SpeechRecognizer.isOnDeviceRecognitionAvailable(context) &&
            context.checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED &&
            withTimeoutOrNull(SUPPORT_MILLIS) { spanishInstalled() } == true
        return installed
    }

    override suspend fun heard(voice: Pcm, word: String): List<String>? =
        if (installed && voice.rate > 0) withTimeoutOrNull(HEARD_MILLIS) { recognize(voice, word) } else null

    override fun release() {
        installed = false
        answer?.complete(null)
        writer.shutdown()
    }

    /** Whether the recognizer has Spanish of Spain installed on the device: it is only asked, nothing is downloaded. */
    private suspend fun spanishInstalled(): Boolean {
        val support = CompletableDeferred<Boolean>()
        val callback = object : RecognitionSupportCallback {
            override fun onSupportResult(recognitionSupport: RecognitionSupport) {
                support.complete(recognitionSupport.installedOnDeviceLanguages.any { it.equals(LANGUAGE, ignoreCase = true) })
            }

            override fun onError(error: Int) {
                support.complete(false)
            }
        }
        val recognizer = runCatching { SpeechRecognizer.createOnDeviceSpeechRecognizer(context) }.getOrNull() ?: return false
        try {
            runCatching { recognizer.checkRecognitionSupport(request(), context.mainExecutor, callback) }
                .onFailure { support.complete(false) }
            return support.await()
        } finally {
            runCatching { recognizer.destroy() }
        }
    }

    /** What the recognizer understood in [voice], which it reads from a pipe; null if it could not tell. */
    private suspend fun recognize(voice: Pcm, word: String): List<String>? {
        val pipe = runCatching { ParcelFileDescriptor.createPipe() }.getOrNull() ?: return null
        val (source, sink) = pipe
        val heard = CompletableDeferred<List<String>?>()
        answer = heard
        var recognizer: SpeechRecognizer? = null
        try {
            runCatching {
                val speech = SpeechRecognizer.createOnDeviceSpeechRecognizer(context)
                recognizer = speech
                speech.setRecognitionListener(Listener(heard))
                speech.startListening(request(word, source))
                writer.execute { feed(voice, sink) }
            }.onFailure { heard.complete(null) }
            return heard.await()
        } finally {
            if (answer === heard) answer = null
            runCatching { recognizer?.destroy() }
            // The writer, if it is still waiting on a pipe nobody reads, wakes up when its end is closed.
            pipe.forEach { runCatching { it.close() } }
        }
    }

    /** What the recognizer is asked for: Spanish of Spain, on the device. */
    private fun request() = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        .putExtra(RecognizerIntent.EXTRA_LANGUAGE, LANGUAGE)
        .putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        .putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, true)

    /** The same, for [word] in the sound that comes through [source]: one channel of 16 bits at [RATE]. */
    private fun request(word: String, source: ParcelFileDescriptor) = request()
        .putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, MAX_GUESSES)
        .putStringArrayListExtra(RecognizerIntent.EXTRA_BIASING_STRINGS, arrayListOf(word))
        .putExtra(RecognizerIntent.EXTRA_AUDIO_SOURCE, source)
        .putExtra(RecognizerIntent.EXTRA_AUDIO_SOURCE_CHANNEL_COUNT, 1)
        .putExtra(RecognizerIntent.EXTRA_AUDIO_SOURCE_ENCODING, AudioFormat.ENCODING_PCM_16BIT)
        .putExtra(RecognizerIntent.EXTRA_AUDIO_SOURCE_SAMPLING_RATE, RATE)

    /** Writes [voice] into [sink] and closes it, which tells the recognizer that she has finished. */
    private fun feed(voice: Pcm, sink: ParcelFileDescriptor) {
        runCatching { ParcelFileDescriptor.AutoCloseOutputStream(sink).use { it.write(bytes(voice)) } }
    }

    /** [voice] as the recognizer reads it: one channel at [RATE], read between neighbouring samples, 16 bits little endian. */
    private fun bytes(voice: Pcm): ByteArray {
        val samples = voice.higher(voice.rate.toFloat() / RATE).samples
        val bytes = ByteBuffer.allocate(samples.size * 2).order(ByteOrder.LITTLE_ENDIAN)
        bytes.asShortBuffer().put(samples)
        return bytes.array()
    }

    /** Gives [answer] what the recognizer understood, its guesses best first, or null if it could not tell. */
    private class Listener(private val answer: CompletableDeferred<List<String>?>) : RecognitionListener {
        override fun onResults(results: Bundle?) {
            answer.complete(results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION))
        }

        override fun onError(error: Int) {
            answer.complete(null)
        }

        override fun onReadyForSpeech(params: Bundle?) = Unit

        override fun onBeginningOfSpeech() = Unit

        override fun onRmsChanged(rmsdB: Float) = Unit

        override fun onBufferReceived(buffer: ByteArray?) = Unit

        override fun onEndOfSpeech() = Unit

        override fun onPartialResults(partialResults: Bundle?) = Unit

        override fun onEvent(eventType: Int, params: Bundle?) = Unit
    }

    private companion object {
        /** Spanish of Spain, as she speaks it. */
        const val LANGUAGE = "es-ES"

        /** What the recognizer reads: 16000 samples a second. */
        const val RATE = 16000

        /** How many guesses it gives, at most. */
        const val MAX_GUESSES = 5

        /** How long [allowed] waits for the recognizer to say what it has installed. */
        const val SUPPORT_MILLIS = 3000L

        /** How long [heard] waits for the recognizer before it gives up: she is waiting for Bruno. */
        const val HEARD_MILLIS = 6000L
    }
}
