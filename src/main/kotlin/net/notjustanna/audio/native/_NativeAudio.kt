@file:Suppress("NOTHING_TO_INLINE")

package net.notjustanna.audio.native

import javax.sound.sampled.*
import javax.sound.sampled.AudioSystem.*

/**
 * Returns a set of all target encodings associated with this format.
 *
 * This is a convenience method that calls [AudioSystem.getTargetEncodings].
 */
inline val AudioFormat.targetEncodings: Set<AudioFormat.Encoding>
    get() = getTargetEncodings(this).toSet()

/**
 * Returns `true` if conversion is supported from this format to the given format.
 *
 * This is a convenience method that calls [AudioSystem.isConversionSupported].
 */
inline fun AudioFormat.isConversionSupported(to: AudioFormat): Boolean = isConversionSupported(this, to)

/**
 * Returns an audio input stream with the given encoding.
 *
 * This is a convenience method that calls [AudioSystem.getAudioInputStream].
 */
inline fun AudioInputStream.withEncoding(encoding: AudioFormat.Encoding): AudioInputStream =
    getAudioInputStream(encoding, this)

/**
 * Returns an audio input stream with the given format.
 *
 * This is a convenience method that calls [AudioSystem.getAudioInputStream].
 */
inline fun AudioInputStream.withFormat(format: AudioFormat): AudioInputStream =
    getAudioInputStream(format, this)

