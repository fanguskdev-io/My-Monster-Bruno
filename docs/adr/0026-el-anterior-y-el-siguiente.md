# ADR-0026 · El anterior y el siguiente, en la isla de los números

| | |
|---|---|
| **Estado** | aceptada: a la propuesta del agente («Con la recta numérica de su clase, en la isla de los números, alternándose cada día con la cuenta atrás: Bruno salta a una casilla del 0 al 10 y pregunta qué número va antes o después; ella toca la casilla. Cinco rondas»), «El anterior y el siguiente» (Fangusk, 2026-11-06, elegida entre opciones). Propuesta en los detalles: cómo se alternan, el bocadillo de la pregunta, la ayuda al equivocarse y lo que cuenta |
| **Fecha** | 2026-11-06 · **Fase:** P0 · Borrador anticipado, sin ninguna puerta confirmada · **Hito:** M5 |
| **Propone · decide** | agente (Claude Code, Opus 5.5) · Fangusk |
| **Reversibilidad** | alta: sin ella, la isla de los números vuelve a tener la cuenta atrás todos los días. Desde el [ADR 0029](0029-sumas-hasta-3.md), la cuenta atrás, la recta y las sumas se turnan las tres |

## Contexto

Fangusk pidió seguir trabajando y, de las siguientes de la batería de [ideas](../ideas.md), eligió «El anterior y el
siguiente» (la 1.4), la última de las cuatro piezas. En el aula de 4 años se cuenta con la recta numérica y se trabaja el
anterior y el siguiente ([investigación §2](../investigacion/investigacion.md#2-qué-se-trabaja-en-el-cole-a-los-4-años));
el juego solo contaba hacia delante, con las fresas, y hacia atrás, con la cuenta atrás.

## Decisión

- **Dónde**: la segunda actividad de la isla de los números, después de las fresas, y por turnos con la cuenta atrás: un
  día que va a la isla le toca una y el siguiente día que va, la otra. Cuentan los días que va, no el calendario, para que
  se alternen aunque no juegue todos los días; el mismo día es siempre la misma. Después, el regalo de la isla.
- **La recta**: once casillas en el césped, del 0 al 10, de colores y con su número grande, como la de la clase.
- **Cómo**: Bruno salta a una casilla y pregunta «¿Qué número va antes del cinco?» o «¿Qué número va después del cinco?».
  Encima de él, un bocadillo con dos casillas, como en la recta: la suya con su número y, delante o detrás, una con una
  interrogación. Ella toca la casilla: si es, se enciende y el narrador lo dice entero, «¡Sí! Antes del cinco va el
  cuatro»; si no, se mueve un poco, el narrador dice cuál ha tocado («Ese es el siete») y «Mira bien: está justo al lado
  de Bruno», las dos casillas de al lado de Bruno brillan y él vuelve a preguntar. Equivocarse no castiga.
- **Cinco rondas**, con «antes» y «después» mezclados, al menos dos de cada, sin repetir pregunta ni casilla dos veces
  seguidas. La primera vez el narrador presenta la recta: «¡Mira, la recta de los números, como la de tu clase! Bruno va
  a saltar por ella». Al acabar, Bruno da las gracias.
- **Lo que cuenta**: en la zona de mayores, cuántas veces ha encontrado el número de antes y el de después.

## Alternativas consideradas

| Alternativa | A favor | Por qué se descarta |
|---|---|---|
| Las dos cada vez, una detrás de otra | Más práctica | La isla se alargaría con tres actividades |
| Alternar por el calendario, días pares e impares | Más sencillo | Si juega un día sí y otro no, le tocaría siempre la misma |
| Sin el bocadillo | Más limpio | «Antes» y «después» cuestan a los 4 años: el bocadillo le dice hacia dónde mirar, como la recta del cole |
| Llevar a Bruno a la casilla arrastrándolo | Más juego | Tocar es más fácil y es lo mismo para encontrar el número |

## Consecuencias

- 54 frases más en el guion: las veinte preguntas de Bruno, las veinte respuestas del narrador, «Ese es el...» de los
  once números, la presentación, la ayuda y el gracias; y un sonido nuevo, el del salto.
- La isla de los números dura lo mismo que antes: un día la cuenta atrás y otro la recta.
- La partida guarda el último día que fue a la isla y cuál le tocó.
