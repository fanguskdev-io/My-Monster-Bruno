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
| Trazar: cada letra (las 27 mayúsculas y las vocales con tilde) se puede trazar entera, también con el dedo temblón; los trazos van en el orden del cole; cada trazo cabe en su caja, y la tilde y la virgulilla, encima; un dedo lejos no empieza ni avanza; no se puede saltar al final de la O; levantar el dedo guarda lo hecho | `TracingTest` | Común |
| Las rondas: Comeletras pide el primer día las vocales y después las letras menos dadas, con las consonantes; la T, la N y la D no salen hasta que conoce la M, la P, la L y la S, ni en Comeletras ni en los trazos; las fresas van del 1 al 5 y, cuando los ha contado, mezclan hasta el 10, nunca empezando por el más grande; se trazan las menos trazadas, las vocales primero; cada letra tiene su palabra; los globos piden cinco colores distintos con otros dos; las series son dos de dos colores y dos de tres, y su respuesta está entre las opciones; la casa se construye en orden y su pieza siempre está entre las tres; las emociones salen una vez cada una, la nana la última, y su carita está entre las tres; «Habla con Bruno» pide cuatro de las palabras que menos ha dicho, de menos palmadas a más, y cada palabra son sus sílabas, de una a cuatro; la isla de las palabras pide cinco parejas, las menos encontradas, con una sola carta que empieza como la palabra de Bruno, y cada palabra tiene su pareja | `RoundsTest` | Común |
| Los minutos del día, el día nuevo y los minutos de más; la partida se guarda y se recupera, y una rota empieza de cero; lo que lleva Bruno y cuánto crece | `SessionTest` | Común |
| Cuándo se va Bruno a dormir: enseguida en el cuarto y en el mapa, al terminar la isla dentro de una, «Habla con Bruno» o su nombre, dormido si se abre sin tiempo, y el huevo no cuenta; la pantalla de inicio lleva al huevo, al cuarto o a la cama y no cuenta minutos; la emoción del día se pregunta una vez al día al abrir, aunque no conteste; su foto se queda al volver a abrir, una nueva hace que Bruno la vuelva a ver y quitarla la borra | `GameStateTest` | Común |
| La emoción del día: una por día, la última que eligió, y solo los últimos 14 días; un día roto no cuenta | `MoodTest` | Común |
| Que se adapte: tres para empezar, cuatro tras cinco rondas seguidas bien a la primera, dos si se equivoca en más de la mitad, y solo cuentan las cinco últimas de cada juego; con dos, tres o cuatro, la respuesta siempre está, en el mismo orden, y en las cartas solo una empieza igual | `AdaptTest` | Común |
| Escribir su nombre: se trazan sus letras en orden, sin espacios, hasta diez y sin las que el juego no sabe trazar; cada letra del abecedario y cada vocal con tilde tiene su trazo y su voz, distinta de las demás; una vocal con tilde cuenta como su vocal | `NameTest` | Común |
| Leer los WAV | `WavTest` | Común |
| Su foto se guarda en partes que caben en todas las plataformas y vuelve entera; otra la sustituye sin dejar restos, quitarla no deja nada y una rota no es foto | `PhotoStoreTest` | Común |
| Cuándo es la palabra que pidió Bruno: la palabra, aunque vaya en una frase o con mayúsculas y puntos; la que dice una niña de 4 años con una letra de cada tres cambiada; nunca otra palabra, ni nada | `WordCheckTest` | Común |
| Lo que se hace con su voz antes de que Bruno la repita: sin el silencio de los bordes (y nada si solo hay silencio o un murmullo), a la altura de las voces del juego, más aguda y más rápida, en un canal y de vuelta a WAV | `MicrophoneTest` | Común |
| El sonido del escritorio: la mezcla suma sin desbordarse, respeta el volumen y pasa todo a un canal a 22 050 Hz; y cortar una voz tras otra, como al contar fresas, no atasca su hilo | `DesktopSoundTest` | JVM |
| Cada frase del juego tiene su fichero y dura lo que una frase; el guion y el código tienen las mismas frases; cada efecto tiene su fichero | `AudioFilesTest` | JVM |
| Las diecinueve pantallas y, cuando ya sabe las vocales y del 1 al 5, Comeletras, los trazos y las fresas con lo nuevo, Comeletras y los trazos con la T, la N y la D, la pantalla de inicio con el huevo, el cuarto con su foto y cuando Bruno le pide que le enseñe su nombre, la emoción del día con su foto, Comeletras, los globos, las emociones y las palabras con cuatro para elegir y Comeletras con dos, y el mapa con todas las islas terminadas y sus banderas, se dibujan en móvil (2,17:1) y en tableta (4:3) | `ScreenshotsTest` | JVM |
| La salida de la zona de mayores: «Salir del juego» guarda la partida y cierra; sin salida en la plataforma, como en iOS, no hay botón | `GrownUpsExitTest` | JVM |
| El icono de iOS es el de Android: lo dibuja del vector de `androidApp` en `shared/build/icono/` y lo compara con el del proyecto de Xcode; si cambia el de Android, se copia el nuevo | `AppIconTest` | JVM |
| En el iPhone 15 Pro de ella y en el 17 Pro del simulador, girados a un lado y al otro, la casa, el altavoz y el candado se ven enteros: ni bajo la Dynamic Island ni cortados por las esquinas redondeadas, medidas en el simulador | `CornerButtonsTest` | Común |

