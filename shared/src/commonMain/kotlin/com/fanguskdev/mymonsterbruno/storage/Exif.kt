package com.fanguskdev.mymonsterbruno.storage

/**
 * How to turn a photo upright: [degrees] clockwise, and then mirrored left to right if [mirrored]. Android 9 and newer
 * do it by themselves when they read a photo; Android 8 does not, and a photo the camera only marked as turned would
 * come out turned (idea 5.6).
 */
internal data class Upright(val degrees: Int, val mirrored: Boolean) {
    val asItIs: Boolean get() = degrees == 0 && !mirrored
}

/** How to turn upright a photo with the EXIF [orientation] its camera wrote, from 1 to 8; anything else, as it is. */
internal fun uprightFrom(orientation: Int): Upright = when (orientation) {
    MIRRORED -> Upright(0, true)
    UPSIDE_DOWN -> Upright(HALF_TURN, false)
    UPSIDE_DOWN_MIRRORED -> Upright(HALF_TURN, true)
    QUARTER_MIRRORED -> Upright(QUARTER_TURN, true)
    QUARTER -> Upright(QUARTER_TURN, false)
    THREE_QUARTERS_MIRRORED -> Upright(THREE_QUARTERS_TURN, true)
    THREE_QUARTERS -> Upright(THREE_QUARTERS_TURN, false)
    else -> Upright(0, false)
}

/**
 * The orientation the camera wrote in the EXIF of [jpeg], from 1 to 8, or 1 if it wrote none. The EXIF goes at the
 * start of the file, before the image: the first [EXIF_BYTES] are enough.
 */
internal fun exifOrientation(jpeg: ByteArray): Int {
    if (jpeg.size < 4 || jpeg.u8(0) != MARK || jpeg.u8(1) != START_OF_IMAGE) return NORMAL
    var at = 2
    while (at + 4 <= jpeg.size && jpeg.u8(at) == MARK) {
        val marker = jpeg.u8(at + 1)
        val length = (jpeg.u8(at + 2) shl BYTE) or jpeg.u8(at + 3)
        if (marker == START_OF_SCAN || marker == END_OF_IMAGE) return NORMAL
        val data = at + 4
        if (marker == APP1 && data + 6 <= jpeg.size && jpeg.decodeToString(data, data + 4) == "Exif") {
            return orientationIn(jpeg, data + 6, minOf(jpeg.size, at + 2 + length))
        }
        at += 2 + length
    }
    return NORMAL
}

/** The orientation in the TIFF block of an EXIF, from [tiff] to [end]: the tag 0x0112 of its first directory. */
private fun orientationIn(bytes: ByteArray, tiff: Int, end: Int): Int {
    if (tiff + 8 > end) return NORMAL
    val little = bytes.u8(tiff) == 'I'.code && bytes.u8(tiff + 1) == 'I'.code

    fun u16(i: Int) = if (little) bytes.u8(i) or (bytes.u8(i + 1) shl BYTE) else (bytes.u8(i) shl BYTE) or bytes.u8(i + 1)

    fun u32(i: Int) = if (little) u16(i) or (u16(i + 2) shl 16) else (u16(i) shl 16) or u16(i + 2)

    val directory = tiff + u32(tiff + 4)
    if (directory < tiff || directory + 2 > end) return NORMAL
    for (k in 0 until u16(directory)) {
        val entry = directory + 2 + ENTRY * k
        if (entry + ENTRY > end) return NORMAL
        if (u16(entry) == ORIENTATION_TAG) return u16(entry + 8).takeIf { it in NORMAL..THREE_QUARTERS } ?: NORMAL
    }
    return NORMAL
}

private fun ByteArray.u8(i: Int) = this[i].toInt() and 0xFF

/** How much of the start of a photo is read to find its EXIF: at most 64 KB, and the start of the file. */
internal const val EXIF_BYTES = 128 * 1024

private const val MARK = 0xFF
private const val START_OF_IMAGE = 0xD8
private const val START_OF_SCAN = 0xDA
private const val END_OF_IMAGE = 0xD9
private const val APP1 = 0xE1
private const val ORIENTATION_TAG = 0x0112
private const val ENTRY = 12
private const val BYTE = 8

private const val NORMAL = 1
private const val MIRRORED = 2
private const val UPSIDE_DOWN = 3
private const val UPSIDE_DOWN_MIRRORED = 4
private const val QUARTER_MIRRORED = 5
private const val QUARTER = 6
private const val THREE_QUARTERS_MIRRORED = 7
private const val THREE_QUARTERS = 8
private const val QUARTER_TURN = 90
private const val HALF_TURN = 180
private const val THREE_QUARTERS_TURN = 270
