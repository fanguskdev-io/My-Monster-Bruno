# ADR-0001 · Kotlin Multiplatform y Compose Multiplatform, como CaroFarty

| | |
|---|---|
| **Estado** | aceptada en el qué: «basate en como se está cronstruyendo CaroFarty y como se integra con Mayeutic usando KMP» (Fangusk, 2026-11-02), y creó el proyecto con el asistente de Kotlin Multiplatform de Android Studio. Propuesta en el cómo: dibujar con vectores en un `Canvas`, sin motor ni imágenes |
| **Fecha** | 2026-11-02 · **Fase:** P0 · Borrador anticipado, sin ninguna puerta confirmada |
| **Propone · decide** | agente (Claude Code, Opus 5.5) · Fangusk |
| **Reversibilidad** | baja una vez escrito el juego: cambiar de plataforma es reescribirlo |

## Contexto

El juego es para Android e iOS ([página de diseño](../diseno.md)). Necesita pantallas que se tocan y se arrastran, un
personaje animado, voz pregrabada y efectos, y guardar una partida en el dispositivo. No necesita física, 3D, red ni
desplazamiento rápido.

Fangusk pidió construirlo como CaroFarty y creó el proyecto, MyMonsterBruno, con el asistente de Kotlin Multiplatform
(Compose Multiplatform para Android, iOS y escritorio). Es el mismo camino que siguen CaroFarty, BakiFace y MyLetters2.0.

## Decisión

- **Kotlin Multiplatform y Compose Multiplatform**, con todo el juego en `shared/commonMain`: las reglas, el dibujo, las
  pantallas y la voz. El código de cada plataforma se limita a lo suyo: reproducir el sonido (SoundPool en Android,
  AVAudioPlayer en iOS, Java Sound en el escritorio), guardar la partida (SharedPreferences, NSUserDefaults, Preferences),
  el botón de atrás de Android y arrancar la app.
- **Las mismas versiones y la misma puerta de calidad que CaroFarty**: Kotlin 2.4.21, Compose Multiplatform 1.12.2, el
  plugin de Android 9.5.0 y Gradle 9.8.2; ktlint, avisos del compilador como errores, nada de `!!` y
  `scripts/verificar.sh` ([proceso](../proceso.md)).
- **Sin motor**: cada pantalla es un `Canvas` que dibuja con vectores (elipses, curvas y trazos) sobre un escenario fijo
  ([ADR 0003](0003-escenario-horizontal-dibujado.md)), con un reloj para las animaciones y un gestor de toques común. La
  zona de mayores, la única con texto, usa los componentes de `foundation`.
- **Sin bibliotecas de terceros** fuera de Kotlin, Compose, las corrutinas y `kotlinx.serialization`, que guarda la
  partida en JSON.
- **El escritorio es una herramienta**: una ventana para probar rápido y las capturas de cada pantalla en las pruebas. No
  se entrega.

## Alternativas consideradas

| Alternativa | A favor | Por qué se descarta |
|---|---|---|
| El motor 2D de píxeles de CaroFarty | Ya existe y está probado | Bruno no es pixel art: se dibuja con curvas suaves que tienen que verse bien en un móvil y en una tableta grande |
| Imágenes pintadas (PNG o SVG) | Más detalle | Cada cara y cada postura de Bruno sería un dibujo más; con vectores, una boca nueva es una función, y crecer o cambiar de color no cuesta nada |
| Godot, Unity o Flutter | Herramientas maduras para juegos y animación | Fuera del ecosistema de Fangusk y contra lo que pidió: «como se está construyendo CaroFarty» |

## Consecuencias y riesgos

- **El arte es código**: los cambios de aspecto se hacen en `ui/art/` y se comprueban con las capturas de las pruebas
  ([pruebas](../pruebas.md)).
- **El sonido es pregrabado**, no sintetizado en tiempo real como la música de CaroFarty: menos riesgo de cortes.
- **iOS** se compila y se prueba en el simulador. Instalarlo en un iPhone o un iPad suyo pide la firma de Apple (R-04 de
  [riesgos](../harness/riesgos.md)).
