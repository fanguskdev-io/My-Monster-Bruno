# ADR-0039 · Una copia de su partida, para cambiar de móvil

| | |
|---|---|
| **Estado** | aceptada: al pedir «Vale, cerremos el backlog y continuemos», la idea 4.4 de la batería («Comprobar que la copia del propio iPhone la guarda y, si no, poder exportarla»), Fangusk, 2026-11-09. Propuesta en los detalles: que no se puede comprobar del todo, y por eso la copia a mano en la zona de mayores, qué lleva y cómo se recupera |
| **Fecha** | 2026-11-09 · **Fase:** P0 · Borrador anticipado, sin ninguna puerta confirmada · **Hito:** M5 |
| **Propone · decide** | agente (Claude Code, Opus 5.5) · Fangusk |
| **Reversibilidad** | alta: sin la copia, la partida se queda solo en el móvil, como antes |

## Contexto

Lo que ha hecho, su nombre y su foto se guardan solo en el móvil ([ADR 0005](0005-un-regalo-sin-conexion.md)): en
iOS, en las preferencias de la app (NSUserDefaults), y en Android, en las suyas (SharedPreferences, con la copia de
seguridad de Android permitida). La copia de seguridad del iPhone, en iCloud o en el ordenador, guarda los datos de todas
las apps. Pero el juego se le instala ad hoc ([ADR 0012](0012-la-entrega-ad-hoc.md)), y al pasar a otro iPhone solo se
reinstalan solas las apps de la App Store: hay que instalar el juego otra vez, con el iPhone nuevo dado de alta, y
Apple no documenta que sus datos vuelvan entonces. Lo dicen guías de terceros, sin poder comprobarlo aquí sin dos
iPhone. Fangusk pidió cerrar la batería de [ideas](../ideas.md), y esta es la 4.4.

## Decisión

- **Una copia a mano**, en la zona de mayores, en «Su partida»: «Guardar una copia» abre el sitio de guardar del
  propio móvil (Archivos en el iPhone, el selector de archivos en Android, una ventana en el Mac) y deja allí un fichero
  con todo: lo que ha hecho, su nombre, su Bruno y su foto.
- **Recuperarla**: «Recuperar una copia» abre el mismo sitio para elegir el fichero; hay que tocar otra vez para
  confirmar, porque cambia la partida de ahora por la de la copia. Si el fichero no es una copia del juego, lo dice y
  no cambia nada.
- **Nada sale solo**: la copia la hace un mayor y va donde él elija; el juego no la manda a ningún sitio.
- **En la entrega** ([entrega](../entrega.md)), una línea: antes de cambiar de iPhone, guardar una copia.

## Alternativas consideradas

| Alternativa | A favor | Por qué se descarta |
|---|---|---|
| Fiarse de la copia del iPhone | Nada que hacer | Con una app ad hoc no está claro que vuelva, y no se puede comprobar aquí |
| Sincronizar con iCloud | Automático | Pide cuenta de desarrollador de pago y conexión; el juego no tiene internet |
| Un código de texto para copiar a mano | Sin ficheros | La foto no cabe en un código que se pueda escribir |

## Consecuencias

- Un fichero de texto, `partida-de-bruno.json`, con la partida y la foto; pesa poco, unos 40 KB con foto.
- Cada plataforma abre su propio sitio de guardar y de abrir ficheros.