Las comunes corren tres veces: en la JVM, en las pruebas de host de Android y en el simulador de iOS. Las de la JVM, solo
allí, porque leen ficheros o dibujan con Skia. Las capturas de `ScreenshotsTest` quedan en `shared/build/capturas/`, y
mirarlas es cosa de una persona. Las del iPhone 15 Pro (`iphone15pro-izquierda-…` e `iphone15pro-derecha-…`, según el
lado de la isla) pintan de negro lo que tapan la isla y las esquinas. La zona de mayores no sale en ellas: se aparta
sola de la isla con el margen seguro del sistema, que en la JVM no existe, y en el simulador se ve entera.

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
| Simulador iPhone 17 Pro, iOS 26.3 (`0632083A…`) | 2622 × 1206 | El más parecido al iPhone 15 Pro de ella: el de la entrega (M4). Para verlo girado al otro lado, Device > Rotate Left o Rotate Right en la app Simulator. Con varios simuladores encendidos, `simctl` va con su UDID y no con `booted` |
| Simulador iPhone 17 Pro, iOS 26.5 (`F098984F…`) | 2622 × 1206 | Lo usa otra sesión: no se usa desde aquí |

Ella jugará en un iPhone 15 Pro (Fangusk, 2026-11-03).

## El micrófono

«Habla con Bruno» es lo único que usa el micrófono ([ADR 0011](adr/0011-habla-con-bruno.md)). Arrancado en silencio, el
juego tampoco escucha: Bruno dice cada palabra él y da las palmadas, que es lo que se prueba en el emulador (que con
`-no-audio` no tiene micrófono) y en el simulador. Con sonido, el simulador de iOS y el escritorio escuchan con el
micrófono del Mac y lo repiten por sus altavoces, así que esa prueba la hace Fangusk, igual que la del iPhone de verdad.
Comprobar la palabra solo se puede en un aparato de verdad: el emulador no tiene reconocedor sin conexión y el Mac no
comprueba ([ADR 0015](adr/0015-bruno-comprueba-la-palabra.md)).

## Recorridos en el iPhone

Para ver el juego en un iPhone 15 Pro simulado, girado a un lado y al otro, sin tocarlo a mano:
[`tools/conductor-ios`](../tools/conductor-ios) es una prueba de interfaz que toca el escenario de 800 × 400 y, en cada
punto de control, espera a que `vigilante.sh` saque una captura con lo que tapan la isla y las esquinas pintado de negro.
Tiene seis recorridos: los dos de la entrega (`testWalkThroughHeldBothWays` y `testFinalVersionBothWays`, que empiezan
en el huevo) y los de después (`testTalkAndPhoto`, `testWordsAndLetters` y `testStartAndName`, que empiezan con Bruno ya
nacido y su nombre, Lucía, sin escribir todavía, y la emoción de hoy ya preguntada; y `testFeelingOfTheDay`, que empieza
igual pero sin ella). Todos pasan antes por la pantalla de inicio, con un toque. Siempre en
silencio (`-silencio`) y en un simulador propio que se borra al acabar, nunca en uno de otra sesión.

1. Un simulador temporal con el juego (el de `build/xcode`, que deja la puerta) y, para su foto, una imagen en su galería:

