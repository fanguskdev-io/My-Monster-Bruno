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
import com.fanguskdev.mymonsterbruno.game.CONSONANTS
import com.fanguskdev.mymonsterbruno.game.Feeling
import com.fanguskdev.mymonsterbruno.game.Hue
import com.fanguskdev.mymonsterbruno.game.LAST_CONSONANTS
import com.fanguskdev.mymonsterbruno.game.NEXT_CONSONANTS
import com.fanguskdev.mymonsterbruno.game.Shape
import com.fanguskdev.mymonsterbruno.game.VOWELS
import com.fanguskdev.mymonsterbruno.ui.GameState
import com.fanguskdev.mymonsterbruno.ui.Island
import com.fanguskdev.mymonsterbruno.ui.Stage
import com.fanguskdev.mymonsterbruno.ui.StageCanvas
import com.fanguskdev.mymonsterbruno.ui.Touch
import com.fanguskdev.mymonsterbruno.ui.andika
import com.fanguskdev.mymonsterbruno.ui.art.Pose
import com.fanguskdev.mymonsterbruno.ui.art.drawBruno
import com.fanguskdev.mymonsterbruno.ui.art.drawFeelingFace
import com.fanguskdev.mymonsterbruno.ui.art.drawOpenBook
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

private val VOWELS_ISLAND = Offset(262f, 258f)
private val NUMBERS_ISLAND = Offset(588f, 150f)
private const val ISLAND_RX = 110f
private const val ISLAND_RY = 52f
private val COLORS_ISLAND = Offset(674f, 318f)
private val SHAPES_ISLAND = Offset(340f, 78f)
private val FEELINGS_ISLAND = Offset(112f, 150f)
private val WORDS_ISLAND = Offset(712f, 56f)
private const val WORDS_RX = 80f
private const val WORDS_RY = 38f
private val BOAT = Offset(410f, 336f)

/**
 * The map of the islands: the one of the letters, the one of the numbers, the one of the colours, the one of the shapes,
 * the one of the feelings and, the smallest, far up on the right, the one of the words. A touch on an island says where
 * they go and goes there; the flag says she has finished it at least once.
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
                    inIsland(point, FEELINGS_ISLAND) -> go(Island.FEELINGS, Voice.TO_FEELINGS)
                    inIsland(point, WORDS_ISLAND, WORDS_RX, WORDS_RY) -> go(Island.WORDS, Voice.TO_WORDS)
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
        drawIsland(VOWELS_ISLAND, t)
        drawVowelRocks(VOWELS_ISLAND, measurer, font)
        drawIsland(NUMBERS_ISLAND, t)
        drawNumberRocks(NUMBERS_ISLAND, measurer, font)
        drawIsland(COLORS_ISLAND, t)
        drawIslandBalloons(COLORS_ISLAND, t)
        drawIsland(SHAPES_ISLAND, t)
        drawIslandShapes(SHAPES_ISLAND)
        drawIsland(FEELINGS_ISLAND, t)
        drawIslandFaces(FEELINGS_ISLAND)
        drawIsland(WORDS_ISLAND, t, WORDS_RX, WORDS_RY)
        drawOpenBook(WORDS_ISLAND + Offset(0f, -14f), 58f)
        if ((game.data.islands[Island.VOWELS.name] ?: 0) > 0) drawFlag(VOWELS_ISLAND + Offset(92f, -34f))
        if ((game.data.islands[Island.NUMBERS.name] ?: 0) > 0) drawFlag(NUMBERS_ISLAND + Offset(92f, -34f))
        // Lower than the others: higher, on an iPhone held with its island on the right, the Dynamic Island hides it.
        if ((game.data.islands[Island.COLORS.name] ?: 0) > 0) drawFlag(COLORS_ISLAND + Offset(84f, 24f))
        if ((game.data.islands[Island.SHAPES.name] ?: 0) > 0) drawFlag(SHAPES_ISLAND + Offset(102f, -4f))
        if ((game.data.islands[Island.FEELINGS.name] ?: 0) > 0) drawFlag(FEELINGS_ISLAND + Offset(84f, -30f))
        // Low, beside the book: the island is so high that a flag at its back would go out of the screen.
        if ((game.data.islands[Island.WORDS.name] ?: 0) > 0) drawFlag(WORDS_ISLAND + Offset(50f, 14f))
        drawBoat(game.data.look, BOAT + Offset(0f, 4f * sin(t * 1.5f)), t)
        drawHomeButton(stage)
    }
}

private fun inIsland(
    point: Offset,
    island: Offset,
    rx: Float = ISLAND_RX,
    ry: Float = ISLAND_RY,
): Boolean {
    val dx = (point.x - island.x) / (rx + TOUCH_SLACK)
    val dy = (point.y - island.y) / (ry + 40f)
    return dx * dx + dy * dy <= 1f
}

private fun DrawScope.drawIsland(
    center: Offset,
    t: Float,
    rx: Float = ISLAND_RX,
    ry: Float = ISLAND_RY,
) {
    ellipse(Color(0x33FFFFFF), center + Offset(0f, 8f), rx + 14f + 3f * sin(t * 1.4f), ry + 10f)
    ellipse(Color(0xFFF6DFA6), center, rx, ry)
    ellipse(Color(0xFF93D46E), center + Offset(0f, -8f), rx - 20f, ry - 16f)
}

/** The rocks of the island of letters: the vowels in a row, and the consonants in front, as they come in. */
private fun DrawScope.drawVowelRocks(center: Offset, measurer: TextMeasurer, font: FontFamily) {
    val steps = listOf(VOWELS, CONSONANTS, NEXT_CONSONANTS, LAST_CONSONANTS)
    drawRocks(center, steps.map { row -> row.map { it.uppercase() } }, measurer, font)
}

