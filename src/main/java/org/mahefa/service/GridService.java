package org.mahefa.service;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.mahefa.common.enumerator.AnimationSpeed;
import org.mahefa.common.enumerator.LaunchAnimationSpeed;
import org.mahefa.common.enumerator.MazeGenerationAnimationSpeed;
import org.mahefa.component.Grid;
import org.mahefa.service.concurrent.MazeGeneratorWorker;
import org.mahefa.service.concurrent.PathfindingWorker;
import org.mahefa.service.concurrent.progress.LogCleaner;

import static org.mahefa.common.CellStyle.Flag.NONE;

public class GridService {

    protected Grid grid;
    private MazeGeneratorWorker mazeGeneratorWorker;
    private PathfindingWorker pathfindingWorker;
    private final LogCleaner logCleaner;

    private BooleanProperty isReady = new SimpleBooleanProperty(false);

    public GridService(Grid grid, LogCleaner logCleaner) {
        this.grid = grid;
        this.logCleaner = logCleaner;
        this.mazeGeneratorWorker = new MazeGeneratorWorker(grid);
        this.pathfindingWorker = new PathfindingWorker(grid);

        isReady.bind(
                Bindings.and(
                        mazeGeneratorWorker.runningProperty().not(),
                        pathfindingWorker.runningProperty().not()
                )
        );
    }

    public Grid getGrid() {
        return grid;
    }

    public MazeGeneratorWorker getMazeGeneratorWorker() {
        return mazeGeneratorWorker;
    }

    public PathfindingWorker getPathfindingWorker() {
        return pathfindingWorker;
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

    /**
     * Cancels both workers if they're running. Used to recover from an exception raised
     * before either worker's AnimationTimer ever started (e.g. no algorithm selected yet).
     */
    public void cancelRunningWorkers() {
        if (mazeGeneratorWorker.isRunning()) {
            mazeGeneratorWorker.cancel();
        }

        if (pathfindingWorker.isRunning()) {
            pathfindingWorker.cancel();
        }
    }

    public void updateSpeed(AnimationSpeed currentSpeed) {
        getMazeGeneratorWorker().setCurrentSpeed(MazeGenerationAnimationSpeed.valueOf(currentSpeed.name()).getInterval());
        getPathfindingWorker().setCurrentSpeed(LaunchAnimationSpeed.valueOf(currentSpeed.name()).getInterval());
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

        if (grid != null) {
            grid.setDefaultFlag(NONE);
            grid.clear(reset, removeWalls, reset);
        }
    }
}
