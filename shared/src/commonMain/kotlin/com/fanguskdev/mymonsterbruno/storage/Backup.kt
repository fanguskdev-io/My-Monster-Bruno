package com.fanguskdev.mymonsterbruno.storage

import androidx.compose.runtime.Composable
import com.fanguskdev.mymonsterbruno.game.SaveData
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.io.encoding.Base64

/**
 * A copy of her game, to take it to another phone (ADR 0039): what she has done, her name, her Bruno and her photo, as
 * the text of a file a grown-up keeps wherever they choose. The game never sends it anywhere.
 */
internal object Backup {
    /** The name the file is offered with. */
    const val FILE_NAME = "partida-de-bruno.json"

    private const val GAME = "Mi amigo Bruno"
    private const val VERSION = 1

    /** What the file has inside: which game it is, its version, the save and the photo in Base64, empty without one. */
    @Serializable
    private class Copy(
        val juego: String,
        val version: Int,
        val partida: SaveData,
        val foto: String = "",
    )

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    /** The text of a copy of [data] and [photo]. */
    fun write(data: SaveData, photo: ByteArray?): String =
        json.encodeToString(Copy.serializer(), Copy(GAME, VERSION, data, photo?.let { Base64.encode(it) }.orEmpty()))

    /** The game and the photo in [text]; null if it is not a copy of this game or cannot be read. */
    fun read(text: String): Restored? {
        val copy = runCatching { json.decodeFromString(Copy.serializer(), text) }.getOrNull() ?: return null
        if (copy.juego != GAME) return null
        val photo = if (copy.foto.isEmpty()) null else runCatching { Base64.decode(copy.foto) }.getOrNull() ?: return null
        return Restored(copy.partida, photo)
    }
}

/** A copy read back: the game and her photo, if it had one. */
internal class Restored(val data: SaveData, val photo: ByteArray?)

/** Where a copy goes and where it comes from: the device's own place for files (ADR 0039). */
interface BackupFiles {
    /** Lets a grown-up keep [text] as a file called [name] wherever they choose; true if it was kept. */
    suspend fun save(name: String, text: String): Boolean

    /** Lets a grown-up choose a file; its text, or null if none was chosen or it cannot be read. */
    suspend fun open(): String?
}

/** The place for files of this platform. */
@Composable
internal expect fun rememberBackupFiles(): BackupFiles

/** Nowhere to keep anything: the tests. */
class NoBackupFiles : BackupFiles {
    override suspend fun save(name: String, text: String) = false

    override suspend fun open(): String? = null
}
