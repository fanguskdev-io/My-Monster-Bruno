package com.fanguskdev.mymonsterbruno

import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.fanguskdev.mymonsterbruno.storage.AndroidKeyValueStore

/** The app for Android: one activity, in landscape and immersive full screen, drawing behind the cutout too. */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }
        hideSystemBars()
        val store = AndroidKeyValueStore(applicationContext)
        // `adb shell am start -n com.fanguskdev.mymonsterbruno/.MainActivity --ez silencio true` turns the voice and the
        // sounds off, to try it on the emulator without them coming out of the Mac's speakers.
        val sound = !(BuildConfig.DEBUG && intent.getBooleanExtra("silencio", false))
        setContent {
            App(store, sound = sound)
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemBars()
    }

    private fun hideSystemBars() {
        WindowCompat.getInsetsController(window, window.decorView).apply {
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            hide(WindowInsetsCompat.Type.systemBars())
        }
    }
}
