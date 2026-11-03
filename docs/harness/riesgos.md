# Registro de riesgos · MyMonsterBruno

**Última revisión:** 2026-11-03, al hacer «Habla con Bruno» (M2) · **Abiertos:** 7 · **Vencidos sin acción:** 0

## Escalas

| Probabilidad | Significa | Impacto | Significa |
|---|---|---|---|
| **alta** | más probable que no en seis meses | **alto** | mata o pivota el proyecto; daño a personas; sanción |
| **media** | podría pasar | **medio** | retrasa una fase o cuesta dinero relevante |
| **baja** | sería raro | **bajo** | molesta, se absorbe |

## Riesgos vivos

| Id | Riesgo (qué pasaría y por qué) | Dimensión | Prob. | Impacto | Mitigación (qué hacemos para que no pase o duela menos) | Dueño | Fecha | Estado | Origen |
|---|---|---|---|---|---|---|---|---|---|
| R-01 | Si sus padres no quieren pantallas a los 4 años, el regalo no se usa y cae H3. La Asociación Española de Pediatría recomienda cero antes de los 6, y el comité de expertos del Gobierno las desaconseja entre los 3 y los 6 salvo de forma ocasional y acompañada | EI | media | alto | Propuesta: el juego se hace a la medida de esa recomendación, con 10 minutos al día que eligen los mayores, Bruno que se duerme solo, la misión para casa y nada que empuje a volver ([ADR 0006](../adr/0006-diez-minutos-y-a-dormir.md)). Y que Fangusk se lo cuente a sus padres antes de dárselo | Fangusk (propuesto) | — | abierto | medida del agente ([investigación §4](../investigacion/investigacion.md#4-pantallas-a-los-4-años-lo-que-dicen-los-pediatras)) |
| R-02 | Si la voz sintética no se entiende bien o no le gusta, ella no sabe qué hacer, porque no lee. Cae H4 | UX | media | medio | Fangusk decidió quedarse con las voces actuales, sin las de la familia (2026-11-03). Hecho: Bruno habla más despacio, a unas 4 sílabas por segundo; las frases son cortas y el altavoz repite lo que se pide ([ADR 0004](../adr/0004-voces-provisionales.md)). Falta verla jugar con estas voces | Fangusk (propuesto) | — | mitigando | medida del agente |
| R-03 | Si una niña de 4 años no entiende qué hacer en alguna pantalla (arrastrar la galleta, empezar el trazo en el punto verde, mantener el dedo en la calma), se frustra y lo deja. Cae H4 | UX | media | medio | Propuesta: voz en cada pantalla, tocar vale lo mismo que arrastrar, aros que llaman al toque, un camino de trazo que perdona y botones que aceptan el dedo 0,3 cm más allá de su borde ([página de diseño](../diseno.md)). Falta verla jugar su primera partida | Fangusk (propuesto) | — | abierto | medida del agente |
| R-04 | Si juega en un iPhone o un iPad y no hay Apple Developer Program, la app caduca a los 7 días y hay que reinstalarla desde el Mac cada semana | OO | alta, si es de Apple | medio | Propuesta: decidirlo al llegar a la entrega (M4): reinstalar cada semana, o pagar el programa (ad hoc un año, o TestFlight 90 días por versión). En Android basta el APK | Fangusk (propuesto) | — | abierto | medida del agente |
| R-05 | El juego guarda en el dispositivo el nombre de una menor y lo que ha practicado. Si un día sacara datos fuera o se publicara, trataría datos de menores, con sus obligaciones | LC | baja | alto | Propuesta: nada sale del dispositivo, sin permiso de internet ni analítica, y el nombre solo lo escriben los mayores ([ADR 0005](../adr/0005-un-regalo-sin-conexion.md)). Si se publica, política de privacidad y la categoría de niños de cada tienda | Fangusk (propuesto) | — | abierto | medida del agente |
| R-06 | Si el dispositivo en que juega es una tableta antigua o barata, el dibujo con vectores de cada fotograma podría ir a tirones | RE | baja | medio | Propuesta: medirlo en su dispositivo al llegar a la entrega (M4). Si va lento, guardar en caché lo que no se mueve (los fondos) ([ADR 0001](../adr/0001-kotlin-multiplatform-como-carofarty.md)) | Fangusk (propuesto) | — | abierto | medida del agente |
| R-07 | «Habla con Bruno» escucha con el micrófono a una niña de 4 años. Si grabara sin que ella lo sepa, guardara su voz o la sacara del aparato, trataría datos de una menor, y la familia perdería la confianza en el regalo | LC | baja | alto | Hecho: solo escucha mientras ella mantiene el botón, como mucho 4 segundos; lo que oye suena una vez y se olvida, sin guardarse ni salir del aparato, y el juego no tiene permiso de internet. El permiso lo da un mayor la primera vez, y la zona de mayores lo explica ([ADR 0011](../adr/0011-habla-con-bruno.md)). Falta probarlo en su iPhone 15 Pro | Fangusk (propuesto) | — | mitigando | medida del agente |

## Riesgos aceptados

| Id | Riesgo | Por qué se acepta | Quién acepta | Fecha | Se revisa el | Qué lo reabriría |
|---|---|---|---|---|---|---|

## Criterios de muerte enlazados

| Riesgo | Criterio de muerte | Fecha | Estado |
|---|---|---|---|

## Riesgos cerrados

| Id | Riesgo (qué pasaría y por qué) | Dimensión | Prob. | Impacto | Mitigación | Dueño | Fecha | Cómo se cerró | Origen |
|---|---|---|---|---|---|---|---|---|---|

## Reglas de este fichero

- Los ids `R-nn` no se reciclan.
- En cada puerta, toda fila de «Riesgos vivos» con fecha pasada se resuelve o se vuelve a fechar con motivo escrito; nunca se borra.
