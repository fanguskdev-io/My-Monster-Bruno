# ADR-0019 · La emoción del día, como en la asamblea de clase

| | |
|---|---|
| **Estado** | aceptada: a la propuesta del agente («Como en la asamblea de su clase: la primera vez que entra al cuarto cada día, Bruno le pregunta “¿Cómo te sientes hoy?” y ella arrastra su foto (o una carita, si no hay foto) a uno de cinco botes de colores: alegría, tristeza, enfado, miedo y calma. Bruno le contesta con cariño y pone también la suya. Se puede saltar con la casa, y en la zona de mayores se ve cómo se ha sentido los últimos días, sin que salga del aparato»), «La emoción del día» (Fangusk, 2026-11-04, elegida entre opciones). Propuesta en los detalles: cuándo pregunta, lo que contesta Bruno a cada emoción, que la suya vaya al bote de la alegría y cuántos días se guardan |
| **Fecha** | 2026-11-04 · **Fase:** P0 · Borrador anticipado, sin ninguna puerta confirmada · **Hito:** M5 |
| **Propone · decide** | agente (Claude Code, Opus 5.5) · Fangusk |
| **Reversibilidad** | alta: es una pantalla aparte, delante del cuarto, y lo que guarda se borra con «Volver a empezar» |

## Contexto

Fangusk pidió seguir con el desarrollo y, de las siguientes de la batería de [ideas](../ideas.md), eligió «La emoción del
día» (la 1.11). En la asamblea de cada mañana de las clases de 4 años se pone la emoción del día, a menudo con la foto de
cada uno en el bote o el rincón de su color
([investigación §2](../investigacion/investigacion.md#2-qué-se-trabaja-en-el-cole-a-los-4-años)). El juego ya tenía sus
cinco emociones con sus colores y sus caritas, de la isla de las emociones ([ADR 0010](0010-la-isla-de-las-emociones.md)),
y su foto, si los mayores la eligen ([ADR 0016](0016-su-foto.md)).

## Decisión

- **Cuándo**: la primera vez cada día que se pasa de la pantalla de inicio al cuarto, antes de él. Una vez al día, aunque
  no conteste. El día que nace Bruno no se pregunta, y si ya no quedan minutos, tampoco: Bruno duerme.
- **Cómo**: en el cuarto, cinco botes de cristal en un banco, cada uno con la tapa de su color y su carita, como el rincón
  de las emociones de clase: alegría (amarillo), tristeza (azul), enfado (rojo), miedo (morado) y calma (verde). Bruno
  saluda y pregunta «¡Hola! ¿Cómo te sientes hoy?», y el narrador: «Lleva tu foto al bote de cómo te sientes». Si no hay
  foto, es una carita dibujada, y el narrador dice «la carita». Tocar un bote dice su nombre («Tristeza»).
- **Lo que contesta Bruno**: con la alegría, «¡Qué bien! Me gusta verte contenta»; con la tristeza, «Vaya... Te doy un
  abrazo muy grande»; con el enfado, «A veces nos enfadamos. Si quieres, respiramos juntos con el globo», que es el rincón
  de la calma; con el miedo, «No pasa nada. Yo estoy contigo»; y con la calma, «¡Qué tranquila estás! Me gusta». Después
  pone la suya en el bote de la alegría: «Y yo estoy contento, ¡porque has venido a jugar conmigo!». Luego, el cuarto, que
  ya no vuelve a saludar y solo la invita a la isla.
- **Se salta** con la casa o con el botón de atrás de Android, y pasa al cuarto.
- **Lo que se guarda**, solo en el aparato: una emoción por día, la última que eligió, de los últimos 14 días. En la zona
  de mayores, «Cómo se ha sentido» enseña los últimos siete, el más reciente primero («4/11: alegría»).

## Alternativas consideradas

| Alternativa | A favor | Por qué se descarta |
|---|---|---|
| Hacer otra de las ideas: que el juego se adapte a lo que sabe, o rimas con Bruno | Más juego | Fangusk eligió la emoción del día |
| Bruno se pone en el mismo bote que ella | La acompaña en lo que siente | Si está triste, Bruno se pondría triste sin que nada le pase; en el de la alegría, porque ella ha venido, le dice algo bonito siempre (detalle del agente) |
| Preguntar cada vez que entra al cuarto | Más práctica | Cansa: en clase es una vez, por la mañana |

## Consecuencias

- 14 frases más en el guion: la pregunta, lo que hay que hacer (con foto o con carita), el nombre de cada bote, lo que
  contesta Bruno a cada emoción y la suya.
- El aparato guarda también cómo dice que se siente cada día ([R-05](../harness/riesgos.md)); no sale de él y «Volver a
  empezar» lo borra.
- Los recorridos del iPhone que empiezan en el cuarto siembran la partida con la emoción de hoy ya preguntada
  ([pruebas](../pruebas.md)).
