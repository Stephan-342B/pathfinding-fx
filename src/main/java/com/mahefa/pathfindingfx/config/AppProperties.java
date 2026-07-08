package com.mahefa.pathfindingfx.config;

import com.mahefa.pathfindingfx.service.concurrent.progress.LogCleaner;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Type-safe binding for this application's own configuration, all under the {@code app.*} prefix so
 * it never collides with Spring's own namespaces (e.g. {@code logging.*}, which stays top-level and
 * is owned by Spring Boot). Replaces the scattered {@code @Value} lookups.
 */
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private final Grid grid = new Grid();
    private final Narrative narrative = new Narrative();

    public Grid getGrid() {
        return grid;
    }

    public Narrative getNarrative() {
        return narrative;
    }

    public static class Grid {
        /** Side length, in pixels, of each square grid cell. */
        private double size = 25.0;

        public double getSize() {
            return size;
        }

        public void setSize(double size) {
            this.size = size;
        }
    }

    public static class Narrative {
        private final File file = new File();
        private final Clear clear = new Clear();

        public File getFile() {
            return file;
        }

        public Clear getClear() {
            return clear;
        }

        /** Builds a {@link LogCleaner} wired to this narrative's clear modes and file naming. */
        public LogCleaner newLogCleaner() {
            return new LogCleaner(clear.getConsole(), clear.getFile(), file::newTimestampedPath);
        }

        /** Optional per-session log file (opt-in). Written to a fresh timestamped file per run. */
        public static class File {
            private static final DateTimeFormatter TIMESTAMP = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");

            /** When true, a timestamped file appender is attached at startup. */
            private boolean enabled = false;
            /** Directory the log files live in (created if missing). */
            private String directory = "logs";
            /** Base name; the timestamp and {@code .log} extension are appended. */
            private String baseName = "pathfinding-fx";
            /** Logback encoder pattern for the file (full detail, unlike the message-only console). */
            private String pattern = "%d{yyyy-MM-dd HH:mm:ss.SSS} %-5level [%thread] %logger{36} - %msg%n";

            /** A fresh timestamped path, e.g. {@code logs/pathfinding-fx-2026-07-07_14-30-05.log}. */
            public String newTimestampedPath() {
                String name = baseName + "-" + LocalDateTime.now().format(TIMESTAMP) + ".log";
                return Paths.get(directory, name).toString();
            }

            public boolean isEnabled() {
                return enabled;
            }

            public void setEnabled(boolean enabled) {
                this.enabled = enabled;
            }

            public String getDirectory() {
                return directory;
            }

            public void setDirectory(String directory) {
                this.directory = directory;
            }

            public String getBaseName() {
                return baseName;
            }

            public void setBaseName(String baseName) {
                this.baseName = baseName;
            }

            public String getPattern() {
                return pattern;
            }

            public void setPattern(String pattern) {
                this.pattern = pattern;
            }
        }

        /** What the Clear Board / Walls / Path buttons do to the narrative output. */
        public static class Clear {
            /** Console: {@code WIPE} clears the screen, {@code MARKER} prints a divider. */
            private LogCleaner.ConsoleMode console = LogCleaner.ConsoleMode.WIPE;
            /** File: {@code WIPE} truncates, {@code APPEND} adds a divider, {@code NEW_FILE} rolls over. */
            private LogCleaner.FileMode file = LogCleaner.FileMode.NEW_FILE;

            public LogCleaner.ConsoleMode getConsole() {
                return console;
            }

            public void setConsole(LogCleaner.ConsoleMode console) {
                this.console = console;
            }

            public LogCleaner.FileMode getFile() {
                return file;
            }

            public void setFile(LogCleaner.FileMode file) {
                this.file = file;
            }
        }
    }
}
