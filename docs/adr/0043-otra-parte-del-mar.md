# ADR-0043 · Otra parte del mar, para más islas

| | |
|---|---|
| **Estado** | aceptada: al pedir «Vale, cerremos el backlog y continuemos», la idea 5.7 de la batería («Con seis islas, el mapa está lleno: antes de una isla nueva hace falta otra parte del mar, a la que se llega en la barca»), Fangusk, 2026-11-09. Propuesta en los detalles: el botón de navegar junto a la barca, cómo se pasa de un mar a otro y qué hay en el nuevo |
| **Fecha** | 2026-11-09 · **Fase:** P0 · Borrador anticipado, sin ninguna puerta confirmada · **Hito:** M5 |
| **Propone · decide** | agente (Claude Code, Opus 5.5) · Fangusk |
| **Reversibilidad** | media: el mar de siempre no cambia; quitar el otro es quitar sus islas |

## Contexto

Las seis islas llenan el mapa ([diseño](../diseno.md), «El mapa»), y en los bordes del medio no se puede poner nada:
con el iPhone girado, la Dynamic Island los tapa. Para la isla de la casa ([ADR 0044](0044-la-isla-de-la-casa.md))
hacía falta sitio. Fangusk pidió cerrar la batería de [ideas](../ideas.md), y esta es la 5.7.

## Decisión

- **Dos partes del mar**: la de siempre, con sus seis islas, y otra al lado, con la isla de la casa y sitio para las
  que vengan.
- **Se pasa navegando**: junto a la barca de Bruno, abajo, un botón redondo con una flecha y una casita lleva al otro
  lado: «¡Vamos al otro lado del mar!». El mar se desliza y la barca se queda en medio, meciéndose, como si navegara.
  Desde el otro lado, el mismo botón, con la flecha al revés, vuelve: «¡Volvemos a las otras islas!».
- **Se vuelve a donde se estaba**: al terminar una isla, o al salir de ella con la casa, el mapa abre en la parte del mar
  de esa isla.
- **La isla sugerida** ([ADR 0035](0035-bruno-sugiere-una-isla.md)) puede estar al otro lado: entonces las ondas
  rodean el botón de navegar, y Bruno lo mira.
- Si los mayores quitan todas las islas de una de las dos partes ([ADR 0041](0041-elegir-que-islas-y-letras-salen.md)),
  el botón no sale, y el mapa abre en la parte que tiene islas.

## Alternativas consideradas

| Alternativa | A favor | Por qué se descarta |
|---|---|---|
| Hacer las islas más pequeñas | Todo a la vista | Más pequeñas son más difíciles de tocar a los 4 años |
| Un mapa que se arrastra con el dedo | Libre | Arrastrar el mar se confunde con tocar una isla |
| El botón en el borde de la derecha | Se entiende «hacia allá» | En el iPhone girado, la Dynamic Island lo taparía |

## Consecuencias

- Dos frases más en el guion, la de ir y la de volver.
- El mapa recuerda en qué parte del mar está mientras el juego está abierto.
