package net.notjustanna;

import io.micronaut.runtime.Micronaut;
import net.notjustanna.utils.MainThreadExecutor;

public class Application {

    public static void main(String[] args) {
        MainThreadExecutor executor = new MainThreadExecutor();

        Micronaut.build(args)
            .mainClass(Application.class)
            .singletons(executor)
            .start();

        executor.run();
    }
}