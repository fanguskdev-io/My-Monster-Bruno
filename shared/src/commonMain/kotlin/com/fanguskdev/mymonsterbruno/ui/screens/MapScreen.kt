package com.fanguskdev.mymonsterbruno.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.rememberTextMeasurer
import com.fanguskdev.mymonsterbruno.audio.Sfx
import com.fanguskdev.mymonsterbruno.audio.Voice
import com.fanguskdev.mymonsterbruno.game.BrunoLook
import com.fanguskdev.mymonsterbruno.game.Hue
import com.fanguskdev.mymonsterbruno.game.Shape
import com.fanguskdev.mymonsterbruno.ui.GameState
import com.fanguskdev.mymonsterbruno.ui.Island
import com.fanguskdev.mymonsterbruno.ui.Stage
import com.fanguskdev.mymonsterbruno.ui.StageCanvas
import com.fanguskdev.mymonsterbruno.ui.Touch
import com.fanguskdev.mymonsterbruno.ui.andika
import com.fanguskdev.mymonsterbruno.ui.art.Pose
import com.fanguskdev.mymonsterbruno.ui.art.drawBruno
import com.fanguskdev.mymonsterbruno.ui.art.drawSea
import com.fanguskdev.mymonsterbruno.ui.art.drawShapePiece
import com.fanguskdev.mymonsterbruno.ui.art.drawTiedBalloon
import com.fanguskdev.mymonsterbruno.ui.art.paint
import com.fanguskdev.mymonsterbruno.ui.art.pieceBox
import com.fanguskdev.mymonsterbruno.ui.circle
import com.fanguskdev.mymonsterbruno.ui.ellipse
import com.fanguskdev.mymonsterbruno.ui.fill
import com.fanguskdev.mymonsterbruno.ui.label
import com.fanguskdev.mymonsterbruno.ui.line
import com.fanguskdev.mymonsterbruno.ui.path
import com.fanguskdev.mymonsterbruno.ui.rememberClock
import com.fanguskdev.mymonsterbruno.ui.stroke
import kotlinx.coroutines.launch
import kotlin.math.sin

private val VOWELS_ISLAND = Offset(250f, 250f)
private val NUMBERS_ISLAND = Offset(560f, 168f)
private const val ISLAND_RX = 120f
private const val ISLAND_RY = 56f
private val COLORS_ISLAND = Offset(668f, 314f)
private val SHAPES_ISLAND = Offset(318f, 84f)
private val FAR_ISLANDS = listOf(Offset(108f, 140f))
private val BOAT = Offset(410f, 336f)

/**
 * The map of the islands: the one of the vowels, the one of the numbers, the one of the colours and the one of the shapes,
 * and another far away in the mist that is not ready yet. A touch on an island says where they go and goes there; the flag says she has
 * finished it at least once.
 */
