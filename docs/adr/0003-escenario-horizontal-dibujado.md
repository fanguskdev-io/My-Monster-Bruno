# ADR-0003 · En horizontal, sobre un escenario de 800 × 400 que se adapta a cada pantalla

| | |
|---|---|
| **Estado** | propuesta: la tomó el agente, siguiendo la nota de la investigación «Pantalla en horizontal, que se adapte a móvil y a tablet» |
| **Fecha** | 2026-11-02 · **Fase:** P0 · Borrador anticipado, sin ninguna puerta confirmada |
| **Propone · decide** | agente (Claude Code, Opus 5.5) · Fangusk |
| **Reversibilidad** | media: las posiciones de cada pantalla están en unidades del escenario; girarlo a vertical es recolocarlas todas |

## Contexto

A los 4 años se juega mucho en tableta, pero el juego tiene que ir también en un móvil. Las proporciones van de 4:3 en un
iPad a más de 2:1 en los móviles. Los dedos de esa edad necesitan botones grandes y zonas de soltar generosas
([investigación §3](../investigacion/investigacion.md#3-qué-dice-la-investigación-sobre-diseñar-para-esta-edad)).

## Decisión

- **Solo en horizontal**: `sensorLandscape` en Android, y en iOS solo las dos orientaciones apaisadas, también en iPad. La
  pantalla ocupa todo, sin barras del sistema.
- **Un escenario de 800 × 400 unidades** (2:1, la forma de un móvil tumbado). Cada pantalla coloca sus cosas en esas
  unidades y el escenario se escala para caber entero y centrado (`ui/Stage.kt`). El fondo se dibuja más allá, hasta los
  bordes de la pantalla, así que no quedan franjas en ninguna proporción. Los dedos se traducen a unidades del escenario.
- **Los botones de las esquinas** (la casa, el altavoz y el candado) se pegan a las esquinas visibles, no a las del
  escenario. En una tableta, por tanto, quedan en el borde de verdad.
- **Cada botón acepta el dedo 16 unidades más allá de su borde**: los dedos pequeños fallan por poco
  (`TOUCH_SLACK` en `ui/screens/Common.kt`).

## Alternativas consideradas

| Alternativa | A favor | Por qué se descarta |
|---|---|---|
| Diseñar cada pantalla con el layout de Compose | Se adapta solo | Un juego con personajes que se mueven y cosas que se arrastran necesita posiciones fijas; el layout es para la zona de mayores |
| Un escenario 4:3 | Llena la tableta | En un móvil quedaría pequeño: la altura es lo que más falta en horizontal |
| Vertical | Se sujeta con una mano | El mundo de Bruno, el mapa y el plato al lado de Bruno piden anchura |

## Consecuencias

- Las capturas de las pruebas se hacen en dos proporciones, móvil (2,17:1) y tableta (4:3) ([pruebas](../pruebas.md)).
- Con un iPad en vertical, iPadOS 26 muestra el juego apaisado y pequeño, con franjas negras arriba y abajo (observado en
  el simulador): se juega girándolo.
- El lint de Android avisa (`DiscouragedApi`) de que Android 16 y 17 ignoran la orientación fija en pantallas grandes.
  Los juegos quedan fuera de ese cambio si lo declaran, y el manifiesto lo hace con `android:appCategory="game"`
  ([Android 16](https://developer.android.com/about/versions/16/behavior-changes-16),
  [Android 17](https://developer.android.com/about/versions/17/changes/ff-restrictions-ignored)). El aviso se queda, porque
  el lint no mira la categoría.
