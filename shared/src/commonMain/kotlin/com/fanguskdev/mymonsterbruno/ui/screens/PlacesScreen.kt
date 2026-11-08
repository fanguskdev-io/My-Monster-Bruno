package com.fanguskdev.mymonsterbruno.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import com.fanguskdev.mymonsterbruno.audio.Sfx
import com.fanguskdev.mymonsterbruno.audio.Voice
import com.fanguskdev.mymonsterbruno.game.Place
import com.fanguskdev.mymonsterbruno.game.Placing
import com.fanguskdev.mymonsterbruno.game.Thing
import com.fanguskdev.mymonsterbruno.game.placings
import com.fanguskdev.mymonsterbruno.game.plusOne
import com.fanguskdev.mymonsterbruno.ui.GameState
import com.fanguskdev.mymonsterbruno.ui.Stage
import com.fanguskdev.mymonsterbruno.ui.StageCanvas
import com.fanguskdev.mymonsterbruno.ui.Touch
import com.fanguskdev.mymonsterbruno.ui.art.Arms
import com.fanguskdev.mymonsterbruno.ui.art.Eyes
import com.fanguskdev.mymonsterbruno.ui.art.Mouth
import com.fanguskdev.mymonsterbruno.ui.art.Pose
import com.fanguskdev.mymonsterbruno.ui.art.drawBall
import com.fanguskdev.mymonsterbruno.ui.art.drawBoxBack
import com.fanguskdev.mymonsterbruno.ui.art.drawBoxFront
import com.fanguskdev.mymonsterbruno.ui.art.drawBruno
import com.fanguskdev.mymonsterbruno.ui.art.drawRoom
import com.fanguskdev.mymonsterbruno.ui.art.drawSparkles
import com.fanguskdev.mymonsterbruno.ui.art.drawTable
import com.fanguskdev.mymonsterbruno.ui.circle
import com.fanguskdev.mymonsterbruno.ui.clamp01
import com.fanguskdev.mymonsterbruno.ui.distance
import com.fanguskdev.mymonsterbruno.ui.rememberClock
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin

/** The big box and the small one, by their front faces, and the board of the table: on the floor of Bruno's room. */
private val BIG_BOX = Rect(226f, 262f, 376f, 352f)
private val SMALL_BOX = Rect(436f, 298f, 526f, 352f)
private val TABLE = Rect(580f, 224f, 752f, 238f)
private const val FLOOR = 350f

/** Where Bruno and his ball wait their turn: on the floor at the left, under the window. */
private const val BRUNO_SPOT = 86f
private const val BALL_SPOT = 172f

/** Bruno is small here, to hide in a box or under the table, and his ball smaller. */
private const val BRUNO_HEIGHT = 88f
private const val BALL_RADIUS = 24f

/** Where Bruno's feet and the top of his horns are, from his centre, in parts of his height (as drawBruno draws him). */
private const val FEET = 0.38f
private const val TOP = 0.53f

/** How far a finger may be from the centre of what it takes. */
private const val GRAB_RADIUS = 46f

private enum class PlacesPhase { ASKING, WRONG, RIGHT, THANKS }

/**
 * «Dentro y fuera», the second thing of the island of shapes (ADR 0028): in Bruno's room, a big box, a small one and a
 * table, and the voice asks her to put Bruno, or his ball, somewhere: «Esconde a Bruno dentro de la caja grande», «Pon la
 * pelota encima de la mesa», «Saca la pelota fuera de la caja». She drags it there: if it is, it stays and the voice says
 * where it is; if not, it stays a moment where she left it while the voice says where that is («Huum... Así está encima
 * de la mesa»), goes back and the place she was asked for glows. Five rounds, every place once, Bruno and the ball by
 * turns.
 */
