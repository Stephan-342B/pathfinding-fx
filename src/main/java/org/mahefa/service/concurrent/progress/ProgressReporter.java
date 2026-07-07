package org.mahefa.service.concurrent.progress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Renders a single run's progress as a live, in-place console narrative, mirroring the style of the
 * sibling scan-2-sheet project (re-themed here for one search/generation instead of a folder tree):
 *
 * <pre>
 *   ▶ pathfinding · A*  →  25×25 grid
 *     start (2,2) · target (22,20) · Manhattan
 *     ├─ ⠼ search   142 cells · open 18 · 3s     ← live line, spinner + growing counters
 *   ✓ Path found · 34 steps · 142 cells explored · 2s
 * </pre>
 *
 * The header, config line and final summary are logged via SLF4J at INFO (so they also reach the
 * optional file appender); the animated {@code ├─} activity line is written straight to
 * {@code System.out} because it must redraw in place, which a logger cannot do. A background daemon
 * ticker animates the live line; every console write is serialized on a lock so the ticker and the
 * final commit never interleave on the same line. The metrics for the live line are pulled from a
 * caller-supplied {@link Supplier} each tick, so the reporter stays ignorant of the domain.
 */
public final class ProgressReporter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProgressReporter.class);

    private static final String[] FRAMES = {"⠋", "⠙", "⠹", "⠸", "⠼", "⠴", "⠦", "⠧", "⠇", "⠏"};
    private static final long TICK_MS = 250;
    private static final String CLEAR_TAIL = "          "; // overwrite leftovers when a line shrinks

    private final Object console = new Object();

    private volatile String activityLabel = "";
    private volatile Supplier<String> metrics = () -> "";
    private volatile long startMillis;
    private volatile boolean active = false;

    private int frame;
    private ScheduledExecutorService ticker;

    /**
     * Opens the narrative: logs the {@code header} (and optional {@code configLine}) and starts
     * animating an {@code ├─ <activityLabel>} line whose metrics are read from {@code metrics}
     * every tick. {@code metrics} should return a {@code ·}-separated fragment such as
     * {@code "142 cells · open 18"} (the elapsed time is appended automatically).
     */
    public synchronized void start(String header, String configLine, String activityLabel, Supplier<String> metrics) {
        if (ticker != null) {
            // Defensive: a prior run was never finished. Tear it down without committing a line.
            ticker.shutdownNow();
            ticker = null;
            synchronized (console) {
                active = false;
            }
        }

        this.activityLabel = activityLabel;
        this.metrics = (metrics != null) ? metrics : () -> "";
        this.startMillis = System.currentTimeMillis();
        this.frame = 0;
        this.active = true;

        synchronized (console) {
            System.out.println(); // blank separator before the header
            System.out.flush();
        }
        LOGGER.info(header);
        if (configLine != null && !configLine.isBlank()) {
            LOGGER.info(configLine);
        }

        ticker = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "pfx-progress");
            t.setDaemon(true);
            return t;
        });
        ticker.scheduleAtFixedRate(this::redraw, TICK_MS, TICK_MS, TimeUnit.MILLISECONDS);
    }

    /**
     * Commits the live line with a {@code ✓}/{@code ✗} marker and logs {@code summary}. Idempotent:
     * a second call (e.g. cancel racing completion) is a no-op once the ticker is gone.
     */
    public synchronized void finish(boolean ok, String summary) {
        if (ticker != null) {
            ticker.shutdownNow();
            ticker = null;
        }
        synchronized (console) {
            if (active) {
                System.out.print("\r" + line(ok ? Narrative.OK : Narrative.FAIL) + CLEAR_TAIL + System.lineSeparator());
                System.out.flush();
                active = false;
            } else {
                return; // already finished; don't log a duplicate summary
            }
        }
        if (summary != null && !summary.isBlank()) {
            LOGGER.info(summary);
        }
    }

    /**
     * One-shot narrative for work that completed synchronously (no live line to animate): logs the
     * {@code header} then the {@code summary}. Used e.g. by the instant random maze generator.
     */
    public synchronized void report(String header, String summary) {
        synchronized (console) {
            System.out.println(); // blank separator before the header
            System.out.flush();
        }
        LOGGER.info(header);
        if (summary != null && !summary.isBlank()) {
            LOGGER.info(summary);
        }
    }

    /** Whole-run elapsed time in seconds; lets callers build a summary with a matching duration. */
    public long elapsedSeconds() {
        return (System.currentTimeMillis() - startMillis) / 1000;
    }

    /** True while a run is being reported (between {@link #start} and {@link #finish}). */
    public boolean isReporting() {
        return active;
    }

    private void redraw() {
        synchronized (console) {
            if (!active) {
                return;
            }
            System.out.print("\r" + line(FRAMES[Math.abs(frame++) % FRAMES.length]) + CLEAR_TAIL);
            System.out.flush();
        }
    }

    private String line(String marker) {
        long secs = (System.currentTimeMillis() - startMillis) / 1000;
        String m = metrics.get();
        String detail = (m == null || m.isBlank())
                ? Narrative.human(secs)
                : m + " " + Narrative.DOT + " " + Narrative.human(secs);
        return String.format("  %s %s %s   %s", Narrative.BRANCH, marker, activityLabel, detail);
    }
}
