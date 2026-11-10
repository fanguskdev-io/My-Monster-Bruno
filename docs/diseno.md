# Mi amigo Bruno · página de diseño

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
amigable», menos robótico. Como en su Mac no hay una Mónica mejorada, prefirió dejarlo como está. Después, «Continúa», y la isla de las formas: «Construirle la casa a Bruno: ella arrastra un cuadrado, un
triángulo, un círculo y un rectángulo a su hueco, y Bruno dice el nombre de cada forma»
([ADR 0008](adr/0008-la-isla-de-las-formas.md)). Y al ver que no había botón para salir: «Sí, añádelo», en la zona de
mayores. Después, otra vez «Continúa», y ampliar las islas que ya había: «consonantes (M, P, L, S) con su palabra en
Comeletras y en los trazos, junto a las vocales», y «fresas hasta el 10, con las dos manos en la tarjeta»
([ADR 0009](adr/0009-mas-letras-y-numeros-hasta-el-diez.md)). Y por último la isla de las emociones, tal como se la
propuso el agente: «Así» ([ADR 0010](adr/0010-la-isla-de-las-emociones.md)). Y para M2: «para M2 no vamos a utilizar
las voces de la familia, seguiremos con las voces actuales» ([ADR 0004](adr/0004-voces-provisionales.md)), y «Habla con
Bruno» como se lo propuso el agente, con micrófono: «Sí, así» ([ADR 0011](adr/0011-habla-con-bruno.md)). Para la
entrega en su iPhone, «Mejor ad hoc» ([ADR 0012](adr/0012-la-entrega-ad-hoc.md)). Y de lo que faltaba, «La isla lejana: el
sonido inicial», tal como se la propuso el agente: «Así» ([ADR 0013](adr/0013-la-isla-de-las-palabras.md)). Después, de
lo que quedaba, «T, N y D» ([ADR 0014](adr/0014-la-t-la-n-y-la-d.md)), que Bruno compruebe la palabra que le dicen
([ADR 0015](adr/0015-bruno-comprueba-la-palabra.md)) y su foto, tal como se la propuso el agente: «Así»
([ADR 0016](adr/0016-su-foto.md)). Para seguir eligió «Más islas primero» y, entre las que le propuso el agente, «Colores»: «Bruno
pide un color (“¡Quiero el globo rojo!”) y después series: rojo, azul, rojo… ¿cuál va ahora?»
([ADR 0007](adr/0007-la-isla-de-los-colores.md)).

El 2026-11-04 pidió una batería de mejoras y ampliaciones ([ideas](ideas.md)) y, de ellas, «Escribir su nombre», tal como
se la propuso el agente: «Así» ([ADR 0017](adr/0017-escribir-su-nombre.md)). Y una pantalla de inicio: «Añade tambien una
pantalla de Splash para cuando se inicia el juego, queda un poco soso iniciar el juego como tal, básate en como lo hace
CaroFarty» ([ADR 0018](adr/0018-la-pantalla-de-inicio.md)). Y otro nombre para el juego: «Sustituye: Mi monstruo Bruno
por: Mi amigo Bruno» ([ADR 0002](adr/0002-mi-monstruo-bruno.md)). Después, «Continua con el desarrollo» y, de las
siguientes ideas, «La emoción del día», tal como se la propuso el agente ([ADR 0019](adr/0019-la-emocion-del-dia.md)). Y
al día siguiente, «continua» y, de otras cuatro, «Que se adapte» a lo que sabe
([ADR 0020](adr/0020-se-adapta-a-lo-que-sabe.md)); y después, «Rimas con Bruno», en la isla de las palabras
([ADR 0021](adr/0021-rimas-con-bruno.md)). Al día siguiente, «Continua» y «La F, la B y la R»
([ADR 0022](adr/0022-la-f-la-b-y-la-r.md)), y después «La cuenta atrás» ([ADR 0023](adr/0023-la-cuenta-atras.md)). Y
cuatro seguidas: un repaso en su iPhone, «Pegatinas para el cuarto» ([ADR 0024](adr/0024-pegatinas-para-el-cuarto.md)),
«Ordena la historia» ([ADR 0025](adr/0025-ordena-la-historia.md)) y «El anterior y el siguiente»
([ADR 0026](adr/0026-el-anterior-y-el-siguiente.md)). Al probarlo en el Mac, que también comprobara la palabra
([ADR 0027](adr/0027-el-mac-tambien-comprueba-la-palabra.md)); y otras cuatro seguidas: «Dentro y fuera» ([ADR 0028](adr/0028-dentro-y-fuera.md)),
«Sumas hasta 3» ([ADR 0029](adr/0029-sumas-hasta-3.md)), «La asamblea de Bruno» ([ADR 0030](adr/0030-la-asamblea-de-bruno.md)) y «Trazos antes de las letras» ([ADR 0031](adr/0031-trazos-antes-de-las-letras.md)).

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

