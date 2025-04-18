package net.notjustanna.logging;

import io.jstach.rainbowgum.*;
import io.jstach.rainbowgum.pattern.format.PatternEncoderBuilder;
import io.jstach.rainbowgum.spi.RainbowGumServiceProvider;
import org.jetbrains.annotations.NotNull;

import java.lang.System.Logger.Level;
import java.net.URI;
import java.util.Optional;

public class LoggingProvider implements RainbowGumServiceProvider.RainbowGumProvider {
    public static final String FILE_PATTERN = "%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n";
    public static final String CONSOLE_PATTERN = "%cyan(%d{HH:mm:ss.SSS}) %gray([%thread]) %highlight(%-5level) %magenta(%logger{36}) - %msg%n";

    @NotNull
    @Override
    public Optional<RainbowGum> provide(@NotNull LogConfig config) {
        return RainbowGum.builder(config) //
            .route(route -> {
                route.level(Level.INFO);
                route.appender("console", appender -> {
                    appender.encoder(
                        patternAppender("console", CONSOLE_PATTERN, config)
                    );
                    appender.output(LogOutput.ofStandardOut());
                });
                route.appender("file", appender -> {
                    appender.encoder(
                        patternAppender("file", FILE_PATTERN, config)
                    );
                    appender.output(fileOutput());
                });
            })
            .optional();
    }

    @NotNull
    private static LogProvider<LogOutput> fileOutput() {
        return LogOutput.of(LogProviderRef.of(URI.create("file:/./AuxCable.log"), "file"));
    }

    @NotNull
    private static LogProvider<LogEncoder> patternAppender(String name, String pattern, @NotNull LogConfig config) {
        return new PatternEncoderBuilder(name)
            .pattern(pattern)
            .fromProperties(config.properties())
            .build();
    }
}