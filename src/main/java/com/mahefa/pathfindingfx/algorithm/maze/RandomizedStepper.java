package com.mahefa.pathfindingfx.algorithm.maze;

import com.mahefa.pathfindingfx.algorithm.grid.GridModel;
import com.mahefa.pathfindingfx.algorithm.step.AbstractStepper;
import com.mahefa.pathfindingfx.algorithm.step.StepType;
import com.mahefa.pathfindingfx.domain.Location;

/**
 * The basic random maze as a pure {@link com.mahefa.pathfindingfx.algorithm.step.Stepper}: scans the
 * grid and, with ~25% probability, marks a (non-start/target) cell as a wall. It emits one wall per
 * pulled step, but the service replays it instantly (all walls at once, independent of the speed
 * slider) to match the original — see {@code GridService.generateMaze} / {@code StepWorker.runInstant}.
 */
public class RandomizedStepper extends AbstractStepper {

    private int index;
    private boolean finished;

    public RandomizedStepper(GridModel model) {
        super(model);
    }

    @Override
    protected boolean isComplete() {
        return finished;
    }

    @Override
    protected void advance() {
        int total = model.getRowLen() * model.getColLen();
        while (index < total) {
            int row = index / model.getColLen();
            int col = index % model.getColLen();
            index++;

            Location location = new Location(row, col);
            boolean special = location.equals(model.getStart()) || location.equals(model.getTarget());
            if (!special && Math.random() < 0.25) {
                emit(location, StepType.WALL);
                return;
            }
        }
        finished = true;
    }
}
