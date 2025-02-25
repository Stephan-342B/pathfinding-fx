package org.mahefa.service.maze_generator;

import javafx.animation.AnimationTimer;
import org.mahefa.common.enumerator.NodeType;
import org.mahefa.common.utils.GridUtils;
import org.mahefa.component.Cell;
import org.mahefa.component.Grid;
import org.mahefa.component.Location;

import static org.mahefa.common.CellStyle.Flag;
import static org.mahefa.common.CellStyle.Flag.*;

public class AldousBroder extends MazeGenerator {

    public AldousBroder(Grid grid) {
        super(grid);
    }

    @Override
    public AnimationTimer build() {
        return new AnimationTimer() {

            private int totalUnvisitedCell;
            private Cell currentCell, neighbour;
            private Location currentLocation;
            private Flag currentFlag;

            @Override
            public void start() {
                lastToggle = 0;
                totalUnvisitedCell = (grid.getRowLen() / 2) * (grid.getColLen() / 2);

                setIsRunning(true);
                super.start();
            }

            @Override
            public void handle(long now) {
                if ((now - lastToggle) >= currentSpeed) {
                    if(currentCell == null) {
                        // Mark start and target cell as visited
                        markAsVisited(grid.getStartCell());
                        grid.getStartCell().setFlag(NONE);

                        markAsVisited(grid.getTargetCell());
                        grid.getTargetCell().setFlag(NONE);

                        // Pick random cell as the current cell
                        currentCell = GridUtils.pickRandomCell(grid);
                        currentFlag = currentCell.getFlag();

                        markAsVisited(currentCell);
                        totalUnvisitedCell--;

                        currentCell.setFlag(Flag.POINTER);
                    } else {
                        if (currentFlag == null || currentCell.getNodeType().equals(NodeType.NONE)) {
                            currentCell.setFlag(NONE);
                        } else {
                            currentCell.revertFlag();
                        }

                        if (totalUnvisitedCell > 0) {
                            // Pick random neighbour
                            neighbour = GridUtils.getRandomNeighbour(grid, currentCell, true);
                            neighbour.setFlag(POINTER);
//                            new ZoomIn(neighbour).play();

                            // If the chosen neighbour has not been visited
                            if (!isAlreadyVisited(neighbour)) {
                                // Get the wall cell
                                currentLocation = currentCell.getLocation();
                                Location neighborLocation = neighbour.getLocation();

                                int wallRow = (currentLocation.getRow() + neighborLocation.getRow()) / 2;
                                int wallCol = (currentLocation.getCol() + neighborLocation.getCol()) / 2;
                                Cell wallCell = grid.getCellAt(wallRow, wallCol);

                                // Remove the wall between the current cell and the neighbour
                                if (wallCell.getFlag().equals(WALL_NODE) && currentCell.getNodeType().equals(NodeType.NONE)) {
                                    wallCell.setFlag(NONE);
                                }

                                // Mark as visited
                                markAsVisited(neighbour);
                                totalUnvisitedCell--;
                            }

                            // Make the chosen neighbour the current cell
                            currentCell = neighbour;
                            currentFlag = currentCell.getFlag();
                        } else {
                            setIsRunning(false);
                            super.stop();
                        }
                    }

                    lastToggle = now;
                }
            }
        };
    }
}
