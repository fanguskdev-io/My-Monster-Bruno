#!/usr/bin/env python3
"""tools/musica.py · la música suave de cada isla, sintetizada desde cero (ADR 0038).

    python3 tools/musica.py              la genera en shared/src/commonMain/composeResources/files/musica/
    python3 tools/musica.py --comprobar  solo comprueba que los ficheros son los que sale de aquí (lo usa la puerta)

Una pieza por isla, tranquila y distinta: ocho compases en una escala pentatónica, para que nada suene mal, con una
melodía, un bajo y, en algunas, un colchón de fondo. Se repite sin costura: lo que suena pasado el final vuelve al
principio. La melodía sale de una semilla fija, así que el resultado es siempre el mismo byte a byte. Va a 16 000 Hz
y en mono, porque es de fondo y así pesa poco. Solo usa la biblioteca estándar.
"""

import argparse
import io
import math
import random
import sys
import wave
from pathlib import Path

RAIZ = Path(__file__).resolve().parent.parent
SALIDA = RAIZ / "shared" / "src" / "commonMain" / "composeResources" / "files" / "musica"
FRECUENCIA = 16000
COMPASES = 8
TIEMPOS = 4


def nota(semitonos_desde_la):
    """La frecuencia de la nota a tantos semitonos del la de 440 Hz."""
    return 440.0 * 2 ** (semitonos_desde_la / 12)


def pulsada(f, segundos, caida=3.2, brillo=0.25):
    """Una nota de marimba: un seno con algo de su octava, que suena enseguida y se apaga despacio."""
    n = int(segundos * FRECUENCIA)
    salida = []
    for i in range(n):
        t = i / FRECUENCIA
        ataque = min(1.0, t / 0.006)
        valor = math.sin(2 * math.pi * f * t) + brillo * math.sin(4 * math.pi * f * t) * math.exp(-6 * t)
        salida.append(valor * ataque * math.exp(-caida * t))
    return salida


def cajita(f, segundos):
    """Una nota de caja de música: aguda, con sus parciales de campana, que se apaga rápido."""
    n = int(segundos * FRECUENCIA)
    salida = []
    for i in range(n):
        t = i / FRECUENCIA
        ataque = min(1.0, t / 0.003)
        valor = math.sin(2 * math.pi * f * t) + 0.3 * math.sin(2 * math.pi * f * 3.01 * t) * math.exp(-8 * t)
        salida.append(valor * ataque * math.exp(-4.5 * t))
    return salida


def flauta(f, segundos):
    """Una nota de flauta: entra suave, se mantiene con un vibrato pequeño y se va suave."""
    n = int(segundos * FRECUENCIA)
    salida = []
    fase = 0.0
    for i in range(n):
        t = i / FRECUENCIA
        vibrato = 1 + 0.004 * math.sin(2 * math.pi * 5 * t) * min(1.0, t / 0.3)
        fase += 2 * math.pi * f * vibrato / FRECUENCIA
        entrada = min(1.0, t / 0.07)
        salida_suave = min(1.0, (segundos - t) / 0.15)
        valor = math.sin(fase) + 0.12 * math.sin(2 * fase)
        salida.append(valor * entrada * max(0.0, salida_suave))
    return salida


def colchon(fs, segundos):
    """Un acorde de fondo, [fs], que entra y se va muy despacio, con dos senos un poco desafinados por nota."""
    n = int(segundos * FRECUENCIA)
    salida = [0.0] * n
    for f in fs:
        for desafino in (0.998, 1.002):
            paso = 2 * math.pi * f * desafino / FRECUENCIA
            for i in range(n):
                salida[i] += math.sin(paso * i)
    for i in range(n):
        t = i / FRECUENCIA
        envolvente = min(1.0, t / 0.6) * min(1.0, (segundos - t) / 0.6)
        salida[i] *= max(0.0, envolvente) / (2 * len(fs))
    return salida


def poner(pieza, sonido, desde, volumen):
    """Suma [sonido] a [pieza] desde [desde] segundos; lo que pasa del final vuelve al principio, para el bucle."""
    inicio = int(desde * FRECUENCIA)
    largo = len(pieza)
    for i, m in enumerate(sonido):
        pieza[(inicio + i) % largo] += m * volumen


def melodia(azar, grados, compases):
    """
    Una melodía de [compases] compases en la escala [grados]: pasos cortos, con alguna nota larga, que acaba en la tónica.
    Cada nota es (tiempo de inicio, duración en tiempos, grado).
    """
    ritmos = [(1, 1, 1, 1), (2, 1, 1), (1, 1, 2), (2, 2), (1, 1, 1, 1), (3, 1)]
    notas = []
    grado = 0
    for compas in range(compases):
        ritmo = (4,) if compas == compases - 1 else azar.choice(ritmos)
        tiempo = compas * TIEMPOS
        for duracion in ritmo:
            if compas == compases - 1:
                grado = 0
            else:
                grado = max(-2, min(len(grados) + 1, grado + azar.choice((-2, -1, -1, 0, 1, 1, 2))))
            notas.append((tiempo, duracion, grado))
            tiempo += duracion
    return notas


