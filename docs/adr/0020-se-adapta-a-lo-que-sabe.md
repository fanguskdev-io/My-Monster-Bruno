# ADR-0020 · Los juegos se adaptan a lo que sabe

| | |
|---|---|
| **Estado** | aceptada: a la propuesta del agente («En Comeletras, los globos, las caritas de las emociones y las cartas de la isla de las palabras: si ha acertado a la primera las últimas cinco rondas de ese juego, la siguiente trae cuatro para elegir en vez de tres; si ha fallado más de la mitad, dos. Nunca se lo dice: sin niveles, puntos ni estrellas. En la zona de mayores se ve con cuántas juega cada uno»), «Que se adapte» (Fangusk, 2026-11-05, elegida entre opciones). Propuesta en los detalles: cómo caben cuatro en cada pantalla y que, hasta jugar cinco rondas, sean tres |
| **Fecha** | 2026-11-05 · **Fase:** P0 · Borrador anticipado, sin ninguna puerta confirmada · **Hito:** M5 |
| **Propone · decide** | agente (Claude Code, Opus 5.5) · Fangusk |
| **Reversibilidad** | alta: con tres siempre, el juego queda como antes |

## Contexto

Fangusk pidió seguir con el desarrollo y, de las siguientes de la batería de [ideas](../ideas.md), eligió «Que se adapte a
lo que sabe» (la 2.1), para que el juego le dure mientras crece. Hasta ahora, cada ronda de cuatro juegos traía siempre
tres para elegir: tres galletas en Comeletras, tres globos, tres caritas en la isla de las emociones y tres cartas en la
de las palabras.

## Decisión

- **Los cuatro juegos que se adaptan**: Comeletras, los globos de la isla de los colores, las caritas de la isla de las
  emociones y las cartas de la isla de las palabras. Las series de globos, los trazos, las fresas y la casa no, porque no
  son elegir entre unos pocos.
- **Cuántas**: tres para empezar. Si las últimas cinco rondas de ese juego las acertó todas a la primera, la siguiente
  trae cuatro; si en más de la mitad de las cinco se equivocó antes de acertar, dos. Cuenta cada juego por su lado, y se
  mira al empezar cada ronda, así que cambia en la misma visita.
- **Nunca se lo dice**: ni niveles, ni puntos, ni estrellas, ni una frase distinta. La respuesta está siempre, en un sitio
  cualquiera.
- **Cómo caben cuatro**: más juntas y un poco más pequeñas, sin bajar de lo que se toca bien con un dedo de 4 años (las
  galletas pasan de 52 a 46 unidades de radio); con dos, más separadas y del tamaño de siempre.
- **En la zona de mayores**, la sección «Se adapta a lo que sabe» explica la regla y dice entre cuántas elige ahora en cada
  juego. Se guarda solo en el aparato, y «Volver a empezar» lo devuelve a tres.

## Alternativas consideradas

| Alternativa | A favor | Por qué se descarta |
|---|---|---|
| Hacer otra de las ideas: la cuenta atrás, rimas con Bruno o que Bruno sugiera una isla | Más juego | Fangusk eligió que se adapte |
| Cambiar también lo que se pide (letras más difíciles antes) | Más reto | Las letras ya entran por pasos, por lo que ha practicado (ADR 0009 y 0014) |
| Decírselo: «¡Ahora con cuatro!» | Lo celebra | Sería un nivel, y las reglas los evitan; Fangusk eligió que no se lo diga |

## Consecuencias

- Cada ronda se prepara con cuatro opciones y, al empezar, se queda con las que tocan.
- Un juego que le cuesta se le hace más fácil sin que lo note, y uno que domina, un poco más difícil.
- Sin frases nuevas en el guion.
