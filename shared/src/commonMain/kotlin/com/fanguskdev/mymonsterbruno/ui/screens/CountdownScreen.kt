package com.fanguskdev.mymonsterbruno.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.rememberTextMeasurer
import com.fanguskdev.mymonsterbruno.audio.Sfx
import com.fanguskdev.mymonsterbruno.audio.Voice
import com.fanguskdev.mymonsterbruno.game.COUNTDOWN_FROM
import com.fanguskdev.mymonsterbruno.game.Countdown
import com.fanguskdev.mymonsterbruno.game.countdownPanel
import com.fanguskdev.mymonsterbruno.ui.GameState
import com.fanguskdev.mymonsterbruno.ui.Stage
import com.fanguskdev.mymonsterbruno.ui.StageCanvas
import com.fanguskdev.mymonsterbruno.ui.Touch
import com.fanguskdev.mymonsterbruno.ui.andika
import com.fanguskdev.mymonsterbruno.ui.art.Arms
import com.fanguskdev.mymonsterbruno.ui.art.Eyes
import com.fanguskdev.mymonsterbruno.ui.art.Mouth
import com.fanguskdev.mymonsterbruno.ui.art.Pose
import com.fanguskdev.mymonsterbruno.ui.art.Shadow
import com.fanguskdev.mymonsterbruno.ui.art.drawGarden
import com.fanguskdev.mymonsterbruno.ui.art.drawLaunchPad
import com.fanguskdev.mymonsterbruno.ui.art.drawLaunchTower
import com.fanguskdev.mymonsterbruno.ui.art.drawRocket
import com.fanguskdev.mymonsterbruno.ui.art.drawSmoke
import com.fanguskdev.mymonsterbruno.ui.art.drawSparkles
import com.fanguskdev.mymonsterbruno.ui.circle
import com.fanguskdev.mymonsterbruno.ui.clamp01
import com.fanguskdev.mymonsterbruno.ui.easeOut
import com.fanguskdev.mymonsterbruno.ui.label
import com.fanguskdev.mymonsterbruno.ui.lerp
import com.fanguskdev.mymonsterbruno.ui.rememberClock
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.sin

/** The rocket stands on its pad on the grass, with the tower of lights beside it. */
private val ROCKET_BASE = Offset(200f, 300f)
private const val TOWER_X = 318f
private const val TOWER_TOP = 72f
private const val TOWER_BOTTOM = 296f

/**
 * The panel: the ten numbers in two rows, in any order, and the big red button of the zero under them; its right side
 * clear of the Dynamic Island of her iPhone, held with the island on the right.
 */
private const val PANEL_LEFT = 389f
private val SPOTS = listOf(175f, 255f).flatMap { y -> (0 until 5).map { i -> Offset(PANEL_LEFT + 42f + 75f * i, y) } }
private const val BUTTON_RADIUS = 31f
private val ZERO = Offset(PANEL_LEFT + 192f, 345f)
private const val ZERO_RADIUS = 40f

private enum class CountdownPhase { COUNTING, LAUNCHING, AWAY, LANDING, LANDED }

/**
 * The countdown, the second thing of the island of numbers (ADR 0023): Bruno is in his rocket, and she counts down for
 * it to take off, touching ten, nine... to one on the panel, where they are in any order, while the voice says each one
 * and a light of the tower comes on. Then the zero, the big red button: «¡Cero! ¡Despegue!», the rocket shakes, rises
 * in its smoke and flies out of sight, and comes back down to its pad. A number that is not the one that goes wiggles,
 * and the voice asks for the right one.
 */
