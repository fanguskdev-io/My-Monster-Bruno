package com.fanguskdev.mymonsterbruno.storage

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.CompletableDeferred
import platform.Foundation.NSString
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.NSURL
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.create
import platform.Foundation.stringWithContentsOfURL
import platform.Foundation.writeToURL
import platform.UIKit.UIAdaptivePresentationControllerDelegateProtocol
import platform.UIKit.UIDocumentPickerDelegateProtocol
import platform.UIKit.UIDocumentPickerViewController
import platform.UIKit.UIPresentationController
import platform.UIKit.presentationController
import platform.UniformTypeIdentifiers.UTTypeData
import platform.UniformTypeIdentifiers.UTTypeJSON
import platform.darwin.NSObject

@Composable
internal actual fun rememberBackupFiles(): BackupFiles {
    val files = remember { IosBackupFiles() }
    DisposableEffect(files) { onDispose(files::release) }
    return files
}

/**
 * The files on iOS: Apple's document picker, over the game, to keep the copy in Files or to choose one there. It needs
 * no permission: only the file chosen reaches the game. Called on the main thread, where it also answers.
 */
private class IosBackupFiles : BackupFiles {
    /** The picker on screen, if any. */
    private var open: FilesDelegate? = null

    @OptIn(BetaInteropApi::class, ExperimentalForeignApi::class)
    override suspend fun save(name: String, text: String): Boolean {
        val url = NSURL.fileURLWithPath(NSTemporaryDirectory() + name)
        val written = NSString.create(string = text).writeToURL(url, atomically = true, encoding = NSUTF8StringEncoding, error = null)
        if (!written) return false
        val picker = UIDocumentPickerViewController(forExportingURLs = listOf(url), asCopy = true)
        return !show(picker).isNullOrEmpty()
    }

    @OptIn(ExperimentalForeignApi::class)
    override suspend fun open(): String? {
        val picker = UIDocumentPickerViewController(forOpeningContentTypes = listOf(UTTypeJSON, UTTypeData), asCopy = true)
        val url = show(picker)?.firstOrNull() as? NSURL ?: return null
        return NSString.stringWithContentsOfURL(url, encoding = NSUTF8StringEncoding, error = null)
    }

    /** Shows [picker] and waits: the files chosen, or null if the grown-up cancelled or it could not be shown. */
    private suspend fun show(picker: UIDocumentPickerViewController): List<*>? {
        release()
        val top = topViewController()?.takeUnless { it.beingPresented || it.beingDismissed } ?: return null
        val delegate = FilesDelegate()
        picker.delegate = delegate
        picker.presentationController?.delegate = delegate
        open = delegate
        top.presentViewController(picker, animated = true, completion = null)
        return delegate.chosen.await().also { if (open === delegate) open = null }
    }

    fun release() {
        open?.chosen?.complete(null)
        open = null
    }
}

/** The delegate of the picker, which UIKit keeps only weakly: [IosBackupFiles] keeps it while the picker is open. */
private class FilesDelegate :
    NSObject(),
    UIDocumentPickerDelegateProtocol,
    UIAdaptivePresentationControllerDelegateProtocol {
    val chosen = CompletableDeferred<List<*>?>()

    override fun documentPicker(controller: UIDocumentPickerViewController, didPickDocumentsAtURLs: List<*>) {
        chosen.complete(didPickDocumentsAtURLs)
    }

    override fun documentPickerWasCancelled(controller: UIDocumentPickerViewController) {
        chosen.complete(null)
    }

    /** Swiped down, as a sheet: the same as Cancel. */
    override fun presentationControllerDidDismiss(presentationController: UIPresentationController) {
        chosen.complete(null)
    }
}
