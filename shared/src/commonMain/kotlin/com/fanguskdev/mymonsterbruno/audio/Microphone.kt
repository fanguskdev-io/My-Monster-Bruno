package com.fanguskdev.mymonsterbruno.audio

import androidx.compose.runtime.Composable
import kotlin.math.abs
import kotlin.math.max

/**
 * The microphone, for «Habla con Bruno» (ADR 0011): she keeps her finger on the button while she says the word, and what
 * she said comes back as a sound that is played once and forgotten. Nothing of it is saved nor leaves the device.
 */
interface Microphone {
    /** Whether the game may listen. The first time, the platform asks with its own dialog. */
    suspend fun allowed(): Boolean

    /** Starts listening, if it may and can. */
    fun start()

    /** Stops listening and gives what it heard, 16-bit PCM at any rate, or null if it heard nothing or could not listen. */
    suspend fun stop(): Pcm?

    fun release()
}

/** The microphone of this platform, for as long as the screen that uses it is on: it is let go when the screen goes. */
@Composable
internal expect fun rememberMicrophone(): Microphone

/** A microphone that hears nothing: the tests, the screenshots and a launch without sound. */
class DeafMicrophone : Microphone {
    override suspend fun allowed() = false

    override fun start() = Unit

    override suspend fun stop(): Pcm? = null

    override fun release() = Unit
}

/** One channel: the channels of [this] mixed. */
fun Pcm.mono(): Pcm {
    if (channels <= 1) return this
    val frames = samples.size / channels
    val mixed = ShortArray(frames) { frame ->
        var sum = 0
        for (channel in 0 until channels) sum += samples[frame * channels + channel]
        (sum / channels).toShort()
    }
    return Pcm(mixed, 1, rate)
}

/** One channel without the silence at both ends (quieter than [threshold]), with a little margin; null if all is silence. */
fun Pcm.trimmed(threshold: Int = SILENCE, marginMillis: Int = MARGIN_MILLIS): Pcm? {
    val sound = mono().samples
    val first = sound.indexOfFirst { abs(it.toInt()) > threshold }
    if (first < 0) return null
    val last = sound.indexOfLast { abs(it.toInt()) > threshold }
    val margin = rate * marginMillis / MILLIS
    return Pcm(sound.copyOfRange(max(0, first - margin), minOf(sound.size, last + margin + 1)), 1, rate)
}

/** At the height of the game's voices: its loudest sample at [peak] of the most it can be. */
fun Pcm.normalized(peak: Float = PEAK): Pcm {
    val loudest = samples.maxOfOrNull { abs(it.toInt()) } ?: 0
    if (loudest == 0) return this
    val gain = peak * Short.MAX_VALUE / loudest
    val louder = ShortArray(samples.size) { (samples[it] * gain).toInt().coerceIn(-LOUDEST, LOUDEST).toShort() }
    return Pcm(louder, channels, rate)
}

/** Higher and quicker, [factor] times, read faster between neighbouring samples: a monster's voice, as Bruno's lines. */
fun Pcm.higher(factor: Float): Pcm {
    val sound = mono().samples
    if (factor == 1f || sound.isEmpty()) return mono()
    val length = (sound.size / factor).toInt()
    val out = ShortArray(length) { i ->
        val x = i * factor
        val a = x.toInt().coerceAtMost(sound.lastIndex)
        val b = (a + 1).coerceAtMost(sound.lastIndex)
        val t = x - a
        (sound[a] * (1 - t) + sound[b] * t).toInt().toShort()
    }
    return Pcm(out, 1, rate)
}

/** As a WAV file of 16-bit PCM, the format every player of the game reads. */
fun Pcm.toWav(): ByteArray {
    val data = samples.size * 2
    val bytes = ByteArray(WAV_HEADER + data)
    fun text(at: Int, value: String) = value.encodeToByteArray().copyInto(bytes, at)
    fun int16(at: Int, value: Int) {
        bytes[at] = value.toByte()
        bytes[at + 1] = (value shr 8).toByte()
    }
    fun int32(at: Int, value: Int) {
        int16(at, value and 0xFFFF)
        int16(at + 2, value ushr 16)
    }
    text(0, "RIFF")
    int32(4, WAV_HEADER - 8 + data)
    text(8, "WAVE")
    text(12, "fmt ")
    int32(16, 16)
    int16(20, 1)
    int16(22, channels)
    int32(24, rate)
    int32(28, rate * channels * 2)
    int16(32, channels * 2)
    int16(34, 16)
    text(36, "data")
    int32(40, data)
    samples.forEachIndexed { i, sample -> int16(WAV_HEADER + 2 * i, sample.toInt()) }
    return bytes
}

private const val SILENCE = 600
private const val MARGIN_MILLIS = 60
private const val MILLIS = 1000
private const val PEAK = 0.9f
private const val LOUDEST = 32767
private const val WAV_HEADER = 44
