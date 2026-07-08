package com.mahefa.pathfindingfx.service.concurrent;

import com.mahefa.pathfindingfx.algorithm.step.Step;
import com.mahefa.pathfindingfx.algorithm.step.Stepper;
import com.mahefa.pathfindingfx.service.concurrent.progress.Narrative;
import com.mahefa.pathfindingfx.service.concurrent.progress.ProgressReporter;
import com.mahefa.pathfindingfx.ui.component.Grid;
import com.mahefa.pathfindingfx.ui.component.GridStepRenderer;
import javafx.animation.AnimationTimer;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * A single, reusable {@link AnimationWorker} that drives ANY {@link Stepper} — maze generator or
 * pathfinder alike — through a {@link StepPlayer} rendering onto the grid via {@link GridStepRenderer}.
 * One worker + one player + one renderer for every algorithm; a new algorithm is just a new
 * {@code Stepper}. Speed changes are forwarded live, and each run's narrative (▶ header, live
 * spinner, ✓/✗ summary) is produced from a caller-supplied {@link RunNarrative} + live {@link StepTally}.
 */
public class StepWorker extends AnimationWorker<Object> {

    private static final long DEFAULT_INTERVAL_NANOS = 25_000_000L; // 25 ms

    private final Grid grid;
    private final BooleanProperty running = new SimpleBooleanProperty(false);
    private final ProgressReporter reporter = new ProgressReporter();

    private StepPlayer player;
    private RunNarrative narrative;
    private StepTally tally;

    public StepWorker(Grid grid) {
        this.grid = grid;
    }

    @Override
    protected void onSpeedChanged(Long speed) {
        if (player != null && speed != null) {
            player.setIntervalNanos(speed);
        }
    }

    /** Runs the given stepper, rendering each step onto the grid at the current speed. */
    public void run(Stepper stepper, RunNarrative narrative) {
        run(stepper, narrative, 0L);
    }

    /**
     * Runs the stepper at the current speed, pacing {@link com.mahefa.pathfindingfx.algorithm.step.StepType#PATH}
     * steps at the fixed {@code pathIntervalNanos} (0 = same as the search speed).
     */
    public void run(Stepper stepper, RunNarrative narrative, long pathIntervalNanos) {
        run(stepper, narrative, pathIntervalNanos, false);
    }

    /**
     * Runs the stepper instantly — every step is laid down in one pulse, regardless of the speed
     * slider. Matches the original basic-random maze, which placed all its walls at once.
     */
    public void runInstant(Stepper stepper, RunNarrative narrative) {
        run(stepper, narrative, 0L, true);
    }

    private void run(Stepper stepper, RunNarrative narrative, long pathIntervalNanos, boolean instant) {
        if (isRunning()) {
            cancel();
        }

        this.narrative = narrative;
        this.tally = new StepTally();

        GridStepRenderer gridRenderer = new GridStepRenderer(grid);
        Consumer<Step> renderer = step -> {
            gridRenderer.accept(step);
            tally.record(step.type());
        };

        long interval = instant ? 0L : ((getCurrentSpeed() != null) ? getCurrentSpeed() : DEFAULT_INTERVAL_NANOS);
        player = new StepPlayer(stepper, renderer, interval, pathIntervalNanos);

        updateMessage(narrative.header());
        reporter.start(narrative.header(), narrative.configLine(), narrative.activityLabel(),
                () -> narrative.liveMetrics().apply(tally));

        running.set(true);
        Supplier<AnimationTimer> supplier = () -> new AnimationTimer() {
            @Override
            public void handle(long now) {
                player.pulse(now);
                if (player.isFinished()) {
                    running.set(false);
                    stop();
                }
            }
        };

        begin(supplier, running, this);
    }

    @Override
    protected void onSucceeded() {
        String summary = narrative.summary().apply(tally, reporter.elapsedSeconds());
        updateMessage(summary);
        reporter.finish(narrative.success().test(tally), summary);
    }

    @Override
    protected void onCancelled() {
        if (!reporter.isReporting()) {
            return;
        }
        String summary = Narrative.FAIL + " Cancelled " + Narrative.DOT + " " + Narrative.human(reporter.elapsedSeconds());
        updateMessage(summary);
        reporter.finish(false, summary);
    }
}
