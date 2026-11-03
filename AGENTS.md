# MyMonsterBruno · para agentes

Lo que no se deduce del código. Si algo de aquí discrepa de su fuente, manda la fuente.

- **Qué es**: «Mi monstruo Bruno», un juego educativo para Android e iOS que Fangusk le regala a su sobrina de 4 años. De
  un huevo nace Bruno y ella le enseña las vocales, a contar y a trazar. Lo que pidió, con sus palabras, y las reglas están
  en [docs/diseno.md](docs/diseno.md); la investigación y las cuatro propuestas, en [docs/investigacion/](docs/investigacion/investigacion.md).
- **Decide Fangusk**: las decisiones están en [docs/adr/](docs/adr/README.md). Las «aceptadas» las dictó él; las
  «propuestas» las tomó un agente y solo él las acepta. No se deduce el producto: si algo no está dicho, se le pregunta.
- **Para una niña de 4 años**: nada que leer (todo lo dice una voz), equivocarse no castiga, botones grandes, solo tocar,
  arrastrar y mantener, y nada que empuje a volver. Bruno nunca pasa hambre ni se pone triste si ella no juega
  ([docs/diseno.md](docs/diseno.md), «Las reglas»).
- **Nada sale del dispositivo**: sin internet, anuncios, compras ni analítica ([ADR 0005](docs/adr/0005-un-regalo-sin-conexion.md)).
- **Lo que se dice** está en `voces/guion.tsv`. Una frase nueva va al guion y a `audio/Voice.kt`, y su WAV se genera con
  `python3 tools/voces.py`, que necesita macOS. Los efectos salen de `python3 tools/sonidos.py`
  ([ADR 0004](docs/adr/0004-voces-provisionales.md)).
- **Al probar en el emulador o en el simulador**, el sonido va quitado (el emulador con `-no-audio` y la app con su
  argumento de silencio), y al acabar se cierran la app y el emulador o el simulador: se oye en el Mac
  ([docs/pruebas.md](docs/pruebas.md)).
- **Hecho quiere decir** que `bash scripts/verificar.sh` termina en «La puerta pasa» y que se ha probado en el emulador y
  en el simulador ([docs/proceso.md](docs/proceso.md)).
- El código y sus comentarios, en inglés; los documentos y los textos del juego, en español.

<!-- mayeutic-harness:begin 1.15.0 -->
## Mayeutic Harness

Este proyecto usa el Mayeutic Harness 1.15.0, en ../mayeutic-harness; dónde está y qué sigue, en docs/harness/STATUS.md.
- Antes de tocar docs/harness/, lee docs/paquete-de-contexto.md del arnés y lo que cita; sus skills/ dicen qué hacer en cada paso, aunque no seas Claude Code.
- Decide Fangusk: lo que decide una persona no lo decides tú (docs/07-agentes.md del arnés, §1).
- Lo del arnés que no encaja (una pregunta, una regla, un aviso, una herramienta), una línea en docs/harness/uso-del-arnes.md en el momento; al acabar cada puerta, su revisión (docs/07-agentes.md del arnés, §9).
<!-- mayeutic-harness:end -->
