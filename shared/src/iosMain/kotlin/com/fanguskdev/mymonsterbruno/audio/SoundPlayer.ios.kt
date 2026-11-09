package com.fanguskdev.mymonsterbruno.audio

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.AVFAudio.AVAudioPlayer
import platform.Foundation.NSData
import platform.Foundation.dataWithBytes
import platform.darwin.dispatch_async
import platform.darwin.dispatch_queue_create

/**
 * Where every AVAudioPlayer is made and told what to do: a queue of its own, because playing a sound can keep the thread
 * that asks for it busy for some milliseconds, and on the main one that shows as stutters (CaroFarty's lesson).
 */
private val audioQueue = dispatch_queue_create("com.fanguskdev.mymonsterbruno.audio", null)

private fun onAudioQueue(block: () -> Unit) = dispatch_async(audioQueue, block)

internal actual fun soundPlayer(): SoundPlayer = IosSoundPlayer()

/** The sounds on iOS, which respect the silent switch like any game: an AVAudioPlayer ready for each. */
private class IosSoundPlayer : SoundPlayer {
    private val players = HashMap<String, AVAudioPlayer>()

    override fun load(name: String, wav: ByteArray) = onAudioQueue {
        audioPlayer(wav)?.let {
            it.prepareToPlay()
            players[name] = it
        }
    }

    override fun play(name: String, volume: Float) = onAudioQueue {
        players[name]?.let {
            it.numberOfLoops = 0
            it.currentTime = 0.0
            it.volume = volume
            it.play()
        }
    }

    override fun loop(name: String, volume: Float) = onAudioQueue {
        players[name]?.let {
            it.numberOfLoops = -1
            it.currentTime = 0.0
            it.volume = volume
            it.play()
        }
    }

    override fun stop(name: String) = onAudioQueue {
        players[name]?.stop()
    }

    override fun stopAll() = onAudioQueue {
        for (player in players.values) player.stop()
    }

    override fun release() = onAudioQueue {
        for (player in players.values) player.stop()
        players.clear()
    }
}

/** An AVAudioPlayer for the WAV in [wav], or null if iOS cannot read it. */
@OptIn(ExperimentalForeignApi::class)
private fun audioPlayer(wav: ByteArray): AVAudioPlayer? {
    if (wav.isEmpty()) return null
    val data = wav.usePinned { NSData.dataWithBytes(it.addressOf(0), wav.size.toULong()) }
    return runCatching { AVAudioPlayer(data = data, error = null) }.getOrNull()
}
