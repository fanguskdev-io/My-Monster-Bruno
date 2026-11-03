# ADR-0012 · La entrega: ad hoc, en su iPhone 15 Pro el día que se lo dé

| | |
|---|---|
| **Estado** | aceptada: «No voy a poder tener el iphone para probarlo, va a tener que ser en la version definitiva» y, a «¿Pasamos a TestFlight?», «Mejor ad hoc» (Fangusk, 2026-11-03, elegida entre opciones). Antes había elegido «Xcode gratis», también entre opciones, y la cambió al saber que esa vía pide el iPhone cada 7 días |
| **Fecha** | 2026-11-03 · **Fase:** P0 · Borrador anticipado, sin ninguna puerta confirmada · **Hito:** M4 |
| **Propone · decide** | agente (Claude Code, Opus 5.5) · Fangusk |
| **Reversibilidad** | baja una vez instalado: cambiar de vía cambia quién firma la app, iOS obliga a borrarla y Bruno vuelve al huevo |

## Contexto

Ella jugará en un iPhone 15 Pro (Fangusk, 2026-11-03), y el juego no se publica en la App Store
([ADR 0005](0005-un-regalo-sin-conexion.md)). Para instalar en un iPhone una app que no está en la tienda, Apple deja tres
vías ([entrega](../entrega.md)): con Xcode y un Apple ID gratis, que caduca a los 7 días, o con TestFlight o ad hoc, que
piden el Apple Developer Program (unos 99 € al año). Fangusk no tendrá su iPhone antes del día en que se lo dé, así que
no se puede probar allí antes: la primera versión que llegue a su iPhone es la definitiva.

## Decisión

- **Ad hoc**, con el Apple Developer Program de Fangusk: se instala desde su Mac el día de la entrega, con el iPhone
  conectado por cable, y dura un año.
- **Antes**, sin el iPhone: Fangusk se da de alta en el programa e inicia sesión en Xcode. El agente pone el Team ID en
  `iosApp/Configuration/Local.xcconfig`, que git ignora, pasa la puerta, prueba la versión definitiva entera en un
  simulador de iPhone 15 Pro y la archiva.
- **El día de la entrega**: el iPhone, con iOS 18.5 o posterior, se conecta y tiene el Modo de desarrollador activado
  mientras se instala. El agente lo registra por su UDID, exporta la versión ad hoc y la instala, y se prueba allí con
  sonido, también el permiso del micrófono de «Habla con Bruno».
- **Dentro de un año**, o si hace falta un arreglo, se vuelve a conectar y se instala encima, y la partida se conserva.

## Alternativas consideradas

| Alternativa | A favor | Por qué se descarta |
|---|---|---|
| Xcode con su Apple ID, gratis | No cuesta nada | Pide el iPhone en el Mac para instalarlo y otra vez cada 7 días; sin él, deja de abrirse. Fangusk la eligió primero y la cambió al saber que no tendrá el iPhone |
| TestFlight | Nunca pide el iPhone en el Mac, y los arreglos le llegan solos | Su familia tiene que instalar TestFlight con su Apple ID y aceptar la invitación, y cada versión dura 90 días. Fangusk prefirió ad hoc |

## Consecuencias

- **Cuesta el programa**, unos 99 € al año. Para seguir pasado el año hay que renovarlo y reinstalarla encima.
- **No hay prueba previa en su iPhone**: lo que solo falle allí se verá el día de la entrega, y arreglarlo pedirá volver
  a tener el iPhone. Por eso la versión definitiva se prueba antes entera en un simulador de iPhone 15 Pro, con la isla
  y las esquinas medidas ([pruebas](../pruebas.md)), y ese día conviene tener el Mac a mano.
- **El sonido y el micrófono** solo se oyen allí: en el simulador se prueba en silencio.
