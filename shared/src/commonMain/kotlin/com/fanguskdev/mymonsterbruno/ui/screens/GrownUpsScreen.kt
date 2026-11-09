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
import com.fanguskdev.mymonsterbruno.game.HIDEABLE_LETTERS
import com.fanguskdev.mymonsterbruno.game.Hue
import com.fanguskdev.mymonsterbruno.game.LETTERS
import com.fanguskdev.mymonsterbruno.game.MAX_COUNT
import com.fanguskdev.mymonsterbruno.game.Miss
import com.fanguskdev.mymonsterbruno.game.Ordinal
import com.fanguskdev.mymonsterbruno.game.PathShape
import com.fanguskdev.mymonsterbruno.game.Pet
import com.fanguskdev.mymonsterbruno.game.Place
import com.fanguskdev.mymonsterbruno.game.RhymeWord
import com.fanguskdev.mymonsterbruno.game.SUMS
import com.fanguskdev.mymonsterbruno.game.SaveData
import com.fanguskdev.mymonsterbruno.game.Session
import com.fanguskdev.mymonsterbruno.game.Shape
import com.fanguskdev.mymonsterbruno.game.Side
import com.fanguskdev.mymonsterbruno.game.StartWord
import com.fanguskdev.mymonsterbruno.game.Story
import com.fanguskdev.mymonsterbruno.game.TalkWord
import com.fanguskdev.mymonsterbruno.game.Weather
import com.fanguskdev.mymonsterbruno.game.Weekday
import com.fanguskdev.mymonsterbruno.game.choices
import com.fanguskdev.mymonsterbruno.game.chosenPet
import com.fanguskdev.mymonsterbruno.game.feltDays
import com.fanguskdev.mymonsterbruno.game.hardest
import com.fanguskdev.mymonsterbruno.game.hidden
import com.fanguskdev.mymonsterbruno.game.toggledIsland
import com.fanguskdev.mymonsterbruno.game.toggledLetter
import com.fanguskdev.mymonsterbruno.game.week
import com.fanguskdev.mymonsterbruno.game.weekdayOf
import com.fanguskdev.mymonsterbruno.storage.Backup
import com.fanguskdev.mymonsterbruno.storage.Restored
import com.fanguskdev.mymonsterbruno.storage.rememberBackupFiles
import com.fanguskdev.mymonsterbruno.storage.rememberPhotoPicker
import com.fanguskdev.mymonsterbruno.ui.GameState
import com.fanguskdev.mymonsterbruno.ui.Island
import com.fanguskdev.mymonsterbruno.ui.andika
import com.fanguskdev.mymonsterbruno.ui.rememberPhoto
import kotlinx.coroutines.launch

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
                Text(
                    "Sale en el cuarto de Bruno, en su barca, en la emoción del día y en la misión para casa. Se elige en la " +
                        "galería y solo se guarda aquí.",
                    font,
                    15,
                    color = Soft,
                )
                val picker = rememberPhotoPicker()
                val scope = rememberCoroutineScope()
                val photo = rememberPhoto(game)
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
            Section("Su animal favorito", font) {
                Text("Vive en el cuarto con Bruno, en una balda; si lo toca, salta y Bruno le dice que lo cuida.", font, 15, color = Soft)
                val pets = listOf<Pet?>(null) + Pet.entries
                for (row in pets.chunked(4)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        for (pet in row) {
                            Chip(pet?.spoken ?: "Ninguno", font, selected = data.chosenPet == pet) {
                                game.update { it.copy(pet = pet?.word.orEmpty(), petSeen = it.pet == pet?.word.orEmpty() && it.petSeen) }
                            }
                        }
                    }
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
                    Chip(if (data.musicOn) "Música: sí" else "Música: no", font, selected = data.musicOn) {
                        game.update { it.copy(musicOn = !it.musicOn) }
                    }
                }
                Text("La música suena muy suave en las islas, distinta en cada una.", font, 15, color = Soft)
            }
            Section("Qué sale en el juego", font) {
                Text(
                    "Las islas del mapa, y las consonantes de Comeletras, de los trazos y de las rocas de su isla. Quitad una isla que " +
                        "ya domine, o una letra que aún no hayáis visto en clase. Las vocales salen siempre, y en el mapa queda al " +
                        "menos una isla.",
                    font,
                    15,
                    color = Soft,
                )
                val islandNames = Island.entries.map { it.name }
                for (row in Island.entries.chunked(ISLANDS_IN_A_ROW)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        for (island in row) {
                            val shown = island.name !in data.hiddenIslands
                            Chip(island.spoken.replaceFirstChar { it.uppercase() }, font, selected = shown) {
                                game.update { it.toggledIsland(island.name, islandNames) }
                            }
                        }
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    for (letter in HIDEABLE_LETTERS) {
                        Chip(letter.uppercase(), font, selected = letter !in data.hidden) {
                            game.update { it.toggledLetter(letter) }
                        }
                    }
                }
            }
            Section("Su semana", font) {
                Text(
                    "Los últimos siete días: cuánto ha jugado cada uno, qué islas ha terminado y lo que más le ha costado, lo que pedía " +
                        "Bruno cuando ella tocó otra cosa. Ella no lo ve nunca.",
                    font,
                    15,
                    color = Soft,
                )
                val today = game.currentDay()
                val week = data.week(today)
                val minutes = week.joinToString("   ") { (day, log) ->
                    "${weekdayOf(day).short} ${day % 100}: ${(log.seconds + HALF_MINUTE) / SECONDS_IN_A_MINUTE}"
                }
                Text("Minutos de juego:   $minutes", font, 16)
                val finished = HashMap<String, Int>()
                for ((_, log) in week) for ((island, times) in log.islands) finished[island] = (finished[island] ?: 0) + times
                val islands = Island.entries
                    .filter { (finished[it.name] ?: 0) > 0 }
                    .joinToString(", ") { "${it.spoken} ${finished[it.name]}" }
                Text(if (islands.isEmpty()) "Islas terminadas: ninguna esta semana." else "Islas terminadas:   $islands", font, 16)
                val hardest = data.hardest(today)
                val hard = hardest.joinToString(", ") { (what, times) ->
                    "${missLabel(what)} (${if (times == 1) "una vez" else "$times veces"})"
                }
                Text(
                    if (hardest.isEmpty()) {
                        "Lo que más le ha costado: nada; esta semana le ha salido todo a la primera."
                    } else {
                        "Lo que más le ha costado:   $hard"
                    },
                    font,
                    16,
                )
            }
            Section("Lo que ha practicado", font) {
                val fed = LETTERS.joinToString("   ") { "${it.uppercaseChar()} ${data.fed[it.toString()] ?: 0}" }
                val traced = LETTERS.joinToString("   ") { "${it.uppercaseChar()} ${data.traced[it.toString()] ?: 0}" }
                val counted = (1..MAX_COUNT).joinToString("   ") { "$it: ${data.counted[it.toString()] ?: 0}" }
                Text("Galletas bien dadas, por letra:   $fed", font, 16)
                Text("Letras trazadas enteras:   $traced", font, 16)
                val paths = PathShape.entries.joinToString("   ") { "${it.spoken} ${data.paths[it.word] ?: 0}" }
                Text("Caminos de Bruno hasta su casa:   $paths", font, 16)
                Text("Veces que ha escrito su nombre: ${data.namesWritten}.", font, 16)
                Text("Fresas contadas, por número:   $counted", font, 16)
                Text("Cohetes despegados, contando hacia atrás: ${data.launches}.", font, 16)
                val sums = SUMS.joinToString("   ") { "${it.has} y ${it.more}: ${data.sums[it.key] ?: 0}" }
                Text("Sumas de fresas resueltas:   $sums", font, 16)
                val races = Ordinal.entries.joinToString("   ") { "${it.word} ${data.races[it.word] ?: 0}" }
                Text("En las carreras de animales, quién llegó:   $races", font, 16)
                val before = data.neighbours[Side.BEFORE.word] ?: 0
                val after = data.neighbours[Side.AFTER.word] ?: 0
                Text("En la recta de los números, el de antes: $before; el de después: $after.", font, 16)
                val balloons = Hue.entries.joinToString("   ") { "${it.word} ${data.balloons[it.word] ?: 0}" }
                Text("Globos bien dados, por color:   $balloons", font, 16)
                Text("Series de globos terminadas: ${data.series}.", font, 16)
                val shapes = Shape.entries.joinToString("   ") { "${it.spoken} ${data.shapes[it.word] ?: 0}" }
                Text("Piezas de la casa bien puestas, por forma:   $shapes", font, 16)
                Text("Casas construidas: ${data.houses}.", font, 16)
                val places = Place.entries.joinToString("   ") { "${it.spoken} ${data.places[it.word] ?: 0}" }
                Text("Dentro y fuera, encima y debajo:   $places", font, 16)
                val feelings = Feeling.entries.joinToString("   ") { "${it.spoken} ${data.feelings[it.word] ?: 0}" }
                Text("Emociones que ha reconocido:   $feelings", font, 16)
                val weathers = Weather.entries.joinToString("   ") { "${it.spoken} ${data.weathers[it.word] ?: 0}" }
                Text("El tiempo que ha visto por la ventana, en la asamblea:   $weathers", font, 16)
                val stories = Story.entries.joinToString("   ") { "${it.spoken} ${data.stories[it.key] ?: 0}" }
                Text("Historias que ha ordenado:   $stories", font, 16)
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
                Text("Pegatinas en la pared del cuarto: ${data.stickers.size} de ${Island.entries.size}.", font, 16)
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
            Section("Su partida", font) {
                Text(
                    "Una copia de todo lo que ha hecho, con su nombre, su Bruno y su foto, para llevarla a otro móvil si cambiáis de " +
                        "teléfono. Se guarda donde elijáis; el juego no la manda a ningún sitio.",
                    font,
                    15,
                    color = Soft,
                )
                val files = rememberBackupFiles()
                val scope = rememberCoroutineScope()
                var note by remember { mutableStateOf<String?>(null) }
                var found by remember { mutableStateOf<Restored?>(null) }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Chip("Guardar una copia", font, selected = false) {
                        scope.launch {
                            found = null
                            note = if (files.save(Backup.FILE_NAME, Backup.write(game.data, game.photo))) "Copia guardada." else null
                        }
                    }
                    Chip(if (found == null) "Recuperar una copia" else "Toca otra vez para recuperarla", font, selected = found != null) {
                        val copy = found
                        if (copy != null) {
                            game.restore(copy)
                            found = null
                            note = "Partida recuperada."
                        } else {
                            scope.launch {
                                val text = files.open() ?: return@launch
                                found = Backup.read(text)
                                note = if (found == null) {
                                    "Ese fichero no es una copia del juego: no cambia nada."
                                } else {
                                    "Cambiará la partida de ahora por la de la copia."
                                }
                            }
                        }
                    }
                }
                note?.let { Text(it, font, 15) }
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
                                musicOn = it.musicOn,
                            )
                        }
                        confirmReset = false
                        game.leaveGrownUps()
                    } else {
                        confirmReset = true
                    }
                }
            }
            Text(
                "Sin internet, sin anuncios, sin compras y sin datos fuera del dispositivo: la copia de su partida solo sale si la " +
                    "guardáis vosotros.",
                font,
                14,
                color = Soft,
            )
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

