package com.fanguskdev.mymonsterbruno.audio

import kotlin.math.min

/**
 * Plays short sounds, each from its start: the voice lines and the effects. Each platform does it its own way (a SoundPool
 * on Android, an AVAudioPlayer for each sound on iOS, one line mixed by hand on the desktop), never on the thread that asks.
 */
interface SoundPlayer {
    /** Gets [wav] ready to be played as [name]. */
    fun load(name: String, wav: ByteArray)

    /** Plays [name] from its start at [volume] (0 to 1), if it is loaded. */
    fun play(name: String, volume: Float = 1f)

    /** Stops [name] if it is playing. */
    fun stop(name: String)

    /** Stops whatever is playing: the app went to the background. */
    fun stopAll()

    fun release()
}

/** The player of sounds of this platform. */
internal expect fun soundPlayer(): SoundPlayer

/** A player that plays nothing: for the tests, the screenshots and a launch with the sound off. */
class SilentPlayer : SoundPlayer {
    override fun load(name: String, wav: ByteArray) = Unit

    override fun play(name: String, volume: Float) = Unit

    override fun stop(name: String) = Unit

    override fun stopAll() = Unit

    override fun release() = Unit
}

/** A sound in 16-bit PCM: its [samples], interleaved if it has two [channels], at [rate] samples a second. */
class Pcm(val samples: ShortArray, val channels: Int, val rate: Int) {
    /** How long it lasts, in milliseconds. */
    val millis: Long get() = if (rate <= 0 || channels <= 0) 0L else samples.size * MILLIS_IN_SECOND / (channels.toLong() * rate)
}

/** The sound in a WAV file of 16-bit PCM, mono or stereo, or null if it is not one (CaroFarty's reader). */
fun readWav(bytes: ByteArray): Pcm? {
    if (bytes.size < HEADER || bytes.decodeToString(0, 4) != "RIFF" || bytes.decodeToString(8, 12) != "WAVE") return null
    var format = 0
    var channels = 0
    var rate = 0
    var bits = 0
    var offset = HEADER
    while (offset + CHUNK_HEADER <= bytes.size) {
        val id = bytes.decodeToString(offset, offset + 4)
        val size = int32(bytes, offset + 4)
        val body = offset + CHUNK_HEADER
        if (size < 0) return null
        if (id == "fmt " && body + FMT_SIZE <= bytes.size) {
            format = int16(bytes, body)
            channels = int16(bytes, body + 2)
            rate = int32(bytes, body + 4)
            bits = int16(bytes, body + 14)
        } else if (id == "data") {
            if ((format != PCM && format != EXTENSIBLE) || bits != 16 || channels !in 1..2 || rate <= 0) return null
            val end = min(bytes.size.toLong(), body.toLong() + size).toInt()
            val samples = ShortArray((end - body) / 2) { int16(bytes, body + 2 * it).toShort() }
            return Pcm(samples, channels, rate)
        }
        // Chunks take an even number of bytes.
        offset = body + size + (size and 1)
    }
    return null
}

private fun int16(bytes: ByteArray, at: Int): Int = (bytes[at].toInt() and 0xFF) or (bytes[at + 1].toInt() shl 8)

private fun int32(bytes: ByteArray, at: Int): Int = (int16(bytes, at) and 0xFFFF) or (int16(bytes, at + 2) shl 16)

private const val HEADER = 12
private const val CHUNK_HEADER = 8
private const val FMT_SIZE = 16
private const val PCM = 1
private const val EXTENSIBLE = 0xFFFE
private const val MILLIS_IN_SECOND = 1000L
