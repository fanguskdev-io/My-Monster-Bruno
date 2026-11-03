#!/usr/bin/env python3
"""tools/voces.py · las voces del juego: del guion (voces/guion.tsv) a los WAV que carga el juego (ADR 0004).

    python3 tools/voces.py               genera las que faltan o han cambiado en el guion
    python3 tools/voces.py --todas       las genera todas otra vez
    python3 tools/voces.py --comprobar   solo comprueba que están al día (lo usa la puerta, scripts/verificar.sh)

Cada línea se dice con la voz «Mónica» del Mac (español de España) con `say`. Las de Bruno, además, van más agudas,
como un monstruo pequeño: se remuestrean ×1,2, lo que también las acelera. Para que no hable «demasiado rápido para un
niño de 4 años» (Fangusk), después se alargan sin cambiarles el tono con tools/despacio.swift, que usa el
AVAudioUnitTimePitch de Apple, y quedan en unas 4 sílabas por segundo. La velocidad de `say` no sirve para eso: Mónica no
baja de la que tiene a 150 palabras por minuto. Salen en WAV de 16 bits, mono, a 22 050 Hz, sin el silencio de los bordes
y a la misma altura de volumen. Si algún día se cambia una voz, basta con sustituir su fichero, con el mismo nombre.

Solo usa la biblioteca estándar de Python; generar necesita macOS (`say`) y, para las de Bruno, Xcode (`swiftc`);
comprobar no necesita ninguno.
"""

import argparse
import array
import csv
import hashlib
import json
import re
import subprocess
import sys
import tempfile
import wave
from pathlib import Path

RAIZ = Path(__file__).resolve().parent.parent
GUION = RAIZ / "voces" / "guion.tsv"
INDICE = RAIZ / "voces" / "indice.json"
SALIDA = RAIZ / "shared" / "src" / "commonMain" / "composeResources" / "files" / "voces"

FRECUENCIA = 22050
VOCES = {
    "narrador": {"voz": "Mónica", "ritmo": 165, "tono": 1.0},
    "bruno": {"voz": "Mónica", "ritmo": 150, "tono": 1.2, "tempo": 0.75},
}
# Sube si cambia cómo se procesa una voz: obliga a generarlas todas otra vez.
VERSION = 1
UMBRAL_SILENCIO = 300
MARGEN_MS = 70
PICO = 0.89


def leer_guion():
    with GUION.open(encoding="utf-8", newline="") as f:
        filas = list(csv.DictReader(f, delimiter="\t"))
    vistos = set()
    for fila in filas:
        ident, voz, texto = fila["id"], fila["voz"], fila["texto"]
        if not ident or not texto:
            sys.exit(f"voces/guion.tsv: una fila sin id o sin texto: {fila}")
        # Una palabra sin vocales la voz la deletrea: «Mmm» sonaba «eme eme eme» (Fangusk, 2026-11-03). «Huum», no.
        for palabra in re.findall(r"[^\W\d_]+", texto):
            if len(palabra) > 1 and not re.search(r"[aeiouáéíóúü]", palabra, re.IGNORECASE):
                sys.exit(f"voces/guion.tsv: «{ident}» dice «{palabra}», sin vocales: la voz lo deletrea")
        if voz not in VOCES:
            sys.exit(f"voces/guion.tsv: «{ident}» tiene la voz «{voz}»; las que hay son {', '.join(VOCES)}")
        if ident in vistos:
            sys.exit(f"voces/guion.tsv: «{ident}» está dos veces")
        vistos.add(ident)
    return filas


def huella(fila):
    ajustes = VOCES[fila["voz"]]
    clave = f"{VERSION}|{ajustes['voz']}|{ajustes['ritmo']}|{ajustes['tono']}|{fila['texto']}"
    if tempo(ajustes) != 1.0:
        clave += f"|tempo {tempo(ajustes)}"
    return hashlib.sha256(clave.encode("utf-8")).hexdigest()[:16]


def tempo(ajustes):
    """La velocidad final de una voz: 1 es la que sale de `say` (y del remuestreo); 0,75, un tercio más larga."""
    return ajustes.get("tempo", 1.0)


def decir(texto, ajustes, destino):
    subprocess.run(
        ["say", "-v", ajustes["voz"], "-r", str(ajustes["ritmo"]), "-o", str(destino),
         f"--data-format=LEI16@{FRECUENCIA}", texto],
        check=True,
    )
    with wave.open(str(destino), "rb") as w:
        if w.getnchannels() != 1 or w.getsampwidth() != 2:
            sys.exit(f"say devolvió un formato inesperado para «{texto}»")
        muestras = array.array("h", w.readframes(w.getnframes()))
    return muestras


