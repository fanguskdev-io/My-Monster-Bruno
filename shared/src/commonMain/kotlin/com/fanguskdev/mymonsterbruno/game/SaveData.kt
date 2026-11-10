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
    /** Times she took Bruno home along each path, by path (PathShape.word) (ADR 0031). */
    val paths: Map<String, Int> = emptyMap(),
    /** The last day, as yyyymmdd, she went to the island of letters, and how many days she has gone (ADR 0031). */
    val lettersDay: Int = 0,
    val lettersDays: Int = 0,
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
    /** Times she put Bruno or his ball where she was asked, by place (Place.word) (ADR 0028). */
    val places: Map<String, Int> = emptyMap(),
    /** Times she found how many strawberries Bruno had, by sum (Sum.key) (ADR 0029). */
    val sums: Map<String, Int> = emptyMap(),
    /** The last day, as yyyymmdd, she heard a story at bedtime, and how many she has heard (ADR 0045). */
    val taleDay: Int = 0,
    val talesTold: Int = 0,
    /** The last day, as yyyymmdd, she went to the island of the house, and how many days she has gone (ADR 0044). */
    val homeDay: Int = 0,
    val homeDays: Int = 0,
    /** Times she brushed Bruno's teeth clean (ADR 0044). */
    val brushed: Int = 0,
    /** Times she dressed Bruno right for each weather, by weather (Weather.word) (ADR 0044). */
    val dressed: Map<String, Int> = emptyMap(),
    /** Things she put in their bin, by bin (Bin.word) (ADR 0044). */
    val recycled: Map<String, Int> = emptyMap(),
    /** Times she knew who arrived in each place of a race of animals, by place (Ordinal.word) (ADR 0037). */
    val races: Map<String, Int> = emptyMap(),
    /** Times Bruno's rocket took off when she counted down to zero (ADR 0023). */
    val launches: Int = 0,
    /** The last day, as yyyymmdd, she went to the island of the numbers, and how many days she has gone (ADR 0026). */
    val numbersDay: Int = 0,
    val numbersDays: Int = 0,
    /** Times she found the number before or after the one Bruno was on, on the number line, by side (Side.word) (ADR 0026). */
    val neighbours: Map<String, Int> = emptyMap(),
    /** Times she knew how Bruno felt, by feeling (Feeling.word). */
    val feelings: Map<String, Int> = emptyMap(),
    /** Times she put each story in order, by story (Story.key) (ADR 0025). */
    val stories: Map<String, Int> = emptyMap(),
    /** Words she said to Bruno and clapped with him, by word (TalkWord.word). Her voice itself is never kept. */
    val talked: Map<String, Int> = emptyMap(),
    /** Times she found the word that starts as Bruno's, by the syllable they start with: «pa». */
    val starts: Map<String, Int> = emptyMap(),
    /** Times she found the word that rhymes with Bruno's, by pair (RhymeWord.pair): «gato» for gato and pato. */
    val rhymes: Map<String, Int> = emptyMap(),
    /** Times she wrote her whole name (ADR 0017). */
    val namesWritten: Int = 0,
    /** Whether Bruno has seen her photo and said «¡Mira! ¡Eres tú!» (ADR 0016); a new photo makes it false again. */
    val photoSeen: Boolean = false,
    /** Her favourite animal (Pet.word), which lives in Bruno's room; empty if a grown-up has not chosen one (ADR 0033). */
    val pet: String = "",
    /** Whether Bruno has said that her animal came to live with him; another animal makes it false again. */
    val petSeen: Boolean = false,
    /** The last day, as yyyymmdd, Bruno asked her «¿Cómo te sientes hoy?», whether or not she answered (ADR 0019). */
    val moodDay: Int = 0,
    /** The last day, as yyyymmdd, she said in the assembly what weather it was, and which (Weather.word) (ADR 0030). */
    val weatherDay: Int = 0,
    val weather: String = "",
    /** Times she saw each weather out of her window, by weather (Weather.word). */
    val weathers: Map<String, Int> = emptyMap(),
    /** How she felt each day she said it: the day, as yyyymmdd, to the feeling (Feeling.word); the last [MOOD_DAYS]. */
    val moods: Map<String, String> = emptyMap(),
    /** Whether she got each of the last rounds right at the first try, by game (Choosing.word), to adapt (ADR 0020). */
    val firstTries: Map<String, List<Boolean>> = emptyMap(),
    /** Times she finished each island, by its name. */
    val islands: Map<String, Int> = emptyMap(),
    /** The islands the grown-ups took off the map, by name, and the consonants they took out of the letters (ADR 0041). */
    val hiddenIslands: List<String> = emptyList(),
    val hiddenLetters: List<String> = emptyList(),
    /** What she did each of the last days, by day as yyyymmdd: her week in the grown-ups' corner (ADR 0040). */
    val days: Map<String, DayLog> = emptyMap(),
    /** Where she stuck the sticker of each island on the wall of the room, by its name: x and y on the stage (ADR 0024). */
    val stickers: Map<String, List<Float>> = emptyMap(),
    val dailyMinutes: Int = DEFAULT_MINUTES,
    /** The day [playedSeconds] and [extraSeconds] are for, as yyyymmdd. */
    val playedDay: Int = 0,
    val playedSeconds: Int = 0,
    /** The time a grown-up added for today only. */
    val extraSeconds: Int = 0,
    val voiceOn: Boolean = true,
    val effectsOn: Boolean = true,
    /** Whether the soft music of the islands sounds (ADR 0038). */
    val musicOn: Boolean = true,
    /** Whether Bruno and the narrator speak a little slower (ADR 0042). */
    val slowVoice: Boolean = false,
) {
    val islandsDone: Int get() = islands.values.sum()

    companion object {
        const val DEFAULT_MINUTES = 10
        val MINUTE_CHOICES = listOf(5, 10, 15, 20)
    }
}

/**
 * The save when a grown-up starts again, in their corner: Bruno back in the egg and what she practised forgotten. Her
 * name, today's minutes and the grown-ups' settings stay: the sound, the slow voice, her favourite animal (ADR 0033) and
 * what comes out in the game (ADR 0041). Her photo is kept apart, and stays too.
 */
internal fun SaveData.startedAgain(): SaveData = SaveData(
    childName = childName,
    dailyMinutes = dailyMinutes,
    playedDay = playedDay,
    playedSeconds = playedSeconds,
    extraSeconds = extraSeconds,
    voiceOn = voiceOn,
    effectsOn = effectsOn,
    musicOn = musicOn,
    slowVoice = slowVoice,
    pet = pet,
    hiddenIslands = hiddenIslands,
    hiddenLetters = hiddenLetters,
)

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
