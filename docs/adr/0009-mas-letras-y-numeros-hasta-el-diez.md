# ADR-0009 · Más letras y números hasta el 10 en las islas que ya hay

| | |
|---|---|
| **Estado** | aceptada en qué se hace: «Continúa» y, de las dos piezas de M3 que quedaban, «Ampliar las que hay»: «En la isla de las letras, consonantes (M, P, L, S) con su palabra en Comeletras y en los trazos, junto a las vocales. En la de los números, fresas hasta el 10, con las dos manos en la tarjeta» (Fangusk, 2026-11-03, elegida entre opciones). Propuesta en los detalles: las palabras de cada consonante, que salgan las letras menos practicadas con las vocales primero, cuándo llegan los números grandes y cómo se trazan |
| **Fecha** | 2026-11-03 · **Fase:** P0 · Borrador anticipado, sin ninguna puerta confirmada · **Hito:** M3 |
| **Propone · decide** | agente (Claude Code, Opus 5.5) · Fangusk |
| **Reversibilidad** | alta: son letras y números más en los mismos juegos |

## Contexto

La isla de las vocales y la de los números enseñaban lo de la primera versión: las cinco vocales y contar hasta 5. En el
aula de 4 años se pasa de las vocales a las primeras consonantes (M, P, L, S…) y se cuenta hasta 10, con los dedos de
las dos manos ([investigación §2](../investigacion/investigacion.md#2-qué-se-trabaja-en-el-cole-a-los-4-años)). La
propuesta 3 ya los nombraba en «Qué aprende».

## Decisión

- **La isla de las letras** (antes, de las vocales) tiene las cinco vocales y cuatro consonantes: la eme de manzana, la
  pe de pelota, la ele de luna y la ese de sol. Son palabras que se dibujan fácil en el bocadillo de Bruno. Las piedras
  del mapa enseñan las nueve letras, y el narrador dice «¡A la isla de las letras!».
- **Comeletras y los trazos sacan las letras que menos ha practicado**: Comeletras, las cinco que menos le ha dado a
  Bruno, y los trazos, las dos que menos ha trazado. Cuando hay empate van antes las vocales, así que el primer día son
  las vocales, como hasta ahora, y las consonantes entran en cuanto las ha visto. En el plato, las otras galletas pueden
  ser cualquier letra.
- **Las consonantes se trazan en el orden del cole**: la M, primero el palo izquierdo y luego el pico y el palo derecho
  sin levantar el dedo; la P, el palo y luego la barriga desde arriba; la L, hacia abajo y a la derecha de un tirón; la
  S, desde arriba a la derecha, por la izquierda y otra vez a la derecha.
- **Las fresas llegan hasta el 10.** Hasta que ha contado del 1 al 5 una vez cada uno, las rondas son esas, como antes.
  Después mezclan dos del 1 al 5 y tres del 6 al 10, los que menos ha contado, y nunca empieza por el más grande.
- **La tarjeta enseña dos manos** a partir del 6: una mano entera y otra con los dedos que faltan, y los puntos en filas
  de cinco. En el arbusto hay doce fresas, así que hasta la décima es una elección, y en el plato caben diez.
- **Lo que ha practicado**, en la zona de mayores, ya cuenta todas las letras y los números hasta el 10.

## Alternativas consideradas

| Alternativa | A favor | Por qué se descarta |
|---|---|---|
| Una isla nueva para las consonantes | Se ve como algo nuevo | Fangusk eligió ampliar las que hay; el juego de las letras es el mismo |
| Las consonantes por su sonido («la mmm») | Es como se aprende a leer | La voz sintética no sabe decir sonidos sueltos; se dice su nombre, como en muchas aulas, y cuando grabe la familia se puede cambiar |
| Las letras siempre al azar | Más sencillo | Podría salir el primer día la M antes que las vocales; con lo menos practicado y las vocales primero, el orden es el del cole |
| Contar siempre del 1 al 10 | Todo desde el principio | Diez rondas de hasta diez fresas es mucho; cinco rondas, mezclando, y los grandes cuando ya sabe los pequeños |

## Consecuencias

- Son 39 frases más en el guion (177 en total), con la misma voz provisional ([ADR 0004](0004-voces-provisionales.md)).
- En el código la isla se sigue llamando `VOWELS`, porque es la clave con la que está guardada en la partida.
- Queda en el mapa la isla lejana de las emociones, cuando Fangusk diga.
