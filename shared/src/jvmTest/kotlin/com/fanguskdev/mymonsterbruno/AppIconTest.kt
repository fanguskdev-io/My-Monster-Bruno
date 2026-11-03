package com.fanguskdev.mymonsterbruno

import androidx.compose.ui.graphics.asSkiaPath
import androidx.compose.ui.graphics.vector.PathParser
import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.Canvas
import org.jetbrains.skia.ColorAlphaType
import org.jetbrains.skia.EncodedImageFormat
import org.jetbrains.skia.Image
import org.jetbrains.skia.ImageInfo
import org.jetbrains.skia.Paint
import org.jetbrains.skia.PaintMode
import org.jetbrains.skia.PaintStrokeCap
import org.w3c.dom.Element
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * The iOS icon is the Android one: Bruno's face (androidApp/src/main/res/drawable/ic_launcher_foreground.xml) on the lilac
 * of the game (fondo_icono), with the 72 dp an adaptive icon shows filling the 1024 px iOS asks for, and no alpha. This
 * draws it in `shared/build/icono/` and checks that the icon of the Xcode project is that drawing: when the Android one
 * changes, the new one is there to copy.
 */
class AppIconTest {
    private val res = File("../androidApp/src/main/res")
    private val ios = File("../iosApp/iosApp/Assets.xcassets/AppIcon.appiconset/app-icon-1024.png")

    @Test
    fun theIosIconIsTheAndroidOne() {
        val drawn = pixels(draw())
        val out = File("build/icono/app-icon-1024.png").apply { parentFile.mkdirs() }
        out.writeBytes(encode(draw()))
        assertTrue(ios.exists(), "falta $ios: cópialo de $out")
        val committed = pixels(decode(ios.readBytes()))
        assertEquals(drawn.size, committed.size, "el icono de iOS no mide $SIZE × $SIZE: copia $out a $ios")
        val difference = drawn.indices.sumOf { abs(drawn[it] - committed[it]).toLong() }.toDouble() / drawn.size
        assertTrue(difference < MAX_DIFFERENCE, "el icono de iOS no es el de Android (diferencia $difference): copia $out a $ios")
    }

    private fun draw(): Bitmap {
        val bitmap = Bitmap().apply { allocPixels(ImageInfo.makeN32(SIZE, SIZE, ColorAlphaType.OPAQUE)) }
        val canvas = Canvas(bitmap)
        canvas.clear(color("fondo_icono"))
        canvas.scale(SIZE / VISIBLE, SIZE / VISIBLE)
        canvas.translate(-(VIEWPORT - VISIBLE) / 2, -(VIEWPORT - VISIBLE) / 2)
        val paths = xml("drawable/ic_launcher_foreground.xml").getElementsByTagName("path")
        for (i in 0 until paths.length) {
            val path = paths.item(i) as Element
            val shape = PathParser().parsePathString(path.getAttribute("android:pathData")).toPath().asSkiaPath()
            path.getAttribute("android:fillColor").takeIf { it.isNotEmpty() }?.let { fill ->
                canvas.drawPath(shape, Paint().apply { color = argb(fill) })
            }
            path.getAttribute("android:strokeColor").takeIf { it.isNotEmpty() }?.let { stroke ->
                val round = path.getAttribute("android:strokeLineCap") == "round"
                canvas.drawPath(
                    shape,
                    Paint().apply {
                        color = argb(stroke)
                        mode = PaintMode.STROKE
                        strokeWidth = path.getAttribute("android:strokeWidth").toFloat()
                        strokeCap = if (round) PaintStrokeCap.ROUND else PaintStrokeCap.BUTT
                    },
                )
            }
        }
        return bitmap
    }

    private fun xml(name: String) = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(File(res, name))

    private fun color(name: String): Int {
        val colors = xml("values/colors.xml").getElementsByTagName("color")
        for (i in 0 until colors.length) {
            val color = colors.item(i) as Element
            if (color.getAttribute("name") == name) return argb(color.textContent.trim())
        }
        error("no está el color $name en values/colors.xml")
    }

    /** An Android colour, #RRGGBB or #AARRGGBB, as an ARGB int. */
    private fun argb(hex: String): Int {
        val digits = hex.removePrefix("#")
        val value = digits.toLong(RADIX).toInt()
        return if (digits.length == RGB_DIGITS) value or OPAQUE else value
    }

    private fun encode(bitmap: Bitmap): ByteArray = requireNotNull(Image.makeFromBitmap(bitmap).encodeToData(EncodedImageFormat.PNG)).bytes

    private fun decode(png: ByteArray): Bitmap {
        val image = Image.makeFromEncoded(png)
        val bitmap = Bitmap().apply { allocPixels(ImageInfo.makeN32(image.width, image.height, ColorAlphaType.OPAQUE)) }
        image.readPixels(bitmap)
        return bitmap
    }

    private fun pixels(bitmap: Bitmap): IntArray {
        val info = ImageInfo.makeN32(bitmap.width, bitmap.height, ColorAlphaType.OPAQUE)
        val bytes = requireNotNull(bitmap.readPixels(info, bitmap.width * BYTES_PER_PIXEL, 0, 0))
        return IntArray(bytes.size) { bytes[it].toInt() and BYTE }
    }

    private companion object {
        const val SIZE = 1024
        const val VIEWPORT = 108f
        const val VISIBLE = 72f
        const val MAX_DIFFERENCE = 1.0
        const val RADIX = 16
        const val RGB_DIGITS = 6
        const val OPAQUE = -0x1000000
        const val BYTES_PER_PIXEL = 4
        const val BYTE = 0xFF
    }
}
