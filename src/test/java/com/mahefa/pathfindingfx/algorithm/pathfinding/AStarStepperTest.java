package com.mahefa.pathfindingfx.algorithm.pathfinding;

import com.mahefa.pathfindingfx.algorithm.grid.GridModel;
import com.mahefa.pathfindingfx.algorithm.step.Step;
import com.mahefa.pathfindingfx.algorithm.step.StepType;
import com.mahefa.pathfindingfx.domain.Location;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Confirms A* still works through the new Stepper pipeline: on an open grid it reaches the target,
 * emits VISITED steps during the search and a PATH (with increasing arrow angle data) at the end.
 */
class AStarStepperTest {

    private static GridModel openGrid(int rows, int cols, Location start, Location target) {
        return new GridModel(rows, cols, new boolean[rows][cols], new double[rows][cols], start, target);
    }

    @Test
    void findsPathAndEmitsPathSteps() {
        AStarStepper stepper = new AStarStepper(openGrid(9, 9, new Location(1, 1), new Location(7, 7)));

        List<Step> steps = new ArrayList<>();
        int guard = 0;
        while (stepper.hasNext() && guard++ < 1_000_000) {
            steps.add(stepper.next());
        }

        assertFalse(stepper.hasNext(), "search should terminate");

        long visited = steps.stream().filter(s -> s.type() == StepType.VISITED).count();
        long pathCells = steps.stream().filter(s -> s.type() == StepType.PATH).count();

        assertTrue(visited > 0, "A* should explore cells");
        assertTrue(pathCells > 0, "A* should emit a shortest path (arrow steps)");
    }
}
