package com.mahefa.pathfindingfx.service.concurrent;

import com.mahefa.pathfindingfx.algorithm.grid.GridModel;
import com.mahefa.pathfindingfx.domain.Location;
import com.mahefa.pathfindingfx.ui.component.Cell;
import com.mahefa.pathfindingfx.ui.component.Grid;

import static com.mahefa.pathfindingfx.ui.style.CellStyle.Flag.WALL_NODE;

/**
 * Reads the live {@link Grid} once into a pure {@link GridModel} for an algorithm run. This is the
 * UI→algorithm boundary: after this snapshot, the stepper never touches the grid.
 */
public final class GridSnapshot {

    private GridSnapshot() {
    }

    public static GridModel of(Grid grid) {
        return of(grid, grid.getStartCell().getLocation(), grid.getTargetCell().getLocation());
    }

    /**
     * Snapshot with explicit start/target locations — used to preview the path while a start/target node
     * is being dragged, before the move is committed to the grid.
     */
    public static GridModel of(Grid grid, Location start, Location target) {
        int rows = grid.getRowLen();
        int cols = grid.getColLen();
        boolean[][] wall = new boolean[rows][cols];
        double[][] weight = new double[rows][cols];

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Cell cell = grid.getCellAt(r, c);
                wall[r][c] = cell.getFlag() == WALL_NODE;
                weight[r][c] = cell.getWeight();
            }
        }

        return new GridModel(rows, cols, wall, weight, start, target);
    }
}