@Composable
internal fun PlacesScreen(game: GameState, onDone: () -> Unit) {
    val clock = rememberClock()
    val scope = rememberCoroutineScope()
    val audio = game.audio
    val rounds = remember { placings(game.random) }
    val brunoHeight = BRUNO_HEIGHT * game.data.look.size
    var round by remember { mutableStateOf(0) }
    var phase by remember { mutableStateOf(PlacesPhase.ASKING) }
    var phaseAt by remember { mutableStateOf(0f) }

    // Where Bruno and the ball are, and in which place; those flying somewhere, and from which place; the one in her
    // finger; and whether the place she is asked for glows, after a mistake.
    val at =
        remember { mutableStateMapOf<Thing, Offset>().apply { Thing.entries.forEach { put(it, startPoint(it, rounds[0], brunoHeight)) } } }
    val inside = remember { mutableStateMapOf<Thing, Place?>().apply { Thing.entries.forEach { put(it, startPlace(it, rounds[0])) } } }
    val flights = remember { mutableStateMapOf<Thing, Flight>() }
    val leaving = remember { mutableStateMapOf<Thing, Place?>() }
    var dragging by remember { mutableStateOf<Thing?>(null) }
    var grab by remember { mutableStateOf(Offset.Zero) }
    var downAt by remember { mutableStateOf(Offset.Zero) }
    var hinted by remember { mutableStateOf(false) }

    fun current() = rounds[round.coerceAtMost(rounds.lastIndex)]

    fun restAt(thing: Thing, place: Place?, x: Float) = rest(thing, place, x, brunoHeight)

    fun startOf(thing: Thing) = startPlace(thing, current())

    fun startAt(thing: Thing) = startPoint(thing, current(), brunoHeight)

    /** Sends [thing] flying from where it is to [place] at [to]. */
    fun fly(
        thing: Thing,
        to: Offset,
        place: Place?,
        seconds: Float,
    ) {
        val from = at[thing] ?: to
        leaving[thing] = inside[thing]
        flights[thing] = Flight(from, to, clock.value, seconds)
        at[thing] = to
        inside[thing] = place
    }

    LaunchedEffect(round) {
        hinted = false
        for (thing in Thing.entries) fly(thing, startAt(thing), startOf(thing), SETTLE_SECONDS)
        phase = PlacesPhase.ASKING
        phaseAt = clock.value
        if (round == 0) audio.say(Voice.PLACES_INTRO)
        audio.say(Voice.putIt(current()))
    }

    // The voice asks again when she waits a while.
    LaunchedEffect(round, phase) {
        if (phase != PlacesPhase.ASKING) return@LaunchedEffect
        while (true) {
            delay(IDLE_MILLIS)
            audio.say(Voice.putIt(current()))
        }
    }

    fun right(thing: Thing, place: Place?, point: Offset) {
        val placing = current()
        fly(thing, restAt(thing, place, point.x), place, SETTLE_SECONDS)
        phase = PlacesPhase.RIGHT
        phaseAt = clock.value
        audio.sfx(Sfx.RIGHT)
        game.update { it.copy(places = it.places.plusOne(placing.place.word)) }
        scope.launch {
            audio.say(Voice.itIs(placing))
            if (thing == Thing.BRUNO && place in HIDING) audio.say(Voice.HIDDEN)
            delay(PAUSE_MILLIS)
            if (round + 1 < rounds.size) {
                round++
            } else {
                phase = PlacesPhase.THANKS
                phaseAt = clock.value
                for (each in Thing.entries) {
                    fly(
                        each,
                        restAt(
                            each,
                            null,
                            if (each ==
                                Thing.BRUNO
                            ) {
                                BRUNO_SPOT
                            } else {
                                BALL_SPOT
                            },
                        ),
                        null,
                        SETTLE_SECONDS,
                    )
                }
                audio.sfx(Sfx.SPARKLE)
                audio.say(Voice.THANKS_PLACES)
                onDone()
            }
        }
    }

    fun wrong(thing: Thing, place: Place?, point: Offset) {
        val placing = current()
        fly(thing, restAt(thing, place, point.x), place, SETTLE_SECONDS)
        phase = PlacesPhase.WRONG
        phaseAt = clock.value
        hinted = true
        audio.sfx(Sfx.BOING)
        scope.launch {
            audio.say(Voice.leftAt(place))
            fly(thing, startAt(thing), startOf(thing), RETURN_SECONDS)
            delay(RETURN_MILLIS)
            phase = PlacesPhase.ASKING
            phaseAt = clock.value
            audio.speak(Voice.putIt(placing))
        }
    }

    val touch = remember {
        object : Touch {
            override fun down(point: Offset, stage: Stage): Boolean {
                if (hits(point, speakerButton(stage), CORNER_RADIUS)) {
                    if (phase == PlacesPhase.ASKING) audio.speak(Voice.putIt(current()))
                    return true
                }
                if (hits(point, homeButton(stage), CORNER_RADIUS)) {
                    audio.hush()
                    game.back()
                    return true
                }
                if (phase != PlacesPhase.ASKING) return false
                val thing = current().thing
                val center = at[thing] ?: return false
                if (!hits(point, center, GRAB_RADIUS)) return false
                dragging = thing
                grab = center - point
                downAt = point
                flights.remove(thing)
                leaving.remove(thing)
                inside[thing] = null
                audio.sfx(Sfx.POP)
                return true
            }

            override fun move(point: Offset) {
                val thing = dragging ?: return
                at[thing] = point + grab
            }

            override fun up(point: Offset) {
                val thing = dragging ?: return
                dragging = null
                val center = point + grab
                if (distance(point, downAt) < TAP_MOVE) {
                    fly(thing, startAt(thing), startOf(thing), RETURN_SECONDS)
                    audio.speak(Voice.putIt(current()))
                    return
                }
                val place = placeAt(center)
                if (current().rightAt(place)) right(thing, place, center) else wrong(thing, place, center)
            }

            override fun cancel() {
                val thing = dragging ?: return
                dragging = null
                fly(thing, startAt(thing), startOf(thing), RETURN_SECONDS)
            }
        }
    }

    StageCanvas(touch) { stage ->
        val t = clock.value
        val since = t - phaseAt
        val placing = current()
        drawRoom(stage, t, game.weather)
        if (hinted && phase == PlacesPhase.ASKING) drawHint(placing.place.area(), t)
        drawTable(TABLE, FLOOR)

        /** Where [thing] is drawn now: flying with a little hop, in her finger, or resting. */
        fun where(thing: Thing): Offset {
            val flight = flights[thing]?.takeIf { !it.over(t) } ?: return at.getValue(thing)
            val k = clamp01((t - flight.start) / flight.length)
            return flight.at(t) + Offset(0f, -HOP * sin(PI.toFloat() * k))
        }

        /** The box [thing] is drawn in, if it is in one: between its back and its front. */
        fun layer(thing: Thing): Place? {
            if (dragging == thing) return null
            val flight = flights[thing]?.takeIf { !it.over(t) }
            val place = when {
                flight == null -> inside[thing]
                (t - flight.start) / flight.length < 0.5f && leaving[thing] in BOXES -> leaving[thing]
                else -> inside[thing]
            }
            return place?.takeIf { it in BOXES }
        }

        fun drawThing(thing: Thing) {
            val center = where(thing)
            if (thing == Thing.BALL) {
                val resting = dragging != thing && flights[thing]?.over(t) != false
                drawBall(center, BALL_RADIUS, turn = center.x * 2.2f, shadow = resting && layer(thing) == null)
                return
            }
            val hidden = inside[thing] in HIDING && flights[thing]?.over(t) != false
            val ballX = where(Thing.BALL).x
            val pose = when {
                dragging == thing -> Pose(Mouth.OPEN, Eyes.WIDE, Arms.UP)
                flights[thing]?.over(t) == false -> Pose(Mouth.GRIN, Eyes.HAPPY, Arms.UP)
                phase == PlacesPhase.THANKS -> Pose(Mouth.GRIN, Eyes.HAPPY, Arms.UP, hop = 8f * sin(since * 6f).coerceAtLeast(0f))
                phase == PlacesPhase.WRONG && placing.thing == thing -> Pose(Mouth.WAVY, Eyes.WIDE, Arms.DOWN)
                hidden && phase == PlacesPhase.RIGHT -> Pose(Mouth.GRIN, Eyes.HAPPY, Arms.DOWN)
                placing.thing == Thing.BALL -> Pose(lookX = if (ballX > center.x) 1f else -1f, lookY = 0.4f)
                else -> Pose(lookX = 0.8f, lookY = 0.2f, hop = 1.5f * sin(t * 1.6f))
            }
            drawBruno(game.data.look, center, brunoHeight, pose, t)
        }

        val layers = Thing.entries.associateWith { layer(it) }
        for (box in BOXES) {
            drawBoxBack(box.front())
            for (thing in Thing.entries) if (layers[thing] == box) drawThing(thing)
            drawBoxFront(box.front())
        }
        for (thing in Thing.entries) if (layers[thing] == null && dragging != thing) drawThing(thing)
        if (phase == PlacesPhase.ASKING && dragging == null) drawCallRing(where(placing.thing), GRAB_RADIUS, t, GOLD)
        dragging?.let { drawThing(it) }
        if (phase == PlacesPhase.RIGHT) drawSparkles(where(placing.thing), 50f, t)

        for (i in rounds.indices) {
            val done = i < round || (i == round && (phase == PlacesPhase.RIGHT || phase == PlacesPhase.THANKS))
            circle(if (done) Color(0xFFE9B36A) else Color(0x99FFFFFF), Offset(340f + 30f * i, 26f), 10f)
        }
        drawHomeButton(stage)
        drawSpeakerButton(stage)
    }
}

