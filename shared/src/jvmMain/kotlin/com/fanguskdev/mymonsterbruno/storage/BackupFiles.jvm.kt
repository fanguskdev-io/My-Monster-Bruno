package com.fanguskdev.mymonsterbruno.storage

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import java.awt.FileDialog
import java.awt.Frame
import java.io.File

@Composable
internal actual fun rememberBackupFiles(): BackupFiles = remember { DesktopBackupFiles() }

/** The files on the desktop: the system's window to save or open a file. */
private class DesktopBackupFiles : BackupFiles {
    override suspend fun save(name: String, text: String): Boolean {
        val chosen = choose("Guardar una copia", FileDialog.SAVE, name) ?: return false
        return runCatching { chosen.writeText(text) }.isSuccess
    }

    override suspend fun open(): String? {
        val chosen = choose("Recuperar una copia", FileDialog.LOAD, null) ?: return null
        return runCatching { chosen.readText() }.getOrNull()
    }

    /** Shows the window, which waits until the grown-up chooses; the file, or null if they cancelled. */
    private fun choose(title: String, mode: Int, name: String?): File? {
        val dialog = FileDialog(null as Frame?, title, mode)
        name?.let { dialog.file = it }
        dialog.isVisible = true
        val folder = dialog.directory ?: return null
        val file = dialog.file ?: return null
        return File(folder, file)
    }
}
