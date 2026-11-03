# ADR-0005 · Un regalo sin conexión: nada sale del dispositivo

| | |
|---|---|
| **Estado** | propuesta: la tomó el agente con las reglas de la investigación («Limpio: sin anuncios, sin compras, sin cuentas, sin internet y sin recoger datos»), igual que en CaroFarty, donde es aceptada |
| **Fecha** | 2026-11-02 · **Fase:** P0 · Borrador anticipado, sin ninguna puerta confirmada |
| **Propone · decide** | agente (Claude Code, Opus 5.5) · Fangusk |
| **Reversibilidad** | alta mientras no se publique |

## Contexto

El juego es para una niña de 4 años. Lo que más quejas genera en las familias son los anuncios, las suscripciones y los
diseños que empujan a seguir jugando. En la zona de mayores se escribe su nombre, un dato de una menor. Para regalárselo
no hace falta publicarlo ([investigación §5](../investigacion/investigacion.md#5-si-algún-día-se-publica)).

## Decisión

- **Sin anuncios, sin compras, sin cuentas, sin analítica ni servicios en línea.** Android no pide el permiso de
  internet.
- **Lo que se guarda, en el dispositivo y en un solo sitio**: una partida en JSON (`game/SaveData.kt`) con Bruno, lo que
  ha practicado, los minutos de hoy y los ajustes, y su nombre si los mayores lo escriben. «Volver a empezar» lo borra
  todo menos los ajustes.
- **Lo de los mayores, detrás de un candado** que se abre manteniendo el dedo: los ajustes y lo que ha practicado.

## Alternativas consideradas

| Alternativa | A favor | Por qué se descarta |
|---|---|---|
| Ver su progreso desde el móvil de sus padres | Cómodo para la familia | Saca datos de una menor del dispositivo y pide un servidor; lo que ha practicado ya está en la zona de mayores |
| Analítica para ajustar los juegos | Datos reales | Lo mismo; los ajustes salen de verla jugar |

## Consecuencias

- **Si un día se publica** en la categoría de niños de las tiendas, hará falta una política de privacidad y la
  clasificación por edades. Lo demás (sin publicidad de terceros, sin ubicación, puerta para padres) ya se cumple.
- **Lo que oye el micrófono** en «Habla con Bruno» suena una vez y se olvida: ni se guarda ni sale del dispositivo
  ([ADR 0011](0011-habla-con-bruno.md)). Es el único permiso que pide el juego, y Android sigue sin pedir el de internet.