@Composable
internal fun MapScreen(game: GameState) {
    val clock = rememberClock()
    val scope = rememberCoroutineScope()
    val measurer = rememberTextMeasurer()
    val font = andika()
    val audio = game.audio
    var going by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { audio.say(Voice.WHICH_ISLAND) }

    val touch = remember {
        object : Touch {
            override fun down(point: Offset, stage: Stage): Boolean {
                if (going) return true
                when {
                    hits(point, homeButton(stage), CORNER_RADIUS) -> {
                        audio.sfx(Sfx.TAP)
                        game.back()
                    }
                    inIsland(point, VOWELS_ISLAND) -> go(Island.VOWELS, Voice.TO_LETTERS)
                    inIsland(point, NUMBERS_ISLAND) -> go(Island.NUMBERS, Voice.TO_NUMBERS)
                    inIsland(point, COLORS_ISLAND) -> go(Island.COLORS, Voice.TO_COLORS)
                    inIsland(point, SHAPES_ISLAND) -> go(Island.SHAPES, Voice.TO_SHAPES)
                    FAR_ISLANDS.any { hits(point, it, 50f) } -> audio.speak(Voice.TOO_FAR)
                    else -> return false
                }
                return true
            }

            private fun go(island: Island, line: String) {
                going = true
                audio.sfx(Sfx.WHOOSH)
                scope.launch {
                    audio.say(line)
                    game.goTo(island)
                }
            }
        }
    }

    StageCanvas(touch) { stage ->
        val t = clock.value
        drawSea(stage, t)
        drawPath(t)
        for ((i, far) in FAR_ISLANDS.withIndex()) drawFarIsland(far, t, i)
        drawIsland(VOWELS_ISLAND, t)
        drawVowelRocks(VOWELS_ISLAND, measurer, font)
        drawIsland(NUMBERS_ISLAND, t)
        drawNumberRocks(NUMBERS_ISLAND, measurer, font)
        drawIsland(COLORS_ISLAND, t)
        drawIslandBalloons(COLORS_ISLAND, t)
        drawIsland(SHAPES_ISLAND, t)
        drawIslandShapes(SHAPES_ISLAND)
        if ((game.data.islands[Island.VOWELS.name] ?: 0) > 0) drawFlag(VOWELS_ISLAND + Offset(92f, -34f))
        if ((game.data.islands[Island.NUMBERS.name] ?: 0) > 0) drawFlag(NUMBERS_ISLAND + Offset(92f, -34f))
        if ((game.data.islands[Island.COLORS.name] ?: 0) > 0) drawFlag(COLORS_ISLAND + Offset(92f, -34f))
        if ((game.data.islands[Island.SHAPES.name] ?: 0) > 0) drawFlag(SHAPES_ISLAND + Offset(112f, -6f))
        drawBoat(game.data.look, BOAT + Offset(0f, 4f * sin(t * 1.5f)), t)
        drawHomeButton(stage)
    }
}

private fun inIsland(point: Offset, island: Offset): Boolean {
    val dx = (point.x - island.x) / (ISLAND_RX + TOUCH_SLACK)
    val dy = (point.y - island.y) / (ISLAND_RY + 40f)
    return dx * dx + dy * dy <= 1f
}

private fun DrawScope.drawIsland(center: Offset, t: Float) {
    ellipse(Color(0x33FFFFFF), center + Offset(0f, 8f), ISLAND_RX + 14f + 3f * sin(t * 1.4f), ISLAND_RY + 10f)
    ellipse(Color(0xFFF6DFA6), center, ISLAND_RX, ISLAND_RY)
    ellipse(Color(0xFF93D46E), center + Offset(0f, -8f), ISLAND_RX - 20f, ISLAND_RY - 16f)
}

/** The rocks of the island of letters: the vowels in a row and the consonants in front. */
private fun DrawScope.drawVowelRocks(center: Offset, measurer: TextMeasurer, font: FontFamily) =
    drawRocks(center, listOf("A", "E", "I", "O", "U"), listOf("M", "P", "L", "S"), measurer, font)

/** The rocks of the island of numbers: one to five in a row and six to ten in front. */
private fun DrawScope.drawNumberRocks(center: Offset, measurer: TextMeasurer, font: FontFamily) =
    drawRocks(center, (1..5).map { it.toString() }, (6..10).map { it.toString() }, measurer, font)

/** Two rows of white rocks on an island, each with its sign in a colour. */
private fun DrawScope.drawRocks(
    center: Offset,
    back: List<String>,
    front: List<String>,
    measurer: TextMeasurer,
    font: FontFamily,
) {
    val colors = listOf(Color(0xFFE4483C), Color(0xFF2C74D8), Color(0xFF2E9E5B), Color(0xFFF4B623), Color(0xFF9B6BD3))
    for ((row, signs) in listOf(back, front).withIndex()) {
        signs.forEachIndexed { i, sign ->
            val p = center + Offset(-15.5f * (signs.size - 1) + 31f * i, -22f + 26f * row)
            circle(Color.White, p, 14f)
            label(measurer, sign, p + Offset(0f, 1f), if (sign.length > 1) 16f else 20f, colors[(i + row * 2) % colors.size], font)
        }
    }
}

