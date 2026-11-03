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
| Trazar: cada vocal se puede trazar entera, también con el dedo temblón; los trazos van en el orden del cole; un dedo lejos no empieza ni avanza; no se puede saltar al final de la O; levantar el dedo guarda lo hecho | `TracingTest` | Común |
| Las rondas: Comeletras pide cada vocal una vez con otras dos en el plato; las fresas van del 1 al 5 y nunca empiezan por el 5; se trazan las menos trazadas; cada vocal tiene su palabra; los globos piden cinco colores distintos con otros dos; las series son dos de dos colores y dos de tres, y su respuesta está entre las opciones | `RoundsTest` | Común |
| Los minutos del día, el día nuevo y los minutos de más; la partida se guarda y se recupera, y una rota empieza de cero; lo que lleva Bruno y cuánto crece | `SessionTest` | Común |
| Cuándo se va Bruno a dormir: enseguida en el cuarto y en el mapa, al terminar la isla dentro de una, dormido si se abre sin tiempo, y el huevo no cuenta | `GameStateTest` | Común |
| Leer los WAV | `WavTest` | Común |
| El sonido del escritorio: la mezcla suma sin desbordarse, respeta el volumen y pasa todo a un canal a 22 050 Hz; y cortar una voz tras otra, como al contar fresas, no atasca su hilo | `DesktopSoundTest` | JVM |
| Cada frase del juego tiene su fichero y dura lo que una frase; el guion y el código tienen las mismas frases; cada efecto tiene su fichero | `AudioFilesTest` | JVM |
| Las doce pantallas se dibujan, en móvil (2,17:1) y en tableta (4:3) | `ScreenshotsTest` | JVM |
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
| Emulador Android `Pixel_10_Pro_XL` | 2992 × 1344 | El de cada prueba en Android |
| Simulador iPad (A16), iOS 26.3 | 2360 × 1640 | El de cada prueba en iOS: la tableta, la proporción más cuadrada |
| Simulador iPhone 17 Pro, iOS 26.5 | 2622 × 1206 | El iPhone; con este juego, todavía sin probar |

Dónde jugará ella, con qué dispositivo, está por saber.

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
  la voz y los efectos funcionan; volver a empezar devuelve a Bruno al huevo y conserva los ajustes.
- [ ] Todo se ve bien en un móvil y en una tableta, sin franjas.

### M3 · La isla de los colores

- [ ] En el mapa, la isla de los colores lleva a los globos; el camino de puntos llega hasta ella.
- [ ] Los globos: Bruno pide uno de un color; el bueno, arrastrado o tocado, se queda en su ramo; el que no es vuelve y
  Bruno dice de qué color es, y desde entonces lo piensa en un bocadillo. Son cinco rondas.
- [ ] Las series: el narrador lee la fila y cada globo salta con su nombre; el bueno llena el hueco; con el que no es,
  Bruno dice su color y la fila se vuelve a leer. Son cuatro series, dos de dos colores y dos de tres.
- [ ] Al terminar, regalo y bandera; en la zona de mayores, los globos por color y las series terminadas.
