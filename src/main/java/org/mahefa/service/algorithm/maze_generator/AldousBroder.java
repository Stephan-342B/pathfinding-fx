package org.mahefa.service.algorithm.maze_generator;

import org.mahefa.component.Grid;
import org.springframework.stereotype.Component;

@Component
public class AldousBroder extends MazeGenerator {

    @Override
    public void generate(Grid g) {
        setGrid(g);

//        AnimationTimer animationTimer = new AnimationTimer() {
//
//            private long lastToggle;
//            private int totalUnvisitedCell = 0;
//            private Cell currentCell, neighbour;
//            private Flag currentOldFlag, neighbourOldFlag;
//
//            @Override
//            public void start() {
//                totalUnvisitedCell = (grid.getRowLen() / 2) * (grid.getColLen() / 2);
//                super.start();
//            }
//
//            @Override
//            public void stop() {
//                if(totalUnvisitedCell == 0) {
//                    super.stop();
//                    System.out.println("stopped");
//                }
//            }
//
//            @Override
//            public void handle(long now) {
//                if ((now - lastToggle) >= 1_000_000_000L) {
//                    if(currentCell == null) {
//                        // Pick random cell as the current cell
//                        currentCell = CellUtils.pickRandomlyOdd(grid.getCells());
//
//                        // Mark as visited
//                        currentOldFlag = currentCell.getFlag();
//                        currentCell.setFlag(Flag.POINTER);
//                        setVisitedAt(currentCell.getLocation());
//                        totalUnvisitedCell--;
//                    } else {
//                        if(isVisited(currentCell.getLocation())) {
//                            currentCell.setFlag(currentOldFlag);
//
//                            if(neighbour != null) {
//                                neighbour.setFlag(neighbourOldFlag);
//                                currentCell = neighbour;
//                                currentOldFlag = currentCell.getFlag();
//                            }
//
//                            stop();
//                        }
//
//                        // Pick a random neighbour
//                        Direction direction = CellUtils.pickRandomly();
//                        Location location = currentCell.getLocation();
//                        Location neighbourLocation = null;
//
//                        final int x = location.getX();
//                        final int y = location.getY();
//
////                        Open cell at (x, y) --> Open cell at (2x + 1, 2y + 1)
////                        Wall between (x, y) and (x, y + 1) --> Wall cell at (2x + 1, 2(y + 1))
////                        Wall between (x, y) and (x + 1, y) --> Wall cell at (2(x + 1), 2y + 1)
////                        --> All corner cells (2x, 2y) are walls
//
//                        // Check if it is possible to go north
//                        if(direction.equals(Direction.UP) && x >= 3) {
//                            neighbourLocation = location.move(Direction.UP);
//                        }
//                        // Check if it is possible to go south
//                        else if(direction.equals(Direction.DOWN) && x + 2 <= (grid.getRowLen() - 2)) {
//                            neighbourLocation = location.move(Direction.DOWN);
//                        }
//                        // Check if it is possible to go west
//                        else if(direction.equals(Direction.LEFT) && y >= 3) {
//                            neighbourLocation = location.move(Direction.LEFT);
//                        }
//                        // Check if it is possible to go east
//                        else if(direction.equals(Direction.RIGHT) && y + 2 <= (grid.getColLen() - 2)) {
//                            neighbourLocation = location.move(Direction.RIGHT);
//                        }
//
//                        if(neighbourLocation != null) {
////                            neighbourLocation = location.move(direction);
//                            neighbour = grid.getCellAt(neighbourLocation);
//
//                            // Move pointer to the current neighbour
//                            neighbourOldFlag = neighbour.getFlag();
//                            currentCell.setFlag(currentOldFlag);
//                            neighbour.setFlag(Flag.POINTER);
//
//                            // If the chosen neighbour has not been visited
//                            if(!isVisited(neighbourLocation)) {
//                                // Remove the wall between the current cell and the chosen neighbour
//                                currentOldFlag = Flag.PATH;
//                                neighbourOldFlag = Flag.PATH;
//
//                                // Mark the chosen neighbour as visited
//                                setVisitedAt(neighbourLocation);
//                                totalUnvisitedCell--;
//                            }
//                        }
//                    }
//
//                    lastToggle = now;
//                }
//            }
        };
//        animationTimer.start();
//    }
}