/** How the grown-ups read what she missed (ADR 0040): «la M», «el rojo», «el número de antes, en la recta». */
private fun missLabel(key: String): String {
    val what = key.substringAfter(':', "")
    return when (key.substringBefore(':')) {
        Miss.LETTER -> "la ${what.uppercase()}"
        Miss.COLOR -> "el $what"
        Miss.SERIES -> "las series de globos"
        Miss.SHAPE -> Shape.entries.firstOrNull { it.word == what }?.let { "el ${it.spoken}" } ?: what
        Miss.FEELING -> Feeling.entries.firstOrNull { it.word == what }?.let {
            (if (it == Feeling.ANGER || it == Feeling.FEAR) "el " else "la ") + it.spoken
        } ?: what
        Miss.STORY -> "ordenar las historias"
        Miss.START -> "lo que empieza por «$what»"
        Miss.RHYME -> "la rima de «$what»"
        Miss.COUNTDOWN -> "la cuenta atrás"
        Miss.SIDE -> if (what == Side.BEFORE.word) "el número de antes, en la recta" else "el número de después, en la recta"
        Miss.SUM -> what.split('_').let { "la suma de ${it.first()} y ${it.last()}" }
        Miss.PLACE -> Place.entries.firstOrNull { it.word == what }?.spoken ?: what
        Miss.ORDINAL -> "quién llega $what, en la carrera"
        else -> what
    }
}

