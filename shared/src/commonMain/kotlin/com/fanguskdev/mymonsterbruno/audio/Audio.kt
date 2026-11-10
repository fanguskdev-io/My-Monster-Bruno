package com.fanguskdev.mymonsterbruno.audio

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mymonsterbruno.shared.generated.resources.Res

/**
 * The voice and the sounds of the game. Bruno and the narrator never talk over each other: each new line cuts the one
 * before. [say] waits until the lines have been said, so a screen can go on when the voice ends; with the voice off it
 * waits a moment instead, so the game keeps its pace without words. In the background nothing is said: the next line
 * waits until the game is on screen again.
 */
internal class Audio(
    private val player: SoundPlayer,
    private val scope: CoroutineScope,
    private val read: suspend (String) -> ByteArray? = { runCatching { Res.readBytes(it) }.getOrNull() },
) {
    var voiceOn = true
    var effectsOn = true

    /**
     * Whether Bruno and the narrator speak a little slower, with the same voice (ADR 0042): each line is stretched when it
     * is said, and the pause after it is longer.
     */
    var slow = false

    /** The lines said slow lately, stretched, so that the ones that come back are ready: the latest last. */
    private val slowed = LinkedHashMap<String, Slowed>()

    /**
     * Whether the game is on screen, and how many times it has gone to the background: in the background the voice waits,
     * and [tell] knows whether that is what cut a line.
     */
    private val onScreen = MutableStateFlow(true)
    private var pauses = 0

    /** Whether the music of the islands sounds (ADR 0038); off, it stops at once. */
    var musicOn = true
        set(value) {
            field = value
            refreshMusic()
        }

    /** The music of where she is, and the one that is sounding: they differ until it is loaded, or with the music off. */
    private var music: Music? = null
    private var musicPlaying: Music? = null

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
            for (piece in Music.entries) loadOne("$MUSIC/${piece.file}.wav", musicName(piece))
        }
    }

    /** The music of where she is now, or none: it changes only if it is another (ADR 0038). */
    fun music(piece: Music?) {
        music = piece
        refreshMusic()
    }

    /** Plays the music that should sound, if it is on and loaded, and stops the one that should not. */
    private fun refreshMusic() {
        val wanted = music?.takeIf { musicOn && musicName(it) in millis }
        if (wanted == musicPlaying) return
        musicPlaying?.let { player.stop(musicName(it)) }
        musicPlaying = wanted
        wanted?.let { player.loop(musicName(it), MUSIC_VOLUME) }
    }

    private fun musicName(piece: Music) = "musica_${piece.file}"

    private suspend fun loadOne(path: String, name: String) {
        val wav = read(path) ?: return
        loaded(name, wav)
    }

    /** [line] stretched, from its file, kept for next time; null if its file cannot be read. */
    private suspend fun slowLine(line: String): Slowed? {
        slowed.remove(line)?.let {
            slowed[line] = it
            return it
        }
        val pcm = read("$VOICES/$line.wav")?.let { readWav(it) } ?: return null
        val made = withContext(Dispatchers.Default) {
            val long = stretch(pcm, SLOW_FACTOR)
            Slowed(long.toWav(), long.millis)
        }
        slowed[line] = made
        while (slowed.size > SLOW_KEPT) slowed.remove(slowed.keys.first())
        return made
    }

    /** [wav] is ready as [name]; if it is the music of where she is, it starts. */
    internal fun loaded(name: String, wav: ByteArray) {
        player.load(name, wav)
        millis[name] = readWav(wav)?.millis ?: 0L
        if (music?.let { musicName(it) } == name) refreshMusic()
    }

    /**
     * Says [lines] one after the other, cutting whatever was being said, and returns when they are said: true, or false if
     * something cut them, another line or the game going to the background.
     */
    suspend fun say(vararg lines: String): Boolean {
        val mine = ++turn
        val me = currentCoroutineContext()[Job]
        if (background != me) background?.cancel()
        try {
            for (line in lines) {
                if (turn != mine) return false
                // In the background, the next line waits until she is back.
                awake()
                if (turn != mine) return false
                cut()
                val long = if (voiceOn && slow) slowLine(line) else null
                if (turn != mine) return false
                when {
                    long != null -> {
                        // One place for the slow line, which takes the place of the one before: little to keep.
                        speaking = SLOW_LINE
                        player.playOnce(SLOW_LINE, long.wav, VOICE_VOLUME)
                        delay(long.millis + SLOW_GAP_MILLIS)
                    }
                    voiceOn -> {
                        speaking = line
                        player.play(line, VOICE_VOLUME)
                        delay((millis[line] ?: UNKNOWN_MILLIS) + GAP_MILLIS)
                    }
                    else -> delay(SILENT_MILLIS)
                }
            }
            return turn == mine
        } finally {
            if (turn == mine) cut()
        }
    }

    /** Waits while the game is in the background. */
    suspend fun awake() {
        onScreen.first { it }
    }

    /**
     * Says [line] to the end: if going to the background cuts it, it is said again from its start once the game is on
     * screen again. For the story at bedtime, which goes on where it was (ADR 0045).
     */
    suspend fun tell(line: String) {
        while (true) {
            val before = pauses
            if (say(line) || pauses == before) return
            awake()
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
        onScreen.value = false
        pauses++
        hush()
        player.stopAll()
        musicPlaying = null
    }

    /** The app is back: the voice goes on, and the music of where she is sounds again. */
    fun resume() {
        onScreen.value = true
        refreshMusic()
    }

    fun release() {
        hush()
        player.release()
    }

    private companion object {
        const val VOICES = "files/voces"
        const val SOUNDS = "files/sonidos"
        const val MUSIC = "files/musica"
        const val ECHO = "su_voz"
        const val VOICE_VOLUME = 1f
        const val EFFECTS_VOLUME = 0.7f

        /** Under the voice, which is at 1: a background, not a song. */
        const val MUSIC_VOLUME = 0.3f
        const val GAP_MILLIS = 250L

        /** The slow voice (ADR 0042): how much longer each line, the pause after it, and how many lines are kept ready. */
        const val SLOW_LINE = "voz_despacio"
        const val SLOW_FACTOR = 1.2f
        const val SLOW_GAP_MILLIS = 450L
        const val SLOW_KEPT = 24
        const val UNKNOWN_MILLIS = 1200L
        const val SILENT_MILLIS = 700L
    }
}

/** A line said slow: its WAV and how long it lasts. */
private class Slowed(val wav: ByteArray, val millis: Long)
