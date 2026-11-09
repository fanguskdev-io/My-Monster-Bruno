# ADR-0042 · Bruno más despacio

| | |
|---|---|
| **Estado** | aceptada: al pedir «Vale, cerremos el backlog y continuemos», la idea 4.3 de la batería («Un ajuste de velocidad de la voz, por si a ella le cuesta seguirlo»), Fangusk, 2026-11-09. Propuesta en los detalles: un interruptor y no una escala, cuánto más despacio, la pausa entre frases y cómo se alarga la voz sin cambiar su tono |
| **Fecha** | 2026-11-09 · **Fase:** P0 · Borrador anticipado, sin ninguna puerta confirmada · **Hito:** M5 |
| **Propone · decide** | agente (Claude Code, Opus 5.5) · Fangusk |
| **Reversibilidad** | alta: con «Voz despacio: no», las frases suenan como siempre |

## Contexto

Las frases están grabadas de antemano con la voz del Mac ([ADR 0004](0004-voces-provisionales.md)); las de Bruno ya
se alargaron al grabarlas. Si a ella le cuesta seguirlas, no hay forma de que hablen más despacio. Fangusk pidió cerrar
la batería de [ideas](../ideas.md), y esta es la 4.3.

## Decisión

- **En la zona de mayores, «Voz despacio: sí» o «no»**, junto a la voz, los efectos y la música. De serie, no.
- **Cuánto**: cada frase dura una vez y un quinto lo que dura (al 83 %), con la misma voz y el mismo tono, y la pausa
  entre frase y frase pasa de un cuarto de segundo a casi medio. Vale para Bruno y para el narrador; su propia voz en
  «Habla con Bruno», no.
- **Cómo**: el propio juego alarga cada frase al decirla, con WSOLA: la corta en trozos que se solapan y los vuelve a
  juntar más separados, eligiendo cada trozo donde mejor encaja con el anterior, para que no suene roto. La misma cuenta
  en Android, en iOS y en el Mac. Las últimas frases alargadas se guardan un rato, para no alargarlas otra vez.

## Alternativas consideradas

| Alternativa | A favor | Por qué se descarta |
|---|---|---|
| Grabar otra vez todas las frases más despacio | El mejor sonido | Serían el doble de voces, unos 50 MB más en su iPhone |
| Reproducirlas más lentas sin más | Fácil | La voz se vuelve más grave, como un disco lento; Bruno dejaría de sonar a Bruno |
| Una escala de velocidades | Más fino | Un mayor quiere «más despacio», no un número; un paso se nota y basta |
| Solo más pausa entre frases | Sin tocar la voz | Ayuda, pero las palabras seguirían igual de rápidas |

## Consecuencias

- La partida guarda si la voz va despacio.
- Cada frase dicha despacio se calcula al decirla, unas decenas de milisegundos, fuera del hilo de la pantalla.
- Queda por oírla en un aparato de verdad, con sonido, el día de la entrega (idea 5.2).
