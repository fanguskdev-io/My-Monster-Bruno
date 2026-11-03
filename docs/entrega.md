# Entrega: el juego en su iPhone

Cómo se instala «Mi monstruo Bruno» en el iPhone 15 Pro en el que jugará tu sobrina sin publicarlo en la App Store, y
cómo se instala en un Android si algún día hiciera falta. Es el hito M4 ([proceso](proceso.md)): sale cuando está
instalado allí, con su icono, y probado allí. La vía la eliges tú. En cada una están separadas las cosas que haces tú,
que son las que piden tu Apple ID o tener el iPhone en la mano, de las que hace un agente desde este Mac.

## Antes de elegir

- **El iPhone tiene que tener iOS 18.5 o posterior** (Ajustes > General > Información > Versión de iOS). El iPhone 15 Pro
  lo admite: si tiene una versión anterior, se actualiza antes.
- **La partida se guarda en el iPhone** y no sale de él ([ADR 0005](adr/0005-un-regalo-sin-conexion.md)). Al instalar una
  versión nueva encima por la misma vía, Bruno sigue como estaba. Si se borra el juego, o si se pasa de una vía a otra,
  Bruno vuelve al huevo: cambia quién firma la app, e iOS obliga a borrarla antes de instalarla de nuevo. Por eso conviene
  elegir la vía antes de dárselo.
- **El proyecto ya está listo para firmar.** Xcode firma solo, y lo único que le falta es el Team ID de tu cuenta de
  Apple. No se sube a GitHub: va en `iosApp/Configuration/Local.xcconfig`, un fichero de este Mac que git ignora, con una
  sola línea, `TEAM_ID = ABCDE12345` (con el tuyo). Lo escribe el agente.
- **En el iPhone se llama «Bruno»**, es la versión 1.0 (1), tiene el mismo icono que en Android y va siempre en
  horizontal, girado a un lado o al otro.

| Vía | Cuánto cuesta | Cuánto dura instalada | Cuándo tiene que venir el iPhone al Mac |
|---|---|---|---|
| 1. Xcode con tu Apple ID | nada | 7 días: después no abre hasta que se reinstala | cada semana |
| 2. TestFlight | el Apple Developer Program, unos 99 € al año | 90 días cada versión: antes de que caduque, se sube otra | nunca: se instala con la app TestFlight |
| 3. Ad hoc | el Apple Developer Program, unos 99 € al año | un año | la primera vez, cada año y con cada versión nueva |
| Android: el APK | nada | siempre | no hace falta un Mac: se copia el fichero |

Lo que más pesa al elegir es dónde vive el iPhone. Si está en casa de tu sobrina, la vía gratuita obliga a traerlo cada
semana, y TestFlight es la que menos pide una vez montada: las versiones nuevas le llegan solas. Si lo tienes a mano, la
gratuita basta para empezar, sabiendo que pasar después a otra vía devuelve a Bruno al huevo.

## Vía 1 · Xcode con tu Apple ID, gratis

Lo que haces tú:

1. **Inicia sesión en Xcode con tu Apple ID**: Xcode > Ajustes > Cuentas, botón «+», Apple ID. Solo la primera vez.
2. **Elige tu equipo en el proyecto**: abre `iosApp/iosApp.xcodeproj`, selecciona el objetivo `iosApp`, pestaña
   «Signing & Capabilities», y en «Team» elige el tuyo, el que pone «(Personal Team)». Con eso el agente sabe tu Team ID:
   lo pasa a `Local.xcconfig` y deja el proyecto como estaba.
3. **Conecta el iPhone al Mac** con un cable USB-C, desbloquéalo y toca «Confiar» cuando pregunte si confía en este
   ordenador.
4. **Activa el Modo de desarrollador** en el iPhone: Ajustes > Privacidad y seguridad > Modo de desarrollador. Aparece
   después de conectarlo al Mac con Xcode abierto. Al activarlo, el iPhone se reinicia; al volver, confirma con
   «Activar» y el código.