/** The day of the week, short, as a grown-up reads it. */
private val Weekday.short: String get() = when (this) {
    Weekday.MONDAY -> "lun"
    Weekday.TUESDAY -> "mar"
    Weekday.WEDNESDAY -> "mié"
    Weekday.THURSDAY -> "jue"
    Weekday.FRIDAY -> "vie"
    Weekday.SATURDAY -> "sáb"
    Weekday.SUNDAY -> "dom"
}

/** How the grown-ups' corner names each island. */
private val Island.spoken: String get() = when (this) {
    Island.VOWELS -> "letras"
    Island.NUMBERS -> "números"
    Island.COLORS -> "colores"
    Island.SHAPES -> "formas"
    Island.FEELINGS -> "emociones"
    Island.WORDS -> "palabras"
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
private const val ISLANDS_IN_A_ROW = 3
private const val SECONDS_IN_A_MINUTE = 60
private const val HALF_MINUTE = 30
private const val EXTRA_MINUTES = 10

/** How many of the days she said how she felt the corner shows, the latest first. */
private const val FELT_DAYS_SHOWN = 7

/** How the grown-ups' corner names each story. */
private val Story.spoken: String get() = when (this) {
    Story.FLOWER -> "la flor"
    Story.TEETH -> "los dientes"
    Story.CAKE -> "el bizcocho"
    Story.SNOWMAN -> "el muñeco de nieve"
}

private val Place.spoken: String get() = when (this) {
    Place.BIG_BOX -> "dentro de la caja grande"
    Place.SMALL_BOX -> "dentro de la caja pequeña"
    Place.ON_TABLE -> "encima de la mesa"
    Place.UNDER_TABLE -> "debajo de la mesa"
    Place.OUT_OF_BOX -> "fuera de la caja"
}

private val Weather.spoken: String get() = when (this) {
    Weather.SUN -> "sol"
    Weather.CLOUDS -> "nubes"
    Weather.RAIN -> "lluvia"
    Weather.SNOW -> "nieve"
}

private val PathShape.spoken: String get() = when (this) {
    PathShape.ACROSS -> "recto"
    PathShape.DOWN -> "de arriba abajo"
    PathShape.SLANT -> "inclinado"
    PathShape.WAVES -> "de olas"
    PathShape.LOOPS -> "con bucles"
    PathShape.SPIRAL -> "caracol"
}

private val Pet.spoken: String get() = when (this) {
    Pet.CAT -> "Gato"
    Pet.DOG -> "Perro"
    Pet.RABBIT -> "Conejo"
    Pet.FISH -> "Pez"
    Pet.BIRD -> "Pájaro"
    Pet.TURTLE -> "Tortuga"
}
