package com.fanguskdev.mymonsterbruno.audio

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import mymonsterbruno.shared.generated.resources.Res

/**
 * The voice and the sounds of the game. Bruno and the narrator never talk over each other: each new line cuts the one
 * before. [say] waits until the lines have been said, so a screen can go on when the voice ends; with the voice off it
 * waits a moment instead, so the game keeps its pace without words.
 */
internal class Audio(private val player: SoundPlayer, private val scope: CoroutineScope) {
    var voiceOn = true
    var effectsOn = true

    /** How long each loaded file lasts, in milliseconds. */
    private val millis = HashMap<String, Long>()
    private var speaking: String? = null

    /** Which call to [say] is the latest: an older one stops as soon as a newer one starts. */
    private var turn = 0
    private var background: Job? = null

    /** Loads every line and every sound, in the background: what is missing stays silent. */
    fun load() {
        scope.launch {
            for (sound in Sfx.entries) loadOne("$SOUNDS/${sound.file}.wav", sound.file)
            for (line in Voice.ALL) loadOne("$VOICES/$line.wav", line)
        }
    }

    private suspend fun loadOne(path: String, name: String) {
        val wav = runCatching { Res.readBytes(path) }.getOrNull() ?: return
        player.load(name, wav)
        millis[name] = readWav(wav)?.millis ?: 0L
    }

    /** Says [lines] one after the other, cutting whatever was being said, and returns when they are said. */
    suspend fun say(vararg lines: String) {
        val mine = ++turn
        val me = currentCoroutineContext()[Job]
        if (background != me) background?.cancel()
        try {
            for (line in lines) {
                if (turn != mine) return
                cut()
                if (voiceOn) {
                    speaking = line
                    player.play(line, VOICE_VOLUME)
                    delay((millis[line] ?: UNKNOWN_MILLIS) + GAP_MILLIS)
                } else {
                    delay(SILENT_MILLIS)
                }
            }
        } finally {
            if (turn == mine) cut()
        }
    }

    /**
     * Plays [voice], her own voice turned into Bruno's, once and as a voice line (ADR 0011): it cuts whatever was being
     * said, it is not heard with the voice off, and it returns when it is over. The player forgets it after.
     */
    suspend fun echo(voice: Pcm) {
        val mine = ++turn
        val me = currentCoroutineContext()[Job]
        if (background != me) background?.cancel()
        cut()
        if (!voiceOn) return
        try {
            speaking = ECHO
            player.playOnce(ECHO, voice.toWav(), VOICE_VOLUME)
            delay(voice.millis + GAP_MILLIS)
        } finally {
            if (turn == mine) cut()
        }
    }

    /** Says [lines] without waiting for them: the screen goes on while they are said. */
    fun speak(vararg lines: String) {
        background?.cancel()
        val job = scope.launch { say(*lines) }
        background = job
    }

    /** Stops the voice at once. */
    fun hush() {
        turn++
        background?.cancel()
        cut()
    }

    private fun cut() {
        speaking?.let { player.stop(it) }
        speaking = null
    }

    fun sfx(sound: Sfx) {
        if (effectsOn) player.play(sound.file, EFFECTS_VOLUME)
    }

    /** The app went to the background: silence until it comes back. */
    fun pause() {
        hush()
        player.stopAll()
    }

    fun release() {
        hush()
        player.release()
    }

    private companion object {
        const val VOICES = "files/voces"
        const val SOUNDS = "files/sonidos"
        const val ECHO = "su_voz"
        const val VOICE_VOLUME = 1f
        const val EFFECTS_VOLUME = 0.7f
        const val GAP_MILLIS = 250L
        const val UNKNOWN_MILLIS = 1200L
        const val SILENT_MILLIS = 700L
    }
}
