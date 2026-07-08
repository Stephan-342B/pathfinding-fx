package com.mahefa.pathfindingfx.algorithm.grid;

import com.mahefa.pathfindingfx.domain.Location;

/**
 * A pure, JavaFX-free snapshot of the grid the algorithms run against: dimensions, which cells are
 * walls, per-cell weight, and the start/target locations. Taken once before a run (by the worker,
 * which reads the live {@code Grid}), it lets {@link com.mahefa.pathfindingfx.algorithm.step.Stepper}s
 * stay free of any UI dependency and be unit-tested without a toolkit.
 */
public final class GridModel {

    private final int rowLen;
    private final int colLen;
    private final boolean[][] wall;
    private final double[][] weight;
    private final Location start;
    private final Location target;

    public GridModel(int rowLen, int colLen, boolean[][] wall, double[][] weight, Location start, Location target) {
        this.rowLen = rowLen;
        this.colLen = colLen;
        this.wall = wall;
        this.weight = weight;
        this.start = start;
        this.target = target;
    }

    public int getRowLen() {
        return rowLen;
    }

    public int getColLen() {
        return colLen;
    }

    public Location getStart() {
        return start;
    }

    public Location getTarget() {
        return target;
    }

    public boolean inBounds(Location location) {
        return inBounds(location.getRow(), location.getCol());
    }

    public boolean inBounds(int row, int col) {
        return row >= 0 && row < rowLen && col >= 0 && col < colLen;
    }

    public boolean isWall(Location location) {
        return wall[location.getRow()][location.getCol()];
    }

    public double weightAt(Location location) {
        return weight[location.getRow()][location.getCol()];
    }
}
