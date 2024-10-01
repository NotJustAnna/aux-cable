package net.notjustanna.audio.discord

import net.dv8tion.jda.api.audio.AudioSendHandler
import net.dv8tion.jda.api.audio.AudioSendHandler.INPUT_FORMAT
import net.notjustanna.audio.native.mixer
import net.notjustanna.audio.native.targetDataLine
import net.notjustanna.audio.native.withFormat
import net.notjustanna.audio.system.AudioInput
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.Closeable
import java.lang.System.currentTimeMillis
import java.nio.ByteBuffer
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.DataLine
import javax.sound.sampled.TargetDataLine


class AuxSendHandler(
    val input: AudioInput,
    val target: TargetDataLine,
    val stream: AudioInputStream
) : AudioSendHandler, Closeable {
    val frames = Array(50) { ByteBuffer.allocate(FRAME_SIZE) }
    var readIndex = 0
    var availableIndex = 0
    val thread = Thread.ofPlatform().name("AuxSendHandler Thread").start {
        Thread.sleep(5000)
        try {
            while (true) {
                val buffer = frames[readIndex]
                buffer.clear()
                while (buffer.hasRemaining()) {
                    val read = stream.read(buffer.array(), buffer.position(), buffer.remaining())
                    if (read == -1) {
                        break
                    }
                    buffer.position(buffer.position() + read)
                }
                buffer.flip()
                readIndex = (readIndex + 1) % frames.size

                if (readIndex == availableIndex) {
                    log.warn("JDA is not keeping up with the audio stream.")
                    log.warn("Output will be truncated.")
                    var bytes = 0
                    val trash = ByteArray(1048)
                    var lastWarn = currentTimeMillis()
                    while (readIndex == availableIndex) {
                        val read = stream.read(trash)
                        if (read <= 0) {
                            Thread.sleep(10)
                        }
                        bytes += read

                        if (currentTimeMillis() - lastWarn > 1000) {
                            log.warn("Over a second has passed of truncated data.")
                            log.debug("Discarded $bytes bytes of audio data.")
                            lastWarn = currentTimeMillis()
                        }
                    }
                }
            }
        } catch (_: InterruptedException) {
            // Do nothing
        }

    }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(AuxSendHandler::class.java)

        /**
         * The frame size, in bytes, of [AudioSendHandler.INPUT_FORMAT].
         *
         * Calculated as follows:
         *    int frameSize = channels * (sampleRate * (20 / 1000f)) * (sampleSizeInBits / 8);
         */
        const val FRAME_SIZE = 3840

        fun open(input: AudioInput): AuxSendHandler? {
            log.debug("Trying to open AuxSendHandler for $input")
            var attempt = 1
            for (mixerInfo in input.mixers) {
                try {
                    val target = mixerInfo.targetDataLine(INPUT_FORMAT)
                    val stream = AudioInputStream(target).withFormat(INPUT_FORMAT)
                    target.open(INPUT_FORMAT, FRAME_SIZE * 4)
                    target.start()
                    log.info("AuxSendHandler for ${input.fullName} created.")
                    log.debug("Created at attempt $attempt")
                    log.debug("By mixerInfo.targetDataLine directly")
                    return AuxSendHandler(input, target, stream)
                } catch (e: Exception) {
                    log.debug("Attempt #$attempt to create AuxSendHandler failed.")
                    log.debug("Tried directly with mixerInfo.targetDataLine, got $e")
                    log.trace("Trace:", e)
                    attempt++
                }

                try {
                    val target = mixerInfo.targetDataLine(INPUT_FORMAT)
                    val stream = AudioInputStream(target).withFormat(INPUT_FORMAT)
                    target.open()
                    target.start()
                    log.info("AuxSendHandler for ${input.fullName} created.")
                    log.debug("Created at attempt $attempt")
                    log.debug("By mixerInfo.targetDataLine + AudioInputStream.withFormat")
                    return AuxSendHandler(input, target, stream)
                } catch (e: Exception) {
                    log.debug("Attempt #$attempt to create AuxSendHandler failed.")
                    log.debug("Tried mixerInfo.targetDataLine + AudioInputStream.withFormat, got $e")
                    log.trace("Trace:", e)
                    attempt++
                }

                try {
                    val mixer = mixerInfo.mixer
                    val target =
                        mixer.getLine(DataLine.Info(TargetDataLine::class.java, INPUT_FORMAT)) as TargetDataLine
                    val stream = AudioInputStream(target)
                    mixer.open()
                    target.open(INPUT_FORMAT, FRAME_SIZE)
                    target.start()
                    log.info("AuxSendHandler for ${input.fullName} created.")
                    log.debug("Created at attempt $attempt")
                    log.debug("By Mixer.getLine with custom DataLine.Info")
                    return AuxSendHandler(input, target, stream)
                } catch (e: Exception) {
                    log.debug("Attempt #$attempt to create AuxSendHandler failed.")
                    log.debug("Tried Mixer.getLine with custom DataLine.Info, got ${e.javaClass.simpleName}: ${e.localizedMessage}")
                    log.trace("Trace:", e)
                    attempt++
                }
            }

            log.error("Failed to create AuxSendHandler for $input")
            return null
        }
    }

    override fun canProvide(): Boolean {
        // add a buffer of 5 frames
        return (readIndex + 5) % frames.size != availableIndex
    }

    override fun provide20MsAudio(): ByteBuffer {
        val buffer = frames[availableIndex]
        availableIndex = (availableIndex + 1) % frames.size
        return buffer
    }

    override fun close() {
        thread.interrupt()
        stream.close()
        target.close()
    }
}