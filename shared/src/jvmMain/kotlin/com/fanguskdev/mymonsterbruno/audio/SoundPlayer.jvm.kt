package com.fanguskdev.mymonsterbruno.audio

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.LinkedBlockingQueue
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.SourceDataLine

internal actual fun soundPlayer(): SoundPlayer = DesktopSoundPlayer()

/**
 * The sounds on the desktop: one line to the sound card, open while the game is, and a thread of its own that mixes into
 * it whatever is playing. Nothing opens or closes while the game plays: with a Clip for each sound, cutting one that was
 * playing blocked Java Sound on macOS for up to two seconds each time, and the strawberries, where each count cuts the one
 * before, went silent for good. The game only leaves orders in a queue, so it never waits for the sound card. Silence if
 * the computer has no way to play them.
 */
internal class DesktopSoundPlayer : SoundPlayer {
    private val sounds = ConcurrentHashMap<String, ShortArray>()
    private val orders = LinkedBlockingQueue<Order>()

    init {
        Thread(::run, "audio").apply { isDaemon = true }.start()
    }

    override fun load(name: String, wav: ByteArray) {
        readWav(wav)?.let { sounds[name] = toLineFormat(it) }
    }

    override fun play(name: String, volume: Float) {
        val samples = sounds[name] ?: return
        orders.put(Order.Play(Playing(name, samples, volume)))
    }

    override fun stop(name: String) = orders.put(Order.Stop(name))

    override fun stopAll() = orders.put(Order.StopAll)

    override fun release() = orders.put(Order.Release)

    /** How many orders wait for the thread of the line: it takes them every chunk, so a few at most and only for a moment. */
    val backlog: Int get() = orders.size

    /** The thread of the line: it waits for something to play, then mixes and writes it a chunk at a time. */
    private fun run() {
        val line = runCatching { openLine() }.getOrNull()
        val playing = ArrayList<Playing>()
        val chunk = ShortArray(CHUNK_FRAMES)
        val bytes = ByteArray(CHUNK_FRAMES * BYTES_PER_SAMPLE)
        try {
            while (true) {
                if (playing.isEmpty() && !follow(orders.take(), playing)) return
                while (true) {
                    val order = orders.poll() ?: break
                    if (!follow(order, playing)) return
                }
                if (playing.isEmpty()) continue
                if (line == null) {
                    // Nowhere to play it: forget it, so the thread waits instead of spinning.
                    playing.clear()
                    continue
                }
                mix(playing, chunk)
                toBytes(chunk, bytes)
                line.write(bytes, 0, bytes.size)
            }
        } finally {
            line?.close()
            sounds.clear()
        }
    }

    /** Does what [order] says to [playing]; false when the player is released. */
    private fun follow(order: Order, playing: MutableList<Playing>): Boolean {
        when (order) {
            is Order.Play -> {
                playing.removeAll { it.name == order.sound.name }
                playing += order.sound
            }
            is Order.Stop -> playing.removeAll { it.name == order.name }
            Order.StopAll -> playing.clear()
            Order.Release -> return false
        }
        return true
    }

    private fun openLine(): SourceDataLine {
        val format = AudioFormat(LINE_RATE.toFloat(), BITS, 1, true, false)
        return AudioSystem.getSourceDataLine(format).apply {
            open(format, BUFFER_FRAMES * BYTES_PER_SAMPLE)
            start()
        }
    }

    private sealed interface Order {
        data class Play(val sound: Playing) : Order

        data class Stop(val name: String) : Order

        data object StopAll : Order

        data object Release : Order
    }
}

/** A sound that plays: [samples] at [volume], from [position] on. */
internal class Playing(val name: String, val samples: ShortArray, val volume: Float) {
    var position = 0
}

/** Mixes the next [out].size samples of everything [playing] into [out], and forgets the sounds that end. */
internal fun mix(playing: MutableList<Playing>, out: ShortArray) {
    out.fill(0)
    val sounds = playing.iterator()
    while (sounds.hasNext()) {
        val sound = sounds.next()
        val count = minOf(out.size, sound.samples.size - sound.position)
        for (i in 0 until count) {
            val sample = out[i] + (sound.samples[sound.position + i] * sound.volume).toInt()
            out[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        sound.position += count
        if (sound.position >= sound.samples.size) sounds.remove()
    }
}

/** [pcm] as the line plays it: one channel at [LINE_RATE]. The game's files already are; anything else is converted. */
internal fun toLineFormat(pcm: Pcm): ShortArray {
    val channels = pcm.channels.coerceAtLeast(1)
    val frames = pcm.samples.size / channels
    val mono = ShortArray(frames) { frame ->
        var sum = 0
        for (channel in 0 until channels) sum += pcm.samples[frame * channels + channel]
        (sum / channels).toShort()
    }
    if (pcm.rate == LINE_RATE || pcm.rate <= 0 || frames == 0) return mono
    val length = (frames.toLong() * LINE_RATE / pcm.rate).toInt()
    return ShortArray(length) { i -> mono[(i.toLong() * pcm.rate / LINE_RATE).toInt().coerceAtMost(frames - 1)] }
}

private fun toBytes(samples: ShortArray, bytes: ByteArray) {
    for (i in samples.indices) {
        val sample = samples[i].toInt()
        bytes[BYTES_PER_SAMPLE * i] = sample.toByte()
        bytes[BYTES_PER_SAMPLE * i + 1] = (sample shr BITS_PER_BYTE).toByte()
    }
}

/** The rate of the line, the one tools/voces.py and tools/sonidos.py write. */
internal const val LINE_RATE = 22050
private const val BITS = 16
private const val BITS_PER_BYTE = 8
private const val BYTES_PER_SAMPLE = 2

/** What is mixed at a time (23 ms), and what the sound card holds (93 ms): a cut voice stops within a tenth of a second. */
private const val CHUNK_FRAMES = 512
private const val BUFFER_FRAMES = 2048
