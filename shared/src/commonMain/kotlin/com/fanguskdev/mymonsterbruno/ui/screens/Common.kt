package com.fanguskdev.mymonsterbruno.ui.screens

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import com.fanguskdev.mymonsterbruno.game.CHOICES
import com.fanguskdev.mymonsterbruno.ui.Stage
import com.fanguskdev.mymonsterbruno.ui.art.Shadow
import com.fanguskdev.mymonsterbruno.ui.art.drawLock
import com.fanguskdev.mymonsterbruno.ui.art.drawRoundButton
import com.fanguskdev.mymonsterbruno.ui.art.homeIcon
import com.fanguskdev.mymonsterbruno.ui.art.speakerIcon
import com.fanguskdev.mymonsterbruno.ui.distance
import com.fanguskdev.mymonsterbruno.ui.easeInOut
import com.fanguskdev.mymonsterbruno.ui.lerp
import com.fanguskdev.mymonsterbruno.ui.ring
import kotlin.math.abs
import kotlin.math.sin

/**
 * What the screens share: the two small buttons of the top corners (back home, and say it again) and the padlock of the
 * grown-ups, held for a while to open. They are small on purpose: they are not for playing.
 */
internal fun homeButton(stage: Stage) = Offset(stage.left + 44f, stage.top + 42f)

internal fun speakerButton(stage: Stage) = Offset(stage.right - 44f, stage.top + 42f)

internal fun lockButton(stage: Stage) = Offset(stage.right - 38f, stage.top + 38f)

internal const val CORNER_RADIUS = 30f
internal const val LOCK_RADIUS = 22f

/** A finger counts as on a button if it is this much beyond its edge, too: little fingers miss (NN/G, Soni 2019). */
internal const val TOUCH_SLACK = 16f

internal fun hits(point: Offset, center: Offset, radius: Float) = distance(point, center) <= radius + TOUCH_SLACK

internal fun DrawScope.drawHomeButton(stage: Stage) {
    val at = homeButton(stage)
    drawRoundButton(at, CORNER_RADIUS, Color.White) { homeIcon(at, 26f) }
}

internal fun DrawScope.drawSpeakerButton(stage: Stage) {
    val at = speakerButton(stage)
    drawRoundButton(at, CORNER_RADIUS, Color.White) { speakerIcon(at, 26f) }
}

/** How long a grown-up has to hold the padlock, in seconds. */
internal const val HOLD_SECONDS = 2.5f

/** The padlock, filling while it is held since [heldSince] (in the screen's clock), if it is. */
internal fun DrawScope.drawGrownUpsLock(stage: Stage, time: Float, heldSince: Float?) =
    drawLock(lockButton(stage), LOCK_RADIUS, if (heldSince == null) 0f else ((time - heldSince) / HOLD_SECONDS).coerceIn(0f, 1f))

/** A ring that grows and fades round [center], over and over: «touch here». */
internal fun DrawScope.drawCallRing(
    center: Offset,
    radius: Float,
    time: Float,
    color: Color = Color.White,
) {
    val phase = (time % 1.6f) / 1.6f
    ring(color, center, radius * (1f + 0.3f * phase), 5f, alpha = (1f - phase) * 0.9f)
}

/** A gentle up and down, for things that wait to be touched. */
internal fun bob(time: Float, speed: Float = 2f, amount: Float = 4f) = sin(time * speed) * amount

/** Something that flies from [from] to [to] between [start] and [start] + [length] seconds of the screen's clock. */
internal class Flight(
    val from: Offset,
    val to: Offset,
    val start: Float,
    val length: Float,
) {
    fun at(time: Float): Offset = lerp(from, to, easeInOut((time - start) / length))

    fun over(time: Float) = time >= start + length
}

/** [count] places in a row, [step] apart and centred on [center]: where the things to choose from go (ADR 0020). */
internal fun row(center: Offset, count: Int, step: Float): List<Offset> =
    List(count) { center + Offset(step * (it - (count - 1) / 2f), 0f) }

/**
 * Where the picture cards of the island of words go, its words and its rhymes, as many as she chooses from (ADR 0020):
 * four go closer, and a little smaller.
 */
internal fun cardSpots(count: Int): List<Offset> =
    if (count > CHOICES) row(Offset(534f, 330f), count, 132f) else row(Offset(570f, 330f), count, 150f)

internal fun cardSize(count: Int) = if (count > CHOICES) Size(116f, 100f) else Size(124f, 104f)

/** How big the picture is on a card of [count]. */
internal fun cardPicture(count: Int) = if (count > CHOICES) 78f else 84f

internal fun inCard(point: Offset, center: Offset, size: Size) =
    abs(point.x - center.x) <= size.width / 2 + TOUCH_SLACK && abs(point.y - center.y) <= size.height / 2 + TOUCH_SLACK

/** A white card of [size] with its [picture]: faded if she chose it and it was not; with an [edge] if it was ([right]). */
internal fun DrawScope.drawPictureCard(
    center: Offset,
    size: Size,
    aside: Boolean,
    right: Boolean,
    edge: Color,
    picture: DrawScope.() -> Unit,
) {
    val topLeft = center - Offset(size.width / 2, size.height / 2)
    drawRoundRect(Shadow, topLeft + Offset(0f, 6f), size, CornerRadius(18f))
    drawRoundRect(Color.White, topLeft, size, CornerRadius(18f))
    picture()
    if (aside) drawRoundRect(Color.White, topLeft, size, CornerRadius(18f), alpha = 0.6f)
    if (right) drawRoundRect(edge, topLeft, size, CornerRadius(18f), style = Stroke(6f))
}
