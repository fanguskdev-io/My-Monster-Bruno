package com.fanguskdev.mymonsterbruno.storage

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** The EXIF of a photo, read by hand for Android 8 (idea 5.6): which way the camera says it goes, and how to turn it. */
class ExifTest {
    /** A JPEG with no image, only its EXIF with [orientation], in the byte order of Intel (II) or Motorola (MM). */
    private fun jpeg(orientation: Int, little: Boolean): ByteArray {
        fun u16(v: Int) = if (little) listOf(v and 0xFF, v shr 8) else listOf(v shr 8, v and 0xFF)

        fun u32(v: Int) = if (little) u16(v and 0xFFFF) + u16(v ushr 16) else u16(v ushr 16) + u16(v and 0xFFFF)
        val tiff = (if (little) listOf(0x49, 0x49) else listOf(0x4D, 0x4D)) + u16(42) + u32(8) +
            u16(2) +
            u16(0x010F) + u16(2) + u32(4) + u32(0) +
            u16(0x0112) + u16(3) + u32(1) + u16(orientation) + u16(0) +
            u32(0)
        val app1 = "Exif".map { it.code } + listOf(0, 0) + tiff
        val bytes = listOf(0xFF, 0xD8, 0xFF, 0xE1, (app1.size + 2) shr 8, (app1.size + 2) and 0xFF) + app1 +
            listOf(0xFF, 0xDA, 0, 2, 0xFF, 0xD9)
        return ByteArray(bytes.size) { bytes[it].toByte() }
    }

    @Test
    fun theOrientationTheCameraWroteInEitherByteOrder() {
        for (orientation in 1..8) {
            assertEquals(orientation, exifOrientation(jpeg(orientation, little = true)), "II, $orientation")
            assertEquals(orientation, exifOrientation(jpeg(orientation, little = false)), "MM, $orientation")
        }
    }

    @Test
    fun withoutAnExifOrNotAJpegItGoesAsItIs() {
        val plain = byteArrayOf(0xFF.toByte(), 0xD8.toByte(), 0xFF.toByte(), 0xDA.toByte(), 0, 2)
        assertEquals(1, exifOrientation(plain))
        assertEquals(1, exifOrientation(byteArrayOf(1, 2, 3, 4, 5)))
        assertEquals(1, exifOrientation(ByteArray(0)))
        assertEquals(1, exifOrientation(jpeg(9, little = true)), "un valor que no existe")
        assertEquals(1, exifOrientation(jpeg(6, little = true).copyOf(30)), "cortado a medias")
    }

    @Test
    fun howToTurnEachOrientationUpright() {
        assertTrue(uprightFrom(1).asItIs)
        assertEquals(Upright(0, true), uprightFrom(2))
        assertEquals(Upright(180, false), uprightFrom(3))
        assertEquals(Upright(180, true), uprightFrom(4))
        assertEquals(Upright(90, true), uprightFrom(5))
        assertEquals(Upright(90, false), uprightFrom(6), "la foto del móvil en vertical")
        assertEquals(Upright(270, true), uprightFrom(7))
        assertEquals(Upright(270, false), uprightFrom(8))
        assertTrue(uprightFrom(0).asItIs)
    }
}
