package com.fanguskdev.mymonsterbruno.ui

import com.fanguskdev.mymonsterbruno.audio.Audio
import com.fanguskdev.mymonsterbruno.audio.SilentPlayer
import com.fanguskdev.mymonsterbruno.game.SaveData
import com.fanguskdev.mymonsterbruno.game.SaveStore
import com.fanguskdev.mymonsterbruno.game.Session
import com.fanguskdev.mymonsterbruno.storage.MemoryStore
import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/** When Bruno goes to bed (ADR 0006): at once between things, at the end of an island inside one, never halfway. */
class GameStateTest {
    private val monday = 20261102

    /** A game with Bruno hatched and [left] seconds of the day to play. */
    private fun game(left: Int): GameState {
        val store = MemoryStore()
        val minutes = 5
        val played = minutes * Session.SECONDS_IN_MINUTE - left
        SaveStore.save(store, SaveData(hatched = true, dailyMinutes = minutes, playedDay = monday, playedSeconds = played))
        return GameState(store, Audio(SilentPlayer(), CoroutineScope(EmptyCoroutineContext)), Random(1)) { monday }
    }

    @Test
    fun inTheRoomBrunoGoesToBedWhenTheTimeIsOver() {
        val game = game(left = 1)
        assertEquals(Scene.Home, game.scene)
        game.tick()
        assertEquals(Scene.Sleep, game.scene)
        assertTrue(game.wentToBed)
    }

    @Test
    fun onTheMapToo() {
        val game = game(left = 2)
        game.play()
        assertEquals(Scene.Map, game.scene)
        game.tick()
        assertEquals(Scene.Map, game.scene)
        game.tick()
        assertEquals(Scene.Sleep, game.scene)
    }

    @Test
    fun noNewIslandOnceTheTimeIsOver() {
        val game = game(left = 60)
        game.play()
        game.update { it.copy(playedSeconds = it.dailyMinutes * Session.SECONDS_IN_MINUTE) }
        game.goTo(Island.VOWELS)
        assertEquals(Scene.Sleep, game.scene)
    }

    @Test
    fun inAnIslandBrunoWaitsForItsPresent() {
        val game = game(left = 1)
        game.play()
        game.goTo(Island.VOWELS)
        val feed = Scene.Play(Island.VOWELS, 0)
        assertEquals(feed, game.scene)
        repeat(30) { game.tick() }
        assertEquals(feed, game.scene)
        game.finished(feed)
        assertEquals(Scene.Play(Island.VOWELS, 1), game.scene)
        game.finished(Scene.Play(Island.VOWELS, 1))
        assertEquals(Scene.Present(Island.VOWELS), game.scene)
        game.presentGiven(Island.VOWELS)
        assertEquals(Scene.Sleep, game.scene)
        assertEquals(Island.VOWELS, game.lastIsland)
    }

    @Test
    fun talkingWithBrunoEndsInTheRoomAndThenToBed() {
        val game = game(left = 1)
        game.go(Scene.Talk)
        repeat(30) { game.tick() }
        assertEquals(Scene.Talk, game.scene, "no se va a dormir a mitad de una palabra")
        game.back()
        assertEquals(Scene.Home, game.scene)
        game.tick()
        assertEquals(Scene.Sleep, game.scene)
    }

    @Test
    fun writingHerNameEndsInTheRoomAndThenToBed() {
        val game = game(left = 1)
        game.go(Scene.Name)
        repeat(30) { game.tick() }
        assertEquals(Scene.Name, game.scene, "no se va a dormir a mitad de su nombre")
        game.back()
        assertEquals(Scene.Home, game.scene)
        game.tick()
        assertEquals(Scene.Sleep, game.scene)
    }

    @Test
    fun openedAfterTheTimeBrunoIsAlreadyAsleep() {
        val game = game(left = 0)
        assertEquals(Scene.Sleep, game.scene)
        assertFalse(game.wentToBed)
        // A grown-up gives ten minutes more: back to the room.
        game.go(Scene.GrownUps)
        game.update { Session.moreTime(it, monday, 10) }
        game.leaveGrownUps()
        assertEquals(Scene.Home, game.scene)
    }