Al abrir el juego, **la pantalla de inicio** ([ADR 0018](adr/0018-la-pantalla-de-inicio.md)): Bruno se mece en su barca
(antes de nacer, su huevo) mientras pasan las islas por el horizonte, y las letras de BRUNO caen a su sitio. La voz dice
«¡Mi amigo Bruno!» y «¡Toca para empezar!», y un toque en cualquier sitio abre el juego donde estaba. Si los mayores
han elegido su foto, ella va sentada a su lado en la barca, aquí y en el mapa ([ADR 0034](adr/0034-su-foto-en-mas-sitios.md)).

La primera vez de cada día, antes del cuarto, **la emoción del día** ([ADR 0019](adr/0019-la-emocion-del-dia.md)), como
en la asamblea de su clase: Bruno saluda y pregunta «¿Cómo te sientes hoy?», y ella lleva su foto (o una carita, si no
hay foto) a uno de los cinco botes de colores: alegría, tristeza, enfado, miedo y calma. Tocar un bote dice su nombre.
Bruno le contesta con cariño («Vaya... Te doy un abrazo muy grande») y pone la suya en el bote de la alegría, porque ella
ha venido a jugar. Se puede saltar con la casa. El día que nace Bruno no se pregunta.

Después, **la asamblea de Bruno** ([ADR 0030](adr/0030-la-asamblea-de-bruno.md)): en la pared del cuarto, el calendario de la semana con la letra y el
color de cada día, como en clase. «¡Es la hora de la asamblea! ¿Qué día es hoy?»; Bruno lo dice, «¡Hoy es martes!», su
tarjeta llama y ella la toca; el narrador dice los de al lado: «Ayer fue lunes y mañana será miércoles». Luego, «¿Qué
tiempo hace hoy? Mira por la ventana y dímelo»: ella mira por la suya y toca sol, nubes, lluvia o nieve, Bruno dice algo
de él y la ventana del cuarto lo enseña el resto del día. El día sale de la fecha del aparato; el tiempo, de ella.

1. **Nace tu monstruo** (solo la primera vez). Es de noche y hay un huevo. Cada toque lo hace temblar y lo agrieta; al
   quinto se rompe y sale Bruno: «¡Hola! ¡Soy Bruno! No sé nada de nada... ¿Me enseñas?». Ella elige su color (menta,
   rosa, naranja o morado) y lo que lleva en la cabeza (cuernos, orejas o antenas), y el botón verde se lo queda.
2. **El cuarto de Bruno**, la casa. Bruno la saluda y la invita a la isla. Si le toca, se ríe con cosquillas. El botón
   grande lleva al mapa, el micrófono a «Habla con Bruno» y el globo, al rincón de la calma. Arriba cuelga un banderín con
   su nombre, si los mayores lo han escrito, o con estrellas, y en la pared, su foto en un marco, si la han elegido: la
   primera vez, Bruno la mira y dice «¡Mira! ¡Eres tú!» ([ADR 0016](adr/0016-su-foto.md)). El banderín con su nombre
   lleva a escribirlo; mientras no lo haya escrito, Bruno se lo pide cada día que abre el juego: «¿Me enseñas a escribir
   tu nombre? Toca el banderín». Y la primera vez que termina cada isla, Bruno le guarda **una pegatina** de esa isla en la
   alfombra ([ADR 0024](adr/0024-pegatinas-para-el-cuarto.md)): ella la arrastra a la pared y la pega donde quiera, menos
   encima de lo que se toca; y la mueve cuando quiera. La percha del suelo lleva a **su armario** ([ADR 0032](adr/0032-el-armario-de-bruno.md)):
   ella elige qué regalos lleva, uno de cada sitio o ninguno, y le cambia el color o lo de la cabeza, como al nacer.
   Si los mayores han elegido **su animal favorito** ([ADR 0033](adr/0033-su-animal-favorito.md)), vive en una balda a la derecha de Bruno:
   la primera vez Bruno lo presenta y, si lo toca, salta y Bruno dice que lo cuida.
