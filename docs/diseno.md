# Mi monstruo Bruno · página de diseño

> **Sin validar todavía.** La redacta el agente con la propuesta que eligió Fangusk y con la primera versión construida.
> Lo que dijo él va entre comillas; lo demás es propuesta hasta que lo valide ([ADR](adr/README.md)).

## Visión

Un juego para la sobrina de Fangusk, de 4 años, que este curso ha empezado el cole en España. De un huevo nace Bruno, un
monstruo que no sabe nada de nada, y ella le enseña las vocales, a contar y a trazar las letras. Es la propuesta 3 de la
[investigación](investigacion/investigacion.md) ([maqueta](investigacion/propuestas.html)), con el monstruo llamado Bruno en
lugar de Coco ([ADR 0002](adr/0002-mi-monstruo-bruno.md)).

Los pilares, que deciden qué entra y qué no:

1. **Ella es la profe**: Bruno pide y ella le enseña. Se esfuerzan más enseñando a un personaje que aprendiendo para sí
   mismos (el «efecto protegido», [investigación §3](investigacion/investigacion.md#3-qué-dice-la-investigación-sobre-diseñar-para-esta-edad)).
2. **Sin leer**: todo lo dice una voz y se entiende con gestos grandes. El texto es solo para los mayores.
3. **Equivocarse no castiga**: sin «¡mal!», vidas ni cronómetro. Bruno pone cara rara y lo vuelve a pedir.
4. **Corto y con final**: unos 10 minutos y Bruno se va a dormir hasta mañana ([ADR 0006](adr/0006-diez-minutos-y-a-dormir.md)).
5. **Limpio**: sin anuncios, compras, cuentas ni internet, y nada sale del dispositivo
   ([ADR 0005](adr/0005-un-regalo-sin-conexion.md)).

## Lo que pidió Fangusk (2026-11-02)

1. «Quiero hacerle un videojuego para Android y iOS a mi sobrina pequeña que tiene 4 añitos y ha entrado este año en el
   colegio, en España. Quiero que sea un videojuego educativo y divertido, investiga cuales son las opciones del mercado
   actual mas populares y elaborame varias propuestas en un mockup uniendo los mejores aspectos que hayas podido
   encontrar. Cuando elija una propuesta, comenzaremos con el desarrollo, pero por el momento solo quiero que investigues».
2. Después de leer las cuatro propuestas: «Comenzamos con la propuesta 3 Mi monstruo, pero en lugar de Coco, llamemosle
   Bruno».
3. Sobre cómo construirlo: «basate en como se está cronstruyendo CaroFarty y como se integra con Mayeutic usando KMP»
   ([ADR 0001](adr/0001-kotlin-multiplatform-como-carofarty.md)). Creó el proyecto, MyMonsterBruno, con el asistente de
   Kotlin Multiplatform de Android Studio: «ya he creado el proyecto».

Después de probar la primera versión (2026-11-03): «He probado un poco y me gusta». Pidió que Bruno hablara «un poco más
lento, habla demasiado rápido para un niño de 4 años» ([ADR 0004](adr/0004-voces-provisionales.md)) y un narrador «más
amigable», menos robótico. Para seguir eligió «Más islas primero» y, entre las que le propuso el agente, «Colores»: «Bruno
pide un color (“¡Quiero el globo rojo!”) y después series: rojo, azul, rojo… ¿cuál va ahora?»
([ADR 0007](adr/0007-la-isla-de-los-colores.md)).

La propuesta 3, tal como la leyó (en la [maqueta](investigacion/propuestas.html#p3)):

- **Una partida, unos 10 minutos**: el saludo («¡tengo hambre de letras!»), el mapa con la isla del día, minijuegos en los
  que ella le enseña (darle la letra que pide, contarle fresas, construirle la casa con formas, trazarle el camino), Bruno
  crece y gana un gorro o unas gafas, «Habla con Bruno» (ella dice una palabra y él la repite dando palmas por sílabas) y a
  dormir: «¡Hasta mañana!». Nunca se pone triste si ella no juega.
- **Qué aprende**: vocales y consonantes en mayúscula (M, P, L, S…), el sonido inicial, sílabas con palmadas y trazos;
  números hasta 10, formas, colores y series; emociones (Bruno cambia de color según cómo se siente) y cuidar de otro.
- **La primera versión**: «el nacimiento, Comeletras con las vocales, contar hasta 5, trazos y un mapa con dos islas».
- **Con las cuatro piezas comunes** a todas las propuestas: voces de la familia, rincón de la calma, misión para casa y zona
  de mayores.

## Cómo es una partida

1. **Nace tu monstruo** (solo la primera vez). Es de noche y hay un huevo. Cada toque lo hace temblar y lo agrieta; al
   quinto se rompe y sale Bruno: «¡Hola! ¡Soy Bruno! No sé nada de nada... ¿Me enseñas?». Ella elige su color (menta,
   rosa, naranja o morado) y lo que lleva en la cabeza (cuernos, orejas o antenas), y el botón verde se lo queda.
2. **El cuarto de Bruno**, la casa. Bruno la saluda y la invita a la isla. Si le toca, se ríe con cosquillas. El botón
   grande lleva al mapa y el globo, al rincón de la calma. Arriba cuelga un banderín con su nombre, si los mayores lo han
   escrito, o con estrellas.
3. **El mapa**. Bruno va en su barca. Hay tres islas con algo que hacer: la de las vocales, la de los números y la de los
   colores. Las demás están «muy lejos. ¡Pronto iremos!». Una isla terminada lleva su bandera.
4. **La isla de las vocales**:
   - **Comeletras**. «¡Tengo hambre de letras!». Bruno pide una vocal con su palabra («¡Quiero la a! La a de avión») y en
     el plato hay tres galletas con vocales. Ella le da una, arrastrándola a la boca o tocándola. Si es la que quería:
     «¡Ñam!». Si no, Bruno pone cara rara, la galleta vuelve al plato y él dice cuál era y cuál quiere («Huum... Esa es la
     e. ¡Yo quiero la a!»). Son cinco rondas, cada vocal una vez.
   - **Trazar**. Dos vocales en mayúscula, las que menos ha trazado, con los trazos en el orden del cole. Un punto verde
     dice dónde empezar y una flecha, hacia dónde. El dedo no tiene que ir exacto: el camino perdona. Al acabar la letra,
     Bruno la celebra: «¡La a! ¡Ya sé hacer la a!».
5. **La isla de los números**: **las fresas**. «Bruno quiere tres fresas. ¿Se las das?». Ella toca fresas del arbusto y
   cada una se cuenta en voz alta. Al llegar al número, Bruno se las come. La tarjeta enseña el número, los dedos de una
   mano y sus puntos. Son cinco rondas, del 1 al 5, y la primera nunca es el 5.
6. **La isla de los colores** ([ADR 0007](adr/0007-la-isla-de-los-colores.md)):
   - **Los globos**. «¡Quiero el globo rojo!». Hay tres globos, y ella le da uno a Bruno arrastrándolo hasta él o
     tocándolo. El que acierta se queda atado en su mano, así que al final tiene un ramo. Si no es el suyo, Bruno dice de
     qué color es y lo vuelve a pedir, y desde ese momento lo piensa en un bocadillo, como pista. Son cinco rondas.
   - **Las series**. Una fila de globos atados a una cuerda, como rojo, azul, rojo, azul, y un hueco. El narrador la lee y
     cada globo salta al oír su nombre: «¿Cuál va ahora?». Ella elige entre tres. Si no es, Bruno dice de qué color es y
     el narrador vuelve a leer la fila. Son cuatro series, dos de dos colores y dos de tres.
7. **Bruno crece**. Al terminar una isla hay un regalo. Con un toque se abre la caja y un accesorio vuela hasta Bruno, que
   ya lo lleva siempre: gorro, gafas, pajarita, flores, bufanda y corona. Cada dos islas, además, crece un poco, hasta tres
   veces. Con los seis accesorios, la caja trae chispas y un gracias.
8. **A dormir**. Cuando se acaban los minutos del día, Bruno se va a dormir en cuanto ella termina lo que está haciendo,
   nunca a medias: enseguida si están en el cuarto o en el mapa, y al terminar la isla, con su regalo, si están en una.
   Bosteza, le da una **misión para casa** que va con lo que han hecho (buscar algo que empiece por la a, contar las sillas
   de la cocina o buscar algo amarillo) y le dice «¡Hasta mañana!». Si se abre otra vez ese día, Bruno sigue dormido:
   «Huum... Mañana jugamos más».

Fuera del camino:

- **El rincón de la calma**, el de su clase. Mientras mantiene el dedo, el globo se hincha y ella toma aire. Al soltar, el
  globo se deshincha despacio. Son tres respiraciones con Bruno.
- **La zona de mayores**, detrás del candado de la esquina, que se abre manteniendo el dedo 2,5 segundos. En ella se puede
  escribir su nombre (para el banderín), elegir los minutos al día (5, 10, 15 o 20), dar «10 minutos más, solo hoy»,
  quitar la voz o los efectos, ver lo que ha practicado (galletas por vocal, letras trazadas, fresas por número, islas y
  regalos) y volver a empezar, que devuelve a Bruno al huevo y conserva los ajustes. Es la única pantalla con texto.
- **Dos botones pequeños** en las esquinas de arriba. La casa vuelve un paso hacia el cuarto, desde el mapa, los juegos y
  la calma. El altavoz, en los juegos, repite lo que pide Bruno. Son pequeños a propósito, porque no son para jugar.

## Las reglas, y cómo las cumple la primera versión

Salen de la [investigación §6](investigacion/investigacion.md#6-reglas-que-salen-de-todo-esto).

| Regla | En la primera versión |
|---|---|
| Sin leer | Cada pantalla se explica con voz. Las letras y los números del juego son lo que se aprende, no instrucciones |
| Para dedos de 4 años | Solo tocar, arrastrar y mantener: sin doble toque, pellizcos ni prisas. En un móvil de 6,8″ (el emulador Pixel 10 Pro XL), el botón de jugar mide 2,4 cm, las galletas 1,8 cm y los colores 1,7 cm. Cada uno acepta el dedo hasta 0,3 cm más allá de su borde. Los botones de las esquinas miden 1 cm a propósito. En una tableta, todo es más grande |
| Equivocarse no castiga | Bruno nunca dice «¡mal!». Pone cara rara, devuelve la galleta y lo vuelve a pedir. No hay vidas, puntos ni tiempo |
| Unos 10 minutos con final | Límite por día, 10 minutos de serie. Bruno se duerme al terminar lo que ella esté haciendo. No hay rachas, cuentas atrás ni «¡no te vayas!» ([ADR 0006](adr/0006-diez-minutos-y-a-dormir.md)) |
| Para jugar acompañada | La misión para casa de cada noche. La voz de la familia y «Habla con Bruno», que son para hacer juntos, llegan después |
| Lo del cole | Vocales en mayúscula, sus trazos en el orden del cole, y contar del 1 al 5 con los dedos y los puntos, en castellano de España |
| Hecho para ella | Su nombre en el banderín y su Bruno, con el color y la cabeza que ella elige. La voz todavía no dice su nombre ([ADR 0004](adr/0004-voces-provisionales.md)) |
| Tranquilo | Colores suaves, sonidos cortos que solo suenan al tocar y animaciones lentas. La voz y los efectos se pueden quitar |
| Premios con sentido | Los accesorios de Bruno se ganan terminando islas. No hay cofres sorpresa ni nada que comprar |
| Limpio | Sin anuncios, compras, cuentas ni internet. En Android no pide el permiso de internet. Todo lo guarda el dispositivo ([ADR 0005](adr/0005-un-regalo-sin-conexion.md)) |

## Lo que trae y lo que falta

**Trae** lo que la propuesta pide a la primera versión: el nacimiento, Comeletras con las vocales, contar hasta 5, los
trazos y el mapa con dos islas. De las piezas comunes, el rincón de la calma, la misión para casa y la zona de mayores
(sin foto ni voces). Y la primera isla de M3, la de los colores, con sus globos y sus series.

**Falta**, de la propuesta 3 que eligió Fangusk. El orden y el alcance de cada pieza los decide él.

- **Las voces de la familia**: que la tía, mamá o el abuelo graben lo que dice Bruno o el narrador. Pide el micrófono en
  la zona de mayores. Hasta entonces suena una voz sintética provisional ([ADR 0004](adr/0004-voces-provisionales.md)).
- **Habla con Bruno**: ella dice una palabra y Bruno la repite con voz graciosa y una palmada por sílaba (MA-RI-PO-SA).
- **Más islas**: las consonantes (M, P, L, S…), el sonido inicial, los números hasta 10, las formas («construirle la casa
  con formas») y las emociones, con Bruno cambiando de color según cómo se siente.
- **Su foto** y que la voz diga **su nombre**.

## Pantalla, dibujo, voz y sonido

- **En horizontal**, en móvil y en tableta. Todo se dibuja en un escenario de 800 × 400 unidades que se escala para caber
  y cuyo fondo se alarga hasta los bordes de cualquier pantalla ([ADR 0003](adr/0003-escenario-horizontal-dibujado.md)).
- **Bruno y todo lo demás se dibujan con vectores**, sin imágenes: Bruno respira, parpadea, mira y cambia de cara (ocho
  bocas, cuatro ojos, tres posturas de brazos).
- **Las voces** son las de un narrador y la de Bruno, algo más aguda y más despacio, a unas 4 sílabas por segundo. Hay
  116 frases pregrabadas con la voz del sistema del Mac y **los efectos** (13) se sintetizan desde cero
  ([ADR 0004](adr/0004-voces-provisionales.md)). Las frases están en [`voces/guion.tsv`](../voces/guion.tsv).
- **La letra** de la zona de mayores, de los números y del banderín es Andika, una tipografía pensada para quien aprende a
  leer (licencia OFL, en [`licencias/`](../licencias/Andika-OFL.txt)).