```bash
U=$(xcrun simctl create "Bruno iPhone 15 Pro temporal" com.apple.CoreSimulator.SimDeviceType.iPhone-15-Pro com.apple.CoreSimulator.SimRuntime.iOS-26-5)
```

```bash
xcrun simctl boot "$U" && xcrun simctl install "$U" build/xcode/Build/Products/Debug-iphonesimulator/MyMonsterBruno.app
```

```bash
xcrun simctl addmedia "$U" una-foto.jpg
```

2. La partida de la que se parte se escribe en el fichero de preferencias del juego, dentro de su contenedor (con el
   juego cerrado). Sin la ruta del contenedor, `defaults write` solo vale antes de abrirlo por primera vez:

```bash
C=$(xcrun simctl get_app_container "$U" com.fanguskdev.mymonsterbruno.MyMonsterBruno data)
xcrun simctl spawn "$U" defaults write "$C/Library/Preferences/com.fanguskdev.mymonsterbruno.MyMonsterBruno.plist" partida -string '{"hatched":true,"childName":"Lucía","moodDay":'$(date +%Y%m%d)'}'
```

   Con `"moodDay"`, la emoción del día ya está preguntada hoy y se va derecho al cuarto; sin él, como en
   `testFeelingOfTheDay`, antes del cuarto se pregunta.

3. El vigilante en segundo plano y un recorrido:

```bash
bash tools/conductor-ios/vigilante.sh "$U" /tmp/bruno-conductor/sync /tmp/bruno-capturas ip15 &
```

```bash
TEST_RUNNER_CONDUCTOR_DIR=/tmp/bruno-conductor/sync xcodebuild test -project tools/conductor-ios/Driver.xcodeproj -scheme Driver -destination "platform=iOS Simulator,id=$U" -derivedDataPath /tmp/bruno-conductor/dd -only-testing:DriverUITests/BrunoDriverTests/testTalkAndPhoto
```

4. Al acabar, se para el vigilante y se borra el simulador:

```bash
touch /tmp/bruno-conductor/sync/stop && xcrun simctl delete "$U"
```

El selector de fotos es un proceso de Apple: el recorrido toca la primera foto por su sitio, no por su nombre. Y la zona de
mayores se desplaza arrastrando despacio, porque el deslizar rápido de las pruebas de interfaz no la mueve.

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
- [ ] En su iPhone: la primera vez pide también el permiso de reconocimiento de voz. Si dice otra palabra («pera» por
  «manzana»), Bruno la repite y dice «Huum... Eso no es manzana. ¿Me dices manzana?»; a la segunda la dice él. Si la dice
  bien, aunque sea a su manera («mapoza»), la da por buena.
- [ ] Son cuatro palabras, de menos palmadas a más; al acabar, Bruno da las gracias y vuelve al cuarto. La zona de
  mayores cuenta las palabras dichas.
- [ ] En el iPhone 15 Pro, su voz se oye por el altavoz grande y, al salir, el resto del juego suena como antes.
- [x] En el simulador de iPhone 15 Pro (iOS 26.5), en silencio y girado a un lado y al otro: el micrófono del cuarto lleva
  a «Habla con Bruno», Bruno dice la palabra él y da las palmadas, y la isla no tapa nada (2026-11-03).

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

### M5 · Su foto

- [ ] En la zona de mayores, «Elegir su foto» abre la galería sin pedir permiso de fotos; la elegida sale en el marco del
  cuarto, recortada en cuadrado, y también en pequeño en la zona de mayores.
- [ ] Al volver al cuarto, Bruno la mira y dice «¡Mira! ¡Eres tú!»; la siguiente vez ya no, salvo que ella toque la foto.
- [ ] «Cambiar su foto» pone otra y Bruno la vuelve a ver; «Quitar su foto» la quita del cuarto; «Volver a empezar» la
  conserva. Al cerrar y abrir el juego, sigue.
- [x] Probado en el emulador Tester_API36 con el selector de Android (pide además tocar «Done») y en el simulador de iPhone
  15 Pro con el de Fotos («Acceso privado a las fotos»), sin ningún permiso: la foto sale en la zona de mayores y en el
  marco del cuarto, con el iPhone girado a los dos lados. En Android, además, sigue al cerrar y abrir, Bruno no la vuelve
  a saludar, y «Quitar su foto» la quita (2026-11-03). Falta tocar la foto, «Cambiar su foto» y «Volver a empezar».

