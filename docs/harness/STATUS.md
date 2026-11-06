---
harness_version: 1.15.0                # la que declara scoring/*.yaml del arnés; su «Sin publicar» está vacía (commit e2f6f8f5, 2026-10-31)
harness_path: ../mayeutic-harness      # relativa a la raíz de este repositorio
proyecto: MyMonsterBruno
estado: activo
talla: sin declarar                    # la decide la persona; su ronda está en cola en entrevistas/one-pager-pending.md
fase: P0
proxima_puerta: G0
decisor: Fangusk                       # medido: pide el juego, elige la propuesta y dice cómo se construye en la sesión del 2026-11-02 (docs/diseno.md, docs/adr/README.md); ninguna señal de que decida otra persona (docs/05 §2)
fecha_revision:                        # vacía: todavía no hay acta, ni criterio de muerte con fecha, ni riesgo aceptado
actualizado: 2026-11-06
---

# Estado · MyMonsterBruno

## Dónde estamos

P0, la chispa. El 2026-11-02 Fangusk pidió un juego educativo y divertido para su sobrina de 4 años y, antes de
desarrollar nada, una investigación del mercado con varias propuestas
([`docs/investigacion/`](../investigacion/investigacion.md)). Eligió la 3, «Mi monstruo», con el monstruo llamado Bruno, y
pidió construirlo como CaroFarty: «basate en como se está cronstruyendo CaroFarty y como se integra con Mayeutic usando
KMP». Se adopta como en CaroFarty y BakiFace. El arnés sirve al desarrollo (ADR con estado, puerta de calidad y CI
`mayeutic`), y las rondas de descubrimiento se quedan escritas en su cola, sin abrir: el decisor dijo en BakiFace que no
son relevantes y que él expone lo que tiene que hacer la app. Sin la vez concreta del problema ni criterios de muerte, G0
no se puede revisar y el proyecto sigue en P0. A la vez, el agente construye el juego como **borrador anticipado**
(docs/07 §1 del arnés): el código y los ADR existen antes de G4, los revisan las puertas que los piden y no deciden la
talla, los criterios de muerte ni ninguna salida.

**Siguiente paso:** ninguno del arnés; el desarrollo sigue por orden de Fangusk. M1, M2 y M3 están listos para que los
pruebe y dé su visto bueno, y de M5, la isla de las palabras, la T, la N y la D, su foto, escribir su nombre, la
pantalla de inicio, la emoción del día, que se adapte a lo que sabe, las rimas y la F, la B y la R. M4, la entrega en el iPhone 15 Pro en que jugará ella,
será ad hoc ([ADR 0012](../adr/0012-la-entrega-ad-hoc.md)): falta que Fangusk se dé de alta en el Apple Developer
Program e inicie sesión en Xcode, y el día de la entrega, instalarlo ([entrega](../entrega.md), [proceso](../proceso.md)). Si un
día quiere revisar G0, se abre la primera ronda de la cola de
[entrevistas/one-pager-pending.md](entrevistas/one-pager-pending.md).

## Qué se está haciendo

| Qué | Dimensión | Quién | Desde | Estado |
|---|---|---|---|---|
| Adopción del arnés ([hilo](entrevistas/one-pager-pending.md)) | PU, PV, EE, EI | agente (Claude Code, Opus 5.5) | 2026-11-02 | `bloqueado por Fangusk`: las rondas de descubrimiento no se abren mientras él no lo pida (lo dijo en BakiFace, ronda 2, 2026-11-01) |
| Borrador anticipado del juego: código, puerta de calidad y [ADR](../adr/README.md), pedido por Fangusk | AT | agente (Claude Code, Opus 5.5) | 2026-11-02 | `en curso`: M1, la primera versión, se juega de principio a fin en Android e iOS y pasa la puerta; Fangusk la probó el 2026-11-03 («me gusta») y pidió a Bruno más lento y un narrador menos robótico. M3, hecho a falta de que lo pruebe: las islas de los colores, las formas y las emociones, y las letras y números ampliados, que eligió él. M2, «Habla con Bruno» con el micrófono, hecho; al probarlo pidió que compruebe la palabra, y ya la comprueba con el reconocimiento del propio aparato (ADR 0015), a falta de probarlo en un aparato de verdad. M5, la isla de las palabras, la T, la N y la D, y su foto, que eligió él, hechas a falta de que las pruebe; y de la batería de ideas que pidió, escribir su nombre (ADR 0017), la emoción del día (ADR 0019), que se adapte a lo que sabe (ADR 0020), las rimas (ADR 0021) y la F, la B y la R (ADR 0022), y la pantalla de inicio que pidió como la de CaroFarty (ADR 0018). ADR 0001, 0002 y 0007 a 0022 aceptados con sus palabras; 0003 a 0006, propuestos. `comprobar` avisa de que se escribe sin criterios de muerte confirmados (docs/07 §1 del arnés): Fangusk pidió desarrollar ya, y el borrador no cuenta como evidencia de ninguna puerta hasta que confirme los del one-pager, cuya ronda está en cola |

