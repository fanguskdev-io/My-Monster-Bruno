package com.fanguskdev.mymonsterbruno.audio

import android.media.AudioAttributes
import android.media.SoundPool
import java.io.File

internal actual fun soundPlayer(): SoundPlayer = AndroidSoundPlayer()

/**
 * The sounds on Android: a SoundPool, made for short sounds. It reads files, so each sound is written to the app's cache
 * first (java.io.tmpdir there). What plays last of each sound is remembered, to stop it.
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

    override fun load(name: String, wav: ByteArray) {
        val folder = System.getProperty("java.io.tmpdir") ?: return
        val id = runCatching { pool.load(File(folder, "sonido-$name.wav").apply { writeBytes(wav) }.path, 1) }.getOrNull()
        if (id != null && id > 0) ids[name] = id
    }

    override fun play(name: String, volume: Float) {
        val id = ids[name] ?: return
        val stream = runCatching { pool.play(id, volume, volume, 1, 0, 1f) }.getOrDefault(0)
        if (stream != 0) streams[name] = stream
    }

    override fun stop(name: String) {
        val stream = streams.remove(name) ?: return
        runCatching { pool.stop(stream) }
    }

    override fun stopAll() {
        for (stream in streams.values) runCatching { pool.stop(stream) }
        streams.clear()
    }

    override fun release() {
        pool.release()
        ids.clear()
        streams.clear()
    }

    private companion object {
        const val MAX_STREAMS = 6
    }
}
