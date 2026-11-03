package com.fanguskdev.mymonsterbruno.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import com.fanguskdev.mymonsterbruno.audio.Sfx
import com.fanguskdev.mymonsterbruno.audio.Voice
import com.fanguskdev.mymonsterbruno.game.HOUSE_PIECES
import com.fanguskdev.mymonsterbruno.game.HousePart
import com.fanguskdev.mymonsterbruno.game.Hue
import com.fanguskdev.mymonsterbruno.game.houseRounds
import com.fanguskdev.mymonsterbruno.game.plusOne
import com.fanguskdev.mymonsterbruno.ui.GameState
import com.fanguskdev.mymonsterbruno.ui.Stage
import com.fanguskdev.mymonsterbruno.ui.StageCanvas
import com.fanguskdev.mymonsterbruno.ui.Touch
import com.fanguskdev.mymonsterbruno.ui.art.Arms
import com.fanguskdev.mymonsterbruno.ui.art.Eyes
import com.fanguskdev.mymonsterbruno.ui.art.Mouth
import com.fanguskdev.mymonsterbruno.ui.art.Pose
import com.fanguskdev.mymonsterbruno.ui.art.drawBruno
import com.fanguskdev.mymonsterbruno.ui.art.drawMeadow
import com.fanguskdev.mymonsterbruno.ui.art.drawShapePiece
import com.fanguskdev.mymonsterbruno.ui.art.drawSparkles
import com.fanguskdev.mymonsterbruno.ui.art.paint
import com.fanguskdev.mymonsterbruno.ui.art.pieceBox
import com.fanguskdev.mymonsterbruno.ui.art.shapePath
import com.fanguskdev.mymonsterbruno.ui.circle
import com.fanguskdev.mymonsterbruno.ui.distance
import com.fanguskdev.mymonsterbruno.ui.easeInOut
import com.fanguskdev.mymonsterbruno.ui.fill
import com.fanguskdev.mymonsterbruno.ui.lerp
import com.fanguskdev.mymonsterbruno.ui.rememberClock
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.sin

private val BRUNO_CENTER = Offset(500f, 240f)
private const val BRUNO_HEIGHT = 190f
private val TRAY = listOf(Offset(568f, 344f), Offset(658f, 344f), Offset(748f, 344f))
private const val PIECE_SIZE = 76f
private const val PIECE_TOUCH = 44f

/** Where each part of the house goes, on the stage: the walls in the middle, the roof on them, the door at the bottom. */
private fun HousePart.box(): Rect = when (this) {
    HousePart.WALLS -> Rect(212f, 124f, 388f, 300f)
    HousePart.ROOF -> Rect(196f, 40f, 404f, 124f)
    HousePart.DOOR -> Rect(276f, 222f, 324f, 300f)
    HousePart.ROUND_WINDOW -> Rect(228f, 150f, 272f, 194f)
    HousePart.WINDOW -> Rect(330f, 150f, 374f, 194f)
    HousePart.CHIMNEY -> Rect(344f, 44f, 370f, 112f)
}

/** The order the parts are drawn in: the chimney behind the roof, the windows and the door on the walls. */
private val DRAWING_ORDER = listOf(
    HousePart.CHIMNEY,
    HousePart.WALLS,
    HousePart.ROOF,
    HousePart.DOOR,
    HousePart.ROUND_WINDOW,
    HousePart.WINDOW,
)

/** A box that flies from [from] to [to], growing or shrinking on the way, between [start] and [start] + [length]. */
private class Move(
    val from: Rect,
    val to: Rect,
    val start: Float,
    val length: Float,
) {
    fun at(time: Float): Rect {
        val k = easeInOut((time - start) / length)
        return Rect(
            lerp(from.left, to.left, k),
            lerp(from.top, to.top, k),
            lerp(from.right, to.right, k),
            lerp(from.bottom, to.bottom, k),
        )
    }

    fun over(time: Float) = time >= start + length
}

private enum class HousePhase { ASKING, BUILDING, WRONG, THANKS }

