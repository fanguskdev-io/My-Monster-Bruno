package com.fanguskdev.mymonsterbruno.storage

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.autoreleasepool
import kotlinx.cinterop.readBytes
import kotlinx.cinterop.useContents
import kotlinx.coroutines.CompletableDeferred
import platform.CoreGraphics.CGContextSetInterpolationQuality
import platform.CoreGraphics.CGRectMake
import platform.CoreGraphics.CGSizeMake
import platform.CoreGraphics.kCGInterpolationHigh
import platform.Foundation.NSData
import platform.PhotosUI.PHPickerConfiguration
import platform.PhotosUI.PHPickerFilter
import platform.PhotosUI.PHPickerResult
import platform.PhotosUI.PHPickerViewController
import platform.PhotosUI.PHPickerViewControllerDelegateProtocol
import platform.UIKit.UIAdaptivePresentationControllerDelegateProtocol
import platform.UIKit.UIApplication
import platform.UIKit.UIColor
import platform.UIKit.UIGraphicsImageRenderer
import platform.UIKit.UIGraphicsImageRendererFormat
import platform.UIKit.UIGraphicsImageRendererFormatRangeStandard
import platform.UIKit.UIImage
import platform.UIKit.UIPresentationController
import platform.UIKit.UISceneActivationStateForegroundActive
import platform.UIKit.UIViewController
import platform.UIKit.UIWindowScene
import platform.UIKit.presentationController
import platform.darwin.NSObject

@Composable
internal actual fun rememberPhotoPicker(): PhotoPicker {
    val picker = remember { IosPhotoPicker() }
    DisposableEffect(picker) { onDispose(picker::release) }
    return picker
}

/**
 * The gallery on iOS: Apple's PHPickerViewController, which runs outside the game and needs no permission: only the photo
 * chosen reaches it. Shown over the game and called on the main thread, where it also answers; the photo is read and cut
 * on the thread where iOS gives it.
 */
private class IosPhotoPicker : PhotoPicker {
    /** The picker on screen: a second [pick] while it is open waits for the same photo. */
    private var open: PickerDelegate? = null

    override suspend fun pick(): ByteArray? = (open ?: show())?.photo?.await()

    /** The screen goes: the picker, if it is still open, goes with it. */
    fun release() {
        val current = open ?: return
        open = null
        current.controller.presentingViewController?.dismissViewControllerAnimated(true, completion = null)
        current.photo.complete(null)
    }

    /** Shows the picker; null if there is nowhere to show it right now, as while the one before is still closing. */
    private fun show(): PickerDelegate? {
        val top = topViewController()?.takeUnless { it.beingPresented || it.beingDismissed } ?: return null
        val configuration = PHPickerConfiguration().apply {
            filter = PHPickerFilter.imagesFilter
            selectionLimit = 1
        }
        val controller = PHPickerViewController(configuration)
        val delegate = PickerDelegate(controller) { if (open === it) open = null }
        controller.delegate = delegate
        controller.presentationController?.delegate = delegate
        open = delegate
        top.presentViewController(controller, animated = true, completion = null)
        return delegate
    }
}

/**
 * The picker on screen and its delegate, which UIKit keeps only weakly: [IosPhotoPicker] keeps it while the picker is open,
 * and [closed] tells it when it closes. Called on the main thread.
 */
@OptIn(BetaInteropApi::class)
private class PickerDelegate(val controller: PHPickerViewController, private val closed: (PickerDelegate) -> Unit) :
    NSObject(),
    PHPickerViewControllerDelegateProtocol,
    UIAdaptivePresentationControllerDelegateProtocol {
    /** The photo chosen, ready for its frame; null if none was chosen or it could not be read. */
    val photo = CompletableDeferred<ByteArray?>()

    /** A photo chosen, or Cancel: the picker does not close by itself. */
    override fun picker(picker: PHPickerViewController, didFinishPicking: List<*>) {
        picker.dismissViewControllerAnimated(true, completion = null)
        closed(this)
        val item = (didFinishPicking.firstOrNull() as? PHPickerResult)?.itemProvider
        if (item == null) {
            photo.complete(null)
            return
        }
        // iOS gives it on a thread of its own, where it is also cut.
        item.loadDataRepresentationForTypeIdentifier(IMAGE) { data, _ ->
            photo.complete(data?.let { runCatching { autoreleasepool { centred(it) } }.getOrNull() })
        }
    }

    /** Swiped down, where it shows as a sheet, as on an iPad: the same as Cancel. */
    override fun presentationControllerDidDismiss(presentationController: UIPresentationController) {
        closed(this)
        photo.complete(null)
    }
}

/** The view controller on top in the key window of the game's scene: the picker is shown over it. */
internal fun topViewController(): UIViewController? {
    val scenes = UIApplication.sharedApplication.connectedScenes.filterIsInstance<UIWindowScene>()
    val scene = scenes.firstOrNull { it.activationState == UISceneActivationStateForegroundActive } ?: scenes.firstOrNull()
    val root = scene?.keyWindow?.rootViewController ?: return null
    return generateSequence(root) { it.presentedViewController }.last()
}

/**
 * The image in [data], centred, cut square and [PHOTO_SIZE] pixels a side, as a JPEG; null if it cannot be read. Drawn
 * as the UIImage it is, upright: UIKit follows the orientation the camera wrote.
 */
@OptIn(ExperimentalForeignApi::class)
private fun centred(data: NSData): ByteArray? {
    val image = UIImage.imageWithData(data) ?: return null
    val (width, height) = image.size.useContents { width to height }
    if (width <= 0.0 || height <= 0.0) return null
    val side = PHOTO_SIZE.toDouble()
    val factor = side / minOf(width, height)
    val format = UIGraphicsImageRendererFormat().apply {
        // Exactly PHOTO_SIZE pixels a side, whatever the screen; and an ordinary JPEG, in sRGB and without HDR.
        scale = 1.0
        opaque = true
        preferredRange = UIGraphicsImageRendererFormatRangeStandard
    }
    val renderer = UIGraphicsImageRenderer(size = CGSizeMake(side, side), format = format)
    val jpeg = renderer.JPEGDataWithCompressionQuality(QUALITY) { context ->
        // White where it was transparent: a JPEG cannot be.
        UIColor.whiteColor.setFill()
        context?.fillRect(CGRectMake(0.0, 0.0, side, side))
        CGContextSetInterpolationQuality(context?.CGContext, kCGInterpolationHigh)
        image.drawInRect(CGRectMake((side - width * factor) / 2, (side - height * factor) / 2, width * factor, height * factor))
    }
    return jpeg.bytes?.readBytes(jpeg.length.toInt())
}

/** What the picker is asked for: the photo as it is kept, in any format of image iOS reads. */
private const val IMAGE = "public.image"

/** The JPEG's quality, of 1: the photo looks as it is and takes little to keep. */
private const val QUALITY = 0.85
