package com.fanguskdev.mymonsterbruno

import androidx.compose.ui.window.ComposeUIViewController
import com.fanguskdev.mymonsterbruno.storage.IosKeyValueStore
import platform.Foundation.NSProcessInfo
import platform.UIKit.UIViewController
import kotlin.experimental.ExperimentalNativeApi

/**
 * The app for iOS: hosts the Compose app. Its name is the one the Swift side of the template calls
 * (`MainViewControllerKt.MainViewController()`), hence the capital letter.
 */
@OptIn(ExperimentalNativeApi::class)
@Suppress("ktlint:standard:function-naming")
fun MainViewController(): UIViewController {
    val store = IosKeyValueStore()
    val debug = Platform.isDebugBinary
    // `xcrun simctl launch <simulador> com.fanguskdev.mymonsterbruno.MyMonsterBruno -silencio` turns the voice and the
    // sounds off, to try it on the simulator without them coming out of the Mac's speakers.
    val sound = !(debug && NSProcessInfo.processInfo.arguments.contains("-silencio"))
    return ComposeUIViewController { App(store, sound = sound) }
}
