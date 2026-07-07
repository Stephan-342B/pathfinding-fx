package com.mahefa.pathfindingfx.service.concurrent.progress;

/**
 * Shared vocabulary for the console narrative: the symbols that give each line its shape and the
 * duration formatter, kept in one place so the workers and {@link ProgressReporter} render a
 * consistent story. Mirrors the style used by the sibling scan-2-sheet project.
 */
public final class Narrative {

    /** Run header marker, e.g. {@code ▶ pathfinding · A*}. */
    public static final String START = "▶";
    /** Separates the subject from its destination, e.g. {@code A*  →  25×25 grid}. */
    public static final String ARROW = "→";
    /** Success marker for committed/summary lines. */
    public static final String OK = "✓";
    /** Failure / no-result / cancelled marker. */
    public static final String FAIL = "✗";
    /** Tree branch prefix for the live activity line. */
    public static final String BRANCH = "├─";
    /** Separates metrics within a line, e.g. {@code 142 cells · open 18 · 3s}. */
    public static final String DOT = "·";

    private Narrative() {
    }

    /** Formats a duration as {@code 45s} under a minute, or {@code 2m05s} beyond it. */
    public static String human(long seconds) {
        return seconds < 60 ? seconds + "s" : String.format("%dm%02ds", seconds / 60, seconds % 60);
    }
}
