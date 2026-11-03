package com.fanguskdev.mymonsterbruno.ui.screens

import androidx.compose.ui.geometry.Offset
import com.fanguskdev.mymonsterbruno.ui.Iphone
import com.fanguskdev.mymonsterbruno.ui.Stage
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * The buttons of the top corners (back home, say it again and the grown-ups' padlock) on the iPhone 15 Pro she plays on
 * and on the simulator's 17 Pro, held either way: whole, neither under the Dynamic Island nor cut by the rounded corners.
 * The game fills the whole screen, past the safe area (`iosApp/iosApp/ContentView.swift`): only where they are drawn
 * keeps them clear.
 */
class CornerButtonsTest {
    @Test
    fun theCornerButtonsAreSeenWholeOnTheIphone() {
        for (iphone in listOf(Iphone.PRO_15, Iphone.PRO_17)) {
            val stage = Stage(iphone.width, iphone.height)
            val buttons = listOf(
                Triple("la casa", homeButton(stage), CORNER_RADIUS),
                Triple("el altavoz", speakerButton(stage), CORNER_RADIUS),
                Triple("el candado", lockButton(stage), LOCK_RADIUS),
            )
            for (islandOnLeft in listOf(true, false)) {
                for ((name, center, radius) in buttons) {
                    val onScreen = Offset((center.x - stage.left) * stage.scale, (center.y - stage.top) * stage.scale)
                    val room = iphone.room(onScreen, (radius + OUTLINE) * stage.scale, islandOnLeft)
                    val side = if (islandOnLeft) "izquierda" else "derecha"
                    assertTrue(room >= 0f, "En el ${iphone.name}, con la isla a la $side, se tapan ${-room} puntos de $name")
                }
            }
        }
    }

    private companion object {
        /** What is drawn round a button beyond its circle: the padlock's ring, while it is held, goes this much further. */
        const val OUTLINE = 2.5f
    }
}