/** The boxes, which hide what is inside them, and the places where Bruno is hidden. */
private val BOXES = listOf(Place.BIG_BOX, Place.SMALL_BOX)
private val HIDING = listOf(Place.BIG_BOX, Place.SMALL_BOX, Place.UNDER_TABLE)

private fun Place.front(): Rect = if (this == Place.BIG_BOX) BIG_BOX else SMALL_BOX

/** Where [thing] starts a round of [placing]: in the big box if she has to take it out, at its spot if not. */
private fun startPlace(thing: Thing, placing: Placing): Place? =
    if (thing == placing.thing && placing.place == Place.OUT_OF_BOX) Place.BIG_BOX else null

private fun startPoint(thing: Thing, placing: Placing, brunoHeight: Float): Offset =
    rest(thing, startPlace(thing, placing), if (thing == Thing.BRUNO) BRUNO_SPOT else BALL_SPOT, brunoHeight)

/** Where something let go with its centre at [point] ends: in a box, on the table or under it; null on the floor. */
private fun placeAt(point: Offset): Place? = when {
    point.x in BIG_BOX.left..BIG_BOX.right && point.y in (BIG_BOX.top - 80f)..(BIG_BOX.bottom + 10f) -> Place.BIG_BOX
    point.x in SMALL_BOX.left..SMALL_BOX.right && point.y in (SMALL_BOX.top - 70f)..(SMALL_BOX.bottom + 10f) -> Place.SMALL_BOX
    point.x in (TABLE.left - 8f)..(TABLE.right + 8f) && point.y < TABLE.bottom -> Place.ON_TABLE
    point.x in TABLE.left..TABLE.right -> Place.UNDER_TABLE
    else -> null
}

