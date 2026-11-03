# ADR-0016 · Su foto, en un marco del cuarto de Bruno

| | |
|---|---|
| **Estado** | aceptada: a la propuesta del agente («En la zona de mayores, “Elegir su foto” abre la galería del aparato, sin pedir permiso de fotos: solo llega al juego la que se elige. Sale en un marco colgado en el cuarto de Bruno, junto a la ventana, y la primera vez Bruno dice “¡Mira! ¡Eres tú!”. Se guarda solo en el aparato, recortada y pequeña. “Quitar su foto” la borra, y “Volver a empezar” la conserva, como su nombre»), «Así» (Fangusk, 2026-11-03, elegida entre opciones). Propuesta en los detalles: el sitio del marco, que no cupo junto a la ventana, y su tamaño |
| **Fecha** | 2026-11-03 · **Fase:** P0 · Borrador anticipado, sin ninguna puerta confirmada · **Hito:** M5 |
| **Propone · decide** | agente (Claude Code, Opus 5.5) · Fangusk |
| **Reversibilidad** | alta: «Quitar su foto» la borra del aparato |

## Contexto

Las cuatro propuestas tenían una zona de mayores con «nombre y foto»
([investigación](../investigacion/investigacion.md)). Su nombre ya cuelga en el banderín del cuarto de Bruno; faltaba su
foto, lo último que quedaba de la propuesta 3. Con su nombre, su foto y sus cosas, los niños de 3 y 4 años se ríen más y
aprenden más palabras, según la investigación.

## Decisión

- **Se elige en la zona de mayores**: «Elegir su foto» abre la galería del propio aparato (el selector de fotos de
  Android, el de iOS o, en el Mac, una ventana para abrir un fichero). No se pide permiso de fotos: solo llega al juego
  la que se elige.
- **Se guarda pequeña y solo en el aparato**: centrada, recortada en cuadrado y de 360 píxeles de lado, junto a la
  partida. «Cambiar su foto» pone otra, «Quitar su foto» la borra y «Volver a empezar» la conserva, como su nombre.
- **Cuelga en un marco de madera del cuarto de Bruno**, de un clavo, en la pared de la derecha, encima del botón de
  jugar: junto a la ventana no cabía sin tapar a Bruno. La primera vez que ella entra en el cuarto con la foto puesta,
  Bruno la mira y dice «¡Mira! ¡Eres tú!», y lo repite cada vez que ella toca la foto.

## Alternativas consideradas

| Alternativa | A favor | Por qué se descarta |
|---|---|---|
| Y también al dormir, en el sueño de Bruno | Más presencia | Fangusk eligió solo el marco |
| No hacerla | El nombre ya personaliza | Fangusk eligió hacerla |
| Hacer la foto con la cámara desde el juego | Más directo | Pediría el permiso de la cámara; la galería no pide nada |

## Consecuencias

- **El aparato guarda la foto de una menor**, además de su nombre ([R-05](../harness/riesgos.md)): no sale de él, y se
  borra con «Quitar su foto» o al desinstalar el juego.
- Una frase más en el guion (374 en total).
- Con esto está todo lo de la propuesta 3, menos las voces de la familia, que Fangusk decidió no hacer.
