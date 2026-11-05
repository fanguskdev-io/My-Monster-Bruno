package com.fanguskdev.mymonsterbruno.ui.screens

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.fanguskdev.mymonsterbruno.ui.Stage
import com.fanguskdev.mymonsterbruno.ui.art.drawLock
import com.fanguskdev.mymonsterbruno.ui.art.drawRoundButton
import com.fanguskdev.mymonsterbruno.ui.art.homeIcon
import com.fanguskdev.mymonsterbruno.ui.art.speakerIcon
import com.fanguskdev.mymonsterbruno.ui.distance
import com.fanguskdev.mymonsterbruno.ui.easeInOut
import com.fanguskdev.mymonsterbruno.ui.lerp
import com.fanguskdev.mymonsterbruno.ui.ring
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
