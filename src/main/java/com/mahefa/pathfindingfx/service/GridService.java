package com.mahefa.pathfindingfx.service;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import com.mahefa.pathfindingfx.algorithm.maze.AldousBroderStepper;
import com.mahefa.pathfindingfx.algorithm.maze.RandomizedStepper;
import com.mahefa.pathfindingfx.algorithm.pathfinding.AStarStepper;
import com.mahefa.pathfindingfx.algorithm.step.Stepper;
import com.mahefa.pathfindingfx.algorithm.step.StepType;
import com.mahefa.pathfindingfx.domain.Location;
import com.mahefa.pathfindingfx.exception.MissingAlgorithmException;
import com.mahefa.pathfindingfx.exception.UnsupportedAlgorithmException;
import com.mahefa.pathfindingfx.domain.enumerator.AnimationSpeed;
import com.mahefa.pathfindingfx.domain.enumerator.LaunchAnimationSpeed;
import com.mahefa.pathfindingfx.domain.enumerator.MazeAlgorithm;
import com.mahefa.pathfindingfx.domain.enumerator.MazeGenerationAnimationSpeed;
import com.mahefa.pathfindingfx.domain.enumerator.PathFindingAlgorithm;
import com.mahefa.pathfindingfx.ui.component.Grid;
import com.mahefa.pathfindingfx.ui.component.GridStepRenderer;
import com.mahefa.pathfindingfx.service.concurrent.GridSnapshot;
import com.mahefa.pathfindingfx.service.concurrent.RunNarrative;
import com.mahefa.pathfindingfx.service.concurrent.StepWorker;
import com.mahefa.pathfindingfx.service.concurrent.progress.LogCleaner;
import com.mahefa.pathfindingfx.service.concurrent.progress.Narrative;

import static com.mahefa.pathfindingfx.ui.style.CellStyle.Flag.NONE;
import static com.mahefa.pathfindingfx.ui.style.CellStyle.Flag.WALL_NODE;

/**
 * Orchestrates algorithm runs. Both maze generation and pathfinding now flow through a single
 * reusable {@link StepWorker}: the grid is snapshotted into a pure model, the matching
 * {@link Stepper} is built, and the worker replays its steps at the current speed.
 */
public class GridService {

    protected Grid grid;
    private final StepWorker stepWorker;
    private final LogCleaner logCleaner;

    private PathFindingAlgorithm pathAlgorithm;
    private AnimationSpeed currentSpeed;
    private boolean lastRunWasMaze;

    private BooleanProperty isReady = new SimpleBooleanProperty(false);
    // True once a pathfinding run has finished and its result is on the grid (the original's "algoDone").
    // Gates the instant recompute while dragging start/target — no point recomputing if nothing ran yet.
    private final BooleanProperty pathDisplayed = new SimpleBooleanProperty(false);

    public GridService(Grid grid, LogCleaner logCleaner) {
        this.grid = grid;
        this.logCleaner = logCleaner;
        this.stepWorker = new StepWorker(grid);

        isReady.bind(stepWorker.runningProperty().not());
    }

    public Grid getGrid() {
        return grid;
    }

    /** Selects the pathfinding algorithm to use on the next {@link #findPath()}. */
    public void setPathAlgorithm(PathFindingAlgorithm algorithm) {
        this.pathAlgorithm = algorithm;
    }

    public void generateMaze(MazeAlgorithm algorithm) {
        lastRunWasMaze = true;
        pathDisplayed.set(false);
        Stepper stepper;
        switch (algorithm) {
            case ALDOUS_BRODER -> {
                grid.setDefaultFlag(WALL_NODE);
                grid.clear(true, false, false);
                stepper = new AldousBroderStepper(GridSnapshot.of(grid));
            }
            case BASIC_RANDOM -> {
                grid.setDefaultFlag(NONE);
                grid.clear(true, false, false);
                stepper = new RandomizedStepper(GridSnapshot.of(grid));
            }
            default -> throw new UnsupportedAlgorithmException("Unsupported algorithm: " + algorithm);
        }

        // Basic random drops all its walls at once, independent of the UI speed slider (like the
        // original); Aldous-Broder is a genuine walk and stays paced by the slider.
        if (algorithm == MazeAlgorithm.BASIC_RANDOM) {
            stepWorker.runInstant(stepper, mazeNarrative(algorithm));
        } else {
            run(stepper, mazeNarrative(algorithm));
        }
    }

    public void findPath() {
        if (pathAlgorithm == null) {
            throw new MissingAlgorithmException("Pick an Algorithm!");
        }

        lastRunWasMaze = false;
        pathDisplayed.set(false);
        switch (pathAlgorithm) {
            case A_STAR -> {
                grid.clear(false, false, false);
                // The shortest-path arrow animates at a fixed speed, like the original (independent
                // of the search-speed slider), so it stays quick even when the search is set to slow.
                run(new AStarStepper(GridSnapshot.of(grid)), pathfindingNarrative(),
                        LaunchAnimationSpeed.SHORTEST_PATH.getInterval());
            }
            default -> throw new UnsupportedAlgorithmException("Unsupported algorithm: " + pathAlgorithm);
        }

        // Mark the result "displayed" once this run finishes (one-shot), so dragging start/target after
        // it can recompute instantly.
        isReady.addListener(new javafx.beans.value.ChangeListener<>() {
            @Override
            public void changed(javafx.beans.value.ObservableValue<? extends Boolean> obs, Boolean was, Boolean done) {
                if (done) {
                    pathDisplayed.set(true);
                    isReady.removeListener(this);
                }
            }
        });
    }

