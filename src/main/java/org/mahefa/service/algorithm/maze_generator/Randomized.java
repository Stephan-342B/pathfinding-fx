package org.mahefa.service.algorithm.maze_generator;

import javafx.animation.AnimationTimer;
import org.mahefa.common.enumerator.Direction;
import org.mahefa.common.enumerator.Flag;
import org.mahefa.data.Grid;
import org.mahefa.data.Location;
import org.springframework.stereotype.Component;

@Component
public class Randomized extends MazeGenerator {

    @Override
    public void generate(Grid grid) {
        this.grid = grid;

        AnimationTimer animationTimer = new AnimationTimer() {

            private long lastToggle;
            private int limit = 0;
            private Location currentLocation;

            @Override
            public void start() {
                limit = grid.getRowLen() * grid.getColLen();
                currentLocation = new Location(0, 0);
                super.start();
            }

            @Override
            public void stop() {
                if(limit-- == 0) {
                    super.stop();
                    System.out.println("stopped");
                }
            }

            @Override
            public void handle(long now) {
                if ((now - lastToggle) >= 10_000_000L) {
                    /**
                     * Handling occurrence
                     * Here the possibility to have a wall is lesser
                     */
                    final boolean isWall = Math.random() > 0.75;

                    if(isWall)
                        updateCell(currentLocation, Flag.WALL);

                    if(currentLocation.getY() < grid.getColLen() - 1) {
                        currentLocation = currentLocation.move(Direction.RIGHT);
                    } else {
                        currentLocation = new Location(currentLocation.getX() , 0).move(Direction.DOWN);
                    }

                    lastToggle = now;
                    stop();
                }
            }
        };

//        this.delay = System.currentTimeMillis();
//
//        for(int r = 0; r < this.grid.getRowLen(); r++) {
//            for(int c = 0; c < this.grid.getColLen(); c++) {
//                /**
//                 * Handling occurrence
//                 * Here the possibility to have a wall is lesser
//                 */
//                final boolean isWall = Math.random() > 0.75;
//
//                if(isWall)
//                    updateCell(new Location(r, c), Flag.WALL);
//            }
//        }

        System.out.println("finished");
    }
}