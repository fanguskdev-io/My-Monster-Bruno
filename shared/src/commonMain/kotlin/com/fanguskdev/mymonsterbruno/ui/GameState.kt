package com.fanguskdev.mymonsterbruno.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import com.fanguskdev.mymonsterbruno.audio.Audio
import com.fanguskdev.mymonsterbruno.audio.Music
import com.fanguskdev.mymonsterbruno.game.NumbersGame
import com.fanguskdev.mymonsterbruno.game.SaveData
import com.fanguskdev.mymonsterbruno.game.SaveStore
import com.fanguskdev.mymonsterbruno.game.Session
import com.fanguskdev.mymonsterbruno.game.Weather
import com.fanguskdev.mymonsterbruno.game.Weekday
import com.fanguskdev.mymonsterbruno.game.lettersOn
import com.fanguskdev.mymonsterbruno.game.numbersGameToday
import com.fanguskdev.mymonsterbruno.game.numbersOn
import com.fanguskdev.mymonsterbruno.game.pathsToday
import com.fanguskdev.mymonsterbruno.game.plusOne
import com.fanguskdev.mymonsterbruno.game.weekdayOf
import com.fanguskdev.mymonsterbruno.storage.KeyValueStore
import com.fanguskdev.mymonsterbruno.storage.PhotoStore
import com.fanguskdev.mymonsterbruno.storage.Restored
import com.fanguskdev.mymonsterbruno.storage.today
import org.jetbrains.compose.resources.decodeToImageBitmap
import kotlin.random.Random

/** The islands of the map that have something to do, each with its activities in order (docs/diseno.md). */
internal enum class Island(val activities: List<Activity>, val music: Music) {
    VOWELS(listOf(Activity.FEED, Activity.TRACE), Music.LETTERS),
    NUMBERS(listOf(Activity.COUNT, Activity.COUNTDOWN), Music.NUMBERS),
    COLORS(listOf(Activity.BALLOONS, Activity.SERIES), Music.COLORS),
    SHAPES(listOf(Activity.HOUSE, Activity.PLACES), Music.SHAPES),
    FEELINGS(listOf(Activity.FEELINGS, Activity.STORY), Music.FEELINGS),
    WORDS(listOf(Activity.WORDS, Activity.RHYMES), Music.WORDS),
}

/**
 * The things she does on an island: give Bruno the letter he wants, trace a letter or take him home along a path, a day
 * each (ADR 0031), give him strawberries, give him the
 * balloon of the colour he wants, finish a series of balloons, build his house with shapes, know how he feels and find
 * the word that starts as his, and the one that rhymes with his (ADR 0021); count down for his rocket (ADR 0023), or
 * find the number before or after on the number line, or add strawberries, or say who arrived first, second or third in
 * a race of animals, a day each (ADR 0026, 0029 and 0037); put a story
 * in order (ADR 0025); and put Bruno and his ball inside, out of, on and under things (ADR 0028).
 */
internal enum class Activity {
    FEED,
    TRACE,
    PATHS,
    COUNT,
    COUNTDOWN,
    NUMBER_LINE,
    SUMS,
    RACE,
    BALLOONS,
    SERIES,
    HOUSE,
    PLACES,
    FEELINGS,
    STORY,
    WORDS,
    RHYMES,
}

/** Where the game is. */
internal sealed interface Scene {
    /** The start screen, each time the game opens (ADR 0018). */
    data object Splash : Scene

    data object Hatch : Scene

    data object Home : Scene

    data object Map : Scene

    data class Play(val island: Island, val step: Int) : Scene

    data class Present(val island: Island) : Scene

    data object Sleep : Scene

    data object Calm : Scene

    /** «Habla con Bruno», from the room (ADR 0011). */
    data object Talk : Scene

    /** Writing her name, from the bunting of the room (ADR 0017). */
    data object Name : Scene

    /** «¿Cómo te sientes hoy?», the first time she comes to the room each day (ADR 0019). */
    data object Mood : Scene

    /** The assembly, after the feeling of the day: what day it is and what weather (ADR 0030). */
    data object Assembly : Scene

