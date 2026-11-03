package com.fanguskdev.mymonsterbruno.ui.art

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.fanguskdev.mymonsterbruno.ui.Stage
import com.fanguskdev.mymonsterbruno.ui.circle
import com.fanguskdev.mymonsterbruno.ui.ellipse
import com.fanguskdev.mymonsterbruno.ui.fill
import com.fanguskdev.mymonsterbruno.ui.path
import com.fanguskdev.mymonsterbruno.ui.sparkle
import com.fanguskdev.mymonsterbruno.ui.stroke
import kotlin.math.sin

/**
 * The backgrounds of the screens. Each one covers all the screen ([Stage.left] to [Stage.right], [Stage.top] to
 * [Stage.bottom]), wider or taller than the stage on some screens, with the floor at the same height of the stage.
 */

private fun DrawScope.band(
    stage: Stage,
    color: Color,
    from: Float,
    to: Float,
) = drawRect(color, Offset(stage.left, from), Size(stage.right - stage.left, to - from))

private fun DrawScope.gradient(stage: Stage, top: Color, bottom: Color) = drawRect(
    Brush.verticalGradient(listOf(top, bottom), startY = stage.top, endY = stage.bottom),
    Offset(stage.left, stage.top),
    Size(stage.right - stage.left, stage.bottom - stage.top),
)

/** The night sky of the hatching, with its stars twinkling. */
internal fun DrawScope.drawNightSky(stage: Stage, time: Float) {
    gradient(stage, Color(0xFF7470DE), Color(0xFFABA6F3))
    val stars =
        listOf(
            Offset(150f, 60f),
            Offset(256f, 116f),
            Offset(565f, 66f),
            Offset(616f, 156f),
            Offset(205f, 226f),
            Offset(582f, 256f),
            Offset(60f, 330f),
            Offset(745f, 300f),
        )
    stars.forEachIndexed { i, p -> fill(sparkle(p, 6f + 3f * sin(time * 2f + i)), Color.White, alpha = 0.85f) }
}

/** Bruno's room by day: a warm wall, a window with the sky, the wooden floor and a rug. */
internal fun DrawScope.drawRoom(stage: Stage, time: Float) {
    band(stage, Color(0xFFFFE9D6), stage.top, 296f)
    band(stage, Color(0xFFF9D5B4), 262f, 296f)
    band(stage, Color(0xFFE6B47C), 296f, stage.bottom)
    for (y in listOf(330f, 366f)) drawLine(Color(0xFFD49B5E), Offset(stage.left, y), Offset(stage.right, y), 2f)
    // The window.
    drawRoundRect(Color.White, Offset(96f, 40f), Size(170f, 130f), CornerRadius(10f))
    drawRoundRect(Color(0xFFA6DCF6), Offset(106f, 50f), Size(150f, 110f), CornerRadius(6f))
    circle(Color(0xFFFFD54A), Offset(214f, 84f), 18f)
    val cloud = 140f + 10f * sin(time * 0.3f)
    for ((dx, r) in listOf(0f to 14f, 16f to 18f, 34f to 12f)) circle(Color.White, Offset(cloud + dx, 112f), r)
    drawRect(Color.White, Offset(179f, 50f), Size(5f, 110f))
    drawRect(Color.White, Offset(106f, 103f), Size(150f, 5f))
    ellipse(Color(0xFF4FA89A), Offset(400f, 352f), 250f, 34f)
    ellipse(Color(0xFF6CC2B2), Offset(400f, 352f), 222f, 25f)
}

/** Bruno's room at night: the same room, dark, with the moon in the window and his bed. */
internal fun DrawScope.drawNightRoom(stage: Stage, time: Float) {
    band(stage, Color(0xFF3B3F86), stage.top, 296f)
    band(stage, Color(0xFF353978), 262f, 296f)
    band(stage, Color(0xFF6B5A7E), 296f, stage.bottom)
    drawRoundRect(Color(0xFF6B70B8), Offset(96f, 40f), Size(170f, 130f), CornerRadius(10f))
    drawRoundRect(Color(0xFF1E2350), Offset(106f, 50f), Size(150f, 110f), CornerRadius(6f))
    circle(Color(0xFFFFF3C4), Offset(210f, 88f), 20f)
    circle(Color(0xFF1E2350), Offset(220f, 82f), 18f)
    for ((i, p) in listOf(Offset(130f, 70f), Offset(150f, 130f), Offset(240f, 140f)).withIndex()) {
        fill(sparkle(p, 5f + 2f * sin(time * 2f + i)), Color.White, alpha = 0.9f)
    }
    drawRect(Color(0xFF6B70B8), Offset(179f, 50f), Size(5f, 110f))
    drawRect(Color(0xFF6B70B8), Offset(106f, 103f), Size(150f, 5f))
}

/** The bed, with its pillow, drawn behind Bruno; [drawBlanket] goes over him. */
internal fun DrawScope.drawBed(center: Offset) {
    drawRoundRect(Color(0xFF8A5A3C), Offset(center.x - 170f, center.y - 90f), Size(26f, 150f), CornerRadius(8f))
    drawRoundRect(Color(0xFF8A5A3C), Offset(center.x + 144f, center.y - 40f), Size(26f, 100f), CornerRadius(8f))
    drawRoundRect(Color.White, Offset(center.x - 140f, center.y - 50f), Size(110f, 46f), CornerRadius(20f))
}

