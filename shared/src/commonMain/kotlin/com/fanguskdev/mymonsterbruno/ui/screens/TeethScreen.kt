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
import com.fanguskdev.mymonsterbruno.audio.Sfx
import com.fanguskdev.mymonsterbruno.audio.Voice
import com.fanguskdev.mymonsterbruno.game.TEETH
import com.fanguskdev.mymonsterbruno.ui.GameState
import com.fanguskdev.mymonsterbruno.ui.Stage
import com.fanguskdev.mymonsterbruno.ui.StageCanvas
import com.fanguskdev.mymonsterbruno.ui.Touch
import com.fanguskdev.mymonsterbruno.ui.art.FOAM_SECONDS
import com.fanguskdev.mymonsterbruno.ui.art.MOUTH
import com.fanguskdev.mymonsterbruno.ui.art.TOOTH_BOXES
import com.fanguskdev.mymonsterbruno.ui.art.drawFoam
import com.fanguskdev.mymonsterbruno.ui.art.drawOpenMouth
import com.fanguskdev.mymonsterbruno.ui.art.drawSparkles
import com.fanguskdev.mymonsterbruno.ui.art.drawToothbrush
import com.fanguskdev.mymonsterbruno.ui.distance
import com.fanguskdev.mymonsterbruno.ui.rememberClock
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/** Where the toothbrush waits, its head: low on the right, its handle towards the edge. */
private val BRUSH_HOME = Offset(596f, 356f)

/** How far from its head the brush can be taken, and how near a tooth the head brushes it. */
private const val BRUSH_TOUCH = 70f
private const val BRUSH_REACH = 12f

/** How much brushing a stain takes to go: the head moving this far over its tooth. */
private const val SCRUB_DISTANCE = 200f

/**
 * Bruno's teeth (ADR 0044), always first on the island of the house: his face close, his mouth wide open and his eight
 * teeth stained. «¡He comido muchas fresas! ¿Me ayudas a lavarme los dientes?». She takes the toothbrush and moves it over
 * them: the stains go with foam, a little at a time, and each tooth shines when it is clean. With all of them clean,
 * «¡Qué dientes tan limpios! ¡Gracias!».
 */
@Composable
internal fun TeethScreen(game: GameState, onDone: () -> Unit) {
    val clock = rememberClock()
    val scope = rememberCoroutineScope()
    val audio = game.audio
    val dirt = remember { mutableStateListOf<Float>().apply { repeat(TEETH) { add(1f) } } }
    var brush by remember { mutableStateOf(BRUSH_HOME) }
    var holding by remember { mutableStateOf(false) }
    var grab by remember { mutableStateOf(Offset.Zero) }
    var cleanAt by remember { mutableStateOf<Float?>(null) }
    val foam = remember { mutableStateListOf<Pair<Offset, Float>>() }

    LaunchedEffect(Unit) { audio.say(Voice.TEETH_INTRO, Voice.BRUSH) }

    // The voice says how again when she waits a while.
    LaunchedEffect(cleanAt == null) {
        while (cleanAt == null) {
            delay(IDLE_MILLIS)
            if (!holding) audio.say(Voice.BRUSH)
        }
    }

    fun scrub(from: Offset, to: Offset) {
        val moved = distance(from, to)
        val now = clock.value
        foam.removeAll { now - it.second > FOAM_SECONDS }
        for (i in 0 until TEETH) {
            if (dirt[i] <= 0f || !TOOTH_BOXES[i].inflate(BRUSH_REACH).contains(to)) continue
            dirt[i] = (dirt[i] - moved / SCRUB_DISTANCE).coerceAtLeast(0f)
            if (dirt[i] == 0f) audio.sfx(Sfx.POP)
            if (foam.lastOrNull()?.let { now - it.second > FOAM_EVERY } != false) foam.add(to to now)
        }
        if (cleanAt == null && dirt.all { it <= 0f }) {
            cleanAt = now
            holding = false
            audio.sfx(Sfx.SPARKLE)
            game.update { it.copy(brushed = it.brushed + 1) }
            scope.launch {
                audio.say(Voice.CLEAN_TEETH)
                delay(PAUSE_MILLIS)
                onDone()
            }
        }
    }

    val touch = remember {
        object : Touch {
            override fun down(point: Offset, stage: Stage): Boolean {
                if (hits(point, speakerButton(stage), CORNER_RADIUS)) {
                    if (cleanAt == null) audio.speak(Voice.BRUSH)
                    return true
                }
                if (hits(point, homeButton(stage), CORNER_RADIUS)) {
                    audio.hush()
                    game.back()
                    return true
                }
                if (cleanAt != null || !hits(point, brush + Offset(60f, 6f), BRUSH_TOUCH)) return false
                holding = true
                grab = brush - point
                audio.sfx(Sfx.TAP)
                return true
            }

            override fun move(point: Offset) {
                if (!holding) return
                val next = point + grab
                scrub(brush, next)
                brush = next
            }

            override fun up(point: Offset) {
                holding = false
            }

            override fun cancel() {
                holding = false
            }
        }
    }

    StageCanvas(touch) { stage ->
        val t = clock.value
        drawOpenMouth(game.data.look, dirt.toFloatArray(), t, stage)
        for ((at, born) in foam) if (t - born < FOAM_SECONDS) drawFoam(at, t - born)
        val clean = cleanAt
        if (clean != null) drawSparkles(MOUTH.center, 200f, t)
        drawToothbrush(brush, foam = holding)
        if (clean == null && !holding && dirt.all { it == 1f }) drawCallRing(brush + Offset(4f, -6f), 34f, t)
        drawHomeButton(stage)
        drawSpeakerButton(stage)
    }
}

private const val FOAM_EVERY = 0.07f
private const val PAUSE_MILLIS = 900L
private const val IDLE_MILLIS = 10_000L
