@file:Suppress("NOTHING_TO_INLINE")

package net.notjustanna.audio.native

import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioSystem.getMixer
import javax.sound.sampled.AudioSystem.getTargetDataLine
import javax.sound.sampled.Mixer
import javax.sound.sampled.TargetDataLine

typealias MixerInfo = Mixer.Info

/**
 * Returns the mixer associated with this info.
 *
 * This is a convenience method that calls [AudioSystem.getMixer].
 */
inline val MixerInfo.mixer: Mixer
    get() = getMixer(this)

/**
 * Returns a target data line associated with this info and format.
 *
 * This is a convenience method that calls [AudioSystem.getTargetDataLine].
 */
inline fun MixerInfo.targetDataLine(format: AudioFormat): TargetDataLine = getTargetDataLine(format, this)