/**
 * Bruno's house, on the island of shapes (ADR 0008): he asks for each part with its shape, «¡Ahora, el tejado! Dame el
 * triángulo», and its gap glows in the house. She drags one of three pieces to it, or just touches it; the right one
 * grows into its place and Bruno names it. If it is another shape, Bruno says which one it is and asks again. Six parts:
 * the walls, the roof, the door, a round window, another window and the chimney.
 */
@Composable
internal fun HouseScreen(game: GameState, onDone: () -> Unit) {
    val clock = rememberClock()
    val scope = rememberCoroutineScope()
    val audio = game.audio
    val rounds = remember { houseRounds(game.random) }
    var round by remember { mutableStateOf(0) }
    var phase by remember { mutableStateOf(HousePhase.ASKING) }
    var phaseAt by remember { mutableStateOf(0f) }
    val built = remember { mutableStateListOf<HousePart>() }
    val builtColors = remember { mutableStateListOf<Hue>() }
    val colors = remember { mutableStateListOf<Hue>() }
    val positions = remember { mutableStateListOf(*TRAY.toTypedArray()) }
    val moves = remember { mutableStateListOf<Move?>(null, null, null) }
    var placed by remember { mutableStateOf<Int?>(null) }
    var dragging by remember { mutableStateOf<Int?>(null) }
    var grab by remember { mutableStateOf(Offset.Zero) }
    var downAt by remember { mutableStateOf(Offset.Zero) }

    fun current() = rounds[round.coerceAtMost(rounds.lastIndex)]

    fun loose(i: Int, at: Offset = positions[i]) = pieceBox(current().pieces[i], at, PIECE_SIZE)

    LaunchedEffect(round) {
        colors.clear()
        colors.addAll(Hue.entries.shuffled(game.random).take(HOUSE_PIECES))
        for (i in 0 until HOUSE_PIECES) {
            positions[i] = TRAY[i]
            moves[i] = null
        }
        placed = null
        phase = HousePhase.ASKING
        phaseAt = clock.value
        if (round == 0) audio.say(Voice.BUILD_HOUSE)
        audio.say(Voice.askPart(current().part))
    }

    fun give(i: Int) {
        val now = clock.value
        val shape = current().pieces[i]
        val part = current().part
        dragging = null
        audio.sfx(Sfx.WHOOSH)
        if (shape == part.shape) {
            phase = HousePhase.BUILDING
            phaseAt = now
            placed = i
            moves[i] = Move(loose(i), part.box(), now, BUILD_SECONDS)
            scope.launch {
                delay(BUILD_MILLIS)
                built += part
                builtColors += colors[i]
                moves[i] = null
                audio.sfx(Sfx.RIGHT)
                game.update { it.copy(shapes = it.shapes.plusOne(shape.word)) }
                audio.say(Voice.yes(shape))
                if (round + 1 < rounds.size) {
                    round++
                } else {
                    phase = HousePhase.THANKS
                    phaseAt = clock.value
                    game.update { it.copy(houses = it.houses + 1) }
                    audio.sfx(Sfx.SPARKLE)
                    audio.say(Voice.THANKS_HOUSE)
                    onDone()
                }
            }
        } else {
            phase = HousePhase.WRONG
            phaseAt = now
            val near = lerp(positions[i], part.box().center, 0.6f)
            moves[i] = Move(loose(i), loose(i, near), now, BUILD_SECONDS * 0.6f)
            scope.launch {
                delay(BUILD_MILLIS * 6 / 10)
                audio.sfx(Sfx.BOING)
                moves[i] = Move(loose(i, near), loose(i, TRAY[i]), clock.value, BUILD_SECONDS)
                positions[i] = TRAY[i]
                audio.say(Voice.thatShape(shape), Voice.iWantShape(part.shape))
                moves[i] = null
                phase = HousePhase.ASKING
                phaseAt = clock.value
            }
        }
    }

    val touch = remember {
        object : Touch {
            override fun down(point: Offset, stage: Stage): Boolean {
                if (hits(point, speakerButton(stage), CORNER_RADIUS)) {
                    if (phase == HousePhase.ASKING) audio.speak(Voice.askPart(current().part))
                    return true
                }
                if (hits(point, homeButton(stage), CORNER_RADIUS)) {
                    audio.hush()
                    game.back()
                    return true
                }
                if (phase != HousePhase.ASKING) return false
                for (i in 0 until HOUSE_PIECES) {
                    if (hits(point, positions[i], PIECE_TOUCH)) {
                        dragging = i
                        grab = positions[i] - point
                        downAt = point
                        audio.sfx(Sfx.TAP)
                        return true
                    }
                }
                return false
            }

            override fun move(point: Offset) {
                val i = dragging ?: return
                positions[i] = point + grab
            }

            override fun up(point: Offset) {
                val i = dragging ?: return
                val gap = current().part.box()
                when {
                    gap.inflate(REACH).contains(positions[i]) -> give(i)
                    distance(point, downAt) < TAP_MOVE -> give(i)
                    else -> {
                        moves[i] = Move(loose(i), loose(i, TRAY[i]), clock.value, BUILD_SECONDS * 0.6f)
                        positions[i] = TRAY[i]
                        dragging = null
                    }
                }
            }

            override fun cancel() {
                val i = dragging ?: return
                positions[i] = TRAY[i]
                dragging = null
            }
        }
    }

    StageCanvas(touch) { stage ->
        val t = clock.value
        drawMeadow(stage, t)
        val since = t - phaseAt
        for (part in DRAWING_ORDER) {
            val n = built.indexOf(part)
            if (n >= 0) drawShapePiece(part.shape, part.box(), builtColors[n].paint())
        }
        if (phase == HousePhase.ASKING || phase == HousePhase.WRONG) {
            // The gap of the part he wants, glowing.
            val gap = shapePath(current().part.shape, current().part.box())
            fill(gap, Color.White, alpha = 0.35f + 0.2f * sin(t * 4f))
            drawPath(
                gap,
                Color(0xFF5B5F97),
                style = Stroke(4f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 9f), t * 20f)),
            )
        }
        val pose = when (phase) {
            HousePhase.ASKING -> Pose(Mouth.OPEN, Eyes.OPEN, Arms.DOWN, lookX = -1f, lookY = -0.3f, hop = 1.5f * sin(t * 3f))
            HousePhase.BUILDING -> Pose(Mouth.GRIN, Eyes.HAPPY, Arms.UP, hop = 8f * sin(since * 6f).coerceAtLeast(0f))
            HousePhase.WRONG -> Pose(Mouth.WAVY, Eyes.WIDE, Arms.DOWN, lookX = 0.8f, lookY = 0.6f)
            HousePhase.THANKS -> Pose(Mouth.GRIN, Eyes.HAPPY, Arms.UP, hop = 10f * sin(since * 5f).coerceAtLeast(0f))
        }
        drawBruno(game.data.look, BRUNO_CENTER, BRUNO_HEIGHT, pose, t)
        if (phase == HousePhase.THANKS) drawSparkles(Offset(300f, 170f), 170f, t)
        if (phase != HousePhase.THANKS) {
            for (i in 0 until colors.size.coerceAtMost(HOUSE_PIECES)) {
                if (i == dragging || (i == placed && moves[i] == null)) continue
                val move = moves[i]
                val box = if (move != null &&
                    !move.over(t)
                ) {
                    move.at(t)
                } else {
                    loose(i, positions[i] + Offset(0f, bob(t, 1.5f + 0.3f * i, 3f)))
                }
                drawShapePiece(current().pieces[i], box, colors[i].paint())
            }
            dragging?.let {
                drawShapePiece(current().pieces[it], pieceBox(current().pieces[it], positions[it], PIECE_SIZE * 1.08f), colors[it].paint())
            }
        }
        for (i in rounds.indices) {
            val done = i < round || phase == HousePhase.THANKS
            circle(if (done) Color(0xFFE9B36A) else Color(0x66FFFFFF), Offset(320f + 30f * i, 382f), 10f)
        }
        drawHomeButton(stage)
        drawSpeakerButton(stage)
    }
}

private const val BUILD_SECONDS = 0.5f
private const val BUILD_MILLIS = 500L
private const val REACH = 50f
private const val TAP_MOVE = 14f
