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
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.rememberTextMeasurer
import com.fanguskdev.mymonsterbruno.audio.Sfx
import com.fanguskdev.mymonsterbruno.audio.Voice
import com.fanguskdev.mymonsterbruno.game.Weather
import com.fanguskdev.mymonsterbruno.game.Weekday
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
import com.fanguskdev.mymonsterbruno.ui.art.drawBruno
import com.fanguskdev.mymonsterbruno.ui.art.drawRoom
import com.fanguskdev.mymonsterbruno.ui.art.drawSparkles
import com.fanguskdev.mymonsterbruno.ui.art.drawWeatherIcon
import com.fanguskdev.mymonsterbruno.ui.circle
import com.fanguskdev.mymonsterbruno.ui.clamp01
import com.fanguskdev.mymonsterbruno.ui.fill
import com.fanguskdev.mymonsterbruno.ui.label
import com.fanguskdev.mymonsterbruno.ui.rememberClock
import com.fanguskdev.mymonsterbruno.ui.ring
import com.fanguskdev.mymonsterbruno.ui.sparkle
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.sin

/** The calendar of the week on the wall, Monday to Sunday, as in her class: clear of the window and of the Dynamic Island. */
private val DAYS = Weekday.entries.indices.map { Offset(331f + 69f * it, 112f) }
private val DAY_CARD = Size(60f, 76f)

/** The colours of the days, as the calendars of a classroom; the letter goes dark on the yellow one. */
private val DAY_COLORS = listOf(
    Color(0xFFE4483C),
    Color(0xFFF28B2E),
    Color(0xFFF4C423),
    Color(0xFF2E9E5B),
    Color(0xFF2C74D8),
    Color(0xFF9B6BD3),
    Color(0xFFF06E98),
)

/** Bruno on the rug, under the window; and the four weathers to choose, on the floor. */
private val BRUNO_CENTER = Offset(170f, 282f)
private const val BRUNO_HEIGHT = 150f
private val WEATHERS = Weather.entries.indices.map { Offset(352f + 112f * it, 302f) }
private const val WEATHER_RADIUS = 46f

private val GOLD = Color(0xFFF4B623)
private val INK = Color(0xFF1D2B53)

private enum class AssemblyPhase { DAY, WEEK, WEATHER, CHOSEN }

/**
 * «La asamblea de Bruno» (ADR 0030), after the feeling of the day, as in the assembly of her class: the calendar of the
 * week on the wall of the room, «¿Qué día es hoy?», Bruno says it, «¡Hoy es martes!», and she touches its card, which
 * glows; the voice says the day before and the one after, «Ayer fue lunes y mañana será miércoles», while they glow too.
 * Then «¿Qué tiempo hace hoy? Mira por la ventana y dímelo», and she touches what she sees out of her own window: sun,
 * clouds, rain or snow. The window of Bruno's room shows it for the rest of the day, and Bruno says something about it.
 */