/** The rocks of the island of numbers: one to five in a row and six to ten in front. */
private fun DrawScope.drawNumberRocks(center: Offset, measurer: TextMeasurer, font: FontFamily) =
    drawRocks(center, listOf((1..5).map { it.toString() }, (6..10).map { it.toString() }), measurer, font)

/** Rows of white rocks on an island, each with its sign in a colour, the first row at the back; four rows go closer. */
private fun DrawScope.drawRocks(
    center: Offset,
    rows: List<List<String>>,
    measurer: TextMeasurer,
    font: FontFamily,
) {
    val colors = listOf(Color(0xFFE4483C), Color(0xFF2C74D8), Color(0xFF2E9E5B), Color(0xFFF4B623), Color(0xFF9B6BD3))
    val packed = rows.size > 3
    val step = if (packed) 22f else 26f
    val across = if (packed) 28f else 31f
    val radius = if (packed) 12f else 14f
    val top = if (packed) -37f else -22f - 8f * (rows.size - 2)
    for ((row, signs) in rows.withIndex()) {
        signs.forEachIndexed { i, sign ->
            val p = center + Offset(-across / 2 * (signs.size - 1) + across * i, top + step * row)
            circle(Color.White, p, radius)
            val size = (if (sign.length > 1) 16f else 20f) * radius / 14f
            label(measurer, sign, p + Offset(0f, 1f), size, colors[(i + row * 2) % colors.size], font)
        }
    }
}

/** The faces of the island of feelings, one of each, in a row on the grass. */
private fun DrawScope.drawIslandFaces(center: Offset) {
    Feeling.entries.forEachIndexed { i, feeling ->
        drawFeelingFace(feeling, center + Offset(-64f + 32f * i, -12f + if (i % 2 == 0) 0f else 8f), 14f)
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

private fun DrawScope.drawPath(t: Float) {
    val dots = path {
        moveTo(VOWELS_ISLAND.x + 60f, VOWELS_ISLAND.y - 40f)
        quadraticTo(400f, 150f, NUMBERS_ISLAND.x - 90f, NUMBERS_ISLAND.y + 10f)
        moveTo(NUMBERS_ISLAND.x + 70f, NUMBERS_ISLAND.y + 48f)
        quadraticTo(720f, 230f, COLORS_ISLAND.x + 20f, COLORS_ISLAND.y - 62f)
        moveTo(VOWELS_ISLAND.x + 10f, VOWELS_ISLAND.y - 60f)
        quadraticTo(290f, 170f, SHAPES_ISLAND.x - 30f, SHAPES_ISLAND.y + 56f)
        moveTo(VOWELS_ISLAND.x - 90f, VOWELS_ISLAND.y - 36f)
        quadraticTo(150f, 228f, FEELINGS_ISLAND.x + 30f, FEELINGS_ISLAND.y + 56f)
        moveTo(SHAPES_ISLAND.x + 116f, SHAPES_ISLAND.y - 10f)
        quadraticTo(520f, 26f, WORDS_ISLAND.x - WORDS_RX - 8f, WORDS_ISLAND.y)
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