3. **El mapa**. Bruno va en su barca. Hay seis islas: la de las letras, la de los números, la de los colores, la de las
   formas, la de las emociones y, la más pequeña, arriba a la derecha, la de las palabras. Una isla terminada lleva su
   bandera. La primera vez que llega al mapa cada vez que se abre el juego, Bruno **sugiere una isla**, la que menos ha
   terminado: «¡A mí me apetece ir a la isla de las formas! ¿Y a ti?», la mira y alrededor salen unas ondas suaves; pero
   cualquier otra vale igual ([ADR 0035](adr/0035-bruno-sugiere-una-isla.md)). Junto a la barca, un botón con una flecha y
   una casita lleva navegando a **la otra parte del mar** ([ADR 0043](adr/0043-otra-parte-del-mar.md)), donde está la isla
   de la casa: «¡Vamos al otro lado del mar!»; el mismo botón, con la flecha al revés, vuelve. Al acabar una isla, el mapa
   abre en la parte del mar de esa isla.
4. **La isla de las letras**, las vocales, la M, la P, la L y la S, la T, la N y la D, y la F, la B y la R
   ([ADR 0009](adr/0009-mas-letras-y-numeros-hasta-el-diez.md), [ADR 0014](adr/0014-la-t-la-n-y-la-d.md),
   [ADR 0022](adr/0022-la-f-la-b-y-la-r.md)):
   - **Comeletras**. «¡Tengo hambre de letras!». Bruno pide una vocal con su palabra («¡Quiero la a! La a de avión») y en
     el plato hay tres galletas con vocales. Ella le da una, arrastrándola a la boca o tocándola. Si es la que quería:
     «¡Ñam!». Si no, Bruno pone cara rara, la galleta vuelve al plato y él dice cuál era y cuál quiere («Huum... Esa es la
     e. ¡Yo quiero la a!»). Son cinco rondas, con las letras que menos le ha dado: el primer día, las vocales; luego
     entran la M, la P, la L y la S («¡Quiero la eme! La eme de manzana»); cuando las conoce, la T, la N y la D («¡Quiero
     la te! La te de tomate»), y después, la F, la B y la R («¡Quiero la be! La be de barco»).
   - **Trazar**. Dos letras en mayúscula, las que menos ha trazado (entran por los mismos pasos), con los trazos en el orden del
     cole. Un punto verde
     dice dónde empezar y una flecha, hacia dónde. El dedo no tiene que ir exacto: el camino perdona. Al acabar la letra,
     Bruno la celebra: «¡La a! ¡Ya sé hacer la a!».
   - **Los caminos de Bruno** ([ADR 0031](adr/0031-trazos-antes-de-las-letras.md)), el día siguiente que va, por turnos con trazar: «¡Bruno quiere volver a
     su casa! Llévalo con el dedo por el camino». Un camino de arena cruza un campo hasta su casa, y Bruno anda bajo su
     dedo mientras no se salga; lo hecho queda en dorado y, si levanta el dedo, sigue desde donde lo dejó. Seis caminos
     en el orden del cole, recto, de arriba abajo, inclinado, de olas, con bucles y un caracol, tres por visita: los que
     menos ha hecho, las rectas primero.