### M5 · La T, la N y la D

- [ ] Cuando ya ha dado a Bruno la M, la P, la L y la S, Comeletras pide también la te de tomate, la ene de nube y la de
  de dado, con su dibujo en el bocadillo.
- [ ] Cuando ya las ha trazado, salen también para trazar, con sus trazos en orden: la T, la raya y luego el palo; la N,
  el palo y luego la diagonal y el otro palo sin levantar el dedo; la D, el palo y luego la barriga.
- [ ] En el mapa, la isla de las letras tiene una fila de rocas más: T, N y D.
- [x] En el emulador Tester_API36 y en el simulador de iPhone 15 Pro, con la M, la P, la L y la S ya dadas y trazadas:
  Comeletras pide la te de tomate (y en Android, la de de dado), los trazos ofrecen la N y la D, y el mapa tiene su fila
  de rocas (2026-11-03).

### M5 · La isla de las palabras

- [ ] En el mapa, la isla pequeña de arriba a la derecha lleva a la isla de las palabras; el camino de puntos llega hasta
  ella desde la de las formas.
- [ ] Bruno dice su palabra a trozos, con una palmada y una tarjeta por sílaba, la primera en dorado, y el narrador
  pregunta qué empieza igual.
- [ ] Con la carta buena, Bruno la dice a trozos, su primera sílaba sale en dorado y dice que empiezan igual. Con otra,
  su primera sílaba sale en morado, Bruno dice por qué empieza y esa carta se aparta.
- [ ] Son cinco rondas; al terminar, regalo, bandera, la misión de buscar algo que empiece como su nombre (con su
  inicial en la tarjeta si está escrito) y, en la zona de mayores, las parejas encontradas por sílaba.
- [x] Jugada entera en el emulador Tester_API36, con cartas buenas y malas, hasta el regalo y la bandera; en el simulador
  de iPhone 15 Pro, la pregunta y una carta mala, girado a los dos lados (2026-11-03).

### M5 · Escribir su nombre

- [ ] Con su nombre escrito en la zona de mayores, al entrar en el cuarto Bruno mira el banderín, que ondea, y dice «¿Me
  enseñas a escribir tu nombre? Toca el banderín». Cada vez que se abre el juego, hasta que lo escriba.
- [ ] Tocar el banderín lleva a escribirlo: arriba, su nombre en banderines, con la letra que toca en blanco; en la hoja,
  la letra grande con el punto verde, y el narrador dice «¡Vamos a escribir tu nombre, letra a letra!» y la letra.
- [ ] Al acabar cada letra, se queda verde y vuela a su banderín, que toma su color, y sale la siguiente. La tilde o la
  virgulilla, al final de su letra. El altavoz repite la letra y la casa vuelve al cuarto.
- [ ] Con todas, su nombre sale entero en la hoja, los banderines saltan, Bruno dice «¡Ya sé escribir tu nombre!» y
  vuelve al cuarto, donde ya no se lo pide. En la zona de mayores, «Veces que ha escrito su nombre» suma una.
- [ ] Sin nombre, el banderín de estrellas no hace nada.
- [x] En el emulador Tester_API36, con «Lucía» escrito y sin escribir nunca: Bruno mira el banderín y lo pide; el banderín
  lleva a escribirlo, y un dedo de `adb` (`input motionevent`, punto a punto por los trazos de cada letra) escribe las
  cinco letras con la tilde de la Í. Cada una vuela a su banderín, sale «LUCÍA» en la hoja, Bruno lo celebra y vuelve al
  cuarto, donde ya no lo pide; en la zona de mayores, «Veces que ha escrito su nombre: 1» y la A, la I, la U y la L
  trazadas una vez. Atrás vuelve al cuarto. En el simulador de iPhone 15 Pro, girado a los dos lados
  (`testStartAndName`): la invitación, la pantalla del nombre y la L trazada, que vuela a su banderín (2026-11-04).

### M5 · Que se adapte a lo que sabe

- [ ] Con las cinco últimas rondas de Comeletras bien a la primera, el plato trae cuatro galletas; equivocándose en tres
  de cinco, dos. Igual con los globos, las caritas de las emociones y las cartas de la isla de las palabras.