/**
 * Where [thing] rests at [place] (null: the floor), let go near [x]: in a box peeking out, Bruno up to his eyes and the
 * ball a little; on the board of the table; under it, or on the floor. [brunoHeight] is how tall Bruno is drawn.
 */
private fun rest(
    thing: Thing,
    place: Place?,
    x: Float,
    brunoHeight: Float,
): Offset {
    val bruno = thing == Thing.BRUNO
    // How far its centre is over what it stands on, and from the top of a box when it is inside it.
    val lift = if (bruno) brunoHeight * FEET else BALL_RADIUS
    fun inBox(box: Rect, peek: Float) =
        Offset(box.center.x, if (bruno) box.top - brunoHeight * peek + brunoHeight * TOP else box.top + BALL_RADIUS * 0.35f)
    return when (place) {
        Place.BIG_BOX -> inBox(BIG_BOX, BIG_PEEK)
        Place.SMALL_BOX -> inBox(SMALL_BOX, SMALL_PEEK)
        Place.ON_TABLE -> Offset(x.coerceIn(TABLE.left + 30f, TABLE.right - 30f), TABLE.top - lift)
        Place.UNDER_TABLE -> Offset(x.coerceIn(TABLE.left + 44f, TABLE.right - 44f), FLOOR - lift)
        else -> Offset(x.coerceIn(50f, 760f), FLOOR - lift)
    }
}

/** The place she was asked for, glowing: what she has to look at, after a mistake. Out of the box is the floor where it waited. */
private fun Place.area(): Rect = when (this) {
    Place.BIG_BOX -> Rect(BIG_BOX.left - 12f, BIG_BOX.top - 44f, BIG_BOX.right + 12f, BIG_BOX.bottom + 8f)
    Place.SMALL_BOX -> Rect(SMALL_BOX.left - 12f, SMALL_BOX.top - 40f, SMALL_BOX.right + 12f, SMALL_BOX.bottom + 8f)
    Place.ON_TABLE -> Rect(TABLE.left - 8f, TABLE.top - 78f, TABLE.right + 8f, TABLE.bottom + 4f)
    Place.UNDER_TABLE -> Rect(TABLE.left + 8f, TABLE.bottom + 4f, TABLE.right - 8f, FLOOR + 8f)
    Place.OUT_OF_BOX -> Rect(36f, 262f, 214f, FLOOR + 10f)
}

private fun DrawScope.drawHint(area: Rect, time: Float) {
    val glow = 0.18f + 0.12f * sin(time * 4f)
    drawRoundRect(GOLD, area.topLeft, area.size, CornerRadius(18f), alpha = glow)
    drawRoundRect(
        GOLD,
        area.topLeft,
        area.size,
        CornerRadius(18f),
        style = Stroke(4f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(14f, 10f), time * 24f)),
    )
}

private val GOLD = Color(0xFFF4B623)

/** How much of Bruno peeks out of each box, in parts of his height. */
private const val BIG_PEEK = 0.54f
private const val SMALL_PEEK = 0.6f

/** How high things hop when they fly somewhere. */
private const val HOP = 26f

private const val SETTLE_SECONDS = 0.35f
private const val RETURN_SECONDS = 0.5f
private const val RETURN_MILLIS = 500L
private const val PAUSE_MILLIS = 700L
private const val IDLE_MILLIS = 10_000L
private const val TAP_MOVE = 14f
