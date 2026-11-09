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
| M5 Más contenido | Lo que Fangusk elija de lo que falta de la propuesta: el sonido inicial, más consonantes y su foto | en marcha: eligió el sonido inicial, y la isla de las palabras está hecha ([ADR 0013](adr/0013-la-isla-de-las-palabras.md)); después, la T, la N y la D, hechas ([ADR 0014](adr/0014-la-t-la-n-y-la-d.md)). Y su foto, en un marco del cuarto ([ADR 0016](adr/0016-su-foto.md)). De la batería de [ideas](ideas.md), escribir su nombre ([ADR 0017](adr/0017-escribir-su-nombre.md)), la emoción del día ([ADR 0019](adr/0019-la-emocion-del-dia.md)) que se adapte a lo que sabe ([ADR 0020](adr/0020-se-adapta-a-lo-que-sabe.md)) las rimas ([ADR 0021](adr/0021-rimas-con-bruno.md)) la F, la B y la R ([ADR 0022](adr/0022-la-f-la-b-y-la-r.md)), la cuenta atrás ([ADR 0023](adr/0023-la-cuenta-atras.md)), las pegatinas del cuarto ([ADR 0024](adr/0024-pegatinas-para-el-cuarto.md)) y ordena la historia ([ADR 0025](adr/0025-ordena-la-historia.md)), el anterior y el siguiente ([ADR 0026](adr/0026-el-anterior-y-el-siguiente.md)), dentro y fuera ([ADR 0028](adr/0028-dentro-y-fuera.md)), las sumas hasta 3 ([ADR 0029](adr/0029-sumas-hasta-3.md)), la asamblea de Bruno ([ADR 0030](adr/0030-la-asamblea-de-bruno.md)), los trazos antes de las letras ([ADR 0031](adr/0031-trazos-antes-de-las-letras.md)), el armario de Bruno ([ADR 0032](adr/0032-el-armario-de-bruno.md)), y la pantalla de inicio ([ADR 0018](adr/0018-la-pantalla-de-inicio.md)). Falta que lo pruebe |

## Riesgos

Viven en el registro del arnés, [docs/harness/riesgos.md](harness/riesgos.md), con su dimensión, su mitigación y su dueño,
y se revisan al cerrar cada hito.
