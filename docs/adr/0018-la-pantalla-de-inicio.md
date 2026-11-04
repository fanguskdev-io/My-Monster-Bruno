# ADR-0018 · La pantalla de inicio, como la de CaroFarty

| | |
|---|---|
| **Estado** | aceptada: «Añade tambien una pantalla de Splash para cuando se inicia el juego, queda un poco soso iniciar el juego como tal, básate en como lo hace CaroFarty» (Fangusk, 2026-11-04), y el título: «Sustituye: Mi monstruo Bruno por: Mi amigo Bruno» (Fangusk, 2026-11-04). Propuesta en los detalles: lo que sale en ella y que no tenga nada que leer |
| **Fecha** | 2026-11-04 · **Fase:** P0 · Borrador anticipado, sin ninguna puerta confirmada · **Hito:** M5 |
| **Propone · decide** | Fangusk · agente en los detalles |
| **Reversibilidad** | alta: es una pantalla aparte, delante de todo |

## Contexto

Al abrir, el juego iba derecho al huevo, al cuarto de Bruno o a la cama. CaroFarty abre con una presentación (en sus
`docs/pantallas.md`): el título animado sobre un cielo con nubes que pasan, el subtítulo en una cinta, «TOCA PARA
EMPEZAR» parpadeando y, abajo, su escenario pasando con Caro corriendo. Un toque en cualquier sitio suena y entra en el
juego.

## Decisión

- **El mundo de Bruno visto de lado**, como el escenario de CaroFarty: el cielo con el sol y sus nubes, que pasan
  despacio; el mar lila del mapa, con sus olas; y en el horizonte, pasando hacia la izquierda, las islas del juego con sus
  señales (las letras, los globos, las formas, las caritas y el libro). Bruno se mece en su barca, con lo que lleva
  puesto; antes de nacer, en la barca va su huevo.
- **El título**: «Mi amigo» y, debajo y en grande, BRUNO, cada letra de un color, que caen a su sitio una tras otra y
  después se mecen.
- **Nada que leer**: en lugar de «TOCA PARA EMPEZAR», cuando las letras han caído suena el efecto de inicio y la voz dice
  «¡Mi amigo Bruno!» y «¡Toca para empezar!», mientras late un botón verde de jugar. Un toque en cualquier sitio suena,
  la pantalla se funde en blanco y el juego se abre donde estaba: el huevo, el cuarto o la cama, si ya no quedan minutos.
- **Cada vez que se abre el juego**, y no gasta minutos del día. El botón de atrás de Android sale del juego, como en el
  cuarto.

## Alternativas consideradas

| Alternativa | A favor | Por qué se descarta |
|---|---|---|
| «TOCA PARA EMPEZAR» escrito y parpadeando, como en CaroFarty | Igual que CaroFarty | Ella aún no lee (regla 2): lo dice la voz y lo enseña el botón |
| Que pase sola al cabo de unos segundos | No hace falta tocar | La sacaría mientras mira; CaroFarty también espera al toque |
| Solo la primera vez | Se llega antes a jugar | Abrir el juego cada día con su título es parte del ritual; un toque la pasa |

## Consecuencias

- Dos frases más en el guion y un efecto más, el de inicio.
- Un toque más cada vez que se abre el juego.
