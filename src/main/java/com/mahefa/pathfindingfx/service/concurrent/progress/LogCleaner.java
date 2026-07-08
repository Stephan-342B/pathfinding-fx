package com.mahefa.pathfindingfx.service.concurrent.progress;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.FileAppender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Resets the narrative output when the grid is cleared (Clear Board / Walls / Path). The console
 * and the optional log file are handled independently, each with its own configurable mode:
 *
 * <ul>
 *   <li>Console — {@link ConsoleMode#WIPE} clears the terminal screen; {@link ConsoleMode#MARKER}
 *       leaves it and prints a {@code — <label> —} divider.</li>
 *   <li>File — {@link FileMode#WIPE} truncates the current log file; {@link FileMode#APPEND} keeps
 *       it and writes a divider; {@link FileMode#NEW_FILE} rolls to a fresh timestamped file.</li>
 * </ul>
 *
 * Targets are separated cleanly: the console is driven straight through {@code System.out}, while
 * the file divider is emitted at DEBUG — which the INFO-thresholded console drops but the file
 * keeps — so the two never bleed into each other. All file operations are no-ops when file logging
 * is disabled (no {@link FileAppender} is attached). A console wipe only shows on a real terminal
 * (TTY), not an IDE console tab.
 */
public final class LogCleaner {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogCleaner.class);

    // Home the cursor (ESC[H), clear the visible screen (ESC[2J), then the scrollback buffer (ESC[3J).
    private static final String ANSI_CLEAR = "\u001B[H\u001B[2J\u001B[3J";

    public enum ConsoleMode {
        WIPE, MARKER
    }

    public enum FileMode {
        WIPE, APPEND, NEW_FILE
    }

    private final ConsoleMode consoleMode;
    private final FileMode fileMode;
    private final Supplier<String> newFilePath;

    /**
     * @param newFilePath supplies a fresh timestamped path for {@link FileMode#NEW_FILE}; may be
     *                    {@code null} (NEW_FILE then degrades to truncating the current file).
     */
    public LogCleaner(ConsoleMode consoleMode, FileMode fileMode, Supplier<String> newFilePath) {
        this.consoleMode = consoleMode;
        this.fileMode = fileMode;
        this.newFilePath = newFilePath;
    }

    /** Applies the configured console and file behaviour for a clear action described by {@code label}. */
    public void onClear(String label) {
        clearConsole(label);
        clearFile(label);
    }

    private void clearConsole(String label) {
        if (consoleMode == ConsoleMode.WIPE) {
            System.out.print(ANSI_CLEAR);
        } else {
            System.out.println("— " + label + " —");
        }
        System.out.flush();
    }

    private void clearFile(String label) {
        switch (fileMode) {
            case WIPE -> forEachFileAppender(this::truncate);
            case NEW_FILE -> forEachFileAppender(this::rollToNewFile);
            // DEBUG so the divider reaches only the file (the console appender is thresholded at INFO).
            case APPEND -> LOGGER.debug("— {} —", label);
        }
    }

    private void truncate(FileAppender<ILoggingEvent> appender) {
        String path = appender.getFile();
        appender.stop();
        try (FileOutputStream ignored = new FileOutputStream(path, false)) {
            // Opening in overwrite mode truncates the file to zero length.
        } catch (IOException e) {
            LOGGER.warn("Could not truncate log file {}", path, e);
        } finally {
            appender.start();
        }
    }

    private void rollToNewFile(FileAppender<ILoggingEvent> appender) {
        String next = (newFilePath != null) ? newFilePath.get() : null;
        if (next == null) {
            truncate(appender); // no naming info available; fall back to a fresh empty file
            return;
        }
        appender.stop();
        appender.setFile(next);
        appender.start(); // reopens against the new (append=true, empty) file
    }

    /**
     * Runs {@code action} against every {@link FileAppender} on the root logger. No-op when file
     * logging is disabled (no such appender exists). Appenders are stopped/restarted by the actions
     * themselves so the file mutation is clean rather than fighting Logback's open stream.
     */
    private void forEachFileAppender(Consumer<FileAppender<ILoggingEvent>> action) {
        if (!(LoggerFactory.getILoggerFactory() instanceof ch.qos.logback.classic.LoggerContext context)) {
            return;
        }

        ch.qos.logback.classic.Logger root = context.getLogger(Logger.ROOT_LOGGER_NAME);
        for (Iterator<Appender<ILoggingEvent>> it = root.iteratorForAppenders(); it.hasNext(); ) {
            Appender<ILoggingEvent> appender = it.next();
            if (appender instanceof FileAppender<ILoggingEvent> fileAppender) {
                action.accept(fileAppender);
            }
        }
    }
}
