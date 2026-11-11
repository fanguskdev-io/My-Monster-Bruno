#!/usr/bin/env bash
# tools/entrega-iphone.sh · la entrega ad hoc de «Mi amigo Bruno» en el iPhone de tu sobrina (docs/entrega.md, vía 3).
#
#   bash tools/entrega-iphone.sh comprobar          sin cuenta de Apple: compila la versión de lanzamiento para un
#                                                   iPhone, sin firmar, y dice cómo es: nombre, versión, iOS mínimo,
#                                                   permisos y lo que ocupa
#   bash tools/entrega-iphone.sh archivar           con tu Team ID en iosApp/Configuration/Local.xcconfig: la archiva,
#                                                   firmada, en build/entrega/
#   bash tools/entrega-iphone.sh instalar [UDID]    el día de la entrega, con su iPhone conectado y el Modo de
#                                                   desarrollador activado: lo registra en tu cuenta, exporta la versión
#                                                   ad hoc con su iPhone dentro y la instala
#
# Solo cuenta los iPhone de verdad conectados a este Mac, nunca los simuladores; si hay más de uno, pide el UDID del de
# ella. No guarda contraseñas ni claves: firma con la sesión de Xcode de este Mac.
set -euo pipefail

cd "$(dirname "${BASH_SOURCE[0]}")/.."

PROYECTO=iosApp/iosApp.xcodeproj
ESQUEMA=iosApp
CARPETA=build/entrega
ARCHIVO=$CARPETA/MyMonsterBruno.xcarchive
LOCAL=iosApp/Configuration/Local.xcconfig
IOS_MINIMO=18.5

if [[ -z "${JAVA_HOME:-}" && -d "/Applications/Android Studio.app/Contents/jbr/Contents/Home" ]]; then
    export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"
fi

# El Team ID de Local.xcconfig, o nada si no está.
equipo() {
    [[ -f $LOCAL ]] || return 0
    sed -n 's/^[[:space:]]*TEAM_ID[[:space:]]*=[[:space:]]*\([A-Z0-9]\{10\}\)[[:space:]]*$/\1/p' "$LOCAL" | head -1
}

con_equipo() {
    if [[ -z "$(equipo)" ]]; then
        echo "Falta tu Team ID: date de alta en el Apple Developer Program, inicia sesión en Xcode con esa cuenta y elige"
        echo "tu equipo en el proyecto; el agente lo pasa a $LOCAL (docs/entrega.md, vía 3)."
        exit 1
    fi
}

# Cómo es la app de [1]: lo que se ve en el iPhone, lo que pide y lo que ocupa.
describir() {
    local app=$1
    local plist="$app/Info.plist"
    leer() { /usr/libexec/PlistBuddy -c "Print :$1" "$plist" 2>/dev/null || echo "(falta)"; }
    echo "La app: $app"
    echo "  En el iPhone se llama: $(leer CFBundleDisplayName)"
    echo "  Identificador: $(leer CFBundleIdentifier)"
    echo "  Versión: $(leer CFBundleShortVersionString) ($(leer CFBundleVersion))"
    echo "  iOS mínimo: $(leer MinimumOSVersion)"
    echo "  Procesador: $(lipo -archs "$app/$(leer CFBundleExecutable)")"
    echo "  Ocupa: $(du -sh "$app" | cut -f1)"
    echo "  Micrófono: $(leer NSMicrophoneUsageDescription)"
    echo "  Reconocimiento de voz: $(leer NSSpeechRecognitionUsageDescription)"
}

comprobar() {
    xcodebuild -quiet -project "$PROYECTO" -scheme "$ESQUEMA" -configuration Release -destination 'generic/platform=iOS' \
        -derivedDataPath "$CARPETA/comprobar" CODE_SIGNING_ALLOWED=NO CODE_SIGNING_REQUIRED=NO build
    describir "$CARPETA/comprobar/Build/Products/Release-iphoneos/MyMonsterBruno.app"
    echo "Compila para un iPhone de verdad. Sin firmar no se puede instalar: falta tu cuenta (docs/entrega.md)."
}

archivar() {
    con_equipo
    mkdir -p "$CARPETA"
    rm -rf "$ARCHIVO"
    xcodebuild -quiet -project "$PROYECTO" -scheme "$ESQUEMA" -configuration Release -destination 'generic/platform=iOS' \
        -archivePath "$ARCHIVO" -allowProvisioningUpdates archive
    git rev-parse HEAD > "$CARPETA/commit.txt"
    if [[ -n "$(git status --porcelain)" ]]; then
        echo "Ojo: hay cambios sin commit, y van en esta versión."
    fi
    describir "$ARCHIVO/Products/Applications/MyMonsterBruno.app"
    echo "Archivada en $ARCHIVO, del commit $(cut -c1-7 "$CARPETA/commit.txt")."
}

# Los iPhone de verdad conectados a este Mac: UDID, nombre, versión de iOS y Modo de desarrollador, separados por
# tabuladores, uno por línea.
iphones() {
    local json
    json=$(mktemp)
    xcrun devicectl list devices --json-output "$json" >/dev/null 2>&1 || true
    python3 - "$json" <<'PY'
import json, sys
try:
    devices = json.load(open(sys.argv[1]))["result"]["devices"]
except Exception:
    devices = []
for d in devices:
    hw, dp, cp = d.get("hardwareProperties", {}), d.get("deviceProperties", {}), d.get("connectionProperties", {})
    if hw.get("reality") != "physical" or hw.get("deviceType") != "iPhone":
        continue
    if cp.get("transportType") not in ("wired", "localNetwork"):
        continue
    print("\t".join([hw.get("udid", ""), dp.get("name", ""), dp.get("osVersionNumber", ""), str(dp.get("developerModeStatus", ""))]))
PY
    rm -f "$json"
}

