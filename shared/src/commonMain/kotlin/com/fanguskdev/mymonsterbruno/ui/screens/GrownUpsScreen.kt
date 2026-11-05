package com.fanguskdev.mymonsterbruno.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fanguskdev.mymonsterbruno.game.Choosing
import com.fanguskdev.mymonsterbruno.game.Feeling
import com.fanguskdev.mymonsterbruno.game.Hue
import com.fanguskdev.mymonsterbruno.game.LETTERS
import com.fanguskdev.mymonsterbruno.game.MAX_COUNT
import com.fanguskdev.mymonsterbruno.game.RhymeWord
import com.fanguskdev.mymonsterbruno.game.SaveData
import com.fanguskdev.mymonsterbruno.game.Session
import com.fanguskdev.mymonsterbruno.game.Shape
import com.fanguskdev.mymonsterbruno.game.StartWord
import com.fanguskdev.mymonsterbruno.game.TalkWord
import com.fanguskdev.mymonsterbruno.game.choices
import com.fanguskdev.mymonsterbruno.game.feltDays
import com.fanguskdev.mymonsterbruno.storage.rememberPhotoPicker
import com.fanguskdev.mymonsterbruno.ui.GameState
import com.fanguskdev.mymonsterbruno.ui.andika
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.decodeToImageBitmap

private val Ink = Color(0xFF1D2B53)
private val Soft = Color(0xFF46526F)
private val Line = Color(0xFFDCE3EF)

/**
 * The grown-ups' corner, behind the padlock: her name for the bunting, the minutes a day, the sound, what she has
 * practised, starting again and, where the platform allows it, closing the game ([onExit]). The only screen with words to
 * read; nothing of it leaves the device (ADR 0005).
 */
