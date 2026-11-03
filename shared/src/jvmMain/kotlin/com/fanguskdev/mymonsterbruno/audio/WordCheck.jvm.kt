package com.fanguskdev.mymonsterbruno.audio

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

/** The desktop cannot check: it has no recognizer of its own that the game may use. */
@Composable
internal actual fun rememberWordCheck(): WordCheck = remember { NoWordCheck() }
