package org.mahefa.component;

import org.mahefa.common.CellStyle.Flag;

import java.util.function.Consumer;
import java.util.stream.IntStream;

public class Grid {

    private Cell[][] cells;
    private int rowLen;
    private int colLen;

    private Cell startCell;
    private Cell targetCell;

    public Grid(int canvasWidth, int canvasHeight, int squareSize, Consumer<Cell> func) {
        final int gapRow = canvasHeight % squareSize;
        final int gapCol = canvasWidth % squareSize;

        this.rowLen = (canvasHeight - gapRow) / squareSize;
        this.colLen = (canvasWidth - gapCol) / squareSize;
        this.cells = new Cell[rowLen][colLen];

        // Default start and target cell position
        this.startCell = new Cell(new Location((int) Math.ceil(this.rowLen / 2d), (int) Math.ceil(this.colLen / 4d)));
        this.targetCell = new Cell(new Location(
                (int) Math.ceil(this.rowLen - (this.rowLen / 2d)),
                (int) Math.ceil((this.colLen - 1d) - (this.colLen / 4d))
        ));

        // Build grid
        IntStream.iterate(gapRow / 2, i -> i + squareSize).limit(rowLen).forEach(i -> {
            IntStream.iterate(gapCol / 2, j -> j + squareSize).limit(colLen).forEach(j -> {
                Cell cell = new Cell(i, j, squareSize);
                cell.setFlag(startCell.equals(cell) ? Flag.START : targetCell.equals(cell) ? Flag.TARGET : Flag.UNVISITED);

                final Location location = cell.getLocation();

                this.cells[location.getX()][location.getY()] = cell;

                // Showing up current tile in UI
                func.accept(cell);
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
        return cells[location.getX()][location.getY()];
    }

    public Cell getCellAt(int r, int c) {
        return cells[r][c];
    }

    public Cell getStartCell() {
        return startCell;
    }

    public void setStartCell(Cell startCell) {
        this.startCell = startCell;
    }

    public Cell getTargetCell() {
        return targetCell;
    }

    public void setTargetCell(Cell targetCell) {
        this.targetCell = targetCell;
    }

}
