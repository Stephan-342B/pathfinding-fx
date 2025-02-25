package org.mahefa.service.maze_generator;

import animatefx.animation.ZoomIn;
import javafx.animation.AnimationTimer;
import org.mahefa.common.CellStyle;
import org.mahefa.common.enumerator.NodeType;
import org.mahefa.component.Cell;
import org.mahefa.component.Grid;

public class Randomized extends MazeGenerator {

    public Randomized(Grid grid) {
        super(grid);
    }

    @Override
    public AnimationTimer build() {
        setIsRunning(true);

        for (int row = 0; row < grid.getRowLen(); row++) {
            for (int col = 0; col < grid.getColLen(); col++) {
                int currentRow = row;
                int currentCol = col;

                Cell currentCell = grid.getCellAt(currentRow, currentCol);
                CellStyle.Flag currentFlag = currentCell.getFlag();
                NodeType nodeType = currentCell.getNodeType();

                if (Math.random() > 0.75 && currentFlag.equals(CellStyle.Flag.NONE) && nodeType.equals(NodeType.NONE)) {
                    currentCell.setFlag(CellStyle.Flag.WALL_NODE);
                    new ZoomIn(currentCell).setSpeed(4).play();
                }
            }
        }

        setIsRunning(false);

        return null;
    }


//    @Override
//    public AnimationTimer build() {
//        return new AnimationTimer() {
//
//            private int currentRow, currentCol;
//
//            @Override
//            public void start() {
//                lastToggle = 0;
//                currentRow = 0;
//                currentCol = 0;
//
//                setIsRunning(true);
//                super.start();
//            }
//
//            @Override
//            public void handle(long now) {
//                if ((now - lastToggle) >= 1_000_000) {
//                    Cell currentCell = grid.getCellAt(currentRow, currentCol);
//                    Flag currentFlag = currentCell.getFlag();
//                    NodeType nodeType = currentCell.getNodeType();
//
//                    if(Math.random() > 0.75 && currentFlag.equals(NONE) && nodeType.equals(NodeType.NONE))
//                        currentCell.setFlag(Flag.WALL_NODE);
//
//                    currentCol++;
//
//                    if (currentCol == grid.getColLen()) {
//                        currentRow++;
//                        currentCol = 0;
//
//                        if (currentRow == grid.getRowLen()) {
//                            setIsRunning(false);
//                            super.stop();
//                        }
//
//                    }
//
//                    lastToggle = now;
//                }
//            }
//        };
//    }
}