@Composable
internal fun AssemblyScreen(game: GameState, onDone: () -> Unit) {
    val clock = rememberClock()
    val scope = rememberCoroutineScope()
    val measurer = rememberTextMeasurer()
    val font = andika()
    val audio = game.audio
    val today = remember { game.weekday }
    var phase by remember { mutableStateOf(AssemblyPhase.DAY) }
    var phaseAt by remember { mutableStateOf(0f) }
    var found by remember { mutableStateOf(false) }

    // The day card she touched that is not today, and when; the days the voice is naming; and the weather she chose.
    var wrong by remember { mutableStateOf<Pair<Int, Float>?>(null) }
    var naming by remember { mutableStateOf<Weekday?>(null) }
    var chosen by remember { mutableStateOf<Weather?>(null) }

    LaunchedEffect(Unit) {
        audio.say(Voice.ASSEMBLY_INTRO, Voice.todayIs(today))
    }

    // The voice asks again when she waits a while.
    LaunchedEffect(phase) {
        if (phase != AssemblyPhase.DAY && phase != AssemblyPhase.WEATHER) return@LaunchedEffect
        while (true) {
            delay(IDLE_MILLIS)
            audio.say(if (phase == AssemblyPhase.DAY) Voice.todayIs(today) else Voice.WHAT_WEATHER)
        }
    }

    fun touchDay(day: Weekday) {
        if (day != today) {
            wrong = day.ordinal to clock.value
            audio.sfx(Sfx.BOING)
            audio.speak(Voice.thatDay(day), Voice.todayIs(today))
            return
        }
        found = true
        phase = AssemblyPhase.WEEK
        phaseAt = clock.value
        audio.sfx(Sfx.RIGHT)
        scope.launch {
            delay(SHORT_MILLIS)
            naming = today.yesterday
            launch {
                delay(NAMING_MILLIS)
                naming = today.tomorrow
            }
            audio.say(Voice.aroundToday(today))
            naming = null
            phase = AssemblyPhase.WEATHER
            phaseAt = clock.value
            audio.say(Voice.WHAT_WEATHER)
        }
    }

    fun touchWeather(weather: Weather) {
        chosen = weather
        phase = AssemblyPhase.CHOSEN
        phaseAt = clock.value
        audio.sfx(Sfx.SPARKLE)
        game.sawWeather(weather)
        scope.launch {
            audio.say(Voice.weatherIs(weather), Voice.DAY_STARTS)
            delay(PAUSE_MILLIS)
            onDone()
        }
    }

    val touch = remember {
        object : Touch {
            override fun down(point: Offset, stage: Stage): Boolean {
                if (hits(point, speakerButton(stage), CORNER_RADIUS)) {
                    when (phase) {
                        AssemblyPhase.DAY -> audio.speak(Voice.todayIs(today))
                        AssemblyPhase.WEATHER -> audio.speak(Voice.WHAT_WEATHER)
                        else -> Unit
                    }
                    return true
                }
                if (hits(point, homeButton(stage), CORNER_RADIUS)) {
                    audio.hush()
                    game.back()
                    return true
                }
                when (phase) {
                    AssemblyPhase.DAY -> {
                        val day = DAYS.indices.firstOrNull { inCard(point, DAYS[it], DAY_CARD) } ?: return false
                        touchDay(Weekday.entries[day])
                    }
                    AssemblyPhase.WEATHER -> {
                        val weather = WEATHERS.indices.firstOrNull { hits(point, WEATHERS[it], WEATHER_RADIUS) } ?: return false
                        touchWeather(Weather.entries[weather])
                    }
                    else -> return false
                }
                return true
            }
        }
    }

    StageCanvas(touch) { stage ->
        val t = clock.value
        val since = t - phaseAt
        drawRoom(stage, t, chosen)
        for ((i, day) in Weekday.entries.withIndex()) {
            val shake = wrong?.takeIf { it.first == i }?.let { (_, at) ->
                6f * sin((t - at) * 30f) * (1f - clamp01((t - at) / WIGGLE_SECONDS))
            } ?: 0f
            drawDayCard(DAYS[i] + Offset(shake, 0f), day, today = found && day == today, named = naming == day, measurer, font)
        }
        if (phase == AssemblyPhase.DAY) drawCallRing(DAYS[today.ordinal], DAY_CARD.height * 0.62f, t, GOLD)
        if (phase == AssemblyPhase.WEATHER || phase == AssemblyPhase.CHOSEN) {
            for ((i, weather) in Weather.entries.withIndex()) {
                val center = WEATHERS[i]
                val picked = chosen == weather
                circle(Shadow, center + Offset(0f, 5f), WEATHER_RADIUS)
                circle(if (picked) Color(0xFFFFF1C2) else Color.White, center, WEATHER_RADIUS)
                if (picked) ring(GOLD, center, WEATHER_RADIUS, 5f)
                drawWeatherIcon(weather, center, WEATHER_RADIUS * 1.5f, t)
            }
        }
        val pose = when (phase) {
            AssemblyPhase.DAY -> Pose(Mouth.OPEN, Eyes.OPEN, Arms.DOWN, lookX = 1f, lookY = -0.6f, hop = 1.5f * sin(t * 1.6f))
            AssemblyPhase.WEEK -> Pose(Mouth.SMILE, Eyes.OPEN, Arms.DOWN, lookX = 1f, lookY = -0.6f)
            AssemblyPhase.WEATHER -> Pose(Mouth.OPEN, Eyes.OPEN, Arms.DOWN, lookX = -0.6f, lookY = -0.8f)
            AssemblyPhase.CHOSEN -> Pose(Mouth.GRIN, Eyes.HAPPY, Arms.UP, hop = 8f * sin(since * 6f).coerceAtLeast(0f))
        }
        drawBruno(game.data.look, BRUNO_CENTER, BRUNO_HEIGHT, pose, t)
        if (phase == AssemblyPhase.CHOSEN) drawSparkles(BRUNO_CENTER, 100f, t)
        drawHomeButton(stage)
        drawSpeakerButton(stage)
    }
}

/**
 * The card of a [day] on the calendar, its colour and its letter: with a gold edge and a star once she has found it is
 * [today], and lifted a little while the voice names it ([named]).
 */
private fun DrawScope.drawDayCard(
    center: Offset,
    day: Weekday,
    today: Boolean,
    named: Boolean,
    measurer: TextMeasurer,
    font: FontFamily,
) {
    val at = center + Offset(0f, if (named) -8f else 0f)
    val topLeft = at - Offset(DAY_CARD.width / 2, DAY_CARD.height / 2)
    val color = DAY_COLORS[day.ordinal]
    drawRoundRect(Shadow, topLeft + Offset(0f, 5f), DAY_CARD, CornerRadius(12f))
    drawRoundRect(color, topLeft, DAY_CARD, CornerRadius(12f))
    if (today || named) drawRoundRect(if (today) GOLD else Color.White, topLeft, DAY_CARD, CornerRadius(12f), style = Stroke(5f))
    label(measurer, day.letter, at + Offset(0f, 4f), 40f, if (day == Weekday.WEDNESDAY) INK else Color.White, font)
    if (today) fill(sparkle(at + Offset(DAY_CARD.width / 2 - 4f, -DAY_CARD.height / 2 + 4f), 14f), GOLD)
}

private const val SHORT_MILLIS = 400L
private const val NAMING_MILLIS = 1700L
private const val PAUSE_MILLIS = 600L
private const val WIGGLE_SECONDS = 0.5f
private const val IDLE_MILLIS = 10_000L
