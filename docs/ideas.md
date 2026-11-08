# Ideas para mejorar y ampliar el juego

Una batería de mejoras y ampliaciones posibles para «Mi amigo Bruno», propuesta por el agente el 2026-11-04, cuando ya
está toda la propuesta 3 salvo las voces de la familia. Ninguna está decidida: qué se hace, en qué orden y cómo lo decide
Fangusk ([proceso](proceso.md)).

Salen de cuatro sitios: lo que se trabaja en el cole a los 4 años y el juego aún no tiene
([investigación §2](investigacion/investigacion.md#2-qué-se-trabaja-en-el-cole-a-los-4-años)), las reglas de la
investigación ([§6](investigacion/investigacion.md#6-reglas-que-salen-de-todo-esto)), los riesgos abiertos
([riesgos](harness/riesgos.md)) y lo que se vio al probarlo.

El **tamaño** es el trabajo de un agente: **pequeña**, unas horas; **mediana**, un día; **grande**, varios días.

**Hechas** desde entonces: la 1.1, escribir su nombre ([ADR 0017](adr/0017-escribir-su-nombre.md)); la 1.11, la
emoción del día ([ADR 0019](adr/0019-la-emocion-del-dia.md)); la 2.1, que se adapte a lo que sabe
([ADR 0020](adr/0020-se-adapta-a-lo-que-sabe.md)); la 1.2, rimas con Bruno ([ADR 0021](adr/0021-rimas-con-bruno.md)); la 1.13, la F, la B y la R
([ADR 0022](adr/0022-la-f-la-b-y-la-r.md)); la 1.5, la cuenta atrás ([ADR 0023](adr/0023-la-cuenta-atras.md)); la 2.4, las
pegatinas para el cuarto ([ADR 0024](adr/0024-pegatinas-para-el-cuarto.md)); la 1.9, ordena la historia
([ADR 0025](adr/0025-ordena-la-historia.md)); la 1.4, el anterior y el siguiente
([ADR 0026](adr/0026-el-anterior-y-el-siguiente.md)); la 1.8, dentro y fuera ([ADR 0028](adr/0028-dentro-y-fuera.md)); la 1.6, sumas hasta 3
([ADR 0029](adr/0029-sumas-hasta-3.md)); la 1.12, la asamblea de Bruno ([ADR 0030](adr/0030-la-asamblea-de-bruno.md)); y las 5.3, 5.4, 5.5 y 5.8,
de calidad: lo que faltaba por probar en el iPhone, lo que rozaba la Dynamic Island, la prueba de sonido del Mac y los
recorridos del iPhone de un tirón. Y una que no estaba aquí, la pantalla de inicio
([ADR 0018](adr/0018-la-pantalla-de-inicio.md)).

## Las cinco con las que empezaría

1. **Escribir su nombre** (1.1): es lo primero que escriben a los 4 años, y es suyo. Hecha.
2. **La emoción del día, con su foto** (1.11): reutiliza la isla de las emociones y su foto, y es la rutina de su clase.
   Hecha.
3. **Que el juego se adapte a lo que sabe** (2.1): así le dura más mientras crece. Hecha.
4. **Rimas con Bruno** (1.2): completa lo de oír las palabras, junto a las palmadas y la sílaba del principio. Hecha.
5. **Antes de dárselo, probar en su iPhone** (5.1, 5.2 y 5.4): lo que solo se puede ver allí.

## 1. Lo que se aprende en el cole y aún no está

- **1.1 Escribir su nombre.** Las letras de su nombre, las del banderín, se trazan una a una en el orden del cole, y al
  acabar Bruno se lo lleva al banderín: «¡Ya sé escribir tu nombre!». La voz no puede decir su nombre, pero sí celebrarlo.
  Hace falta el trazo de las mayúsculas que tenga su nombre real (hoy solo hay doce). *Grande. Hecha
  ([ADR 0017](adr/0017-escribir-su-nombre.md)).*
- **1.2 Rimas con Bruno.** «¡Gato! ¿Qué rima con gato?»: de tres dibujos, el que acaba igual (pato). Bruno se inventa rimas
  tontas que dan risa. Como la isla de las palabras, con parejas nuevas. *Mediana. Hecha, como segunda actividad de esa
  isla ([ADR 0021](adr/0021-rimas-con-bruno.md)).*
- **1.3 Trazos antes de las letras.** Llevar a Bruno a casa por un camino con el dedo: rectas, diagonales, ondas, bucles y
  espirales, en el orden de la grafomotricidad del cole. Ayuda a los trazos de las letras. *Mediana.*
- **1.4 El anterior y el siguiente.** Bruno salta por una recta numérica del 0 al 10 y pregunta qué número va antes o
  después. En el aula de 4 años se usa la recta, y el juego solo cuenta. *Mediana. Hecha, en la isla de los números,
  por turnos con la cuenta atrás ([ADR 0026](adr/0026-el-anterior-y-el-siguiente.md)).*
- **1.5 La cuenta atrás.** El cohete de Bruno despega cuando ella cuenta del 10 al 0, tocando las luces. Corta y muy
  divertida. *Pequeña. Hecha, como segunda actividad de la isla de los números
  ([ADR 0023](adr/0023-la-cuenta-atras.md)).*
- **1.6 Sumas con dibujos hasta 3.** «Bruno tiene una fresa y le das dos más. ¿Cuántas tiene?». *Mediana. Hecha, en la isla
  de los números, por turnos con la cuenta atrás y la recta ([ADR 0029](adr/0029-sumas-hasta-3.md)).*
- **1.7 Primero, segundo y tercero.** Una carrera de animales: ¿quién llega primero? *Pequeña.*
- **1.8 Grande y pequeño, dentro y fuera, arriba y abajo.** «Esconde a Bruno dentro de la caja», «pon la pelota encima de la
  mesa». *Mediana. Hecha, como segunda actividad de la isla de las formas, con dos cajas y una mesa
  ([ADR 0028](adr/0028-dentro-y-fuera.md)).*
- **1.9 Ordena la historia.** Tres viñetas de algo de cada día (lavarse los dientes, plantar una semilla) para ordenar:
  primero, después, al final. *Mediana. Hecha, en la isla de las emociones ([ADR 0025](adr/0025-ordena-la-historia.md)).*
- **1.10 Hábitos.** Lavarle los dientes a Bruno, vestirle según el tiempo que hace, llevar la basura a su contenedor. Va
  bien como isla nueva, la de la casa. *Grande.*
- **1.11 La emoción del día, con su foto.** Al llegar al cuarto, como en la asamblea: «¿Cómo te sientes hoy?», y ella pone su
  foto en el bote del color de cómo se siente. Bruno pone la suya. Si no hay foto, una carita. *Pequeña. Hecha
  ([ADR 0019](adr/0019-la-emocion-del-dia.md)).*
- **1.12 La asamblea de Bruno.** El día de la semana (con la fecha del aparato) y el tiempo que hace, que elige ella mirando
  por la ventana: sol, nubes, lluvia o nieve. *Mediana. Hecha, después de la emoción del día
  ([ADR 0030](adr/0030-la-asamblea-de-bruno.md)).*
- **1.13 Más consonantes.** La F, la B y la R (la efe de foca, la be de barco, la erre de ratón), entrando por pasos detrás
  de la T, la N y la D. *Mediana. Hecha ([ADR 0022](adr/0022-la-f-la-b-y-la-r.md)).*
- **1.14 Colores y números en inglés.** La ley pide un primer contacto con una lengua extranjera a esta edad. Con la voz en
  inglés, aparte de lo demás. *Mediana. Decide Fangusk si se quiere.*

## 2. Jugar más y mejor, sin trampas

- **2.1 Que se adapte a lo que sabe.** Si acierta mucho, cuatro galletas o cuatro cartas en vez de tres; si le cuesta, dos.
  Sin decírselo nunca y sin niveles ni puntos. *Mediana. Hecha, en Comeletras, los globos, las caritas y las cartas
  ([ADR 0020](adr/0020-se-adapta-a-lo-que-sabe.md)).*
- **2.2 Bruno sugiere una isla.** Al llegar al mapa, Bruno señala la isla que menos ha visitado, sin obligar. *Pequeña.*
- **2.3 El armario de Bruno.** En el cuarto, elegir qué regalos lleva puestos y cambiarle el color o la cabeza más adelante.
  *Pequeña.*
- **2.4 Pegatinas para el cuarto.** Cada isla terminada da una pegatina que ella pega donde quiera en el cuarto de Bruno.
  Son premios con sentido, que dice la regla 9; sin coleccionar por coleccionar. *Mediana. Hecha, una por isla
  ([ADR 0024](adr/0024-pegatinas-para-el-cuarto.md)).*
- **2.5 Un cuento para dormir.** Al acabarse los minutos, el narrador cuenta un cuento de un minuto en el que Bruno es el
  protagonista, como en la propuesta 4. *Grande, por los dibujos.*
- **2.6 Música suave que se puede quitar.** La regla 8 la contempla y el juego no tiene música: un fondo tranquilo, distinto
  en cada isla, con su interruptor en la zona de mayores. *Pequeña.*
- **2.7 Jugar con un mayor.** Momentos para dos: en «Habla con Bruno», que el mayor diga una palabra y ella dé las
  palmadas; o que Bruno le pida enseñarle al mayor lo que ha aprendido. Ayuda a que lo vean como algo que se hace juntos.
  *Pequeña.*

## 3. Más suyo

- **3.1 Su foto en más sitios.** En la barca con Bruno, en el mapa, y en la tarjeta de la misión para casa. *Pequeña.*
- **3.2 Que Bruno diga su nombre.** Con la voz del propio teléfono, sin conexión, solo para su nombre; o que un mayor lo
  grabe una vez. La primera sonaría distinta a la de Bruno, y la segunda se parece a las voces de la familia, que se
  descartaron. *Mediana. Decide Fangusk.*
- **3.3 Su animal favorito.** Se elige en la zona de mayores y vive en el cuarto con Bruno, o sale como palabra en las islas.
  *Pequeña.*

## 4. Para los mayores

- **4.1 Resumen de la semana.** En la zona de mayores, qué ha practicado, qué le cuesta más (lo que más falla) y cuántos
  minutos juega cada día. Sin salir del aparato. *Pequeña.*
- **4.2 Elegir qué letras e islas salen.** Por si aún no han visto la M en clase, o para quitar una isla que ya domina.
  *Pequeña.*
- **4.3 Bruno más despacio.** Un ajuste de velocidad de la voz, por si a ella le cuesta seguirlo. *Mediana.*
- **4.4 Que la partida no se pierda al cambiar de móvil.** Comprobar que la copia del propio iPhone la guarda y, si no, poder
  exportarla. *Pequeña.*
- **4.5 Sin la frase de Apple en el permiso.** Comprobar la palabra con la API de iOS 26 (SpeechAnalyzer), que reconoce en
  el iPhone, y ver si así ya no sale el permiso de reconocimiento de voz con su frase fija. Solo vale si su iPhone tiene
  iOS 26. *Mediana. Decide Fangusk.*

## 5. Calidad y entrega

- **5.1 Medir en su iPhone.** Que vaya fluido y cuánta batería gasta en diez minutos ([R-06](harness/riesgos.md)); si fuera
  a tirones, guardar en caché los fondos. *Pequeña el día de la entrega.*
- **5.2 Probar con sonido en un aparato de verdad.** Su voz en «Habla con Bruno», la comprobación de la palabra, los
  permisos y que todo suene por el altavoz. *Pequeña el día de la entrega.*
- **5.3 Lo que falta por probar.** Tocar la foto, «Cambiar su foto», «Volver a empezar» con foto y el final de la isla de las
  palabras en el iPhone. *Pequeña, con el conductor de pruebas. Hecha: los recorridos `testPhotoAndRestart` y
  `testRhymes`.*
- **5.4 Detalles vistos en el iPhone.** Con el iPhone girado a la derecha, la bandera de la isla de los colores roza la
  Dynamic Island, y la isla roza el borde de una galleta en Comeletras. *Pequeña. Hecha, con el globo de la derecha, el
  último bote de la emoción del día y el panel de la cuenta atrás, que también la rozaban.*
- **5.5 La prueba de sonido del Mac.** Falla a veces cuando el Mac está muy cargado; conviene que lo tolere. *Pequeña.
  Hecha: fallaba por el primer toque, que espera a que se despierte la tarjeta de sonido; ahora mira lo que suele tardar
  un toque y que ninguno se quede sin atender.*
- **5.6 Fotos giradas en Android 8.** Allí una foto girada solo por su EXIF saldría torcida. *Pequeña.*
- **5.7 Un mapa con más sitio.** Con seis islas, el mapa está lleno: antes de una isla nueva hace falta otra parte del mar, a
  la que se llega en la barca. *Mediana.*
- **5.8 Los recorridos del iPhone, de un tirón.** Un solo comando que cree el simulador, siembre la partida, pase los cuatro
  recorridos y lo borre. *Pequeña. Hecha: `tools/conductor-ios/recorridos.sh`, con los nueve.*

## Lo que no está aquí, y por qué

- **Voces nuevas o de la familia**: Fangusk decidió quedarse con las actuales ([ADR 0004](adr/0004-voces-provisionales.md)).
- **Rachas, premios diarios o avisos para volver**: van contra la regla 4 y la investigación sobre diseño manipulador.
- **Ver su progreso desde otro móvil, o cualquier cosa por internet**: saca datos de una menor del aparato
  ([ADR 0005](adr/0005-un-regalo-sin-conexion.md)).
- **Más minutos de juego**: los eligen los mayores. La Asociación Española de Pediatría recomienda ninguna pantalla antes
  de los 6 años, y el comité de expertos del Gobierno, solo de forma ocasional y acompañada
  ([investigación §4](investigacion/investigacion.md#4-pantallas-a-los-4-años-lo-que-dicen-los-pediatras)).
