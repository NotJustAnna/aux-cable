package net.notjustanna.utils;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;

public class MainThreadExecutor implements Runnable, Executor {
    private final BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();
    private final Thread thread = Thread.currentThread();

    @Override
    public void run() {
        while (true) {
            try {
                Runnable runnable = queue.take();
                runnable.run();
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    @Override
    public void execute(@NotNull Runnable command) {
        queue.add(command);
    }

    public void shutdown() {
        thread.interrupt();
    }
}