5. **La isla de los números**: **las fresas**. «Bruno quiere tres fresas. ¿Se las das?». Ella toca fresas del arbusto y
   cada una se cuenta en voz alta. Al llegar al número, Bruno se las come. La tarjeta enseña el número, los dedos (desde
   el 6, una mano entera y la otra) y sus puntos. Son cinco rondas: del 1 al 5 hasta que los ha contado todos, y luego
   dos del 1 al 5 y tres del 6 al 10. Después, **la cuenta atrás** ([ADR 0023](adr/0023-la-cuenta-atras.md)): Bruno está en
   su cohete y ella cuenta del 10 al 0 tocando los números de un panel, desordenados, mientras el narrador los dice y se
   encienden las luces de la torre; el cero es un botón rojo grande: «¡Cero! ¡Despegue!», y el cohete sube entre el humo,
   sale por arriba y vuelve a bajar. Eso, un día que va a la isla; el siguiente día que va, **el anterior y el siguiente**
   ([ADR 0026](adr/0026-el-anterior-y-el-siguiente.md)): la recta de los números de su clase, del 0 al 10, y Bruno salta
   a una casilla y pregunta «¿Qué número va antes del cinco?» o «¿Qué número va después del cinco?», con un bocadillo que
   lo enseña como en la recta. Ella toca la casilla; si no es, el narrador le dice cuál ha tocado y que está justo al lado
   de Bruno. Son cinco preguntas. Y el siguiente día que va, **las sumas hasta 3** ([ADR 0029](adr/0029-sumas-hasta-3.md)):
   Bruno tiene una o dos fresas en su plato, «Dale dos más», ella se las da del arbusto, y «¿Cuántas fresas tiene
   ahora Bruno?»: toca el número, del 1 al 3; si no es, el narrador las cuenta todas con ella. La tarjeta de arriba
   enseña la suma con fresas. Y el siguiente, **primero, segundo y tercero**
   ([ADR 0037](adr/0037-primero-segundo-y-tercero.md)): una carrera de tres animales, cada uno en su calle. Bruno da la
   salida, «Preparados, listos... ¡ya!», y cada uno recibe su número al llegar a la meta: el 1 dorado, el 2 plateado y
   el 3 de bronce. «¿Quién ha llegado primero?», y ella toca el animal; si no es, el narrador le dice en qué puesto ha
   llegado ese y el que era llama. Son tres carreras, una pregunta por cada puesto. Así, la cuenta atrás, la recta, las
   sumas y la carrera se turnan, un día cada una.
6. **La isla de los colores** ([ADR 0007](adr/0007-la-isla-de-los-colores.md)):
   - **Los globos**. «¡Quiero el globo rojo!». Hay tres globos, y ella le da uno a Bruno arrastrándolo hasta él o
     tocándolo. El que acierta se queda atado en su mano, así que al final tiene un ramo. Si no es el suyo, Bruno dice de
     qué color es y lo vuelve a pedir, y desde ese momento lo piensa en un bocadillo, como pista. Son cinco rondas.
   - **Las series**. Una fila de globos atados a una cuerda, como rojo, azul, rojo, azul, y un hueco. El narrador la lee y
     cada globo salta al oír su nombre: «¿Cuál va ahora?». Ella elige entre tres. Si no es, Bruno dice de qué color es y
     el narrador vuelve a leer la fila. Son cuatro series, dos de dos colores y dos de tres.
7. **La isla de las formas** ([ADR 0008](adr/0008-la-isla-de-las-formas.md)): **la casa de Bruno**. Bruno pide cada
   pieza por su papel y su forma, «¡Ahora, el tejado! Dame el triángulo», y su hueco brilla en la casa. Ella le da una de
   las tres piezas arrastrándola al hueco o tocándola. La buena crece hasta su sitio y Bruno la nombra. Si no es, dice qué
   forma es y la vuelve a pedir. Son seis piezas: las paredes, el tejado, la puerta, dos ventanas y la chimenea.
   Después, **dentro y fuera** ([ADR 0028](adr/0028-dentro-y-fuera.md)): en el cuarto de Bruno hay una caja grande, una pequeña y una mesa, y el
   narrador pide poner a Bruno, pequeñito, o su pelota en un sitio: «Esconde a Bruno dentro de la caja grande», «Pon la
   pelota encima de la mesa», «Saca la pelota fuera de la caja». Ella lo arrastra; si no es, se queda un momento donde lo
   ha dejado mientras el narrador dice dónde está, vuelve y el sitio pedido brilla. Cinco rondas, cada sitio una vez.
