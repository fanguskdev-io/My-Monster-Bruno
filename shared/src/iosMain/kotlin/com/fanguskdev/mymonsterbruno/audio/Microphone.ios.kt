package com.fanguskdev.mymonsterbruno.audio

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.AVFAudio.AVAudioApplication
import platform.AVFAudio.AVAudioApplicationRecordPermissionGranted
import platform.AVFAudio.AVAudioApplicationRecordPermissionUndetermined
import platform.AVFAudio.AVAudioRecorder
import platform.AVFAudio.AVAudioSession
import platform.AVFAudio.AVAudioSessionCategoryOptionDefaultToSpeaker
import platform.AVFAudio.AVAudioSessionCategoryOptions
import platform.AVFAudio.AVAudioSessionCategoryPlayAndRecord
import platform.AVFAudio.AVFormatIDKey
import platform.AVFAudio.AVLinearPCMBitDepthKey
import platform.AVFAudio.AVLinearPCMIsBigEndianKey
import platform.AVFAudio.AVLinearPCMIsFloatKey
import platform.AVFAudio.AVNumberOfChannelsKey
import platform.AVFAudio.AVSampleRateKey
import platform.AVFAudio.setActive
import platform.CoreAudioTypes.kAudioFormatLinearPCM
import platform.Foundation.NSData
import platform.Foundation.NSNumber
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.NSURL
import platform.Foundation.dataWithContentsOfFile
import platform.darwin.dispatch_async
import platform.darwin.dispatch_queue_create
import platform.posix.memcpy
import kotlin.coroutines.resume

/**
 * Where the recorder is made and told what to do: a queue of its own, as the sounds have, because the audio session can
 * keep the thread that changes it busy for a while.
 */
private val microphoneQueue = dispatch_queue_create("com.fanguskdev.mymonsterbruno.microphone", null)

private fun onMicrophoneQueue(block: () -> Unit) = dispatch_async(microphoneQueue, block)

@Composable
internal actual fun rememberMicrophone(): Microphone {
    val microphone = remember { IosMicrophone() }
    DisposableEffect(microphone) { onDispose(microphone::release) }
    return microphone
}

/**
 * The microphone on iOS: an AVAudioRecorder that writes a WAV in the temporary folder, read and deleted when she stops,
 * at most [MAX_SECONDS]. To listen, the audio session is PlayAndRecord out of the loudspeaker, from the first [start] to
 * [release], which puts back what it was: the rest of the game sounds as before, the silent switch included.
 */
@OptIn(ExperimentalForeignApi::class)
private class IosMicrophone : Microphone {
    private val path = NSTemporaryDirectory().removeSuffix("/") + "/microfono.wav"
    private var recorder: AVAudioRecorder? = null

    /** The session as it was before listening, to put it back; null while it has not changed. */
    private var before: Session? = null

    override suspend fun allowed(): Boolean = when (AVAudioApplication.sharedInstance.recordPermission) {
        AVAudioApplicationRecordPermissionGranted -> true
        AVAudioApplicationRecordPermissionUndetermined -> suspendCancellableCoroutine { answer ->
            AVAudioApplication.requestRecordPermissionWithCompletionHandler { granted -> answer.resume(granted) }
        }
        else -> false
    }

    override fun start() = onMicrophoneQueue {
        discard()
        if (AVAudioApplication.sharedInstance.recordPermission != AVAudioApplicationRecordPermissionGranted || !openSession()) {
            return@onMicrophoneQueue
        }
        val next = runCatching { AVAudioRecorder(uRL = NSURL.fileURLWithPath(path), settings = settings(), error = null) }.getOrNull()
        if (next != null && next.prepareToRecord() && next.recordForDuration(MAX_SECONDS)) recorder = next else next?.deleteRecording()
    }

    override suspend fun stop(): Pcm? = suspendCancellableCoroutine { heard -> onMicrophoneQueue { heard.resume(take()) } }

    override fun release() = onMicrophoneQueue {
        discard()
        restoreSession()
    }

    /** Stops the recorder and gives what it heard, deleting its file. */
    private fun take(): Pcm? {
        val current = recorder ?: return null
        recorder = null
        current.stop()
        val wav = NSData.dataWithContentsOfFile(path)?.toByteArray()
        current.deleteRecording()
        return wav?.let(::readWav)?.takeIf { it.samples.isNotEmpty() }
    }

    /** Stops the recorder, if there is one, and deletes what it heard. */
    private fun discard() {
        recorder?.let {
            it.stop()
            it.deleteRecording()
        }
        recorder = null
    }

    /** The session as PlayAndRecord out of the loudspeaker, remembering how it was the first time; false if iOS says no. */
    private fun openSession(): Boolean {
        val session = AVAudioSession.sharedInstance()
        if (before == null) {
            val was = Session(session.category, session.mode, session.categoryOptions)
            val changed = session.setCategory(
                AVAudioSessionCategoryPlayAndRecord,
                withOptions = AVAudioSessionCategoryOptionDefaultToSpeaker,
                error = null,
            )
            if (!changed) return false
            before = was
        }
        return session.setActive(true, error = null)
    }

    /** The session back as it was before listening. */
    private fun restoreSession() {
        val was = before ?: return
        before = null
        AVAudioSession.sharedInstance().setCategory(was.category, mode = was.mode, options = was.options, error = null)
    }

    /** What the recorder writes: 16-bit PCM, little endian, one channel at [RATE], as the game's voices. */
    private fun settings(): Map<Any?, Any> = mapOf(
        AVFormatIDKey to NSNumber(unsignedInt = kAudioFormatLinearPCM),
        AVSampleRateKey to NSNumber(double = RATE),
        AVNumberOfChannelsKey to NSNumber(int = 1),
        AVLinearPCMBitDepthKey to NSNumber(int = BITS),
        AVLinearPCMIsFloatKey to NSNumber(bool = false),
        AVLinearPCMIsBigEndianKey to NSNumber(bool = false),
    )

    private fun NSData.toByteArray(): ByteArray {
        val bytes = ByteArray(length.toInt())
        if (bytes.isNotEmpty()) bytes.usePinned { memcpy(it.addressOf(0), this.bytes, length) }
        return bytes
    }

    /** A category of the audio session, with its mode and options. */
    private class Session(val category: String?, val mode: String?, val options: AVAudioSessionCategoryOptions)

    private companion object {
        const val RATE = 22050.0
        const val BITS = 16
        const val MAX_SECONDS = 4.0
    }
}
