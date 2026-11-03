# ADR-0015 · Bruno comprueba la palabra, con el reconocimiento del propio aparato

| | |
|---|---|
| **Estado** | aceptada: al probarlo, «si nos pide que digamos la palabra Manzana por ejemplo y yo digo Pera, lo da por valido» y, entre las opciones, «Que la compruebe»: «Con el reconocimiento de voz del propio iPhone o Android, sin conexión: nada sale del aparato. Si dice otra: “Huum... Eso no es manzana. ¿Me dices manzana?”. Indulgente con cómo habla una niña de 4 años, y a la segunda Bruno la dice él para no frustrarla» (Fangusk, 2026-11-03). Propuesta en los detalles: cuánto se parece una palabra a otra para darla por buena |
| **Fecha** | 2026-11-03 · **Fase:** P0 · Borrador anticipado, sin ninguna puerta confirmada · **Hito:** M2 |
| **Propone · decide** | agente (Claude Code, Opus 5.5) · Fangusk |
| **Reversibilidad** | alta: sin comprobación, el juego vuelve a ser el de [ADR 0011](0011-habla-con-bruno.md) |

## Contexto

«Habla con Bruno» se hizo sin reconocimiento de voz, como decía la propuesta («No hace falta reconocer la voz: basta con
grabarla y reproducirla»): Bruno repetía lo que ella dijera y daba las palmadas de la palabra que había pedido
([ADR 0011](0011-habla-con-bruno.md)). Al probarlo, Fangusk vio que daba por buena cualquier palabra.

## Decisión

- **Comprueba la palabra el propio aparato, sin conexión**: en el iPhone, el reconocimiento de voz de Apple en el
  dispositivo, y en Android, el reconocedor del teléfono si tiene el español instalado para usarlo sin conexión. Nada
  sale del aparato. Donde no se puede (un Android sin ese reconocedor, el emulador o el Mac), no comprueba y todo sigue
  como antes.
- **Bruno repite lo que ella ha dicho**, con su voz más aguda, mientras el aparato lo reconoce. Si era otra palabra:
  «Huum... Eso no es manzana. ¿Me dices manzana?». A la segunda, Bruno la dice él y da las palmadas igual, para que no
  se atasque nunca. No hay un «¡mal!».
- **Indulgente**: la da por buena si se parece, con una letra de cada tres cambiada («mapoza» es «mariposa», «zol» es
  «sol»), aunque vaya dentro de una frase («una manzana») o la acierte solo una de las apuestas del reconocedor. Otra
  palabra («pera» por «manzana») no vale nunca.
- **En el iPhone pide un permiso más**, el de reconocimiento de voz, justo después del del micrófono, la primera vez que
  se entra. Si no se da, no comprueba.

## Alternativas consideradas

| Alternativa | A favor | Por qué se descarta |
|---|---|---|
| Que Bruno diga la palabra buena antes de las palmadas, sin comprobar | Sin permisos ni reconocedor | Fangusk eligió que la compruebe |
| Dejarlo como estaba | Lo que decía la propuesta | Fangusk vio que daba por buena cualquier palabra |
| Reconocer la voz en un servidor | Reconoce mejor | Sacaría la voz de una menor del aparato ([ADR 0005](0005-un-regalo-sin-conexion.md)) |

## Consecuencias

- **Puede no entenderla aunque lo diga bien**: los reconocedores están hechos para voces adultas. Por eso es indulgente y
  a la segunda Bruno la dice él. Hay que verla jugar en su iPhone ([R-08](../harness/riesgos.md)).
- **Solo se puede probar en un aparato de verdad**: el emulador no tiene reconocedor sin conexión y el Mac no comprueba.
  En muchos Android que no son Pixel tampoco hay reconocedor en el propio teléfono, y entonces no comprueba.
- **El diálogo del permiso en el iPhone lleva una frase fija de Apple** que no se puede quitar: que los datos de voz se
  enviarán a Apple para procesarlos. El juego pide que se reconozca en el propio iPhone (`requiresOnDeviceRecognition`) y
  su voz no sale, pero el mayor que da el permiso leerá esa frase.
- Son 10 frases más en el guion (373 en total).