def remuestrear(muestras, factor):
    """Más agudo y más deprisa a la vez: lee las muestras [factor] veces más rápido, interpolando entre vecinas."""
    if factor == 1.0:
        return muestras
    largo = int(len(muestras) / factor)
    salida = array.array("h", bytes(2 * largo))
    for i in range(largo):
        x = i * factor
        a = int(x)
        b = min(a + 1, len(muestras) - 1)
        t = x - a
        salida[i] = int(muestras[a] * (1 - t) + muestras[b] * t)
    return salida


def alargar(muestras, velocidad, carpeta):
    """Más despacio sin cambiar el tono: tools/despacio.swift, compilado una vez por ejecución en [carpeta]."""
    if velocidad == 1.0:
        return muestras
    programa = carpeta / "despacio"
    if not programa.exists():
        subprocess.run(["swiftc", "-O", str(RAIZ / "tools" / "despacio.swift"), "-o", str(programa)], check=True)
    entrada, salida = carpeta / "rapida.wav", carpeta / "despacio.wav"
    escribir(muestras, entrada)
    subprocess.run([str(programa), str(velocidad), str(entrada), str(salida)], check=True)
    with wave.open(str(salida), "rb") as w:
        if w.getnchannels() != 1 or w.getsampwidth() != 2 or w.getframerate() != FRECUENCIA:
            sys.exit("tools/despacio.swift devolvió un formato inesperado")
        return array.array("h", w.readframes(w.getnframes()))


def recortar_y_nivelar(muestras):
    inicio = next((i for i, m in enumerate(muestras) if abs(m) > UMBRAL_SILENCIO), 0)
    fin = next((i for i in range(len(muestras) - 1, -1, -1) if abs(muestras[i]) > UMBRAL_SILENCIO), len(muestras) - 1)
    margen = FRECUENCIA * MARGEN_MS // 1000
    util = muestras[max(0, inicio - margen): min(len(muestras), fin + margen)]
    pico = max((abs(m) for m in util), default=0)
    if pico == 0:
        return util
    ganancia = PICO * 32767 / pico
    return array.array("h", (max(-32768, min(32767, int(m * ganancia))) for m in util))


def escribir(muestras, destino):
    with wave.open(str(destino), "wb") as w:
        w.setnchannels(1)
        w.setsampwidth(2)
        w.setframerate(FRECUENCIA)
        w.writeframes(muestras.tobytes())


def generar(filas, todas):
    SALIDA.mkdir(parents=True, exist_ok=True)
    indice = json.loads(INDICE.read_text(encoding="utf-8")) if INDICE.exists() else {}
    hechas = 0
    with tempfile.TemporaryDirectory() as tmp:
        for fila in filas:
            ident = fila["id"]
            destino = SALIDA / f"{ident}.wav"
            if not todas and destino.exists() and indice.get(ident) == huella(fila):
                continue
            ajustes = VOCES[fila["voz"]]
            muestras = decir(fila["texto"], ajustes, Path(tmp) / "linea.wav")
            muestras = alargar(remuestrear(muestras, ajustes["tono"]), tempo(ajustes), Path(tmp))
            escribir(recortar_y_nivelar(muestras), destino)
            indice[ident] = huella(fila)
            hechas += 1
    ids = {fila["id"] for fila in filas}
    for sobrante in SALIDA.glob("*.wav"):
        if sobrante.stem not in ids:
            sobrante.unlink()
    indice = {ident: indice[ident] for ident in sorted(ids)}
    INDICE.write_text(json.dumps(indice, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
    print(f"{hechas} voces generadas; {len(ids)} en el guion.")


def comprobar(filas):
    indice = json.loads(INDICE.read_text(encoding="utf-8")) if INDICE.exists() else {}
    errores = []
    ids = set()
    for fila in filas:
        ident = fila["id"]
        ids.add(ident)
        if not (SALIDA / f"{ident}.wav").exists():
            errores.append(f"falta {ident}.wav")
        elif indice.get(ident) != huella(fila):
            errores.append(f"{ident}.wav no corresponde a su línea del guion")
    for sobrante in SALIDA.glob("*.wav"):
        if sobrante.stem not in ids:
            errores.append(f"sobra {sobrante.name}: no está en el guion")
    if errores:
        print("Las voces no están al día (python3 tools/voces.py las genera):")
        for error in errores:
            print(f"  - {error}")
        sys.exit(1)
    print(f"Las {len(ids)} voces están al día con el guion.")


def main():
    parser = argparse.ArgumentParser(description=__doc__.splitlines()[0])
    parser.add_argument("--todas", action="store_true", help="genera todas las voces otra vez")
    parser.add_argument("--comprobar", action="store_true", help="solo comprueba que están al día")
    args = parser.parse_args()
    filas = leer_guion()
    if args.comprobar:
        comprobar(filas)
    else:
        generar(filas, args.todas)


if __name__ == "__main__":
    main()