8. **La isla de las emociones** ([ADR 0010](adr/0010-la-isla-de-las-emociones.md)). A Bruno le pasa algo («¡Oh, no! A
   Bruno se le ha caído el helado») y se pone del color de cómo se siente: alegría amarillo, tristeza azul, enfado rojo,
   miedo morado y calma verde. «¿Cómo se siente Bruno?»: ella elige entre tres caritas, y el narrador dice la emoción.
   Después le ayuda: aplaudir, un abrazo, respirar juntos manteniendo el dedo, encender la lámpara o acariciarle. Son
   cinco cosas, y la nana va la última. Después, **ordena la historia** ([ADR 0025](adr/0025-ordena-la-historia.md)): tres
   viñetas de algo de cada día, desordenadas, y tres huecos con su número; «¿Qué pasa primero?», y ella toca la viñeta, que
   vuela a su hueco, «¿Y después?», «¿Y al final?». Si pasa más tarde, «Huum... Eso pasa más tarde». Con las tres en orden,
   el narrador cuenta la historia mientras se iluminan. Dos de cuatro historias: la flor, los dientes, el bizcocho y el
   muñeco de nieve.
9. **La isla de las palabras** ([ADR 0013](adr/0013-la-isla-de-las-palabras.md)), en el rincón de los cuentos. Bruno
   piensa en un dibujo y lo dice a trozos, con una palmada por sílaba: «¡PA! ¡TO!». «¿Qué empieza como pato?». De tres
   cartas, ella toca la que empieza igual, y Bruno la dice a trozos también. Si empieza igual, las dos primeras sílabas
   salen en dorado: «¡Sí! Pato y pajarita empiezan por pa». Si no, «Huum... Luna empieza por lu. Busca otra que empiece
   por pa», y esa carta se aparta. Son cinco rondas de diez parejas (gafas y galleta, corona y corazón, mano y mariposa,
   casa y cama, pato y pajarita, luna y lupa, pelota y pera, gorro y gota, oso y ojo, regalo y reloj), las que menos ha
   encontrado. Después, **las rimas** ([ADR 0021](adr/0021-rimas-con-bruno.md)): «¡Gato! ¿Qué rima con gato?», el
   narrador nombra cada carta mientras salta, y ella toca la que termina igual. Si rima, «¡Sí! Gato y pato riman» y Bruno dice una rima tonta, «¡Un gato con
   zapato!», y se ríe; si no, «¿Rana? Huum... No termina igual», y esa carta se aparta. Son cinco rondas de diez parejas
   (gato y pato, luna y cuna, ratón y botón, rana y lana, sol y col, pez y nuez, mesa y fresa, foca y boca, llave y nave,
   seta y maleta), las que menos ha encontrado.
9b. **La isla de la casa** ([ADR 0044](adr/0044-la-isla-de-la-casa.md)), en la otra parte del mar: los hábitos. Primero,
   siempre, **los dientes**: la cara de Bruno de cerca, con la boca muy abierta y los dientes manchados: «¡He comido muchas
   fresas! ¿Me ayudas a lavarme los dientes?». Ella mueve el cepillo por encima y cada mancha se va con espuma; con todos
   limpios, «¡Qué dientes tan limpios! ¡Gracias!». Después, un día cada uno, **vestirse según el tiempo**: la ventana del
   cuarto enseña sol, lluvia o nieve, «¡Llueve! ¿Qué me pongo?», y de tres cosas (gafas de sol o gorra, paraguas o botas
   de agua, gorro de lana o bufanda) ella le lleva la buena; si no, «Eso es para cuando hace sol»; tres rondas, una por
   tiempo. O **cada cosa a su contenedor**: el amarillo para los envases, el azul para el papel y el verde para el vidrio;
   seis cosas, una a una, «¿Dónde va la lata?»; si no es, «La lata va al amarillo» y su contenedor llama.
