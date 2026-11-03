# ADR-0006 · Diez minutos al día y Bruno se va a dormir

| | |
|---|---|
| **Estado** | propuesta. La propuesta que eligió Fangusk decía «Una partida, unos 10 minutos» y «A dormir. Coco bosteza: “¡Hasta mañana!”. Nunca se pone triste si ella no juega». El agente propone que sea un límite por día, cuándo se duerme Bruno y los ajustes de los mayores |
| **Fecha** | 2026-11-02 · **Fase:** P0 · Borrador anticipado, sin ninguna puerta confirmada |
| **Propone · decide** | agente (Claude Code, Opus 5.5) · Fangusk |
| **Reversibilidad** | alta: los minutos son un ajuste |

## Contexto

La Asociación Española de Pediatría recomienda cero pantallas antes de los 6 años, y el comité de expertos del Gobierno
las desaconseja entre los 3 y los 6 salvo de forma ocasional y acompañada. A los 4 años la atención dura unos 8-12
minutos ([investigación §4](../investigacion/investigacion.md#4-pantallas-a-los-4-años-lo-que-dicen-los-pediatras)). Muchos
juegos infantiles empujan a seguir con rachas, cuentas atrás y personajes que piden que no te vayas.

## Decisión

- **Un tiempo por día**, 10 minutos de serie. Cuenta mientras el juego está en pantalla y jugando: no cuentan el huevo, la
  cama ni la zona de mayores. Al día siguiente empieza de cero (`game/Session.kt`).
- **Bruno se va a dormir cuando ella termina lo que está haciendo**, nunca a medias. En el cuarto o en el mapa está entre
  una cosa y otra, así que se duerme enseguida. Si está en una isla, al terminarla, con su regalo. Si está en la calma, al
  volver al cuarto. Bosteza, le da una misión para casa y le dice «¡Hasta mañana!». Si se abre otra vez ese día, sigue
  dormido. Con el tiempo acabado no se empieza una isla (`GameState.kt`).
- **Los mayores eligen** 5, 10, 15 o 20 minutos al día, y pueden dar «10 minutos más, solo hoy», que cuentan desde lo que
  ya ha jugado.
- **Nada que empuje a volver**: sin rachas, sin avisos, sin cuentas atrás y sin un Bruno triste. Mañana está igual.

## Alternativas consideradas

| Alternativa | A favor | Por qué se descarta |
|---|---|---|
| Sin límite, que lo pongan los padres | Más libertad | Lo que la investigación pide es que el juego termine solo, y un final con Bruno durmiendo se acepta mejor que quitarle el móvil |
| Cortar justo al llegar a los 10 minutos | Exacto | Cortar a medias frustra. Terminar la isla cuesta unos minutos más, como mucho los de una isla entera |
| Un número de partidas en lugar de minutos | Más fácil de entender | Una visita puede ser corta o larga; los minutos son lo que piden los pediatras |

## Consecuencias

- Si la familia no lo quiere, se pone en 20 minutos o se da más tiempo ese día. Sin límite no se puede.
- La hora del dispositivo manda: si se cambia la fecha, cambia el día.
