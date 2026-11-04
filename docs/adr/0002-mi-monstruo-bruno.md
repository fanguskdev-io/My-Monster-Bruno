# ADR-0002 · Mi monstruo Bruno: ella le enseña a un monstruo que nace de un huevo

| | |
|---|---|
| **Estado** | aceptada en la propuesta y el nombre: «Comenzamos con la propuesta 3 Mi monstruo, pero en lugar de Coco, llamemosle Bruno» (Fangusk, 2026-11-02). El juego se llama «Mi amigo Bruno» desde que lo pidió: «Sustituye: Mi monstruo Bruno por: Mi amigo Bruno» (Fangusk, 2026-11-04). El alcance de la primera versión es el que la propuesta le daba. Propuesta en los detalles: cuántas rondas tiene cada juego, cuántas letras se trazan, los regalos y cuándo crece |
| **Fecha** | 2026-11-02 · **Fase:** P0 · Borrador anticipado, sin ninguna puerta confirmada |
| **Propone · decide** | agente (Claude Code, Opus 5.5) · Fangusk |
| **Reversibilidad** | media: el personaje y su mundo se pueden ampliar con islas nuevas; cambiar la idea es otro juego |

## Contexto

Fangusk pidió un juego educativo y divertido para su sobrina de 4 años, recién llegada al cole en España, y antes una
investigación del mercado con varias propuestas ([investigación](../investigacion/investigacion.md),
[maqueta](../investigacion/propuestas.html)). Eran cuatro: ¡Buenos días, cole!, La isla de (su nombre), Mi monstruo y Los
cuentos de (su nombre). La recomendada era la 3. Fangusk la eligió después de leerlas todas.

## Decisión

- **Mi monstruo, con el monstruo llamado Bruno**: de un huevo nace un monstruo que no sabe nada y ella le enseña. Él pide,
  ella le da, y él aprende, crece y se ríe con ella.
- **La primera versión** es la que la propuesta describía: «el nacimiento, Comeletras con las vocales, contar hasta 5,
  trazos y un mapa con dos islas». Lleva además tres de las cuatro piezas comunes: el rincón de la calma, la misión para
  casa y la zona de mayores ([página de diseño](../diseno.md)).
- **Los detalles que propone el agente**, que el código sigue mientras Fangusk no diga otra cosa:
  - Comeletras tiene cinco rondas, una por vocal y con tres galletas en el plato. Se puede arrastrar o tocar.
  - Se trazan dos vocales por visita, las que menos ha trazado, en el orden de trazos del cole.
  - Las fresas son cinco rondas, del 1 al 5, y la primera nunca es el 5.
  - Al terminar una isla hay un regalo, un accesorio que Bruno lleva siempre. Son seis en total. Cada dos islas, Bruno
    crece un poco, hasta tres veces.
  - Bruno nunca dice «¡mal!», nunca pasa hambre ni se pone triste si ella no juega, y nunca la presiona para seguir.

## Alternativas consideradas

| Alternativa | A favor | Por qué se descarta |
|---|---|---|
| Las propuestas 1, 2 y 4 | El cole en miniatura, el mundo libre y los cuentos con la familia | Fangusk eligió la 3 |
| Que Bruno pase hambre o se ponga triste si no juega (como las mascotas virtuales) | Hace volver | Es justo el diseño manipulador que la investigación desaconseja (Radesky y otros, 2022) |
| Un nivel que sube con los aciertos | Se adapta a lo que sabe | En la primera versión basta con repetir lo menos practicado; el nivel llega con las islas nuevas |

## Consecuencias

- **Lo que falta de la propuesta** queda escrito en la [página de diseño](../diseno.md#lo-que-trae-y-lo-que-falta):
  su foto. «Habla con Bruno» ya está ([ADR 0011](0011-habla-con-bruno.md)); las voces de la familia, Fangusk decidió no
  hacerlas ([ADR 0004](0004-voces-provisionales.md)).
  El orden lo decide Fangusk.
- **Se puede ampliar sin rehacer nada**: una isla nueva es una lista de actividades y su voz.
