#!/usr/bin/env python3
"""tools/sonidos.py · los efectos de sonido del juego, sintetizados desde cero (ADR 0004).

    python3 tools/sonidos.py              los genera en shared/src/commonMain/composeResources/files/sonidos/
    python3 tools/sonidos.py --comprobar  solo comprueba que los ficheros son los que sale de aquí (lo usa la puerta)

Son cortos y suaves, para no sobreestimular (docs/diseno.md): toques, el huevo que se rompe, el ñam de Bruno, campanitas
cuando algo sale bien y un «boing» simpático cuando no. Cada uno es una receta de senos, ruido y envolventes; el ruido
sale de una semilla fija, así que el resultado es siempre el mismo byte a byte. Solo usa la biblioteca estándar.
"""

import argparse
import io
import math
import random
import sys
import wave
from pathlib import Path

RAIZ = Path(__file__).resolve().parent.parent
SALIDA = RAIZ / "shared" / "src" / "commonMain" / "composeResources" / "files" / "sonidos"
FRECUENCIA = 22050


def silencio(segundos):
    return [0.0] * int(segundos * FRECUENCIA)


def mezclar(base, otro, desde=0.0, volumen=1.0):
    inicio = int(desde * FRECUENCIA)
    if len(base) < inicio + len(otro):
        base = base + [0.0] * (inicio + len(otro) - len(base))
    for i, m in enumerate(otro):
        base[inicio + i] += m * volumen
    return base


def tono(frecuencia, segundos, ataque=0.005, caida=8.0, final=None, armonicos=(1.0,)):
    """Un seno (con armónicos) que sube rápido y se apaga en exponencial; [final] lo desliza hasta otra frecuencia."""
    n = int(segundos * FRECUENCIA)
    salida = []
    fase = 0.0
    for i in range(n):
        t = i / FRECUENCIA
        f = frecuencia if final is None else frecuencia + (final - frecuencia) * (i / n)
        fase += 2 * math.pi * f / FRECUENCIA
        envolvente = min(1.0, t / ataque) * math.exp(-caida * t)
        valor = sum(a * math.sin(fase * (k + 1)) for k, a in enumerate(armonicos))
        salida.append(valor * envolvente)
    return salida


def campana(frecuencia, segundos, caida=6.0):
    return tono(frecuencia, segundos, ataque=0.002, caida=caida, armonicos=(1.0, 0.0, 0.25, 0.0, 0.08))


def ruido(segundos, semilla, caida=30.0, suavizado=0.0):
    """Ruido blanco con su envolvente; [suavizado] entre 0 y 1 le quita agudos (un paso bajo de un polo)."""
    azar = random.Random(semilla)
    n = int(segundos * FRECUENCIA)
    salida = []
    previo = 0.0
    for i in range(n):
        t = i / FRECUENCIA
        valor = azar.uniform(-1.0, 1.0)
        previo = previo * suavizado + valor * (1 - suavizado)
        salida.append(previo * min(1.0, t / 0.002) * math.exp(-caida * t))
    return salida


def toque():
    return tono(880, 0.09, caida=40, final=640)


def crac():
    s = mezclar(ruido(0.05, 1, caida=70, suavizado=0.2), ruido(0.04, 2, caida=80, suavizado=0.3), 0.035, 0.7)
    return mezclar(s, tono(180, 0.06, caida=50), 0.0, 0.4)


def nacer():
    s = []
    for i, f in enumerate((523.25, 659.25, 783.99, 1046.5)):
        s = mezclar(s, campana(f, 0.5), 0.09 * i, 0.5)
    return mezclar(s, chispas(), 0.3, 0.4)


def nam():
    s = []
    for i in range(3):
        mordisco = mezclar(ruido(0.07, 10 + i, caida=45, suavizado=0.85), tono(150, 0.07, caida=40), 0.0, 0.8)
        s = mezclar(s, mordisco, 0.13 * i, 0.9)
    return s


def acierto():
    return mezclar(campana(1318.5, 0.6), campana(1975.5, 0.5), 0.11, 0.7)


def boing():
    n = int(0.42 * FRECUENCIA)
    salida = []
    fase = 0.0
    for i in range(n):
        t = i / FRECUENCIA
        f = 260 + 260 * math.sin(math.pi * min(1.0, t / 0.42)) + 18 * math.sin(2 * math.pi * 14 * t)
        fase += 2 * math.pi * f / FRECUENCIA
        salida.append(math.sin(fase) * min(1.0, t / 0.01) * math.exp(-4 * t))
    return salida