Comeletras, los globos, las caritas, las cartas y las rimas **se adaptan a lo que sabe** ([ADR 0020](adr/0020-se-adapta-a-lo-que-sabe.md)):
si acierta a la primera las últimas cinco rondas de ese juego, la siguiente trae cuatro para elegir en vez de tres; si se
equivoca en más de la mitad, dos. Nunca se le dice, y no hay niveles ni puntos.

10. **Bruno crece**. Al terminar una isla hay un regalo. Con un toque se abre la caja y un accesorio vuela hasta Bruno, que
   ya lo lleva siempre: gorro, gafas, pajarita, flores, bufanda y corona. Cada dos islas, además, crece un poco, hasta tres
   veces. Con los seis accesorios, la caja trae chispas y un gracias.
11. **A dormir**. Cuando se acaban los minutos del día, Bruno se va a dormir en cuanto ella termina lo que está haciendo,
   nunca a medias: enseguida si están en el cuarto o en el mapa, y al terminar la isla, con su regalo, si están en una.
   Bosteza, le da una **misión para casa** que va con lo que han hecho (buscar algo que empiece por la a, contar las sillas
   de la cocina, buscar algo amarillo o algo redondo, contar cómo se ha sentido, buscar algo que empiece como su nombre)
   y le dice «¡Hasta mañana!». Si tiene foto, va prendida en la esquina de la tarjeta de la misión. Después, una vez al
   día, **un cuento para dormir** ([ADR 0045](adr/0045-un-cuento-para-dormir.md)): «¿Te cuento un cuento?», y el narrador
   cuenta uno de tres, cada noche el siguiente (la estrella que se cayó, el pez que no podía dormir y la luna y el gorro),
   con seis láminas de noche que pasan despacio, sin poder saltarlo: «Y colorín colorado, este cuento se ha acabado».
   Si se abre otra vez ese día, Bruno sigue dormido:
   «Huum... Mañana jugamos más».

Fuera del camino:

- **Habla con Bruno** ([ADR 0011](adr/0011-habla-con-bruno.md)), desde el cuarto. Bruno enseña un dibujo y pide su
  palabra: «¡Dime mariposa!». Ella mantiene el dedo en el micrófono grande mientras la dice, y Bruno la repite con la voz
  de ella, más aguda, como un monstruo. Después da una palmada por sílaba diciéndola («¡Ma! ¡Ri! ¡Po! ¡Sa!»), cada sílaba
  sale en una tarjeta de color y el narrador cuenta las palmadas. Son cuatro de diez palabras (de sol a elefante, de una a
  cuatro palmadas), las que menos ha dicho y de menos palmadas a más. Si no la oye, se lo pide otra vez; a la segunda, o
  si no hay micrófono, la dice Bruno y da las palmadas igual. Si el aparato entiende que ha dicho otra palabra, Bruno se
  lo dice con cariño: «Huum... Eso no es manzana. ¿Me dices manzana?», y a la segunda la dice él
  ([ADR 0015](adr/0015-bruno-comprueba-la-palabra.md)). Su voz suena una vez y se olvida: no se guarda ni sale del
  aparato, y lo que dice lo reconoce el propio aparato, sin conexión; en la versión del Mac, también
  ([ADR 0027](adr/0027-el-mac-tambien-comprueba-la-palabra.md)). El permiso del micrófono (y en el iPhone y en el Mac,
  el del reconocimiento de voz) se pide la primera vez que se entra. Al final, **un turno para dos**
  ([ADR 0036](adr/0036-jugar-con-un-mayor.md)): «¡Y ahora, una para jugar con un mayor! Que un mayor diga una palabra y
  tú das las palmadas». El mayor la dice en voz alta, cualquiera, y ella da una palmada por trozo tocando unas manos
  grandes; cada palmada sale en una tarjeta con su número, el narrador las cuenta y Bruno: «¡Qué bien jugáis juntos!».
  El juego no corrige: quien sabe si están bien es el mayor.
