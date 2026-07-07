package org.mahefa.service.pathfinding;

import javafx.animation.AnimationTimer;
import org.mahefa.component.Cell;
import org.mahefa.component.Grid;
import org.mahefa.component.RouteNode;
import org.mahefa.service.State;

import java.util.Map;
import java.util.function.Supplier;

public abstract class Solver extends State {

    protected Grid grid;
    protected RouteNode currentRouteNode;
    protected Map<Cell, RouteNode> nodes;

    protected Long currentSpeed;
    protected long lastToggle = 0L;

    // Live search metrics, read by the ProgressReporter's ticker thread → volatile. Set on the
    // FX Application Thread as the search runs.
    protected volatile int cellsExplored = 0;
    protected volatile int openSetSize = 0;
    protected volatile boolean pathFound = false;
    protected volatile int pathLength = 0;

    public Solver(Grid grid) {
        this.grid = grid;
    }

    public abstract Supplier<AnimationTimer> solve();

    public void setCurrentSpeed(Long speed) {
        this.currentSpeed = speed;
    }

    /** Number of cells moved into the closed set so far. */
    public int getCellsExplored() {
        return cellsExplored;
    }

    /** Current size of the frontier (open set). */
    public int getOpenSetSize() {
        return openSetSize;
    }

    /** Whether the search reached the target (only meaningful once the run has finished). */
    public boolean isPathFound() {
        return pathFound;
    }

    /** Number of cells on the shortest path (0 until the target is reached). */
    public int getPathLength() {
        return pathLength;
    }
}
