package com.fanguskdev.mymonsterbruno.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import com.fanguskdev.mymonsterbruno.game.Miss
import com.fanguskdev.mymonsterbruno.game.SUM_MAX
import com.fanguskdev.mymonsterbruno.game.Sum
import com.fanguskdev.mymonsterbruno.game.plusOne
import com.fanguskdev.mymonsterbruno.game.sumRounds
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
import com.fanguskdev.mymonsterbruno.ui.art.drawBush
import com.fanguskdev.mymonsterbruno.ui.art.drawGarden
import com.fanguskdev.mymonsterbruno.ui.art.drawPlate
import com.fanguskdev.mymonsterbruno.ui.art.drawSparkles
import com.fanguskdev.mymonsterbruno.ui.art.drawStrawberry
import com.fanguskdev.mymonsterbruno.ui.circle
import com.fanguskdev.mymonsterbruno.ui.clamp01
import com.fanguskdev.mymonsterbruno.ui.easeBack
import com.fanguskdev.mymonsterbruno.ui.easeInOut
import com.fanguskdev.mymonsterbruno.ui.label
import com.fanguskdev.mymonsterbruno.ui.lerp
import com.fanguskdev.mymonsterbruno.ui.rememberClock
import com.fanguskdev.mymonsterbruno.ui.ring
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.sin

/** Bruno, his plate and the bush, where they are in the strawberries of the same island. */
private val BRUNO_CENTER = Offset(172f, 226f)
private const val BRUNO_HEIGHT = 220f
private val PLATE = Offset(330f, 340f)
private val BUSH = Offset(610f, 262f)

/** The strawberries of the bush, as in the strawberries of the same island. */
private val BERRIES = listOf(
    Offset(528f, 200f),
    Offset(588f, 180f),
    Offset(648f, 180f),
    Offset(706f, 200f),
    Offset(512f, 254f),
    Offset(572f, 238f),
    Offset(632f, 238f),
    Offset(692f, 254f),
    Offset(538f, 304f),
    Offset(598f, 292f),
    Offset(658f, 292f),
    Offset(716f, 306f),
)

/** Where the strawberries are on his plate: the ones he has first, then the ones she gives him. */
private val ON_PLATE = listOf(Offset(298f, 322f), Offset(330f, 312f), Offset(362f, 322f))
private const val BERRY_RADIUS = 26f
private const val BERRY_TOUCH = 30f

/** The card of the sum, at the top: what he has, plus what she gives, is what? */
private val SUM_CARD = Offset(400f, 64f)
private val SUM_CARD_SIZE = Size(330f, 74f)

/** The numbers she chooses from, one to three, in front of the bush: clear of the Dynamic Island on its right. */
private val CARDS = (1..SUM_MAX).map { Offset(426f + 100f * it, 236f) }
private val CARD = Size(86f, 108f)

private val GOLD = Color(0xFFF4B623)
private val INK = Color(0xFF1D2B53)
private val NUMBER_COLORS = listOf(Color(0xFFE4483C), Color(0xFF2C74D8), Color(0xFF2E9E5B))

private enum class SumPhase { SHOWING, GIVING, GIVEN, ASKING, WRONG, RIGHT, THANKS }

/**
 * «Sumas hasta 3», on the island of numbers by turns with the countdown and the number line (ADR 0029): Bruno has one or
 * two strawberries on his plate, «Bruno tiene una fresa», and the voice asks her to give him more, «Dale dos más»; she
 * gives them from the bush, counted as they go, and the voice asks «¿Cuántas fresas tiene ahora Bruno?». She touches the
 * number: if it is, «¡Sí! Una y dos, tres», and Bruno eats them; if not, the voice counts them all with her, each one
 * glowing on the plate, and asks again. The card at the top shows the sum with strawberries: one, and two, is what? The
 * three sums up to three, in any order.
 */
