package com.fanguskdev.mymonsterbruno.audio

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sqrt

/**
 * [pcm] [factor] times longer, at the same pitch (ADR 0042): WSOLA. The voice is cut into overlapping pieces, each
 * taken from a little later than where the slower time would put it, and laid one after the other half a piece apart;
 * each piece is taken where it fits best with the one before, so the voice never sounds broken or doubled. One channel;
 * anything else, or a [factor] of 1, comes back as it is.
 */
internal fun stretch(pcm: Pcm, factor: Float): Pcm {
    val input = pcm.samples
    if (factor == 1f || pcm.channels != 1 || pcm.rate <= 0 || input.size < MIN_SAMPLES) return pcm
    val frame = (pcm.rate * FRAME_SECONDS).roundToInt() / 2 * 2
    val hop = frame / 2
    val tolerance = frame / 4
    val window = FloatArray(frame) { (0.5 - 0.5 * cos(2 * PI * it / frame)).toFloat() }
    val length = (input.size * factor).roundToInt()
    val out = FloatArray(length + frame)
    var previous = 0
    var k = 0
    while (k * hop < length) {
        val ideal = (k * hop / factor).roundToInt()
        if (ideal >= input.size) break
        val position = if (k == 0) 0 else fit(input, previous + hop, ideal, tolerance, hop)
        val at = k * hop
        for (i in 0 until frame) {
            val x = position + i
            if (x >= input.size) break
            out[at + i] += input[x] * window[i]
        }
        previous = position
        k++
    }
    val samples = ShortArray(length) { out[it].roundToInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort() }
    return Pcm(samples, 1, pcm.rate)
}

/**
 * Where, within [tolerance] of [ideal], a piece of [input] fits best after the one before: the most alike, over [length]
 * samples, to [natural], what came right after that one. Alike as their normalised correlation, every few samples.
 */
private fun fit(
    input: ShortArray,
    natural: Int,
    ideal: Int,
    tolerance: Int,
    length: Int,
): Int {
    val last = input.size - length - 1
    if (natural > last || ideal > last) return ideal.coerceIn(0, maxOf(0, input.size - 1))
    var best = ideal
    var bestScore = Double.NEGATIVE_INFINITY
    var candidate = maxOf(0, ideal - tolerance)
    val end = minOf(last, ideal + tolerance)
    while (candidate <= end) {
        var together = 0.0
        var energy = 1.0
        var i = 0
        while (i < length) {
            val b = input[candidate + i].toDouble()
            together += input[natural + i] * b
            energy += b * b
            i += STEP
        }
        val score = together / sqrt(energy)
        if (score > bestScore) {
            bestScore = score
            best = candidate
        }
        candidate += 2
    }
    return best
}

/** Each piece, about 46 ms: long enough to hold a vibration of the voice, short enough not to blur the syllables. */
private const val FRAME_SECONDS = 0.046f

/** How often within a piece the likeness is looked at: one sample in four is enough, and four times faster. */
private const val STEP = 4

/** Shorter than this, it is a click, not a voice. */
private const val MIN_SAMPLES = 256