- **Escribir su nombre** ([ADR 0017](adr/0017-escribir-su-nombre.md)), desde el banderín del cuarto. Arriba cuelga su
  nombre en un banderín, y abajo, en una hoja, ella traza sus letras una tras otra, con los trazos del cole, mientras el
  narrador dice cada una («La ele»). La tilde y la virgulilla de la ñ son el último trazo de su letra. Cada letra que
  acaba vuela a su banderín y le da su color; con todas, su nombre sale entero en la hoja y Bruno dice «¡Ya sé escribir
  tu nombre!». Son las mayúsculas del abecedario y las vocales con tilde, hasta las diez letras del banderín.
- **El rincón de la calma**, el de su clase. Mientras mantiene el dedo, el globo se hincha y ella toma aire. Al soltar, el
  globo se deshincha despacio. Son tres respiraciones con Bruno.
- **La zona de mayores**, detrás del candado de la esquina, que se abre manteniendo el dedo 2,5 segundos. En ella se puede
  escribir su nombre (para el banderín), elegir su foto en la galería (para el marco del cuarto) o quitarla, elegir los minutos al día (5, 10, 15 o 20), dar «10 minutos más, solo hoy»,
  quitar la voz, los efectos o la música, poner la voz más despacio ([ADR 0042](adr/0042-bruno-mas-despacio.md)), guardar una copia de su partida para otro móvil y recuperarla
  ([ADR 0039](adr/0039-una-copia-de-su-partida.md)), ver su semana (los minutos de cada día, las islas terminadas y lo que
  más le ha costado, [ADR 0040](adr/0040-resumen-de-la-semana.md)), elegir qué islas y qué consonantes salen
  ([ADR 0041](adr/0041-elegir-que-islas-y-letras-salen.md)), cómo se ha sentido los últimos días, entre cuántas elige en cada juego y lo que ha
  practicado (galletas por vocal,
  letras trazadas, veces que ha escrito su nombre, fresas por número, globos,
  series, piezas de la casa, emociones, palabras dichas a Bruno, parejas que empiezan igual, islas y regalos) y volver a
  empezar, que devuelve a Bruno al huevo y conserva su nombre, su foto y los ajustes (el sonido, la voz despacio, su
  animal favorito y qué islas y letras salen). En
  Android y en el escritorio también hay «Salir del juego», que guarda la partida y lo cierra. En iPhone y iPad no lo
  hay: se sale con el gesto del sistema, porque Apple no quiere que una app se cierre sola. Es la única pantalla con
  texto.
- **Dos botones pequeños** en las esquinas de arriba. La casa vuelve un paso hacia el cuarto, desde el mapa, los juegos y
  la calma. El altavoz, en los juegos, repite lo que pide Bruno. Son pequeños a propósito, porque no son para jugar.

## Las reglas, y cómo las cumple la primera versión

