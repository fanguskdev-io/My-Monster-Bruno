package com.fanguskdev.mymonsterbruno.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.pointerInput
import kotlin.math.min

/** The width of the stage every screen is drawn on (ADR 0003): 800 by 400 units, the shape of a phone on its side. */
internal const val STAGE_WIDTH = 800f
internal const val STAGE_HEIGHT = 400f

/**
 * The stage on a screen of [width] by [height] pixels: as big as fits, centred. A phone, longer than the stage, shows
 * some more of it at the sides, and a tablet, squarer, some more above and below: the backgrounds reach [left], [top],
 * [right] and [bottom], and nothing she has to touch is out of 800 by 400.
 */
internal class Stage(width: Float, height: Float) {
    val scale = min(width / STAGE_WIDTH, height / STAGE_HEIGHT)
    private val dx = (width - STAGE_WIDTH * scale) / 2f
    private val dy = (height - STAGE_HEIGHT * scale) / 2f
    val left = -dx / scale
    val top = -dy / scale
    val right = STAGE_WIDTH + dx / scale
    val bottom = STAGE_HEIGHT + dy / scale

    /** A point of the screen, in pixels, on the stage. */
    fun toStage(point: Offset) = Offset((point.x - dx) / scale, (point.y - dy) / scale)

    fun transform(scope: DrawScope, draw: DrawScope.() -> Unit) = scope.withTransform({
        translate(dx, dy)
        scale(scale, scale, Offset.Zero)
    }) { draw() }
}

/**
 * What a screen does with a finger, in stage units. [down] says whether the finger is taken (then it gets the rest); it
 * gets the [Stage] too, for what goes in the corners of the screen.
 */
internal interface Touch {
    fun down(point: Offset, stage: Stage): Boolean

    fun move(point: Offset) {}

    fun up(point: Offset) {}

    fun cancel() {}
}

/** A whole screen drawn on the stage, and the touches on it. */
@Composable
internal fun StageCanvas(touch: Touch?, draw: DrawScope.(Stage) -> Unit) {
    val input = if (touch == null) {
        Modifier
    } else {
        Modifier.pointerInput(touch) {
            awaitEachGesture {
                val stage = Stage(size.width.toFloat(), size.height.toFloat())
                val first = awaitFirstDown(requireUnconsumed = false)
                if (!touch.down(stage.toStage(first.position), stage)) return@awaitEachGesture
                first.consume()
                var finished = false
                try {
                    while (true) {
                        val event = awaitPointerEvent()
                        val change = event.changes.firstOrNull { it.id == first.id } ?: break
                        change.consume()
                        if (!change.pressed) {
                            finished = true
                            touch.up(stage.toStage(change.position))
                            break
                        }
                        touch.move(stage.toStage(change.position))
                    }
                } finally {
                    if (!finished) touch.cancel()
                }
            }
        }
    }
    Canvas(Modifier.fillMaxSize().then(input)) {
        val stage = Stage(size.width, size.height)
        stage.transform(this) { draw(stage) }
    }
}

/** The seconds since the screen appeared, a frame at a time: what the idle animations are drawn with. */
@Composable
internal fun rememberClock(): State<Float> = produceState(0f) {
    val start = withFrameNanos { it }
    while (true) {
        withFrameNanos { value = (it - start) / NANOS_IN_SECOND }
    }
}

private const val NANOS_IN_SECOND = 1_000_000_000f
