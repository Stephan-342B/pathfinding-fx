package com.mahefa.pathfindingfx.service.concurrent;

import com.mahefa.pathfindingfx.algorithm.step.Step;
import com.mahefa.pathfindingfx.algorithm.step.StepType;
import com.mahefa.pathfindingfx.algorithm.step.Stepper;
import com.mahefa.pathfindingfx.domain.Location;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Locks in the original behaviour: the shortest-path (PATH) animation runs at its own fixed, fast
 * interval and is NOT slowed by the search-speed slider (in the original, the drawback used
 * {@code LaunchAnimationSpeed.SHORTEST_PATH} regardless of the chosen search speed).
 */
class StepPlayerPathSpeedTest {

    private static Stepper of(List<Step> steps) {
        Iterator<Step> it = steps.iterator();
        return new Stepper() {
            @Override public boolean hasNext() { return it.hasNext(); }
            @Override public Step next() { return it.next(); }
        };
    }

    @Test
    void pathStepsUseTheFixedFastIntervalNotTheSlowSearchSpeed() {
        long slowSearch = 1_000_000_000L; // 1 s per search step
        long fastPath = 10_000_000L;      // 10 ms per path step (fixed)

        List<Step> steps = new ArrayList<>();
        steps.add(Step.of(new Location(0, 0), StepType.CURRENT)); // one search step
        for (int i = 0; i < 30; i++) {
            steps.add(Step.path(new Location(1, i), i)); // then the path
        }

        AtomicInteger rendered = new AtomicInteger();
        StepPlayer player = new StepPlayer(of(steps), s -> rendered.incrementAndGet(), slowSearch, fastPath);

        player.pulse(0L);                 // seeds + renders the first (CURRENT) step
        int afterSearch = rendered.get();

        player.pulse(100_000_000L);       // 100 ms later
        int pathRendered = rendered.get() - afterSearch;

        // At 10 ms/step, ~100 ms renders many path cells; at the 1 s search speed it would render 0.
        assertTrue(pathRendered >= 5,
                "PATH should pace at the fixed fast interval (rendered " + pathRendered + " in 100ms)");
    }
}
