# ADR-0029 · Sumas hasta 3, en la isla de los números

| | |
|---|---|
| **Estado** | aceptada: a la propuesta del agente («En la isla de los números: «Bruno tiene una fresa y le das dos más. ¿Cuántas tiene?», con los dibujos para contarlas (idea 1.6)»), «Sumas hasta 3» (Fangusk, 2026-11-07, elegida entre opciones). Propuesta en los detalles: que se turne con la cuenta atrás y la recta, darle las fresas, la tarjeta de la suma, los números para elegir y contarlas todas al equivocarse |
| **Fecha** | 2026-11-07 · **Fase:** P0 · Borrador anticipado, sin ninguna puerta confirmada · **Hito:** M5 |
| **Propone · decide** | agente (Claude Code, Opus 5.5) · Fangusk |
| **Reversibilidad** | alta: sin ella, la isla de los números vuelve a turnar solo la cuenta atrás y la recta |

## Contexto

Fangusk pidió seguir y eligió cuatro piezas; esta es la segunda, la idea 1.6. Las sumas con dibujos hasta 3 son de lo que
se trabaja a los 4 años ([investigación §2](../investigacion/investigacion.md#2-qué-se-trabaja-en-el-cole-a-los-4-años)),
y el juego solo contaba. Las fresas de la isla de los números ya son lo que se suma.

## Decisión

- **Dónde**: en la isla de los números, después de las fresas, por turnos con la cuenta atrás y la recta: el primer día
  que va, la cuenta atrás; el siguiente día que va, la recta; el siguiente, las sumas; y vuelta a empezar. Así la isla
  dura lo mismo ([ADR 0026](0026-el-anterior-y-el-siguiente.md)).
- **Cómo**: Bruno tiene una o dos fresas en su plato, que aparecen una a una: «Bruno tiene una fresa». El narrador:
  «Dale dos más», y ella se las da del arbusto, tocándolas, y se cuentan al llegar: «Una», «Dos». Arriba, una tarjeta
  enseña la suma con fresas: las que tenía, un más, las que le da (en blanco hasta que se las da), un igual y una
  interrogación. Luego: «¿Cuántas fresas tiene ahora Bruno?», y ella toca un número, del 1 al 3, cada uno con sus
  puntos. Si es: «¡Sí! Una y dos, tres», el número sale en la tarjeta y Bruno se las come. Si no: «Vamos a contarlas
  todas», y el narrador las cuenta mientras cada fresa del plato brilla; el número bueno llama y lo vuelve a preguntar.
- **Las sumas**: las tres que hay hasta 3, una y una, una y dos, y dos y una, una vez cada una, en cualquier orden.
- **Lo que cuenta**: en la zona de mayores, cuántas veces ha resuelto cada suma.

## Alternativas consideradas

| Alternativa | A favor | Por qué se descarta |
|---|---|---|
| Una actividad más en la isla, cada vez | Más práctica | La isla se alargaría con tres actividades |
| Sumas hasta 5 | Más variedad | Lo que se trabaja a los 4 años es hasta 3 |
| Que diga ella el número, sin tarjetas | Más natural | Habría que reconocer su voz; tocar el número es lo mismo para sumar |

## Consecuencias

- 11 frases más en el guion: la presentación, lo que tiene Bruno, cuántas darle, la pregunta, cada suma, el contarlas
  todas y el gracias; los números y el «¡Ñam!» de las fresas ya estaban.
- La cuenta atrás y la recta salen ahora un día de cada tres que va a la isla, en vez de uno de cada dos.
