# Pruebas

Qué se prueba en cada cambio y en cada hito, y cómo. Los criterios de salida de cada hito están en [proceso](proceso.md).

## En cada cambio

1. **La puerta**: `bash scripts/verificar.sh` termina en «La puerta pasa».
2. **A mano**, lo que cambia, en el emulador de Android y en el simulador de iOS, con capturas si se ve. El emulador va
   sin audio (`-no-audio`) y la app arranca en silencio; al acabar se cierran la app y el emulador o el simulador, para
   que no suene nada en el Mac.

## Las pruebas automáticas

| Qué | Prueba | Dónde |
|---|---|---|
| Trazar: cada letra (vocales, M, P, L y S) se puede trazar entera, también con el dedo temblón; los trazos van en el orden del cole; un dedo lejos no empieza ni avanza; no se puede saltar al final de la O; levantar el dedo guarda lo hecho | `TracingTest` | Común |
| Las rondas: Comeletras pide el primer día las vocales y después las letras menos dadas, con las consonantes; las fresas van del 1 al 5 y, cuando los ha contado, mezclan hasta el 10, nunca empezando por el más grande; se trazan las menos trazadas, las vocales primero; cada letra tiene su palabra; los globos piden cinco colores distintos con otros dos; las series son dos de dos colores y dos de tres, y su respuesta está entre las opciones; la casa se construye en orden y su pieza siempre está entre las tres; las emociones salen una vez cada una, la nana la última, y su carita está entre las tres; «Habla con Bruno» pide cuatro de las palabras que menos ha dicho, de menos palmadas a más, y cada palabra son sus sílabas, de una a cuatro | `RoundsTest` | Común |
| Los minutos del día, el día nuevo y los minutos de más; la partida se guarda y se recupera, y una rota empieza de cero; lo que lleva Bruno y cuánto crece | `SessionTest` | Común |
| Cuándo se va Bruno a dormir: enseguida en el cuarto y en el mapa, al terminar la isla dentro de una o «Habla con Bruno», dormido si se abre sin tiempo, y el huevo no cuenta | `GameStateTest` | Común |
| Leer los WAV | `WavTest` | Común |
| Lo que se hace con su voz antes de que Bruno la repita: sin el silencio de los bordes (y nada si solo hay silencio o un murmullo), a la altura de las voces del juego, más aguda y más rápida, en un canal y de vuelta a WAV | `MicrophoneTest` | Común |
| El sonido del escritorio: la mezcla suma sin desbordarse, respeta el volumen y pasa todo a un canal a 22 050 Hz; y cortar una voz tras otra, como al contar fresas, no atasca su hilo | `DesktopSoundTest` | JVM |
| Cada frase del juego tiene su fichero y dura lo que una frase; el guion y el código tienen las mismas frases; cada efecto tiene su fichero | `AudioFilesTest` | JVM |
| Las quince pantallas y, cuando ya sabe las vocales y del 1 al 5, Comeletras, los trazos y las fresas con lo nuevo, se dibujan en móvil (2,17:1) y en tableta (4:3) | `ScreenshotsTest` | JVM |
| La salida de la zona de mayores: «Salir del juego» guarda la partida y cierra; sin salida en la plataforma, como en iOS, no hay botón | `GrownUpsExitTest` | JVM |
| El icono de iOS es el de Android: lo dibuja del vector de `androidApp` en `shared/build/icono/` y lo compara con el del proyecto de Xcode; si cambia el de Android, se copia el nuevo | `AppIconTest` | JVM |

Las comunes corren tres veces: en la JVM, en las pruebas de host de Android y en el simulador de iOS. Las de la JVM, solo
allí, porque leen ficheros o dibujan con Skia. Las capturas de `ScreenshotsTest` quedan en `shared/build/capturas/`, y
mirarlas es cosa de una persona.

## Arrancar en silencio

La versión de depuración arranca sin sonido con un argumento:

```bash
adb shell am start -n com.fanguskdev.mymonsterbruno/.MainActivity --ez silencio true
```

```bash
xcrun simctl launch booted com.fanguskdev.mymonsterbruno.MyMonsterBruno -silencio
```

```bash
./gradlew :desktopApp:run --args=--silencio
```

## Dispositivos

| Dispositivo | Pantalla en horizontal | Uso |
|---|---|---|
| Emulador Android `Tester_API36` | 2400 × 1080 | El de cada prueba en Android. El `Pixel_10_Pro_XL` es el de la sesión de CaroFarty: no se usa desde aquí (acordado el 2026-11-03) |
| Simulador iPad (A16), iOS 26.3 | 2360 × 1640 | El de cada prueba en iOS: la tableta, la proporción más cuadrada |
| Simulador iPhone 17 Pro, iOS 26.5 | 2622 × 1206 | El iPhone; con este juego, todavía sin probar |

Ella jugará en un iPhone 15 Pro (Fangusk, 2026-11-03).

## El micrófono

«Habla con Bruno» es lo único que usa el micrófono ([ADR 0011](adr/0011-habla-con-bruno.md)). Arrancado en silencio, el
juego tampoco escucha: Bruno dice cada palabra él y da las palmadas, que es lo que se prueba en el emulador (que con
`-no-audio` no tiene micrófono) y en el simulador. Con sonido, el simulador de iOS y el escritorio escuchan con el
micrófono del Mac y lo repiten por sus altavoces, así que esa prueba la hace Fangusk, igual que la del iPhone de verdad.