## Equipo e hitos (EE)

| Persona o agente | Qué parte hace | Horas por semana (medidas, últimas cuatro semanas; EE-E1) |
|---|---|---|
| Fangusk | el juego que pide, las decisiones, probarlo y dárselo a su sobrina | (hueco: en cola) |
| Claude Code (Opus 5.5) | la investigación, el código, los ADR y la puerta de calidad | no aplica: es un agente |

| Hito (EE-E4) | Fecha | Depende de | Colchón | Estado | Si se retrasa tres meses |
|---|---|---|---|---|---|
| M1 a M5 ([proceso](../proceso.md): primera versión → para jugar juntos → más islas → entrega → más contenido) | sin fecha: Fangusk no la ha fijado (2026-11-02) | Fangusk: su visto bueno a cada hito y el orden de lo que viene | — | M1, M2 y M3 hechos a falta de que Fangusk los pruebe; M4, ad hoc ([ADR 0012](../adr/0012-la-entrega-ad-hoc.md)), a falta de que se dé de alta en el programa y se instale en su iPhone el día que se lo dé; M5, la isla de las palabras, la T, la N y la D, su foto, escribir su nombre, la pantalla de inicio, la emoción del día, que se adapte, las rimas y la F, la B y la R hechas | la niña crece: lo de 4 años (vocales, contar hasta 5) se le queda corto; M3 lo alarga con más islas |

## Qué espera respuesta humana

| Entrevista | Para quién | Ronda abierta | Desde | Qué decide la respuesta |
|---|---|---|---|---|

## Criterios de muerte vivos

| # | Criterio | Fecha | Estado | Resolución |
|---|---|---|---|---|

## Condiciones pendientes

| Condición | Puerta | Dueño | Fecha | Estado |
|---|---|---|---|---|

## Scorecard provisional

