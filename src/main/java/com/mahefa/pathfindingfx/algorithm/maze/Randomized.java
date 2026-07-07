package com.mahefa.pathfindingfx.algorithm.maze;

import javafx.animation.AnimationTimer;
import com.mahefa.pathfindingfx.ui.style.CellStyle;
import com.mahefa.pathfindingfx.ui.component.Cell;
import com.mahefa.pathfindingfx.ui.component.Grid;

import java.util.function.Supplier;

@Deprecated(forRemoval = true) // superseded by the Stepper/StepPlayer pipeline; kept until removal
public class Randomized extends MazeGenerator {

    public Randomized(Grid grid) {
        super(grid);
    }

    @Override
    public Supplier<AnimationTimer> build() {
        setIsRunning(true);

        for (int row = 0; row < grid.getRowLen(); row++) {
            for (int col = 0; col < grid.getColLen(); col++) {
                int currentRow = row;
                int currentCol = col;

                Cell currentCell = grid.getCellAt(currentRow, currentCol);
                CellStyle.Flag currentFlag = currentCell.getFlag();

                if (Math.random() < 0.25 && currentFlag.equals(CellStyle.Flag.NONE) && !currentCell.isSpecialNode()) {
                    currentCell.setFlag(CellStyle.Flag.WALL_NODE);
                    currentCell.setWeight(0);
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