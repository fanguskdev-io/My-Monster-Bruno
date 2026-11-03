package com.fanguskdev.mymonsterbruno

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.fanguskdev.mymonsterbruno.audio.Audio
import com.fanguskdev.mymonsterbruno.audio.SilentPlayer
import com.fanguskdev.mymonsterbruno.audio.soundPlayer
import com.fanguskdev.mymonsterbruno.storage.KeyValueStore
import com.fanguskdev.mymonsterbruno.ui.GameState
import com.fanguskdev.mymonsterbruno.ui.PlatformBackHandler
import com.fanguskdev.mymonsterbruno.ui.Scene
import com.fanguskdev.mymonsterbruno.ui.screens.CalmScreen
import com.fanguskdev.mymonsterbruno.ui.screens.CountScreen
import com.fanguskdev.mymonsterbruno.ui.screens.FeedScreen
import com.fanguskdev.mymonsterbruno.ui.screens.GrownUpsScreen
import com.fanguskdev.mymonsterbruno.ui.screens.HatchScreen
import com.fanguskdev.mymonsterbruno.ui.screens.HomeScreen
import com.fanguskdev.mymonsterbruno.ui.screens.MapScreen
import com.fanguskdev.mymonsterbruno.ui.screens.PresentScreen
import com.fanguskdev.mymonsterbruno.ui.screens.SleepScreen
import com.fanguskdev.mymonsterbruno.ui.screens.TraceScreen
import kotlinx.coroutines.delay
import com.fanguskdev.mymonsterbruno.ui.Activity as Step

/**
 * Mi monstruo Bruno (docs/diseno.md): the game, the same on Android, iOS and the desktop. What it remembers goes to
 * [store]; [sound] false keeps it silent (the tests, and trying it on the emulator without the Mac's speakers).
 */
@Composable
fun App(store: KeyValueStore, sound: Boolean = true) {
    val scope = rememberCoroutineScope()
    val audio = remember { Audio(if (sound) soundPlayer() else SilentPlayer(), scope) }
    val game = remember { GameState(store, audio) }
    AppContent(game)

    LaunchedEffect(Unit) { audio.load() }
    DisposableEffect(Unit) { onDispose { audio.release() } }
    // The minutes of the day count only while the game is on screen; in the background it is silent.
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    LaunchedEffect(lifecycle) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            try {
                while (true) {
                    delay(TICK_MILLIS)
                    game.tick()
                }
            } finally {
                game.save()
                audio.pause()
            }
        }
    }
}

/** The screen of [game]'s scene. */
@Composable
internal fun AppContent(game: GameState) {
    PlatformBackHandler(enabled = game.scene !is Scene.Home && game.scene !is Scene.Hatch && game.scene !is Scene.Sleep) {
        game.audio.hush()
        game.back()
    }
    Box(Modifier.fillMaxSize().background(Color(0xFF8E89E8))) {
        when (val scene = game.scene) {
            Scene.Hatch -> HatchScreen(game)
            Scene.Home -> HomeScreen(game)
            Scene.Map -> MapScreen(game)
            is Scene.Play -> when (scene.island.activities[scene.step]) {
                Step.FEED -> FeedScreen(game) { game.finished(scene) }
                Step.TRACE -> TraceScreen(game) { game.finished(scene) }
                Step.COUNT -> CountScreen(game) { game.finished(scene) }
            }
            is Scene.Present -> PresentScreen(game, scene.island) { game.presentGiven(scene.island) }
            Scene.Sleep -> SleepScreen(game)
            Scene.Calm -> CalmScreen(game)
            Scene.GrownUps -> GrownUpsScreen(game)
        }
    }
}

private const val TICK_MILLIS = 1000L
