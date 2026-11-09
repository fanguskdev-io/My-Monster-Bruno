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
import com.fanguskdev.mymonsterbruno.audio.Sfx
import com.fanguskdev.mymonsterbruno.audio.Voice
import com.fanguskdev.mymonsterbruno.game.Glyph
import com.fanguskdev.mymonsterbruno.game.PathShape
import com.fanguskdev.mymonsterbruno.game.TraceProgress
import com.fanguskdev.mymonsterbruno.game.pathRounds
import com.fanguskdev.mymonsterbruno.game.plusOne
import com.fanguskdev.mymonsterbruno.game.points
import com.fanguskdev.mymonsterbruno.ui.GameState
import com.fanguskdev.mymonsterbruno.ui.Stage
import com.fanguskdev.mymonsterbruno.ui.StageCanvas
import com.fanguskdev.mymonsterbruno.ui.Touch
import com.fanguskdev.mymonsterbruno.ui.art.Arms
import com.fanguskdev.mymonsterbruno.ui.art.Eyes
import com.fanguskdev.mymonsterbruno.ui.art.Mouth
import com.fanguskdev.mymonsterbruno.ui.art.Pose
import com.fanguskdev.mymonsterbruno.ui.art.drawBruno
import com.fanguskdev.mymonsterbruno.ui.art.drawField
import com.fanguskdev.mymonsterbruno.ui.art.drawHomeIcon
import com.fanguskdev.mymonsterbruno.ui.art.drawSandPath
import com.fanguskdev.mymonsterbruno.ui.art.drawSparkles
import com.fanguskdev.mymonsterbruno.ui.circle
import com.fanguskdev.mymonsterbruno.ui.clamp01
import com.fanguskdev.mymonsterbruno.ui.rememberClock
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.sin

/** Bruno is small here, to walk along the paths, and his house at their end; the snail's is smaller, in its middle. */
private const val BRUNO_HEIGHT = 66f
private const val HOUSE_SIZE = 76f
private const val SNAIL_HOUSE_SIZE = 54f

/** How near the path her finger has to be, and how near Bruno to take him: a little more than for the letters. */
private const val TOLERANCE = 40f
private const val START_TOLERANCE = 50f

private val GOLD = Color(0xFFF4B623)

private enum class PathPhase { WALKING, HOME, THANKS }

/**
 * «Trazos antes de las letras», on the island of letters by turns with tracing them (ADR 0031): a sandy path across a
 * field, from where Bruno is to his house, and she takes him home with her finger along it: across, down, slanting, in
 * waves, in loops or round a snail, in the order of the strokes of her class. Bruno walks under her finger as long as it
 * stays on the path; lifting it, or leaving the path, keeps him where he is, and she goes on from there: nothing is
 * wrong, it just does not advance. Three paths a visit, those she has done least.
 */
