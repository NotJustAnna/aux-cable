@file:Suppress("NOTHING_TO_INLINE")

package net.notjustanna.audio.native

import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.AudioSystem.getMixerInfo as nativeMixerInfo
import javax.sound.sampled.AudioSystem.isConversionSupported as nativeIsConversionSupported

/**
 * Convenience object that makes working with the native audio system more object-oriented.
 */
object NativeAudio {
    /**
     * Returns a set of all available mixers.
     *
     * This is a convenience method that calls [AudioSystem.getMixerInfo1].
     */
    inline val mixerInfo: Set<MixerInfo>
        get() = nativeMixerInfo().toSet()

    /**
     * Returns `true` if conversion is supported from the given format to the given format.
     *
     * This is a convenience method that calls [AudioSystem.isConversionSupported1].
     */
    inline fun isConversionSupported(from: AudioFormat, to: AudioFormat): Boolean =
        nativeIsConversionSupported(from, to)
}