    /** Bruno's wardrobe, from the room: what he wears, his colour and what he has on his head (ADR 0032). */
    data object Wardrobe : Scene

    data object GrownUps : Scene
}

/**
 * The game: what is saved, where it is and the way from one screen to the next. When the minutes of the day are over,
 * the way out of whatever she is doing goes to bed (ADR 0006).
 */
internal class GameState(
    private val store: KeyValueStore,
    val audio: Audio,
    val random: Random = Random.Default,
    splash: Boolean = false,
    private val day: () -> Int = ::today,
) {
    var data by mutableStateOf(Session.onDay(SaveStore.load(store), day()))
        private set

    var scene: Scene by mutableStateOf(if (splash) Scene.Splash else firstScene())
        private set

    /** Her photo, a small square JPEG, if a grown-up chose one (ADR 0016). */
    var photo: ByteArray? by mutableStateOf(PhotoStore.load(store))
        private set

    /** The last island she finished today, for the mission at bedtime. */
    var lastIsland: Island? = null
        private set

    /** Whether Bruno has said hello since the game opened. */
    var greeted = false

    /** Whether Bruno has invited her to the islands since the game opened. */
    var invitedToIsland = false

    /** Whether Bruno has suggested an island on the map since the game opened (ADR 0035). */
    var suggestedIsland = false

    /** Whether Bruno has asked her, since the game opened, to teach him her name (ADR 0017). */
    var askedForName = false

    /** Whether she arrived at bed by playing (Bruno yawns and says goodnight) or the game opened already asleep. */
    var wentToBed = false
        private set

    init {
        audio.voiceOn = data.voiceOn
        audio.effectsOn = data.effectsOn
        audio.musicOn = data.musicOn
    }

    private fun firstScene(): Scene = when {
        !data.hatched -> Scene.Hatch
        Session.timeUp(data, day()) -> Scene.Sleep
        else -> Scene.Home
    }

    val timeUp: Boolean get() = Session.timeUp(data, day())

    fun update(change: (SaveData) -> SaveData) {
        data = change(data)
        audio.voiceOn = data.voiceOn
        audio.effectsOn = data.effectsOn
        audio.musicOn = data.musicOn
        SaveStore.save(store, data)
    }

    /**
     * A second more on screen; written to the device every [SAVE_EVERY] seconds. In the room or on the map she is between
     * things, so there Bruno goes to bed as soon as the time is over; anywhere else, when what she is doing ends.
     */
    fun tick() {
        if (scene == Scene.Splash || scene == Scene.Hatch || scene == Scene.Sleep || scene == Scene.GrownUps) return
        data = Session.tick(data, day(), 1)
        if (data.playedSeconds % SAVE_EVERY == 0) SaveStore.save(store, data)
        if (timeUp && (scene == Scene.Home || scene == Scene.Map)) go(Scene.Sleep)
    }

    fun save() = SaveStore.save(store, data)

    /** A grown-up chose her [photo]: it hangs in the room, and Bruno will see it the next time she is there. */
    fun choosePhoto(photo: ByteArray) {
        PhotoStore.save(store, photo)
        this.photo = photo
        update { it.copy(photoSeen = false) }
    }

    /** A grown-up took her photo away: it is deleted from the device. */
    fun removePhoto() {
        PhotoStore.remove(store)
        photo = null
    }

    /**
     * A grown-up recovered a copy of her game (ADR 0039): it takes the place of this one, her photo too, and the game goes
     * back to where that copy was when the grown-ups' corner closes.
     */
    fun restore(copy: Restored) {
        update { Session.onDay(copy.data, day()) }
        val restored = copy.photo
        if (restored != null) {
            PhotoStore.save(store, restored)
            photo = restored
        } else {
            removePhoto()
        }
    }

    fun go(next: Scene) {
        if (next == Scene.Sleep) wentToBed = scene != Scene.Sleep && scene != Scene.GrownUps && scene != Scene.Splash
        scene = next
    }

    /**
     * From the start screen to where the game was: the egg, the room or, if the time of the day is over, the bed. The first
     * time each day, before the room, Bruno asks her how she feels (ADR 0019): once, even if she does not answer.
     */
    fun leaveSplash() {
        val first = firstScene()
        if (first == Scene.Home && data.moodDay != day()) {
            update { it.copy(moodDay = day()) }
            go(Scene.Mood)
        } else {
            go(first)
        }
    }

    /** The feeling of the day is said: the assembly follows, once a day (ADR 0030). */
    fun moodDone() = go(if (data.weatherDay == day()) Scene.Home else Scene.Assembly)

    /** The day of the week today, for the calendar of the assembly. */
    val weekday: Weekday get() = weekdayOf(day())

    /** What she saw out of her window today, said in the assembly; null if she has not said it today. */
    val weather: Weather? get() = if (data.weatherDay == day()) Weather.entries.firstOrNull { it.word == data.weather } else null

    /** She said in the assembly what weather it is: the window of the room shows it until tomorrow. */
    fun sawWeather(weather: Weather) =
        update { it.copy(weatherDay = day(), weather = weather.word, weathers = it.weathers.plusOne(weather.word)) }

    /** From the room to the map, or to bed if the time is over. */
    fun play() = go(if (timeUp) Scene.Sleep else Scene.Map)

    /** To [island], or to bed if the time is over: no new island once it is. */
    fun goTo(island: Island) {
        if (timeUp) return go(Scene.Sleep)
        if (island == Island.NUMBERS) update { it.numbersOn(day()) }
        if (island == Island.VOWELS) update { it.lettersOn(day()) }
        go(Scene.Play(island, 0))
    }

    /**
     * What she does on [island] today: on the island of the numbers, after the strawberries, the countdown one day she
     * goes, the number line the next, the sums the next and the race the next (ADR 0026, 0029 and 0037); on the island of letters, after
     * Comeletras, tracing letters one day and Bruno's paths the next (ADR 0031).
     */
    fun activities(island: Island): List<Activity> {
        if (island == Island.VOWELS && data.pathsToday) return listOf(Activity.FEED, Activity.PATHS)
        if (island != Island.NUMBERS) return island.activities
        val second = when (data.numbersGameToday) {
            NumbersGame.COUNTDOWN -> Activity.COUNTDOWN
            NumbersGame.NUMBER_LINE -> Activity.NUMBER_LINE
            NumbersGame.SUMS -> Activity.SUMS
            NumbersGame.RACE -> Activity.RACE
        }
        return listOf(Activity.COUNT, second)
    }

    /** The activity of [scene] is over: the next one of its island, or the present. */
    fun finished(scene: Scene.Play) {
        val next = scene.step + 1
        go(if (next < activities(scene.island).size) Scene.Play(scene.island, next) else Scene.Present(scene.island))
    }

    /** The present is given: the island counts as done and she goes back to the map, or to bed. */
    fun presentGiven(island: Island) {
        lastIsland = island
        go(if (timeUp) Scene.Sleep else Scene.Map)
    }

    /** Back from wherever she is, one step towards the room. */
    fun back() {
        when (scene) {
            Scene.Map, Scene.Calm, Scene.Talk, Scene.Name, Scene.Mood, Scene.Assembly, Scene.Wardrobe, Scene.GrownUps ->
                go(if (data.hatched) Scene.Home else Scene.Hatch)
            is Scene.Play, is Scene.Present -> go(if (timeUp) Scene.Sleep else Scene.Map)
            else -> Unit
        }
    }

    /** The grown-ups' corner is closed: back to the room, or to bed if there is no time left. */
    fun leaveGrownUps() = go(
        when {
            !data.hatched -> Scene.Hatch
            timeUp -> Scene.Sleep
            else -> Scene.Home
        },
    )

    /** Today, as yyyymmdd. */
    fun currentDay(): Int = day()

    private companion object {
        const val SAVE_EVERY = 10
    }
}

/**
 * Her photo as an image, decoded once while a screen is on: in the room, the feeling of the day, the grown-ups' corner,
 * Bruno's boat and the mission card (ADR 0016, ADR 0034). Null without one, or if it cannot be read.
 */
@Composable
internal fun rememberPhoto(game: GameState): ImageBitmap? =
    remember(game.photo) { game.photo?.let { runCatching { it.decodeToImageBitmap() }.getOrNull() } }
