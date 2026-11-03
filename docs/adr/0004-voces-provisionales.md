# ADR-0004 · Voces pregrabadas, provisionales con la voz del Mac, y efectos sintetizados

| | |
|---|---|
| **Estado** | propuesta: la tomó el agente. Las voces de la familia son de la propuesta que eligió Fangusk, y cuándo llegan lo decide él |
| **Fecha** | 2026-11-02 · **Fase:** P0 · Borrador anticipado, sin ninguna puerta confirmada |
| **Propone · decide** | agente (Claude Code, Opus 5.5) · Fangusk |
| **Reversibilidad** | alta: cambiar una voz es cambiar su fichero WAV, con el mismo nombre |

## Contexto

A los 4 años no se lee: todo lo tiene que decir una voz ([investigación §3](../investigacion/investigacion.md#3-qué-dice-la-investigación-sobre-diseñar-para-esta-edad)).
La propuesta quiere las **voces de la familia**, grabadas por la tía, mamá o el abuelo. Para probar el juego hace falta
ya una voz, en castellano de España y sin conexión.

## Decisión

- **Todo lo que se dice está en un guion**, [`voces/guion.tsv`](../../voces/guion.tsv): 79 frases, cada una con su
  identificador, quién la dice (el narrador o Bruno) y su texto. El código las nombra en `audio/Voice.kt`, y una prueba
  comprueba que el guion y el código tienen las mismas frases y que cada una tiene su fichero.
- **Provisionalmente las genera el Mac** con su voz de castellano de España, Mónica (`say`), a través de
  `python3 tools/voces.py`. La de Bruno se remuestrea ×1,2 para que suene más aguda y algo más deprisa, como un monstruo
  pequeño. Se recortan los silencios, se igualan de volumen y se guardan en WAV mono de 22 050 Hz en
  `composeResources/files/voces/`. `voces/indice.json` guarda una huella del texto y la voz de cada una, y `--comprobar`
  avisa si alguna falta o está desfasada.
- **Los efectos** (13: toques, el huevo que se rompe, el ñam, campanitas, el regalo…) se sintetizan desde cero con
  `python3 tools/sonidos.py`, siempre iguales. Son cortos y suaves.
- **Sin música de fondo** en la primera versión: «sonidos solo al tocar», como Pok Pok.
- **La voz y los efectos se pueden quitar** en la zona de mayores. Sin voz, el juego espera un poco donde habría hablado.

## Alternativas consideradas

| Alternativa | A favor | Por qué se descarta |
|---|---|---|
| Esperar a las voces de la familia | La voz definitiva desde el principio | No se puede probar el juego sin voz, y grabarlas pide a la familia y una zona de grabación que todavía no existen |
| Texto a voz en el dispositivo | Diría su nombre | La voz cambia de un teléfono a otro y en algunos falta la de España; además, el resultado no se oye antes de dárselo |
| Una voz de IA en la nube | Más natural | Pide conexión y mandar el guion fuera; para un regalo sin conexión, no ([ADR 0005](0005-un-regalo-sin-conexion.md)) |

## Consecuencias

- **La voz suena a máquina** y no dice su nombre (R-02 de [riesgos](../harness/riesgos.md)). Se sustituye frase a frase
  cuando la familia grabe la suya, sin tocar el código.
- La voz de Mónica es de Apple. Para un regalo no hay problema, pero si un día se publicara habría que cambiarla.