@Composable
internal fun PathsScreen(game: GameState, onDone: () -> Unit) {
    val clock = rememberClock()
    val scope = rememberCoroutineScope()
    val audio = game.audio
    val rounds = remember { pathRounds(game.data.paths) }
    var round by remember { mutableStateOf(0) }
    var phase by remember { mutableStateOf(PathPhase.WALKING) }
    var phaseAt by remember { mutableStateOf(0f) }

    // The path of this round and how far along it Bruno is; whether her finger is taking him; and when the voice last
    // told her to put her finger on Bruno, so that it does not say it at every touch.
    var points by remember { mutableStateOf(rounds[0].points()) }
    var progress by remember { mutableStateOf(TraceProgress(Glyph(' ', 0f, listOf(points)))) }
    var reached by remember { mutableStateOf(0) }
    var walking by remember { mutableStateOf(false) }
    var toldAt by remember { mutableStateOf(-HINT_SECONDS) }

    fun current(): PathShape = rounds[round.coerceAtMost(rounds.lastIndex)]

    LaunchedEffect(round) {
        points = current().points()
        progress = TraceProgress(Glyph(' ', 0f, listOf(points)))
        reached = 0
        walking = false
        phase = PathPhase.WALKING
        phaseAt = clock.value
        if (round == 0) audio.say(Voice.PATHS_INTRO)
        audio.say(Voice.pathIs(current()))
    }

    fun home() {
        val shape = current()
        walking = false
        reached = points.lastIndex
        phase = PathPhase.HOME
        phaseAt = clock.value
        audio.sfx(Sfx.SPARKLE)
        game.update { it.copy(paths = it.paths.plusOne(shape.word)) }
        scope.launch {
            audio.say(Voice.AT_HOME)
            delay(PAUSE_MILLIS)
            if (round + 1 < rounds.size) {
                round++
            } else {
                phase = PathPhase.THANKS
                phaseAt = clock.value
                audio.say(Voice.THANKS_PATHS)
                onDone()
            }
        }
    }

    val touch = remember {
        object : Touch {
            override fun down(point: Offset, stage: Stage): Boolean {
                if (hits(point, speakerButton(stage), CORNER_RADIUS)) {
                    if (phase == PathPhase.WALKING) audio.speak(Voice.pathIs(current()))
                    return true
                }
                if (hits(point, homeButton(stage), CORNER_RADIUS)) {
                    audio.hush()
                    game.back()
                    return true
                }
                if (phase != PathPhase.WALKING) return false
                if (progress.canStart(point, START_TOLERANCE)) {
                    walking = true
                } else if (clock.value - toldAt > HINT_SECONDS) {
                    toldAt = clock.value
                    audio.speak(Voice.FOLLOW_PATH)
                }
                return true
            }

            override fun move(point: Offset) {
                if (!walking) return
                if (progress.moveTo(point, TOLERANCE)) home() else reached = progress.reached
            }

            override fun up(point: Offset) {
                walking = false
            }

            override fun cancel() {
                walking = false
            }
        }
    }

    StageCanvas(touch) { stage ->
        val t = clock.value
        val since = t - phaseAt
        val shape = current()
        drawField(stage, t)
        drawSandPath(points, reached, t)
        val house = points.last()
        drawHomeIcon(house, if (shape == PathShape.SPIRAL) SNAIL_HOUSE_SIZE else HOUSE_SIZE)

        // Bruno walks on the path, a little above it so that his feet are on it; at home he goes in by the door.
        val at = points[reached.coerceIn(0, points.lastIndex)]
        val inside = if (phase == PathPhase.WALKING) 0f else clamp01(since / GO_IN_SECONDS)
        if (inside < 1f) {
            val pose = when {
                phase != PathPhase.WALKING -> Pose(Mouth.GRIN, Eyes.HAPPY, Arms.UP)
                walking -> Pose(Mouth.SMILE, Eyes.OPEN, Arms.UP, hop = 3f * sin(t * 14f).coerceAtLeast(0f))
                else -> Pose(Mouth.OPEN, Eyes.OPEN, Arms.DOWN, hop = 1.5f * sin(t * 2f))
            }
            drawBruno(game.data.look, at + Offset(0f, -BRUNO_HEIGHT * 0.3f), BRUNO_HEIGHT * (1f - 0.6f * inside), pose, t)
        }
        if (phase == PathPhase.WALKING && !walking) drawCallRing(at, START_TOLERANCE, t, GOLD)
        if (phase != PathPhase.WALKING) drawSparkles(house, 70f, t)

        for (i in rounds.indices) {
            val done = i < round || (i == round && phase != PathPhase.WALKING)
            circle(if (done) Color(0xFFE9B36A) else Color(0x99FFFFFF), Offset(370f + 30f * i, 26f), 10f)
        }
        drawHomeButton(stage)
        drawSpeakerButton(stage)
    }
}

private const val HINT_SECONDS = 6f
private const val GO_IN_SECONDS = 0.6f
private const val PAUSE_MILLIS = 900L
