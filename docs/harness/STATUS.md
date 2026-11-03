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
actualizado: 2026-11-03
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

**Siguiente paso:** ninguno del arnés; el desarrollo sigue por orden de Fangusk. La primera versión (M1) está lista para
que la pruebe y dé su visto bueno, y lo que viene después (M2 en adelante, [proceso](../proceso.md)) lo decide él. Si un
día quiere revisar G0, se abre la primera ronda de la cola de
[entrevistas/one-pager-pending.md](entrevistas/one-pager-pending.md).

## Qué se está haciendo

| Qué | Dimensión | Quién | Desde | Estado |
|---|---|---|---|---|
| Adopción del arnés ([hilo](entrevistas/one-pager-pending.md)) | PU, PV, EE, EI | agente (Claude Code, Opus 5.5) | 2026-11-02 | `bloqueado por Fangusk`: las rondas de descubrimiento no se abren mientras él no lo pida (lo dijo en BakiFace, ronda 2, 2026-11-01) |
| Borrador anticipado del juego: código, puerta de calidad y [ADR](../adr/README.md), pedido por Fangusk | AT | agente (Claude Code, Opus 5.5) | 2026-11-02 | `en curso`: M1, la primera versión, se juega de principio a fin en Android e iOS y pasa la puerta; Fangusk la probó el 2026-11-03 («me gusta») y pidió a Bruno más lento y un narrador menos robótico. M3 lleva la isla de los colores y la de las formas, que eligió él. ADR 0001, 0002, 0007 y 0008 aceptados con sus palabras; 0003 a 0006, propuestos. `comprobar` avisa de que se escribe sin criterios de muerte confirmados (docs/07 §1 del arnés): Fangusk pidió desarrollar ya, y el borrador no cuenta como evidencia de ninguna puerta hasta que confirme los del one-pager, cuya ronda está en cola |

## Equipo e hitos (EE)

| Persona o agente | Qué parte hace | Horas por semana (medidas, últimas cuatro semanas; EE-E1) |
|---|---|---|
| Fangusk | el juego que pide, las decisiones, probarlo y dárselo a su sobrina | (hueco: en cola) |
| Claude Code (Opus 5.5) | la investigación, el código, los ADR y la puerta de calidad | no aplica: es un agente |

| Hito (EE-E4) | Fecha | Depende de | Colchón | Estado | Si se retrasa tres meses |
|---|---|---|---|---|---|
| M1 a M4 ([proceso](../proceso.md): primera versión → para jugar con la familia → más islas → entrega) | sin fecha: Fangusk no la ha fijado (2026-11-02) | Fangusk: su visto bueno a cada hito y el orden de lo que viene | — | M1 hecho a falta de que Fangusk lo pruebe | la niña crece: lo de 4 años (vocales, contar hasta 5) se le queda corto; M3 lo alarga con más islas |

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
- R-02 · la voz provisional, sintética · hasta que grabe la familia (M2).

## Historial de este fichero

| Fecha | Quién | Qué cambió |
|---|---|---|
| 2026-11-02 | agente (Claude Code, Opus 5.5) | Adopción del arnés 1.15.0 en modo nuevo, pedida por Fangusk («basate en como se está cronstruyendo CaroFarty y como se integra con Mayeutic usando KMP»), sobre la investigación y la propuesta que eligió; rondas de descubrimiento en cola sin abrir, como en CaroFarty; el desarrollo sigue como borrador anticipado, con M1 hecho; bloque del arnés en `AGENTS.md` y CI `mayeutic` |
| 2026-11-03 | agente (Claude Code, Opus 5.5) | Fangusk prueba M1: se arregla el sonido del escritorio, Bruno habla más despacio y empieza M3 con la isla de los colores (ADR 0007), que él eligió antes que M2; el narrador nuevo espera a que descargue una voz mejorada |
| 2026-11-03 | agente (Claude Code, Opus 5.5) | La isla de las formas (ADR 0008), elegida por Fangusk; el narrador se queda con la voz básica hasta las voces de la familia (ADR 0004) |
