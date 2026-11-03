#!/usr/bin/env python3
"""tools/voces.py · las voces del juego: del guion (voces/guion.tsv) a los WAV que carga el juego (ADR 0004).

    python3 tools/voces.py               genera las que faltan o han cambiado en el guion
    python3 tools/voces.py --todas       las genera todas otra vez
    python3 tools/voces.py --comprobar   solo comprueba que están al día (lo usa la puerta, scripts/verificar.sh)

Cada línea se dice con la voz «Mónica» del Mac (español de España) con `say`. Las de Bruno, además, van más agudas y un
poco más deprisa: se remuestrean ×1,2, como un monstruo pequeño. Salen en WAV de 16 bits, mono, a 22 050 Hz, sin el
silencio de los bordes y a la misma altura de volumen. Son provisionales: las grabaciones de la familia las sustituirán
con el mismo nombre de fichero.

Solo usa la biblioteca estándar de Python; generar necesita macOS (`say`), comprobar no.
"""

import argparse
import array
import csv
import hashlib
import json
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
    "bruno": {"voz": "Mónica", "ritmo": 150, "tono": 1.2},
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
        if voz not in VOCES:
            sys.exit(f"voces/guion.tsv: «{ident}» tiene la voz «{voz}»; las que hay son {', '.join(VOCES)}")
        if ident in vistos:
            sys.exit(f"voces/guion.tsv: «{ident}» está dos veces")
        vistos.add(ident)
    return filas


def huella(fila):
    ajustes = VOCES[fila["voz"]]
    clave = f"{VERSION}|{ajustes['voz']}|{ajustes['ritmo']}|{ajustes['tono']}|{fila['texto']}"
    return hashlib.sha256(clave.encode("utf-8")).hexdigest()[:16]


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
            escribir(recortar_y_nivelar(remuestrear(muestras, ajustes["tono"])), destino)
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
