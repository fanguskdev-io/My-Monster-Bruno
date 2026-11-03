package com.fanguskdev.mymonsterbruno.ui

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.sp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.pow
import kotlin.math.sin

/** Small helpers to draw on the stage with the shapes of the mockups (ellipses, paths and strokes with round ends). */

internal fun lerp(a: Float, b: Float, t: Float) = a + (b - a) * t

internal fun lerp(a: Offset, b: Offset, t: Float) = Offset(lerp(a.x, b.x, t), lerp(a.y, b.y, t))

internal fun clamp01(t: Float) = t.coerceIn(0f, 1f)

internal fun easeOut(t: Float) = 1f - (1f - clamp01(t)).pow(3)

internal fun easeInOut(t: Float): Float {
    val x = clamp01(t)
    return if (x < 0.5f) 4f * x * x * x else 1f - (-2f * x + 2f).pow(3) / 2f
}

/** Goes up past the end and settles: the pop of something that appears. */
internal fun easeBack(t: Float): Float {
    val x = clamp01(t) - 1f
    return 1f + 2.7f * x * x * x + 1.7f * x * x
}

internal fun distance(a: Offset, b: Offset) = hypot(a.x - b.x, a.y - b.y)

internal fun DrawScope.ellipse(
    color: Color,
    center: Offset,
    rx: Float,
    ry: Float,
    alpha: Float = 1f,
) = drawOval(color, Offset(center.x - rx, center.y - ry), Size(2 * rx, 2 * ry), alpha = alpha)

internal fun DrawScope.tiltedEllipse(
    color: Color,
    center: Offset,
    rx: Float,
    ry: Float,
    degrees: Float,
) = rotate(degrees, center) { drawOval(color, Offset(center.x - rx, center.y - ry), Size(2 * rx, 2 * ry)) }

internal fun DrawScope.circle(
    color: Color,
    center: Offset,
    radius: Float,
    alpha: Float = 1f,
) = drawCircle(color, radius, center, alpha = alpha)

internal fun DrawScope.ring(
    color: Color,
    center: Offset,
    radius: Float,
    width: Float,
    alpha: Float = 1f,
) = drawCircle(color, radius, center, alpha = alpha, style = Stroke(width))

internal fun DrawScope.line(
    color: Color,
    from: Offset,
    to: Offset,
    width: Float,
    alpha: Float = 1f,
) = drawLine(color, from, to, width, StrokeCap.Round, alpha = alpha)

internal fun DrawScope.fill(path: Path, color: Color, alpha: Float = 1f) = drawPath(path, color, alpha)

internal fun DrawScope.stroke(
    path: Path,
    color: Color,
    width: Float,
    alpha: Float = 1f,
) = drawPath(path, color, alpha, Stroke(width, cap = StrokeCap.Round, join = StrokeJoin.Round))

internal fun path(build: Path.() -> Unit): Path = Path().apply(build)

/** A star of [points] points, [outer] wide at its tips and [inner] at its waist. */
internal fun star(
    center: Offset,
    outer: Float,
    inner: Float = outer * 0.43f,
    points: Int = 5,
    turn: Float = 0f,
): Path = path {
    for (i in 0 until points * 2) {
        val radius = if (i % 2 == 0) outer else inner
        val angle = -PI / 2 + PI * i / points + turn
        val x = center.x + radius * cos(angle).toFloat()
        val y = center.y + radius * sin(angle).toFloat()
        if (i == 0) moveTo(x, y) else lineTo(x, y)
    }
    close()
}

/** A four-point sparkle, the one of the mockups. */
internal fun sparkle(center: Offset, size: Float): Path = star(center, size, size * 0.24f, points = 4)

/**
 * Writes [text] centred on [center] at [height] units high, in [family]: measured with a density of one, so its units
 * are the stage's.
 */
internal fun DrawScope.label(
    measurer: TextMeasurer,
    text: String,
    center: Offset,
    height: Float,
    color: Color,
    family: FontFamily,
    weight: FontWeight = FontWeight.Bold,
) {
    val layout = measurer.measure(
        text,
        style = TextStyle(color = color, fontSize = height.sp, fontFamily = family, fontWeight = weight),
        density = UNIT_DENSITY,
    )
    drawText(layout, topLeft = Offset(center.x - layout.size.width / 2f, center.y - layout.size.height / 2f))
}

private val UNIT_DENSITY = Density(1f, 1f)