    @Test
    fun herPhotoIsKeptAndBrunoSeesANewOne() {
        val store = MemoryStore()
        SaveStore.save(store, SaveData(hatched = true, photoSeen = true))
        val game = GameState(store, Audio(SilentPlayer(), CoroutineScope(EmptyCoroutineContext)), Random(1)) { monday }
        val photo = byteArrayOf(1, 2, 3)
        game.choosePhoto(photo)
        assertFalse(game.data.photoSeen, "una foto nueva, y Bruno aún no la ha visto")
        // The next time the game opens, it is still there.
        val again = GameState(store, Audio(SilentPlayer(), CoroutineScope(EmptyCoroutineContext)), Random(1)) { monday }
        assertEquals(photo.toList(), again.photo?.toList())
        again.removePhoto()
        assertEquals(null, again.photo)
        assertEquals(null, GameState(store, Audio(SilentPlayer(), CoroutineScope(EmptyCoroutineContext)), Random(1)) { monday }.photo)
    }

    @Test
    fun theStartScreenLeadsWhereTheGameWasAndCountsNoTime() {
        fun opened(save: SaveData): GameState {
            val store = MemoryStore()
            SaveStore.save(store, save)
            return GameState(store, Audio(SilentPlayer(), CoroutineScope(EmptyCoroutineContext)), Random(1), splash = true) { monday }
        }
        val egg = opened(SaveData())
        assertEquals(Scene.Splash, egg.scene)
        egg.leaveSplash()
        assertEquals(Scene.Hatch, egg.scene)

        val room = opened(SaveData(hatched = true, moodDay = monday))
        repeat(5) { room.tick() }
        assertEquals(0, room.data.playedSeconds, "la pantalla de inicio no cuenta minutos")
        room.leaveSplash()
        assertEquals(Scene.Home, room.scene)

        // Opened after the time of the day: Bruno is already asleep, without yawning nor a mission.
        val bed = opened(SaveData(hatched = true, dailyMinutes = 5, playedDay = monday, playedSeconds = 5 * Session.SECONDS_IN_MINUTE))
        bed.leaveSplash()
        assertEquals(Scene.Sleep, bed.scene)
        assertFalse(bed.wentToBed)
    }

    @Test
    fun brunoAsksHowSheFeelsOnceADayWhenTheGameOpens() {
        val store = MemoryStore()
        SaveStore.save(store, SaveData(hatched = true))
        var today = monday

        fun opened() = GameState(store, Audio(SilentPlayer(), CoroutineScope(EmptyCoroutineContext)), Random(1), splash = true) { today }
        val first = opened()
        first.leaveSplash()
        assertEquals(Scene.Mood, first.scene)
        first.back()
        assertEquals(Scene.Home, first.scene, "la casa lo deja sin contestar")
        val again = opened()
        again.leaveSplash()
        assertEquals(Scene.Home, again.scene, "una vez al día, aunque no conteste")
        today = monday + 1
        val tomorrow = opened()
        tomorrow.leaveSplash()
        assertEquals(Scene.Mood, tomorrow.scene)
    }

    @Test
    fun onTheIslandOfNumbersTheCountdownAndTheLineTakeTurnsByTheDaysSheGoes() {
        val store = MemoryStore()
        SaveStore.save(store, SaveData(hatched = true))
        var today = monday

        fun secondGame(): Activity {
            val game = GameState(store, Audio(SilentPlayer(), CoroutineScope(EmptyCoroutineContext)), Random(1)) { today }
            game.goTo(Island.NUMBERS)
            game.finished(Scene.Play(Island.NUMBERS, 0))
            assertEquals(Scene.Play(Island.NUMBERS, 1), game.scene)
            return game.activities(Island.NUMBERS)[1]
        }
        assertEquals(Activity.COUNTDOWN, secondGame(), "el primer día, la cuenta atrás")
        assertEquals(Activity.COUNTDOWN, secondGame(), "el mismo día, la misma")
        today = monday + 3
        assertEquals(Activity.NUMBER_LINE, secondGame(), "el siguiente día que va, aunque no sea el siguiente, la recta")
        today = monday + 4
        assertEquals(Activity.COUNTDOWN, secondGame())
    }

    @Test
    fun theEggComesFirst() {
        val game = GameState(MemoryStore(), Audio(SilentPlayer(), CoroutineScope(EmptyCoroutineContext)), Random(1)) { monday }
        assertEquals(Scene.Hatch, game.scene)
        game.tick()
        assertEquals(0, game.data.playedSeconds)
    }
}