/** The pieces of the island of shapes, one of each, standing on the grass. */
private fun DrawScope.drawIslandShapes(center: Offset) {
    Shape.entries.zip(listOf(Hue.RED, Hue.BLUE, Hue.YELLOW, Hue.GREEN)).forEachIndexed { i, (shape, hue) ->
        drawShapePiece(shape, pieceBox(shape, center + Offset(-60f + 40f * i, -18f + if (i % 2 == 0) 0f else 10f), 32f), hue.paint())
    }
}

/** The balloons of the island of colours, one of each, tied to the grass and swaying. */
private fun DrawScope.drawIslandBalloons(center: Offset, t: Float) {
    Hue.entries.forEachIndexed { i, hue ->
        val tie = center + Offset(-62f + 25f * i, -6f + if (i % 2 == 0) -8f else 4f)
        val at = tie + Offset(6f * sin(t * 1.3f + i), -48f - 10f * (i % 2))
        drawTiedBalloon(at, 34f, hue.paint(), tie)
    }
}

private fun DrawScope.drawFarIsland(center: Offset, t: Float, i: Int) {
    val alpha = 0.55f
    ellipse(Color(0xFFF6DFA6), center, 52f, 22f, alpha = alpha)
    ellipse(Color(0xFF93D46E), center + Offset(0f, -4f), 40f, 14f, alpha = alpha)
    for (k in 0 until 3) {
        circle(Color.White, center + Offset(-40f + 40f * k + 6f * sin(t * 0.5f + i + k), -10f), 20f, alpha = 0.55f)
    }
}

private fun DrawScope.drawPath(t: Float) {
    val dots = path {
        moveTo(VOWELS_ISLAND.x + 60f, VOWELS_ISLAND.y - 40f)
        quadraticTo(400f, 150f, NUMBERS_ISLAND.x - 90f, NUMBERS_ISLAND.y + 10f)
        moveTo(NUMBERS_ISLAND.x + 70f, NUMBERS_ISLAND.y + 48f)
        quadraticTo(720f, 230f, COLORS_ISLAND.x + 20f, COLORS_ISLAND.y - 62f)
        moveTo(VOWELS_ISLAND.x - 20f, VOWELS_ISLAND.y - 64f)
        quadraticTo(232f, 160f, SHAPES_ISLAND.x - 40f, SHAPES_ISLAND.y + 60f)
    }
    drawPath(
        dots,
        Color.White,
        alpha = 0.85f,
        style = androidx.compose.ui.graphics.drawscope.Stroke(
            6f,
            cap = androidx.compose.ui.graphics.StrokeCap.Round,
            pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(0.1f, 16f), -t * 12f),
        ),
    )
}

private fun DrawScope.drawFlag(at: Offset) {
    line(Color(0xFF8A5A3C), at, at + Offset(0f, -50f), 4f)
    fill(
        path {
            moveTo(at.x, at.y - 50f)
            lineTo(at.x + 28f, at.y - 42f)
            lineTo(at.x, at.y - 34f)
            close()
        },
        Color(0xFF2E9E5B),
    )
}

private fun DrawScope.drawBoat(look: BrunoLook, at: Offset, t: Float) {
    drawBruno(look, at + Offset(0f, -36f), 80f, Pose(lookX = 0.6f), t)
    fill(
        path {
            moveTo(at.x - 62f, at.y - 10f)
            lineTo(at.x + 62f, at.y - 10f)
            lineTo(at.x + 44f, at.y + 22f)
            lineTo(
                at.x - 44f,
                at.y + 22f,
            )
            close()
        },
        Color(0xFFB5643A),
    )
    line(Color(0xFF8A4B2E), Offset(at.x - 56f, at.y - 2f), Offset(at.x + 56f, at.y - 2f), 3f)
    stroke(
        path {
            moveTo(at.x - 80f, at.y + 24f)
            quadraticTo(at.x - 70f, at.y + 16f, at.x - 60f, at.y + 24f)
        },
        Color.White,
        3f,
        alpha = 0.5f,
    )
}