internal fun DrawScope.drawBlanket(center: Offset) {
    val blanket = path {
        moveTo(center.x - 150f, center.y + 2f)
        quadraticTo(center.x, center.y - 26f, center.x + 150f, center.y + 2f)
        lineTo(center.x + 150f, center.y + 60f)
        lineTo(center.x - 150f, center.y + 60f)
        close()
    }
    fill(blanket, Color(0xFF9B7BEA))
    for (x in listOf(-100f, -40f, 20f, 80f)) circle(Color(0xFFCDBDF6), Offset(center.x + x, center.y + 30f), 9f)
}

/** The kitchen where Bruno eats: a lilac wall with dots and a table. */
internal fun DrawScope.drawKitchen(stage: Stage) {
    band(stage, Color(0xFFEFE9FF), stage.top, 300f)
    var y = stage.top - stage.top % 60f + 30f
    while (y < 300f) {
        var x = stage.left - stage.left % 60f + 30f
        while (x < stage.right) {
            circle(Color(0xFFE2D8FF), Offset(x, y), 6f)
            x += 60f
        }
        y += 60f
    }
    band(stage, Color(0xFFCDBDF6), 300f, stage.bottom)
    drawRoundRect(Color(0xFFC98A4B), Offset(430f, 296f), Size(stage.right - 420f, 18f), CornerRadius(6f))
    drawRect(Color(0xFFA86E37), Offset(456f, 314f), Size(14f, stage.bottom - 314f))
    drawRect(Color(0xFFA86E37), Offset(740f, 314f), Size(14f, stage.bottom - 314f))
}

/** Bruno's thought bubble, with its little circles going to his head at [from]. */
internal fun DrawScope.drawThoughtBubble(
    center: Offset,
    rx: Float,
    ry: Float,
    from: Offset,
) {
    circle(Color.White, lerpPoint(from, center, 0.15f), 7f)
    circle(Color.White, lerpPoint(from, center, 0.32f), 11f)
    ellipse(Shadow, center + Offset(0f, 5f), rx, ry)
    ellipse(Color.White, center, rx, ry)
}

private fun lerpPoint(a: Offset, b: Offset, t: Float) = Offset(a.x + (b.x - a.x) * t, a.y + (b.y - a.y) * t)

/** The wall of the tracing: mint green, with the floor and the big sheet of paper on the easel. */
internal fun DrawScope.drawTracingWall(stage: Stage, sheet: Offset, sheetSize: Size) {
    band(stage, Color(0xFFDCF1E8), stage.top, 330f)
    band(stage, Color(0xFFE6B47C), 330f, stage.bottom)
    drawLine(Color(0xFFD49B5E), Offset(stage.left, 360f), Offset(stage.right, 360f), 2f)
    drawRect(Color(0xFFA86E37), Offset(sheet.x + 30f, sheet.y + sheetSize.height - 10f), Size(12f, 400f - sheet.y - sheetSize.height + 20f))
    drawRect(
        Color(0xFFA86E37),
        Offset(sheet.x + sheetSize.width - 42f, sheet.y + sheetSize.height - 10f),
        Size(
            12f,
            400f - sheet.y - sheetSize.height + 20f,
        ),
    )
    drawRoundRect(Shadow, sheet + Offset(0f, 6f), sheetSize, CornerRadius(14f))
    drawRoundRect(Color.White, sheet, sheetSize, CornerRadius(14f))
}

/** The garden of the strawberries: sky, sun, clouds and grass. */
internal fun DrawScope.drawGarden(stage: Stage, time: Float) {
    gradient(stage, Color(0xFFBFE6F5), Color(0xFFE6F6FC))
    circle(Color(0xFFFFE27A), Offset(694f, 64f), 28f)
    val drift = 8f * sin(time * 0.25f)
    for ((x, y) in listOf(250f to 70f, 520f to 52f)) {
        for ((dx, r) in listOf(0f to 16f, 20f to 22f, 42f to 15f)) circle(Color.White, Offset(x + dx + drift, y), r)
    }
    band(stage, Color(0xFF93D46E), 300f, stage.bottom)
    for (x in listOf(40f, 180f, 420f, 640f, 760f)) {
        stroke(
            path {
                moveTo(x, 300f)
                quadraticTo(x - 4f, 288f, x - 8f, 282f)
            },
            Color(0xFF6FB24F),
            3f,
        )
        stroke(
            path {
                moveTo(x + 6f, 300f)
                quadraticTo(x + 8f, 290f, x + 14f, 284f)
            },
            Color(0xFF6FB24F),
            3f,
        )
    }
}

/** The calm corner: soft green, with the circles of breathing. */
internal fun DrawScope.drawCalm(stage: Stage) {
    gradient(stage, Color(0xFFE3F1E0), Color(0xFFCFE8C9))
    band(stage, Color(0xFFB9DDB1), 330f, stage.bottom)
}

/** The sea of the islands, lilac like Bruno's sky, with its waves. */
internal fun DrawScope.drawSea(stage: Stage, time: Float) {
    gradient(stage, Color(0xFF7C88EF), Color(0xFF5967D8))
    val waves = listOf(Offset(60f, 80f), Offset(420f, 46f), Offset(660f, 330f), Offset(200f, 350f), Offset(740f, 160f), Offset(40f, 250f))
    waves.forEachIndexed { i, p ->
        val x = p.x + 6f * sin(time * 0.8f + i)
        stroke(
            path {
                moveTo(x, p.y)
                quadraticTo(x + 10f, p.y - 8f, x + 20f, p.y)
                quadraticTo(x + 30f, p.y + 8f, x + 40f, p.y)
            },
            Color.White,
            3f,
            alpha = 0.3f,
        )
    }
}