Salen de la [investigación §6](investigacion/investigacion.md#6-reglas-que-salen-de-todo-esto).

| Regla | En la primera versión |
|---|---|
| Sin leer | Cada pantalla se explica con voz. Las letras y los números del juego son lo que se aprende, no instrucciones |
| Para dedos de 4 años | Solo tocar, arrastrar y mantener: sin doble toque, pellizcos ni prisas. En un móvil de 6,8″ (el emulador Pixel 10 Pro XL), el botón de jugar mide 2,4 cm, las galletas 1,8 cm y los colores 1,7 cm. Cada uno acepta el dedo hasta 0,3 cm más allá de su borde. Los botones de las esquinas miden 1 cm a propósito. En una tableta, todo es más grande |
| Equivocarse no castiga | Bruno nunca dice «¡mal!». Pone cara rara, devuelve la galleta y lo vuelve a pedir. No hay vidas, puntos ni tiempo. Si le cuesta, elige entre dos, sin que se le diga ([ADR 0020](adr/0020-se-adapta-a-lo-que-sabe.md)) |
| Unos 10 minutos con final | Límite por día, 10 minutos de serie. Bruno se duerme al terminar lo que ella esté haciendo. No hay rachas, cuentas atrás ni «¡no te vayas!» ([ADR 0006](adr/0006-diez-minutos-y-a-dormir.md)) |
| Para jugar acompañada | La misión para casa de cada noche, y «Habla con Bruno», que la primera vez necesita a un mayor para el permiso del micrófono |
| Lo del cole | Vocales en mayúscula, sus trazos en el orden del cole, escribir su nombre, la emoción del día como en la asamblea, y contar del 1 al 5 con los dedos y los puntos, en castellano de España |
| Hecho para ella | Su nombre en el banderín, que aprende a escribir, y su Bruno, con el color y la cabeza que ella elige. La voz no dice su nombre: son frases grabadas de antemano ([ADR 0004](adr/0004-voces-provisionales.md)) |
| Tranquilo | Colores suaves, sonidos cortos que solo suenan al tocar y animaciones lentas. En las islas, una música muy suave, distinta en cada una ([ADR 0038](adr/0038-musica-suave-en-las-islas.md)). La voz, los efectos y la música se pueden quitar |
| Premios con sentido | Los accesorios de Bruno se ganan terminando islas, y la pegatina de cada isla, que ella pega donde quiere en el cuarto, una por isla ([ADR 0024](adr/0024-pegatinas-para-el-cuarto.md)). No hay cofres sorpresa ni nada que comprar |
| Limpio | Sin anuncios, compras, cuentas ni internet. En Android no pide el permiso de internet. Todo lo guarda el dispositivo ([ADR 0005](adr/0005-un-regalo-sin-conexion.md)). El micrófono solo escucha mientras ella mantiene el botón, y su voz no se guarda ([ADR 0011](adr/0011-habla-con-bruno.md)) |

## Lo que trae y lo que falta

**Trae** lo que la propuesta pide a la primera versión: el nacimiento, Comeletras con las vocales, contar hasta 5, los
trazos y el mapa con dos islas. De las piezas comunes, el rincón de la calma, la misión para casa y la zona de mayores
(sin foto ni voces). Y de M3: la isla de los colores, con sus globos y sus series; la de las formas, con la casa de
Bruno; las letras M, P, L y S y los números hasta el 10 en las islas que ya había; y la isla de las emociones. Con
eso está todo lo que la propuesta ponía en el mapa. De M2, «Habla con Bruno». Y de lo que faltaba, la isla de las
palabras, con el sonido inicial por la sílaba. Y de las ideas, escribir su nombre, la pantalla de inicio, la emoción
del día, que los juegos se adapten a lo que sabe y las rimas con Bruno.

**Falta**, de la propuesta 3 que eligió Fangusk. El orden y el alcance de cada pieza los decide él.

- **Más contenido**: más consonantes, después de la T, la N y la D. El sonido inicial ya está, por la sílaba, en la isla
  de las palabras.

Una batería de ideas para después, sin decidir, está en [ideas](ideas.md).

Las **voces de la familia** no se harán: «para M2 no vamos a utilizar las voces de la familia, seguiremos con las voces actuales» (Fangusk, 2026-11-03).

## Pantalla, dibujo, voz y sonido

- **En horizontal**, en móvil y en tableta. Todo se dibuja en un escenario de 800 × 400 unidades que se escala para caber
  y cuyo fondo se alarga hasta los bordes de cualquier pantalla ([ADR 0003](adr/0003-escenario-horizontal-dibujado.md)).
- **Bruno y todo lo demás se dibujan con vectores**, sin imágenes: Bruno respira, parpadea, mira y cambia de cara (ocho
  bocas, cuatro ojos, tres posturas de brazos).
- **Las voces** son las de un narrador y la de Bruno, algo más aguda y más despacio, a unas 4 sílabas por segundo. Hay
  802 frases pregrabadas con la voz del sistema del Mac y **los efectos** (17) se sintetizan desde cero
  ([ADR 0004](adr/0004-voces-provisionales.md)). Las frases están en [`voces/guion.tsv`](../voces/guion.tsv).
- **La letra** de la zona de mayores, de los números, del banderín y del título es Andika, una tipografía pensada para quien aprende a
  leer (licencia OFL, en [`licencias/`](../licencias/Andika-OFL.txt)).
