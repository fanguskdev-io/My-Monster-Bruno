package com.fanguskdev.mymonsterbruno.storage

import androidx.compose.runtime.Composable
import kotlin.io.encoding.Base64

/**
 * Her photo (ADR 0016): a grown-up chooses it from the device's own gallery, in the grown-ups' corner, and it hangs in a
 * frame in Bruno's room. It is kept small and square, only on the device.
 */
interface PhotoPicker {
    /**
     * Opens the gallery of the device, without asking for the photos: only the one chosen reaches the game. Gives it
     * centred and cut square, [PHOTO_SIZE] pixels a side, as a JPEG; null if none was chosen or it could not be read.
     */
    suspend fun pick(): ByteArray?
}

/** The gallery of this platform. */
@Composable
internal expect fun rememberPhotoPicker(): PhotoPicker

/** A gallery with nothing in it: the tests and the screenshots. */
class NoPhotoPicker : PhotoPicker {
    override suspend fun pick(): ByteArray? = null
}

/** How many pixels a side the photo keeps: enough for its frame on any screen, and little to keep. */
const val PHOTO_SIZE = 360

/**
 * Keeps her photo in the [KeyValueStore], as text in pieces: so it goes where the game already keeps everything, and
 * the pieces fit the desktop's preferences, which take no more than 8 192 characters a value.
 */
internal object PhotoStore {
    private const val PIECES = "foto.partes"
    private const val PIECE_LENGTH = 4000

    fun load(store: KeyValueStore): ByteArray? {
        val count = store.getString(PIECES)?.toIntOrNull() ?: return null
        val text = StringBuilder()
        for (i in 0 until count) text.append(store.getString(piece(i)) ?: return null)
        return runCatching { Base64.decode(text.toString()) }.getOrNull()
    }

    fun save(store: KeyValueStore, photo: ByteArray) {
        remove(store)
        val pieces = Base64.encode(photo).chunked(PIECE_LENGTH)
        pieces.forEachIndexed { i, piece -> store.putString(piece(i), piece) }
        store.putString(PIECES, pieces.size.toString())
    }

    fun remove(store: KeyValueStore) {
        val count = store.getString(PIECES)?.toIntOrNull() ?: 0
        store.remove(PIECES)
        for (i in 0 until count) store.remove(piece(i))
    }

    private fun piece(i: Int) = "foto.$i"
}
