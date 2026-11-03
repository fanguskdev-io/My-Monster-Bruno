# ADR-0007 · La isla de los colores: globos que pide Bruno y series de globos

| | |
|---|---|
| **Estado** | aceptada en qué isla va primero y cómo se juega: «Más islas primero» y, de las cuatro que le propuso el agente, «Colores», con su descripción: «Bruno pide un color (“¡Quiero el globo rojo!”) y después series: rojo, azul, rojo… ¿cuál va ahora?» (Fangusk, 2026-11-03, elegida entre opciones). Propuesta en los detalles: los seis colores, las rondas, la pista y cómo se ve |
| **Fecha** | 2026-11-03 · **Fase:** P0 · Borrador anticipado, sin ninguna puerta confirmada · **Hito:** M3 |
| **Propone · decide** | agente (Claude Code, Opus 5.5) · Fangusk |
| **Reversibilidad** | alta: es una isla más; quitarla deja el mapa como estaba |

## Contexto

La primera versión tiene dos islas, la de las vocales y la de los números, de unos tres minutos cada una. Con diez minutos
al día, la niña las ve enteras el primer día. La propuesta 3 dejaba para después las consonantes, los números hasta 10,
las formas, los colores y las series, y las emociones. En la maqueta, el mapa tenía islas de formas, colores y emociones,
las mismas que el juego dibuja hoy «muy lejos» ([página de diseño](../diseno.md)). En el aula de 4 años se trabajan los
colores y las series de tres colores ([investigación §2](../investigacion/investigacion.md#2-qué-se-trabaja-en-el-cole-a-los-4-años)).

## Decisión

- **La isla de los colores** ocupa en el mapa el sitio de la isla lejana de abajo a la derecha, con un globo de cada color
  atado a la hierba. Tiene dos juegos, como la de las vocales, y al terminarlos hay regalo, como en todas.
- **Los globos.** Bruno pide un globo de un color, «¡Quiero el globo rojo!», y hay tres. Ella le da uno arrastrándolo hasta
  él o tocándolo. El que acierta se queda atado en su mano, así que reúne un ramo que crece ronda a ronda. Si le da otro
  color, Bruno pone cara rara, dice de qué color es y vuelve a pedir el suyo: «Mmm... ese es el azul. ¡Yo quiero el
  rojo!». A partir de ese momento piensa en su globo en un bocadillo: una pista en vez de la respuesta. Son cinco rondas,
  con cinco colores distintos.
- **Las series.** Hay una fila de globos atados a una cuerda, como rojo, azul, rojo, azul, y un hueco al final. El
  narrador la lee mientras cada globo salta al oír su nombre, y pregunta «¿Cuál va ahora?». Ella elige entre tres globos,
  arrastrándolo al hueco o tocándolo. Si no es ese, Bruno dice de qué color es y el narrador vuelve a leer la fila: «Mira
  otra vez». Son cuatro series: dos de dos colores y dos de tres, como en el cole. La fila enseña el patrón entero y uno o
  dos globos más, así que el hueco no siempre es el primer color.
- **Seis colores**: rojo, amarillo, azul, verde, naranja y morado, bien distintos entre sí.
- **Lo que ha practicado**, en la zona de mayores: los globos acertados por color y las series terminadas.
- **La misión para casa** de la isla es la de buscar algo amarillo.

## Alternativas consideradas

| Alternativa | A favor | Por qué se descarta |
|---|---|---|
| Formas, emociones o ampliar las islas que hay | También estaban en la propuesta | Fangusk eligió los colores primero; las otras siguen en el mapa, lejos |
| Que Bruno enseñe el color en el bocadillo desde el principio | Más fácil | Se convierte en emparejar colores sin oír su nombre; la pista llega solo después de un fallo |
| Series con más tipos de patrón (AAB, ABB) | Más variedad | A los 4 años, primero las de dos y tres colores alternos, que son las del aula; se pueden añadir |

## Consecuencias

- Son 37 frases más en el guion (116 en total), con la misma voz provisional ([ADR 0004](0004-voces-provisionales.md)).
- Una partida de 10 minutos ya no lo ve todo el primer día: hay tres islas de unos tres o cuatro minutos cada una.
- Quedan en el mapa, lejos, dos islas para lo que Fangusk elija después.
