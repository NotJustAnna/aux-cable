package net.notjustanna.audio;

import io.micronaut.core.annotation.Nullable;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.sampled.*;
import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.MessageFormat;
import java.util.concurrent.Semaphore;

public class AudioSystemSendHandler implements AudioSendHandler, Closeable, Runnable {
    private static final Logger logger = LoggerFactory.getLogger(AudioSystemSendHandler.class);

    /**
     * The frame size, in bytes, of [AudioSendHandler.INPUT_FORMAT].
     * <p>
     * Calculated as follows:
     *    int frameSize = channels * (sampleRate * (20 / 1000f)) * (sampleSizeInBits / 8);
     */
    private static final int FRAME_SIZE = 3840;

    private final AudioInput input;
    private final TargetDataLine target;
    private final AudioInputStream stream;
    private final ByteBuffer[] frames;
    private final Semaphore sync;
    private final Thread thread;
    private long readIndex = 0;
    private long writeIndex = 0;

    public AudioSystemSendHandler(AudioInput input, TargetDataLine target, AudioInputStream stream) {
        this.input = input;
        this.target = target;
        this.stream = stream;
        this.frames = new ByteBuffer[50];
        this.sync = new Semaphore(1);
        this.sync.acquireUninterruptibly();
        for (int i = 0; i < frames.length; i++) {
            frames[i] = ByteBuffer.allocate(FRAME_SIZE);
        }

        this.thread = Thread.ofPlatform()
            .name("AudioSystemSendHandler Thread - " + input.name() + "(" + input.device() + ")")
            .start(this);
    }

    public AudioInput getInput() {
        return input;
    }

    @Override
    public boolean canProvide() {
        sync.release();
        return readIndex < writeIndex;
    }

    @Override
    public ByteBuffer provide20MsAudio() {
        return frames[Math.toIntExact((readIndex++ % frames.length))];
    }

    @Override
    public void close() throws IOException {
        if (thread.isAlive()) {
            thread.interrupt();
        }
        try {
            stream.close();
        } catch (IOException e) {
            logger.error("Error while closing audio stream", e);
        }
        target.close();
    }

    @Override
    public void run() {
        try {
            sync.acquire();
            //noinspection InfiniteLoopStatement
            while (true) {
                ByteBuffer buffer = frames[Math.toIntExact((writeIndex++ % frames.length))];
                buffer.clear();
                while (buffer.hasRemaining()) {
                    int read = stream.read(buffer.array(), buffer.position(), buffer.remaining());
                    if (read == -1) {
                        break;
                    }
                    buffer.position(buffer.position() + read);
                }
                buffer.flip();

                if (readIndex < writeIndex - frames.length) {
                    logger.warn("JDA is not keeping up with the audio stream.");
                    logger.warn("Output will be truncated.");
                    long bytes = 0;
                    byte[] trash = new byte[1920];
                    // 1920 bytes = 10ms of the target audio format
                    // so we're discarding audio 10ms at a time.
                    long lastWarn = System.currentTimeMillis();
                    while (readIndex == writeIndex) {
                        int read = stream.read(trash);
                        if (read <= 0) {
                            //noinspection BusyWait
                            Thread.sleep(10);
                        }
                        bytes += read;

                        if (System.currentTimeMillis() - lastWarn > 1000) {
                            logger.warn("Over a second has passed of truncated data.");
                            logger.debug("Discarded {} bytes of audio data.", bytes);
                            lastWarn = System.currentTimeMillis();
                        }
                    }
                }
            }
        } catch (InterruptedException ignored) {
        } catch (IOException e) {
            logger.error("Error while reading audio stream", e);
            try {
                this.close();
            } catch (IOException ignored) {
            }
        }
    }

    @Override
    public String toString() {
        return AudioSystemSendHandler.toStringFormat.format(new Object[]{input, sync, thread, readIndex, writeIndex});
    }

    @Nullable
    public static AudioSystemSendHandler create(AudioInput input) {
        int attempt = 1;
        for (Mixer.Info mixerInfo : input.mixers()) {
            try {
                return createDirect(input, mixerInfo);
            } catch (Exception e) {
                logger.debug("Attempt #{} to create AudioSystemSendHandler failed.", attempt);
                logger.debug("Tried mixerInfo.targetDataLine directly, got {}: {}", e.getClass().getSimpleName(), e.getLocalizedMessage());
                logger.trace("Trace:", e);
                attempt++;
            }

            try {
                return createWithFormat(input, mixerInfo);
            } catch (Exception e) {
                logger.debug("Attempt #{} to create AudioSystemSendHandler failed.", attempt);
                logger.debug("Tried mixerInfo.targetDataLine + AudioInputStream.withFormat, got {}: {}", e.getClass().getSimpleName(), e.getLocalizedMessage());
                logger.trace("Trace:", e);
                attempt++;
            }

            try {
                return createWithCustomInfo(input, mixerInfo);
            } catch (Exception e) {
                logger.debug("Attempt #{} to create AudioSystemSendHandler failed.", attempt);
                logger.debug("Tried Mixer.getLine with custom DataLine.Info, got {}: {}", e.getClass().getSimpleName(), e.getLocalizedMessage());
                logger.trace("Trace:", e);
                attempt++;
            }
        }

        logger.error("Failed to create AudioSystemSendHandler for {}", input);
        return null;
    }

    private static AudioSystemSendHandler createDirect(AudioInput input, Mixer.Info mixerInfo) throws Exception {
        TargetDataLine target = AudioSystem.getTargetDataLine(AudioSendHandler.INPUT_FORMAT, mixerInfo);
        AudioInputStream stream = AudioSystem.getAudioInputStream(AudioSendHandler.INPUT_FORMAT, new AudioInputStream(target));
        target.open(AudioSendHandler.INPUT_FORMAT, FRAME_SIZE * 4);
        target.start();
        logger.info("AudioSystemSendHandler for {} created.", input.fullName());
        logger.debug("Created by mixerInfo.targetDataLine directly");
        return new AudioSystemSendHandler(input, target, stream);
    }

    private static AudioSystemSendHandler createWithFormat(AudioInput input, Mixer.Info mixerInfo) throws Exception {
        TargetDataLine target = AudioSystem.getTargetDataLine(AudioSendHandler.INPUT_FORMAT, mixerInfo);
        AudioInputStream stream = AudioSystem.getAudioInputStream(AudioSendHandler.INPUT_FORMAT, new AudioInputStream(target));
        target.open();
        target.start();
        logger.info("AudioSystemSendHandler for {} created.", input.fullName());
        logger.debug("Created by mixerInfo.targetDataLine + AudioInputStream.withFormat");
        return new AudioSystemSendHandler(input, target, stream);
    }

    private static AudioSystemSendHandler createWithCustomInfo(AudioInput input, Mixer.Info mixerInfo) throws Exception {
        Mixer mixer = AudioSystem.getMixer(mixerInfo);
        TargetDataLine target = (TargetDataLine) mixer.getLine(new DataLine.Info(TargetDataLine.class, AudioSendHandler.INPUT_FORMAT));
        AudioInputStream stream = new AudioInputStream(target);
        mixer.open();
        target.open(AudioSendHandler.INPUT_FORMAT, FRAME_SIZE);
        target.start();
        logger.info("AudioSystemSendHandler for {} created.", input.fullName());
        logger.debug("Created by Mixer.getLine with custom DataLine.Info");
        return new AudioSystemSendHandler(input, target, stream);
    }

    private static final MessageFormat toStringFormat = new MessageFormat("AudioSystemSendHandler'{'input={0}, sync={1}, thread={2}, readIndex={3}, writeIndex={4}'}'");
}