| Dimensión | Nota provisional | Confianza | Fichero |
|---|---|---|---|
| PU | 0 · sin cerrar · one-pager §2 | baja | [one-pager §2](one-pager.md#2-el-usuario-con-nombre-pu) |
| PV | 0 · sin cerrar · one-pager §1 | baja | [one-pager §1](one-pager.md#1-lo-que-creemos-pv) |
| MC | 0 · sin cerrar · one-pager §6 | baja | [one-pager §6](one-pager.md#6-existe-ya-la-búsqueda-de-diez-minutos-mc) |
| EE | 0 · sin cerrar · one-pager §7 | baja | [one-pager §7](one-pager.md#7-quién-lo-hace-y-con-cuánto-tiempo-ee) |
| EI | 0 · ninguna | baja | — |

## Actas

| Puerta | Fecha | Salida | Acta |
|---|---|---|---|

## Entregas

| Fecha | Salida | Afirmaciones de la portada | Commit | Versión del arnés | A quién se entregó |
|---|---|---|---|---|---|

## Riesgos que hay que mirar esta semana

- R-01 · que sus padres no quieran pantallas a los 4 años · impacto alto, sin hablar con ellos todavía.
- R-03 · que no entienda qué hacer en alguna pantalla sin leer · sin probar con una niña de 4 años.
- R-02 · la voz provisional, sintética · Fangusk se queda con ella; falta verla jugar, y oír las sílabas sueltas de «Habla con Bruno».
- R-04 · la app de su iPhone caduca a los 7 días sin el programa de Apple · eligió ad hoc ([ADR 0012](../adr/0012-la-entrega-ad-hoc.md)); falta que se dé de alta en el programa ([entrega](../entrega.md)).
- R-08 · que el reconocedor no entienda a una niña de 4 años · indulgente y, a la segunda, Bruno la dice él; falta verla jugar.
- R-07 · el micrófono escucha a una menor · solo mientras mantiene el botón y sin guardar nada; falta probarlo en su iPhone.

## Historial de este fichero

| Fecha | Quién | Qué cambió |
|---|---|---|
| 2026-11-02 | agente (Claude Code, Opus 5.5) | Adopción del arnés 1.15.0 en modo nuevo, pedida por Fangusk («basate en como se está cronstruyendo CaroFarty y como se integra con Mayeutic usando KMP»), sobre la investigación y la propuesta que eligió; rondas de descubrimiento en cola sin abrir, como en CaroFarty; el desarrollo sigue como borrador anticipado, con M1 hecho; bloque del arnés en `AGENTS.md` y CI `mayeutic` |
| 2026-11-03 | agente (Claude Code, Opus 5.5) | Fangusk prueba M1: se arregla el sonido del escritorio, Bruno habla más despacio y empieza M3 con la isla de los colores (ADR 0007), que él eligió antes que M2; el narrador nuevo espera a que descargue una voz mejorada |
| 2026-11-03 | agente (Claude Code, Opus 5.5) | La isla de las formas (ADR 0008), elegida por Fangusk; el narrador se queda con la voz básica hasta las voces de la familia (ADR 0004) |
| 2026-11-03 | agente (Claude Code, Opus 5.5) | «Salir del juego» en la zona de mayores, y las letras M, P, L, S y los números hasta el 10 (ADR 0009), elegidos por Fangusk |
| 2026-11-03 | agente (Claude Code, Opus 5.5) | La isla de las emociones (ADR 0010), con la que M3 queda hecho a falta de que Fangusk lo pruebe |
| 2026-11-03 | agente (Claude Code, Opus 5.5) | M2: «Habla con Bruno» con el micrófono (ADR 0011), como lo eligió Fangusk, sin las voces de la familia; riesgo R-07 por el micrófono |
| 2026-11-03 | agente (Claude Code, Opus 5.5) | M4 se une a main con «Sí, únela»: la entrega en su iPhone 15 Pro (docs/entrega.md), el proyecto de Xcode listo para firmar y la prueba de los botones de las esquinas; falta que Fangusk elija la vía |
| 2026-11-03 | agente (Claude Code, Opus 5.5) | La entrega será ad hoc (ADR 0012), elegida por Fangusk porque no tendrá su iPhone hasta el día que se lo dé; R-04 pasa a mitigando |
| 2026-11-03 | agente (Claude Code, Opus 5.5) | M5: la isla de las palabras (ADR 0013), en el sitio de la isla lejana, elegida por Fangusk con el sonido inicial por la sílaba |
| 2026-11-03 | agente (Claude Code, Opus 5.5) | M5: la T, la N y la D (ADR 0014), elegidas por Fangusk, que entran cuando conoce la M, la P, la L y la S |
| 2026-11-03 | agente (Claude Code, Opus 5.5) | M2: Bruno comprueba la palabra con el reconocimiento del propio aparato (ADR 0015), porque al probarlo daba por buena «pera» por «manzana»; riesgo R-08 |
| 2026-11-03 | agente (Claude Code, Opus 5.5) | M5: su foto en un marco del cuarto de Bruno (ADR 0016), lo último de la propuesta 3, como lo eligió Fangusk |
| 2026-11-03 | agente (Claude Code, Opus 5.5) | Recorridos en un iPhone 15 Pro simulado de lo hecho tras la entrega («Habla con Bruno», su foto con el selector de Fotos, la isla de las palabras, la T, la N y la D), elegidos por Fangusk; el conductor de pruebas pasa al repositorio (`tools/conductor-ios`) |
| 2026-11-04 | agente (Claude Code, Opus 5.5) | Una batería de mejoras y ampliaciones sin decidir ([ideas](../ideas.md)), pedida por Fangusk; de ella eligió escribir su nombre (ADR 0017), y pidió una pantalla de inicio como la de CaroFarty (ADR 0018) y que el juego se llame «Mi amigo Bruno» (ADR 0002) |
| 2026-11-04 | agente (Claude Code, Opus 5.5) | M5: la emoción del día (ADR 0019), que Fangusk eligió de las siguientes ideas al pedir que siguiera el desarrollo; R-05 suma lo que guarda de cómo se siente |
| 2026-11-05 | agente (Claude Code, Opus 5.5) | M5: los juegos de elegir se adaptan a lo que sabe (ADR 0020), que Fangusk eligió de las siguientes ideas al pedir que siguiera |
| 2026-11-05 | agente (Claude Code, Opus 5.5) | M5: rimas con Bruno (ADR 0021), segunda actividad de la isla de las palabras, que Fangusk eligió al pedir que siguiera |
| 2026-11-06 | agente (Claude Code, Opus 5.5) | M5: la F, la B y la R (ADR 0022), que Fangusk eligió al pedir que siguiera |