## En cada hito

### M1 · Primera versión

- [ ] La primera vez sale el huevo. Cinco toques lo rompen y Bruno saluda. Se elige el color y la cabeza, y el botón
  verde lleva al cuarto.
- [ ] En el cuarto, Bruno saluda, se ríe con cosquillas, y el botón grande lleva al mapa.
- [ ] En el mapa, las dos islas llevan a su juego y las demás dicen que están lejos.
- [ ] Comeletras: una galleta equivocada vuelve al plato y Bruno dice cuál era y cuál quiere. La buena se puede arrastrar
  o tocar. Son cinco rondas.
- [ ] Trazar: dos vocales, en el orden del cole. Salirse del camino solo para la pintura.
- [ ] Las fresas: cada toque cuenta en voz alta; al llegar al número, Bruno se las come.
- [ ] Al terminar una isla, el regalo se abre y Bruno lo lleva; cada dos islas crece. La isla lleva su bandera en el mapa.
- [ ] Con el tiempo acabado, Bruno se duerme enseguida en el cuarto o en el mapa, y al terminar la isla dentro de una.
  Bosteza, da la misión para casa y dice «¡Hasta mañana!». Al volver a abrir el juego ese día, sigue dormido.
- [ ] La calma: el globo se hincha mientras se mantiene el dedo; tres respiraciones.
- [ ] La zona de mayores se abre manteniendo el candado. El nombre sale en el banderín; los minutos, «10 minutos más»,
  la voz y los efectos funcionan; volver a empezar devuelve a Bruno al huevo y conserva los ajustes. En Android y en el
  escritorio, «Salir del juego» lo cierra con la partida guardada; en iOS no aparece.
- [ ] Todo se ve bien en un móvil y en una tableta, sin franjas.

### M2 · Habla con Bruno

- [ ] En el cuarto, el micrófono rosa lleva a «Habla con Bruno». La primera vez se pide el permiso del micrófono.
- [ ] Bruno enseña el dibujo y pide la palabra («¡Dime luna!»); la primera vez, el narrador explica que hay que mantener
  el dedo. Mientras se mantiene, el botón crece y echa ondas.
- [ ] Al soltarlo, Bruno repite lo que ha oído con la voz de ella, más aguda. Luego da una palmada por sílaba diciéndola,
  cada sílaba sale en su tarjeta y el narrador cuenta las palmadas.
- [ ] Si no se dice nada, «¡No te oigo!» la primera vez; a la segunda, Bruno dice la palabra él. Sin permiso del
  micrófono, la dice siempre él.
- [ ] Son cuatro palabras, de menos palmadas a más; al acabar, Bruno da las gracias y vuelve al cuarto. La zona de
  mayores cuenta las palabras dichas.
- [ ] En el iPhone 15 Pro, su voz se oye por el altavoz grande y, al salir, el resto del juego suena como antes.

### M3 · La isla de los colores

- [ ] En el mapa, la isla de los colores lleva a los globos; el camino de puntos llega hasta ella.
- [ ] Los globos: Bruno pide uno de un color; el bueno, arrastrado o tocado, se queda en su ramo; el que no es vuelve y
  Bruno dice de qué color es, y desde entonces lo piensa en un bocadillo. Son cinco rondas.
- [ ] Las series: el narrador lee la fila y cada globo salta con su nombre; el bueno llena el hueco; con el que no es,
  Bruno dice su color y la fila se vuelve a leer. Son cuatro series, dos de dos colores y dos de tres.
- [ ] Al terminar, regalo y bandera; en la zona de mayores, los globos por color y las series terminadas.

### M3 · La isla de las formas

- [ ] En el mapa, la isla de las formas lleva a la casa de Bruno; el camino de puntos llega hasta ella.
- [ ] Bruno pide cada pieza y su hueco brilla; la buena, arrastrada o tocada, crece hasta su sitio; la que no es vuelve y
  Bruno dice qué forma es. Son seis piezas, y la casa queda entera.
- [ ] Al terminar, regalo y bandera; en la zona de mayores, las piezas por forma y las casas construidas.

### M3 · Más letras y números

- [ ] El primer día, Comeletras pide las vocales; la vez siguiente entran la M, la P, la L y la S, con su dibujo en el
  bocadillo (manzana, pelota, luna, sol) y su frase («¡Quiero la eme! La eme de manzana»).
- [ ] Las consonantes se trazan en el orden del cole y se pueden acabar.
- [ ] Cuando ha contado del 1 al 5, las fresas mezclan números hasta el 10; la tarjeta enseña dos manos y los puntos en
  dos filas, y en el plato caben diez.

### M3 · La isla de las emociones

- [ ] En el mapa, la isla de las emociones lleva a su juego; el camino de puntos llega hasta ella.
- [ ] A Bruno le pasa algo, se ve en su bocadillo y se pone del color de cómo se siente; la carita buena hace que el
  narrador diga la emoción, y otra, que describa esa carita y vuelva a preguntar.
- [ ] Cada ayuda funciona: aplaudir, el abrazo con corazones, respirar manteniendo el dedo (si se suelta antes, vuelve a
  empezar), la lámpara que enciende la luz y la caricia. Bruno vuelve a su color.
- [ ] Son cinco, la nana la última; al terminar, regalo, bandera, la misión de contar cómo se ha sentido y, en la zona
  de mayores, las emociones reconocidas.
