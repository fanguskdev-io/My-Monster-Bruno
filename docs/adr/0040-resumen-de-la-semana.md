# ADR-0040 · Su semana, en la zona de mayores

| | |
|---|---|
| **Estado** | aceptada: al pedir «Vale, cerremos el backlog y continuemos», la idea 4.1 de la batería («En la zona de mayores, qué ha practicado, qué le cuesta más (lo que más falla) y cuántos minutos juega cada día. Sin salir del aparato»), Fangusk, 2026-11-09. Propuesta en los detalles: los últimos siete días, qué cuenta como fallo y cómo se dice |
| **Fecha** | 2026-11-09 · **Fase:** P0 · Borrador anticipado, sin ninguna puerta confirmada · **Hito:** M5 |
| **Propone · decide** | agente (Claude Code, Opus 5.5) · Fangusk |
| **Reversibilidad** | alta: sin el resumen, la zona de mayores enseña lo de siempre |

## Contexto

La zona de mayores ya cuenta lo que ha practicado desde el principio, entre cuántas elige en cada juego y cómo se ha
sentido. No decía cuánto juega cada día ni qué le cuesta: el juego nunca guardaba los fallos, porque equivocarse no
castiga ([diseño](../diseno.md), «Las reglas»). Fangusk pidió cerrar la batería de [ideas](../ideas.md), y esta es la
4.1.

## Decisión

- **Una sección nueva, «Su semana»**, arriba de lo que ha practicado, con los últimos siete días:
  - **Los minutos de cada día**, con el día de la semana: «lun 3: 10 min · mar 4: 0 min · ...».
  - **Las islas que ha terminado**, y cuáles.
  - **Lo que más le ha costado**: lo que pidió Bruno cuando ella tocó otra cosa, los tres que más veces: «la M (3
    veces), el triángulo (2), debajo (2)». Si no hay nada: «Esta semana le ha salido todo a la primera o casi».
- **Qué cuenta como fallo**: tocar o llevar otra cosa de la que se pide, en cada juego que pide algo: la letra de
  Comeletras, el color del globo, la serie, la pieza de la casa, la carita, la historia, la palabra que empieza igual,
  la rima, la cuenta atrás, la recta, la suma, el sitio de dentro y fuera y el puesto de la carrera. Ella no lo ve
  nunca: solo lo leen los mayores.
- **Solo siete días**: lo de antes se olvida, para que hable de ahora.

## Alternativas consideradas

| Alternativa | A favor | Por qué se descarta |
|---|---|---|
| Fallos de siempre, sin días | Más datos | Hablaría de lo que le costaba hace meses |
| Un gráfico | Se ve de un vistazo | Una línea por día se lee igual en un móvil y no hay que dibujar ejes |
| Mandarlo por correo cada semana | Sin abrir el juego | Nada sale del aparato ([ADR 0005](0005-un-regalo-sin-conexion.md)) |

## Consecuencias

- La partida guarda, de los últimos siete días, los segundos de juego, las islas terminadas y los fallos de cada cosa.
- Cada juego que pide algo apunta el fallo en su sitio, sin que cambie nada de lo que ella ve u oye.