- [ ] Las cuatro caben sin salirse de la pantalla ni tocar a Bruno, y se cogen bien con el dedo.
- [ ] En la zona de mayores, «Se adapta a lo que sabe» dice entre cuántas elige ahora en cada juego.
- [x] En el emulador Tester_API36, con partidas sembradas: con las cinco últimas bien en cada juego, cuatro galletas,
  cuatro globos, cuatro caritas y cuatro cartas; con las cinco mal, dos caritas y dos cartas. Con cuatro bien en
  Comeletras, la ronda 1 trae tres y, al acertar a la primera, la 2 ya trae cuatro; la zona de mayores dice «galletas 4
  globos 3 caritas 3 cartas 3». En el simulador de iPhone 15 Pro, girado a los dos lados (`testWordsAndLetters`), cuatro
  cartas y cuatro galletas (2026-11-05).

### M5 · La emoción del día

- [ ] La primera vez que se abre el juego cada día, después de la pantalla de inicio: Bruno saluda y pregunta «¿Cómo te
  sientes hoy?», y el narrador dice que lleve su foto (o la carita, si no hay foto) al bote de cómo se siente.
- [ ] Tocar un bote dice su nombre y lo hace bailar. Arrastrar la foto fuera de los botes la devuelve a su sitio.
- [ ] Con la foto en un bote, Bruno contesta a esa emoción, pone la suya en el bote de la alegría y vuelve al cuarto, que
  solo invita a la isla. Al volver a abrir el juego ese día, ya no pregunta; al día siguiente, sí.
- [ ] La casa lo salta, y ese día ya no se pregunta. El día que nace Bruno no se pregunta.
- [ ] En la zona de mayores, «Cómo se ha sentido» enseña el día y la emoción.
- [x] En el emulador Tester_API36: la pregunta tras la pantalla de inicio, un bote tocado, la carita arrastrada al bote
  de la tristeza, el abrazo con corazones, la ficha de Bruno en el de la alegría y el cuarto; al volver a abrir ese día,
  derecho al cuarto; la casa lo salta y lo deja preguntado; en la zona de mayores, «4/11: tristeza   3/11: alegría». En
  el simulador de iPhone 15 Pro, girado a los dos lados (`testFeelingOfTheDay`), lo mismo hasta el cuarto (2026-11-04).

### M5 · La pantalla de inicio

- [ ] Al abrir el juego, Bruno en su barca (o su huevo, antes de nacer), las islas pasando por el horizonte y las letras
  de BRUNO cayendo; después, el sonido de inicio, «¡Mi amigo Bruno!» y «¡Toca para empezar!».
- [ ] Un toque en cualquier sitio funde a blanco y abre el huevo, el cuarto o, sin minutos, la cama. El botón de atrás de
  Android sale del juego, como en el cuarto.
- [x] En el emulador Tester_API36 y en el simulador de iPhone 15 Pro, girado a los dos lados: «Mi amigo» y BRUNO, Bruno en
  su barca y las islas pasando; un toque lleva al cuarto. En Android, atrás sale del juego (2026-11-04).

### M4 · Entrega

- [x] En el simulador iPhone 17 Pro (iOS 26.3), en silencio y girado a un lado y al otro: el huevo, elegir su color, el
  cuarto, el mapa, Comeletras, la calma y la zona de mayores. La casa, el altavoz y el candado se ven enteros; la isla
  solo tapa la punta de la isla de las emociones en el mapa y menos de un punto del borde de la galleta de la derecha en
  Comeletras (2026-11-03).
- [x] La versión definitiva, con «Habla con Bruno», en un simulador de iPhone 15 Pro (iOS 26.3), en silencio y girado a
  un lado y al otro: el huevo, el cuarto, «Habla con Bruno», el mapa, las cinco islas, la calma y la zona de mayores. Se
  ve igual que en el 17 Pro, y el micrófono rosa del cuarto queda a unos 20 puntos de la isla (2026-11-03).
- [ ] Instalado en su iPhone 15 Pro, ad hoc, el día de la entrega ([ADR 0012](adr/0012-la-entrega-ad-hoc.md),
  [entrega](entrega.md)), con su icono y el nombre «Bruno».
- [ ] Allí, con sonido: se juega girado a un lado y al otro, se oyen la voz y los efectos, «Habla con Bruno» repite su
  voz, y al cerrarlo y volver a abrirlo Bruno sigue como estaba.