def frecuencia_de(escala, grado, octava):
    """La frecuencia del [grado] de la [escala] (semitonos sobre su tónica), subiendo octavas si pasa de ella."""
    vueltas, resto = divmod(grado, len(escala))
    return nota(octava + escala[resto] + 12 * vueltas)


# Las escalas pentatónicas, en semitonos sobre la tónica.
MAYOR = (0, 2, 4, 7, 9)
MENOR = (0, 3, 5, 7, 10)

# Cada isla: su velocidad, su escala y tónica (semitonos desde el la 440), los grados del bajo de cada compás, sus
# instrumentos y su semilla.
ISLAS = {
    "letras": dict(tempo=84, escala=MAYOR, tonica=-9, bajo=(0, 5, 3, 4, 0, 5, 3, 4), voz=pulsada, colchon=True, semilla=11),
    "numeros": dict(tempo=96, escala=MAYOR, tonica=-2, bajo=(0, 3, 4, 0, 0, 3, 4, 0), voz=cajita, colchon=False, semilla=23),
    "colores": dict(tempo=80, escala=MAYOR, tonica=-4, bajo=(0, 4, 5, 3, 0, 4, 3, 0), voz=pulsada, colchon=True, semilla=37),
    "formas": dict(tempo=92, escala=MAYOR, tonica=-7, bajo=(0, 3, 0, 4, 0, 3, 4, 0), voz=pulsada, colchon=False, semilla=41),
    "emociones": dict(tempo=66, escala=MENOR, tonica=0, bajo=(0, 3, 2, 4, 0, 3, 4, 0), voz=flauta, colchon=True, semilla=53),
    "palabras": dict(tempo=76, escala=MAYOR, tonica=-7, bajo=(0, 5, 3, 4, 0, 5, 4, 0), voz=flauta, colchon=True, semilla=67),
    "casa": dict(tempo=88, escala=MAYOR, tonica=-4, bajo=(0, 3, 4, 0, 5, 3, 4, 0), voz=cajita, colchon=True, semilla=79),
}

# Los grados del bajo, en la escala mayor de siete notas, para que el acorde de cada compás sea el suyo.
DIATONICA = (0, 2, 4, 5, 7, 9, 11)


def pieza(isla):
    receta = ISLAS[isla]
    tiempo = 60.0 / receta["tempo"]
    largo = COMPASES * TIEMPOS * tiempo
    salida = [0.0] * int(round(largo * FRECUENCIA))
    azar = random.Random(receta["semilla"])
    escala = receta["escala"]
    tonica = receta["tonica"]
    voz = receta["voz"]

    for inicio, duracion, grado in melodia(azar, escala, COMPASES):
        f = frecuencia_de(escala, grado, tonica + 12)
        if voz is cajita:
            f *= 2
        segundos = duracion * tiempo * (1.0 if voz is flauta else 1.6)
        poner(salida, voz(f, segundos), inicio * tiempo, 0.5)

    for compas, grado in enumerate(receta["bajo"]):
        raiz = tonica - 12 + DIATONICA[grado]
        for golpe in (0, 2):
            poner(salida, pulsada(nota(raiz), 2 * tiempo, caida=2.2, brillo=0.1), (compas * TIEMPOS + golpe) * tiempo, 0.32)
        if receta["colchon"]:
            acorde = [nota(raiz + 12), nota(raiz + 12 + (3 if grado in (1, 2, 5) else 4)), nota(raiz + 19)]
            poner(salida, colchon(acorde, TIEMPOS * tiempo), compas * TIEMPOS * tiempo, 0.22)
    return salida


def wav(muestras, fuerza=0.11, pico=0.6):
    """En 16 bits, todas igual de fuertes (la misma energía, [fuerza]), sin pasar nunca de [pico]."""
    alto = max((abs(m) for m in muestras), default=0.0) or 1.0
    energia = math.sqrt(sum(m * m for m in muestras) / max(1, len(muestras))) or 1.0
    escala = min(fuerza / energia, pico / alto)
    datos = bytearray()
    for m in muestras:
        valor = int(round(m * escala * 32767))
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
    parser.add_argument("--comprobar", action="store_true", help="solo comprueba que está al día")
    args = parser.parse_args()
    SALIDA.mkdir(parents=True, exist_ok=True)
    errores = []
    for isla in ISLAS:
        datos = wav(pieza(isla))
        fichero = SALIDA / f"{isla}.wav"
        if args.comprobar:
            if not fichero.exists() or fichero.read_bytes() != datos:
                errores.append(f"{isla}.wav no está al día")
        else:
            fichero.write_bytes(datos)
    for sobrante in SALIDA.glob("*.wav"):
        if sobrante.stem not in ISLAS:
            if args.comprobar:
                errores.append(f"sobra {sobrante.name}")
            else:
                sobrante.unlink()
    if errores:
        print("La música no está al día (python3 tools/musica.py la genera):")
        for error in errores:
            print(f"  - {error}")
        sys.exit(1)
    print(f"Las {len(ISLAS)} piezas están al día." if args.comprobar else f"{len(ISLAS)} piezas generadas.")


if __name__ == "__main__":
    main()
