package org.mahefa.component;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.mahefa.common.enumerator.NodeType;

import java.util.function.Consumer;

import static org.mahefa.common.CellStyle.Flag;
import static org.mahefa.common.CellStyle.Flag.*;

public class Grid {

    private Cell[][] cells;
    private int rowLen;
    private int colLen;

    private ObjectProperty<Cell> startCell = new SimpleObjectProperty<>();
    private ObjectProperty<Cell> targetCell = new SimpleObjectProperty<>();

    private int startRow, startCol, targetRow, targetCol;
    private Flag defaultFlag = NONE;

    public Grid(double canvasWidth, double canvasHeight, double gridSize, Consumer<Cell> func) {
        // Ensure rowLen and colLen are odds
        rowLen = ((int) Math.ceil(canvasHeight/ gridSize) / 2) * 2 - 1;
        colLen = ((int) Math.ceil(canvasWidth / gridSize) / 2) * 2 - 1;
        cells = new Cell[rowLen][colLen];

        // Calculate padding
        double paddingY = Math.abs((colLen * gridSize) - canvasWidth) / 2;
        double paddingX = Math.abs((rowLen * gridSize) - canvasHeight) / 2;

        // Default start and target cell position
        startRow = (int) Math.ceil(rowLen / 2d);
        startCol = (int) Math.ceil(colLen / 4d);
        targetRow = (int) Math.ceil(rowLen - (rowLen / 2d));
        targetCol = (int) Math.ceil((colLen - 1d) - (colLen / 4d));

        // Build grid
        for (int r = 0; r < rowLen; r++) {
            double posX = r * gridSize;

            for (int c = 0; c < colLen; c++) {
                double posY = (c * gridSize) + paddingY;

                Cell cell = new Cell(posX, posY, r, c, gridSize);
                Location location = cell.getLocation();

                cell.setFlag(defaultFlag);

                if ((r == startRow && c == startCol) || (r == targetRow && c == targetCol)) {
                    cell.setNodeType((r == startRow && c == startCol) ? NodeType.START : NodeType.TARGET);
                }

                // Position the current cell into the grid
                this.cells[location.getRow()][location.getCol()] = cell;

                // Display on the UI
                func.accept(cell);
            }
        }

        startCell.setValue(cells[startRow][startCol]);
        targetCell.setValue(cells[targetRow][targetCol]);
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
        return cells[location.getRow()][location.getCol()];
    }

    public Cell getCellAt(int r, int c) {
        return cells[r][c];
    }

    public int getStartRow() {
        return startRow;
    }

    public int getStartCol() {
        return startCol;
    }

    public int getTargetRow() {
        return targetRow;
    }

    public int getTargetCol() {
        return targetCol;
    }

    public Cell getStartCell() {
        return startCell.get();
    }

    public ObjectProperty<Cell> startCellProperty() {
        return startCell;
    }

    public void setStartCell(Cell startCell) {
        this.startCell.set(startCell);
    }

    public Cell getTargetCell() {
        return targetCell.get();
    }

    public ObjectProperty<Cell> targetCellProperty() {
        return targetCell;
    }

    public void setTargetCell(Cell targetCell) {
        this.targetCell.set(targetCell);
    }

    public Flag getDefaultFlag() {
        return defaultFlag;
    }

    public void setDefaultFlag(Flag defaultFlag) {
        this.defaultFlag = defaultFlag;
    }

    public void clear(boolean resetBoard, boolean resetNodePosition) {
        for (int r = 0; r < rowLen; r++) {
            for (int c = 0; c < colLen; c++) {
                Cell currentCell = getCellAt(r, c);
                Flag currentFlag = currentCell.getFlag();

                if (resetBoard) {
                    currentCell.setWeight(0);
                    currentCell.resetFlag(defaultFlag);
                } else {
                    if (currentFlag != null) {
                        if (currentFlag.equals(POINTER) || currentFlag.equals(VISITED) || currentFlag.equals(PATH_NODE))
                            currentCell.resetFlag(defaultFlag);
                    }
                }
            }
        }

        // Reset the start and target cell to their default value
        if (resetBoard && resetNodePosition) {
            setStartCell(getCellAt(startRow, startCol));
            setTargetCell(getCellAt(targetRow, targetCol));
        }
    }
}
