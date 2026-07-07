package com.mahefa.pathfindingfx.algorithm.maze;

import javafx.animation.AnimationTimer;
import com.mahefa.pathfindingfx.ui.component.Cell;
import com.mahefa.pathfindingfx.ui.component.Grid;
import com.mahefa.pathfindingfx.domain.Location;
import com.mahefa.pathfindingfx.algorithm.State;

import java.util.function.Supplier;

@Deprecated(forRemoval = true) // superseded by the Stepper/StepPlayer pipeline; kept until removal
public abstract class MazeGenerator extends State {

    protected Grid grid;
    protected boolean[][] visited;

    protected long currentSpeed;
    protected long lastToggle = 0L;

    public MazeGenerator(Grid grid) {
        this.grid = grid;
        this.visited = new boolean[grid.getRowLen()][grid.getColLen()];
    }

    public abstract Supplier<AnimationTimer> build();

    public boolean isAlreadyVisited(Cell cell) {
        Location location = cell.getLocation();
        return this.visited[location.getRow()][location.getCol()];
    }

    public void markAsVisited(Cell cell) {
        Location location = cell.getLocation();
        this.visited[location.getRow()][location.getCol()] = true;
    }

    public void setCurrentSpeed(long speed) {
        this.currentSpeed = speed;
    }

    /** Cells visited so far, scanned from the {@code visited} grid for the narrative's live line. */
    public int getVisitedCount() {
        int count = 0;
        for (boolean[] row : visited) {
            for (boolean cell : row) {
                if (cell) {
                    count++;
                }
            }
        }
        return count;
    }

    /** Total cells in the grid, used for the {@code x/y cells} progress fraction and the summary. */
    public int getTotalCells() {
        return grid.getRowLen() * grid.getColLen();
    }
}
