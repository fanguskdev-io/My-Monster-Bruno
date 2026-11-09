# ADR-0035 · Bruno sugiere una isla

| | |
|---|---|
| **Estado** | aceptada: al pedir «Vale, cerremos el backlog y continuemos», la idea 2.2 de la batería («Al llegar al mapa, Bruno señala la isla que menos ha visitado, sin obligar»), Fangusk, 2026-11-09. Propuesta en los detalles: una vez cada vez que se abre el juego, la isla que menos veces ha terminado, la frase de Bruno y las ondas que la rodean |
| **Fecha** | 2026-11-09 · **Fase:** P0 · Borrador anticipado, sin ninguna puerta confirmada · **Hito:** M5 |
| **Propone · decide** | agente (Claude Code, Opus 5.5) · Fangusk |
| **Reversibilidad** | alta: sin la sugerencia, el mapa vuelve a preguntar solo «¿A qué isla vamos hoy?» |

## Contexto

A los 4 años se vuelve a lo conocido, y está bien; pero hay seis islas, y alguna se queda sin visitar. Fangusk pidió
cerrar la batería de [ideas](../ideas.md), y esta es la 2.2.

## Decisión

- **Cuándo**: la primera vez que llega al mapa cada vez que se abre el juego. Las demás veces, el mapa es como antes.
- **Cuál**: la isla que menos veces ha terminado. Si hay varias, cada día una distinta de esas.
- **Cómo**: después de «¿A qué isla vamos hoy?», Bruno dice «¡A mí me apetece ir a la isla de las formas! ¿Y a ti?», la
  mira desde su barca y alrededor de la isla salen dos ondas suaves en el agua, despacio, mientras están en el mapa.
- **Sin obligar**: todas las islas se pueden tocar igual, y no lo repite ni insiste.

## Alternativas consideradas

| Alternativa | A favor | Por qué se descarta |
|---|---|---|
| Sugerirla cada vez que llega al mapa | La ayuda siempre a la vista | Al volver de cada isla sería repetirse; parecería que obliga |
| Contar las veces que entra, no las que termina | Es lo que dice la idea | Hay que guardar algo nuevo; y terminar es lo que cuenta para la bandera |
| Que la isla salte o se mueva | Se ve más | Una isla que salta pide que se toque: sería obligar |
| El aro que llama, el de «toca aquí» | Ya lo conoce | Es el de lo que hay que tocar; las ondas solo enseñan |

## Consecuencias

- 6 frases más en el guion, una por isla.
