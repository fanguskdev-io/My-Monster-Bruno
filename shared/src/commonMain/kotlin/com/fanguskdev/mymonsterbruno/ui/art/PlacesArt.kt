package com.fanguskdev.mymonsterbruno.ui.art

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import com.fanguskdev.mymonsterbruno.ui.circle
import com.fanguskdev.mymonsterbruno.ui.ellipse
import com.fanguskdev.mymonsterbruno.ui.fill
import com.fanguskdev.mymonsterbruno.ui.line
import com.fanguskdev.mymonsterbruno.ui.path
import com.fanguskdev.mymonsterbruno.ui.ring

private val Wood = Color(0xFFC98A4B)
private val WoodDark = Color(0xFFA86E37)
private val WoodBack = Color(0xFF8E5A2B)
private val WoodLight = Color(0xFFDDA266)

/**
 * The table of «Dentro y fuera» (ADR 0028): its [top] board and its legs down to [floor], the back two set in and darker,
 * so that there is room under it.
 */
internal fun DrawScope.drawTable(top: Rect, floor: Float) {
    ellipse(Shadow, Offset(top.center.x, floor), top.width / 2f + 6f, 8f)
    for (x in listOf(top.left + 34f, top.right - 44f)) drawRect(WoodBack, Offset(x, top.bottom), Size(10f, floor - 14f - top.bottom))
    for (x in listOf(top.left + 12f, top.right - 24f)) drawRect(WoodDark, Offset(x, top.bottom), Size(12f, floor - top.bottom))
    drawRoundRect(Wood, top.topLeft, top.size, CornerRadius(6f))
    drawRoundRect(WoodLight, top.topLeft, Size(top.width, 5f), CornerRadius(3f))
    drawRect(WoodDark, Offset(top.left + 6f, top.bottom - 3f), Size(top.width - 12f, 3f))
}

private val Cardboard = Color(0xFFD9A066)
private val CardboardLight = Color(0xFFE6B77F)
private val CardboardDark = Color(0xFFB98149)
private val CardboardInside = Color(0xFF8F5F31)
private val Tape = Color(0xFFF1D7A6)

/**
 * The back of an open cardboard box whose [front] face is that rectangle: its shadow, the dark inside seen over the front
 * edge and the flaps of the back and the sides, open outwards. What goes inside is drawn after it, and then
 * [drawBoxFront], which hides its bottom.
 */
internal fun DrawScope.drawBoxBack(front: Rect) {
    val depth = front.height * 0.18f
    val flap = front.height * 0.34f
    ellipse(Shadow, Offset(front.center.x, front.bottom), front.width / 2f + 8f, 7f)
    // The flaps of the sides, falling outwards, and the one of the back, standing up.
    fill(
        path {
            moveTo(front.left, front.top)
            lineTo(front.left + depth, front.top - depth)
            lineTo(front.left - flap * 0.7f, front.top - depth - flap * 0.45f)
            lineTo(front.left - flap * 0.85f, front.top - flap * 0.2f)
            close()
        },
        CardboardDark,
    )
    fill(
        path {
            moveTo(front.right, front.top)
            lineTo(front.right - depth, front.top - depth)
            lineTo(front.right + flap * 0.7f, front.top - depth - flap * 0.45f)
            lineTo(front.right + flap * 0.85f, front.top - flap * 0.2f)
            close()
        },
        CardboardDark,
    )
    fill(
        path {
            moveTo(front.left + depth, front.top - depth)
            lineTo(front.left + depth - flap * 0.12f, front.top - depth - flap)
            lineTo(front.right - depth + flap * 0.12f, front.top - depth - flap)
            lineTo(front.right - depth, front.top - depth)
            close()
        },
        CardboardLight,
    )
    // The inside, dark, between the front edge and the back one.
    fill(
        path {
            moveTo(front.left, front.top)
            lineTo(front.left + depth, front.top - depth)
            lineTo(front.right - depth, front.top - depth)
            lineTo(front.right, front.top)
            close()
        },
        CardboardInside,
    )
}

/** The front of the box of [drawBoxBack]: its face, its flap folded down over it and a strip of tape. */
internal fun DrawScope.drawBoxFront(front: Rect) {
    val flap = front.height * 0.26f
    drawRect(Cardboard, front.topLeft, front.size)
    drawRect(CardboardDark, Offset(front.left, front.bottom - 6f), Size(front.width, 6f))
    val tape = front.width * 0.14f
    drawRect(Tape, Offset(front.center.x - tape / 2f, front.top + flap), Size(tape, front.height - flap - 6f))
    fill(
        path {
            moveTo(front.left - 2f, front.top)
            lineTo(front.right + 2f, front.top)
            lineTo(front.right - 3f, front.top + flap)
            lineTo(front.left + 3f, front.top + flap)
            close()
        },
        CardboardLight,
    )
    line(CardboardDark, Offset(front.left, front.top), Offset(front.right, front.top), 2.5f)
}

/**
 * Bruno's ball, a beach ball of [radius] centred on [center], its six colours turned [turn] degrees as it rolls; with its
 * [shadow] under it when it rests on something.
 */
internal fun DrawScope.drawBall(
    center: Offset,
    radius: Float,
    turn: Float = 0f,
    shadow: Boolean = true,
) {
    if (shadow) ellipse(Shadow, center + Offset(0f, radius * 0.95f), radius * 0.85f, radius * 0.2f)
    val colors = listOf(Color(0xFFE4483C), Color.White, Color(0xFF2C74D8), Color.White, Color(0xFFF4C423), Color.White)
    rotate(turn, center) {
        for ((i, color) in colors.withIndex()) {
            drawArc(color, i * 60f, 60f, useCenter = true, topLeft = center - Offset(radius, radius), size = Size(2 * radius, 2 * radius))
        }
    }
    circle(Color.White, center, radius * 0.2f)
    circle(Color.White, center + Offset(-radius * 0.4f, -radius * 0.42f), radius * 0.17f, alpha = 0.7f)
    ring(PictureInk.copy(alpha = 0.25f), center, radius, 1.5f)
}
