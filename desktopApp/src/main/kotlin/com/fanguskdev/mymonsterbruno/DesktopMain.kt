package com.fanguskdev.mymonsterbruno

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.fanguskdev.mymonsterbruno.storage.DesktopKeyValueStore

/**
 * The desktop window, a development tool: the game at 1280 × 640, the shape of a phone on its side; `--silencio` turns
 * the voice and the sounds off. It is not delivered.
 */
fun main(args: Array<String>) = application {
    val store = DesktopKeyValueStore()
    Window(
        onCloseRequest = ::exitApplication,
        title = "Bruno",
        state = rememberWindowState(width = 1280.dp, height = 640.dp),
    ) {
        App(store, sound = "--silencio" !in args)
    }
}
