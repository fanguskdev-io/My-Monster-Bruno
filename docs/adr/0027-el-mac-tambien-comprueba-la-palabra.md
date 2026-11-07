# ADR-0027 · El Mac también comprueba la palabra de «Habla con Bruno»

| | |
|---|---|
| **Estado** | aceptada: al probarlo en el Mac, «El juego de decir la palabra sigue sin funcionar, si bruno me pide que diga Globo y digo otra palabra que no es, sigue dandolo por bueno» y, a la propuesta de que la versión del Mac compruebe la palabra con el reconocedor de español de Apple del propio Mac, sin conexión, «Sí, que compruebe» (Fangusk, 2026-11-07). Propuesta en los detalles: cómo lo usa el juego, el programa que lo hace y qué pasa si no está |
| **Fecha** | 2026-11-07 · **Fase:** P0 · Borrador anticipado, sin ninguna puerta confirmada · **Hito:** M2 |
| **Propone · decide** | agente (Claude Code, Opus 5.5) · Fangusk |
| **Reversibilidad** | alta: sin el programa, el Mac vuelve a no comprobar, como decía el [ADR 0015](0015-bruno-comprueba-la-palabra.md) |

## Contexto

El [ADR 0015](0015-bruno-comprueba-la-palabra.md) hizo que el iPhone y los Android con reconocedor sin conexión
comprobaran la palabra, pero no el Mac, que entonces no tenía un reconocedor que el juego pudiera usar. Fangusk prueba
el juego en el Mac y vio que seguía dando por buena cualquier palabra. Este Mac (macOS 26) tiene el reconocedor de
español de España de Apple y puede usarlo sin conexión, el mismo tipo de reconocedor que usa el juego en el iPhone.

## Decisión

- **La versión del Mac comprueba la palabra como el iPhone**: con el reconocimiento de voz de Apple en el propio Mac,
  nunca por internet, la misma indulgencia («mapoza» es «mariposa») y lo mismo si es otra: «Huum... Eso no es globo.
  ¿Me dices globo?», y a la segunda Bruno la dice él.
- **Lo hace un programa pequeño, `tools/reconocer`**, porque la versión del Mac no puede llamar al reconocedor de Apple
  por sí misma. Gradle lo compila al abrir el juego (`./gradlew :desktopApp:run`) y el juego le pasa su voz en un WAV
  temporal, que se borra en cuanto contesta.
- **La primera vez, el Mac pide el permiso de reconocimiento de voz** en nombre de ese programa, «Mi amigo Bruno». macOS
  pide el permiso en nombre de quien lanza el programa y, si ese no explica para qué lo quiere, lo cierra en vez de
  preguntar; por eso el programa se vuelve a lanzar a sí mismo como responsable de su permiso, como hace Qt Creator.
- **Si no se puede** (otro ordenador, sin Xcode, sin permiso o el juego arrancado en silencio), no comprueba y Bruno da
  por buena lo que oye, como antes.

## Alternativas consideradas

| Alternativa | A favor | Por qué se descarta |
|---|---|---|
| Dejarlo como estaba | Nada que cambiar | Fangusk prueba en el Mac y quiere ver que comprueba |
| Probar la comprobación solo en un teléfono Android | El reconocedor del teléfono | Habría que usar sus teléfonos, y no todos los Android lo tienen |
| Un reconocedor de otro, como Vosk | No pide permisos | Una biblioteca y un modelo de español de decenas de megas solo para probar en el Mac |

## Consecuencias

- **El Mac se parece más a su iPhone** al probar «Habla con Bruno», pero una voz de adulto se reconoce mejor que la de una
  niña de 4 años: la prueba de verdad sigue siendo verla jugar en su iPhone ([R-08](../harness/riesgos.md)).
- **Al cambiar el programa, macOS vuelve a pedir el permiso**: el permiso va con el programa tal como se compiló.
- El programa usa una llamada de macOS que no es pública (`responsibility_spawnattrs_setdisclaim`) para ser responsable
  de su permiso; es solo para probar en el Mac y no va en el juego de Android ni en el del iPhone.
