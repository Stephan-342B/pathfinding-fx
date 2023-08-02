package org.mahefa.service.algorithm.maze_generator;

import javafx.animation.AnimationTimer;
import org.mahefa.common.enumerator.Direction;
import org.mahefa.common.enumerator.Flag;
import org.mahefa.data.Cell;
import org.mahefa.data.Grid;
import org.mahefa.data.Location;
import org.springframework.stereotype.Component;

@Component
public class Randomized extends MazeGenerator {

    @Override
    public void generate(Grid grid) {
        setGrid(grid);

        AnimationTimer animationTimer = new AnimationTimer() {
            private long lastToggle;
            private int limit = 0;
            private Cell currentCell;
            private Location currentLocation;

            @Override
            public void start() {
                limit = (grid.getRowLen() * grid.getColLen()) - 1;
                currentLocation = Location.first();
                super.start();
            }

            @Override
            public void stop() {
                super.stop();
            }

            @Override
            public void handle(long now) {
                if (--limit == 0)
                    stop();

                currentCell = grid.getCellAt(currentLocation);

                if(Math.random() > 0.75 && currentCell.getFlag().equals(Flag.PATH))
                    currentCell.setFlag(Flag.WALL);

                if (currentLocation.getY() < grid.getColLen() - 1) {
                    currentLocation = currentLocation.move(Direction.RIGHT);
                } else {
                    currentLocation.setY(0);
                    currentLocation = currentLocation.move(Direction.DOWN);
                }

                lastToggle = now;
            }
        };

        animationTimer.start();
    }
}