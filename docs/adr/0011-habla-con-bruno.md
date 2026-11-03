# ADR-0011 · Habla con Bruno: ella dice la palabra, Bruno la repite con su voz y la da en palmadas

| | |
|---|---|
| **Estado** | aceptada en cómo se juega: a la propuesta del agente («Bruno enseña un dibujo y pide “¡Dime MARIPOSA!”; ella mantiene un botón de micrófono grande y lo dice; Bruno lo repite con su voz de monstruo (la de ella, más aguda) y da una palmada por sílaba: MA-RI-PO-SA. La grabación no se guarda ni sale del aparato. Pide permiso de micrófono la primera vez»), «Sí, así» (Fangusk, 2026-11-03, elegida entre opciones). Propuesta en los detalles: las diez palabras, cuántas por visita, dónde está el botón, lo que pasa si no la oye y cuánto más aguda suena |
| **Fecha** | 2026-11-03 · **Fase:** P0 · Borrador anticipado, sin ninguna puerta confirmada · **Hito:** M2 |
| **Propone · decide** | agente (Claude Code, Opus 5.5) · Fangusk |
| **Reversibilidad** | alta: es una pantalla más y dos permisos; quitar el micrófono la deja en «Bruno la dice él» |

## Contexto

La propuesta 3 traía «Habla con Bruno»: ella dice una palabra y él la repite con voz graciosa, dando una palmada por
sílaba. Separar las sílabas con palmadas es de lo que más se trabaja en el aula de 4 años
([investigación §2](../investigacion/investigacion.md#2-qué-se-trabaja-en-el-cole-a-los-4-años)). La propuesta ya decía
que no hace falta reconocer la voz: basta con grabarla y reproducirla. Para M2, Fangusk decidió seguir con las voces
actuales, sin las de la familia ([ADR 0004](0004-voces-provisionales.md)), así que M2 es esta pantalla.

## Decisión

- **Se entra desde el cuarto de Bruno**, con un botón rosa con un micrófono, encima del globo de la calma. No es una isla:
  no da regalo ni cuenta para que Bruno crezca.
- **Diez palabras con dibujo**, de una a cuatro sílabas: sol, luna, oso, fresa, globo, pelota, manzana, regalo, mariposa
  y elefante. En cada visita, cuatro: las que menos ha dicho, de menos palmadas a más.
- **Cómo se juega**: Bruno enseña el dibujo y pide «¡Dime luna!». Ella mantiene el dedo en el micrófono mientras lo dice
  y lo suelta. Bruno lo repite con la voz de ella, un 35 % más aguda y más rápida, como su voz de monstruo. Después da
  una palmada por sílaba diciéndola («¡Lu! ¡Na!»), y cada sílaba aparece en una tarjeta de color; el narrador cuenta las
  palmadas («¡Dos palmadas!»). Al acabar las cuatro, Bruno da las gracias y vuelve a su cuarto.
- **Si no la oye**: la primera vez, «¡No te oigo! ¿Me lo dices otra vez?»; la segunda, Bruno dice la palabra él y da las
  palmadas igual. Sin micrófono (los mayores no dan el permiso, o el aparato no tiene), Bruno la dice siempre él: se
  juega igual y no se graba nada.
- **El micrófono y su voz**: el permiso se pide al entrar la primera vez, con un mayor delante, que es quien responde.
  Solo escucha mientras ella mantiene el botón, como mucho 4 segundos. Lo que oye se queda en la memoria (en iPhone, en
  un fichero temporal que se borra en cuanto se lee), suena una vez y se olvida: ni se guarda ni sale del aparato. Lo
  único que se apunta es cuántas veces ha dicho cada palabra, que se ve en la zona de mayores.
- **No hay reconocimiento de voz**: Bruno no sabe si la ha dicho bien, y no hace falta. Lo que se practica es decir la
  palabra y partirla en sílabas, y nunca hay un «¡mal!». *Cambiado después: Bruno la comprueba con el reconocimiento del
  propio aparato ([ADR 0015](0015-bruno-comprueba-la-palabra.md)).*

## Alternativas consideradas

| Alternativa | A favor | Por qué se descarta |
|---|---|---|
| Sin micrófono: Bruno dice la palabra por sílabas y ella da las palmadas tocando la pantalla | Nada que grabar ni permisos | Fangusk eligió con micrófono. Queda como lo que pasa cuando no hay micrófono |
| Reconocer la palabra (voz a texto) | Sabría si la ha dicho bien | Los servicios del sistema pueden ir por internet, fallan con voces de 4 años y darían un «no» que el juego no da nunca |
| Guardar sus grabaciones para que la familia las escuche | Un recuerdo bonito | Guarda la voz de una menor; el regalo no guarda nada suyo salvo lo practicado ([ADR 0005](0005-un-regalo-sin-conexion.md)) |
| Que escuche sola, sin mantener el botón | Más fácil para ella | Habría que tener el micrófono abierto y decidir cuándo ha terminado de hablar; con el botón, escucha solo cuando ella quiere |

## Consecuencias

- **Son 51 frases más** en el guion (257 en total) y un efecto nuevo, la palmada (14). Las sílabas sueltas («¡Fan!»,
  «¡Glo!») son lo que más puede sonar raro con la voz provisional: hay que oírlas.
- **Dos permisos nuevos**: el micrófono en Android (`RECORD_AUDIO`) y en iOS (`NSMicrophoneUsageDescription`, con el
  texto que lee el mayor). El de internet sigue sin pedirse. Si un día se publicara en la categoría de niños, la política
  de privacidad tendría que explicar el micrófono ([R-05](../harness/riesgos.md)).
- **En iPhone**, desde que ella pulsa el micrófono hasta que sale de la pantalla, el sonido va por el altavoz grande, no
  por el del oído, y suena aunque el interruptor de silencio esté puesto. Al salir, el juego vuelve a sonar como antes,
  interruptor incluido.
- **Se prueba en el aparato de verdad**: el emulador de Android no tiene micrófono (allí se ve el camino de «Bruno la dice
  él»), y el del iPhone usa el del Mac. Falta oírla a ella en su iPhone 15 Pro.
