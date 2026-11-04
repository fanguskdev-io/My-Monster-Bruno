package com.fanguskdev.mymonsterbruno.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.fanguskdev.mymonsterbruno.audio.Audio
import com.fanguskdev.mymonsterbruno.game.SaveData
import com.fanguskdev.mymonsterbruno.game.SaveStore
import com.fanguskdev.mymonsterbruno.game.Session
import com.fanguskdev.mymonsterbruno.storage.KeyValueStore
import com.fanguskdev.mymonsterbruno.storage.PhotoStore
import com.fanguskdev.mymonsterbruno.storage.today
import kotlin.random.Random

/** The islands of the map that have something to do, each with its activities in order (docs/diseno.md). */
internal enum class Island(val activities: List<Activity>) {
    VOWELS(listOf(Activity.FEED, Activity.TRACE)),
    NUMBERS(listOf(Activity.COUNT)),
    COLORS(listOf(Activity.BALLOONS, Activity.SERIES)),
    SHAPES(listOf(Activity.HOUSE)),
    FEELINGS(listOf(Activity.FEELINGS)),
    WORDS(listOf(Activity.WORDS)),
}

/**
 * The things she does on an island: give Bruno the letter he wants, trace a letter, give him strawberries, give him the
 * balloon of the colour he wants, finish a series of balloons, build his house with shapes, know how he feels and find
 * the word that starts as his.
 */
internal enum class Activity { FEED, TRACE, COUNT, BALLOONS, SERIES, HOUSE, FEELINGS, WORDS }

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

    /** Whether Bruno has asked her, since the game opened, to teach him her name (ADR 0017). */
    var askedForName = false

    /** Whether she arrived at bed by playing (Bruno yawns and says goodnight) or the game opened already asleep. */
    var wentToBed = false
        private set

    init {
        audio.voiceOn = data.voiceOn
        audio.effectsOn = data.effectsOn
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

    fun go(next: Scene) {
        if (next == Scene.Sleep) wentToBed = scene != Scene.Sleep && scene != Scene.GrownUps && scene != Scene.Splash
        scene = next
    }

    /** From the start screen to where the game was: the egg, the room or, if the time of the day is over, the bed. */
    fun leaveSplash() = go(firstScene())

    /** From the room to the map, or to bed if the time is over. */
    fun play() = go(if (timeUp) Scene.Sleep else Scene.Map)

    /** To [island], or to bed if the time is over: no new island once it is. */
    fun goTo(island: Island) = go(if (timeUp) Scene.Sleep else Scene.Play(island, 0))

    /** The activity of [scene] is over: the next one of its island, or the present. */
    fun finished(scene: Scene.Play) {
        val next = scene.step + 1
        go(if (next < scene.island.activities.size) Scene.Play(scene.island, next) else Scene.Present(scene.island))
    }

    /** The present is given: the island counts as done and she goes back to the map, or to bed. */
    fun presentGiven(island: Island) {
        lastIsland = island
        go(if (timeUp) Scene.Sleep else Scene.Map)
    }

    /** Back from wherever she is, one step towards the room. */
    fun back() {
        when (scene) {
            Scene.Map, Scene.Calm, Scene.Talk, Scene.Name, Scene.GrownUps -> go(if (data.hatched) Scene.Home else Scene.Hatch)
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
