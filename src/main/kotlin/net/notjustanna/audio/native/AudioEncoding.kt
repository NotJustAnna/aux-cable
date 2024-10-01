@file:Suppress("NOTHING_TO_INLINE")

package net.notjustanna.audio.native

import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.AudioSystem.getTargetEncodings
import javax.sound.sampled.AudioSystem.getTargetFormats
import javax.sound.sampled.AudioSystem.isConversionSupported as nativeIsConversionSupported

typealias AudioEncoding = AudioFormat.Encoding

/**
 * Returns a set of all target encodings associated with this encoding.
 *
 * This is a convenience method that calls [AudioSystem.getTargetEncodings].
 */
inline val AudioEncoding.targetEncodings: Set<AudioEncoding>
    get() = getTargetEncodings(this).toSet()

/**
 * Returns `true` if conversion is supported from this encoding to the given format.
 *
 * This is a convenience method that calls [AudioSystem.isConversionSupported].
 */
inline fun AudioEncoding.isConversionSupported(format: AudioFormat): Boolean =
    nativeIsConversionSupported(this, format)

/**
 * Returns a set of all target formats associated with this encoding.
 *
 * This is a convenience method that calls [AudioSystem.getTargetFormats].
 */
inline fun AudioEncoding.targetFormats(format: AudioFormat): Set<AudioFormat> = getTargetFormats(this, format).toSet()