def vuela():
    base = ruido(0.32, 20, caida=0.0, suavizado=0.6)
    n = len(base)
    return [m * math.sin(math.pi * i / n) ** 2 for i, m in enumerate(base)]


def chispas():
    azar = random.Random(30)
    s = []
    for i in range(6):
        s = mezclar(s, campana(azar.uniform(2200, 3600), 0.25, caida=14), 0.09 * i, 0.35)
    return s


def trazo():
    return campana(1567.98, 0.35, caida=9)


def letra():
    s = []
    for i, f in enumerate((1046.5, 1318.5, 1567.98, 2093.0)):
        s = mezclar(s, campana(f, 0.5, caida=7), 0.08 * i, 0.45)
    return s


def regalo():
    s = []
    for i, f in enumerate((523.25, 659.25, 783.99, 1046.5, 1318.5)):
        s = mezclar(s, campana(f, 0.7, caida=4), 0.12 * i, 0.4)
    return mezclar(s, chispas(), 0.55, 0.5)


def fresa():
    return tono(540, 0.11, caida=30, final=300)


def pop():
    return mezclar(tono(1000, 0.06, caida=60, final=700), ruido(0.03, 40, caida=120, suavizado=0.3), 0.0, 0.4)


def inicio():
    """La pantalla de inicio: un arpegio alegre que sube, sol, do, mi, sol y do, y unas chispas al final."""
    s = []
    for i, f in enumerate((392.0, 523.25, 659.25, 783.99, 1046.5)):
        s = mezclar(s, campana(f, 0.6, caida=5), 0.1 * i, 0.45)
    return mezclar(s, chispas(), 0.45, 0.4)


def palmada():
    """Una palmada de Bruno en «Habla con Bruno»: un golpe de ruido con cuerpo y otro más flojo detrás, como dos manos."""
    s = mezclar(ruido(0.09, 50, caida=45, suavizado=0.35), ruido(0.07, 51, caida=55, suavizado=0.45), 0.012, 0.6)
    return mezclar(s, tono(1100, 0.05, caida=60), 0.0, 0.15)


def cohete():
    """El despegue del cohete de Bruno: un rugido suave y grave que crece y se va, con un tono que sube debajo."""
    base = ruido(1.4, 31, caida=0.0, suavizado=0.92)
    n = len(base)
    rugido = [m * math.sin(math.pi * i / n) ** 1.5 for i, m in enumerate(base)]
    return mezclar(rugido, tono(110, 1.4, ataque=0.3, caida=1.2, final=420), 0.0, 0.3)


RECETAS = {
    "toque": toque,
    "crac": crac,
    "nacer": nacer,
    "nam": nam,
    "acierto": acierto,
    "boing": boing,
    "vuela": vuela,
    "chispas": chispas,
    "trazo": trazo,
    "letra": letra,
    "regalo": regalo,
    "fresa": fresa,
    "pop": pop,
    "palmada": palmada,
    "inicio": inicio,
    "cohete": cohete,
}


def wav(muestras, pico=0.7):
    alto = max((abs(m) for m in muestras), default=0.0) or 1.0
    datos = bytearray()
    for m in muestras:
        valor = int(round(m / alto * pico * 32767))
        datos += max(-32768, min(32767, valor)).to_bytes(2, "little", signed=True)
    salida = io.BytesIO()
    with wave.open(salida, "wb") as w:
        w.setnchannels(1)
        w.setsampwidth(2)
        w.setframerate(FRECUENCIA)
        w.writeframes(bytes(datos))
    return salida.getvalue()


def main():
    parser = argparse.ArgumentParser(description=__doc__.splitlines()[0])
    parser.add_argument("--comprobar", action="store_true", help="solo comprueba que están al día")
    args = parser.parse_args()
    SALIDA.mkdir(parents=True, exist_ok=True)
    errores = []
    for nombre, receta in RECETAS.items():
        datos = wav(receta())
        fichero = SALIDA / f"{nombre}.wav"
        if args.comprobar:
            if not fichero.exists() or fichero.read_bytes() != datos:
                errores.append(f"{nombre}.wav no está al día")
        else:
            fichero.write_bytes(datos)
    for sobrante in SALIDA.glob("*.wav"):
        if sobrante.stem not in RECETAS:
            if args.comprobar:
                errores.append(f"sobra {sobrante.name}")
            else:
                sobrante.unlink()
    if errores:
        print("Los sonidos no están al día (python3 tools/sonidos.py los genera):")
        for error in errores:
            print(f"  - {error}")
        sys.exit(1)
    print(f"Los {len(RECETAS)} sonidos están al día." if args.comprobar else f"{len(RECETAS)} sonidos generados.")


if __name__ == "__main__":
    main()
