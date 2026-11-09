package com.fanguskdev.mymonsterbruno.storage

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.CreateDocument
import androidx.activity.result.contract.ActivityResultContracts.OpenDocument
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.CompletableDeferred

@Composable
internal actual fun rememberBackupFiles(): BackupFiles {
    val context = LocalContext.current
    val files = remember { AndroidBackupFiles(context.applicationContext) }
    files.saver = rememberLauncherForActivityResult(CreateDocument(JSON)) { files.saved(it) }
    files.opener = rememberLauncherForActivityResult(OpenDocument()) { files.opened(it) }
    DisposableEffect(files) { onDispose(files::release) }
    return files
}

/**
 * The files on Android: the system's own screens to create a document and to open one, which need no permission: the
 * game only gets the file the grown-up chose. They answer on the main thread, where the copy is written or read: it is
 * small.
 */
private class AndroidBackupFiles(private val context: Context) : BackupFiles {
    var saver: ActivityResultLauncher<String>? = null
    var opener: ActivityResultLauncher<Array<String>>? = null

    /** What is being kept, and its answer; and the answer of the file being opened. */
    private var saving: Pair<String, CompletableDeferred<Boolean>>? = null
    private var opening: CompletableDeferred<String?>? = null

    override suspend fun save(name: String, text: String): Boolean {
        saving?.second?.complete(false)
        val answer = CompletableDeferred<Boolean>()
        saving = text to answer
        val launched = saver?.let { runCatching { it.launch(name) }.isSuccess } ?: false
        if (!launched) saved(null)
        return answer.await()
    }

    override suspend fun open(): String? {
        opening?.complete(null)
        val answer = CompletableDeferred<String?>()
        opening = answer
        val launched = opener?.let { runCatching { it.launch(arrayOf(ANY)) }.isSuccess } ?: false
        if (!launched) opened(null)
        return answer.await()
    }

    /** Where the grown-up chose to keep it; null if they went back. */
    fun saved(uri: Uri?) {
        val (text, answer) = saving ?: return
        saving = null
        val kept = uri != null &&
            runCatching { context.contentResolver.openOutputStream(uri, "wt")?.use { it.write(text.encodeToByteArray()) } != null }
                .getOrDefault(false)
        answer.complete(kept)
    }

    /** The file the grown-up chose; null if they went back. */
    fun opened(uri: Uri?) {
        val answer = opening ?: return
        opening = null
        val text = uri?.let {
            runCatching { context.contentResolver.openInputStream(it)?.use { input -> input.readBytes().decodeToString() } }.getOrNull()
        }
        answer.complete(text)
    }

    fun release() {
        saver = null
        opener = null
        saving?.second?.complete(false)
        saving = null
        opening?.complete(null)
        opening = null
    }
}

/** What the copy is, and what can be chosen to recover one: anything, as some places do not know a JSON. */
private const val JSON = "application/json"
private const val ANY = "*/*"