@Composable
internal fun GrownUpsScreen(game: GameState, onExit: (() -> Unit)? = null) {
    val font = andika()
    val data = game.data
    var confirmReset by remember { mutableStateOf(false) }
    val day = game.currentDay()
    val focus = LocalFocusManager.current

    Box(
        Modifier
            .fillMaxSize()
            .background(Color(0xFFF6F7FB))
            .pointerInput(Unit) { detectTapGestures { focus.clearFocus() } }
            .safeDrawingPadding(),
    ) {
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 28.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Zona de mayores", font, 26, FontWeight.Bold, Modifier.weight(1f))
                if (onExit != null) {
                    Chip("Salir del juego", font, selected = false) {
                        game.save()
                        onExit()
                    }
                }
                Chip("Volver al juego", font, selected = true) { game.leaveGrownUps() }
            }
            Section("Su nombre", font) {
                Text("Se ve en el banderín del cuarto de Bruno.", font, 15, color = Soft)
                var name by remember { mutableStateOf(data.childName) }
                BasicTextField(
                    value = name,
                    onValueChange = { value ->
                        // Only letters for the bunting: one pennant each.
                        name = value.filter { it.isLetter() || it == ' ' }.take(NAME_LENGTH)
                        game.update { it.copy(childName = name.trim()) }
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words, imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { focus.clearFocus() }),
                    textStyle = TextStyle(fontFamily = font, fontSize = 22.sp, color = Ink),
                    modifier = Modifier
                        .widthIn(max = 360.dp)
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(12.dp))
                        .border(2.dp, Line, RoundedCornerShape(12.dp))
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                )
            }
            Section("Su foto", font) {
                Text("Sale en un marco del cuarto de Bruno. Se elige en la galería y solo se guarda aquí.", font, 15, color = Soft)
                val picker = rememberPhotoPicker()
                val scope = rememberCoroutineScope()
                val photo = remember(game.photo) { game.photo?.let { runCatching { it.decodeToImageBitmap() }.getOrNull() } }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                    if (photo != null) {
                        Image(photo, contentDescription = "Su foto", modifier = Modifier.size(64.dp).clip(RoundedCornerShape(10.dp)))
                    }
                    Chip(if (photo == null) "Elegir su foto" else "Cambiar su foto", font, selected = false) {
                        scope.launch { picker.pick()?.let(game::choosePhoto) }
                    }
                    if (photo != null) Chip("Quitar su foto", font, selected = false) { game.removePhoto() }
                }
            }
            Section("Tiempo de juego", font) {
                Text("Cuando se acaba, Bruno se va a dormir al terminar lo que esté haciendo y vuelve mañana.", font, 15, color = Soft)
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("Minutos al día:", font, 17)
                    for (minutes in SaveData.MINUTE_CHOICES) {
                        Chip("$minutes", font, selected = data.dailyMinutes == minutes) {
                            game.update { it.copy(dailyMinutes = minutes) }
                        }
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("Hoy lleva ${Session.minutesPlayed(data, day)} min.", font, 17)
                    Chip("10 minutos más, solo hoy", font, selected = false) {
                        game.update { Session.moreTime(it, day, EXTRA_MINUTES) }
                    }
                }
            }
            Section("Sonido", font) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Chip(if (data.voiceOn) "Voz: sí" else "Voz: no", font, selected = data.voiceOn) {
                        game.update { it.copy(voiceOn = !it.voiceOn) }
                    }
                    Chip(if (data.effectsOn) "Efectos: sí" else "Efectos: no", font, selected = data.effectsOn) {
                        game.update { it.copy(effectsOn = !it.effectsOn) }
                    }
                }
            }
            Section("Lo que ha practicado", font) {
                val fed = LETTERS.joinToString("   ") { "${it.uppercaseChar()} ${data.fed[it.toString()] ?: 0}" }
                val traced = LETTERS.joinToString("   ") { "${it.uppercaseChar()} ${data.traced[it.toString()] ?: 0}" }
                val counted = (1..MAX_COUNT).joinToString("   ") { "$it: ${data.counted[it.toString()] ?: 0}" }
                Text("Galletas bien dadas, por letra:   $fed", font, 16)
                Text("Letras trazadas enteras:   $traced", font, 16)
                Text("Veces que ha escrito su nombre: ${data.namesWritten}.", font, 16)
                Text("Fresas contadas, por número:   $counted", font, 16)
                val balloons = Hue.entries.joinToString("   ") { "${it.word} ${data.balloons[it.word] ?: 0}" }
                Text("Globos bien dados, por color:   $balloons", font, 16)
                Text("Series de globos terminadas: ${data.series}.", font, 16)
                val shapes = Shape.entries.joinToString("   ") { "${it.spoken} ${data.shapes[it.word] ?: 0}" }
                Text("Piezas de la casa bien puestas, por forma:   $shapes", font, 16)
                Text("Casas construidas: ${data.houses}.", font, 16)
                val feelings = Feeling.entries.joinToString("   ") { "${it.spoken} ${data.feelings[it.word] ?: 0}" }
                Text("Emociones que ha reconocido:   $feelings", font, 16)
                val talked = TalkWord.entries.joinToString("   ") { "${it.word} ${data.talked[it.word] ?: 0}" }
                Text("Palabras dichas a Bruno, con sus palmadas:   $talked", font, 16)
                val starts = StartWord.entries.map {
                    it.start
                }.distinct().joinToString("   ") { "${it.uppercase()} ${data.starts[it] ?: 0}" }
                Text("Palabras que empiezan igual, por sílaba:   $starts", font, 16)
                val rhymes = RhymeWord.entries.filter { it.key == it.pair }.joinToString("   ") { first ->
                    val second = RhymeWord.entries.first { it.ending == first.ending && it != first }
                    "${first.word} y ${second.word} ${data.rhymes[first.pair] ?: 0}"
                }
                Text("Rimas encontradas, por pareja:   $rhymes", font, 16)
                Text("Islas terminadas: ${data.islandsDone}. Regalos de Bruno: ${data.look.accessories.size} de 6.", font, 16)
            }
            Section("Se adapta a lo que sabe", font) {
                Text(
                    "Con cinco rondas seguidas bien a la primera, elige entre una más; si falla más de la mitad de las últimas cinco, " +
                        "entre una menos. Ella no lo sabe: no hay niveles ni puntos.",
                    font,
                    15,
                    color = Soft,
                )
                Text("Ahora elige entre:   " + Choosing.entries.joinToString("   ") { "${it.word} ${data.choices(it)}" }, font, 16)
            }
            Section("Cómo se ha sentido", font) {
                Text(
                    "Lo dice ella el primer rato de cada día, en el bote de su emoción, como en la asamblea de clase.",
                    font,
                    15,
                    color = Soft,
                )
                val days = data.feltDays().take(FELT_DAYS_SHOWN)
                val felt = days.joinToString("   ") { (day, feeling) -> "${day % 100}/${day / 100 % 100}: ${feeling.spoken}" }
                Text(if (days.isEmpty()) "Todavía no lo ha dicho." else felt, font, 16)
            }
            Section("Bruno", font) {
                Text("Volver a empezar: Bruno vuelve al huevo y se borra lo practicado. Los ajustes se quedan.", font, 15, color = Soft)
                Chip(if (confirmReset) "Toca otra vez para confirmar" else "Volver a empezar", font, selected = confirmReset) {
                    if (confirmReset) {
                        game.update {
                            SaveData(
                                childName = it.childName,
                                dailyMinutes = it.dailyMinutes,
                                playedDay = it.playedDay,
                                playedSeconds = it.playedSeconds,
                                extraSeconds = it.extraSeconds,
                                voiceOn = it.voiceOn,
                                effectsOn = it.effectsOn,
                            )
                        }
                        confirmReset = false
                        game.leaveGrownUps()
                    } else {
                        confirmReset = true
                    }
                }
            }
            Text("Sin internet, sin anuncios, sin compras y sin datos fuera del dispositivo.", font, 14, color = Soft)
            Text(
                "El micrófono solo escucha en «Habla con Bruno» mientras ella mantiene el botón. Lo que oye lo reconoce el propio " +
                    "aparato, sin conexión, y no se guarda.",
                font,
                14,
                color = Soft,
            )
            Spacer(Modifier.height(12.dp))
        }
    }
}

