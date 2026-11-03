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
