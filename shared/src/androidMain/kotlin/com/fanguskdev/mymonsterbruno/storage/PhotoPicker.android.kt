package com.fanguskdev.mymonsterbruno.storage

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ImageDecoder
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.InputStream

@Composable
internal actual fun rememberPhotoPicker(): PhotoPicker {
    val context = LocalContext.current
    val picker = remember { AndroidPhotoPicker(context.applicationContext) }
    // A new contract each time would register the launcher again at every recomposition.
    val contract = remember { PickVisualMedia() }
    val launcher = rememberLauncherForActivityResult(contract, picker::chosen)
    DisposableEffect(picker) {
        picker.launcher = launcher
        onDispose(picker::release)
    }
    return picker
}

/**
 * The gallery on Android: the system's photo picker, which needs no permission and gives the game only the photo chosen
 * (where Android has none, its own file picker of images, which needs none either). Called on the main thread, where
 * Android also answers; the photo is read and cut on a thread of the IO pool.
 */
private class AndroidPhotoPicker(private val context: Context) : PhotoPicker {
    /** Opens the system's picker: there is one while the screen that remembers this picker is on. */
    var launcher: ActivityResultLauncher<PickVisualMediaRequest>? = null

    /** The choice the picker has not given yet: a second [pick] while it is open waits for the same one. */
    private var choice: CompletableDeferred<Uri?>? = null

    override suspend fun pick(): ByteArray? {
        val uri = (choice ?: open()).await() ?: return null
        return withContext(Dispatchers.IO) { runCatching { read(uri) }.getOrNull() }
    }

    private fun open(): CompletableDeferred<Uri?> {
        val next = CompletableDeferred<Uri?>()
        choice = next
        val request = PickVisualMediaRequest(PickVisualMedia.ImageOnly)
        val opened = launcher?.let { runCatching { it.launch(request) }.isSuccess } ?: false
        if (!opened) chosen(null)
        return next
    }

    /** What the grown-up chose in the picker; null if nothing. */
    fun chosen(uri: Uri?) {
        choice?.complete(uri)
        choice = null
    }

    fun release() {
        launcher = null
        chosen(null)
    }

    /** The photo at [uri], centred, cut square and [PHOTO_SIZE] pixels a side, as a JPEG; null if it cannot be read. */
    private fun read(uri: Uri): ByteArray? {
        val photo = (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) decode(uri) else decodeSampled(uri)) ?: return null
        val square = centred(photo)
        photo.recycle()
        val jpeg = ByteArrayOutputStream()
        val written = square.compress(Bitmap.CompressFormat.JPEG, QUALITY, jpeg)
        square.recycle()
        return if (written) jpeg.toByteArray() else null
    }

    /**
     * The photo upright, as ImageDecoder follows the rotation the camera wrote in its EXIF, and already as small as its
     * short side allows, [PHOTO_SIZE]: a photo of 12 MP is never held whole.
     */
    @RequiresApi(Build.VERSION_CODES.P)
    private fun decode(uri: Uri): Bitmap {
        val source = ImageDecoder.createSource(context.contentResolver, uri)
        return ImageDecoder.decodeBitmap(source) { decoder, info, _ ->
            val width = info.size.width
            val height = info.size.height
            val short = minOf(width, height)
            decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
            decoder.setTargetSize(scaled(width, short), scaled(height, short))
        }
    }

    /**
     * Before Android 9: the photo a power of two smaller, as long as its short side keeps [PHOTO_SIZE]. BitmapFactory does
     * not follow the EXIF, so it is read by hand and the photo turned upright here: a photo the camera only marked as
     * turned would come out turned (idea 5.6).
     */
    private fun decodeSampled(uri: Uri): Bitmap? {
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, bounds) }
        val short = minOf(bounds.outWidth, bounds.outHeight)
        var sample = 1
        while (short / (sample * 2) >= PHOTO_SIZE) sample *= 2
        val options = BitmapFactory.Options().apply { inSampleSize = sample }
        val photo = context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, options) } ?: return null
        val start = context.contentResolver.openInputStream(uri)?.use { readStart(it) } ?: return photo
        val upright = uprightFrom(exifOrientation(start))
        if (upright.asItIs) return photo
        val turn = Matrix().apply {
            setRotate(upright.degrees.toFloat())
            if (upright.mirrored) postScale(-1f, 1f)
        }
        val turned = Bitmap.createBitmap(photo, 0, 0, photo.width, photo.height, turn, true)
        if (turned !== photo) photo.recycle()
        return turned
    }

    /** The first [EXIF_BYTES] of [input], or all of it if it is shorter: where the EXIF of a photo is. */
    private fun readStart(input: InputStream): ByteArray {
        val buffer = ByteArray(EXIF_BYTES)
        var filled = 0
        while (filled < buffer.size) {
            val read = input.read(buffer, filled, buffer.size - filled)
            if (read <= 0) break
            filled += read
        }
        return buffer.copyOf(filled)
    }

    /** The square in the middle of [photo], [PHOTO_SIZE] pixels a side; white where it was transparent, as a JPEG cannot be. */
    private fun centred(photo: Bitmap): Bitmap {
        val short = minOf(photo.width, photo.height)
        val left = (photo.width - short) / 2
        val top = (photo.height - short) / 2
        val square = Bitmap.createBitmap(PHOTO_SIZE, PHOTO_SIZE, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(square)
        canvas.drawColor(Color.WHITE)
        val middle = Rect(left, top, left + short, top + short)
        canvas.drawBitmap(photo, middle, Rect(0, 0, PHOTO_SIZE, PHOTO_SIZE), Paint(Paint.FILTER_BITMAP_FLAG))
        return square
    }

    private companion object {
        /** The JPEG's quality, of 100: the photo looks as it is and takes little to keep. */
        const val QUALITY = 85

        /** How long [side] becomes when the short side, [short], becomes [PHOTO_SIZE]. */
        fun scaled(side: Int, short: Int) = ((side.toLong() * PHOTO_SIZE + short / 2) / short).toInt()
    }
}
