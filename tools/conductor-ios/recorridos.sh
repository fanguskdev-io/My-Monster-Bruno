#!/bin/bash
# Los recorridos del iPhone, de un tirón (docs/pruebas.md, «Recorridos en el iPhone»): crea un iPhone 15 Pro simulado,
# le pone dos fotos en la galería e instala el juego que deja la puerta; para cada recorrido, lo reinstala limpio, le
# pone su partida y lo pasa mientras el vigilante hace las capturas; al acabar, borra el simulador, también si algo falla.
# Siempre en silencio (los recorridos abren el juego con -silencio) y en un simulador propio, nunca en uno de otra sesión.
#
#   bash tools/conductor-ios/recorridos.sh                           todos
#   bash tools/conductor-ios/recorridos.sh testRhymes testCountdown   solo esos
#
# Las capturas quedan en build/recorridos-ios/, con la isla y las esquinas en negro.
set -uo pipefail
cd "$(dirname "$0")/../.." || exit 1

APP=build/xcode/Build/Products/Debug-iphonesimulator/MyMonsterBruno.app
BUNDLE=com.fanguskdev.mymonsterbruno.MyMonsterBruno
OUT=build/recorridos-ios
DD=build/conductor-ios
HOY=$(date +%Y%m%d)
TODOS=(testWalkThroughHeldBothWays testFinalVersionBothWays testTalkAndPhoto testWordsAndLetters testStartAndName
    testFeelingOfTheDay testRhymes testCountdown testPhotoAndRestart testStickers testStory testNumberLine
    testPlaces testSums)
if (($# > 0)); then RECORRIDOS=("$@"); else RECORRIDOS=("${TODOS[@]}"); fi

# La partida de la que parte cada recorrido: los de la entrega, en el huevo; los demás, con Bruno nacido, su nombre
# (Lucía, de ejemplo) y la emoción de hoy ya preguntada, salvo los que prueban justo eso; el de la recta, con un día ya
# ido a la isla de los números, para que hoy le toque la recta y no la cuenta atrás, y el de las sumas, con dos.
partida() {
    local sabe='{"a":1,"e":1,"i":1,"o":1,"u":1,"m":1,"p":1,"l":1,"s":1}'
    case "$1" in
        testWalkThroughHeldBothWays | testFinalVersionBothWays) echo "" ;;
        testStartAndName) echo "{\"hatched\":true,\"childName\":\"Lucía\",\"moodDay\":$HOY}" ;;
        testFeelingOfTheDay) echo '{"hatched":true,"childName":"Lucía","namesWritten":1}' ;;
        testStickers) echo "{\"hatched\":true,\"childName\":\"Lucía\",\"namesWritten\":1,\"moodDay\":$HOY,\"islands\":{\"VOWELS\":1,\"NUMBERS\":1,\"COLORS\":1,\"SHAPES\":1,\"FEELINGS\":1,\"WORDS\":1}}" ;;
        testSums) echo "{\"hatched\":true,\"childName\":\"Lucía\",\"namesWritten\":1,\"moodDay\":$HOY,\"numbersDay\":1,\"numbersDays\":2}" ;;
        testNumberLine) echo "{\"hatched\":true,\"childName\":\"Lucía\",\"namesWritten\":1,\"moodDay\":$HOY,\"numbersDay\":1,\"numbersDays\":1}" ;;
        testWordsAndLetters) echo "{\"hatched\":true,\"childName\":\"Lucía\",\"namesWritten\":1,\"moodDay\":$HOY,\"fed\":$sabe,\"traced\":$sabe}" ;;
        *) echo "{\"hatched\":true,\"childName\":\"Lucía\",\"namesWritten\":1,\"moodDay\":$HOY}" ;;
    esac
}

if [[ ! -d $APP ]]; then
    echo "Falta la app de iOS: pasa antes la puerta (bash scripts/verificar.sh), que la compila."
    exit 1
fi

