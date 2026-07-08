package com.mahefa.pathfindingfx.service.concurrent;

import com.mahefa.pathfindingfx.algorithm.step.Step;
import com.mahefa.pathfindingfx.algorithm.step.StepType;
import com.mahefa.pathfindingfx.algorithm.step.Stepper;
import javafx.animation.AnimationTimer;

import java.util.function.Consumer;

/**
 * Replays a {@link Stepper}'s steps onto a renderer at a speed-controlled cadence. This is the ONLY
 * place that decides timing; algorithms stay pure. Speed is {@code intervalNanos} = the minimum time
 * between steps (smaller = faster), matching the app's {@code *AnimationSpeed} enums.
 * <p>
 * {@link StepType#PATH} steps are paced by a separate, fixed {@code pathIntervalNanos} (0 = "use the
 * search interval"). This mirrors the original A*, whose shortest-path "drawback" always ran at a
 * fixed speed ({@code LaunchAnimationSpeed.SHORTEST_PATH}) regardless of the search-speed slider.
 * <p>
 * {@link #pulse(long)} is the pure, testable core: given the frame timestamp, it applies as many
 * pending steps as the elapsed time allows and returns the count.
 */
public final class StepPlayer {

    private final Stepper stepper;
    private final Consumer<Step> renderer;
    private volatile long intervalNanos;      // search / generation cadence (live)
    private final long pathIntervalNanos;     // fixed cadence for PATH steps (0 = same as above)
    private final boolean instant;            // drain every step in one pulse, ignoring the speed slider

    private Step pending; // one-step lookahead so we can pace by the next step's type
    private long lastToggle;
    private boolean started;

    public StepPlayer(Stepper stepper, Consumer<Step> renderer, long intervalNanos) {
        this(stepper, renderer, intervalNanos, 0L);
    }

    public StepPlayer(Stepper stepper, Consumer<Step> renderer, long intervalNanos, long pathIntervalNanos) {
        this.stepper = stepper;
        this.renderer = renderer;
        // A non-positive interval means "instant": lay down every step in the first pulse, regardless of
        // the speed slider (matches the original basic-random maze, which placed all walls at once).
        this.instant = intervalNanos <= 0L;
        this.intervalNanos = Math.max(1L, intervalNanos);
        this.pathIntervalNanos = pathIntervalNanos;
    }

    /** Live speed change for search/generation (PATH steps keep their fixed cadence). */
    public void setIntervalNanos(long intervalNanos) {
        this.intervalNanos = Math.max(1L, intervalNanos);
    }

    public long getIntervalNanos() {
        return intervalNanos;
    }

    public boolean isFinished() {
        return peek() == null;
    }

    /**
     * Apply as many steps as {@code now} allows since the last one; returns the count applied. Each
     * step is paced by the interval for its own {@link StepType}. The first pulse is seeded so a
     * single step fires immediately (no blank first frame).
     */
    public int pulse(long now) {
        if (instant) {
            int applied = 0;
            while (peek() != null) {
                renderer.accept(take());
                applied++;
            }
            return applied;
        }

        Step next = peek();
        if (!started) {
            started = true;
            lastToggle = now - ((next != null) ? intervalFor(next.type()) : intervalNanos);
        }

        int applied = 0;
        while ((next = peek()) != null) {
            long interval = intervalFor(next.type());
            if ((now - lastToggle) < interval) {
                break;
            }
            renderer.accept(take());
            lastToggle += interval;
            applied++;
        }
        return applied;
    }

    private long intervalFor(StepType type) {
        return (type == StepType.PATH && pathIntervalNanos > 0) ? pathIntervalNanos : intervalNanos;
    }

    private Step peek() {
        if (pending == null && stepper.hasNext()) {
            pending = stepper.next();
        }
        return pending;
    }

    private Step take() {
        Step step = peek();
        pending = null;
        return step;
    }

    /** Wraps this player in an {@link AnimationTimer} for use on the FX thread. */
    public AnimationTimer asAnimationTimer() {
        return new AnimationTimer() {
            @Override
            public void handle(long now) {
                pulse(now);
                if (isFinished()) {
                    stop();
                }
            }
        };
    }
}
