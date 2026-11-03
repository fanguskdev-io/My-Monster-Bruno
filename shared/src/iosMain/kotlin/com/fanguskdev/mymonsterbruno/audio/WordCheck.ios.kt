package com.fanguskdev.mymonsterbruno.audio

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import platform.Foundation.NSData
import platform.Foundation.NSFileManager
import platform.Foundation.NSLocale
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.NSURL
import platform.Foundation.dataWithBytes
import platform.Foundation.writeToFile
import platform.Speech.SFSpeechRecognitionResult
import platform.Speech.SFSpeechRecognitionTask
import platform.Speech.SFSpeechRecognizer
import platform.Speech.SFSpeechRecognizerAuthorizationStatus.SFSpeechRecognizerAuthorizationStatusAuthorized
import platform.Speech.SFSpeechRecognizerAuthorizationStatus.SFSpeechRecognizerAuthorizationStatusNotDetermined
import platform.Speech.SFSpeechURLRecognitionRequest
import platform.Speech.SFTranscription
import kotlin.coroutines.resume

@Composable
internal actual fun rememberWordCheck(): WordCheck {
    val check = remember { IosWordCheck() }
    DisposableEffect(check) { onDispose(check::release) }
    return check
}

/**
 * The check on iOS: Apple's recognizer of Spanish of Spain, made to work on the iPhone itself and never over the
 * internet, which reads her voice from a WAV in the temporary folder, deleted as soon as it answers. The first time, iOS
 * asks for it with its own dialog. Called on the main thread, where the recognizer also answers.
 */
private class IosWordCheck : WordCheck {
    /** Null if iOS has no recognizer for Spanish of Spain. */
    private val recognizer by lazy { runCatching { SFSpeechRecognizer(locale = NSLocale(localeIdentifier = LANGUAGE)) }.getOrNull() }

    /** The answer the recognizer has not given yet: given up on when the screen goes. */
    private var answer: CompletableDeferred<List<String>?>? = null

    /** Numbers the WAVs, so that a check never reads nor deletes the one of another. */
    private var takes = 0

    override suspend fun allowed(): Boolean = when (SFSpeechRecognizer.authorizationStatus()) {
        SFSpeechRecognizerAuthorizationStatusAuthorized -> onDevice() != null
        SFSpeechRecognizerAuthorizationStatusNotDetermined -> ask() && onDevice() != null
        else -> false
    }

    override suspend fun heard(voice: Pcm, word: String): List<String>? {
        if (SFSpeechRecognizer.authorizationStatus() != SFSpeechRecognizerAuthorizationStatusAuthorized) return null
        val speech = onDevice() ?: return null
        val path = NSTemporaryDirectory().removeSuffix("/") + "/palabra${++takes}.wav"
        val heard = CompletableDeferred<List<String>?>()
        answer = heard
        var task: SFSpeechRecognitionTask? = null
        try {
            if (!write(voice.toWav(), path)) return null
            val request = SFSpeechURLRecognitionRequest(uRL = NSURL.fileURLWithPath(path))
            request.requiresOnDeviceRecognition = true
            request.shouldReportPartialResults = false
            request.contextualStrings = listOf(word)
            task = speech.recognitionTaskWithRequest(request) { result, error ->
                when {
                    result != null && result.final -> heard.complete(guesses(result))
                    error != null -> heard.complete(null)
                }
            }
            return withTimeoutOrNull(HEARD_MILLIS) { heard.await() }
        } finally {
            task?.cancel()
            if (answer === heard) answer = null
            delete(path)
        }
    }

    override fun release() {
        answer?.complete(null)
    }

    /** The recognizer, if it can work on the iPhone itself right now; null if not. */
    private fun onDevice(): SFSpeechRecognizer? = recognizer?.takeIf { it.supportsOnDeviceRecognition && it.available }

    /** Asks with iOS's dialog; whether she (or a grown-up) said yes. iOS answers on a thread of its own. */
    private suspend fun ask(): Boolean = suspendCancellableCoroutine { reply ->
        SFSpeechRecognizer.requestAuthorization { reply.resume(it == SFSpeechRecognizerAuthorizationStatusAuthorized) }
    }

    /** Every transcription of [result], best first. */
    private fun guesses(result: SFSpeechRecognitionResult): List<String> =
        result.transcriptions.mapNotNull { (it as? SFTranscription)?.formattedString }

    /** Writes [wav] into the file at [path]; false if iOS cannot. */
    @OptIn(ExperimentalForeignApi::class)
    private fun write(wav: ByteArray, path: String): Boolean {
        val data = wav.usePinned { NSData.dataWithBytes(it.addressOf(0), wav.size.toULong()) }
        return data.writeToFile(path, atomically = true)
    }

    /** Deletes the file at [path], if there is one. */
    @OptIn(ExperimentalForeignApi::class)
    private fun delete(path: String) {
        NSFileManager.defaultManager.removeItemAtPath(path, error = null)
    }

    private companion object {
        /** Spanish of Spain, as she speaks it. */
        const val LANGUAGE = "es-ES"

        /** How long [heard] waits for the recognizer before it gives up: she is waiting for Bruno. */
        const val HEARD_MILLIS = 6000L
    }
}
