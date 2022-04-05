package org.mahefa.data;

import org.mahefa.common.enumerator.Flag;

import java.util.function.IntBinaryOperator;
import java.util.stream.IntStream;

public class Grid {

    private Cell[][] cells;
    private int rowLen;
    private int colLen;

    public Grid(int CANVAS_WIDTH, int CANVAS_HEIGHT, int squareSize, boolean fullOfWalls, IntBinaryOperator func) {
        final int gapRow = CANVAS_HEIGHT % squareSize;
        final int gapCol = CANVAS_WIDTH % squareSize;
        this.rowLen = (CANVAS_HEIGHT - gapRow) / squareSize;
        this.colLen = (CANVAS_WIDTH - gapCol) / squareSize;

        this.cells = new Cell[rowLen][colLen];

        IntStream.iterate(gapRow / 2, i -> i + squareSize).limit(rowLen).forEach(i -> {
            IntStream.iterate(gapCol / 2, j -> j + squareSize).limit(colLen).forEach(j -> {
                final Location location = new Location(i / squareSize, j / squareSize);
                Cell cell = (fullOfWalls)
                        ? new Cell(location, Flag.WALL)
                        : new Cell(location, Flag.PATH);

                this.cells[location.getX()][location.getY()] = cell;
                func.applyAsInt(j, i);
            });
        });
    }

    public Cell[][] getCells() {
        return cells;
    }

    public int getRowLen() {
        return rowLen;
    }

    public int getColLen() {
        return colLen;
    }

    public Cell[] getNeighbors(Cell currentCell) {
        return new Cell[4];
    }

    public Cell getCellAt(Location location) {
        return getCellAt(location.getX(), location.getY());
    }

    public Cell getCellAt(int r, int c) {
        return cells[r][c];
    }

    public void clear() {
        this.cells = new Cell[rowLen][colLen];
    }
}
