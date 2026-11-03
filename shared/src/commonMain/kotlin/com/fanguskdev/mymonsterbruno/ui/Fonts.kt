package com.fanguskdev.mymonsterbruno.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import mymonsterbruno.shared.generated.resources.Res
import mymonsterbruno.shared.generated.resources.andika_bold
import mymonsterbruno.shared.generated.resources.andika_regular
import org.jetbrains.compose.resources.Font

/**
 * Andika, by SIL, the typeface made for those who are learning to read: the letters of the game are drawn with it.
 * Under the SIL Open Font License (licencias/Andika-OFL.txt).
 */
@Composable
internal fun andika(): FontFamily = FontFamily(
    Font(Res.font.andika_regular, FontWeight.Normal),
    Font(Res.font.andika_bold, FontWeight.Bold),
)
