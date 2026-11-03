package com.fanguskdev.mymonsterbruno.ui

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.v2.runComposeUiTest
import com.fanguskdev.mymonsterbruno.AppContent
import com.fanguskdev.mymonsterbruno.audio.Audio
import com.fanguskdev.mymonsterbruno.audio.SilentPlayer
import com.fanguskdev.mymonsterbruno.game.SaveData
import com.fanguskdev.mymonsterbruno.game.SaveStore
import com.fanguskdev.mymonsterbruno.storage.MemoryStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** «Salir del juego», in the grown-ups' corner: there where the platform can close the game, and it saves first. */
@OptIn(ExperimentalTestApi::class)
class GrownUpsExitTest {
    private val day = 20261103

    private fun game(store: MemoryStore): GameState {
        SaveStore.save(store, SaveData(hatched = true, playedDay = day))
        return GameState(store, Audio(SilentPlayer(), CoroutineScope(Dispatchers.Unconfined)), Random(1)) { day }
    }

    @Test
    fun theGrownUpsCloseTheGameAndItIsSavedFirst() = runComposeUiTest {
        val store = MemoryStore()
        val game = game(store)
        // Five seconds played in the room: not written yet, it writes every ten.
        repeat(5) { game.tick() }
        assertEquals(0, SaveStore.load(store).playedSeconds)
        game.go(Scene.GrownUps)
        var closed = false
        setContent { AppContent(game, onExit = { closed = true }) }
        onNodeWithText("Salir del juego").performClick()
        assertTrue(closed)
        assertEquals(5, SaveStore.load(store).playedSeconds)
    }

    @Test
    fun withoutAWayOutThereIsNoButton() = runComposeUiTest {
        val game = game(MemoryStore())
        game.go(Scene.GrownUps)
        setContent { AppContent(game) }
        onNodeWithText("Volver al juego").assertExists()
        onNodeWithText("Salir del juego").assertDoesNotExist()
    }
}
