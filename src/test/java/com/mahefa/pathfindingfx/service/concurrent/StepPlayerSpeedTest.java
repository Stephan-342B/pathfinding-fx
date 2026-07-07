package com.mahefa.pathfindingfx.service.concurrent;

import com.mahefa.pathfindingfx.algorithm.grid.GridModel;
import com.mahefa.pathfindingfx.algorithm.maze.AldousBroderStepper;
import com.mahefa.pathfindingfx.algorithm.step.Stepper;
import com.mahefa.pathfindingfx.domain.Location;
import com.mahefa.pathfindingfx.domain.enumerator.MazeGenerationAnimationSpeed;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Proves the key property of the record/replay design: the speed setting controls how much of the
 * visualization renders per unit time, and a slow/open-ended algorithm (Aldous-Broder) still renders
 * immediately (no precompute freeze). Fully headless — drives {@link StepPlayer#pulse} with synthetic
 * frame timestamps and counts applied steps; no JavaFX toolkit involved.
 */
class StepPlayerSpeedTest {

    private static final long FRAME_DT = 16_666_667L; // ~60 fps
    private static final int FRAMES = 120;            // ~2 seconds of playback
    private static final long START_NOW = 1_000_000_000L;

    private static GridModel maze(int rows, int cols) {
        boolean[][] wall = new boolean[rows][cols];
        double[][] weight = new double[rows][cols];
        return new GridModel(rows, cols, wall, weight, new Location(1, 1), new Location(rows - 2, cols - 2));
    }

    private int render(long intervalNanos, Stepper stepper) {
        AtomicInteger rendered = new AtomicInteger();
        StepPlayer player = new StepPlayer(stepper, step -> rendered.incrementAndGet(), intervalNanos);
        long now = START_NOW;
        for (int i = 0; i < FRAMES; i++) {
            player.pulse(now);
            now += FRAME_DT;
        }
        return rendered.get();
    }

    // Big enough that the random walk never finishes within the frame budget, so counts reflect SPEED.
    private Stepper bigMaze() {
        return new AldousBroderStepper(maze(51, 51));
    }

    @Test
    void fasterSpeedRendersMoreStepsInTheSameTime() {
        int fast = render(MazeGenerationAnimationSpeed.FAST.getInterval(), bigMaze());
        int average = render(MazeGenerationAnimationSpeed.AVERAGE.getInterval(), bigMaze());
        int slow = render(MazeGenerationAnimationSpeed.SLOW.getInterval(), bigMaze());

        System.out.printf("rendered over ~2s — fast=%d, average=%d, slow=%d%n", fast, average, slow);

        assertTrue(fast > average, "FAST should render more than AVERAGE (fast=" + fast + ", avg=" + average + ")");
        assertTrue(average > slow, "AVERAGE should render more than SLOW (avg=" + average + ", slow=" + slow + ")");
        assertTrue(slow <= 5, "SLOW should render only a few steps over ~2s, was " + slow);
        assertTrue(fast >= 100, "FAST should render many steps over ~2s, was " + fast);
    }

    @Test
    void slowAlgorithmRendersOnTheVeryFirstFrame() {
        AtomicInteger rendered = new AtomicInteger();
        StepPlayer player = new StepPlayer(bigMaze(), step -> rendered.incrementAndGet(),
                MazeGenerationAnimationSpeed.SLOW.getInterval());

        int appliedOnFirstPulse = player.pulse(START_NOW);

        assertTrue(appliedOnFirstPulse >= 1, "expected an immediate first step, got " + appliedOnFirstPulse);
    }

    @Test
    void aldousBroderStreamsToCompletion() {
        Stepper stepper = new AldousBroderStepper(maze(7, 7));

        int steps = 0;
        int guard = 0;
        while (stepper.hasNext() && guard++ < 1_000_000) {
            stepper.next();
            steps++;
        }

        assertFalse(stepper.hasNext(), "stepper should terminate");
        assertTrue(steps > 0, "stepper should emit steps");
    }
}
