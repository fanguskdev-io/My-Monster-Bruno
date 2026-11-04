package com.fanguskdev.mymonsterbruno.game

import com.fanguskdev.mymonsterbruno.storage.KeyValueStore
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 * Everything the game remembers on the device (ADR 0005): Bruno, what she has practised, the minutes played today and
 * the settings of the grown-ups' corner. Nothing of it leaves the device.
 */
@Serializable
data class SaveData(
    val hatched: Boolean = false,
    val look: BrunoLook = BrunoLook(),
    val childName: String = "",
    /** Times she gave Bruno the right cookie, by vowel. */
    val fed: Map<String, Int> = emptyMap(),
    /** Times she traced each vowel to the end. */
    val traced: Map<String, Int> = emptyMap(),
    /** Times she gave Bruno that many strawberries, by number. */
    val counted: Map<String, Int> = emptyMap(),
    /** Times she gave Bruno the balloon he wanted, by colour (Hue.word). */
    val balloons: Map<String, Int> = emptyMap(),
    /** Series of balloons she has finished. */
    val series: Int = 0,
    /** Pieces she put in Bruno's house, by shape (Shape.word). */
    val shapes: Map<String, Int> = emptyMap(),
    /** Houses she has built for Bruno. */
    val houses: Int = 0,
    /** Times she knew how Bruno felt, by feeling (Feeling.word). */
    val feelings: Map<String, Int> = emptyMap(),
    /** Words she said to Bruno and clapped with him, by word (TalkWord.word). Her voice itself is never kept. */
    val talked: Map<String, Int> = emptyMap(),
    /** Times she found the word that starts as Bruno's, by the syllable they start with: «pa». */
    val starts: Map<String, Int> = emptyMap(),
    /** Times she wrote her whole name (ADR 0017). */
    val namesWritten: Int = 0,
    /** Whether Bruno has seen her photo and said «¡Mira! ¡Eres tú!» (ADR 0016); a new photo makes it false again. */
    val photoSeen: Boolean = false,
    /** The last day, as yyyymmdd, Bruno asked her «¿Cómo te sientes hoy?», whether or not she answered (ADR 0019). */
    val moodDay: Int = 0,
    /** How she felt each day she said it: the day, as yyyymmdd, to the feeling (Feeling.word); the last [MOOD_DAYS]. */
    val moods: Map<String, String> = emptyMap(),
    /** Times she finished each island, by its name. */
    val islands: Map<String, Int> = emptyMap(),
    val dailyMinutes: Int = DEFAULT_MINUTES,
    /** The day [playedSeconds] and [extraSeconds] are for, as yyyymmdd. */
    val playedDay: Int = 0,
    val playedSeconds: Int = 0,
    /** The time a grown-up added for today only. */
    val extraSeconds: Int = 0,
    val voiceOn: Boolean = true,
    val effectsOn: Boolean = true,
) {
    val islandsDone: Int get() = islands.values.sum()

    companion object {
        const val DEFAULT_MINUTES = 10
        val MINUTE_CHOICES = listOf(5, 10, 15, 20)
    }
}

/** Adds one to [key] in a map of counts. */
internal fun Map<String, Int>.plusOne(key: String): Map<String, Int> = this + (key to (this[key] ?: 0) + 1)

/** Reads and writes the [SaveData] as JSON under one key; a missing or broken save starts from nothing. */
internal object SaveStore {
    private const val KEY = "partida"
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    fun load(store: KeyValueStore): SaveData {
        val text = store.getString(KEY) ?: return SaveData()
        return runCatching { json.decodeFromString(SaveData.serializer(), text) }.getOrDefault(SaveData())
    }

    fun save(store: KeyValueStore, data: SaveData) = store.putString(KEY, json.encodeToString(SaveData.serializer(), data))
}
