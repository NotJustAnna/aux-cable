package net.notjustanna.audio.discord

import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.FlowableEmitter
import net.dv8tion.jda.api.audio.AudioSendHandler
import net.notjustanna.audio.system.AudioInput
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.Closeable
import java.nio.ByteBuffer
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.TargetDataLine

class NewAuxSendHandler(
    val input: AudioInput,
    val target: TargetDataLine,
    val stream: AudioInputStream
) : AudioSendHandler, Closeable {
    private val frames = Array(128) { ByteBuffer.allocate(FRAME_SIZE) }

    override fun canProvide(): Boolean {
        TODO("Not yet implemented")
    }

    override fun provide20MsAudio(): ByteBuffer? {
        TODO("Not yet implemented")
    }

    override fun close() {
        stream.close()
        target.close()
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
    }
}