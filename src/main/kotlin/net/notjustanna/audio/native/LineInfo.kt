@file:Suppress("NOTHING_TO_INLINE")

package net.notjustanna.audio.native

import javax.sound.sampled.AudioSystem.*
import javax.sound.sampled.Line

typealias LineInfo = Line.Info

/**
 * Returns a set of all target lines associated with this info.
 *
 * This is a convenience method that calls [AudioSystem.getTargetLineInfo].
 */
inline val LineInfo.targetLineInfo: Set<LineInfo>
    get() = getTargetLineInfo(this).toSet()

/**
 * Returns `true` if this line is supported.
 *
 * This is a convenience method that calls [AudioSystem.isLineSupported].
 */
inline val LineInfo.isSupported: Boolean
    get() = isLineSupported(this)

/**
 * Returns the line associated with this info.
 *
 * This is a convenience method that calls [AudioSystem.getLine].
 */
inline val LineInfo.line: Line
    get() = getLine(this)
