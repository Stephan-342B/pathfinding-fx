package org.mahefa.service.concurrent;

import org.mahefa.common.enumerator.PathFindingAlgorithm;
import org.mahefa.common.exceptions.MissingAlgorithmException;
import org.mahefa.common.exceptions.UnsupportedAlgorithmException;
import org.mahefa.component.Grid;
import org.mahefa.service.pathfinding.AStar;
import org.mahefa.service.pathfinding.Solver;

/**
 * {@link javafx.concurrent.Worker}-based equivalent of {@link org.mahefa.service.RouteFinderService},
 * reusing the existing {@link Solver}/{@link AStar} implementations unchanged.
 */
public class PathfindingWorker extends AnimationWorker<Solver> {

    private final Grid grid;

    private PathFindingAlgorithm algorithm;
    private Solver solver;

    public PathfindingWorker(Grid grid) {
        this.grid = grid;
    }

    public void setAlgorithm(PathFindingAlgorithm algorithm) {
        this.algorithm = algorithm;
    }

    public Solver getSolver() {
        return solver;
    }

    @Override
    protected void onSpeedChanged(Long speed) {
        if (solver != null) {
            solver.setCurrentSpeed(speed);
        }
    }

    public void start() {
        if (algorithm == null) {
            throw new MissingAlgorithmException("Pick an Algorithm!");
        }

        if (isRunning()) {
            cancel();
        }

        switch (algorithm) {
            case A_STAR:
                solver = new AStar(grid);
                break;
            case DIJKSTRA:
            case BREADTH_FIRST_SEARCH:
            case DEPTH_FIRST_SEARCH:
            default:
                throw new UnsupportedAlgorithmException("Unsupported algorithm: " + algorithm);
        }

        grid.clear(false, false, false);
        solver.setCurrentSpeed(getCurrentSpeed());

        begin(solver.solve(), solver.isRunningProperty(), solver);
    }
}
