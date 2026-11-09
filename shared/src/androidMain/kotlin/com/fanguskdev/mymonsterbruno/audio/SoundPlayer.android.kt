package com.fanguskdev.mymonsterbruno.audio

import android.media.AudioAttributes
import android.media.SoundPool
import java.io.File

internal actual fun soundPlayer(): SoundPlayer = AndroidSoundPlayer()

/**
 * The sounds on Android: a SoundPool, made for short sounds. It reads files, so each sound is written to the app's cache
 * first (java.io.tmpdir there). What plays last of each sound is remembered, to stop it. SoundPool reads in the background
 * and says when it has a sound on the thread that made the player, the main one, where the game calls it too.
 */
private class AndroidSoundPlayer : SoundPlayer {
    private val pool =
        SoundPool.Builder()
            .setMaxStreams(MAX_STREAMS)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build(),
            ).build()
    private val ids = HashMap<String, Int>()
    private val streams = HashMap<String, Int>()

    /** What [playOnce] loaded and SoundPool is still reading, by its id: it plays as soon as SoundPool has it. */
    private val waiting = HashMap<Int, Waiting>()

    init {
        pool.setOnLoadCompleteListener { _, id, status ->
            val sound = waiting.remove(id)
            if (sound != null && status == 0) start(sound.name, id, sound.volume)
        }
    }

    override fun load(name: String, wav: ByteArray) {
        val folder = System.getProperty("java.io.tmpdir") ?: return
        val id = runCatching { pool.load(File(folder, "sonido-$name.wav").apply { writeBytes(wav) }.path, 1) }.getOrNull()
        if (id != null && id > 0) ids[name] = id
    }

    override fun play(name: String, volume: Float) {
        val id = ids[name] ?: return
        start(name, id, volume)
    }

    /** The music: over and over, and the first to give way if SoundPool runs out of streams (priority 0). */
    override fun loop(name: String, volume: Float) {
        val id = ids[name] ?: return
        stop(name)
        val stream = runCatching { pool.play(id, volume, volume, 0, -1, 1f) }.getOrDefault(0)
        if (stream != 0) streams[name] = stream
    }

    /** Puts [wav] in the place of what [name] was, and plays it when SoundPool has read it. */
    override fun playOnce(name: String, wav: ByteArray, volume: Float) {
        stop(name)
        ids.remove(name)?.let { runCatching { pool.unload(it) } }
        val folder = System.getProperty("java.io.tmpdir") ?: return
        val file = File(folder, "voz-$name.wav")
        val id = runCatching { pool.load(file.apply { writeBytes(wav) }.path, 1) }.getOrNull()
        // SoundPool opens the file before load returns and keeps it open while it reads: it can go at once, so her voice
        // never stays on the device.
        file.delete()
        if (id == null || id <= 0) return
        ids[name] = id
        waiting[id] = Waiting(name, volume)
    }

    override fun stop(name: String) {
        waiting.values.removeAll { it.name == name }
        val stream = streams.remove(name) ?: return
        runCatching { pool.stop(stream) }
    }

    override fun stopAll() {
        waiting.clear()
        for (stream in streams.values) runCatching { pool.stop(stream) }
        streams.clear()
    }

    override fun release() {
        pool.release()
        ids.clear()
        streams.clear()
        waiting.clear()
    }

    /** Plays the sound [id] as [name], from its start. */
    private fun start(name: String, id: Int, volume: Float) {
        val stream = runCatching { pool.play(id, volume, volume, 1, 0, 1f) }.getOrDefault(0)
        if (stream != 0) streams[name] = stream
    }

    /** A sound of [playOnce] that waits for SoundPool, to play as [name] at [volume]. */
    private class Waiting(val name: String, val volume: Float)

    private companion object {
        const val MAX_STREAMS = 8
    }
}
