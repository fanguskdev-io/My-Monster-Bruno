package com.fanguskdev.mymonsterbruno.ui

import androidx.compose.runtime.Composable

/** An iPhone has no back button: the screens have their own. */
@Composable
internal actual fun PlatformBackHandler(enabled: Boolean, onBack: () -> Unit) = Unit
