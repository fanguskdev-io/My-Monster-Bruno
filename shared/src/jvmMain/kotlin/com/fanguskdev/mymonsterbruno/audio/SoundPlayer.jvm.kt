package com.fanguskdev.mymonsterbruno.audio

import java.io.ByteArrayInputStream
import java.util.concurrent.Executors
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.Clip
import javax.sound.sampled.FloatControl
import javax.sound.sampled.LineEvent
import kotlin.math.log10

/** Where every Clip is opened and told what to do: a thread of its own, so the window never waits for the sound card. */
private val audioThread =
    Executors.newSingleThreadExecutor { task -> Thread(task, "audio").apply { isDaemon = true } }

private fun onAudioThread(block: () -> Unit) {
    audioThread.execute { runCatching(block) }
}

internal actual fun soundPlayer(): SoundPlayer = DesktopSoundPlayer()

/**
 * The sounds on the desktop: the bytes of each kept in memory and a Clip opened only while it plays, so the many voice
 * lines do not hold a line of the sound card each; silence if the computer has no way to play them.
 */
private class DesktopSoundPlayer : SoundPlayer {
    private val sounds = HashMap<String, ByteArray>()
    private val playing = HashMap<String, Clip>()

    override fun load(name: String, wav: ByteArray) = onAudioThread {
        sounds[name] = wav
    }

    override fun play(name: String, volume: Float) = onAudioThread {
        val wav = sounds[name] ?: return@onAudioThread
        playing.remove(name)?.close()
        val clip = AudioSystem.getClip()
        clip.open(AudioSystem.getAudioInputStream(ByteArrayInputStream(wav)))
        clip.setVolume(volume)
        clip.addLineListener { event ->
            if (event.type == LineEvent.Type.STOP) {
                onAudioThread {
                    if (playing[name] === clip) playing.remove(name)
                    clip.close()
                }
            }
        }
        playing[name] = clip
        clip.start()
    }

    override fun stop(name: String) = onAudioThread {
        playing.remove(name)?.close()
    }

    override fun stopAll() = onAudioThread {
        for (clip in playing.values) clip.close()
        playing.clear()
    }

    override fun release() = onAudioThread {
        for (clip in playing.values) clip.close()
        playing.clear()
        sounds.clear()
    }
}

/** Its volume, from 0 to 1, if the computer lets it change. */
private fun Clip.setVolume(volume: Float) {
    if (!isControlSupported(FloatControl.Type.MASTER_GAIN)) return
    val gain = getControl(FloatControl.Type.MASTER_GAIN) as? FloatControl ?: return
    gain.value = (DECIBELS * log10(volume.coerceIn(MIN_VOLUME, 1f))).coerceIn(gain.minimum, gain.maximum)
}

private const val DECIBELS = 20f
private const val MIN_VOLUME = 0.0001f