5. **Pide al agente que lo instale.** La primera vez, el iPhone no lo abre hasta que confías en tu propia cuenta:
   Ajustes > General > VPN y gestión de dispositivos, tu Apple ID, «Confiar».
6. **Cada 7 días deja de abrirse.** Vuelve a conectarlo y pide que se reinstale. No lo borres: así Bruno sigue como estaba.

Lo que hace el agente:

- Pasa el Team ID a `iosApp/Configuration/Local.xcconfig` y deja `project.pbxproj` como estaba, sin el equipo.
- Busca el iPhone de ella con `xcrun devicectl list devices`, por su nombre o su UDID, aunque haya otro conectado, y
  compila la versión de lanzamiento, la firma y la instala:

  ```bash
  xcodebuild -project iosApp/iosApp.xcodeproj -scheme iosApp -configuration Release -destination 'platform=iOS,id=<UDID>' -derivedDataPath build/xcode-iphone -allowProvisioningUpdates -allowProvisioningDeviceRegistration build
  ```

  ```bash
  xcrun devicectl device install app --device <UDID> build/xcode-iphone/Build/Products/Release-iphoneos/MyMonsterBruno.app
  ```

- Lo prueba allí contigo (más abajo, «Después de instalarlo»), y cada semana lo reinstala cuando conectas el iPhone.

Con una cuenta gratuita caben como mucho tres apps tuyas a la vez en el mismo iPhone.

## Vía 2 · TestFlight

Lo que haces tú:

1. **Date de alta en el Apple Developer Program** como particular, con tu Apple ID: en
   [developer.apple.com/programs/enroll](https://developer.apple.com/programs/enroll/) o con la app Apple Developer. Cuesta
   99 USD al año (unos 99 € en España), pide la verificación en dos pasos y Apple puede tardar uno o dos días en
   aprobarlo.
2. **Inicia sesión en Xcode y elige tu equipo**, como en los pasos 1 y 2 de la vía 1, pero el del programa, no el
   «(Personal Team)».
3. **Crea la app en App Store Connect** ([appstoreconnect.apple.com](https://appstoreconnect.apple.com)), Apps, «+»,
   «Nueva app», cuando el agente te diga que ya ha firmado la primera versión. Antes, el identificador no sale en la
   lista.
   - Plataforma: iOS.
   - Nombre: uno que no tenga ninguna otra app de la App Store. «Bruno» a secas estará cogido; vale «Mi monstruo Bruno».
     En el iPhone seguirá poniendo «Bruno».
   - Idioma principal: español.
   - Identificador del paquete: `com.fanguskdev.mymonsterbruno.MyMonsterBruno`.
   - SKU: el que quieras, por ejemplo `mymonsterbruno`.
4. **Invita al iPhone de tu sobrina** cuando el agente suba la versión. En TestFlight, crea un grupo de probadores
   externos, rellena lo que Apple pide para revisarla (una descripción corta y un correo de contacto) y añade el Apple ID
   que use ese iPhone, o activa un enlace público y ábrelo desde él. Apple revisa la primera que va a ese grupo antes de
   que llegue al iPhone; suele tardar alrededor de un día.
5. **En el iPhone**, instala TestFlight desde la App Store, acepta la invitación e instala Bruno. Cada versión dura 90
   días. TestFlight avisa antes de que caduque, y el agente sube otra que se instala encima.

Lo que hace el agente:

- Pasa el Team ID del programa a `Local.xcconfig`, como en la vía 1.
- Añade `ITSAppUsesNonExemptEncryption` con `NO` a `iosApp/iosApp/Info.plist`: el juego no cifra nada, ni siquiera se
  conecta a internet. Así App Store Connect no pregunta por la exportación en cada versión.
- En cada subida, sube `CURRENT_PROJECT_VERSION` en `iosApp/Configuration/Config.xcconfig`, porque App Store Connect no
  acepta dos versiones con el mismo número. Después archiva la versión de lanzamiento y la sube con tu sesión de Xcode,
  sin guardar contraseñas ni claves en el repositorio:

  ```bash
  xcodebuild -project iosApp/iosApp.xcodeproj -scheme iosApp -configuration Release -destination 'generic/platform=iOS' -archivePath build/MyMonsterBruno.xcarchive -allowProvisioningUpdates archive
  ```

  ```bash
  xcodebuild -exportArchive -archivePath build/MyMonsterBruno.xcarchive -exportOptionsPlist build/subir.plist -exportPath build/testflight -allowProvisioningUpdates
  ```

  `build/subir.plist` lleva `method` = `app-store-connect` y `destination` = `upload`.
- Antes de los 90 días, sube la siguiente.

## Vía 3 · Ad hoc

Lo que haces tú:

1. **Date de alta en el Apple Developer Program, inicia sesión en Xcode y elige tu equipo**, como en los pasos 1 y 2 de
   la vía 2.
2. **Conecta el iPhone al Mac** y toca «Confiar», como en el paso 3 de la vía 1. Aquí no hace falta el Modo de
   desarrollador.
3. **Vuelve a conectarlo** cada año, cuando caduca, y con cada versión nueva.

Lo que hace el agente:

- Pasa el Team ID a `Local.xcconfig` y registra el iPhone en tu cuenta del programa por su UDID, que lee de
  `xcrun devicectl list devices`. Caben 100 aparatos de cada tipo al año.
- Archiva la versión de lanzamiento como en la vía 2. La exporta con `method` = `release-testing` (la distribución ad
  hoc) en vez de subirla, y la instala desde el Mac con `xcrun devicectl device install app --device <UDID> <la .ipa>`,
  o con Apple Configurator.

Con el programa, la vía 1 también dura un año en vez de 7 días: es casi lo mismo que la ad hoc, pero instalando desde
Xcode.

## Android · el APK

Por si juega en un móvil o una tableta Android.

Lo que haces tú:

1. **Permite instalar el juego**: en el Android, Ajustes > Aplicaciones > Acceso especial > Instalar aplicaciones
   desconocidas, y da permiso a la app con la que vayas a abrir el fichero (Archivos, Drive o el navegador). El nombre
   exacto del menú cambia de una marca a otra.
2. **Pásale el APK** que te dé el agente (por cable, por Drive o por correo), ábrelo en el Android y toca «Instalar».

Lo que hace el agente:

- Lo compila optimizado con R8 y firmado con la clave de depuración de este Mac. La puerta ya lo hace en cada cambio:

  ```bash
  ./gradlew :androidApp:assembleRelease
  ```

  El fichero sale en `androidApp/build/outputs/apk/release/`. Si el Android está conectado por USB con la depuración
  activada, puede instalarlo directamente con `adb -s <su número de serie> install -r <el APK>`, nunca con `adb` sin `-s`.
- Para instalar encima una versión nueva sin borrar la partida, hay que firmarla con la misma clave, la de este Mac
  (`~/.android/debug.keystore`). Si un día se compila en otro ordenador, se copia esa clave antes.

## Después de instalarlo

Es la última parte de M4, y se prueba allí, con el sonido puesto:

- El icono de Bruno está en la pantalla de inicio y el juego abre.
- Se juega girado a un lado y al otro: la casa, el altavoz y el candado se ven enteros, y la Dynamic Island no tapa nada
  que haga falta (en el simulador, así es: [pruebas](pruebas.md)).
- Se oyen la voz y los efectos por el altavoz.
- La primera vez que se entra en «Habla con Bruno» (el micrófono rosa del cuarto), el iPhone pide permiso para usar el
  micrófono: lo da un mayor. Después, Bruno repite con la voz de ella la palabra que dice, por el altavoz
  ([ADR 0011](adr/0011-habla-con-bruno.md)).
- Al cerrarlo y volver a abrirlo, Bruno sigue como estaba, y la zona de mayores se abre manteniendo el candado.