@Composable
internal fun SumsScreen(game: GameState, onDone: () -> Unit) {
    val clock = rememberClock()
    val scope = rememberCoroutineScope()
    val measurer = rememberTextMeasurer()
    val font = andika()
    val audio = game.audio
    val rounds = remember { sumRounds(game.random) }
    var round by remember { mutableStateOf(0) }
    var phase by remember { mutableStateOf(SumPhase.SHOWING) }
    var phaseAt by remember { mutableStateOf(0f) }

    // The strawberries he has that are already on the plate, and when each came; the ones she gave, from where and when;
    // the one being counted; the number she touched that was not, and when; and when he started eating them.
    var shown by remember { mutableStateOf(0) }
    val shownAt = remember { mutableStateListOf<Float>() }
    val given = remember { mutableStateListOf<Int>() }
    val givenAt = remember { mutableStateListOf<Float>() }
    var counting by remember { mutableStateOf<Int?>(null) }
    var wrong by remember { mutableStateOf<Pair<Int, Float>?>(null) }
    var hinted by remember { mutableStateOf(false) }
    var eatingAt by remember { mutableStateOf<Float?>(null) }

    fun current(): Sum = rounds[round.coerceAtMost(rounds.lastIndex)]

    LaunchedEffect(round) {
        val sum = current()
        phase = SumPhase.SHOWING
        phaseAt = clock.value
        shown = 0
        shownAt.clear()
        given.clear()
        givenAt.clear()
        counting = null
        wrong = null
        hinted = false
        eatingAt = null
        if (round == 0) audio.say(Voice.SUMS_INTRO)
        repeat(sum.has) {
            shown++
            shownAt.add(clock.value)
            audio.sfx(Sfx.POP)
            delay(SHOW_MILLIS)
        }
        audio.say(Voice.brunoHas(sum.has))
        phase = SumPhase.GIVING
        phaseAt = clock.value
        audio.say(Voice.giveMore(sum.more))
    }

    // The voice asks again when she waits a while.
    LaunchedEffect(round, phase) {
        if (phase != SumPhase.GIVING && phase != SumPhase.ASKING) return@LaunchedEffect
        while (true) {
            delay(IDLE_MILLIS)
            audio.say(if (phase == SumPhase.GIVING) Voice.giveMore(current().more) else Voice.HOW_MANY_NOW)
        }
    }

    fun give(berry: Int) {
        val sum = current()
        given.add(berry)
        givenAt.add(clock.value)
        audio.sfx(Sfx.PICK)
        val count = given.size
        if (count < sum.more) {
            audio.speak(Voice.number(count))
            return
        }
        phase = SumPhase.GIVEN
        scope.launch {
            audio.say(Voice.number(count))
            delay(FLY_MILLIS)
            phase = SumPhase.ASKING
            phaseAt = clock.value
            audio.say(Voice.HOW_MANY_NOW)
        }
    }

    fun choose(number: Int) {
        val sum = current()
        if (number == sum.total) {
            phase = SumPhase.RIGHT
            phaseAt = clock.value
            hinted = false
            audio.sfx(Sfx.RIGHT)
            game.update { it.copy(sums = it.sums.plusOne(sum.key)) }
            scope.launch {
                audio.say(Voice.sumIs(sum))
                eatingAt = clock.value
                audio.sfx(Sfx.MUNCH)
                audio.say(Voice.strawberries(sum.total))
                delay(PAUSE_MILLIS)
                if (round + 1 < rounds.size) {
                    round++
                } else {
                    phase = SumPhase.THANKS
                    phaseAt = clock.value
                    audio.sfx(Sfx.SPARKLE)
                    audio.say(Voice.THANKS_SUMS)
                    onDone()
                }
            }
        } else {
            phase = SumPhase.WRONG
            phaseAt = clock.value
            wrong = number to clock.value
            audio.sfx(Sfx.BOING)
            game.missed(Miss.sum(sum))
            scope.launch {
                audio.say(Voice.COUNT_THEM_ALL)
                for (n in 1..sum.total) {
                    counting = n
                    audio.say(Voice.number(n))
                }
                counting = null
                hinted = true
                phase = SumPhase.ASKING
                phaseAt = clock.value
            }
        }
    }

    val touch = remember {
        object : Touch {
            override fun down(point: Offset, stage: Stage): Boolean {
                if (hits(point, speakerButton(stage), CORNER_RADIUS)) {
                    when (phase) {
                        SumPhase.GIVING -> audio.speak(Voice.giveMore(current().more))
                        SumPhase.ASKING -> audio.speak(Voice.HOW_MANY_NOW)
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
                    SumPhase.GIVING -> {
                        val berry = BERRIES.indices.firstOrNull { it !in given && hits(point, BERRIES[it], BERRY_TOUCH) } ?: return false
                        give(berry)
                    }
                    SumPhase.ASKING -> {
                        val card = CARDS.indices.firstOrNull { inCard(point, CARDS[it], CARD) } ?: return false
                        audio.sfx(Sfx.TAP)
                        choose(card + 1)
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
        val sum = current()
        drawGarden(stage, t)
        val eating = eatingAt?.let { t >= it } ?: false
        val pose = when {
            phase == SumPhase.THANKS -> Pose(Mouth.GRIN, Eyes.HAPPY, Arms.UP, hop = 10f * sin(since * 5f).coerceAtLeast(0f))
            eating -> Pose(Mouth.MUNCH, Eyes.HAPPY, Arms.UP, hop = 6f * sin(t * 6f).coerceAtLeast(0f))
            phase == SumPhase.WRONG -> Pose(Mouth.OPEN, Eyes.OPEN, Arms.DOWN, lookX = 0.8f, lookY = 0.9f)
            else -> Pose(Mouth.OPEN, Eyes.OPEN, Arms.DOWN, lookX = 1f, lookY = 0.3f, hop = 1.5f * sin(t * 3f))
        }
        drawBruno(game.data.look, BRUNO_CENTER, BRUNO_HEIGHT, pose, t)
        drawPlate(PLATE, 190f)

        // The strawberries on the plate: the ones he had, popping in, and the ones she gave, flying from the bush; the
        // one being counted, in its ring; and, while he eats them, going one after another.
        fun gone(n: Int) = eatingAt?.let { (t - it) * 3f > n + 1 } ?: false
        for (n in 0 until shown) {
            if (gone(n)) continue
            val grow = easeBack(clamp01((t - shownAt[n]) / POP_SECONDS))
            drawStrawberry(ON_PLATE[n], BERRY_RADIUS * 0.8f * grow)
        }
        given.forEachIndexed { k, berry ->
            val n = sum.has + k
            if (gone(n)) return@forEachIndexed
            drawStrawberry(lerp(BERRIES[berry], ON_PLATE[n], easeInOut((t - givenAt[k]) / FLY_SECONDS)), BERRY_RADIUS * 0.8f)
        }
        counting?.let { ring(GOLD, ON_PLATE[it - 1], BERRY_RADIUS * 1.15f, 5f) }
        if (eating || phase == SumPhase.THANKS) drawSparkles(BRUNO_CENTER + Offset(0f, 20f), 120f, t)

        drawBush(BUSH, 320f)
        val choosing = phase == SumPhase.ASKING || phase == SumPhase.WRONG || phase == SumPhase.RIGHT
        if (!choosing) {
            BERRIES.forEachIndexed { i, home ->
                if (i in given) return@forEachIndexed
                drawStrawberry(home + Offset(0f, 2f * sin(t * 2f + i)), BERRY_RADIUS)
            }
        } else {
            for ((i, center) in CARDS.withIndex()) {
                val number = i + 1
                val shake = wrong?.takeIf { it.first == number }?.let { (_, at) ->
                    7f * sin((t - at) * 30f) * (1f - clamp01((t - at) / WIGGLE_SECONDS))
                } ?: 0f
                val lit = phase == SumPhase.RIGHT && number == sum.total
                drawNumberCard(center + Offset(shake, 0f), number, lit, measurer, font)
                if (hinted && phase == SumPhase.ASKING && number == sum.total) drawCallRing(center, CARD.height * 0.6f, t, GOLD)
            }
        }
        drawSumCard(sum, given.size, phase == SumPhase.RIGHT || eating, t, measurer, font)

        for (i in rounds.indices) {
            val done = i < round || (i == round && (phase == SumPhase.RIGHT || phase == SumPhase.THANKS))
            circle(if (done) Color(0xFFE9B36A) else Color(0x99FFFFFF), Offset(370f + 30f * i, 384f), 10f)
        }
        drawHomeButton(stage)
        drawSpeakerButton(stage)
    }
}

/** A card to choose with its [number] and as many dots under it, gold when it is the right one and she touched it. */
private fun DrawScope.drawNumberCard(
    center: Offset,
    number: Int,
    lit: Boolean,
    measurer: TextMeasurer,
    font: FontFamily,
) {
    val topLeft = center - Offset(CARD.width / 2, CARD.height / 2)
    val color = NUMBER_COLORS[(number - 1) % NUMBER_COLORS.size]
    drawRoundRect(Shadow, topLeft + Offset(0f, 6f), CARD, CornerRadius(16f))
    drawRoundRect(if (lit) Color(0xFFFFD54A) else Color.White, topLeft, CARD, CornerRadius(16f))
    drawRoundRect(if (lit) GOLD else color, topLeft, CARD, CornerRadius(16f), style = Stroke(if (lit) 6f else 3f))
    label(measurer, number.toString(), center + Offset(0f, -12f), 54f, if (lit) Color.White else color, font)
    for (d in 0 until number) circle(if (lit) Color.White else color, center + Offset(18f * (d - (number - 1) / 2f), 34f), 6f)
}

/**
 * The card of the sum: the strawberries he had, a plus, the ones she has to give him (empty until she gives each), an
 * equals and a question mark; once she has found it, the total in its place.
 */
private fun DrawScope.drawSumCard(
    sum: Sum,
    given: Int,
    found: Boolean,
    time: Float,
    measurer: TextMeasurer,
    font: FontFamily,
) {
    val topLeft = SUM_CARD - Offset(SUM_CARD_SIZE.width / 2, SUM_CARD_SIZE.height / 2)
    drawRoundRect(Shadow, topLeft + Offset(0f, 5f), SUM_CARD_SIZE, CornerRadius(20f))
    drawRoundRect(Color.White, topLeft, SUM_CARD_SIZE, CornerRadius(20f))
    // Everything in a row, centred: a strawberry is 36 units, the plus 34, the equals 38 and the answer 44.
    var x = SUM_CARD.x - (36f * sum.total + 116f) / 2f + 18f
    val y = SUM_CARD.y
    repeat(sum.has) {
        drawStrawberry(Offset(x, y), 15f)
        x += 36f
    }
    label(measurer, "+", Offset(x - 2f, y), 38f, INK, font)
    x += 34f
    repeat(sum.more) { k ->
        drawStrawberry(Offset(x, y), 15f)
        if (k >= given) faded(Offset(x, y))
        x += 36f
    }
    label(measurer, "=", Offset(x - 2f, y), 38f, INK, font)
    x += 38f
    val box = Offset(x, y)
    if (found) {
        label(measurer, sum.total.toString(), box, 44f, GOLD, font)
    } else {
        drawRoundRect(Color(0xFFFFF4D6), box - Offset(22f, 24f), Size(44f, 48f), CornerRadius(10f))
        label(measurer, "?", box + Offset(0f, 2f * sin(time * 4f)), 34f, INK, font)
    }
}

/** A white veil over a strawberry not given yet, so that it shows as a place to fill. */
private fun DrawScope.faded(center: Offset) = circle(Color.White, center + Offset(0f, 1f), 19f, alpha = 0.65f)

private const val SHOW_MILLIS = 450L
private const val POP_SECONDS = 0.35f
private const val FLY_SECONDS = 0.4f
private const val FLY_MILLIS = 400L
private const val WIGGLE_SECONDS = 0.5f
private const val PAUSE_MILLIS = 600L
private const val IDLE_MILLIS = 9000L
