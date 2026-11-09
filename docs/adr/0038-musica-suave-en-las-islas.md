# ADR-0038 · Música suave en las islas, que se puede quitar

| | |
|---|---|
| **Estado** | aceptada: al pedir «Vale, cerremos el backlog y continuemos», la idea 2.6 de la batería («La regla 8 la contempla y el juego no tiene música: un fondo tranquilo, distinto en cada isla, con su interruptor en la zona de mayores»), Fangusk, 2026-11-09. Propuesta en los detalles: que suene de serie, muy bajo, solo en las islas; cómo se hace cada pieza y qué instrumentos lleva |
| **Fecha** | 2026-11-09 · **Fase:** P0 · Borrador anticipado, sin ninguna puerta confirmada · **Hito:** M5 |
| **Propone · decide** | agente (Claude Code, Opus 5.5) · Fangusk |
| **Reversibilidad** | alta: con «Música: no» en la zona de mayores, el juego suena como antes |

## Contexto

El juego solo tenía voces y efectos cortos que suenan al tocar ([ADR 0004](0004-voces-provisionales.md)). La regla de
que sea tranquilo admite un fondo suave, y Fangusk pidió cerrar la batería de [ideas](../ideas.md); esta es la 2.6.

## Decisión

- **Dónde**: en las islas, mientras juega en ellas y abre su regalo. En el cuarto, el mapa, la calma y a dormir, nada:
  ahí hablan Bruno y ella, o es para calmarse.
- **Cómo es**: una pieza distinta por isla, de unos veinte segundos, que se repite sin que se note el corte. Escalas
  pentatónicas, para que nada suene mal; una melodía, un bajo y, en algunas, un acorde de fondo. Marimba en las letras,
  los colores y las formas; caja de música en los números; flauta, más lenta, en las emociones y las palabras.
- **Muy bajo**: a menos de un tercio de la voz, y todas igual de fuertes. La voz se sigue entendiendo encima.
- **Se puede quitar**: en la zona de mayores, «Música: sí» o «no», junto a la voz y los efectos. Suena de serie.
- **Hecha en casa**: como los efectos, sintetizada desde cero con `tools/musica.py`, sin nada de fuera; la puerta
  comprueba que los ficheros son los de su receta.

## Alternativas consideradas

| Alternativa | A favor | Por qué se descarta |
|---|---|---|
| Música en todo el juego | Más ambiente | En el cuarto y a dormir hablan; la calma tiene su respiración |
| Apagada de serie | Más tranquilo | Nadie la encontraría: la idea es que suene, y se quita con un toque |
| Canciones con letra | Más suyas | Taparían la voz de Bruno, que es lo que se aprende |
| Música de una biblioteca libre | Mejor sonido | Hay que mirar licencias; la sintetizada no debe nada a nadie |

## Consecuencias

- Seis piezas de música, unos 4 MB en total, a 16 000 Hz y en mono: cada una, por debajo de un mega, lo que Android lee
  de una vez.
- El reproductor de cada plataforma sabe repetir un sonido en bucle; el del Mac, además, pasa a otra frecuencia
  uniendo las muestras con rectas.
- Queda por oírla en un aparato de verdad, con sonido, el día de la entrega (idea 5.2): las pruebas van siempre en
  silencio.
