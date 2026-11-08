# ADR-0030 · La asamblea de Bruno

| | |
|---|---|
| **Estado** | aceptada: a la propuesta del agente («Después de la emoción del día, como en la asamblea de clase: qué día de la semana es y qué tiempo hace, que ella elige mirando por la ventana (idea 1.12)»), «La asamblea de Bruno» (Fangusk, 2026-11-07, elegida entre opciones). Propuesta en los detalles: el calendario de la semana con sus letras y colores, que Bruno diga el día y ella lo toque, el día de antes y el de después, los cuatro tiempos y que la ventana del cuarto lo enseñe todo el día |
| **Fecha** | 2026-11-08 · **Fase:** P0 · Borrador anticipado, sin ninguna puerta confirmada · **Hito:** M5 |
| **Propone · decide** | agente (Claude Code, Opus 5.5) · Fangusk |
| **Reversibilidad** | alta: sin ella, después de la emoción del día se vuelve al cuarto, como antes |

## Contexto

Fangusk pidió seguir y eligió cuatro piezas; esta es la tercera, la idea 1.12. La asamblea de cada mañana es una rutina de
las clases de 4 años: qué día es, qué tiempo hace y cómo nos sentimos
([investigación §2](../investigacion/investigacion.md#2-qué-se-trabaja-en-el-cole-a-los-4-años)). El juego ya tenía la
emoción del día ([ADR 0019](0019-la-emocion-del-dia.md)), que es su primera parte.

## Decisión

- **Cuándo**: después de la emoción del día, una vez al día, antes del cuarto. Si sale de la emoción sin contestar, no
  hay asamblea ese día.
- **El día**: en la pared del cuarto, el calendario de la semana, de lunes a domingo, cada día con su color y su letra
  como en clase (L, M, X, J, V, S, D). El narrador: «¡Es la hora de la asamblea! ¿Qué día es hoy?», y Bruno lo dice:
  «¡Hoy es martes!»; la tarjeta de hoy llama, porque a los 4 años no se lee, y ella la toca. Se enciende con una
  estrella y el narrador dice los de al lado, que se levantan: «Ayer fue lunes y mañana será miércoles». Si toca otra:
  «Ese es el jueves», y Bruno lo vuelve a decir. El día sale de la fecha del aparato.
- **El tiempo**: «¿Qué tiempo hace hoy? Mira por la ventana y dímelo». Ella mira por su ventana de verdad (el juego no
  lo sabe, porque no tiene conexión) y toca uno de cuatro: sol, nubes, lluvia o nieve. Bruno dice algo de él, «¡Llueve!
  ¡A por el paraguas y las botas!», y la ventana del cuarto de Bruno lo enseña todo el día. Luego: «¡Ya podemos empezar
  el día!», y al cuarto.
- **Lo que cuenta**: en la zona de mayores, cuántas veces ha visto cada tiempo.

## Alternativas consideradas

| Alternativa | A favor | Por qué se descarta |
|---|---|---|
| Que ella busque el día sin ayuda | Más reto | A los 4 años no se leen los días; la asamblea es una rutina, no una prueba |
| El tiempo de verdad, de internet | Sin preguntar | Nada sale del aparato ([ADR 0005](0005-un-regalo-sin-conexion.md)); y mirar por la ventana es lo que se hace en clase |
| Los nombres de los días enteros en las tarjetas | Como en algunos calendarios | No caben grandes; la letra y el color son los de muchas aulas |

## Consecuencias

- 28 frases más en el guion: los días de Bruno, el de antes y el de después de cada uno, «Ese es el...» de cada día,
  la presentación, la pregunta del tiempo, lo que dice Bruno de cada tiempo y el comienzo del día.
- La ventana del cuarto de Bruno enseña el tiempo del día en todas las pantallas del cuarto.
- La partida guarda el último día que dijo el tiempo y cuál.
