package com.mahefa.pathfindingfx.config;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.filter.ThresholdFilter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.FileAppender;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Attaches a per-session, timestamped {@link FileAppender} to the root logger at startup when
 * {@code app.narrative.file.enabled} is true. Done programmatically (rather than via Spring Boot's
 * literal {@code logging.file.name}) because the file name carries a timestamp and can be rolled
 * over on demand by {@link com.mahefa.pathfindingfx.service.concurrent.progress.LogCleaner}. The narrative logs
 * only during user actions — long after startup — so attaching at bean init (which misses Spring's
 * own early boot lines) is intentional and keeps the file focused on the app's own activity.
 */
@Component
public class LogFileInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogFileInitializer.class);

    private final AppProperties appProperties;

    public LogFileInitializer(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    @PostConstruct
    void attachFileAppenderIfEnabled() {
        AppProperties.Narrative.File config = appProperties.getNarrative().getFile();
        if (!config.isEnabled()) {
            return;
        }

        if (!(LoggerFactory.getILoggerFactory() instanceof LoggerContext context)) {
            LOGGER.warn("SLF4J backing is not Logback; file logging not attached");
            return;
        }

        try {
            Files.createDirectories(Paths.get(config.getDirectory()));
        } catch (IOException e) {
            LOGGER.warn("Could not create log directory {}", config.getDirectory(), e);
        }

        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setContext(context);
        encoder.setPattern(config.getPattern());
        encoder.start();

        // TRACE = pass everything the loggers already allow (com.mahefa.pathfindingfx is at DEBUG), so the file
        // captures the per-cell detail the INFO-thresholded console deliberately drops.
        ThresholdFilter filter = new ThresholdFilter();
        filter.setLevel("TRACE");
        filter.setContext(context);
        filter.start();

        FileAppender<ILoggingEvent> appender = new FileAppender<>();
        appender.setContext(context);
        appender.setName("NARRATIVE_FILE");
        appender.setFile(config.newTimestampedPath());
        appender.setAppend(true);
        appender.setEncoder(encoder);
        appender.addFilter(filter);
        appender.start();

        context.getLogger(Logger.ROOT_LOGGER_NAME).addAppender(appender);
        LOGGER.info("File logging enabled → {}", appender.getFile());
    }
}