    public boolean isPathDisplayed() {
        return pathDisplayed.get();
    }

    /**
     * Re-runs the pathfinder with the given start/target and paints the result instantly — no step
     * animation, no travelling arrow. Used while dragging a start/target node so the visited cells and
     * shortest path follow the cursor live (matching the original). No-op until a path has been run.
     */
    public void recomputeInstant(Location start, Location target) {
        if (!pathDisplayed.get() || !isReady() || pathAlgorithm != PathFindingAlgorithm.A_STAR) {
            return;
        }
        grid.clear(false, false, false);   // clear previous visited/path, keep walls/start/target
        GridStepRenderer renderer = new GridStepRenderer(grid, true);
        Stepper stepper = new AStarStepper(GridSnapshot.of(grid, start, target));
        while (stepper.hasNext()) {
            renderer.accept(stepper.next());
        }
    }

    private void run(Stepper stepper, RunNarrative narrative) {
        run(stepper, narrative, 0L);
    }

    private void run(Stepper stepper, RunNarrative narrative, long pathIntervalNanos) {
        if (currentSpeed != null) {
            stepWorker.setCurrentSpeed(intervalFor(currentSpeed));
        }
        stepWorker.run(stepper, narrative, pathIntervalNanos);
    }

    private RunNarrative mazeNarrative(MazeAlgorithm algorithm) {
        String header = String.format("%s maze %s %s  %s  %d×%d grid",
                Narrative.START, Narrative.DOT, algorithm.getLabel(), Narrative.ARROW,
                grid.getRowLen(), grid.getColLen());
        int cells = grid.getRowLen() * grid.getColLen();
        return new RunNarrative(header, null, "carving",
                tally -> tally.total() + " steps",
                (tally, secs) -> String.format("%s Maze ready %s %d cells %s %s",
                        Narrative.OK, Narrative.DOT, cells, Narrative.DOT, Narrative.human(secs)),
                tally -> true);
    }

    private RunNarrative pathfindingNarrative() {
        Location start = grid.getStartCell().getLocation();
        Location target = grid.getTargetCell().getLocation();
        String header = String.format("%s pathfinding %s %s  %s  %d×%d grid",
                Narrative.START, Narrative.DOT, pathAlgorithm.getLabel(), Narrative.ARROW,
                grid.getRowLen(), grid.getColLen());
        String config = String.format("  start (%d,%d) %s target (%d,%d) %s Manhattan",
                start.getRow(), start.getCol(), Narrative.DOT, target.getRow(), target.getCol(), Narrative.DOT);

        return new RunNarrative(header, config, "search",
                tally -> tally.count(StepType.VISITED) + " cells explored",
                (tally, secs) -> tally.count(StepType.PATH) > 0
                        ? String.format("%s Path found %s %d steps %s %d cells explored %s %s",
                                Narrative.OK, Narrative.DOT, tally.count(StepType.PATH), Narrative.DOT,
                                tally.count(StepType.VISITED), Narrative.DOT, Narrative.human(secs))
                        : String.format("%s No path %s %d cells explored %s %s",
                                Narrative.FAIL, Narrative.DOT, tally.count(StepType.VISITED), Narrative.DOT,
                                Narrative.human(secs)),
                tally -> tally.count(StepType.PATH) > 0);
    }

    public boolean isReady() {
        return isReady.get();
    }

    public BooleanProperty isReadyProperty() {
        return isReady;
    }

    public void setIsReady(boolean isReady) {
        this.isReady.set(isReady);
    }

    public void cancelRunningWorkers() {
        if (stepWorker.isRunning()) {
            stepWorker.cancel();
        }
    }

    public void updateSpeed(AnimationSpeed currentSpeed) {
        this.currentSpeed = currentSpeed;
        stepWorker.setCurrentSpeed(intervalFor(currentSpeed));
    }

    // Maze and pathfinding use different speed scales; pick the right one for what's (about to be) running.
    private long intervalFor(AnimationSpeed speed) {
        return lastRunWasMaze
                ? MazeGenerationAnimationSpeed.valueOf(speed.name()).getInterval()
                : LaunchAnimationSpeed.valueOf(speed.name()).getInterval();
    }

    public void clearBoard() {
        clear(true, true);
        logCleaner.onClear("board cleared");
    }

    public void clearWallWeight() {
        clear(false, true);
        logCleaner.onClear("walls & weights cleared");
    }

    public void clearPath() {
        clear(false, false);
        logCleaner.onClear("path cleared");
    }

    private void clear(boolean reset, boolean removeWalls) {
        cancelRunningWorkers();
        pathDisplayed.set(false);

        if (grid != null) {
            grid.setDefaultFlag(NONE);
            grid.clear(reset, removeWalls, reset);
        }
    }
}