RUNTIME=$(xcrun simctl list runtimes available | grep -oE 'com\.apple\.CoreSimulator\.SimRuntime\.iOS-26-[0-9]+' | tail -1)
[[ -n $RUNTIME ]] || RUNTIME=$(xcrun simctl list runtimes available | grep -oE 'com\.apple\.CoreSimulator\.SimRuntime\.iOS-[0-9-]+' | tail -1)
U=$(xcrun simctl create "Bruno iPhone 15 Pro, recorridos" com.apple.CoreSimulator.SimDeviceType.iPhone-15-Pro "$RUNTIME") || exit 1
SYNC=$(mktemp -d /tmp/bruno-recorridos.XXXXXX)
VIGILANTE=""
acabar() {
    touch "$SYNC/stop"
    [[ -n $VIGILANTE ]] && wait "$VIGILANTE" 2>/dev/null
    xcrun simctl shutdown "$U" >/dev/null 2>&1
    xcrun simctl delete "$U" >/dev/null 2>&1 && echo "Simulador borrado."
    rm -rf "$SYNC"
}
trap acabar EXIT

# Una copia de la app, para que una compilación que se haga a la vez no la cambie a mitad de los recorridos.
cp -R "$APP" "$SYNC/MyMonsterBruno.app"
APP=$SYNC/MyMonsterBruno.app

echo "Simulador $U ($RUNTIME)"
xcrun simctl boot "$U" && xcrun simctl bootstatus "$U" -b >/dev/null || exit 1

# Dos fotos en la galería, para elegir su foto y cambiarla: el icono del juego y la pantalla de inicio del simulador.
mkdir -p "$SYNC/fotos"
cp iosApp/iosApp/Assets.xcassets/AppIcon.appiconset/app-icon-1024.png "$SYNC/fotos/bruno.png"
xcrun simctl io "$U" screenshot "$SYNC/fotos/pantalla.png" >/dev/null 2>&1
xcrun simctl addmedia "$U" "$SYNC"/fotos/*.png

echo "Compilando el conductor..."
xcodebuild build-for-testing -quiet -project tools/conductor-ios/Driver.xcodeproj -scheme Driver \
    -destination "platform=iOS Simulator,id=$U" -derivedDataPath "$DD" >"$SYNC/compilar.log" 2>&1 || {
    tail -20 "$SYNC/compilar.log"
    echo "No se pudo compilar el conductor."
    exit 1
}

rm -rf "$OUT"
mkdir -p "$OUT"
bash tools/conductor-ios/vigilante.sh "$U" "$SYNC" "$OUT" ip15 >/dev/null 2>&1 &
VIGILANTE=$!

fallidos=()
for t in "${RECORRIDOS[@]}"; do
    printf '\n▶ %s\n' "$t"
    xcrun simctl terminate "$U" "$BUNDLE" >/dev/null 2>&1
    xcrun simctl uninstall "$U" "$BUNDLE" >/dev/null 2>&1
    xcrun simctl install "$U" "$APP" || exit 1
    p=$(partida "$t")
    if [[ -n $p ]]; then
        C=$(xcrun simctl get_app_container "$U" "$BUNDLE" data)
        xcrun simctl spawn "$U" defaults write "$C/Library/Preferences/$BUNDLE.plist" partida -string "$p"
    fi
    rm -f "$SYNC/log.txt"
    if TEST_RUNNER_CONDUCTOR_DIR="$SYNC" xcodebuild test-without-building -project tools/conductor-ios/Driver.xcodeproj \
        -scheme Driver -destination "platform=iOS Simulator,id=$U" -derivedDataPath "$DD" \
        -only-testing:"DriverUITests/BrunoDriverTests/$t" >"$SYNC/$t.log" 2>&1; then
        echo "✓ $t"
    else
        cp "$SYNC/$t.log" "$OUT/$t.log"
        echo "✗ $t (su salida, en $OUT/$t.log)"
        fallidos+=("$t")
    fi
    [[ -f $SYNC/log.txt ]] && grep -E "no encuentro|no llego" "$SYNC/log.txt" | sed 's/^/  /'
done

printf '\nLas capturas, en %s\n' "$OUT"
if ((${#fallidos[@]} > 0)); then
    echo "Han fallado: ${fallidos[*]}"
    exit 1
fi
echo "Los recorridos pasan."
