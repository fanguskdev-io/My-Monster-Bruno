# ADR-0041 · Elegir qué islas y qué letras salen

| | |
|---|---|
| **Estado** | aceptada: al pedir «Vale, cerremos el backlog y continuemos», la idea 4.2 de la batería («Por si aún no han visto la M en clase, o para quitar una isla que ya domina»), Fangusk, 2026-11-09. Propuesta en los detalles: solo las consonantes, al menos una isla, y qué pasa en el mapa y en la isla de las letras |
| **Fecha** | 2026-11-09 · **Fase:** P0 · Borrador anticipado, sin ninguna puerta confirmada · **Hito:** M5 |
| **Propone · decide** | agente (Claude Code, Opus 5.5) · Fangusk |
| **Reversibilidad** | alta: con todo marcado, el juego es el de antes |

## Contexto

Las consonantes entran por pasos, la M, la P, la L y la S primero ([ADR 0009](0009-mas-letras-y-numeros-hasta-el-diez.md)), y las seis islas
están siempre en el mapa. En clase pueden ir en otro orden, y una isla que ya domina puede sobrar un tiempo. Fangusk
pidió cerrar la batería de [ideas](../ideas.md), y esta es la 4.2.

## Decisión

- **En la zona de mayores, «Qué sale en el juego»**: las seis islas y las diez consonantes, cada una con su botón, que
  se marca o se desmarca. De serie, todo marcado.
- **Una isla quitada** no está en el mapa, ni su camino de puntos ni su bandera, y Bruno no la sugiere. En el mapa queda
  siempre al menos una: la última no se puede quitar.
- **Una consonante quitada** no la pide Bruno en Comeletras, no sale entre las galletas, no se traza y no está en las
  rocas de la isla. Tampoco frena a las siguientes: sin la M, después de la P, la L y la S vienen la T, la N y la D.
- **Las vocales salen siempre**: son por donde empieza todo.

## Alternativas consideradas

| Alternativa | A favor | Por qué se descarta |
|---|---|---|
| Poder quitar también vocales | Más control | Comeletras y los trazos empiezan por ellas; sin vocales no hay por dónde empezar |
| La isla quitada, en el mapa pero apagada | Se ve que existe | Ella la tocaría y no pasaría nada: parece roto |
| Quitar letras también en la isla de las palabras | Lo mismo en todas partes | Allí van por sílabas y dibujos, no por letras sueltas |

## Consecuencias

- La partida guarda las islas y las letras quitadas.
- Las letras de la isla se piden con lo que haya marcado; lo demás no cambia.