# Si la versión de iOS [1] llega a la mínima.
basta_ios() {
    python3 -c "import sys; v = tuple(int(x) for x in sys.argv[1].split('.') if x.isdigit()); sys.exit(0 if v >= tuple(int(x) for x in sys.argv[2].split('.')) else 1)" "$1" "$IOS_MINIMO"
}

instalar() {
    con_equipo
    local udid=${1:-} lista nombre version modo
    lista=$(iphones)
    if [[ -z $udid ]]; then
        if [[ -z $lista ]]; then
            echo "No hay ningún iPhone conectado: conéctalo con un cable, desbloquéalo y toca «Confiar»."
            exit 1
        fi
        if (($(printf '%s\n' "$lista" | wc -l) > 1)); then
            echo "Hay más de un iPhone conectado. Di cuál es el de ella con su UDID:"
            printf '%s\n' "$lista" | awk -F'\t' '{print "  " $1 "  " $2 " (iOS " $3 ")"}'
            echo "  bash tools/entrega-iphone.sh instalar <UDID>"
            exit 1
        fi
        udid=$(printf '%s\n' "$lista" | cut -f1)
    fi
    nombre=$(printf '%s\n' "$lista" | awk -F'\t' -v u="$udid" '$1 == u {print $2}')
    version=$(printf '%s\n' "$lista" | awk -F'\t' -v u="$udid" '$1 == u {print $3}')
    modo=$(printf '%s\n' "$lista" | awk -F'\t' -v u="$udid" '$1 == u {print $4}')
    if [[ -z $nombre ]]; then
        echo "El iPhone $udid no está conectado."
        exit 1
    fi
    echo "Su iPhone: $nombre, iOS $version ($udid)."
    if ! basta_ios "$version"; then
        echo "Necesita iOS $IOS_MINIMO o posterior: actualízalo en Ajustes > General > Actualización de software."
        exit 1
    fi
    if [[ $modo == "disabled" ]]; then
        echo "Activa el Modo de desarrollador: Ajustes > Privacidad y seguridad > Modo de desarrollador (docs/entrega.md)."
        exit 1
    fi

    # 1. Lo registra en tu cuenta compilando para él: el perfil ad hoc solo vale para los aparatos registrados.
    xcodebuild -quiet -project "$PROYECTO" -scheme "$ESQUEMA" -configuration Release -destination "platform=iOS,id=$udid" \
        -derivedDataPath "$CARPETA/registro" -allowProvisioningUpdates -allowProvisioningDeviceRegistration build

    # 2. La versión archivada, si es la de ahora; si no, la archiva otra vez.
    if [[ ! -d $ARCHIVO || "$(cat "$CARPETA/commit.txt" 2>/dev/null)" != "$(git rev-parse HEAD)" ]]; then
        archivar
    fi

    # 3. La exporta ad hoc, con su iPhone dentro del perfil.
    cat > "$CARPETA/adhoc.plist" <<PLIST
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
<plist version="1.0">
<dict>
    <key>method</key>
    <string>release-testing</string>
    <key>teamID</key>
    <string>$(equipo)</string>
    <key>signingStyle</key>
    <string>automatic</string>
    <key>thinning</key>
    <string>&lt;none&gt;</string>
</dict>
</plist>
PLIST
    rm -rf "$CARPETA/adhoc" "$CARPETA/ipa"
    xcodebuild -exportArchive -archivePath "$ARCHIVO" -exportOptionsPlist "$CARPETA/adhoc.plist" \
        -exportPath "$CARPETA/adhoc" -allowProvisioningUpdates
    local ipa
    ipa=$(find "$CARPETA/adhoc" -maxdepth 1 -name '*.ipa' | head -1)

    # 4. La instala: devicectl instala la .app de dentro de la .ipa. Encima de la que hubiera, Bruno sigue como estaba.
    mkdir -p "$CARPETA/ipa"
    unzip -q "$ipa" -d "$CARPETA/ipa"
    xcrun devicectl device install app --device "$udid" "$CARPETA/ipa/Payload/MyMonsterBruno.app"

    # 5. Una copia con la fecha, para dentro de un año.
    cp "$ipa" "$CARPETA/MiAmigoBruno-$(date +%Y-%m-%d).ipa"
    echo "Instalada en $nombre. Dura un año; la copia está en $CARPETA/MiAmigoBruno-$(date +%Y-%m-%d).ipa."
    echo "Ahora, a probarla allí con sonido (docs/entrega.md, «Después de instalarlo»)."
}

case "${1:-}" in
    comprobar) comprobar ;;
    archivar) archivar ;;
    instalar) instalar "${2:-}" ;;
    *)
        awk 'NR > 1 && /^#/ { sub(/^# ?/, ""); print; next } NR > 1 { exit }' "${BASH_SOURCE[0]}"
        exit 1
        ;;
esac
