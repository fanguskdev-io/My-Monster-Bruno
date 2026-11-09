# ADR-0036 · Jugar con un mayor, en «Habla con Bruno»

| | |
|---|---|
| **Estado** | aceptada: al pedir «Vale, cerremos el backlog y continuemos», la idea 2.7 de la batería («Momentos para dos: en “Habla con Bruno”, que el mayor diga una palabra y ella dé las palmadas; o que Bruno le pida enseñarle al mayor lo que ha aprendido»), Fangusk, 2026-11-09. Propuesta en los detalles: la primera de las dos, como un turno más al final, sin micrófono, con las manos para dar las palmadas |
| **Fecha** | 2026-11-09 · **Fase:** P0 · Borrador anticipado, sin ninguna puerta confirmada · **Hito:** M5 |
| **Propone · decide** | agente (Claude Code, Opus 5.5) · Fangusk |
| **Reversibilidad** | alta: sin el turno para dos, «Habla con Bruno» acaba en la cuarta palabra, como antes |

## Contexto

«Habla con Bruno» ([ADR 0011](0011-habla-con-bruno.md)) son cuatro palabras que ella dice y Bruno da palmadas, una por
trozo. Dar palmadas por trozos es un juego de clase a los 4 años, y se juega en voz alta, con otro. Fangusk pidió cerrar
la batería de [ideas](../ideas.md), y esta es la 2.7: ayuda a que lo vean como algo que se hace juntos.

## Decisión

- **Un turno más, para dos**, después de las cuatro palabras: «¡Y ahora, una para jugar con un mayor! Que un mayor diga
  una palabra y tú das las palmadas». En la nube de Bruno, un mayor y una niña.
- **El mayor la dice en voz alta**, sin micrófono: así vale cualquier palabra, el juego no tiene que oírla ni saberla, y
  funciona también sin permiso de micrófono.
- **Ella da las palmadas** tocando las manos, donde estaba el micrófono: «Toca las manos: una palmada por cada trozo».
  Cada palmada suena, Bruno aplaude con ella y sale una tarjeta con su número. Cuando para un momento, la voz las cuenta,
  «¡Tres palmadas!», y Bruno: «¡Qué bien jugáis juntos!». Como mucho, seis.
- **No se corrige**: el juego no sabe la palabra; quien sabe si están bien es el mayor, que está con ella.
- Si no hay nadie, puede salir con la casa, como de cualquier sitio.

## Alternativas consideradas

| Alternativa | A favor | Por qué se descarta |
|---|---|---|
| Que Bruno le pida enseñarle al mayor lo que ha aprendido | La otra mitad de la idea | Ya lo hace la misión para casa cada noche |
| Que el mayor la diga en el micrófono y Bruno la repita | Más divertido | Sin permiso de micrófono no habría turno; y Bruno no sabría sus trozos |
| Que el juego compruebe las palmadas | Como con sus palabras | No sabe la palabra del mayor; corregir es cosa del mayor |

## Consecuencias

- 5 frases más en el guion: la del turno para dos, la de las manos, la de Bruno y las de cinco y seis palmadas.
- Un punto más en la fila de «Habla con Bruno», el del turno para dos.
