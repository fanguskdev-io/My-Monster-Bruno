# MyMonsterBruno

«Mi monstruo Bruno», un juego educativo para Android e iOS. De un huevo nace Bruno, un monstruo que no sabe nada de nada,
y ella le enseña las vocales, a contar y a trazar las letras. Es un regalo de Fangusk para su sobrina de 4 años, que este
curso ha empezado el cole en España. La [página de diseño](docs/diseno.md) cuenta lo que pidió, las reglas y las
decisiones, y la [investigación](docs/investigacion/investigacion.md), de dónde sale.

> En construcción: la primera versión (M1) se juega de principio a fin en Android e iOS, y Fangusk ya la está probando.
> M3 está hecho: las islas de los colores, las formas y las emociones, y las letras M, P, L y S y los números hasta el 10.
> Después llegaron la T, la N y la D, y su foto en el cuarto de Bruno.
> M2 también: «Habla con Bruno», en el que ella le dice palabras con el micrófono y él las repite con su voz y las da en
> palmadas. Y la isla de las palabras, en la que busca qué empieza como la palabra de Bruno. La entrega en su iPhone
> está preparada: será ad hoc, instalado el día que se lo dé ([entrega](docs/entrega.md)).

## Estructura

```
shared/         el juego entero, común a Android, iOS y el escritorio (Kotlin Multiplatform + Compose Multiplatform)
  src/commonMain/kotlin/com/fanguskdev/mymonsterbruno/
    game/         reglas: Bruno, la partida guardada, los minutos del día, las rondas y el trazado
    audio/        la voz, los efectos y el micrófono: qué se dice, cuándo y cómo
    storage/      lo que se guarda en el dispositivo y la fecha de hoy
    ui/           el escenario, el estado del juego, el dibujo (art/) y cada pantalla (screens/)
  src/commonMain/composeResources/   las voces, los efectos y la letra Andika
  src/androidMain, src/iosMain, src/jvmMain   lo que es de cada plataforma
androidApp/     la app de Android
iosApp/         la app de iOS (proyecto de Xcode)
desktopApp/     herramienta de desarrollo: una ventana para probar rápido; no se entrega
voces/          el guion de todo lo que se dice y la huella de cada voz generada
tools/          las voces (voces.py), los efectos (sonidos.py) y los recorridos en el iPhone simulado (conductor-ios/)
docs/           diseño, investigación, decisiones (ADR), proceso, pruebas y el arnés
scripts/        la puerta de calidad
licencias/      la licencia de la letra Andika
```

Las versiones viven en `gradle/libs.versions.toml` y son las de CaroFarty: Kotlin 2.4.21, Compose Multiplatform 1.12.2,
el plugin de Android 9.5.0 y Gradle 9.8.2.

## Cómo se ejecuta

- Android: `./gradlew :androidApp:installDebug` con un emulador o un dispositivo conectado (para dárselo, mejor
  `:androidApp:installRelease`, optimizada con R8 y firmada con la clave de depuración).
- iOS: abrir `iosApp/iosApp.xcodeproj` en Xcode y ejecutar el esquema `iosApp`, o por consola
  `xcodebuild -project iosApp/iosApp.xcodeproj -scheme iosApp -destination 'platform=iOS Simulator,name=iPad (A16)' build`.
- Escritorio: `./gradlew :desktopApp:run`.
- Para dárselo, en su iPhone o en un Android, sin publicarlo en las tiendas: [entrega](docs/entrega.md).

La versión de depuración puede arrancar en silencio; cómo, en [pruebas](docs/pruebas.md). La zona de mayores se abre
manteniendo el dedo en el candado de la esquina durante 2,5 segundos.

## Cómo se comprueba

```bash
bash scripts/verificar.sh
```

Termina en «La puerta pasa» si pasa todo: sin `!!`, las voces y los efectos al día con su guion, ktlint, sin avisos del
compilador, pruebas en la JVM, en Android y en el simulador de iOS, lint de Android, los dos APK y la app de iOS
([proceso](docs/proceso.md)).

## Las voces y los efectos

Todo lo que se dice está en [`voces/guion.tsv`](voces/guion.tsv). Las voces salen de la voz del sistema del Mac
([ADR 0004](docs/adr/0004-voces-provisionales.md)):

```bash
python3 tools/voces.py
```

```bash
python3 tools/sonidos.py
```

La primera genera las frases nuevas o cambiadas y necesita macOS, y Xcode para alargar las de Bruno. La segunda, los
efectos.

## El arnés

El proyecto usa el Mayeutic Harness para el desarrollo, como los demás de Fangusk: dónde está y qué sigue, en
[docs/harness/STATUS.md](docs/harness/STATUS.md). La CI `mayeutic` (`.github/workflows/mayeutic.yml`) corre la misma puerta
sin la parte de iOS cuando el repositorio esté en GitHub. Lo que el arnés comprueba del proyecto (pide PyYAML):

```bash
python3 ../mayeutic-harness/tools/proyecto.py comprobar .
```

## Licencias

El código es de Fangusk. Bruno, los dibujos, los efectos y el guion se hacen desde cero para este juego. La letra Andika
es de SIL International, con licencia SIL Open Font License ([licencias/](licencias/Andika-OFL.txt)). Las voces
provisionales salen de la voz «Mónica» de macOS ([ADR 0004](docs/adr/0004-voces-provisionales.md)).
