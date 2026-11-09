package com.fanguskdev.mymonsterbruno.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.rememberTextMeasurer
import com.fanguskdev.mymonsterbruno.audio.Sfx
import com.fanguskdev.mymonsterbruno.audio.Voice
import com.fanguskdev.mymonsterbruno.ui.GameState
import com.fanguskdev.mymonsterbruno.ui.Stage
import com.fanguskdev.mymonsterbruno.ui.StageCanvas
import com.fanguskdev.mymonsterbruno.ui.Touch
import com.fanguskdev.mymonsterbruno.ui.andika
import com.fanguskdev.mymonsterbruno.ui.art.drawBoatWith
import com.fanguskdev.mymonsterbruno.ui.art.drawHorizonIslands
import com.fanguskdev.mymonsterbruno.ui.art.drawRoundButton
import com.fanguskdev.mymonsterbruno.ui.art.drawSplashSea
import com.fanguskdev.mymonsterbruno.ui.art.drawSplashSky
import com.fanguskdev.mymonsterbruno.ui.art.playIcon
import com.fanguskdev.mymonsterbruno.ui.clamp01
import com.fanguskdev.mymonsterbruno.ui.easeBack
import com.fanguskdev.mymonsterbruno.ui.label
import com.fanguskdev.mymonsterbruno.ui.rememberClock
import com.fanguskdev.mymonsterbruno.ui.rememberPhoto
import kotlinx.coroutines.delay
import kotlin.math.sin

private val TITLE = Offset(560f, 70f)
private const val TITLE_HEIGHT = 34f
private val NAME = Offset(560f, 150f)
private const val NAME_HEIGHT = 92f
private const val LETTER_STEP = 66f
private val NAME_COLORS = listOf(Color(0xFFE4483C), Color(0xFF2C74D8), Color(0xFF2E9E5B), Color(0xFFF4B623), Color(0xFF9B6BD3))
private val BOAT = Offset(286f, 332f)
private val PLAY = Offset(662f, 318f)
private const val PLAY_RADIUS = 56f

/**
 * The start screen (ADR 0018), as CaroFarty's title screen: the name of the game over Bruno's world seen from the side,
 * the sky with the sun and its clouds drifting, the sea with the islands of the game going by on the horizon, and Bruno
 * rocking in his boat (his egg, before he hatches). The letters of BRUNO fall into place one after the other, the voice
 * says «¡Mi amigo Bruno!» and «¡Toca para empezar!», and a touch anywhere dips to white and opens the game where it
 * was: the egg, Bruno's room or his bed.
 */
@Composable
internal fun SplashScreen(game: GameState) {
    val clock = rememberClock()
    val measurer = rememberTextMeasurer()
    val font = andika()
    val audio = game.audio
    var leavingAt by remember { mutableStateOf<Float?>(null) }

    LaunchedEffect(Unit) {
        // When the letters have landed: the voice has had time to load, too.
        delay(LANDED_MILLIS)
        audio.sfx(Sfx.START)
        audio.say(Voice.TITLE, Voice.TAP_TO_START)
    }
    LaunchedEffect(leavingAt) {
        if (leavingAt == null) return@LaunchedEffect
        delay((DIP_SECONDS * MILLIS).toLong())
        game.leaveSplash()
    }

    val touch = remember {
        object : Touch {
            override fun down(point: Offset, stage: Stage): Boolean {
                if (leavingAt == null) {
                    leavingAt = clock.value
                    audio.hush()
                    audio.sfx(Sfx.TAP)
                }
                return true
            }
        }
    }

    val photo = rememberPhoto(game)

    StageCanvas(touch) { stage ->
        val t = clock.value
        drawSplashSky(stage, t)
        drawSplashSea(stage, t)
        drawHorizonIslands(stage, t)
        drawBoatWith(if (game.data.hatched) game.data.look else null, BOAT + Offset(0f, 4f * sin(t * 1.5f)), t, photo)
        drawTitle(t, measurer, font)
        drawRoundButton(PLAY, PLAY_RADIUS, Color(0xFF2E9E5B), glow = (t % 1.8f) / 1.8f) { playIcon(PLAY, 58f) }
        leavingAt?.let { since ->
            drawRect(
                Color.White,
                Offset(stage.left, stage.top),
                Size(stage.right - stage.left, stage.bottom - stage.top),
                alpha = clamp01((t - since) / DIP_SECONDS),
            )
        }
    }
}

/** «Mi amigo» and, under it, BRUNO, a letter of each colour falling into place one after the other, and then bobbing. */
private fun DrawScope.drawTitle(time: Float, measurer: TextMeasurer, font: FontFamily) {
    outlined(measurer, "Mi amigo", TITLE, TITLE_HEIGHT, Color.White, font)
    val first = NAME.x - LETTER_STEP * (NAME_LETTERS.length - 1) / 2
    NAME_LETTERS.forEachIndexed { i, letter ->
        val landing = clamp01((time - 0.25f - 0.14f * i) / 0.5f)
        if (landing <= 0f) return@forEachIndexed
        val drop = (1f - easeBack(landing)) * -90f
        val bob = if (landing >= 1f) 4f * sin(time * 2.2f + i * 0.8f) else 0f
        val at = Offset(first + LETTER_STEP * i, NAME.y + drop + bob)
        outlined(measurer, letter.toString(), at, NAME_HEIGHT, NAME_COLORS[i % NAME_COLORS.size], font)
    }
}

/** [text] with a dark edge around it, so it reads over the sky. */
private fun DrawScope.outlined(
    measurer: TextMeasurer,
    text: String,
    center: Offset,
    height: Float,
    color: Color,
    font: FontFamily,
) {
    val edge = height * 0.05f
    for ((dx, dy) in listOf(-1f to 0f, 1f to 0f, 0f to -1f, 0f to 1f, 0f to 1.6f)) {
        label(measurer, text, center + Offset(dx * edge, dy * edge), height, Color(0xFF1D2B53), font)
    }
    label(measurer, text, center, height, color, font)
}

private const val NAME_LETTERS = "BRUNO"
private const val LANDED_MILLIS = 1300L
private const val DIP_SECONDS = 0.35f
private const val MILLIS = 1000f
