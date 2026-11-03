# Proceso, criterios de salida y riesgos

## Cómo se trabaja

- **Decide Fangusk**: lo que él dicta va a un ADR como aceptado; lo que decide el agente, como propuesto
  ([ADR](adr/README.md)). Lo que el juego tiene que hacer y no está dicho se le pregunta antes de construirlo.
- **Hecho quiere decir**, en cada cambio:
  1. el código con sus pruebas (la lógica, en pruebas comunes que corren en la JVM, en Android y en el simulador de iOS);
  2. `bash scripts/verificar.sh` termina en «La puerta pasa»: sin `!!`, las voces y los efectos al día con su guion,
     ktlint, sin avisos del compilador, pruebas, lint de Android, APK de depuración y de lanzamiento y la app de iOS
     compilada;
  3. probado en el emulador de Android y en el simulador de iOS, con el sonido quitado y la app cerrada al acabar
     ([pruebas](pruebas.md));
  4. las decisiones, en un ADR; los documentos y los textos del juego, en español; el código, en inglés.
- **Al cerrar cada hito**, una demostración a Fangusk y su visto bueno.

## Hitos

Los propone el agente con lo que la propuesta 3 deja para después ([página de diseño](diseno.md)). El orden de M2 en
adelante y lo que entra en cada uno los decide Fangusk.

| Hito | Sale cuando | Estado |
|---|---|---|
| M1 Primera versión | Lo que la propuesta pide a la primera versión: el nacimiento, Comeletras con las vocales, los trazos, contar hasta 5 y el mapa con dos islas. Además, los regalos, a dormir con la misión para casa, la calma, la zona de mayores y el límite diario. Se juega de principio a fin en el emulador y en el simulador y pasa la puerta. Fangusk lo prueba y da el visto bueno | hecho a falta de que Fangusk lo pruebe |
| M2 Para jugar juntos | «Habla con Bruno». Las voces de la familia, no: «seguiremos con las voces actuales» (Fangusk, 2026-11-03) | hecho ([ADR 0011](adr/0011-habla-con-bruno.md)) a falta de que Fangusk lo pruebe con el micrófono de verdad |
| M3 Más islas | Las que Fangusk elija de la propuesta: consonantes y sonido inicial, números hasta 10, formas, colores y series, y emociones | eligió hacerlo antes que M2; hechas la de los colores ([ADR 0007](adr/0007-la-isla-de-los-colores.md)) y la de las formas ([ADR 0008](adr/0008-la-isla-de-las-formas.md)), ampliadas las letras y los números ([ADR 0009](adr/0009-mas-letras-y-numeros-hasta-el-diez.md)) y hecha la de las emociones ([ADR 0010](adr/0010-la-isla-de-las-emociones.md)): hecho a falta de que Fangusk lo pruebe |
| M4 Entrega | Instalado en el dispositivo en que ella juega, con su icono, y probado allí | en marcha: en su iPhone 15 Pro, ad hoc, el día que se lo dé ([ADR 0012](adr/0012-la-entrega-ad-hoc.md), [entrega](entrega.md)). Probado en el simulador iPhone 17 Pro, y la versión definitiva en uno de iPhone 15 Pro, girados a los dos lados, con el proyecto de Xcode listo para firmar. Falta que Fangusk se dé de alta en el Apple Developer Program, instalarlo el día de la entrega y probarlo allí |

## Riesgos

Viven en el registro del arnés, [docs/harness/riesgos.md](harness/riesgos.md), con su dimensión, su mitigación y su dueño,
y se revisan al cerrar cada hito.