@Composable
private fun Section(title: String, font: FontFamily, content: @Composable () -> Unit) {
    Column(
        Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(16.dp))
            .border(2.dp, Line, RoundedCornerShape(16.dp))
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(title, font, 19, FontWeight.Bold)
        content()
    }
}

@Composable
private fun Text(
    text: String,
    font: FontFamily,
    size: Int,
    weight: FontWeight = FontWeight.Normal,
    modifier: Modifier = Modifier,
    color: Color = Ink,
) = BasicText(text, modifier, TextStyle(fontFamily = font, fontSize = size.sp, fontWeight = weight, color = color))

@Composable
private fun Chip(
    text: String,
    font: FontFamily,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val focus = LocalFocusManager.current
    Box(
        Modifier
            .background(if (selected) Ink else Color.White, RoundedCornerShape(50))
            .border(2.dp, if (selected) Ink else Line, RoundedCornerShape(50))
            .clickable {
                focus.clearFocus()
                onClick()
            }
            .padding(horizontal = 16.dp, vertical = 9.dp),
    ) {
        BasicText(
            text,
            style = TextStyle(
                fontFamily = font,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = if (selected) Color.White else Ink,
            ),
        )
    }
}

/** The name of a [Feeling] as a grown-up reads it. */
private val Feeling.spoken: String get() = when (this) {
    Feeling.JOY -> "alegría"
    Feeling.SADNESS -> "tristeza"
    Feeling.ANGER -> "enfado"
    Feeling.FEAR -> "miedo"
    Feeling.CALM -> "calma"
}

/** The name of [Shape] as a grown-up reads it, with its accent. */
private val Shape.spoken: String get() = when (this) {
    Shape.SQUARE -> "cuadrado"
    Shape.TRIANGLE -> "triángulo"
    Shape.CIRCLE -> "círculo"
    Shape.RECTANGLE -> "rectángulo"
}

private const val NAME_LENGTH = 12
private const val EXTRA_MINUTES = 10

/** How many of the days she said how she felt the corner shows, the latest first. */
private const val FELT_DAYS_SHOWN = 7
