package com.fanguskdev.mymonsterbruno.storage

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.awt.Color
import java.awt.FileDialog
import java.awt.Frame
import java.awt.KeyboardFocusManager
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.File
import javax.imageio.IIOImage
import javax.imageio.ImageIO
import javax.imageio.ImageWriteParam
import javax.imageio.stream.MemoryCacheImageOutputStream

@Composable
internal actual fun rememberPhotoPicker(): PhotoPicker = remember { DesktopPhotoPicker() }

/**
 * The gallery on the desktop, a tool to try the game: the system's dialog to open a JPEG or a PNG. It is modal, so the
 * game waits behind it; it is opened, and the photo read and cut, on a thread of the IO pool.
 */
private class DesktopPhotoPicker : PhotoPicker {
    override suspend fun pick(): ByteArray? = withContext(Dispatchers.IO) { runCatching { choose()?.let(::centred) }.getOrNull() }

    /** The file the grown-up chose in the dialog; null if none. */
    private fun choose(): File? {
        val owner = KeyboardFocusManager.getCurrentKeyboardFocusManager().activeWindow as? Frame
        val dialog = FileDialog(owner, TITLE, FileDialog.LOAD)
        dialog.setFilenameFilter { _, name -> name.substringAfterLast('.').lowercase() in EXTENSIONS }
        try {
            dialog.isVisible = true
            return dialog.files.firstOrNull()
        } finally {
            dialog.dispose()
        }
    }

    /** The image in [file], centred, cut square and [PHOTO_SIZE] pixels a side, as a JPEG; null if ImageIO cannot read it. */
    private fun centred(file: File): ByteArray? {
        val image = ImageIO.read(file) ?: return null
        val short = minOf(image.width, image.height)
        val left = (image.width - short) / 2
        val top = (image.height - short) / 2
        val square = BufferedImage(PHOTO_SIZE, PHOTO_SIZE, BufferedImage.TYPE_INT_RGB)
        val graphics = square.createGraphics()
        try {
            graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR)
            graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)
            // White where it was transparent: a JPEG cannot be.
            graphics.drawImage(image, 0, 0, PHOTO_SIZE, PHOTO_SIZE, left, top, left + short, top + short, Color.WHITE, null)
        } finally {
            graphics.dispose()
        }
        return jpeg(square)
    }

    /** [image] as a JPEG of [QUALITY], written in memory: nothing goes to a temporary file. */
    private fun jpeg(image: BufferedImage): ByteArray? {
        val writer = ImageIO.getImageWritersByFormatName("jpeg").asSequence().firstOrNull() ?: return null
        val bytes = ByteArrayOutputStream()
        try {
            MemoryCacheImageOutputStream(bytes).use { stream ->
                writer.output = stream
                val parameters = writer.defaultWriteParam.apply {
                    compressionMode = ImageWriteParam.MODE_EXPLICIT
                    compressionQuality = QUALITY
                }
                writer.write(null, IIOImage(image, null, null), parameters)
            }
        } finally {
            writer.dispose()
        }
        return bytes.toByteArray()
    }

    private companion object {
        const val TITLE = "Elegir su foto"
        val EXTENSIONS = setOf("jpg", "jpeg", "png")

        /** The JPEG's quality, of 1: the photo looks as it is and takes little to keep. */
        const val QUALITY = 0.85f
    }
}