@Composable
internal fun CountdownScreen(game: GameState, onDone: () -> Unit) {
    val clock = rememberClock()
    val scope = rememberCoroutineScope()
    val measurer = rememberTextMeasurer()
    val font = andika()
    val audio = game.audio
    val panel = remember { countdownPanel(game.random) }
    val countdown = remember { Countdown() }
    var next by remember { mutableStateOf(COUNTDOWN_FROM) }
    var phase by remember { mutableStateOf(CountdownPhase.COUNTING) }
    var phaseAt by remember { mutableStateOf(0f) }
    var introduced by remember { mutableStateOf(false) }

    // The button that is not the one that goes, and when: it wiggles a moment.
    var wrong by remember { mutableStateOf<Pair<Int, Float>?>(null) }

    fun prompt() = if (next == 0) Voice.PRESS_ZERO else Voice.whereIs(next)

    // The voice says what to do the first time, and asks again when she waits a while.
    LaunchedEffect(next, phase) {
        if (phase != CountdownPhase.COUNTING) return@LaunchedEffect
        if (!introduced) {
            introduced = true
            audio.say(Voice.ROCKET_INTRO, prompt())
        }
        while (true) {
            delay(IDLE_MILLIS)
            audio.say(prompt())
        }
    }

    fun launch() {
        countdown.touch(0)
        next = countdown.next
        phase = CountdownPhase.LAUNCHING
        phaseAt = clock.value
        audio.hush()
        audio.sfx(Sfx.ROCKET)
        game.update { it.copy(launches = it.launches + 1) }
        scope.launch {
            launch { audio.say(Voice.LIFT_OFF, Voice.OFF_I_GO) }
            delay(LAUNCH_MILLIS)
            phase = CountdownPhase.AWAY
            phaseAt = clock.value
            delay(AWAY_MILLIS)
            phase = CountdownPhase.LANDING
            phaseAt = clock.value
            delay(LANDING_MILLIS)
            phase = CountdownPhase.LANDED
            phaseAt = clock.value
            audio.sfx(Sfx.SPARKLE)
            audio.say(Voice.THANKS_ROCKET)
            delay(PAUSE_MILLIS)
            onDone()
        }
    }

    val touch = remember {
        object : Touch {
            override fun down(point: Offset, stage: Stage): Boolean {
                if (hits(point, speakerButton(stage), CORNER_RADIUS)) {
                    if (phase == CountdownPhase.COUNTING) audio.speak(prompt())
                    return true
                }
                if (hits(point, homeButton(stage), CORNER_RADIUS)) {
                    audio.hush()
                    game.back()
                    return true
                }
                if (phase != CountdownPhase.COUNTING) return false
                if (hits(point, ZERO, ZERO_RADIUS)) {
                    if (next == 0) {
                        launch()
                    } else {
                        audio.sfx(Sfx.BOING)
                        audio.speak(prompt())
                    }
                    return true
                }
                val spot = SPOTS.indices.firstOrNull { hits(point, SPOTS[it], BUTTON_RADIUS) } ?: return false
                val number = panel[spot]
                if (number > next) return true
                if (countdown.touch(number)) {
                    next = countdown.next
                    audio.sfx(Sfx.TAP)
                    scope.launch {
                        audio.say(Voice.down(number))
                        if (next == 0) audio.say(Voice.PRESS_ZERO)
                    }
                } else {
                    wrong = spot to clock.value
                    audio.sfx(Sfx.BOING)
                    audio.speak(prompt())
                }
                return true
            }
        }
    }

    StageCanvas(touch) { stage ->
        val t = clock.value
        val since = t - phaseAt
        drawGarden(stage, t)
        drawLaunchTower(TOWER_X, TOWER_TOP, TOWER_BOTTOM, COUNTDOWN_FROM - next.coerceAtLeast(0), t)
        drawLaunchPad(ROCKET_BASE + Offset(0f, 4f))

        // Where the rocket is, how strong its flame, and how Bruno looks out of its window.
        val (lift, firing, shake) = when (phase) {
            CountdownPhase.COUNTING -> Triple(0f, 0f, 0f)
            CountdownPhase.LAUNCHING ->
                if (since < IGNITION_SECONDS) {
                    Triple(0f, 0.8f * since / IGNITION_SECONDS, 2.5f * sin(since * 70f))
                } else {
                    val r = since - IGNITION_SECONDS
                    Triple(-(r * r * 320f + r * 40f), 1f, 0f)
                }
            CountdownPhase.AWAY -> Triple(-900f, 0f, 0f)
            CountdownPhase.LANDING -> {
                val k = easeOut(since / (LANDING_MILLIS / MILLIS))
                Triple(lerp(-520f, 0f, k), 0.5f * (1f - k), 0f)
            }
            CountdownPhase.LANDED -> Triple(0f, 0f, 0f)
        }
        val pose = when (phase) {
            CountdownPhase.COUNTING -> Pose(Mouth.OPEN, Eyes.OPEN, Arms.UP, lookX = 1f, lookY = 0.3f)
            else -> Pose(Mouth.GRIN, Eyes.HAPPY, Arms.UP)
        }
        if (phase == CountdownPhase.LAUNCHING) drawSmoke(ROCKET_BASE, since)
        drawRocket(game.data.look, ROCKET_BASE + Offset(shake, lift), pose, firing, t)
        if (phase == CountdownPhase.LANDED) drawSparkles(ROCKET_BASE + Offset(0f, -140f), 120f, t)

        // The panel.
        drawRoundRect(Shadow, Offset(PANEL_LEFT, 134f), Size(384f, 166f), CornerRadius(22f))
        drawRoundRect(Color(0xFFDDE5F2), Offset(PANEL_LEFT, 128f), Size(384f, 166f), CornerRadius(22f))
        drawRoundRect(Color(0xFFB9C4D8), Offset(PANEL_LEFT, 128f), Size(384f, 166f), CornerRadius(22f), style = Stroke(3f))
        val colors = listOf(Color(0xFFE4483C), Color(0xFF2C74D8), Color(0xFF2E9E5B), Color(0xFF9B6BD3))
        for ((i, number) in panel.withIndex()) {
            val counted = number > next
            val shook = wrong?.takeIf { it.first == i }?.let { (_, at) -> 6f * sin((t - at) * 30f) * (1f - clamp01((t - at) / 0.5f)) }
            val center = SPOTS[i] + Offset(shook ?: 0f, 0f)
            circle(Shadow, center + Offset(0f, 4f), BUTTON_RADIUS)
            circle(if (counted) Color(0xFFFFD54A) else Color.White, center, BUTTON_RADIUS)
            val ink = if (counted) Color.White else colors[number % colors.size]
            label(measurer, number.toString(), center + Offset(0f, 1f), if (number > 9) 30f else 36f, ink, font)
        }
        if (next == 0 && phase == CountdownPhase.COUNTING) drawCallRing(ZERO, ZERO_RADIUS * 1.15f, t, Color(0xFFE4483C))
        circle(Shadow, ZERO + Offset(0f, 5f), ZERO_RADIUS)
        circle(if (next <= 0) Color(0xFFE4483C) else Color(0xFFE9A19B), ZERO, ZERO_RADIUS)
        label(measurer, "0", ZERO + Offset(0f, 1f), 44f, Color.White, font)
        drawHomeButton(stage)
        drawSpeakerButton(stage)
    }
}

/** How long she can wait before the voice asks again, and how long each part of the flight lasts. */
private const val IDLE_MILLIS = 9000L
private const val IGNITION_SECONDS = 0.9f
private const val LAUNCH_MILLIS = 2400L
private const val AWAY_MILLIS = 1200L
private const val LANDING_MILLIS = 2500L
private const val PAUSE_MILLIS = 600L
private const val MILLIS = 1000f
