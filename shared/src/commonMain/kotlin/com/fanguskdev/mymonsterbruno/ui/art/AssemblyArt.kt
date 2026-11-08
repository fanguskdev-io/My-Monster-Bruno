package com.fanguskdev.mymonsterbruno.ui.art

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipRect
import com.fanguskdev.mymonsterbruno.game.Weather
import com.fanguskdev.mymonsterbruno.ui.circle
import com.fanguskdev.mymonsterbruno.ui.line
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

private val Sun = Color(0xFFFFD54A)
private val CloudWhite = Color(0xFFF4F6FA)
private val CloudGrey = Color(0xFFB9C6D6)
private val CloudDark = Color(0xFF8C99AA)
private val Rain = Color(0xFF4A9BE8)

/**
 * What she sees through the window of Bruno's room, through its [glass], once she has said in the assembly what weather
 * it is (ADR 0030): the sun in a blue sky, clouds in a grey one, rain that falls or snow that falls slowly and stays on
 * the sill. [time] moves the clouds, the rain and the snow.
 */
internal fun DrawScope.drawWindowWeather(weather: Weather, glass: Rect, time: Float) =
    clipRect(glass.left, glass.top, glass.right, glass.bottom) {
        val sky = when (weather) {
            Weather.SUN -> Color(0xFFA6DCF6)
            Weather.CLOUDS -> Color(0xFFC9D6E3)
            Weather.RAIN -> Color(0xFF9FB0C2)
            Weather.SNOW -> Color(0xFFD8E4EE)
        }
        drawRect(sky, glass.topLeft, glass.size)
        val drift = 10f * sin(time * 0.3f)
        when (weather) {
            Weather.SUN -> {
                sunWithRays(Offset(glass.right - 40f, glass.top + 34f), 18f, time)
                cloud(Offset(glass.left + 36f + drift, glass.top + 70f), 0.8f, CloudWhite)
            }
            Weather.CLOUDS -> {
                cloud(Offset(glass.left + 42f + drift, glass.top + 30f), 1.1f, CloudWhite)
                cloud(Offset(glass.right - 44f - drift, glass.top + 62f), 1f, CloudGrey)
            }
            Weather.RAIN -> {
                cloud(Offset(glass.center.x + drift, glass.top + 22f), 1.4f, CloudDark)
                for (i in 0 until 12) {
                    val x = glass.left + 8f + 12.5f * i
                    val y = glass.top + 34f + (time * 140f + i * 41f) % (glass.height - 30f)
                    line(Rain, Offset(x, y), Offset(x - 3f, y + 9f), 2.5f)
                }
            }
            Weather.SNOW -> {
                for (i in 0 until 14) {
                    val x = glass.left + 8f + 10.5f * i + 4f * sin(time * 1.5f + i)
                    val y = glass.top + (time * 22f + i * 29f) % glass.height
                    circle(Color.White, Offset(x, y), 2.6f)
                }
                drawRect(Color.White, Offset(glass.left, glass.bottom - 8f), Size(glass.width, 8f))
            }
        }
    }

/**
 * The weather she chooses in the assembly, as a picture of [size] across centred on [center]: a sun, clouds, a cloud with
 * rain or a cloud with snowflakes.
 */
internal fun DrawScope.drawWeatherIcon(
    weather: Weather,
    center: Offset,
    size: Float,
    time: Float,
) {
    val k = size / 80f
    when (weather) {
        Weather.SUN -> sunWithRays(center, 18f * k, time)
        Weather.CLOUDS -> {
            cloud(center + Offset(10f * k, -8f * k), 0.9f * k, CloudGrey)
            cloud(center + Offset(-8f * k, 6f * k), 1.05f * k, CloudWhite, outline = true)
        }
        Weather.RAIN -> {
            cloud(center + Offset(0f, -10f * k), 1.1f * k, CloudDark)
            for (i in -1..1) {
                val fall = (time * 1.4f + i * 0.33f) % 1f
                val drop = center + Offset(14f * k * i, (14f + 16f * fall) * k)
                circle(Rain, drop, 3.6f * k)
            }
        }
        Weather.SNOW -> {
            cloud(center + Offset(0f, -10f * k), 1.1f * k, CloudGrey)
            for (i in -1..1) flake(center + Offset(15f * k * i, (20f + 4f * sin(time * 2f + i)) * k), 6f * k)
        }
    }
}

private fun DrawScope.sunWithRays(center: Offset, radius: Float, time: Float) {
    for (i in 0 until 8) {
        val angle = i * PI / 4 + time * 0.4
        val dir = Offset(cos(angle).toFloat(), sin(angle).toFloat())
        line(Sun, center + dir * (radius * 1.3f), center + dir * (radius * 1.75f), radius * 0.22f)
    }
    circle(Sun, center, radius)
}

/** A round cloud of three puffs on a flat base, [scale] times its usual 30 units across. */
private fun DrawScope.cloud(
    center: Offset,
    scale: Float,
    color: Color,
    outline: Boolean = false,
) {
    val puffs = listOf(Offset(-12f, 2f) to 10f, Offset(0f, -4f) to 13f, Offset(13f, 2f) to 9f, Offset(0f, 6f) to 10f)
    if (outline) for ((at, r) in puffs) circle(CloudGrey, center + at * scale, (r + 1.6f) * scale)
    for ((at, r) in puffs) circle(color, center + at * scale, r * scale)
}

/** A snowflake: three crossed lines with a dot in the middle. */
private fun DrawScope.flake(center: Offset, radius: Float) {
    for (i in 0 until 3) {
        val angle = i * PI / 3 + PI / 2
        val dir = Offset(cos(angle).toFloat(), sin(angle).toFloat()) * radius
        line(Color(0xFF7FA8D8), center - dir, center + dir, radius * 0.32f)
    }
}
