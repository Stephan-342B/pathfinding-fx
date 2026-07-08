package com.mahefa.pathfindingfx.algorithm.maze;

import com.mahefa.pathfindingfx.algorithm.grid.GridModel;
import com.mahefa.pathfindingfx.algorithm.step.AbstractStepper;
import com.mahefa.pathfindingfx.algorithm.step.StepType;
import com.mahefa.pathfindingfx.domain.Location;
import com.mahefa.pathfindingfx.domain.enumerator.Direction;

import java.util.Random;

/**
 * Aldous-Broder as a pure {@link com.mahefa.pathfindingfx.algorithm.step.Stepper}: a random walk
 * over the odd-coordinate cells (moving two at a time), carving the wall between when it reaches an
 * unvisited cell, until every cell is visited. Emits steps instead of mutating cells.
 * <p>
 * Fixes the reported bug: the start and target cells are opened ({@code OPEN} → Flag NONE) up front
 * instead of being left as walls. Their {@code NodeType} (START/TARGET) is a separate {@code Cell}
 * property the renderer never touches, so it is preserved.
 */
public class AldousBroderStepper extends AbstractStepper {

    private final boolean[][] visited;
    private final Random random = new Random();

    private Location current;
    private int totalUnvisited;
    private boolean initialized;
    private boolean finished;

    public AldousBroderStepper(GridModel model) {
        super(model);
        this.visited = new boolean[model.getRowLen()][model.getColLen()];
        this.totalUnvisited = (model.getRowLen() / 2) * (model.getColLen() / 2);
    }

    @Override
    protected boolean isComplete() {
        return finished;
    }

    @Override
    protected void advance() {
        if (!initialized) {
            initialized = true;
            // BUG FIX: open start & target so they are not left as walls (NodeType is untouched).
            markVisited(model.getStart());
            emit(model.getStart(), StepType.OPEN);
            markVisited(model.getTarget());
            emit(model.getTarget(), StepType.OPEN);

            current = pickRandomCell();
            markVisited(current);
            emit(current, StepType.CURRENT);
            return;
        }

        if (totalUnvisited <= 0) {
            emit(current, StepType.OPEN); // settle the final highlighted cell
            finished = true;
            return;
        }

        emit(current, StepType.OPEN); // leaving the current cell carves it open
        Location neighbour = randomNeighbour(current);
        emit(neighbour, StepType.CURRENT);

        if (!isVisited(neighbour)) {
            Location wall = new Location(
                    (current.getRow() + neighbour.getRow()) / 2,
                    (current.getCol() + neighbour.getCol()) / 2
            );
            emit(wall, StepType.OPEN);
            markVisited(neighbour);
        }

        current = neighbour;
    }

    private Location pickRandomCell() {
        int row = 1 + random.nextInt((model.getRowLen() - 1) / 2) * 2;
        int col = 1 + random.nextInt((model.getColLen() - 1) / 2) * 2;
        return new Location(row, col);
    }

    // Move two cells in a random cardinal direction (thick walls), retrying until in-bounds.
    private Location randomNeighbour(Location from) {
        Direction[] dirs = {Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT};
        while (true) {
            Direction d = dirs[random.nextInt(dirs.length)];
            Location n = from.move(d).move(d);
            if (model.inBounds(n)) {
                return n;
            }
        }
    }

    private boolean isVisited(Location l) {
        return visited[l.getRow()][l.getCol()];
    }

    // Marks a cell visited; decrements the remaining count once per odd-coordinate ("real") cell so
    // the walk is guaranteed to terminate when all cells have been reached.
    private void markVisited(Location l) {
        if (!visited[l.getRow()][l.getCol()]) {
            visited[l.getRow()][l.getCol()] = true;
            if (l.getRow() % 2 == 1 && l.getCol() % 2 == 1) {
                totalUnvisited--;
            }
        }
    }
}
