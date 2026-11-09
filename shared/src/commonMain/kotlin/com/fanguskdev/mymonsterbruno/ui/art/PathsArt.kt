package com.fanguskdev.mymonsterbruno.ui.art

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import com.fanguskdev.mymonsterbruno.ui.Stage
import com.fanguskdev.mymonsterbruno.ui.circle
import com.fanguskdev.mymonsterbruno.ui.fill
import com.fanguskdev.mymonsterbruno.ui.line
import com.fanguskdev.mymonsterbruno.ui.path
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

private val Grass = Color(0xFF9BD67A)
private val GrassDark = Color(0xFF7FC260)
private val SandEdge = Color(0xFFD4B07A)
private val Sand = Color(0xFFF0D9A8)
private val Gold = Color(0xFFF4B623)

/** Tufts of grass and flowers of the field, where they always are: not on the paths' starts and ends. */
private val TUFTS = listOf(
    Offset(60f, 90f), Offset(250f, 60f), Offset(560f, 70f), Offset(720f, 120f), Offset(90f, 330f),
    Offset(300f, 370f), Offset(520f, 380f), Offset(735f, 330f), Offset(40f, 210f), Offset(760f, 220f),
)
private val FLOWERS = listOf(
    Offset(110f, 150f) to Color.White,
    Offset(690f, 80f) to Color(0xFFFFD54A),
    Offset(230f, 340f) to Color(0xFFF06E98),
    Offset(620f, 360f) to Color.White,
    Offset(70f, 270f) to Color(0xFFFFD54A),
    Offset(740f, 280f) to Color(0xFFF06E98),
)

/** The field Bruno's paths cross, seen from above: grass with its tufts and a few flowers that sway a little. */
internal fun DrawScope.drawField(stage: Stage, time: Float) {
    drawRect(Grass, Offset(stage.left, stage.top), Size(stage.right - stage.left, stage.bottom - stage.top))
    for (tuft in TUFTS) {
        for (dx in listOf(-6f, 0f, 6f)) line(GrassDark, tuft + Offset(dx, 6f), tuft + Offset(dx * 1.6f, -6f), 3f)
    }
    for ((i, flower) in FLOWERS.withIndex()) {
        val (at, color) = flower
        val sway = Offset(1.5f * sin(time * 1.3f + i), 0f)
        for (k in 0 until 5) {
            val angle = (k * 2 * PI / 5).toFloat()
            circle(color, at + sway + Offset(6f * cos(angle), 6f * sin(angle)), 4.5f)
        }
        circle(Color(0xFFF4B623), at + sway, 3.5f)
    }
}

/**
 * A sandy path along [points], with its edge and, where she has still to go, a white dashed line down the middle to
 * follow; the part she has already done ([done] points from the start) is gold.
 */
internal fun DrawScope.drawSandPath(points: List<Offset>, done: Int, time: Float) {
    if (points.size < 2) return
    fun along(from: Int, to: Int) = path {
        moveTo(points[from].x, points[from].y)
        for (i in from + 1..to) lineTo(points[i].x, points[i].y)
    }
    val whole = along(0, points.lastIndex)
    drawPath(whole, SandEdge, style = Stroke(40f, cap = StrokeCap.Round, join = StrokeJoin.Round))
    drawPath(whole, Sand, style = Stroke(32f, cap = StrokeCap.Round, join = StrokeJoin.Round))
    if (done < points.lastIndex) {
        drawPath(
            along(done, points.lastIndex),
            Color.White,
            alpha = 0.85f,
            style = Stroke(4f, cap = StrokeCap.Round, pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 12f), -time * 18f)),
        )
    }
    if (done > 0) drawPath(along(0, done), Gold, style = Stroke(12f, cap = StrokeCap.Round, join = StrokeJoin.Round))
}

/** Bruno's house seen from the front, [size] across and centred on [center]: walls, a red roof, a door and a window. */
internal fun DrawScope.drawHomeIcon(center: Offset, size: Float) {
    val k = size / 80f
    val left = center.x - 30f * k
    val top = center.y - 6f * k
    drawRect(Shadow, Offset(left + 3f * k, top + 4f * k), Size(60f * k, 40f * k))
    drawRect(Color(0xFFFFE3A8), Offset(left, top), Size(60f * k, 40f * k))
    fill(
        path {
            moveTo(center.x - 38f * k, top + 2f * k)
            lineTo(center.x, center.y - 40f * k)
            lineTo(center.x + 38f * k, top + 2f * k)
            close()
        },
        Color(0xFFE4483C),
    )
    drawRect(Color(0xFF9C6A38), Offset(center.x - 8f * k, top + 16f * k), Size(16f * k, 24f * k))
    drawRect(Color(0xFFA6DCF6), Offset(left + 6f * k, top + 8f * k), Size(12f * k, 12f * k))
    drawRect(Color(0xFFA6DCF6), Offset(left + 42f * k, top + 8f * k), Size(12f * k, 12f * k))
}
