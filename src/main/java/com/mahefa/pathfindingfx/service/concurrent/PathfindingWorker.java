package com.mahefa.pathfindingfx.service.concurrent;

import com.mahefa.pathfindingfx.domain.enumerator.PathFindingAlgorithm;
import com.mahefa.pathfindingfx.exception.MissingAlgorithmException;
import com.mahefa.pathfindingfx.exception.UnsupportedAlgorithmException;
import com.mahefa.pathfindingfx.ui.component.Grid;
import com.mahefa.pathfindingfx.domain.Location;
import com.mahefa.pathfindingfx.service.concurrent.progress.Narrative;
import com.mahefa.pathfindingfx.service.concurrent.progress.ProgressReporter;
import com.mahefa.pathfindingfx.algorithm.pathfinding.AStar;
import com.mahefa.pathfindingfx.algorithm.pathfinding.Solver;

/**
 * {@link javafx.concurrent.Worker}-based equivalent of the former {@code RouteFinderService},
 * reusing the existing {@link Solver}/{@link AStar} implementations unchanged.
 */
public class PathfindingWorker extends AnimationWorker<Solver> {

    private final Grid grid;
    private final ProgressReporter reporter = new ProgressReporter();

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

        Location start = grid.getStartCell().getLocation();
        Location target = grid.getTargetCell().getLocation();
        String header = String.format("%s pathfinding %s %s  %s  %d×%d grid",
                Narrative.START, Narrative.DOT, algorithm.getLabel(), Narrative.ARROW,
                grid.getRowLen(), grid.getColLen());
        String config = String.format("  start (%d,%d) %s target (%d,%d) %s Manhattan",
                start.getRow(), start.getCol(), Narrative.DOT,
                target.getRow(), target.getCol(), Narrative.DOT);

        updateMessage(header);
        reporter.start(header, config, "search",
                () -> String.format("%d cells %s open %d",
                        solver.getCellsExplored(), Narrative.DOT, solver.getOpenSetSize()));

        begin(solver.solve(), solver.isRunningProperty(), solver);
    }

    @Override
    protected void onSucceeded() {
        long secs = reporter.elapsedSeconds();
        String summary = solver.isPathFound()
                ? String.format("%s Path found %s %d steps %s %d cells explored %s %s",
                        Narrative.OK, Narrative.DOT, solver.getPathLength(), Narrative.DOT,
                        solver.getCellsExplored(), Narrative.DOT, Narrative.human(secs))
                : String.format("%s No path %s %d cells explored %s %s",
                        Narrative.FAIL, Narrative.DOT, solver.getCellsExplored(), Narrative.DOT,
                        Narrative.human(secs));

        updateMessage(summary);
        reporter.finish(solver.isPathFound(), summary);
    }

    @Override
    protected void onCancelled() {
        if (!reporter.isReporting()) {
            return;
        }

        int explored = (solver != null) ? solver.getCellsExplored() : 0;
        String summary = String.format("%s Cancelled %s %d cells explored %s %s",
                Narrative.FAIL, Narrative.DOT, explored, Narrative.DOT,
                Narrative.human(reporter.elapsedSeconds()));

        updateMessage(summary);
        reporter.finish(false, summary);
    }
}
